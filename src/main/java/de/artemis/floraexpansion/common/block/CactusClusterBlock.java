package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CactusClusterBlock extends BushBlock {
    public static final MapCodec<CactusClusterBlock> CODEC = simpleCodec(CactusClusterBlock::new);
    public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;

    private static final VoxelShape SHAPE_ONE = Block.box(6.0D, 0.0D, 6.0D, 10.0D, 6.0D, 10.0D);
    private static final VoxelShape SHAPE_TWO = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 6.0D, 13.0D);
    private static final VoxelShape SHAPE_THREE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 6.0D, 14.0D);
    private static final VoxelShape SHAPE_FOUR = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D);

    public CactusClusterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PICKLES, 1));
    }

    @Override
    protected @NotNull MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND)
                || state.is(Blocks.TERRACOTTA)
                || state.is(Blocks.WHITE_TERRACOTTA)
                || state.is(Blocks.ORANGE_TERRACOTTA)
                || state.is(Blocks.YELLOW_TERRACOTTA);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return this.mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
        return (!context.isSecondaryUseActive()
                && context.getItemInHand().is(this.asItem())
                && state.getValue(PICKLES) < 4)
                || super.canBeReplaced(state, context);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(this)) {
            return existing.setValue(PICKLES, Math.min(4, existing.getValue(PICKLES) + 1));
        }

        return this.defaultBlockState();
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state,
                                           @NotNull BlockGetter level,
                                           @NotNull BlockPos pos,
                                           @NotNull CollisionContext context) {
        return switch (state.getValue(PICKLES)) {
            case 2 -> SHAPE_TWO;
            case 3 -> SHAPE_THREE;
            case 4 -> SHAPE_FOUR;
            default -> SHAPE_ONE;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PICKLES);
    }
}