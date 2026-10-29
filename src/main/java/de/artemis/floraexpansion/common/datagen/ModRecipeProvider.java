package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import de.artemis.floraexpansion.common.registry.ModTags;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(net.minecraft.data.worldgen.BootstrapContext<Recipe<?>> recipes,
                             net.minecraft.data.worldgen.BootstrapContext<net.minecraft.advancements.Advancement> advancements) {
        super(recipes, advancements, FloraExpansion.MODID);
    }

    @Override
    protected void buildRecipes() {
        RecipeOutput recipeOutput = output;

        stairBuilder(ModBlocks.CACTUS_STAIRS.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_SLAB.get(), ModBlocks.CACTUS_PLANKS.get());
        stairBuilder(ModBlocks.CACTUS_MOSAIC_STAIRS.get(), Ingredient.of(ModBlocks.CACTUS_MOSAIC.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_MOSAIC.get()), has(ModBlocks.CACTUS_MOSAIC.get())).save(recipeOutput);
        slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_MOSAIC_SLAB.get(), ModBlocks.CACTUS_MOSAIC.get());

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

        pressurePlate(ModBlocks.CACTUS_PRESSURE_PLATE.get(), ModBlocks.CACTUS_PLANKS.get());

        signBuilder(ModItems.CACTUS_SIGN.get(), Ingredient.of(ModBlocks.CACTUS_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        hangingSignBuilder(ModItems.CACTUS_HANGING_SIGN.get(), Ingredient.of(ModBlocks.STRIPPED_CACTUS_LOG.get()))
                .unlockedBy(getHasName(ModBlocks.STRIPPED_CACTUS_LOG.get()), has(ModBlocks.STRIPPED_CACTUS_LOG.get()))
                .save(recipeOutput);

        shapeless(RecipeCategory.TRANSPORTATION, ModItems.CACTUS_CHEST_BOAT.get())
                .requires(ModItems.CACTUS_BOAT.get())
                .requires(Blocks.CHEST)
                .unlockedBy(getHasName(ModItems.CACTUS_BOAT.get()), has(ModItems.CACTUS_BOAT.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_WOOD.get(), 3)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.CACTUS_LOG.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_LOG.get()), has(ModBlocks.CACTUS_LOG.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STRIPPED_CACTUS_WOOD.get(), 3)
                .pattern("##")
                .pattern("##")
                .define('#', ModBlocks.STRIPPED_CACTUS_LOG.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_CACTUS_LOG.get()), has(ModBlocks.STRIPPED_CACTUS_LOG.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.TRANSPORTATION, ModItems.CACTUS_BOAT.get())
                .pattern("# #")
                .pattern("###")
                .define('#', ModBlocks.CACTUS_PLANKS.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_PLANKS.get()), has(ModBlocks.CACTUS_PLANKS.get()))
                .save(recipeOutput);

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.CACTUS_LOG.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_LOG.get()),
                        has(ModBlocks.CACTUS_LOG.get()))
                .save(recipeOutput, modLoc("cactus_planks_from_cactus_log"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.STRIPPED_CACTUS_LOG.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_CACTUS_LOG.get()),
                        has(ModBlocks.STRIPPED_CACTUS_LOG.get()))
                .save(recipeOutput, modLoc("cactus_planks_from_stripped_cactus_log"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.CACTUS_WOOD.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_WOOD.get()),
                        has(ModBlocks.CACTUS_WOOD.get()))
                .save(recipeOutput, modLoc("cactus_planks_from_cactus_wood"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.STRIPPED_CACTUS_WOOD.get())
                .unlockedBy(getHasName(ModBlocks.STRIPPED_CACTUS_WOOD.get()),
                        has(ModBlocks.STRIPPED_CACTUS_WOOD.get()))
                .save(recipeOutput, modLoc("cactus_planks_from_stripped_cactus_wood"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_PLANKS.get(), 4)
                .requires(ModBlocks.GIANT_CACTUS_STEM.get(), 4)
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_STEM.get()),
                        has(ModBlocks.GIANT_CACTUS_STEM.get()))
                .save(recipeOutput, modLoc("cactus_planks_from_giant_cactus_stem"));

        shapeless(RecipeCategory.BUILDING_BLOCKS, ModItems.CACTUS_SLICE.get(), 2)
                .requires(ModBlocks.GIANT_CACTUS_STEM, 1)
                .unlockedBy(getHasName(ModBlocks.GIANT_CACTUS_STEM.get()),
                        has(ModBlocks.GIANT_CACTUS_STEM.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.BUILDING_BLOCKS, Items.STICK, 4)
                .pattern("#")
                .pattern("#")
                .define('#', ModBlocks.GIANT_CACTUS_STEM.get())
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(recipeOutput, modLoc("stick_from_giant_cactus_stem"));

        shaped(RecipeCategory.COMBAT, ModItems.SPIKED_LEATHER_HELMET.get())
                .pattern("TDT")
                .pattern(" H ")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_SPINE.get())
                .define('H', Items.LEATHER_HELMET)
                .define('D', Items.DYE.green())
                .unlockedBy(getHasName(ModBlocks.CACTUS_SPINE.get()), has(ModBlocks.CACTUS_SPINE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.COMBAT, ModItems.SPIKED_LEATHER_CHESTPLATE.get())
                .pattern("TDT")
                .pattern("TCT")
                .pattern("TTT")
                .define('T', ModBlocks.CACTUS_SPINE.get())
                .define('C', Items.LEATHER_CHESTPLATE)
                .define('D', Items.DYE.green())
                .unlockedBy(getHasName(ModBlocks.CACTUS_SPINE.get()), has(ModBlocks.CACTUS_SPINE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.COMBAT, ModItems.SPIKED_LEATHER_LEGGINGS.get())
                .pattern("TDT")
                .pattern("TLT")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_SPINE.get())
                .define('L', Items.LEATHER_LEGGINGS)
                .define('D', Items.DYE.green())
                .unlockedBy(getHasName(ModBlocks.CACTUS_SPINE.get()), has(ModBlocks.CACTUS_SPINE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.COMBAT, ModItems.SPIKED_LEATHER_BOOTS.get())
                .pattern("TDT")
                .pattern(" B ")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_SPINE.get())
                .define('B', Items.LEATHER_BOOTS)
                .define('D', Items.DYE.green())
                .unlockedBy(getHasName(ModBlocks.CACTUS_SPINE.get()), has(ModBlocks.CACTUS_SPINE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_MOSAIC.get(), 1)
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.CACTUS_SLAB.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_SLAB.get()), has(ModBlocks.CACTUS_SLAB.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.MISC, ModBlocks.TWIG_LADDER, 3)
                .pattern("A A")
                .pattern("BBB")
                .pattern("A A")
                .define('A', ModTags.Items.LINEN_THREADS)
                .define('B', ModItems.TWIG)
                .unlockedBy("has_linen_thread", has(ModTags.Items.LINEN_THREADS))
                .unlockedBy("has_twig", has(ModItems.TWIG))
                .save(recipeOutput);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.CRATE.get())
                .pattern("PPP")
                .pattern("PCP")
                .pattern("PPP")
                .define('P', ItemTags.PLANKS)
                .define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(recipeOutput);

        shaped(RecipeCategory.TOOLS, ModItems.BASKET.get())
                .pattern("T T")
                .pattern("TTT")
                .define('T', ModTags.Items.LINEN_THREADS)
                .unlockedBy("has_linen_thread", has(ModTags.Items.LINEN_THREADS))
                .save(recipeOutput);

        shaped(RecipeCategory.TOOLS, ModItems.WOODEN_BUCKET.get())
                .pattern("P P")
                .pattern(" P ")
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(recipeOutput);

        shaped(RecipeCategory.DECORATIONS, ModBlocks.LINEN_CARPET, 3)
                .pattern("CC")
                .define('C', ModBlocks.LINEN_BALE.get())
                .unlockedBy("has_linen_bale", has(ModBlocks.LINEN_BALE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.DECORATIONS, Items.PAINTING)
                .pattern("SSS")
                .pattern("SLS")
                .pattern("SSS")
                .define('S', Items.STICK)
                .define('L', ModBlocks.LINEN_BALE.get())
                .unlockedBy("has_linen_bale", has(ModBlocks.LINEN_BALE.get()))
                .save(recipeOutput, modLoc("painting_from_linen_bale"));

        shaped(RecipeCategory.DECORATIONS, Items.BANNER.white())
                .pattern("LLL")
                .pattern("LLL")
                .pattern(" S ")
                .define('L', ModBlocks.LINEN_BALE.get())
                .define('S', Items.STICK)
                .unlockedBy("has_linen_bale", has(ModBlocks.LINEN_BALE.get()))
                .save(recipeOutput, modLoc("white_banner_from_linen_bale"));

        shaped(RecipeCategory.DECORATIONS, Items.BED.white())
                .pattern("LLL")
                .pattern("PPP")
                .define('L', ModBlocks.LINEN_BALE.get())
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_linen_bale", has(ModBlocks.LINEN_BALE.get()))
                .save(recipeOutput, modLoc("white_bed_from_linen_bale"));

        shapeless(RecipeCategory.MISC, ModItems.SPRUCE_CONE, 1)
                .requires(ModBlocks.NEEDLE_LITTER)
                .unlockedBy("has_needle_litter", has(ModBlocks.NEEDLE_LITTER))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModBlocks.SCATTERED_PEBBLES, 1)
                .requires(ModItems.PEBBLES)
                .unlockedBy("has_pebbles", has(ModItems.PEBBLES))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, Items.DYE.lightBlue(), 1)
                .requires(ModItems.FLAX_FLOWER)
                .unlockedBy("has_flax_flower", has(ModItems.FLAX_FLOWER))
                .save(recipeOutput, modLoc("light_blue_dye_from_flax_flower"));

        shapeless(RecipeCategory.MISC, ModItems.SPRUCE_NUTS, 2)
                .requires(ModItems.SPRUCE_CONE)
                .unlockedBy("has_spruce_cone", has(ModItems.SPRUCE_CONE))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, Items.STICK, 2)
                .requires(ModItems.TWIG)
                .unlockedBy("has_twig", has(ModItems.TWIG))
                .save(recipeOutput, modLoc("stick_from_twig"));

        shapeless(RecipeCategory.MISC, ModItems.TRAIL_MIX, 3)
                .requires(ModItems.TOASTED_SPRUCE_NUTS)
                .requires(Items.PUMPKIN_SEEDS)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.HONEY_BOTTLE)
                .unlockedBy("has_toasted_spruce_nuts", has(ModItems.TOASTED_SPRUCE_NUTS))
                .unlockedBy("has_pumpkin_seeds", has(Items.PUMPKIN_SEEDS))
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE))
                .save(recipeOutput);

        shapeless(RecipeCategory.FOOD, ModItems.MIXED_BERRIES, 3)
                .requires(ModItems.BLUEBERRIES)
                .requires(ModItems.STRAWBERRY)
                .requires(tag(Tags.Items.FOODS_BERRY))
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES))
                .unlockedBy("has_strawberry", has(ModItems.STRAWBERRY))
                .unlockedBy("has_berries", has(Tags.Items.FOODS_BERRY))
                .save(recipeOutput);

        shaped(RecipeCategory.FOOD, ModItems.BLUEBERRY_COOKIE.get(), 8)
                .pattern("WBW")
                .define('W', Items.WHEAT)
                .define('B', ModItems.BLUEBERRIES.get())
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.FOOD, ModItems.BLUEBERRY_PIE.get())
                .pattern("BBB")
                .pattern("SES")
                .pattern("WWW")
                .define('B', ModItems.BLUEBERRIES.get())
                .define('S', Items.SUGAR)
                .define('E', Items.EGG)
                .define('W', Items.WHEAT)
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .save(recipeOutput);

        shapeless(RecipeCategory.FOOD, ModItems.SLICE_OF_BLUEBERRY_PIE.get(), 4)
                .requires(ModItems.BLUEBERRY_PIE.get())
                .unlockedBy("has_blueberry_pie", has(ModItems.BLUEBERRY_PIE.get()))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModItems.LINEN_THREAD, 2)
                .requires(tag(ModTags.Items.LINEN_FIBERS), 4)
                .unlockedBy("has_flax_fiber", has(ModTags.Items.LINEN_FIBERS))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModItems.LINEN_FABRIC, 2)
                .requires(tag(ModTags.Items.LINEN_THREADS), 4)
                .unlockedBy("has_linen_thread", has(ModTags.Items.LINEN_THREADS))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModBlocks.LINEN_BALE, 1)
                .requires(ModItems.LINEN_FABRIC, 4)
                .unlockedBy("has_linen_fabric", has(ModItems.LINEN_FABRIC))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModItems.PEBBLES, 9)
                .requires(ModBlocks.PACKED_PEBBLES, 1)
                .unlockedBy("has_packed_pebbles", has(ModBlocks.PACKED_PEBBLES))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModBlocks.PACKED_PEBBLES, 1)
                .requires(ModItems.PEBBLES, 9)
                .unlockedBy("has_pebbles", has(ModItems.PEBBLES))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModBlocks.FLAX_BALE, 1)
                .requires(tag(ModTags.Items.LINEN_FIBERS), 9)
                .unlockedBy("has_flax_fiber", has(ModTags.Items.LINEN_FIBERS))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModItems.FLAX_FIBER, 9)
                .requires(ModBlocks.FLAX_BALE, 1)
                .unlockedBy("has_flax_bale", has(ModBlocks.FLAX_BALE))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, Items.DYE.brown(), 1)
                .requires(ModBlocks.CHERRY_PIT, 1)
                .unlockedBy("has_cherry_pit", has(ModBlocks.CHERRY_PIT))
                .save(recipeOutput, modLoc("brown_dye_from_cherry_pit"));

        shapeless(RecipeCategory.MISC, Items.DYE.brown(), 1)
                .requires(ModBlocks.APPLE_CORE, 1)
                .unlockedBy("has_apple_core", has(ModBlocks.APPLE_CORE))
                .save(recipeOutput, modLoc("brown_dye_from_apple_core"));

        shapeless(RecipeCategory.FOOD, ModItems.CHERRY_JUICE, 1)
                .requires(ModItems.CHERRIES, 3)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_cherries", has(ModItems.CHERRIES))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        shapeless(RecipeCategory.FOOD, ModItems.CACTUS_JUICE, 1)
                .requires(ModItems.CACTUS_SLICE, 2)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_cactus_slice", has(ModItems.CACTUS_SLICE))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        shapeless(RecipeCategory.FOOD, ModItems.BLUEBERRY_JUICE, 1)
                .requires(ModItems.BLUEBERRIES.get(), 3)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        jam(ModItems.BLUEBERRY_JAM, ModItems.BLUEBERRIES);
        jam(ModItems.STRAWBERRY_JAM, ModItems.STRAWBERRY);
        shapeless(RecipeCategory.FOOD, ModItems.STRAWBERRY_JUICE, 1)
                .requires(ModItems.STRAWBERRY.get(), 3).requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_strawberry", has(ModItems.STRAWBERRY))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE)).save(recipeOutput);
        jam(ModItems.CHERRY_JAM, ModItems.CHERRIES);
        jam(ModItems.APPLE_JAM, Items.APPLE);

        shapeless(RecipeCategory.FOOD, ModBlocks.STRAWBERRY_CAKE.get())
                .requires(Items.CAKE)
                .requires(ModItems.STRAWBERRY.get(), 3)
                .unlockedBy("has_cake", has(Items.CAKE))
                .unlockedBy("has_strawberry", has(ModItems.STRAWBERRY.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.MISC, ModItems.GLASS_JAR.get(), 3)
                .pattern("G G")
                .pattern("G G")
                .pattern(" G ")
                .define('G', Items.GLASS)
                .unlockedBy("has_glass", has(Items.GLASS))
                .save(recipeOutput);

        shapeless(RecipeCategory.FOOD, ModItems.APPLE_JUICE, 1)
                .requires(Items.APPLE, 2)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_apples", has(Items.APPLE))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, Items.DYE.pink(), 1)
                .requires(ModBlocks.GIANT_CACTUS_SHOOT.get())
                .unlockedBy("has_giant_cactus_shoot", has(ModBlocks.GIANT_CACTUS_SHOOT.get()))
                .save(recipeOutput, modLoc("pink_dye_from_giant_cactus_shoot"));

        shapeless(RecipeCategory.MISC, Items.DYE.pink(), 1)
                .requires(ModItems.PRICKLY_PEAR.get())
                .unlockedBy("has_prickly_pear", has(ModItems.PRICKLY_PEAR.get()))
                .save(recipeOutput, modLoc("pink_dye_from_prickly_pear"));

        foodSmelting(recipeOutput, ModItems.SPRUCE_NUTS.get(), ModItems.TOASTED_SPRUCE_NUTS.get(), 0.1f, 100);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SMALL_CACTUS.get()),
                        RecipeCategory.MISC,
                        CookingBookCategory.MISC,
                        Items.DYE.green(),
                        0.2f,
                        200
                )
                .unlockedBy("has_small_cactus", has(ModBlocks.SMALL_CACTUS.get()))
                .save(recipeOutput, modLoc("green_dye_from_smelting_small_cactus"));

        foodSmoking(recipeOutput, ModItems.SPRUCE_NUTS.get(), ModItems.TOASTED_SPRUCE_NUTS.get(), 0.1f, 50);

        foodCampfireCooking(recipeOutput, ModItems.SPRUCE_NUTS.get(), ModItems.TOASTED_SPRUCE_NUTS.get(), 0.1f, 150);
    }

    protected void oreSmelting(@NotNull RecipeOutput recipeOutput, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group) {
        oreCooking(recipeOutput, SmeltingRecipe::new, ingredients, category, result,
                experience, cookingTime, group, "_from_smelting");
    }

    protected void oreBlasting(@NotNull RecipeOutput recipeOutput, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group) {
        oreCooking(recipeOutput, BlastingRecipe::new, ingredients, category, result,
                experience, cookingTime, group, "_from_blasting");
    }

    protected void foodSmelting(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, CookingBookCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_smelting");
    }

    protected void foodSmoking(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_smoking");
    }

    protected void foodCampfireCooking(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_campfire_cooking");
    }

    protected <T extends AbstractCookingRecipe> void oreCooking(@NotNull RecipeOutput recipeOutput, AbstractCookingRecipe.@NotNull Factory<T> factory, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group, String recipeName) {
        for(ItemLike itemlike : ingredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), category, CookingBookCategory.BLOCKS, result, experience, cookingTime, factory).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(result) + recipeName + "_" + getItemName(itemlike));
        }
    }

    private void jam(ItemLike result, ItemLike fruit) {
        shapeless(RecipeCategory.FOOD, result, 1).requires(fruit, 4)
                .requires(Items.SUGAR).requires(ModItems.GLASS_JAR.get())
                .unlockedBy(getHasName(fruit), has(fruit))
                .unlockedBy("has_glass_jar", has(ModItems.GLASS_JAR.get())).save(output);
    }

    private static @NotNull ResourceKey<Recipe<?>> modLoc(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path));
    }
}
