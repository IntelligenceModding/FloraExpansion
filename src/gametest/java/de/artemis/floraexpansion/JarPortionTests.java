package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.JamJarBlock;
import de.artemis.floraexpansion.common.block.JarBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFoods;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class JarPortionTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var local = new BlockPos(1, 2, 1);
        var pos = helper.absolutePos(local);
        helper.setBlock(local.below(), Blocks.STONE);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        var placementHit = new BlockHitResult(Vec3.atCenterOf(pos.below()).add(0, 0.5, 0), Direction.UP, pos.below(), false);
        Item[] fruits = {ModItems.BLUEBERRIES.get(), ModItems.STRAWBERRY.get(), ModItems.CHERRIES.get(), Items.APPLE};
        JamJarBlock[] jars = {ModBlocks.BLUEBERRY_JAM.get(), ModBlocks.STRAWBERRY_JAM.get(), ModBlocks.CHERRY_JAM.get(), ModBlocks.APPLE_JAM.get()};
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        for (int kind = 0; kind < jars.length; kind++) {
            var jar = jars[kind];
            helper.setBlock(local, jar);
            player.getFoodData().setFoodLevel(20);
            check(!level.getBlockState(pos).useWithoutItem(level, player, hit).consumesAction(), "Full player ate placed jam");
            player.getFoodData().setFoodLevel(0);
            player.getFoodData().setSaturation(0);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            for (int portions = 4; portions > 0; portions--) {
                check(level.getBlockState(pos).getValue(JamJarBlock.PORTIONS) == portions, "Jar skipped a portion");
                check(level.getBlockState(pos).useWithoutItem(level, player, hit).consumesAction(), "Placed portion could not be eaten");
            }
            check(level.getBlockState(pos).is(ModBlocks.GLASS_JAR.get()), "Last portion did not leave an empty jar");
            check(player.getFoodData().getFoodLevel() == ModFoods.JAM.nutrition(), "Four bites changed whole-jar nutrition");
            var fruit = new ItemStack(fruits[kind], 5);
            player.setItemInHand(InteractionHand.MAIN_HAND, fruit);
            for (int portions = 1; portions <= 4; portions++) {
                check(level.getBlockState(pos).useItemOn(fruit, level, player, InteractionHand.MAIN_HAND, hit).consumesAction(), "Matching fruit rejected");
                check(level.getBlockState(pos).is(jar) && level.getBlockState(pos).getValue(JamJarBlock.PORTIONS) == portions
                        && fruit.getCount() == 5 - portions, "Refill selected wrong flavor or consumed wrong count");
            }
            check(level.getBlockState(pos).useItemOn(fruit, level, player, InteractionHand.MAIN_HAND, hit) == InteractionResult.FAIL
                    && fruit.getCount() == 1, "Full jar consumed extra fruit");
            var fullDrop = Block.getDrops(level.getBlockState(pos), level, pos, null).getFirst();
            check(ItemStack.isSameItemSameComponents(fullDrop, new ItemStack(jar)), "Full jar no longer stacks with existing jars");
            for (int portions = 1; portions < 4; portions++) {
                var state = jar.defaultBlockState().setValue(JamJarBlock.PORTIONS, portions);
                level.setBlockAndUpdate(pos, state);
                var wrong = new ItemStack(fruits[(kind + 1) % fruits.length], 2);
                player.setItemInHand(InteractionHand.MAIN_HAND, wrong);
                check(state.useItemOn(wrong, level, player, InteractionHand.MAIN_HAND, hit) == InteractionResult.FAIL
                        && wrong.getCount() == 2 && level.getBlockState(pos) == state, "Wrong fruit changed a partial jar");
                var partial = Block.getDrops(state, level, pos, null).getFirst();
                check(JamJarBlock.itemPortions(partial) == portions && partial.get(DataComponents.FOOD).nutrition()
                        == JamJarBlock.foodForPortions(portions).nutrition(), "Partial drop restored free jam or food");
                partial = ItemStack.CODEC.parse(ops, ItemStack.CODEC.encodeStart(ops, partial).getOrThrow()).getOrThrow();
                check(JamJarBlock.itemPortions(partial) == portions, "Partial jar did not survive item serialization");
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                player.setItemInHand(InteractionHand.MAIN_HAND, partial);
                partial.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, placementHit));
                check(level.getBlockState(pos) == state && partial.isEmpty(), "Re-placing partial jar restored free portions");
                var food = Block.getDrops(state, level, pos, null).getFirst();
                player.getFoodData().setFoodLevel(0);
                for (int bite = 0; bite < portions; bite++) food = food.finishUsingItem(level, player);
                check(food.is(ModItems.GLASS_JAR.get())
                        && player.getFoodData().getFoodLevel() == JamJarBlock.foodForPortions(portions).nutrition(),
                        "Held partial jar restored full food or lost its container");
            }
            helper.setBlock(local, ModBlocks.GLASS_JAR.get());
            helper.setBlock(local.above(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.FACING, Direction.DOWN));
            var hopper = (HopperBlockEntity) level.getBlockEntity(pos.above());
            hopper.setItem(0, new ItemStack(fruits[kind], 6));
            for (int tick = 0; tick < 48; tick++) HopperBlockEntity.pushItemsTick(level, pos.above(), hopper.getBlockState(), hopper);
            check(level.getBlockState(pos).is(jar) && level.getBlockState(pos).getValue(JamJarBlock.PORTIONS) == 4
                    && hopper.getItem(0).getCount() == 2, "Hopper overfilled, lost fruit, or selected wrong flavor");
            level.setBlockAndUpdate(pos, jar.defaultBlockState().setValue(JamJarBlock.PORTIONS, 2));
            hopper.setItem(0, new ItemStack(fruits[(kind + 1) % fruits.length], 3));
            for (int tick = 0; tick < 16; tick++) HopperBlockEntity.pushItemsTick(level, pos.above(), hopper.getBlockState(), hopper);
            check(hopper.getItem(0).getCount() == 3 && level.getBlockState(pos).getValue(JamJarBlock.PORTIONS) == 2,
                    "Hopper consumed mismatched fruit");
            var input = jar.getContainer(level.getBlockState(pos), level, pos);
            var rejected = HopperBlockEntity.addItem(null, input, new ItemStack(Items.STONE, 3), Direction.EAST);
            check(rejected.getCount() == 3, "Jar accepted non-fruit through automation");
            var sideInput = jar.getContainer(level.getBlockState(pos), level, pos);
            var leftover = HopperBlockEntity.addItem(null, sideInput, new ItemStack(fruits[kind]), Direction.EAST);
            check(leftover.isEmpty() && level.getBlockState(pos).getValue(JamJarBlock.PORTIONS) == 3, "Side input consumed wrong amount");
            sideInput.setChanged();
            check(level.getBlockState(pos).getValue(JamJarBlock.PORTIONS) == 3 && sideInput.getSlotsForFace(Direction.DOWN).length == 0,
                    "Repeated notification duplicated fruit or exposed jam to extraction");
            hopper.clearContent();
            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
        }
        var creative = helper.makeMockPlayer(GameType.CREATIVE);
        GameType.CREATIVE.updatePlayerAbilities(creative.getAbilities());
        helper.setBlock(local, ModBlocks.GLASS_JAR.get());
        var fruit = new ItemStack(Items.APPLE, 2);
        creative.setItemInHand(InteractionHand.MAIN_HAND, fruit);
        level.getBlockState(pos).useItemOn(fruit, level, creative, InteractionHand.MAIN_HAND, hit);
        check(fruit.getCount() == 2 && level.getBlockState(pos).getValue(JamJarBlock.PORTIONS) == 1, "Creative refill consumed fruit");
    }

    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
}
