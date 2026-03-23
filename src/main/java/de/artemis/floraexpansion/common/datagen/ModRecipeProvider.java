package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
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

        //Shaped
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TWIG_LADDER, 3)
                .pattern("A A")
                .pattern("BBB")
                .pattern("A A")
                .define('A', ModItems.LINEN_THREAD)
                .define('B', ModItems.TWIG)
                .unlockedBy("has_linen_thread", has(ModItems.LINEN_THREAD))
                .unlockedBy("has_twig", has(ModItems.TWIG))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.LINEN_CARPET, 3)
                .pattern("CC")
                .define('C', ModBlocks.LINEN_BLOCK.get())
                .unlockedBy("has_linen_block", has(ModBlocks.LINEN_BLOCK.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.PAINTING)
                .pattern("SSS")
                .pattern("SLS")
                .pattern("SSS")
                .define('S', Items.STICK)
                .define('L', ModBlocks.LINEN_BLOCK.get())
                .unlockedBy("has_linen_block", has(ModBlocks.LINEN_BLOCK.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "painting_from_linen_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.WHITE_BANNER)
                .pattern("LLL")
                .pattern("LLL")
                .pattern(" S ")
                .define('L', ModBlocks.LINEN_BLOCK.get())
                .define('S', Items.STICK)
                .unlockedBy("has_linen_block", has(ModBlocks.LINEN_BLOCK.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "white_banner_from_linen_block"));

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.WHITE_BED)
                .pattern("LLL")
                .pattern("PPP")
                .define('L', ModBlocks.LINEN_BLOCK.get())
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_linen_block", has(ModBlocks.LINEN_BLOCK.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "white_bed_from_linen_block"));

        //Shapeless
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINE_CONE, 1)
                .requires(ModBlocks.PINE_LITTER)
                .unlockedBy("has_pine_litter", has(ModBlocks.PINE_LITTER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.PEBBLE_PATCH, 1)
                .requires(ModItems.PEBBLES)
                .unlockedBy("has_pebbles", has(ModItems.PEBBLES))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE, 1)
                .requires(ModItems.FLAX_FLOWER)
                .unlockedBy("has_flax_flower", has(ModItems.FLAX_FLOWER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINE_NUTS, 2)
                .requires(ModItems.PINE_CONE)
                .unlockedBy("has_pine_cone", has(ModItems.PINE_CONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STICK, 2)
                .requires(ModItems.TWIG)
                .unlockedBy("has_twig", has(ModItems.TWIG))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FOREST_SNACK, 3)
                .requires(ModItems.TOASTED_PINE_NUTS)
                .requires(Items.PUMPKIN_SEEDS)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.HONEY_BOTTLE)
                .unlockedBy("has_toasted_pine_nuts", has(ModItems.TOASTED_PINE_NUTS))
                .unlockedBy("has_pumpkin_seeds", has(Items.PUMPKIN_SEEDS))
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SWEET_BERRY_MIX, 3)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.GLOW_BERRIES)
                .requires(ModItems.CHERRIES)
                .unlockedBy("has_cherries", has(ModItems.CHERRIES))
                .unlockedBy("has_glow_berries", has(Items.GLOW_BERRIES))
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LINEN_THREAD, 2)
                .requires(ModItems.FLAX_FIBER, 4)
                .unlockedBy("has_flax_fiber", has(ModItems.FLAX_FIBER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LINEN_CLOTH, 2)
                .requires(ModItems.LINEN_THREAD, 4)
                .unlockedBy("has_linen_thread", has(ModItems.LINEN_THREAD))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.LINEN_BLOCK, 1)
                .requires(ModItems.LINEN_CLOTH, 4)
                .unlockedBy("has_linen_cloth", has(ModItems.LINEN_CLOTH))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PEBBLES, 9)
                .requires(ModBlocks.PEBBLE_BLOCK, 1)
                .unlockedBy("has_pebble_block", has(ModBlocks.PEBBLE_BLOCK))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.PEBBLE_BLOCK, 1)
                .requires(ModItems.PEBBLES, 9)
                .unlockedBy("has_pebbles", has(ModItems.PEBBLES))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.FLAX_BALE, 1)
                .requires(ModItems.FLAX_FIBER, 9)
                .unlockedBy("has_flax_fiber", has(ModItems.FLAX_FIBER))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FLAX_FIBER, 9)
                .requires(ModBlocks.FLAX_BALE, 1)
                .unlockedBy("has_flax_bale", has(ModBlocks.FLAX_BALE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BROWN_DYE, 1)
                .requires(ModBlocks.CHERRY_PIT, 1)
                .unlockedBy("has_cherry_pit", has(ModBlocks.CHERRY_PIT))
                .save(recipeOutput);

        //Smelting
        foodSmelting(recipeOutput, ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 100);

        //Smoking
        foodSmoking(recipeOutput, ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 50);

        //Campfire Cooking
        foodCampfireCooking(recipeOutput, ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 150);
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
