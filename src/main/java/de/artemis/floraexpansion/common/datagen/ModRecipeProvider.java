package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unused")
public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected @NotNull RecipeProvider createRecipeProvider(HolderLookup.@NotNull Provider provider, @NotNull RecipeOutput recipeOutput) {
            return new ModRecipeProvider(provider, recipeOutput);
        }

        @Override
        public @NotNull String getName() {
            return "Flora Expansion Recipes";
        }
    }

    @Override
    protected void buildRecipes() {

        stairBuilder(ModBlocks.CACTUS_STAIRS.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(output);

        slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_SLAB.get(), ModBlocks.CACTUS_PLANKS.get());

        fenceBuilder(ModBlocks.CACTUS_FENCE.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(output);

        fenceGateBuilder(ModBlocks.CACTUS_FENCE_GATE.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(output);

        doorBuilder(ModBlocks.CACTUS_DOOR.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(output);

        trapdoorBuilder(ModBlocks.CACTUS_TRAPDOOR.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(output);

        buttonBuilder(ModBlocks.CACTUS_BUTTON.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(output);

        pressurePlate(ModBlocks.CACTUS_PRESSURE_PLATE.get(), ModBlocks.CACTUS_PLANKS.get());

        signBuilder(ModItems.CACTUS_SIGN.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(output);

        hangingSign(ModItems.CACTUS_HANGING_SIGN.get(), ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get());

        shapeless(RecipeCategory.TRANSPORTATION, ModItems.CACTUS_CHEST_BOAT.get())
                .requires(ModItems.CACTUS_BOAT.get())
                .requires(Blocks.CHEST)
                .unlockedBy(getHasName(ModItems.CACTUS_BOAT.get()), has(ModItems.CACTUS_BOAT.get()))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.GIANT_CACTUS_WOOD.get(), 3)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.GIANT_CACTUS_BASE.get())
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_BASE.get()), has(ModBlocks.GIANT_CACTUS_BASE.get()))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(), 3)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()), has(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()))
                .save(output);

        shaped(RecipeCategory.TRANSPORTATION, ModItems.CACTUS_BOAT.get())
                .pattern("# #")
                .pattern("###")
                .define('#', ModBlocks.CACTUS_PLANKS.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(output);

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.GIANT_CACTUS_BASE.get())
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_BASE.get()), has(ModBlocks.GIANT_CACTUS_BASE.get()))
                .save(output, modLoc("cactus_planks_from_giant_cactus_base"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()), has(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()))
                .save(output, modLoc("cactus_planks_from_stripped_giant_cactus_base"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.GIANT_CACTUS_WOOD.get())
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_WOOD.get()), has(ModBlocks.GIANT_CACTUS_WOOD.get()))
                .save(output, modLoc("cactus_planks_from_giant_cactus_wood"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get()), has(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get()))
                .save(output, modLoc("cactus_planks_from_stripped_giant_cactus_wood"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.GIANT_CACTUS_STEM.get(), 4)
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_STEM.get()), has(ModBlocks.GIANT_CACTUS_STEM.get()))
                .save(output, modLoc("cactus_planks_from_giant_cactus_stem"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.CACTUS_SLICE.get(), 2)
                .requires(ModBlocks.GIANT_CACTUS_STEM.get(), 1)
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_STEM.get()), has(ModBlocks.GIANT_CACTUS_STEM.get()))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, Items.STICK, 4)
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.GIANT_CACTUS_STEM.get())
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output, modLoc("stick_from_giant_cactus_stem"));

        shaped(RecipeCategory.COMBAT, ModItems.CACTUS_HELMET.get())
                .pattern("TDT")
                .pattern(" H ")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_THORN.get())
                .define('H', Items.LEATHER_HELMET)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_THORN.get()), has(ModBlocks.CACTUS_THORN.get()))
                .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.CACTUS_CHESTPLATE.get())
                .pattern("TDT")
                .pattern("TCT")
                .pattern("TTT")
                .define('T', ModBlocks.CACTUS_THORN.get())
                .define('C', Items.LEATHER_CHESTPLATE)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_THORN.get()), has(ModBlocks.CACTUS_THORN.get()))
                .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.CACTUS_LEGGINGS.get())
                .pattern("TDT")
                .pattern("TLT")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_THORN.get())
                .define('L', Items.LEATHER_LEGGINGS)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_THORN.get()), has(ModBlocks.CACTUS_THORN.get()))
                .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.CACTUS_BOOTS.get())
                .pattern("TDT")
                .pattern(" B ")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_THORN.get())
                .define('B', Items.LEATHER_BOOTS)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_THORN.get()), has(ModBlocks.CACTUS_THORN.get()))
                .save(output);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_MOSAIC.get(), 1)
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.CACTUS_SLAB.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_SLAB.get()), has(ModBlocks.CACTUS_SLAB.get()))
                .save(output);

        shaped(RecipeCategory.MISC, ModBlocks.TWIG_LADDER.get(), 3)
                .pattern("A A")
                .pattern("BBB")
                .pattern("A A")
                .define('A', ModItems.LINEN_THREAD.get())
                .define('B', ModItems.TWIG.get())
                .unlockedBy("has_linen_thread", has(ModItems.LINEN_THREAD.get()))
                .unlockedBy("has_twig", has(ModItems.TWIG.get()))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.LINEN_CARPET.get(), 3)
                .pattern("CC")
                .define('C', ModBlocks.LINEN_BLOCK.get())
                .unlockedBy("has_linen_block", has(ModBlocks.LINEN_BLOCK.get()))
                .save(output);

        shaped(RecipeCategory.DECORATIONS, Items.PAINTING)
                .pattern("SSS")
                .pattern("SLS")
                .pattern("SSS")
                .define('S', Items.STICK)
                .define('L', ModBlocks.LINEN_BLOCK.get())
                .unlockedBy("has_linen_block", has(ModBlocks.LINEN_BLOCK.get()))
                .save(output, modLoc("painting_from_linen_block"));

        shaped(RecipeCategory.DECORATIONS, Items.WHITE_BANNER)
                .pattern("LLL")
                .pattern("LLL")
                .pattern(" S ")
                .define('L', ModBlocks.LINEN_BLOCK.get())
                .define('S', Items.STICK)
                .unlockedBy("has_linen_block", has(ModBlocks.LINEN_BLOCK.get()))
                .save(output, modLoc("white_banner_from_linen_block"));

        shaped(RecipeCategory.DECORATIONS, Items.WHITE_BED)
                .pattern("LLL")
                .pattern("PPP")
                .define('L', ModBlocks.LINEN_BLOCK.get())
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_linen_block", has(ModBlocks.LINEN_BLOCK.get()))
                .save(output, modLoc("white_bed_from_linen_block"));

        shapeless(RecipeCategory.MISC, ModItems.PINE_CONE.get(), 1)
                .requires(ModBlocks.PINE_LITTER.get())
                .unlockedBy("has_pine_litter", has(ModBlocks.PINE_LITTER.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModBlocks.PEBBLE_PATCH.get(), 1)
                .requires(ModItems.PEBBLES.get())
                .unlockedBy("has_pebbles", has(ModItems.PEBBLES.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE, 1)
                .requires(ModItems.FLAX_FLOWER.get())
                .unlockedBy("has_flax_flower", has(ModItems.FLAX_FLOWER.get()))
                .save(output, modLoc("light_blue_dye_from_flax_flower"));

        shapeless(RecipeCategory.MISC, ModItems.PINE_NUTS.get(), 2)
                .requires(ModItems.PINE_CONE.get())
                .unlockedBy("has_pine_cone", has(ModItems.PINE_CONE.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, Items.STICK, 2)
                .requires(ModItems.TWIG.get())
                .unlockedBy("has_twig", has(ModItems.TWIG.get()))
                .save(output, modLoc("stick_from_twig"));

        shapeless(RecipeCategory.MISC, ModItems.FOREST_SNACK.get(), 3)
                .requires(ModItems.TOASTED_PINE_NUTS.get())
                .requires(Items.PUMPKIN_SEEDS)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.HONEY_BOTTLE)
                .unlockedBy("has_toasted_pine_nuts", has(ModItems.TOASTED_PINE_NUTS.get()))
                .unlockedBy("has_pumpkin_seeds", has(Items.PUMPKIN_SEEDS))
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE))
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.SWEET_BERRY_MIX.get(), 3)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.GLOW_BERRIES)
                .requires(ModItems.CHERRIES.get())
                .unlockedBy("has_cherries", has(ModItems.CHERRIES.get()))
                .unlockedBy("has_glow_berries", has(Items.GLOW_BERRIES))
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.LINEN_THREAD.get(), 2)
                .requires(ModItems.FLAX_FIBER.get(), 4)
                .unlockedBy("has_flax_fiber", has(ModItems.FLAX_FIBER.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.LINEN_CLOTH.get(), 2)
                .requires(ModItems.LINEN_THREAD.get(), 4)
                .unlockedBy("has_linen_thread", has(ModItems.LINEN_THREAD.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModBlocks.LINEN_BLOCK.get(), 1)
                .requires(ModItems.LINEN_CLOTH.get(), 4)
                .unlockedBy("has_linen_cloth", has(ModItems.LINEN_CLOTH.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.PEBBLES.get(), 9)
                .requires(ModBlocks.PEBBLE_BLOCK.get(), 1)
                .unlockedBy("has_pebble_block", has(ModBlocks.PEBBLE_BLOCK.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModBlocks.PEBBLE_BLOCK.get(), 1)
                .requires(ModItems.PEBBLES.get(), 9)
                .unlockedBy("has_pebbles", has(ModItems.PEBBLES.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModBlocks.FLAX_BALE.get(), 1)
                .requires(ModItems.FLAX_FIBER.get(), 9)
                .unlockedBy("has_flax_fiber", has(ModItems.FLAX_FIBER.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.FLAX_FIBER.get(), 9)
                .requires(ModBlocks.FLAX_BALE.get(), 1)
                .unlockedBy("has_flax_bale", has(ModBlocks.FLAX_BALE.get()))
                .save(output);

        shapeless(RecipeCategory.MISC, Items.BROWN_DYE, 1)
                .requires(ModBlocks.CHERRY_PIT.get(), 1)
                .unlockedBy("has_cherry_pit", has(ModBlocks.CHERRY_PIT.get()))
                .save(output, modLoc("brown_dye_from_cherry_pit"));

        shapeless(RecipeCategory.MISC, Items.BROWN_DYE, 1)
                .requires(ModBlocks.APPLE_CORE.get(), 1)
                .unlockedBy("has_apple_core", has(ModBlocks.APPLE_CORE.get()))
                .save(output, modLoc("brown_dye_from_apple_core"));

        shapeless(RecipeCategory.FOOD, ModItems.CHERRY_JUICE.get(), 1)
                .requires(ModItems.CHERRIES.get(), 3)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_cherries", has(ModItems.CHERRIES.get()))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(output);

        shapeless(RecipeCategory.FOOD, ModItems.CACTUS_JUICE.get(), 1)
                .requires(ModItems.CACTUS_SLICE.get(), 2)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_cactus_slice", has(ModItems.CACTUS_SLICE.get()))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(output);

        shapeless(RecipeCategory.FOOD, ModItems.APPLE_JUICE.get(), 1)
                .requires(Items.APPLE, 2)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_apples", has(Items.APPLE))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(output);

        shapeless(RecipeCategory.MISC, Items.PINK_DYE, 1)
                .requires(ModBlocks.GIANT_CACTUS_BLOSSOM.get())
                .unlockedBy("has_giant_cactus_blossom", has(ModBlocks.GIANT_CACTUS_BLOSSOM.get()))
                .save(output, modLoc("pink_dye_from_giant_cactus_blossom"));

        foodSmelting(ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 100);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.CACTUS_CLUSTER.get()),
                        RecipeCategory.MISC,
                        Items.GREEN_DYE,
                        0.2f,
                        200
                )
                .unlockedBy("has_cactus_cluster", has(ModBlocks.CACTUS_CLUSTER.get()))
                .save(output, modLoc("green_dye_from_smelting_cactus_cluster"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.FRUITING_CHERRY_LEAVES.get()),
                        RecipeCategory.DECORATIONS,
                        Blocks.LEAF_LITTER,
                        0.1f,
                        200
                )
                .unlockedBy("has_fruiting_cherry_leaves", has(ModBlocks.FRUITING_CHERRY_LEAVES.get()))
                .save(output, modLoc("leaf_litter_from_smelting_fruiting_cherry_leaves"));

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.FRUITING_OAK_LEAVES.get()),
                        RecipeCategory.DECORATIONS,
                        Blocks.LEAF_LITTER,
                        0.1f,
                        200
                )
                .unlockedBy("has_fruiting_oak_leaves", has(ModBlocks.FRUITING_OAK_LEAVES.get()))
                .save(output, modLoc("leaf_litter_from_smelting_fruiting_oak_leaves"));

        foodSmoking(ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 50);

        foodCampfireCooking(ModItems.PINE_NUTS.get(), ModItems.TOASTED_PINE_NUTS.get(), 0.1f, 150);
    }

    protected void oreSmelting(@NotNull List<ItemLike> ingredients,
                               @NotNull RecipeCategory category,
                               @NotNull ItemLike result,
                               float experience,
                               int cookingTime,
                               @NotNull String group) {
        oreCooking(RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, ingredients, category, result,
                experience, cookingTime, group, "_from_smelting");
    }

    protected void oreBlasting(@NotNull List<ItemLike> ingredients,
                               @NotNull RecipeCategory category,
                               @NotNull ItemLike result,
                               float experience,
                               int cookingTime,
                               @NotNull String group) {
        oreCooking(RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, ingredients, category, result,
                experience, cookingTime, group, "_from_blasting");
    }

    protected void foodSmelting(ItemLike input, ItemLike outputItem, float exp, int time) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, outputItem, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(output, modLoc(getItemName(outputItem) + "_from_smelting"));
    }

    protected void foodSmoking(ItemLike input, ItemLike outputItem, float exp, int time) {
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, outputItem, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(output, modLoc(getItemName(outputItem) + "_from_smoking"));
    }

    protected void foodCampfireCooking(ItemLike input, ItemLike outputItem, float exp, int time) {
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, outputItem, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(output, modLoc(getItemName(outputItem) + "_from_campfire_cooking"));
    }

    protected <T extends AbstractCookingRecipe> void oreCooking(RecipeSerializer<T> cookingSerializer,
                                                                AbstractCookingRecipe.@NotNull Factory<T> factory,
                                                                List<ItemLike> ingredients,
                                                                @NotNull RecipeCategory category,
                                                                @NotNull ItemLike result,
                                                                float experience,
                                                                int cookingTime,
                                                                @NotNull String group,
                                                                String recipeName) {
        for (ItemLike itemLike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemLike), category, result, experience, cookingTime, cookingSerializer, factory)
                    .group(group)
                    .unlockedBy(getHasName(itemLike), has(itemLike))
                    .save(output, modLoc(getItemName(result) + recipeName + "_" + getItemName(itemLike)));
        }
    }

    private static @NotNull ResourceKey<Recipe<?>> modLoc(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path));
    }
}
