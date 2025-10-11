package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {

        //Shapeless
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINE_CONE, 1)
                .requires(ModBlocks.PINE_LITTER)
                .unlockedBy("has_pine_litter", has(ModBlocks.PINE_LITTER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINE_NUTS, 2)
                .requires(ModItems.PINE_CONE)
                .unlockedBy("has_pine_cone", has(ModItems.PINE_CONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STICK, 2)
                .requires(ModItems.TWIG)
                .unlockedBy("has_twig", has(ModItems.TWIG))
                .save(recipeOutput);

        //Smelting
        foodSmelting(recipeOutput, ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 100);

        //Smoking
        foodSmoking(recipeOutput, ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 50);

        //Campfire Cooking
        foodCampfireCooking(recipeOutput, ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 150);

        /**ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.STONE_BRICKS)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', Blocks.STONE)
                .unlockedBy("has_stone", has(Blocks.STONE)).save(recipeOutput);**/

    }

    protected static void oreSmelting(@NotNull RecipeOutput recipeOutput, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result,
                experience, cookingTime, group, "_from_smelting");
    }

    protected static void oreBlasting(@NotNull RecipeOutput recipeOutput, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result,
                experience, cookingTime, group, "_from_blasting");
    }

    protected static void foodSmelting(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_smelting");
    }

    protected static void foodSmoking(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_smoking");
    }

    protected static void foodCampfireCooking(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_campfire_cooking");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(@NotNull RecipeOutput recipeOutput, RecipeSerializer<T> cookingSerializer, AbstractCookingRecipe.@NotNull Factory<T> factory, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group, String recipeName) {
        for(ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, cookingSerializer, factory).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(result) + recipeName + "_" + getItemName(itemlike));
        }
    }

}
