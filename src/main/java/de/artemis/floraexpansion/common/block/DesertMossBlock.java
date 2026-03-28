package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DesertMossBlock extends BushBlock {
    public static final MapCodec<DesertMossBlock> CODEC = simpleCodec(DesertMossBlock::new);

    // 4 texture/model variants: 0, 1, 2, 3
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 3);

    // Very flat plane, like a thin carpet/foliage layer
    protected static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public DesertMossBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, 0));
    }

    @Override
    protected @NotNull MapCodec<? extends DesertMossBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        int variant = Math.floorMod((int) net.minecraft.util.Mth.getSeed(pos), 4);
        return this.defaultBlockState().setValue(VARIANT, variant);
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.isSolidRender(level, pos);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return this.mayPlaceOn(belowState, level, belowPos);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(VARIANT);
    }
}