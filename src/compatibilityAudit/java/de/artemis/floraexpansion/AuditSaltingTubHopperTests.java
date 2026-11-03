package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

final class AuditSaltingTubHopperTests {
    static void automation(CompatibilityTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(2, 4, 2));
        level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
        var tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
        for (var face : Direction.values()) {
            check(tub.canPlaceItemThroughFace(0, new ItemStack(Items.COD), face) == (face == Direction.UP), "Wrong food hopper face");
            check(tub.canPlaceItemThroughFace(1, new ItemStack(ModItems.SALT.get()), face) == (face != Direction.DOWN), "Wrong salt hopper face");
            check(!tub.canTakeItemThroughFace(0, new ItemStack(Items.COD), face)
                    && !tub.canTakeItemThroughFace(1, new ItemStack(ModItems.SALT.get()), face), "Automation can steal ingredients");
            check(!tub.canPlaceItemThroughFace(2, new ItemStack(Items.COD), face), "Automation can insert output");
        }
        check(!tub.canPlaceItem(0, new ItemStack(Items.STONE)) && !tub.canPlaceItem(1, new ItemStack(Items.COD)), "Tub accepted invalid supplies");
        // A salt-first feed must wait for four foods, never consume salt on an incomplete batch.
        tub.setItem(1, new ItemStack(ModItems.SALT.get()));
        AuditSaltingTubTests.advance(tub, 3);
        check(!tub.isSalted() && tub.getItem(1).getCount() == 1, "Early salt was consumed");
        tub.clearContent();
        level.setBlockAndUpdate(pos.above(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
        level.setBlockAndUpdate(pos.west(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.EAST));
        level.setBlockAndUpdate(pos.below(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.EAST));
        level.setBlockAndUpdate(pos.below().east(), Blocks.CHEST.defaultBlockState());
        var food = (HopperBlockEntity) level.getBlockEntity(pos.above());
        var salt = (HopperBlockEntity) level.getBlockEntity(pos.west());
        var output = (HopperBlockEntity) level.getBlockEntity(pos.below());
        var chest = (Container) level.getBlockEntity(pos.below().east());
        food.setItem(0, new ItemStack(Items.STONE, 5));
        food.setItem(1, new ItemStack(Items.COD, 8));
        salt.setItem(0, new ItemStack(ModItems.SALT.get(), 2));
        for (int i = 0; i < output.getContainerSize(); i++) output.setItem(i, new ItemStack(Items.STONE, 64));
        for (int i = 0; i < chest.getContainerSize(); i++) chest.setItem(i, new ItemStack(Items.STONE, 64));
        helper.runAfterDelay(70, () -> {
            check(tub.isSalted() && !tub.isReady() && tub.layers() == 4 && tub.getItem(2).isEmpty(), "Hoppers bypassed processing or did not supply a full batch");
            check(food.getItem(0).getCount() == 5, "Hopper consumed rejected items");
            var saved = tub.saveWithFullMetadata(level.registryAccess());
            var restored = (SaltingTubBlockEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pos, tub.getBlockState(), saved, level.registryAccess());
            restored.setLevel(level);
            check(restored.isSalted() && restored.remainingTicks() == tub.remainingTicks()
                    && restored.getItem(1).getCount() == tub.getItem(1).getCount(), "Saved automated tub lost supplies or processing time");
        });
        helper.runAfterDelay(150, () -> {
            check(tub.isReady() && tub.getItem(2).is(ModItems.SALTED_RAW_FISH.get()) && tub.getItem(2).getCount() == 4, "Blocked output lost the finished batch");
            check(food.getItem(1).getCount() + tub.getItem(0).getCount() == 4, "Blocked output consumed the next batch's food");
            check(salt.getItem(0).getCount() + tub.getItem(1).getCount() == 1, "Blocked output consumed extra salt");
            // Simulate an extraction rollback: restoring the last serving must preserve the ready batch.
            var all = tub.removeItem(2, 4);
            var drainedSave = tub.saveWithFullMetadata(level.registryAccess());
            var drained = (SaltingTubBlockEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pos, tub.getBlockState(), drainedSave, level.registryAccess());
            check(drained.getItem(2).isEmpty(), "Reload duplicated an already extracted batch");
            tub.setItem(2, all);
            check(tub.isReady() && tub.getItem(2).getCount() == 4, "Output rollback lost the batch");
            output.clearContent();
            chest.clearContent();
        });
        helper.runAfterDelay(350, () -> {
            check(count(chest, ModItems.SALTED_RAW_FISH.get()) + count(output, ModItems.SALTED_RAW_FISH.get()) == 8,
                    "Hoppers failed to collect two complete batches");
            check(tub.layers() == 0 && !tub.isSalted() && tub.isEmpty(), "Tub did not reset automatically");
            check(food.getItem(1).isEmpty() && salt.isEmpty() && food.getItem(0).getCount() == 5, "Automation duplicated, lost or consumed wrong supplies");
            check(count(chest, Items.COD) == 0 && count(chest, ModItems.SALT.get()) == 0, "Bottom hopper extracted unfinished ingredients");
            // Buffered supplies must also survive breaking the block.
            tub.setItem(0, new ItemStack(Items.COD, 3));
            tub.setItem(1, new ItemStack(ModItems.SALT.get(), 2));
            level.destroyBlock(pos, true);
            var drops = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, new net.minecraft.world.phys.AABB(pos).inflate(1));
            check(drops.stream().filter(e -> e.getItem().is(Items.COD)).mapToInt(e -> e.getItem().getCount()).sum() == 3
                    && drops.stream().filter(e -> e.getItem().is(ModItems.SALT.get())).mapToInt(e -> e.getItem().getCount()).sum() == 2,
                    "Breaking the tub lost buffered supplies");
            drops.forEach(net.minecraft.world.entity.item.ItemEntity::discard);
            helper.succeed();
        });
    }

    private static int count(Container container, net.minecraft.world.item.Item item) {
        int total = 0;
        for (int i = 0; i < container.getContainerSize(); i++) if (container.getItem(i).is(item)) total += container.getItem(i).getCount();
        return total;
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
}
