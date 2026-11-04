package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HandCrankBlock extends net.minecraft.world.level.block.BaseEntityBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return useWithoutItem(state, level, pos, player, hit);
    }


    public static final int PULSE_TICKS = 2;
    public static final int DECAY_TICKS = 20;
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty STRENGTH_MODE =
            net.minecraft.world.level.block.state.properties.BooleanProperty.create("strength_mode");
    public static final IntegerProperty POWER = net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER;
    public static final IntegerProperty TURN = IntegerProperty.create("turn", 0, 3);
    public static final net.minecraft.world.level.block.state.properties.EnumProperty<Direction> FACING =
            net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
    public HandCrankBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TURN, 0).setValue(POWER, 0).setValue(STRENGTH_MODE, false).setValue(FACING, Direction.UP));
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader level, @NotNull BlockPos pos) {
        var supportPos = supportPos(state, pos);
        var support = level.getBlockState(supportPos);
        return isOnMill(state, level, pos) || Block.isFaceFull(support.getCollisionShape(level, supportPos), state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = defaultBlockState().setValue(FACING, context.getClickedFace());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }
    public static BlockPos supportPos(BlockState state, BlockPos pos) { return pos.relative(state.getValue(FACING).getOpposite()); }
    public static boolean isOnMill(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(FACING) == Direction.UP && level.getBlockState(pos.below()).is(ModBlocks.HAND_MILL.get());
    }

    @Override
    @NotNull public BlockState updateShape(net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.core.Direction direction, @NotNull net.minecraft.world.level.block.state.BlockState neighbourState, @NotNull net.minecraft.world.level.LevelAccessor level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.core.BlockPos neighbourPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighbourState, level, pos, neighbourPos);
    }

    @NotNull public net.minecraft.world.InteractionResult useWithItem(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        return net.minecraft.world.InteractionResult.PASS;
    }

    @NotNull public InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) {
            var switched = state.cycle(STRENGTH_MODE).setValue(POWER, 0);
            level.setBlock(pos, switched, 3);
            if (level.getBlockEntity(pos) instanceof de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity crank) crank.setNextDecay(0);
            updatePower(level, pos, switched);
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable(switched.getValue(STRENGTH_MODE)
                    ? "message.floraexpansion.hand_crank.strength" : "message.floraexpansion.hand_crank.pulse"), true);
            return InteractionResult.CONSUME;
        }
        boolean onMill = isOnMill(state, level, pos);
        if (onMill && level.getBlockEntity(pos.below()) instanceof HandMillBlockEntity mill && !mill.crank(player)) return InteractionResult.CONSUME;
        int power = state.getValue(STRENGTH_MODE) ? Math.min(15, state.getValue(POWER) + 1) : 15;
        var turned = state.setValue(TURN, (state.getValue(TURN) + 1) % 4).setValue(POWER, power);
        level.setBlock(pos, turned, 3);
        int delay = state.getValue(STRENGTH_MODE) ? DECAY_TICKS : PULSE_TICKS;
        if (level.getBlockEntity(pos) instanceof de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity crank) {
            crank.setNextDecay(level.getGameTime() + delay);
            crank.turn();
        }
        level.scheduleTick(pos, this, delay);
        updatePower(level, pos, turned);
        if (!onMill) {
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.LEVER_CLICK, net.minecraft.sounds.SoundSource.BLOCKS, 0.3F, 0.8F + power * 0.02F);
        }
        return InteractionResult.CONSUME;
    }

    @Override public net.minecraft.world.level.block.entity.BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity(pos, state);
    }
    @NotNull @Override public net.minecraft.world.level.block.RenderShape getRenderShape(@NotNull BlockState state) {
        return net.minecraft.world.level.block.RenderShape.MODEL;
    }
    @Override public boolean isSignalSource(@NotNull BlockState state) { return true; }
    @Override public int getSignal(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction direction) { return state.getValue(POWER); }
    @Override public int getDirectSignal(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction direction) {
        return direction == state.getValue(FACING) ? state.getValue(POWER) : 0;
    }
    private void updatePower(Level level, BlockPos pos, BlockState state) {
        level.updateNeighborsAt(pos, this);
        level.updateNeighborsAt(supportPos(state, pos), this);
    }
    @Override public void tick(BlockState state, @NotNull net.minecraft.server.level.ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(POWER) == 0) return;
        if (level.getBlockEntity(pos) instanceof de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity crank
                && crank.getNextDecay() > level.getGameTime()) {
            level.scheduleTick(pos, this, (int) Math.min(DECAY_TICKS, crank.getNextDecay() - level.getGameTime()));
            return;
        }
        int power = state.getValue(STRENGTH_MODE) ? state.getValue(POWER) - 1 : 0;
        level.setBlock(pos, state.setValue(POWER, power), 3);
        updatePower(level, pos, state);
        if (power > 0) {
            if (level.getBlockEntity(pos) instanceof de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity crank)
                crank.setNextDecay(level.getGameTime() + DECAY_TICKS);
            level.scheduleTick(pos, this, DECAY_TICKS);
        }
    }
    @Override public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!oldState.is(this) && state.getValue(POWER) > 0) level.scheduleTick(pos, this, PULSE_TICKS);
    }
    @Override public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        if (state.getValue(POWER) > 0) updatePower(level, pos, state);
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
    @Override
    @NotNull public VoxelShape getShape(@NotNull BlockState state, BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return level.getBlockEntity(pos) instanceof de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity crank
                ? crank.getCrankShape() : HandMillShapes.crank(state.getValue(TURN) * 90,
                        !isOnMill(state, level, pos), state.getValue(FACING));
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(TURN, POWER, STRENGTH_MODE, FACING); }

    @NotNull @Override public BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
    @NotNull @Override public BlockState mirror(@NotNull BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }
}
