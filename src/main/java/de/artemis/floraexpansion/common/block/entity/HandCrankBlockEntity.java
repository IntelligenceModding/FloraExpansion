package de.artemis.floraexpansion.common.block.entity;

import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class HandCrankBlockEntity extends BlockEntity {
    public static final int TURN_EVENT = 1;
    public static final int ROTATION_TICKS = 8;
    private long nextDecay;
    private int serial;
    private long rotationStart = -1;
    private float rotationFrom, rotationTo;
    private float millShapeAngle = Float.NaN, crankShapeAngle = Float.NaN;
    private net.minecraft.world.phys.shapes.VoxelShape millShape, crankShape;
    private boolean crankShapeHasBase;
    private net.minecraft.core.Direction crankShapeFacing;
    private static java.util.function.DoubleSupplier clientPartialTick = () -> 0;

    public static void setClientPartialTickSupplier(java.util.function.DoubleSupplier supplier) { clientPartialTick = supplier; }
    public float getShapeAngle() {
        return getAngle(level != null && level.isClientSide() ? (float) clientPartialTick.getAsDouble() : 0);
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

    public HandCrankBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.HAND_CRANK.get(), pos, state); }
    public void setNextDecay(long time) { nextDecay = time; setChanged(); }
    public long getNextDecay() { return nextDecay; }
    public void turn() {
        if (level != null && !level.isClientSide()) {
            beginRotation();
            level.blockEvent(worldPosition, getBlockState().getBlock(), TURN_EVENT, ++serial);
        }
    }
    private float rotation(float partialTick) {
        if (level == null || rotationStart < 0) return 0;
        float fraction = Math.clamp((level.getGameTime() - rotationStart + partialTick) / ROTATION_TICKS, 0, 1);
        float eased = 1 - (1 - fraction) * (1 - fraction) * (1 - fraction);
        return rotationFrom + (rotationTo - rotationFrom) * eased;
    }
    public float getAngle(float partialTick) {
        return getBlockState().getValue(HandCrankBlock.TURN) * 90 - Math.max(0, rotationTo - rotation(partialTick));
    }
    @Override public boolean triggerEvent(int id, int parameter) {
        if (id != TURN_EVENT) return super.triggerEvent(id, parameter);
        if (level != null && level.isClientSide()) {
            beginRotation();
        }
        return true;
    }
    private void beginRotation() {
        float current = rotation(0);
        float pending = Math.max(0, rotationTo - current);
        rotationFrom = current % 360;
        rotationTo = rotationFrom + pending + 90;
        rotationStart = level.getGameTime();
    }
    @Override protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong("next_decay", nextDecay);
    }
    @Override protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        nextDecay = input.getLongOr("next_decay", 0);
    }
}
