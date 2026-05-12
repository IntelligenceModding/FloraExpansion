package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.inventory.BasketMenu;
import de.artemis.floraexpansion.common.block.FlaxCropBlock;
import de.artemis.floraexpansion.common.block.FruitingCherryLeavesBlock;
import de.artemis.floraexpansion.common.block.FruitingOakLeavesBlock;
import de.artemis.floraexpansion.common.block.LargeBlueberryBushBlock;
import de.artemis.floraexpansion.common.block.SmallBlueberryBushBlock;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BasketItem extends Item {
    public static final int SLOT_COUNT = 9;
    private static final int TOOLTIP_ENTRY_COUNT = 4;

    public BasketItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        Level level = context.getLevel();
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                openBasket(player, context.getHand());
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.isClientSide) {
            BlockPos pos = context.getClickedPos();
            return this.canPredictHandled(level, pos, level.getBlockState(pos), player)
                    ? InteractionResult.SUCCESS
                    : InteractionResult.PASS;
        }

        return BasketHarvestHelper.tryHarvest(context)
                ? InteractionResult.CONSUME
                : InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            openBasket(player, usedHand);
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        List<PreviewEntry> entries = this.getPreviewEntries(stack);
        if (entries.isEmpty()) {
            return;
        }

        int displayed = Math.min(TOOLTIP_ENTRY_COUNT, entries.size());
        for (int i = 0; i < displayed; i++) {
            PreviewEntry entry = entries.get(i);
            tooltipComponents.add(Component.literal(entry.count() + " ").withStyle(ChatFormatting.GRAY)
                    .append(getStyledItemName(entry.displayStack())));
        }

        if (entries.size() > TOOLTIP_ENTRY_COUNT) {
            tooltipComponents.add(Component.literal("...").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static NonNullList<ItemStack> getContents(ItemStack basket) {
        NonNullList<ItemStack> contents = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        basket.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(contents);
        return contents;
    }

    public static void saveContents(ItemStack basket, NonNullList<ItemStack> contents) {
        boolean hasItems = false;
        for (ItemStack stack : contents) {
            if (!stack.isEmpty()) {
                hasItems = true;
                break;
            }
        }

        if (!hasItems) {
            basket.remove(DataComponents.CONTAINER);
            return;
        }

        basket.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
    }

    public static int insertItem(ItemStack basket, ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }

        NonNullList<ItemStack> contents = getContents(basket);
        int originalCount = stack.getCount();

        for (int i = 0; i < contents.size() && !stack.isEmpty(); i++) {
            ItemStack slotStack = contents.get(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int maxStackSize = Math.min(slotStack.getMaxStackSize(), 64);
                int space = maxStackSize - slotStack.getCount();
                if (space > 0) {
                    int moved = Math.min(space, stack.getCount());
                    slotStack.grow(moved);
                    stack.shrink(moved);
                }
            }
        }

        for (int i = 0; i < contents.size() && !stack.isEmpty(); i++) {
            if (contents.get(i).isEmpty()) {
                int moved = Math.min(stack.getCount(), stack.getMaxStackSize());
                contents.set(i, stack.copyWithCount(moved));
                stack.shrink(moved);
            }
        }

        int inserted = originalCount - stack.getCount();
        if (inserted > 0) {
            saveContents(basket, contents);
        }

        return inserted;
    }

    public static void syncPlayerInventory(Player player) {
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
    }

    public static int getSourceSlot(Player player, InteractionHand hand) {
        return hand == InteractionHand.OFF_HAND ? 40 : player.getInventory().selected;
    }

    public static void openBasket(Player player, InteractionHand hand) {
        int sourceSlot = getSourceSlot(player, hand);
        player.openMenu(
                new SimpleMenuProvider(
                        (containerId, inventory, basketPlayer) -> new BasketMenu(containerId, inventory, hand, sourceSlot),
                        Component.translatable(ModItems.BASKET.get().getDescriptionId())
                ),
                buffer -> {
                    buffer.writeEnum(hand);
                    buffer.writeVarInt(sourceSlot);
                }
        );
    }

    public static boolean tryDepositIntoContainer(Level level, BlockPos pos, Player player, ItemStack basket) {
        Container container = findTargetContainer(level, pos, player);
        if (container == null) {
            return false;
        }

        NonNullList<ItemStack> contents = getContents(basket);
        boolean changed = false;

        for (int i = 0; i < contents.size(); i++) {
            ItemStack basketStack = contents.get(i);
            if (basketStack.isEmpty()) {
                continue;
            }

            int moved = moveStackIntoContainer(container, basketStack);
            if (moved > 0) {
                changed = true;
                if (basketStack.isEmpty()) {
                    contents.set(i, ItemStack.EMPTY);
                }
            }
        }

        if (!changed) {
            return false;
        }

        saveContents(basket, contents);
        container.setChanged();
        syncPlayerInventory(player);
        return true;
    }

    private static int moveStackIntoContainer(Container container, ItemStack stack) {
        int originalCount = stack.getCount();

        for (int slot = 0; slot < container.getContainerSize() && !stack.isEmpty(); slot++) {
            ItemStack containerStack = container.getItem(slot);
            if (containerStack.isEmpty()
                    || !container.canPlaceItem(slot, stack)
                    || !ItemStack.isSameItemSameComponents(containerStack, stack)) {
                continue;
            }

            int maxStackSize = Math.min(container.getMaxStackSize(), containerStack.getMaxStackSize());
            int space = maxStackSize - containerStack.getCount();
            if (space <= 0) {
                continue;
            }

            int moved = Math.min(space, stack.getCount());
            containerStack.grow(moved);
            stack.shrink(moved);
            container.setItem(slot, containerStack);
        }

        for (int slot = 0; slot < container.getContainerSize() && !stack.isEmpty(); slot++) {
            if (!container.getItem(slot).isEmpty() || !container.canPlaceItem(slot, stack)) {
                continue;
            }

            int maxStackSize = Math.min(container.getMaxStackSize(), stack.getMaxStackSize());
            int moved = Math.min(maxStackSize, stack.getCount());
            container.setItem(slot, stack.copyWithCount(moved));
            stack.shrink(moved);
        }

        return originalCount - stack.getCount();
    }

    private boolean canPredictHandled(Level level, BlockPos pos, BlockState state, Player player) {
        if (state.getBlock() instanceof FlaxCropBlock) {
            return state.getValue(FlaxCropBlock.AGE) >= 2;
        }

        if (state.getBlock() instanceof CropBlock cropBlock) {
            return cropBlock.isMaxAge(state);
        }

        if (state.getBlock() instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) >= 3;
        }

        if (state.getBlock() instanceof CocoaBlock) {
            return state.getValue(CocoaBlock.AGE) >= 2;
        }

        if (state.getBlock() instanceof SmallBlueberryBushBlock) {
            return state.getValue(SmallBlueberryBushBlock.AGE) >= 3;
        }

        if (state.getBlock() instanceof LargeBlueberryBushBlock) {
            return state.getValue(LargeBlueberryBushBlock.AGE) >= 2;
        }

        if (state.getBlock() instanceof SweetBerryBushBlock) {
            return state.getValue(SweetBerryBushBlock.AGE) > 1;
        }

        if (state.hasProperty(BlockStateProperties.BERRIES) && Boolean.TRUE.equals(state.getValue(BlockStateProperties.BERRIES))) {
            return true;
        }

        if (state.getBlock() instanceof FruitingCherryLeavesBlock) {
            return state.getValue(FruitingCherryLeavesBlock.AGE) > 0;
        }

        if (state.getBlock() instanceof FruitingOakLeavesBlock) {
            return state.getValue(FruitingOakLeavesBlock.AGE) >= FruitingOakLeavesBlock.MAX_AGE;
        }

        if (findTargetContainer(level, pos, player) != null) {
            return true;
        }

        return state.getBlock() instanceof BushBlock
                || state.getBlock() instanceof GrowingPlantHeadBlock
                || state.getBlock() instanceof GrowingPlantBodyBlock
                || state.getBlock() instanceof CaveVinesBlock
                || state.getBlock() instanceof CaveVinesPlantBlock;
    }

    private static @Nullable Container findTargetContainer(Level level, BlockPos pos, Player player) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof EnderChestBlock) {
            return player.getEnderChestInventory();
        }

        if (state.getBlock() instanceof ChestBlock chestBlock) {
            return ChestBlock.getContainer(chestBlock, state, level, pos, true);
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity instanceof Container container ? container : null;
    }

    private List<PreviewEntry> getPreviewEntries(ItemStack basket) {
        List<PreviewEntry> entries = new ArrayList<>();
        for (ItemStack contentStack : getContents(basket)) {
            if (contentStack.isEmpty()) {
                continue;
            }

            PreviewEntry match = null;
            for (PreviewEntry entry : entries) {
                if (ItemStack.isSameItemSameComponents(entry.displayStack(), contentStack)) {
                    match = entry;
                    break;
                }
            }

            if (match == null) {
                entries.add(new PreviewEntry(contentStack.copyWithCount(1), contentStack.getCount()));
            } else {
                entries.set(entries.indexOf(match), new PreviewEntry(match.displayStack(), match.count() + contentStack.getCount()));
            }
        }

        entries.sort(Comparator.comparingInt(PreviewEntry::count).reversed());
        return entries;
    }

    private static MutableComponent getStyledItemName(ItemStack stack) {
        MutableComponent itemName = Component.empty().append(stack.getHoverName()).withStyle(stack.getRarity().color());
        if (stack.has(DataComponents.CUSTOM_NAME)) {
            itemName.withStyle(ChatFormatting.ITALIC);
        }

        return itemName;
    }

    private record PreviewEntry(ItemStack displayStack, int count) {
    }
}
