package de.artemis.floraexpansion.common.block.entity;

import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.inventory.CrateMenu;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CrateBlockEntity extends RandomizableContainerBlockEntity {
    private static final long BULK_INSERT_WINDOW_TICKS = 8L;
    public static final int CONTAINER_SIZE = CrateMenu.CONTAINER_SLOT_COUNT;

    private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
    private UUID lastInsertPlayer;
    private long lastInsertGameTime = Long.MIN_VALUE;
    private ItemStack lastInsertType = ItemStack.EMPTY;

    public CrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRATE.get(), pos, blockState);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    public int getContainerSize() {
        return CONTAINER_SIZE;
    }

    public int insertItem(@NotNull ItemStack stack) {
        if (stack.isEmpty() || this.isStorageLocked()) {
            return 0;
        }

        ItemStack storedType = this.getStoredType();
        if (!storedType.isEmpty() && !ItemStack.isSameItemSameComponents(storedType, stack)) {
            return 0;
        }

        int remaining = stack.getCount();

        for (int i = 0; i < this.items.size() && remaining > 0; i++) {
            ItemStack slotStack = this.items.get(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int space = Math.min(this.getMaxStackSize(slotStack), slotStack.getMaxStackSize()) - slotStack.getCount();
                if (space > 0) {
                    int moved = Math.min(space, remaining);
                    slotStack.grow(moved);
                    remaining -= moved;
                }
            }
        }

        for (int i = 0; i < this.items.size() && remaining > 0; i++) {
            ItemStack slotStack = this.items.get(i);
            if (slotStack.isEmpty()) {
                int moved = Math.min(Math.min(this.getMaxStackSize(stack), stack.getMaxStackSize()), remaining);
                this.items.set(i, stack.copyWithCount(moved));
                remaining -= moved;
            }
        }

        int inserted = stack.getCount() - remaining;
        if (inserted > 0) {
            this.setChanged();
        }

        return inserted;
    }

    public @NotNull ItemStack extractItem() {
        if (this.isStorageLocked()) {
            return ItemStack.EMPTY;
        }

        ItemStack storedType = this.getStoredType();
        if (storedType.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int remaining = Math.min(this.getMaxStackSize(storedType), storedType.getMaxStackSize());
        ItemStack extracted = storedType.copyWithCount(0);

        for (int i = this.items.size() - 1; i >= 0 && remaining > 0; i--) {
            ItemStack slotStack = this.items.get(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItemSameComponents(slotStack, storedType)) {
                int moved = Math.min(slotStack.getCount(), remaining);
                extracted.grow(moved);
                slotStack.shrink(moved);
                remaining -= moved;
                if (slotStack.isEmpty()) {
                    this.items.set(i, ItemStack.EMPTY);
                }
            }
        }

        if (!extracted.isEmpty()) {
            this.setChanged();
        }

        return extracted;
    }

    public @NotNull ItemStack extractSingleItem() {
        if (this.isStorageLocked()) {
            return ItemStack.EMPTY;
        }

        for (int i = this.items.size() - 1; i >= 0; i--) {
            ItemStack slotStack = this.items.get(i);
            if (!slotStack.isEmpty()) {
                ItemStack extracted = slotStack.copyWithCount(1);
                slotStack.shrink(1);
                if (slotStack.isEmpty()) {
                    this.items.set(i, ItemStack.EMPTY);
                }

                this.setChanged();
                return extracted;
            }
        }

        return ItemStack.EMPTY;
    }

    public int getTotalItemCount() {
        int total = 0;
        for (ItemStack stack : this.items) {
            total += stack.getCount();
        }

        return total;
    }

    public int getTotalCapacity() {
        ItemStack storedType = this.getStoredType();
        int stackLimit = storedType.isEmpty() ? 64 : Math.min(this.getMaxStackSize(storedType), storedType.getMaxStackSize());
        return this.getContainerSize() * stackLimit;
    }

    public float getFillRatio() {
        int capacity = this.getTotalCapacity();
        if (capacity <= 0) {
            return 0.0F;
        }

        return Math.min(1.0F, (float) this.getTotalItemCount() / (float) capacity);
    }

    public boolean shouldBulkInsert(net.minecraft.world.entity.player.Player player, @NotNull ItemStack stack, long gameTime) {
        if (this.lastInsertPlayer == null || !this.lastInsertPlayer.equals(player.getUUID())) {
            return false;
        }

        if (gameTime - this.lastInsertGameTime > BULK_INSERT_WINDOW_TICKS) {
            return false;
        }

        return !stack.isEmpty()
                && !this.lastInsertType.isEmpty()
                && ItemStack.isSameItemSameComponents(this.lastInsertType, stack);
    }

    public void recordInsertClick(net.minecraft.world.entity.player.Player player, @NotNull ItemStack stack, long gameTime) {
        if (stack.isEmpty()) {
            this.lastInsertPlayer = null;
            this.lastInsertGameTime = Long.MIN_VALUE;
            this.lastInsertType = ItemStack.EMPTY;
            return;
        }

        this.lastInsertPlayer = player.getUUID();
        this.lastInsertGameTime = gameTime;
        this.lastInsertType = stack.copyWithCount(1);
    }

    public @NotNull ItemStack getStoredTypeCopy() {
        ItemStack storedType = this.getStoredType();
        return storedType.isEmpty() ? ItemStack.EMPTY : storedType.copyWithCount(1);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty() || this.isStorageLocked()) {
            return false;
        }

        ItemStack storedType = this.getStoredType();
        return storedType.isEmpty() || ItemStack.isSameItemSameComponents(storedType, stack);
    }

    @Override
    public boolean canTakeItem(net.minecraft.world.Container target, int slot, @NotNull ItemStack stack) {
        return !this.isStorageLocked();
    }

    @Override
    public boolean canOpen(net.minecraft.world.entity.player.Player player) {
        return !this.isStorageLocked() && super.canOpen(player);
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return !this.isStorageLocked() && super.stillValid(player);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (!stack.isEmpty() && !this.canPlaceItem(slot, stack)) {
            return;
        }

        super.setItem(slot, stack);
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.getBlockState();
            this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("block.floraexpansion.crate");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new CrateMenu(containerId, inventory, this);
    }

    private @NotNull ItemStack getStoredType() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private boolean isRedstoneLocked() {
        return this.level != null
                && this.level.getBlockState(this.worldPosition).hasProperty(CrateBlock.POWERED)
                && this.level.getBlockState(this.worldPosition).getValue(CrateBlock.POWERED);
    }

    private boolean isPacked() {
        return this.level != null
                && this.level.getBlockState(this.worldPosition).hasProperty(CrateBlock.PACKED)
                && this.level.getBlockState(this.worldPosition).getValue(CrateBlock.PACKED);
    }

    private boolean isStorageLocked() {
        return this.isRedstoneLocked() || this.isPacked();
    }
}
