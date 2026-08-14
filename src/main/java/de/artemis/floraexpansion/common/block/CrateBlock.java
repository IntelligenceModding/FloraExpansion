package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Util;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CrateBlock extends BaseEntityBlock {
    public static final MapCodec<CrateBlock> CODEC = simpleCodec(CrateBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty PACKED = BooleanProperty.create("packed");
    private static final String PACKED_ITEM_KEY = "Packed";
    private static final int PACKED_ITEM_MODEL = 1;
    private static final VoxelShape OUTER_SHAPE = Shapes.block();
    private static final VoxelShape CRATE_SHAPE = Util.make(() ->
            Shapes.join(OUTER_SHAPE, Block.box(2.0, 2.0, 2.0, 14.0, 16.0, 14.0), BooleanOp.ONLY_FIRST));

    public CrateBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false).setValue(PACKED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (state.getValue(POWERED)) {
            return InteractionResult.CONSUME;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrateBlockEntity crate)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (state.getValue(PACKED)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            this.untieCrate(level, pos, state, player, hand);
            return InteractionResult.CONSUME;
        }

        if (stack.is(ModItems.LINEN_THREAD.get())) {
            return this.tieCrate(level, pos, state, player, stack);
        }

        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            player.openMenu(crate);
            player.awardStat(Stats.OPEN_BARREL);
            PiglinAi.angerNearbyPiglins((ServerLevel) level, player, true);
            return InteractionResult.CONSUME;
        }

        ItemStack referenceStack = this.getBulkInsertReference(crate, stack);
        boolean bulkInsert = crate.shouldBulkInsert(player, referenceStack, level.getGameTime());
        int inserted = bulkInsert ? this.insertMatchingInventory(player, crate, referenceStack) : crate.insertItem(stack);
        if (inserted <= 0) {
            if (!level.isClientSide() && !referenceStack.isEmpty()) {
                crate.recordInsertClick(player, referenceStack, level.getGameTime());
                return InteractionResult.CONSUME;
            }

            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            if (!bulkInsert && !player.hasInfiniteMaterials()) {
                stack.shrink(inserted);
            }

            crate.recordInsertClick(player, referenceStack, level.getGameTime());
            this.syncPlayerInventory(player);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 0.9F + level.random.nextFloat() * 0.2F);
        }

        return ((level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER));
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (state.getValue(POWERED)) {
            return InteractionResult.CONSUME;
        }

        if (state.getValue(PACKED)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            this.untieCrate(level, pos, state, player, null);
            return InteractionResult.CONSUME;
        }

        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CrateBlockEntity crate) {
                player.openMenu(crate);
                player.awardStat(Stats.OPEN_BARREL);
                PiglinAi.angerNearbyPiglins((ServerLevel) level, player, true);
            }

            return InteractionResult.CONSUME;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrateBlockEntity crate)) {
            return InteractionResult.PASS;
        }

        ItemStack referenceStack = crate.getStoredTypeCopy();
        if (!crate.shouldBulkInsert(player, referenceStack, level.getGameTime())) {
            crate.recordInsertClick(player, referenceStack, level.getGameTime());
            return InteractionResult.CONSUME;
        }

        int inserted = this.insertMatchingInventory(player, crate, referenceStack);
        if (inserted > 0) {
            crate.recordInsertClick(player, referenceStack, level.getGameTime());
            this.syncPlayerInventory(player);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 0.9F + level.random.nextFloat() * 0.2F);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    protected void affectNeighborsAfterRemoval(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, boolean movedByPiston) {
        if (!state.getValue(PACKED) && level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
            Containers.dropContents(level, pos, crate);
        }

        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder params) {
        if (!state.getValue(PACKED)) {
            return super.getDrops(state, params);
        }

        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        CrateBlockEntity crate = blockEntity instanceof CrateBlockEntity crateBlockEntity ? crateBlockEntity : null;
        return List.of(this.createPackedCrateStack(crate, params.getLevel()));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CrateBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.isClosedState(state) ? OUTER_SHAPE : CRATE_SHAPE;
    }

    @Override
    protected @NotNull VoxelShape getInteractionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return OUTER_SHAPE;
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.isClosedState(state) ? OUTER_SHAPE : CRATE_SHAPE;
    }

    @Override
    protected boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    private boolean isClosedState(BlockState state) {
        return state.getValue(POWERED) || state.getValue(PACKED);
    }

    @Override
    protected boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrateBlockEntity crate)) {
            return 0;
        }

        int totalItems = crate.getTotalItemCount();
        if (totalItems <= 0) {
            return 0;
        }

        return 1 + (int) Math.floor(14.0D * (double) totalItems / (double) crate.getTotalCapacity());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, PACKED);
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
                .setValue(PACKED, isPackedItem(context.getItemInHand()));
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull Orientation orientation, boolean movedByPiston) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            if (!level.isClientSide()) {
                this.playLockStateSound(level, pos, powered);
            }

            level.setBlock(pos, state.setValue(POWERED, powered), 3);
        }
    }

    @Override
    protected @NotNull ItemStack getCloneItemStack(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData) {
        ItemStack stack = super.getCloneItemStack(level, pos, state, includeData);
        if (state.getValue(PACKED)) {
            if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate) {
                crate.saveToItem(stack, level.registryAccess());
            }
            setPackedItem(stack, true);
        }

        return stack;
    }

    private int insertMatchingInventory(Player player, CrateBlockEntity crate, ItemStack referenceStack) {
        int inserted = 0;

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack inventoryStack = player.getInventory().getItem(i);
            if (!ItemStack.isSameItemSameComponents(referenceStack, inventoryStack)) {
                continue;
            }

            int moved = crate.insertItem(inventoryStack);
            if (moved <= 0) {
                continue;
            }

            inserted += moved;
            if (!player.hasInfiniteMaterials()) {
                inventoryStack.shrink(moved);
                if (inventoryStack.isEmpty()) {
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }

        return inserted;
    }

    private ItemStack getBulkInsertReference(CrateBlockEntity crate, ItemStack heldStack) {
        ItemStack storedType = crate.getStoredTypeCopy();
        if (heldStack.isEmpty()) {
            return storedType;
        }

        if (!storedType.isEmpty() && !ItemStack.isSameItemSameComponents(heldStack, storedType)) {
            return storedType;
        }

        return heldStack.copyWithCount(1);
    }

    private InteractionResult tieCrate(Level level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        level.setBlock(pos, state.setValue(PACKED, true), 3);
        if (!player.hasInfiniteMaterials()) {
            stack.shrink(1);
        }

        this.syncPlayerInventory(player);
        level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.65F, 0.9F);
        return InteractionResult.CONSUME;
    }

    private void untieCrate(Level level, BlockPos pos, BlockState state, Player player, @Nullable net.minecraft.world.InteractionHand preferredHand) {
        level.setBlock(pos, state.setValue(PACKED, false), 3);
        if (!player.hasInfiniteMaterials()) {
            this.giveItemToPlayer(player, new ItemStack(ModItems.LINEN_THREAD.get()), preferredHand);
            this.syncPlayerInventory(player);
        }

        level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 0.65F, 0.95F);
    }

    private void playLockStateSound(Level level, BlockPos pos, boolean locking) {
        level.playSound(
                null,
                pos,
                locking ? SoundEvents.CHISELED_BOOKSHELF_INSERT : SoundEvents.CHISELED_BOOKSHELF_PICKUP,
                SoundSource.BLOCKS,
                0.5F,
                locking ? 0.9F : 0.95F
        );
    }

    private void syncPlayerInventory(Player player) {
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
    }

    private ItemStack createPackedCrateStack(@Nullable CrateBlockEntity crate, Level level) {
        ItemStack stack = new ItemStack(this);
        if (crate != null) {
            crate.saveToItem(stack, level.registryAccess());
        }

        setPackedItem(stack, true);
        return stack;
    }

    private void giveItemToPlayer(Player player, ItemStack stack, @Nullable net.minecraft.world.InteractionHand preferredHand) {
        if (preferredHand != null && player.getItemInHand(preferredHand).isEmpty()) {
            player.setItemInHand(preferredHand, stack);
            return;
        }

        if (player.getMainHandItem().isEmpty()) {
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stack);
            return;
        }

        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    public static boolean isPackedItem(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(PACKED_ITEM_KEY).orElse(false);
    }

    public static void setPackedItem(ItemStack stack, boolean packed) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            if (packed) {
                tag.putBoolean(PACKED_ITEM_KEY, true);
            } else {
                tag.remove(PACKED_ITEM_KEY);
            }
        });

        if (packed) {
            stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of((float) PACKED_ITEM_MODEL), List.of(), List.of(), Collections.emptyList()));
        } else {
            stack.remove(DataComponents.CUSTOM_MODEL_DATA);
        }
    }
}
