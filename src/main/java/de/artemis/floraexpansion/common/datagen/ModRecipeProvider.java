package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
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

        /**ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Blocks.STONE_BRICKS, 18)
                .requires(Blocks.STONE)
                .unlockedBy("has_stone", has(Blocks.STONE))
                .save(recipeOutput, "floraexpansion:stone_bricks_from_stone"); **/


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

    protected static <T extends AbstractCookingRecipe> void oreCooking(@NotNull RecipeOutput recipeOutput, RecipeSerializer<T> cookingSerializer, AbstractCookingRecipe.@NotNull Factory<T> factory, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group, String recipeName) {
        for(ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, result, experience, cookingTime, cookingSerializer, factory).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(result) + recipeName + "_" + getItemName(itemlike));
        }
    }
}
