package de.artemis.floraexpansion.common.block.entity;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HandCrankBlockEntity extends BlockEntity {
    public static final int TURN_EVENT = 1;
    public static final int ROTATION_TICKS = 8;
    private long nextDecay;
    private int serial;
    private double rotationStart = -1;
    private float rotationFrom, rotationTo;
    private float clientTargetAngle;
    private float millShapeAngle = Float.NaN, crankShapeAngle = Float.NaN;
    private net.minecraft.world.phys.shapes.VoxelShape millShape, crankShape;
    private boolean crankShapeHasBase;
    private net.minecraft.core.Direction crankShapeFacing;
    private static java.util.function.DoubleSupplier clientPartialTick = () -> 0;
    private static java.util.function.DoubleUnaryOperator clientAnimationTime;

    public static void setClientPartialTickSupplier(java.util.function.DoubleSupplier supplier) { clientPartialTick = supplier; }
    public static void setClientAnimationTimeSupplier(java.util.function.DoubleUnaryOperator supplier) { clientAnimationTime = supplier; }
    private double animationTime(float partialTick) {
        if (level == null) return 0;
        return level.isClientSide && clientAnimationTime != null
                ? clientAnimationTime.applyAsDouble(partialTick) : level.getGameTime() + (double) partialTick;
    }
    public float getShapeAngle() {
        return getAngle(level != null && level.isClientSide ? (float) clientPartialTick.getAsDouble() : 0);
    }
    public net.minecraft.world.phys.shapes.VoxelShape getMillShape() {
        float angle = getShapeAngle() % 90;
        if (millShape == null || angle != millShapeAngle) {
            millShapeAngle = angle;
            millShape = de.artemis.floraexpansion.common.block.HandMillShapes.mill(angle);
        }
        return millShape;
    }
    public net.minecraft.world.phys.shapes.VoxelShape getCrankShape() {
        float angle = getShapeAngle() % 360;
        boolean hasBase = !isOnMill();
        var facing = getBlockState().getValue(HandCrankBlock.FACING);
        if (crankShape == null || angle != crankShapeAngle || hasBase != crankShapeHasBase || facing != crankShapeFacing) {
            crankShapeAngle = angle;
            crankShapeHasBase = hasBase;
            crankShapeFacing = facing;
            crankShape = de.artemis.floraexpansion.common.block.HandMillShapes.crank(angle, hasBase, facing);
        }
        return crankShape;
    }
    public boolean isOnMill() {
        return level != null && HandCrankBlock.isOnMill(getBlockState(), level, worldPosition);
    }

    public HandCrankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAND_CRANK.get(), pos, state);
        clientTargetAngle = state.getValue(HandCrankBlock.TURN) * 90;
    }
    public void setNextDecay(long time) { nextDecay = time; setChanged(); }
    public long getNextDecay() { return nextDecay; }
    public void turn() {
        if (level != null && !level.isClientSide) {
            beginRotation();
            level.blockEvent(worldPosition, getBlockState().getBlock(), TURN_EVENT, ++serial);
        }
    }
    private float rotation(float partialTick) {
        if (level == null || rotationStart < 0) return 0;
        float fraction = (float) net.minecraft.util.Mth.clamp((animationTime(partialTick) - rotationStart) / ROTATION_TICKS, 0D, 1D);
        float eased = 1 - (1 - fraction) * (1 - fraction) * (1 - fraction);
        return rotationFrom + (rotationTo - rotationFrom) * eased;
    }
    public float getAngle(float partialTick) {
        // State updates and turn events arrive in separate packets. The saved TURN
        // can already be final for a frame before the animation event arrives.
        float target = level != null && level.isClientSide ? clientTargetAngle
                : getBlockState().getValue(HandCrankBlock.TURN) * 90;
        return target - Math.max(0, rotationTo - rotation(partialTick));
    }
    @Override public void setBlockState(@NotNull BlockState state) {
        if (level != null && level.isClientSide && state.is(getBlockState().getBlock())) {
            int turns = Math.floorMod(state.getValue(HandCrankBlock.TURN)
                    - getBlockState().getValue(HandCrankBlock.TURN), 4);
            if (turns != 0) {
                beginRotation(turns * 90);
                clientTargetAngle = state.getValue(HandCrankBlock.TURN) * 90;
            }
        }
        super.setBlockState(state);
    }
    @Override public boolean triggerEvent(int id, int parameter) {
        if (id != TURN_EVENT) return super.triggerEvent(id, parameter);
        // The state packet starts client animation atomically with the TURN change.
        // Starting it again here would rewind it, and events have a shorter range
        // than state updates, so they cannot be the source of visual orientation.
        return true;
    }
    private void beginRotation() {
        beginRotation(90);
    }
    private void beginRotation(float degrees) {
        if (level == null) return;
        // Client state updates can arrive between frames. Starting at the whole tick
        // advances the new turn immediately and makes overlapping turns jump.
        float partialTick = level.isClientSide ? (float) clientPartialTick.getAsDouble() : 0;
        float current = rotation(partialTick);
        float pending = Math.max(0, rotationTo - current);
        rotationFrom = current % 360;
        rotationTo = rotationFrom + pending + degrees;
        rotationStart = animationTime(partialTick);
    }
    @Override public void saveAdditional(@NotNull net.minecraft.nbt.CompoundTag output) {
        super.saveAdditional(output);
        output.putLong("next_decay", nextDecay);
    }
    @Override public void load(@NotNull net.minecraft.nbt.CompoundTag input) {
        super.load(input);
        nextDecay = input.getLong("next_decay");
    }
}
