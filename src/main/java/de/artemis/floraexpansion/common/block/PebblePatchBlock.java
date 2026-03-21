package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PebblePatchBlock extends BushBlock {
    public static final MapCodec<PebblePatchBlock> CODEC = simpleCodec(PebblePatchBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty AMOUNT = ModBlockStateProperties.PEBBLE_AMOUNT;

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public PebblePatchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(AMOUNT, 1));
    }

    @Override
    public @NotNull MapCodec<PebblePatchBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, BlockPlaceContext context) {
        return (!context.isSecondaryUseActive()
                && context.getItemInHand().is(this.asItem())
                && state.getValue(AMOUNT) < 5)
                || super.canBeReplaced(state, context);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());

        if (state.is(this)) {
            return state.setValue(AMOUNT, Math.min(5, state.getValue(AMOUNT) + 1));
        }

        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(AMOUNT, 1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, AMOUNT);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.isSolidRender(level, pos);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos below = pos.below();
        return this.mayPlaceOn(level.getBlockState(below), level, below);
    }
}