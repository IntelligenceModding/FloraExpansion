package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

final class AuditHandMillHopperTests {
    static void automation(CompatibilityTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(2, 3, 2));
        level.setBlockAndUpdate(pos, ModBlocks.HAND_MILL.get().defaultBlockState());
        var mill = (HandMillBlockEntity) level.getBlockEntity(pos);
        for (var face : Direction.values()) {
            var raw = new ItemStack(ModItems.RAW_SALT.get(), 2);
            var remainder = HopperBlockEntity.addItem(null, mill, raw, face);
            check(face == Direction.DOWN ? remainder.getCount() == 2 && mill.isEmpty()
                    : remainder.isEmpty() && mill.getItem(0).getCount() == 2 && mill.getItem(1).isEmpty(),
                    "Mill hopper insertion routing incorrect for " + face);
            mill.clearContent();
            var wrong = HopperBlockEntity.addItem(null, mill, new ItemStack(Items.STONE, 3), face);
            check(wrong.getCount() == 3 && mill.isEmpty(), "Mill accepted a non-grinding ingredient from " + face);
            check(!mill.canTakeItemThroughFace(0, raw, face), "Automation can steal unground input");
            check(mill.canTakeItemThroughFace(1, new ItemStack(ModItems.SALT.get()), face) == (face == Direction.DOWN),
                    "Mill hopper extraction routing incorrect for " + face);
        }
        mill.setItem(0, new ItemStack(ModItems.RAW_SALT.get(), 63));
        var overflow = HopperBlockEntity.addItem(null, mill, new ItemStack(ModItems.RAW_SALT.get(), 3), Direction.EAST);
        check(overflow.getCount() == 2 && mill.getItem(0).getCount() == 64 && mill.getItem(1).isEmpty(),
                "Full input overflowed into output or lost items");
        mill.clearContent();

        // A real ticking side hopper feeds the input while the top remains available for the crank.
        level.setBlockAndUpdate(pos.west(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.EAST));
        level.setBlockAndUpdate(pos.below(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.EAST));
        level.setBlockAndUpdate(pos.below().east(), Blocks.CHEST.defaultBlockState());
        level.setBlockAndUpdate(pos.above(), ModBlocks.HAND_CRANK.get().defaultBlockState());
        var input = (HopperBlockEntity) level.getBlockEntity(pos.west());
        var output = (HopperBlockEntity) level.getBlockEntity(pos.below());
        var chest = (Container) level.getBlockEntity(pos.below().east());
        input.setItem(0, new ItemStack(Items.STONE, 5));
        input.setItem(1, new ItemStack(ModItems.RAW_SALT.get(), 2));
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.runAfterDelay(12, () -> {
            check(mill.getItem(0).is(ModItems.RAW_SALT.get()) && mill.getItem(0).getCount() == 2,
                    "Side hopper did not fill the mill with its crank attached");
            check(input.getItem(0).getCount() == 5 && input.getItem(1).isEmpty(), "Hopper lost rejected items or input");
            check(output.isEmpty() && chest.isEmpty(), "Bottom hopper extracted unground input");
            check(mill.getProgress() == 0 && mill.getItem(1).isEmpty(), "Hopper insertion bypassed manual grinding");
            // Replenishing the input must preserve the work already done on the current item.
            turn(level, pos.above(), player);
            check(mill.getProgress() == 1, "Mill would not start with hopper-fed input");
            HopperBlockEntity.addItem(null, mill, new ItemStack(ModItems.RAW_SALT.get()), Direction.NORTH);
            check(mill.getProgress() == 1, "Hopper top-up reset grinding progress");
            for (int turns = 1; turns < mill.getRequiredTurns(); turns++) turn(level, pos.above(), player);
            check(mill.getProgress() == 0 && !mill.getItem(1).isEmpty(), "Hopper-fed recipe stopped unexpectedly");
        });
        helper.runAfterDelay(32, () -> {
            check(count(chest, ModItems.SALT.get()) == 1 && output.isEmpty() && mill.getItem(1).isEmpty(),
                    "Bottom hopper did not deliver the finished salt to the chest");
            check(mill.getItem(0).getCount() == 2 && count(chest, ModItems.RAW_SALT.get()) == 0,
                    "Output extraction stole input or duplicated products");
            mill.setItem(1, new ItemStack(ModItems.SALT.get(), 64));
            check(!mill.crank(player) && mill.getItem(0).getCount() == 2 && mill.getProgress() == 0,
                    "Blocked output consumed input or advanced grinding");
            // The output hopper frees space on its next transfer, allowing hand grinding again.
        });
        helper.runAfterDelay(52, () -> {
            check(mill.getItem(1).getCount() < 64, "Output hopper did not clear space");
            turn(level, pos.above(), player);
            check(mill.getProgress() == 1, "Mill did not resume after the hopper cleared output space");
            check(mill.getItem(0).getCount() == 2 && input.getItem(0).getCount() == 5,
                    "Automation changed input before grinding finished");
            helper.succeed();
        });
    }
    private static void turn(net.minecraft.server.level.ServerLevel level, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        level.getBlockState(pos).useWithoutItem(level, player, new net.minecraft.world.phys.BlockHitResult(
                net.minecraft.world.phys.Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }
    private static int count(Container container, net.minecraft.world.item.Item item) {
        int total = 0;
        for (int slot = 0; slot < container.getContainerSize(); slot++) if (container.getItem(slot).is(item)) total += container.getItem(slot).getCount();
        return total;
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
    private AuditHandMillHopperTests() {}
}
