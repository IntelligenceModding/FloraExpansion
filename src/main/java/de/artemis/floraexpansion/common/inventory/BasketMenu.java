package de.artemis.floraexpansion.common.inventory;

import de.artemis.floraexpansion.common.registry.ModMenuTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BasketMenu extends AbstractContainerMenu {
    public static final int BASKET_SLOT_COUNT = 9;
    private static final int BASKET_SLOT_START_X = 8;
    private static final int BASKET_SLOT_START_Y = 18;
    private static final int PLAYER_SLOT_START_X = 8;
    private static final int PLAYER_SLOT_START_Y = 51;
    private static final int HOTBAR_SLOT_START_Y = 109;

    private final Container basketContainer;
    private final InteractionHand sourceHand;
    private final int sourceSlot;

    public BasketMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readEnum(InteractionHand.class), extraData.readVarInt());
    }

    public BasketMenu(int containerId, Inventory playerInventory, InteractionHand sourceHand, int sourceSlot) {
        this(containerId, playerInventory, new BasketContainer(playerInventory.player, sourceHand, sourceSlot), sourceHand, sourceSlot);
    }

    private BasketMenu(int containerId, Inventory playerInventory, Container basketContainer, InteractionHand sourceHand, int sourceSlot) {
        super(ModMenuTypes.BASKET.get(), containerId);
        checkContainerSize(basketContainer, BASKET_SLOT_COUNT);
        this.basketContainer = basketContainer;
        this.sourceHand = sourceHand;
        this.sourceSlot = sourceSlot;
        basketContainer.startOpen(playerInventory.player);

        for (int column = 0; column < BASKET_SLOT_COUNT; column++) {
            this.addSlot(new Slot(basketContainer, column, BASKET_SLOT_START_X + column * 18, BASKET_SLOT_START_Y));
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                int inventorySlot = column + row * 9 + 9;
                this.addSlot(new BasketCarrierSlot(playerInventory, inventorySlot, PLAYER_SLOT_START_X + column * 18, PLAYER_SLOT_START_Y + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new BasketCarrierSlot(playerInventory, column, PLAYER_SLOT_START_X + column * 18, HOTBAR_SLOT_START_Y));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.basketContainer.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem() || this.isSourceMenuSlot(index)) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack copiedStack = stackInSlot.copy();
        if (index < BASKET_SLOT_COUNT) {
            if (!this.moveItemStackTo(stackInSlot, BASKET_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stackInSlot, 0, BASKET_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        }

        if (stackInSlot.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copiedStack;
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (this.shouldBlockClick(slotId, button, clickType)) {
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.basketContainer.stopOpen(player);
    }

    private boolean shouldBlockClick(int slotId, int button, ClickType clickType) {
        if (slotId >= 0 && slotId < this.slots.size() && this.isSourceMenuSlot(slotId)) {
            return true;
        }

        return clickType == ClickType.SWAP
                && this.sourceHand == InteractionHand.MAIN_HAND
                && this.sourceSlot >= 0
                && this.sourceSlot < 9
                && button == this.sourceSlot;
    }

    private boolean isSourceMenuSlot(int menuSlot) {
        return menuSlot == this.getSourceMenuSlot();
    }

    private int getSourceMenuSlot() {
        if (this.sourceHand == InteractionHand.OFF_HAND) {
            return -1;
        }

        if (this.sourceSlot < 9) {
            return BASKET_SLOT_COUNT + 27 + this.sourceSlot;
        }

        return BASKET_SLOT_COUNT + (this.sourceSlot - 9);
    }

    private class BasketCarrierSlot extends Slot {
        private final int inventorySlot;

        public BasketCarrierSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
            this.inventorySlot = slot;
        }

        @Override
        public boolean mayPickup(@NotNull Player player) {
            return !this.isSourceSlot() && super.mayPickup(player);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return !this.isSourceSlot() && super.mayPlace(stack);
        }

        private boolean isSourceSlot() {
            return BasketMenu.this.sourceHand == InteractionHand.MAIN_HAND && this.inventorySlot == BasketMenu.this.sourceSlot;
        }
    }
}
