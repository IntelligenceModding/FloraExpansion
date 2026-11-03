package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class AuditJarTests {
    static void verify(CompatibilityTestHelper helper) {
        var level = helper.getLevel();
        var local = new BlockPos(1, 2, 1);
        var pos = helper.absolutePos(local);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos.below()).add(0, 0.5, 0), Direction.UP, pos.below(), false);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (Item item : new Item[]{ModItems.GLASS_JAR.get(), ModItems.BLUEBERRY_JAM.get(), ModItems.STRAWBERRY_JAM.get(),
                ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get()}) {
            check(item instanceof BlockItem, "Jar is not a block item");
            var block = ((BlockItem) item).getBlock();
            check(block.asItem() == item, "Jar block is not mapped to existing item");
            helper.setBlock(local.below(), Blocks.STONE);
            var held = new ItemStack(item, 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, held);
            var result = held.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
            check(result.consumesAction() && level.getBlockState(pos).is(block) && held.getCount() == 1,
                    "Jar placement failed or consumed wrong amount: " + item);
            var state = level.getBlockState(pos);
            check(state.canSurvive(level, pos) && !state.isCollisionShapeFullBlock(level, pos), "Jar support or shape is wrong");
            var drops = Block.getDrops(state, level, pos, null);
            check(drops.size() == 1 && drops.getFirst().is(item) && drops.getFirst().getCount() == 1,
                    "Breaking jar lost its contents");
            level.destroyBlock(pos.below(), false);
            check(level.getBlockState(pos).isAir(), "Unsupported jar floated in air");
            int dropped = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2)).stream()
                    .map(ItemEntity::getItem).filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
            check(dropped == 1, "Support loss did not return intact jar: " + item + ", observed=" + dropped
                    + ", chunk=" + level.getChunkAt(pos).getFullStatus()
                    + ", nearby=" + level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2)));
        }
        helper.setBlock(local.below(), Blocks.STONE);
        var creative = helper.makeMockPlayer(GameType.CREATIVE);
        GameType.CREATIVE.updatePlayerAbilities(creative.getAbilities());
        var stack = new ItemStack(ModItems.CHERRY_JAM.get(), 2);
        creative.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(creative, InteractionHand.MAIN_HAND, hit));
        check(stack.getCount() == 2 && level.getBlockState(pos).is(ModBlocks.CHERRY_JAM.get()), "Creative jar placement consumed jam");
    }

    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
}
