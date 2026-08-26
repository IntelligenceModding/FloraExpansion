package de.artemis.floraexpansion.common.inventory;

import de.artemis.floraexpansion.common.registry.ModMenuTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrateMenu extends AbstractContainerMenu {
    public static final int SLOT_SIZE = 18;
    public static final int VISIBLE_COLUMNS = 8;
    public static final int VISIBLE_ROWS = 6;
    public static final int TOTAL_ROWS = 20;
    public static final int CONTAINER_SLOT_COUNT = TOTAL_ROWS * VISIBLE_COLUMNS;
    public static final int VISIBLE_SLOT_COUNT = VISIBLE_ROWS * VISIBLE_COLUMNS;
    public static final int CRATE_SLOT_START_X = 8;
    public static final int CRATE_SLOT_START_Y = 18;
    public static final int PLAYER_SLOT_START_X = 8;
    public static final int PLAYER_SLOT_START_Y = 139;
    public static final int HOTBAR_SLOT_START_Y = 197;
    private static final int PLAYER_INVENTORY_COLUMNS = 9;

    private final Container container;
    private int scrollRow;

    public CrateMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new ClientCrateContainer(CONTAINER_SLOT_COUNT));
    }

    public CrateMenu(int containerId, Inventory playerInventory, Container container) {
        super(ModMenuTypes.CRATE.get(), containerId);
        checkContainerSize(container, CONTAINER_SLOT_COUNT);
        this.container = container;
        container.startOpen(playerInventory.player);

        for (int row = 0; row < VISIBLE_ROWS; row++) {
            for (int column = 0; column < VISIBLE_COLUMNS; column++) {
                this.addSlot(new CrateSlot(container, row, column, CRATE_SLOT_START_X + column * SLOT_SIZE, CRATE_SLOT_START_Y + row * SLOT_SIZE));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
                this.addSlot(new Slot(
                        playerInventory,
                        column + row * PLAYER_INVENTORY_COLUMNS + 9,
                        PLAYER_SLOT_START_X + column * SLOT_SIZE,
                        PLAYER_SLOT_START_Y + row * SLOT_SIZE
                ));
            }
        }

        for (int column = 0; column < PLAYER_INVENTORY_COLUMNS; column++) {
            this.addSlot(new Slot(playerInventory, column, PLAYER_SLOT_START_X + column * SLOT_SIZE, HOTBAR_SLOT_START_Y));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index < VISIBLE_SLOT_COUNT) {
                if (!this.moveItemStackTo(stackInSlot, VISIBLE_SLOT_COUNT, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackToCrate(stackInSlot)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ContainerInput clickType, @NotNull Player player) {
        if (this.shouldRejectClientInsert(slotId, button, clickType, player)) {
            return;
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    @Override
    public boolean canDragTo(@NotNull Slot slot) {
        if (slot.index < VISIBLE_SLOT_COUNT && !this.getCarried().isEmpty() && !slot.mayPlace(this.getCarried())) {
            return false;
        }

        return super.canDragTo(slot);
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        if (id < 0 || id > this.getMaxScrollRow()) {
            return false;
        }

        this.setScrollRow(id);
        this.broadcastChanges();
        return true;
    }

    public boolean canScroll() {
        return this.getMaxScrollRow() > 0;
    }

    public int getMaxScrollRow() {
        return TOTAL_ROWS - VISIBLE_ROWS;
    }

    public int getScrollRow() {
        return this.scrollRow;
    }

    public void setScrollRow(int scrollRow) {
        this.scrollRow = Mth.clamp(scrollRow, 0, this.getMaxScrollRow());
    }

    public int getRowIndexForScroll(float scrollOffs) {
        return Math.max((int) ((double) (scrollOffs * (float) this.getMaxScrollRow()) + 0.5), 0);
    }

    public float getScrollForRowIndex(int rowIndex) {
        if (!this.canScroll()) {
            return 0.0F;
        }

        return Mth.clamp((float) rowIndex / (float) this.getMaxScrollRow(), 0.0F, 1.0F);
    }

    public float subtractInputFromScroll(float scrollOffs, double input) {
        if (!this.canScroll()) {
            return 0.0F;
        }

        return Mth.clamp(scrollOffs - (float) (input / (double) this.getMaxScrollRow()), 0.0F, 1.0F);
    }

    private boolean shouldRejectClientInsert(int slotId, int button, ContainerInput clickType, Player player) {
        if (slotId < 0 || slotId >= VISIBLE_SLOT_COUNT) {
            return false;
        }

        Slot slot = this.slots.get(slotId);
        return switch (clickType) {
            case PICKUP -> !this.getCarried().isEmpty() && !slot.mayPlace(this.getCarried());
            case SWAP -> {
                ItemStack swapStack = player.getInventory().getItem(button);
                yield !swapStack.isEmpty() && !slot.mayPlace(swapStack);
            }
            default -> false;
        };
    }

    private boolean moveItemStackToCrate(ItemStack stack) {
        boolean moved = false;

        if (stack.isStackable()) {
            for (int i = 0; i < this.container.getContainerSize() && !stack.isEmpty(); i++) {
                ItemStack slotStack = this.container.getItem(i);
                if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(stack, slotStack)) {
                    int maxStackSize = this.container.getMaxStackSize(slotStack);
                    int combined = slotStack.getCount() + stack.getCount();
                    if (combined <= maxStackSize) {
                        stack.setCount(0);
                        slotStack.setCount(combined);
                        this.container.setChanged();
                        moved = true;
                    } else if (slotStack.getCount() < maxStackSize) {
                        stack.shrink(maxStackSize - slotStack.getCount());
                        slotStack.setCount(maxStackSize);
                        this.container.setChanged();
                        moved = true;
                    }
                }
            }
        }

        for (int i = 0; i < this.container.getContainerSize() && !stack.isEmpty(); i++) {
            if (this.container.getItem(i).isEmpty() && this.container.canPlaceItem(i, stack)) {
                int movedCount = Math.min(stack.getCount(), this.container.getMaxStackSize(stack));
                this.container.setItem(i, stack.split(movedCount));
                moved = true;
            }
        }

        return moved;
    }

    private class CrateSlot extends Slot {
        private final int visibleRow;
        private final int column;

        public CrateSlot(Container container, int visibleRow, int column, int x, int y) {
            super(container, visibleRow * VISIBLE_COLUMNS + column, x, y);
            this.visibleRow = visibleRow;
            this.column = column;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return this.hasMappedSlot() && this.container.canPlaceItem(this.getContainerSlot(), stack);
        }

        @Override
        public @NotNull ItemStack getItem() {
            return this.hasMappedSlot() ? this.container.getItem(this.getContainerSlot()) : ItemStack.EMPTY;
        }

        @Override
        public boolean hasItem() {
            return !this.getItem().isEmpty();
        }

        @Override
        public void setByPlayer(@NotNull ItemStack stack, @NotNull ItemStack oldStack) {
            if (this.hasMappedSlot()) {
                this.container.setItem(this.getContainerSlot(), stack);
                this.setChanged();
            }
        }

        @Override
        public void set(@NotNull ItemStack stack) {
            if (this.hasMappedSlot()) {
                this.container.setItem(this.getContainerSlot(), stack);
                this.setChanged();
            }
        }

        @Override
        public @NotNull ItemStack remove(int amount) {
            return this.hasMappedSlot() ? this.container.removeItem(this.getContainerSlot(), amount) : ItemStack.EMPTY;
        }

        @Override
        public boolean isActive() {
            return this.hasMappedSlot();
        }

        @Override
        public int getContainerSlot() {
            if (this.isClientSlot()) {
                return this.index;
            }

            return this.column + (this.visibleRow + CrateMenu.this.scrollRow) * VISIBLE_COLUMNS;
        }

        private boolean hasMappedSlot() {
            if (this.isClientSlot()) {
                return true;
            }

            return this.getContainerSlot() < this.container.getContainerSize();
        }

        private boolean isClientSlot() {
            return this.container instanceof ClientCrateContainer;
        }
    }

    private static class ClientCrateContainer extends SimpleContainer {
        public ClientCrateContainer(int size) {
            super(size);
        }

        @Override
        public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }

            ItemStack storedType = this.getStoredType();
            return storedType.isEmpty() || ItemStack.isSameItemSameComponents(storedType, stack);
        }

        private @NotNull ItemStack getStoredType() {
            for (int i = 0; i < this.getContainerSize(); i++) {
                ItemStack stack = this.getItem(i);
                if (!stack.isEmpty()) {
                    return stack;
                }
            }

            return ItemStack.EMPTY;
        }
    }
}
