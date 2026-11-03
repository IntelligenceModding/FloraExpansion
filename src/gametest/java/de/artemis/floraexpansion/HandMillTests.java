package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;
import de.artemis.floraexpansion.common.inventory.HandMillMenu;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class HandMillTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(2, 3, 2));
        level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2)).forEach(ItemEntity::discard);
        var millBlock = ModBlocks.HAND_MILL.get();
        var crankBlock = ModBlocks.HAND_CRANK.get();
        level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(pos, millBlock.defaultBlockState());
        level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
        var mill = (HandMillBlockEntity) level.getBlockEntity(pos);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(Vec3.atCenterOf(pos.offset(1, 0, 1)));
        var menu = (HandMillMenu) mill.createMenu(1, player.getInventory(), player);
        check(menu.slots.size() == 38 && menu.stillValid(player), "Mill menu inventory invalid");
        check(menu.slots.get(0).mayPlace(new ItemStack(ModItems.RAW_SALT.get()))
                && !menu.slots.get(0).mayPlace(new ItemStack(Items.SAND))
                && !menu.slots.get(1).mayPlace(new ItemStack(ModItems.SALT.get())), "Slot filters incorrect");
        check(!Block.isShapeFullBlock(level.getBlockState(pos).getCollisionShape(level, pos)), "Mill still collides as a full cube");
        check(level.getBlockState(pos).getLightBlock() == 0 && level.getBlockState(pos).propagatesSkylightDown(),
                "Open mill housing blocks environmental light");
        menu.slots.get(0).setByPlayer(new ItemStack(ModItems.RAW_SALT.get(), 4));
        check(!mill.crank(player) && mill.getProgress() == 0, "Mill worked without a crank");
        check(crankBlock.defaultBlockState().canSurvive(level, pos.above()), "Crank cannot attach above mill");
        check(!crankBlock.defaultBlockState().canSurvive(level, pos.offset(1, 1, 0)), "Crank can float beside mill");
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(crankBlock));
        var placement = new net.minecraft.world.item.context.UseOnContext(player, net.minecraft.world.InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.5, 0), Direction.UP, pos, false));
        player.getMainHandItem().useOn(placement);
        check(level.getBlockState(pos.above()).is(crankBlock), "Could not place separate crank on mill");
        var hit = new BlockHitResult(Vec3.atCenterOf(pos.above()), Direction.UP, pos.above(), false);
        for (int turn = 1; turn <= 3; turn++) {
            level.getBlockState(pos.above()).useWithoutItem(level, player, hit);
            check(menu.getProgress() == turn && menu.getArrowWidth() == turn * 6, "Menu arrow did not track grinding progress");
            check(mill.getItem(1).isEmpty() && mill.getItem(0).getCount() == 4, "Mill processed input too early");
            check(level.getBlockState(pos.above()).getValue(HandCrankBlock.TURN) == turn, "Crank model did not turn");
        }
        mill.setItem(1, new ItemStack(ModItems.SALT.get(), 64));
        check(!mill.crank(player) && mill.getProgress() == 3 && mill.getItem(0).getCount() == 4, "Full output consumed input/progress");
        var saved = mill.saveWithFullMetadata(level.registryAccess());
        var restored = (HandMillBlockEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                pos, level.getBlockState(pos), saved, level.registryAccess());
        check(restored != null && restored.getItem(0).getCount() == 4 && restored.getItem(1).getCount() == 64
                && restored.getProgress() == 3, "Save/load lost mill inventory or progress");
        level.setBlockEntity(restored);
        mill = restored;
        mill.removeItem(1, 64);
        level.getBlockState(pos.above()).useWithoutItem(level, player, hit);
        check(mill.getItem(0).getCount() == 3 && mill.getItem(1).is(ModItems.SALT.get())
                && mill.getItem(1).getCount() == 1 && mill.getProgress() == 0, "Fourth crank did not produce one salt in output");
        menu = (HandMillMenu) mill.createMenu(2, player.getInventory(), player);
        check(menu.quickMoveStack(player, 1).is(ModItems.SALT.get()) && mill.getItem(1).isEmpty(), "Shift-click output failed");
        player.getInventory().setItem(9, new ItemStack(ModItems.RAW_SALT.get(), 5));
        menu.quickMoveStack(player, 2);
        check(mill.getItem(0).getCount() == 8 && player.getInventory().getItem(9).isEmpty(), "Shift-click input failed");
        mill.setItem(1, new ItemStack(ModItems.SALT.get(), 2));
        level.removeBlock(pos, false);
        var drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2));
        check(drops.stream().filter(e -> e.getItem().is(ModItems.RAW_SALT.get())).mapToInt(e -> e.getItem().getCount()).sum() == 8,
                "Breaking mill lost input");
        check(drops.stream().filter(e -> e.getItem().is(ModItems.SALT.get())).mapToInt(e -> e.getItem().getCount()).sum() == 2,
                "Breaking mill lost output");
        check(level.getBlockState(pos.above()).isAir(), "Crank stayed floating after mill removal");
        check(drops.stream().filter(e -> e.getItem().is(crankBlock.asItem())).mapToInt(e -> e.getItem().getCount()).sum() == 1,
                "Unsupported crank was lost or duplicated");
        check(!menu.stillValid(player), "Menu stayed valid after mill removal");
        check(Block.getDrops(millBlock.defaultBlockState(), level, pos, null).stream().anyMatch(s -> s.is(millBlock.asItem())), "Mill loot missing");
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
}
