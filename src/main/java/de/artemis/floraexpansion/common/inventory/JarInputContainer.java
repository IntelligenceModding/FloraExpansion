package de.artemis.floraexpansion.common.inventory;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.block.JarBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.Direction;

public final class JarInputContainer extends SimpleContainer implements WorldlyContainer {
    private final LevelAccessor level;
    private final BlockPos pos;
    private boolean changed;

    public JarInputContainer(LevelAccessor level, BlockPos pos) {
        super(1);
        this.level = level;
        this.pos = pos.immutable();
    }

    @Override
    public int getMaxStackSize() { return 1; }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction direction) {
        return direction == Direction.DOWN ? new int[0] : new int[]{0};
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return slot == 0 && !changed && JarBlock.canAddFruit(level.getBlockState(pos), stack);
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, Direction direction) {
        return direction != Direction.DOWN && canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction direction) { return false; }

    @Override
    public void setChanged() {
        if (!changed && level instanceof ServerLevel && !getItem(0).isEmpty()
                && JarBlock.addFruit(level, pos, getItem(0))) {
            changed = true;
            removeItemNoUpdate(0);
        }
    }
}
