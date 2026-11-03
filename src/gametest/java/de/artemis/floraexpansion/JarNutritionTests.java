package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.JamJarBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class JarNutritionTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var local = new BlockPos(1, 2, 1);
        var pos = helper.absolutePos(local);
        helper.setBlock(local.below(), Blocks.STONE);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        var inventory = helper.makeMockPlayer(GameType.SURVIVAL);
        var placed = helper.makeMockPlayer(GameType.SURVIVAL);
        var combined = helper.makeMockPlayer(GameType.SURVIVAL);
        int comparisons = 0;
        for (var jar : new JamJarBlock[]{ModBlocks.BLUEBERRY_JAM.get(), ModBlocks.STRAWBERRY_JAM.get(),
                ModBlocks.CHERRY_JAM.get(), ModBlocks.APPLE_JAM.get()}) {
            for (int portions = 1; portions <= 4; portions++) {
                var biteFood = JamJarBlock.foodForBite(portions);
                var remainingFood = JamJarBlock.foodForPortions(portions);
                if (biteFood.nutrition() != 2 || Math.abs(biteFood.saturation() - 1.6F) > 0.0001F
                        || remainingFood.nutrition() != 2 * portions
                        || Math.abs(remainingFood.saturation() - 1.6F * portions) > 0.0001F) {
                    throw new IllegalStateException("Jam must provide two hunger and 1.6 saturation per portion");
                }
                var initialState = jar.defaultBlockState().setValue(JamJarBlock.PORTIONS, portions);
                level.setBlockAndUpdate(pos, initialState);
                var food = Block.getDrops(initialState, level, pos, null).getFirst();
                var savedFood = food.copy();
                int oldNutrition = (5 * portions + 3) / 4;
                savedFood.set(net.minecraft.core.component.DataComponents.FOOD,
                        new net.minecraft.world.food.FoodProperties(oldNutrition, oldNutrition * 1.1F, false));
                reset(inventory, 10, 0);
                savedFood.finishUsingItem(level, inventory);
                if (inventory.getFoodData().getFoodLevel() != 12
                        || Math.abs(inventory.getFoodData().getSaturationLevel() - 1.6F) > 0.0001F) {
                    throw new IllegalStateException("Saved jam must use the updated portion nutrition");
                }
                for (int hunger = 0; hunger <= 20; hunger++) {
                    for (float saturation : new float[]{0, hunger / 2.0F, hunger}) {
                        reset(inventory, hunger, saturation);
                        var held = food.copy();
                        reset(placed, hunger, saturation);
                        level.setBlockAndUpdate(pos, initialState);
                        int eaten = 0;
                        float expectedSaturation = saturation;
                        for (; eaten < portions && inventory.canEat(false); eaten++) {
                            held = held.finishUsingItem(level, inventory);
                            if (eaten < portions - 1 && JamJarBlock.itemPortions(held) != portions - eaten - 1) {
                                throw new IllegalStateException("Inventory eating skipped a portion");
                            }
                            if (!level.getBlockState(pos).useWithoutItem(level, placed, hit).consumesAction()) {
                                throw new IllegalStateException("Jar refused a bite during nutrition comparison");
                            }
                            same(inventory, placed, "Per-portion nutrition");
                            int expectedHunger = Math.min(20, hunger + 2 * (eaten + 1));
                            expectedSaturation = Math.min(expectedHunger, expectedSaturation + 1.6F);
                            if (inventory.getFoodData().getFoodLevel() != expectedHunger
                                    || Math.abs(inventory.getFoodData().getSaturationLevel() - expectedSaturation) > 0.0001F) {
                                throw new IllegalStateException("Jam portion restored incorrect hunger or saturation");
                            }
                        }
                        if (eaten == portions && !held.is(de.artemis.floraexpansion.common.registry.ModItems.GLASS_JAR.get())) {
                            throw new IllegalStateException("Final portion must return an empty jar");
                        }
                        if (eaten < portions && (JamJarBlock.itemPortions(held) != portions - eaten
                                || level.getBlockState(pos).useWithoutItem(level, placed, hit).consumesAction()
                                || held.get(net.minecraft.core.component.DataComponents.CONSUMABLE)
                                        .canConsume(inventory, held))) {
                            throw new IllegalStateException("Full player must keep the remaining jam portions");
                        }
                        same(inventory, placed, "inventory vs placed: " + jar + ", portions=" + portions
                                + ", starting hunger=" + hunger + ", saturation=" + saturation);
                        comparisons++;
                    }
                }
            }
            for (int hunger = 0; hunger <= 13; hunger++) {
                for (float saturation : new float[]{0, hunger / 2.0F, hunger}) {
                    reset(inventory, hunger, saturation);
                    eatAll(new ItemStack(jar), level, inventory);
                    for (int bites = 1; bites <= 3; bites++) {
                        reset(combined, hunger, saturation);
                        level.setBlockAndUpdate(pos, jar.defaultBlockState());
                        for (int bite = 0; bite < bites; bite++) level.getBlockState(pos).useWithoutItem(level, combined, hit);
                        var remainder = Block.getDrops(level.getBlockState(pos), level, pos, null).getFirst();
                        eatAll(remainder, level, combined);
                        same(inventory, combined, "placed bites plus inventory remainder: " + jar + ", bites=" + bites
                                + ", starting hunger=" + hunger + ", saturation=" + saturation);
                        comparisons++;
                    }
                }
            }
        }
        FloraExpansion.LOGGER.info("Jar nutrition verified in {} comparisons: hunger and saturation match for inventory, placed, partial, and mixed consumption", comparisons);
    }

    private static void eatAll(ItemStack stack, net.minecraft.world.level.Level level, Player player) {
        int portions = JamJarBlock.itemPortions(stack);
        for (int i = 0; i < portions; i++) stack = stack.finishUsingItem(level, player);
    }

    private static void reset(Player player, int hunger, float saturation) {
        player.getFoodData().setFoodLevel(hunger);
        player.getFoodData().setSaturation(saturation);
    }

    private static void same(Player expected, Player actual, String context) {
        var first = expected.getFoodData();
        var second = actual.getFoodData();
        if (first.getFoodLevel() != second.getFoodLevel()
                || Math.abs(first.getSaturationLevel() - second.getSaturationLevel()) > 0.0001F) {
            throw new IllegalStateException(context + ": expected " + first.getFoodLevel() + "/" + first.getSaturationLevel()
                    + ", got " + second.getFoodLevel() + "/" + second.getSaturationLevel());
        }
    }
}
