package de.artemis.floraexpansion.common.inventory;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.registry.ModRecipes;

import de.artemis.floraexpansion.common.registry.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class HandMillMenu extends AbstractContainerMenu {
    private final Container inventory;
    private final ContainerData data;
    public HandMillMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(2), new SimpleContainerData(2));
    }
    public HandMillMenu(int id, Inventory playerInventory, Container inventory, ContainerData data) {
        super(ModMenuTypes.HAND_MILL.get(), id);
        checkContainerSize(inventory, 2);
        checkContainerDataCount(data, 2);
        this.inventory = inventory;
        this.data = data;
        addSlot(new Slot(inventory, 0, 44, 35) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return ModRecipes.isGrindingInput(playerInventory.player.level(), stack); }
        });
        addSlot(new Slot(inventory, 1, 116, 35) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return false; }
        });
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
        }
        for (int column = 0; column < 9; column++) addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        addDataSlots(data);
    }
    public int getProgress() { return data.get(0); }
    public int getRequiredTurns() { return data.get(1); }
    public int getArrowWidth() { return getRequiredTurns() > 0 ? Math.clamp(getProgress() * 24 / getRequiredTurns(), 0, 24) : 0; }
    @Override public boolean stillValid(@NotNull Player player) { return inventory.stillValid(player); }
    @NotNull @Override public ItemStack quickMoveStack(@NotNull Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        var slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        var stack = slot.getItem();
        var original = stack.copy();
        if (index < 2) {
            if (!moveItemStackTo(stack, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (slots.get(0).mayPlace(stack)) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index < 29) {
            if (!moveItemStackTo(stack, 29, slots.size(), false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 2, 29, false)) return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, stack);
        return original;
    }
}
