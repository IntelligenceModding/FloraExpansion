package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
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




        stairBuilder(ModBlocks.CACTUS_STAIRS.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_SLAB.get(), ModBlocks.CACTUS_PLANKS.get());

        fenceBuilder(ModBlocks.CACTUS_FENCE.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        fenceGateBuilder(ModBlocks.CACTUS_FENCE_GATE.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        doorBuilder(ModBlocks.CACTUS_DOOR.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        trapdoorBuilder(ModBlocks.CACTUS_TRAPDOOR.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        buttonBuilder(ModBlocks.CACTUS_BUTTON.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        pressurePlate(recipeOutput, ModBlocks.CACTUS_PRESSURE_PLATE.get(), ModBlocks.CACTUS_PLANKS.get());

        signBuilder(ModItems.CACTUS_SIGN.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        hangingSign(recipeOutput, ModItems.CACTUS_HANGING_SIGN.get(), ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get());

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, ModItems.CACTUS_CHEST_BOAT.get())
                .requires(ModItems.CACTUS_BOAT.get())
                .requires(Blocks.CHEST)
                .unlockedBy(getHasName(ModItems.CACTUS_BOAT.get()), has(ModItems.CACTUS_BOAT.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.GIANT_CACTUS_WOOD.get(), 3)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.GIANT_CACTUS_BASE.get())
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_BASE.get()), has(ModBlocks.GIANT_CACTUS_BASE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(), 3)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()), has(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModItems.CACTUS_BOAT.get())
                .pattern("# #")
                .pattern("###")
                .define('#', ModBlocks.CACTUS_PLANKS.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.GIANT_CACTUS_BASE.get())
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_BASE.get()),
                        has(ModBlocks.GIANT_CACTUS_BASE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "cactus_planks_from_giant_cactus_base"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()),
                        has(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "cactus_planks_from_stripped_giant_cactus_base"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.GIANT_CACTUS_WOOD.get())
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_WOOD.get()),
                        has(ModBlocks.GIANT_CACTUS_WOOD.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "cactus_planks_from_giant_cactus_wood"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get()),
                        has(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "cactus_planks_from_stripped_giant_cactus_wood"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.GIANT_CACTUS_STEM.get(), 4)
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_STEM.get()),
                        has(ModBlocks.GIANT_CACTUS_STEM.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "cactus_planks_from_giant_cactus_stem"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.CACTUS_SLICE.get(), 2)
                .requires(ModBlocks.GIANT_CACTUS_STEM, 1)
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_STEM.get()),
                        has(ModBlocks.GIANT_CACTUS_STEM.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Items.STICK, 4)
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.GIANT_CACTUS_STEM.get())
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "stick_from_giant_cactus_stem"));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.CACTUS_HELMET.get())
                .pattern("TDT")
                .pattern(" H ")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_THORN.get())
                .define('H', Items.LEATHER_HELMET)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_THORN.get()), has(ModBlocks.CACTUS_THORN.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.CACTUS_CHESTPLATE.get())
                .pattern("TDT")
                .pattern("TCT")
                .pattern("TTT")
                .define('T', ModBlocks.CACTUS_THORN.get())
                .define('C', Items.LEATHER_CHESTPLATE)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_THORN.get()), has(ModBlocks.CACTUS_THORN.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.CACTUS_LEGGINGS.get())
                .pattern("TDT")
                .pattern("TLT")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_THORN.get())
                .define('L', Items.LEATHER_LEGGINGS)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_THORN.get()), has(ModBlocks.CACTUS_THORN.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.CACTUS_BOOTS.get())
                .pattern("TDT")
                .pattern(" B ")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_THORN.get())
                .define('B', Items.LEATHER_BOOTS)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_THORN.get()), has(ModBlocks.CACTUS_THORN.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_MOSAIC.get(), 1)
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.CACTUS_SLAB.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_SLAB.get()), has(ModBlocks.CACTUS_SLAB.get()))
                .save(recipeOutput);

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

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.CRATE.get())
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ItemTags.PLANKS)
                .define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BASKET.get())
                .pattern("T T")
                .pattern("TTT")
                .define('T', ModItems.LINEN_THREAD.get())
                .unlockedBy("has_linen_thread", has(ModItems.LINEN_THREAD.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WOODEN_BUCKET.get())
                .pattern("P P")
                .pattern(" P ")
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
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
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "light_blue_dye_from_flax_flower"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PINE_NUTS, 2)
                .requires(ModItems.PINE_CONE)
                .unlockedBy("has_pine_cone", has(ModItems.PINE_CONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.STICK, 2)
                .requires(ModItems.TWIG)
                .unlockedBy("has_twig", has(ModItems.TWIG))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "stick_from_twig"));

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

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.BLUEBERRY_COOKIE.get(), 8)
                .pattern("WBW")
                .define('W', Items.WHEAT)
                .define('B', ModItems.BLUEBERRIES.get())
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.BLUEBERRY_PIE.get())
                .pattern("BBB")
                .pattern("SES")
                .pattern("WWW")
                .define('B', ModItems.BLUEBERRIES.get())
                .define('S', Items.SUGAR)
                .define('E', Items.EGG)
                .define('W', Items.WHEAT)
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BLUEBERRY_PIE_SLICE.get(), 4)
                .requires(ModItems.BLUEBERRY_PIE.get())
                .unlockedBy("has_blueberry_pie", has(ModItems.BLUEBERRY_PIE.get()))
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
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "brown_dye_from_cherry_pit"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BROWN_DYE, 1)
                .requires(ModBlocks.APPLE_CORE, 1)
                .unlockedBy("has_apple_core", has(ModBlocks.APPLE_CORE))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "brown_dye_from_apple_core"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHERRY_JUICE, 1)
                .requires(ModItems.CHERRIES, 3)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_cherries", has(ModItems.CHERRIES))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CACTUS_JUICE, 1)
                .requires(ModItems.CACTUS_SLICE, 2)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_cactus_slice", has(ModItems.CACTUS_SLICE))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BLUEBERRY_JUICE, 1)
                .requires(ModItems.BLUEBERRIES.get(), 3)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BLUEBERRY_JAM, 1)
                .requires(ModItems.BLUEBERRIES.get(), 4)
                .requires(Items.SUGAR)
                .requires(ModItems.EMPTY_JAR.get())
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .unlockedBy("has_empty_jar", has(ModItems.EMPTY_JAR.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.STRAWBERRY_JAM, 1)
                .requires(ModItems.STRAWBERRY.get(), 4)
                .requires(Items.SUGAR)
                .requires(ModItems.EMPTY_JAR.get())
                .unlockedBy("has_strawberry", has(ModItems.STRAWBERRY.get()))
                .unlockedBy("has_empty_jar", has(ModItems.EMPTY_JAR.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModBlocks.STRAWBERRY_CAKE.get())
                .requires(Items.CAKE)
                .requires(ModItems.STRAWBERRY.get(), 3)
                .unlockedBy("has_cake", has(Items.CAKE))
                .unlockedBy("has_strawberry", has(ModItems.STRAWBERRY.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EMPTY_JAR.get(), 3)
                .pattern("G G")
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GLASS)
                .unlockedBy("has_glass", has(Items.GLASS))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.APPLE_JUICE, 1)
                .requires(Items.APPLE, 2)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_apples", has(Items.APPLE))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.PINK_DYE, 1)
                .requires(ModBlocks.CACTUS_FLOWER.get())
                .unlockedBy("has_cactus_flower", has(ModBlocks.CACTUS_FLOWER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "pink_dye_from_cactus_flower"));

        //Smelting
        foodSmelting(recipeOutput, ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 100);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.CACTUS_CLUSTER.get()),
                        RecipeCategory.MISC,
                        Items.GREEN_DYE,
                        0.2f,
                        200
                )
                .unlockedBy("has_cactus_cluster", has(ModBlocks.CACTUS_CLUSTER.get()))
                .save(recipeOutput, ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "green_dye_from_smelting_cactus_cluster"));

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

