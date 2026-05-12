package de.artemis.floraexpansion.common.inventory;

import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BasketContainer implements Container {
    private final Player player;
    private final InteractionHand hand;
    private final int sourceSlot;
    private final NonNullList<ItemStack> items = NonNullList.withSize(BasketItem.SLOT_COUNT, ItemStack.EMPTY);

    public BasketContainer(Player player, InteractionHand hand, int sourceSlot) {
        this.player = player;
        this.hand = hand;
        this.sourceSlot = sourceSlot;

        NonNullList<ItemStack> sourceContents = BasketItem.getContents(this.getSourceStack());
        for (int i = 0; i < this.items.size(); i++) {
            this.items.set(i, sourceContents.get(i).copy());
        }
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack removed = ContainerHelper.removeItem(this.items, slot, amount);
        if (!removed.isEmpty()) {
            this.setChanged();
        }

        return removed;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack removed = this.items.get(slot);
        this.items.set(slot, ItemStack.EMPTY);
        return removed;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public void setChanged() {
        ItemStack sourceStack = this.getSourceStack();
        if (sourceStack.is(ModItems.BASKET.get())) {
            BasketItem.saveContents(sourceStack, this.items);
            if (!this.player.level().isClientSide) {
                BasketItem.syncPlayerInventory(this.player);
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return player == this.player && this.getSourceStack().is(ModItems.BASKET.get());
    }

    @Override
    public void clearContent() {
        this.items.clear();
        this.setChanged();
    }

    private ItemStack getSourceStack() {
        if (this.hand == InteractionHand.OFF_HAND) {
            return this.player.getOffhandItem();
        }

        return this.player.getInventory().getItem(this.sourceSlot);
    }
}
