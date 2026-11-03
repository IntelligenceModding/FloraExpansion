package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.GameType;

final class CuredFoodTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(2, 3, 2));
        Item[][] comparisons = {
                {ModItems.CURED_MEAT.get(), Items.COOKED_BEEF},
                {ModItems.CURED_MEAT.get(), Items.COOKED_PORKCHOP},
                {ModItems.CURED_FISH.get(), Items.COOKED_SALMON},
                {ModItems.CURED_FISH.get(), Items.COOKED_COD},
                {ModItems.SALTED_SPRUCE_NUTS.get(), ModItems.SPRUCE_NUTS.get()},
                {ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(), ModItems.TOASTED_SPRUCE_NUTS.get()},
                {ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(), ModItems.SALTED_SPRUCE_NUTS.get()}
        };
        for (var pair : comparisons) {
            var food = new ItemStack(pair[0]).get(DataComponents.FOOD);
            var base = new ItemStack(pair[1]).get(DataComponents.FOOD);
            check(food != null && base != null && food.nutrition() >= base.nutrition()
                    && food.saturation() > base.saturation(), "Processing did not improve food value: " + pair[0]);
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            player.getFoodData().setFoodLevel(10);
            player.getFoodData().setSaturation(0);
            var remainder = new ItemStack(pair[0], 2).finishUsingItem(level, player);
            check(remainder.getCount() == 1 && player.getFoodData().getFoodLevel() == 10 + food.nutrition()
                    && Math.abs(player.getFoodData().getSaturationLevel() - food.saturation()) < .001F,
                    "Eating processed food restored the wrong nutrition");
            check(player.getActiveEffects().isEmpty(), "Cured food applied an unwanted effect");
        }
        for (var raw : new Item[]{Items.BEEF, Items.PORKCHOP, Items.MUTTON, Items.CHICKEN, Items.RABBIT, Items.COD, Items.SALMON}) {
            level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
            var tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
            for (int layer = 0; layer < 4; layer++) check(tub.addFood(new ItemStack(raw)), "Curable food rejected: " + raw);
            check(tub.salt(), "Could not salt raw food");
            SaltingTubTests.advance(tub, 100);
            var batch = tub.take(false);
            check(batch.size() == 1 && batch.getFirst().getCount() == 4, "Salting changed serving count");
            var salted = batch.getFirst();
            check(salted.get(DataComponents.FOOD) == null, "Raw salted food bypassed campfire drying");
            var input = new SingleRecipeInput(salted.copyWithCount(1));
            var cooking = level.recipeAccess().recipeMap().byType(RecipeType.CAMPFIRE_COOKING).stream()
                    .map(holder -> holder.value()).filter(recipe -> recipe.matches(input, level)).findFirst().orElseThrow();
            var result = cooking.assemble(input, level.registryAccess());
            boolean fish = raw == Items.COD || raw == Items.SALMON;
            check(result.is(fish ? ModItems.CURED_FISH.get() : ModItems.CURED_MEAT.get()) && result.getCount() == 1
                    && result.get(DataComponents.FOOD) != null, "Campfire cured the wrong food or duplicated servings");
            check(cooking.cookingTime() >= 600, "Curing lost its drying time");
        }
        level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
        var tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
        for (var invalid : new Item[]{Items.PUFFERFISH, Items.TROPICAL_FISH, Items.ROTTEN_FLESH, Items.COOKED_BEEF})
            check(!tub.addFood(new ItemStack(invalid)), "Inappropriate raw curing ingredient accepted: " + invalid);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
