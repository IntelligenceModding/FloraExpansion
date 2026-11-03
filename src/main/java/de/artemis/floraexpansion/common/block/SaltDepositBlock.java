package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.LevelAccessor;

import net.minecraft.world.item.Items;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SaltDepositBlock extends FallingBlock implements SimpleWaterloggedBlock {
 @NotNull @Override public com.mojang.serialization.MapCodec<SaltDepositBlock> codec() { return simpleCodec(SaltDepositBlock::new); }
    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 4);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty BRINE = BooleanProperty.create("brine");
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");

    public SaltDepositBlock(Properties properties) {
        super(properties);
        // Only world generation opts deposits into buried finds; placement and unmarked old states are inert.
        registerDefaultState(defaultBlockState().setValue(LAYERS, 1).setValue(WATERLOGGED, false).setValue(BRINE, false).setValue(NATURAL, false));
    }


    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(WATERLOGGED) && state.getValue(BRINE);
    }
    @Override
    protected void randomTick(@NotNull BlockState state,@NotNull ServerLevel level,@NotNull BlockPos pos,@NotNull RandomSource random) {
        de.artemis.floraexpansion.common.worldgen.SaltWaterEvaporation.randomTick(level,pos,random);
    }
    @Override
    public int getDustColor(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 0xF5F1E7;
    }

    @Override
    public void onBrokenAfterFall(@NotNull Level level, @NotNull BlockPos pos, FallingBlockEntity entity) {
        int extra = entity.getBlockState().getValue(LAYERS) - 1;
        if (extra > 0) Block.popResource(level, pos, new ItemStack(asItem(), extra));
    }

    public void fallingTick(Level level, BlockPos pos, FallingBlockEntity entity) {
        if (level.isClientSide || !entity.isAlive() || entity.getDeltaMovement().y >= 0) return;
        double nextBottom = entity.getY() + (entity.getDeltaMovement().y - 0.04);
        int bottomY = (int) Math.floor(nextBottom);
        for (int y = pos.getY(); y >= bottomY - 1; y--) {
            var target = new BlockPos(pos.getX(), y, pos.getZ());
            var state = level.getBlockState(target);
            if (state.is(this) && state.getValue(LAYERS) < 4) {
                double surface = y + state.getValue(LAYERS) / 4.0;
                if (entity.getY() >= surface && nextBottom <= surface) {
                    settle(level, target, entity.getBlockState().getValue(LAYERS), entity);
                    entity.discard();
                }
                return;
            }
            if (!FallingBlock.isFree(state)) return;
        }
    }
    private void settle(Level level, BlockPos pos, int remaining, FallingBlockEntity entity) {
        while (remaining > 0) {
            var state = level.getBlockState(pos);
            int existing = state.is(this) ? state.getValue(LAYERS) : 0;
            if (!state.is(this) && !FallingBlock.isFree(state)) break;
            int added = Math.min(4 - existing, remaining);
            var settled = withFluid(defaultBlockState().setValue(LAYERS, existing + added), state.getFluidState());
            settled = settled.setValue(NATURAL, entity.getBlockState().getValue(NATURAL)
                    && (existing == 0 || state.getValue(NATURAL)));
            if (!settled.canSurvive(level, pos)) break;
            level.setBlock(pos, settled, 3);
            remaining -= added;
            pos = pos.above();
        }
        if (remaining > 0) dropLayers(level, entity, remaining);
    }

    private void dropLayers(Level level, FallingBlockEntity entity, int count) {
        if (level instanceof ServerLevel server
                && server.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOENTITYDROPS)) {
            entity.spawnAtLocation(new ItemStack(asItem(), count));
        }
    }
    @Override
    protected void tick(BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                        @NotNull RandomSource random) {
        if (!state.canSurvive(level, pos) && pos.getY() >= level.getMinBuildHeight()) {
            falling(FallingBlockEntity.fall(level, pos, state));
        }
    }
    @Override
    protected boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        var belowPos = pos.below();
        var below = level.getBlockState(belowPos);
        return Block.isFaceFull(below.getCollisionShape(level, belowPos), Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        var state = context.getLevel().getBlockState(context.getClickedPos());
        var placed = state.is(this) ? state.setValue(LAYERS, Math.min(4, state.getValue(LAYERS) + 1)) : defaultBlockState();
        placed = withFluid(placed, context.getLevel().getFluidState(context.getClickedPos())).setValue(NATURAL, false);
        return placed.canSurvive(context.getLevel(), context.getClickedPos()) ? placed : null;
    }

    private static BlockState withFluid(BlockState state, FluidState fluid) {
        boolean brine = fluid.is(ModFluids.SALT_WATER.get()) && fluid.isSource();
        boolean wet = state.getValue(LAYERS) < 4 && (brine || fluid.is(Fluids.WATER));
        return state.setValue(WATERLOGGED, wet).setValue(BRINE, wet && brine);
    }

    @Override
    @NotNull protected FluidState getFluidState(BlockState state) {
        return !state.getValue(WATERLOGGED) ? super.getFluidState(state)
                : state.getValue(BRINE) ? ModFluids.SALT_WATER.get().defaultFluidState()
                : Fluids.WATER.getSource(false);
    }

    @Override
    public boolean canPlaceLiquid(@Nullable net.minecraft.world.entity.player.Player user,
                                  @NotNull BlockGetter level, @NotNull BlockPos pos, BlockState state, @NotNull Fluid fluid) {
        return state.getValue(LAYERS) < 4 && !state.getValue(WATERLOGGED)
                && (fluid == Fluids.WATER
                || fluid == ModFluids.SALT_WATER.get());
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state,
                               FluidState fluid) {
        if (!canPlaceLiquid(null, level, pos, state, fluid.getType())
                || (fluid.is(ModFluids.SALT_WATER.get()) && !fluid.isSource())) return false;
        if (!level.isClientSide()) {
            level.setBlock(pos, withFluid(state, fluid), 3);
            level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
        }
        return true;
    }

    @Override
    @NotNull public ItemStack pickupBlock(@Nullable net.minecraft.world.entity.player.Player user,
                                @NotNull LevelAccessor level, @NotNull BlockPos pos, BlockState state) {
        if (!state.getValue(WATERLOGGED)) return ItemStack.EMPTY;
        var bucket = state.getValue(BRINE) ? ModItems.SALT_WATER_BUCKET.get()
                : Items.WATER_BUCKET;
        level.setBlock(pos, state.setValue(WATERLOGGED, false).setValue(BRINE, false), 3);
        if (!state.canSurvive(level, pos)) level.destroyBlock(pos, true);
        return new ItemStack(bucket);
    }

    @Override
    @NotNull protected BlockState updateShape(net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.core.Direction direction, @NotNull net.minecraft.world.level.block.state.BlockState neighbor, @NotNull net.minecraft.world.level.LevelAccessor level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.core.BlockPos neighborPos) {
        var ticks = level;
        if (state.getValue(WATERLOGGED)) {
            var fluid = state.getFluidState().getType();
            ticks.scheduleTick(pos, fluid, fluid.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public void onLand(Level level, @NotNull BlockPos pos, @NotNull BlockState state, BlockState replaced, @NotNull FallingBlockEntity entity) {
        level.setBlock(pos, withFluid(state, replaced.getFluidState()), 3);
    }

    @Override
    protected boolean canBeReplaced(@NotNull BlockState state, BlockPlaceContext context) {
        return !context.isSecondaryUseActive() && context.getItemInHand().is(asItem()) && state.getValue(LAYERS) < 4
                && (!context.replacingClickedOnBlock() || context.getClickedFace() == Direction.UP);
    }

    @Override
    @NotNull protected VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Block.box(0, 0, 0, 16, state.getValue(LAYERS) * 4, 16);
    }

    @Override
    @NotNull protected VoxelShape getCollisionShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Block.box(0, 0, 0, 16, state.getValue(LAYERS) * 4, 16);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, WATERLOGGED, BRINE, NATURAL);
    }
}
