package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import de.artemis.floraexpansion.common.util.ModUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("deprecation")
public class ScatteredPebblesBlock extends BushBlock implements net.minecraft.world.level.block.SimpleWaterloggedBlock {
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final net.minecraft.world.level.block.state.properties.BooleanProperty BRINE = net.minecraft.world.level.block.state.properties.BooleanProperty.create("brine");

    public static final EnumProperty<@NotNull Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty AMOUNT = ModBlockStateProperties.PEBBLE_AMOUNT;

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public ScatteredPebblesBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(AMOUNT, 1).setValue(WATERLOGGED, false).setValue(BRINE, false));
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
        return (!context.isSecondaryUseActive()
                && context.getItemInHand().getItem() instanceof net.minecraft.world.item.BlockItem item && item.getBlock() == this
                && state.getValue(AMOUNT) < 5)
                || super.canBeReplaced(state, context);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        var fluid = context.getLevel().getFluidState(context.getClickedPos());
        if (fluid.is(de.artemis.floraexpansion.common.registry.ModTags.Fluids.SALT_WATER) && !fluid.isSource()) return null;
        if (!fluid.isEmpty() && !fluid.is(net.minecraft.tags.FluidTags.WATER)
                && !fluid.is(de.artemis.floraexpansion.common.registry.ModTags.Fluids.SALT_WATER)) return null;

        if (state.is(this)) {
            return state.setValue(AMOUNT, Math.min(5, state.getValue(AMOUNT) + 1));
        }

        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, !context.getLevel().getFluidState(context.getClickedPos()).isEmpty())
                .setValue(BRINE, context.getLevel().getFluidState(context.getClickedPos()).is(de.artemis.floraexpansion.common.registry.ModTags.Fluids.SALT_WATER))
                .setValue(AMOUNT, 1);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.getMainHandItem().isEmpty()) {
            if (player.isSpectator() || !player.mayBuild() || !level.mayInteract(player, pos)) return InteractionResult.FAIL;
            if (!level.isClientSide()) {
                if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.level.block.BreakBlockEvent(level, pos, state, player)).isCanceled()) return InteractionResult.FAIL;
                int count = player.isShiftKeyDown() ? state.getValue(AMOUNT) : 1;
                level.setBlock(pos, count == state.getValue(AMOUNT) ? state.getFluidState().createLegacyBlock()
                        : state.setValue(AMOUNT, state.getValue(AMOUNT) - count), 3);
                var collected = new ItemStack(ModItems.PEBBLES.get(), count);
                // Keep the active hand empty so successive clicks continue collecting instead of replacing stones.
                var inventory = player.getInventory();
                for (int slot = 0; slot < 36 && !collected.isEmpty(); slot++) {
                    if (slot == inventory.getSelectedSlot()) continue;
                    var stored = inventory.getItem(slot);
                    if (stored.isEmpty()) { inventory.setItem(slot, collected.copy()); collected.setCount(0); }
                    else if (ItemStack.isSameItemSameComponents(stored, collected)) {
                        int added = Math.min(collected.getCount(), stored.getMaxStackSize() - stored.getCount());
                        stored.grow(added); collected.shrink(added);
                    }
                }
                if (!collected.isEmpty()) popResource(level, pos, collected);
                level.playSound(null, pos, SoundEvents.STONE_STEP, SoundSource.BLOCKS, .5F, 1.2F);

                ModUtils.awardBlockMinedStat(player, this);
            }

            return InteractionResult.SUCCESS.withoutItem();
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, @NotNull BlockState> builder) {
        builder.add(FACING, AMOUNT, WATERLOGGED, BRINE);
    }

    @Override protected net.minecraft.world.level.material.FluidState getFluidState(BlockState state) {
        return !state.getValue(WATERLOGGED) ? super.getFluidState(state)
                : state.getValue(BRINE) ? de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get().defaultFluidState()
                : net.minecraft.world.level.material.Fluids.WATER.getSource(false);
    }

    @Override public boolean canPlaceLiquid(@Nullable LivingEntity user,
            BlockGetter level, BlockPos pos, BlockState state, net.minecraft.world.level.material.Fluid fluid) {
        return !state.getValue(WATERLOGGED) && (fluid == net.minecraft.world.level.material.Fluids.WATER
                || fluid == de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get());
    }

    @Override public boolean placeLiquid(net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockState state,
            net.minecraft.world.level.material.FluidState fluid) {
        if (!canPlaceLiquid(null, level, pos, state, fluid.getType()) || !fluid.isSource()) return false;
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(WATERLOGGED, true).setValue(BRINE,
                    fluid.is(de.artemis.floraexpansion.common.registry.ModTags.Fluids.SALT_WATER)), 3);
            level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
        }
        return true;
    }

    @Override public ItemStack pickupBlock(@Nullable LivingEntity user,
            net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockState state) {
        if (!state.getValue(WATERLOGGED)) return ItemStack.EMPTY;
        var bucket = state.getValue(BRINE) ? de.artemis.floraexpansion.common.registry.ModItems.SALT_WATER_BUCKET.get() : net.minecraft.world.item.Items.WATER_BUCKET;
        level.setBlock(pos, state.setValue(WATERLOGGED, false).setValue(BRINE, false), 3);
        return new ItemStack(bucket);
    }

    @Override protected BlockState updateShape(BlockState state, LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks,
            BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighbor, net.minecraft.util.RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            var fluid = state.getFluidState().getType(); ticks.scheduleTick(pos, fluid, fluid.getTickDelay(level));
        }
        return canSurvive(state, level, pos) ? state : state.getFluidState().createLegacyBlock();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.isFaceSturdy(level, pos, Direction.UP);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos below = pos.below();
        return this.mayPlaceOn(level.getBlockState(below), level, below);
    }
}
