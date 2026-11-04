package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import de.artemis.floraexpansion.common.util.ModUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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


@SuppressWarnings("deprecation")
public class ScatteredPebblesBlock extends BushBlock implements net.minecraft.world.level.block.SimpleWaterloggedBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        return useWithoutItem(state, level, pos, player, hit);
    }



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

    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player, @NotNull BlockHitResult hitResult) {
        if (player.getMainHandItem().isEmpty()) {
            if (player.isSpectator() || !player.mayBuild() || !level.mayInteract(player, pos)) return InteractionResult.FAIL;
            if (!level.isClientSide) {
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.level.BlockEvent.BreakEvent(level, pos, state, player))) return InteractionResult.FAIL;
                int count = player.isShiftKeyDown() ? state.getValue(AMOUNT) : 1;
                level.setBlock(pos, count == state.getValue(AMOUNT) ? state.getFluidState().createLegacyBlock()
                        : state.setValue(AMOUNT, state.getValue(AMOUNT) - count), 3);
                var collected = new ItemStack(ModItems.PEBBLES.get(), count);
                // Keep the active hand empty so successive clicks continue collecting instead of replacing stones.
                var inventory = player.getInventory();
                for (int slot = 0; slot < 36 && !collected.isEmpty(); slot++) {
                    if (slot == inventory.selected) continue;
                    var stored = inventory.getItem(slot);
                    if (stored.isEmpty()) { inventory.setItem(slot, collected.copy()); collected.setCount(0); }
                    else if (ItemStack.isSameItemSameTags(stored, collected)) {
                        int added = Math.min(collected.getCount(), stored.getMaxStackSize() - stored.getCount());
                        stored.grow(added); collected.shrink(added);
                    }
                }
                if (!collected.isEmpty()) popResource(level, pos, collected);
                level.playSound(null, pos, SoundEvents.STONE_STEP, SoundSource.BLOCKS, .5F, 1.2F);

                ModUtils.awardBlockMinedStat(player, this);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, @NotNull BlockState> builder) {
        builder.add(FACING, AMOUNT, WATERLOGGED, BRINE);
    }

    @NotNull @Override public net.minecraft.world.level.material.FluidState getFluidState(BlockState state) {
        return !state.getValue(WATERLOGGED) ? super.getFluidState(state)
                : state.getValue(BRINE) ? de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get().defaultFluidState()
                : net.minecraft.world.level.material.Fluids.WATER.getSource(false);
    }

    @Override public boolean canPlaceLiquid(@NotNull BlockGetter level, @NotNull BlockPos pos, BlockState state, @NotNull net.minecraft.world.level.material.Fluid fluid) {
        return !state.getValue(WATERLOGGED) && (fluid == net.minecraft.world.level.material.Fluids.WATER
                || fluid == de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get());
    }

    @Override public boolean placeLiquid(@NotNull net.minecraft.world.level.LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state,
            net.minecraft.world.level.material.FluidState fluid) {
        if (!canPlaceLiquid(level, pos, state, fluid.getType()) || !fluid.isSource()) return false;
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(WATERLOGGED, true).setValue(BRINE,
                    fluid.is(de.artemis.floraexpansion.common.registry.ModTags.Fluids.SALT_WATER)), 3);
            level.scheduleTick(pos, fluid.getType(), fluid.getType().getTickDelay(level));
        }
        return true;
    }

    @NotNull @Override public ItemStack pickupBlock(@NotNull net.minecraft.world.level.LevelAccessor level, @NotNull BlockPos pos, BlockState state) {
        if (!state.getValue(WATERLOGGED)) return ItemStack.EMPTY;
        var bucket = state.getValue(BRINE) ? de.artemis.floraexpansion.common.registry.ModItems.SALT_WATER_BUCKET.get() : net.minecraft.world.item.Items.WATER_BUCKET;
        level.setBlock(pos, state.setValue(WATERLOGGED, false).setValue(BRINE, false), 3);
        return new ItemStack(bucket);
    }

    @NotNull @Override public BlockState updateShape(net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.core.Direction direction, @NotNull net.minecraft.world.level.block.state.BlockState neighbor, @NotNull net.minecraft.world.level.LevelAccessor level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.core.BlockPos neighborPos) {
        var ticks = level;
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
    public boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.isFaceSturdy(level, pos, Direction.UP);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos below = pos.below();
        return this.mayPlaceOn(level.getBlockState(below), level, below);
    }
}
