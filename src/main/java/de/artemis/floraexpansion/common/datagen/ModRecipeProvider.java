package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import de.artemis.floraexpansion.common.registry.ModTags;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModRecipeProvider extends RecipeProvider {
    private RecipeOutput output;
    public ModRecipeProvider(net.minecraft.data.PackOutput output, java.util.concurrent.CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookup) { super(output); }
    private static ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result, int count) { return ShapedRecipeBuilder.shaped(category, result, count); }
    private static ShapedRecipeBuilder shaped(RecipeCategory category, ItemLike result) { return shaped(category, result, 1); }
    private static ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result, int count) { return ShapelessRecipeBuilder.shapeless(category, result, count); }
    private static ShapelessRecipeBuilder shapeless(RecipeCategory category, ItemLike result) { return shapeless(category, result, 1); }
    private static Ingredient tag(net.minecraft.tags.TagKey<net.minecraft.world.item.Item> tag) { return Ingredient.of(tag); }
    protected void stonecutterResultFromBase(RecipeCategory category, ItemLike result, ItemLike base, int count) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), category, result, count)
                .unlockedBy(getHasName(base), has(base))
                .save(output, modLoc(getConversionRecipeName(result, base) + "_stonecutting"));
    }

    private void stonecutterResultFromBase(RecipeCategory category, ItemLike result, ItemLike base) { stonecutterResultFromBase(category, result, base, 1); }
    private void slab(RecipeCategory category, ItemLike result, ItemLike base) { RecipeProvider.slab(output, category, result, base); }
    private void wall(RecipeCategory category, ItemLike result, ItemLike base) { RecipeProvider.wall(output, category, result, base); }
    private void pressurePlate(ItemLike result, ItemLike base) { RecipeProvider.pressurePlate(output, result, base); }
    @Override
    protected void buildRecipes(@NotNull java.util.function.Consumer<FinishedRecipe> writer) {
        RecipeOutput recipeOutput = new RecipeOutput(writer);
        this.output = recipeOutput;
        calciteOreCooking(recipeOutput, ModBlocks.CALCITE_COAL_ORE.get(), Items.COAL, .1F);
        calciteOreCooking(recipeOutput, ModBlocks.CALCITE_IRON_ORE.get(), Items.IRON_INGOT, .7F);
        calciteOreCooking(recipeOutput, ModBlocks.CALCITE_COPPER_ORE.get(), Items.COPPER_INGOT, .7F);
        calciteOreCooking(recipeOutput, ModBlocks.CALCITE_GOLD_ORE.get(), Items.GOLD_INGOT, 1F);
        calciteOreCooking(recipeOutput, ModBlocks.CALCITE_REDSTONE_ORE.get(), Items.REDSTONE, .7F);
        calciteOreCooking(recipeOutput, ModBlocks.CALCITE_EMERALD_ORE.get(), Items.EMERALD, 1F);
        calciteOreCooking(recipeOutput, ModBlocks.CALCITE_LAPIS_ORE.get(), Items.LAPIS_LAZULI, .2F);
        calciteOreCooking(recipeOutput, ModBlocks.CALCITE_DIAMOND_ORE.get(), Items.DIAMOND, 1F);
        ModGrindingRecipes.register(recipeOutput);
        ModSaltingRecipes.register(recipeOutput, ModRecipeProvider::tag);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SALT_SPIKE.get(), ModBlocks.CRYSTALLINE_SALT.get(), 4);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYSTALLINE_SALT.get())
                .pattern("SS").pattern("SS").define('S', ModBlocks.SALT_SPIKE.get())
                .unlockedBy("has_salt_spike", has(ModBlocks.SALT_SPIKE.get()))
                .save(recipeOutput, modLoc("crystalline_salt_from_salt_spikes"));
        shaped(RecipeCategory.DECORATIONS, ModBlocks.SALTING_TUB.get())
                .pattern("SIS").pattern("S S").pattern("SSS")
                .define('S', tag(ItemTags.WOODEN_SLABS)).define('I', Items.IRON_NUGGET)
                .unlockedBy("has_salt", has(ModItems.SALT.get())).save(recipeOutput);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYSTALLINE_SALT.get())
                .pattern("SS").pattern("SS").define('S', ModItems.RAW_SALT.get())
                .unlockedBy("has_raw_salt", has(ModItems.RAW_SALT.get())).save(recipeOutput);
        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_SALT.get())
                .pattern("S").pattern("S").define('S', ModBlocks.CRYSTALLINE_SALT_SLAB.get())
                .unlockedBy("has_crystalline_salt_slab", has(ModBlocks.CRYSTALLINE_SALT_SLAB.get())).save(recipeOutput);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHISELED_SALT.get(), ModBlocks.CRYSTALLINE_SALT.get());
        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_SALT.get(), 4)
                .pattern("SS").pattern("SS").define('S', ModBlocks.CRYSTALLINE_SALT.get())
                .unlockedBy("has_crystalline_salt", has(ModBlocks.CRYSTALLINE_SALT.get())).save(recipeOutput);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CUT_SALT.get(), ModBlocks.CRYSTALLINE_SALT.get());
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModBlocks.CRYSTALLINE_SALT.get()),
                        RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.SMOOTH_SALT.get(), 0.1F, 200)
                .unlockedBy("has_crystalline_salt", has(ModBlocks.CRYSTALLINE_SALT.get()))
                .save(recipeOutput, modLoc("smooth_salt_from_smelting"));
        stairBuilder(ModBlocks.CRYSTALLINE_SALT_STAIRS.get(), Ingredient.of(ModBlocks.CRYSTALLINE_SALT.get()))
                .unlockedBy(getHasName(ModBlocks.CRYSTALLINE_SALT.get()), has(ModBlocks.CRYSTALLINE_SALT.get())).save(recipeOutput);
        slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYSTALLINE_SALT_SLAB.get(), ModBlocks.CRYSTALLINE_SALT.get());
        wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYSTALLINE_SALT_WALL.get(), ModBlocks.CRYSTALLINE_SALT.get());
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYSTALLINE_SALT_STAIRS.get(), ModBlocks.CRYSTALLINE_SALT.get());
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYSTALLINE_SALT_SLAB.get(), ModBlocks.CRYSTALLINE_SALT.get(), 2);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRYSTALLINE_SALT_WALL.get(), ModBlocks.CRYSTALLINE_SALT.get());
        shaped(RecipeCategory.DECORATIONS, ModBlocks.HAND_CRANK.get())
                .pattern(" SS").pattern(" I ").pattern(" S ")
                .define('S', Items.STICK).define('I', Items.IRON_NUGGET)
                .unlockedBy("has_hand_mill", has(ModBlocks.HAND_MILL.get())).save(recipeOutput);
        shaped(RecipeCategory.DECORATIONS, ModBlocks.HAND_MILL.get())
                .pattern("PPP").pattern("CCC")
                .define('P', Items.STONE_SLAB).define('C', Items.COBBLESTONE)
                .unlockedBy("has_raw_salt", has(ModItems.RAW_SALT.get())).save(recipeOutput);
        foodCampfireCooking(recipeOutput, ModItems.SALTED_RAW_MEAT.get(), ModItems.CURED_MEAT.get(), 0.1F, 1200);
        foodCampfireCooking(recipeOutput, ModItems.SALTED_RAW_FISH.get(), ModItems.CURED_FISH.get(), 0.1F, 1200);
        foodSmelting(recipeOutput, ModItems.SALTED_SPRUCE_NUTS.get(), ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(), 0.1F, 100);
        foodSmoking(recipeOutput, ModItems.SALTED_SPRUCE_NUTS.get(), ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(), 0.1F, 50);
        foodCampfireCooking(recipeOutput, ModItems.SALTED_SPRUCE_NUTS.get(), ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(), 0.1F, 150);

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

        shaped(RecipeCategory.DECORATIONS, ModItems.CACTUS_HANGING_SIGN.get(), 6).group("hanging_sign")
                .define('#', ModBlocks.STRIPPED_CACTUS_LOG.get()).define('X', Items.CHAIN)
                .pattern("X X").pattern("###").pattern("###")
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
                .requires(ModBlocks.GIANT_CACTUS_STEM.get(), 1)
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
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_SPINE.get()), has(ModBlocks.CACTUS_SPINE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.COMBAT, ModItems.SPIKED_LEATHER_CHESTPLATE.get())
                .pattern("TDT")
                .pattern("TCT")
                .pattern("TTT")
                .define('T', ModBlocks.CACTUS_SPINE.get())
                .define('C', Items.LEATHER_CHESTPLATE)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_SPINE.get()), has(ModBlocks.CACTUS_SPINE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.COMBAT, ModItems.SPIKED_LEATHER_LEGGINGS.get())
                .pattern("TDT")
                .pattern("TLT")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_SPINE.get())
                .define('L', Items.LEATHER_LEGGINGS)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_SPINE.get()), has(ModBlocks.CACTUS_SPINE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.COMBAT, ModItems.SPIKED_LEATHER_BOOTS.get())
                .pattern("TDT")
                .pattern(" B ")
                .pattern("T T")
                .define('T', ModBlocks.CACTUS_SPINE.get())
                .define('B', Items.LEATHER_BOOTS)
                .define('D', Items.GREEN_DYE)
                .unlockedBy(getHasName(ModBlocks.CACTUS_SPINE.get()), has(ModBlocks.CACTUS_SPINE.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CACTUS_MOSAIC.get(), 1)
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.CACTUS_SLAB.get())
                .unlockedBy(getHasName(ModBlocks.CACTUS_SLAB.get()), has(ModBlocks.CACTUS_SLAB.get()))
                .save(recipeOutput);

        shaped(RecipeCategory.MISC, ModBlocks.TWIG_LADDER.get(), 3)
                .pattern("A A")
                .pattern("BBB")
                .pattern("A A")
                .define('A', ModTags.Items.LINEN_THREADS)
                .define('B', ModItems.TWIG.get())
                .unlockedBy("has_linen_thread", has(ModTags.Items.LINEN_THREADS))
                .unlockedBy("has_twig", has(ModItems.TWIG.get()))
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

        shaped(RecipeCategory.DECORATIONS, ModBlocks.LINEN_CARPET.get(), 3)
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

        shaped(RecipeCategory.DECORATIONS, Items.WHITE_BANNER)
                .pattern("LLL")
                .pattern("LLL")
                .pattern(" S ")
                .define('L', ModBlocks.LINEN_BALE.get())
                .define('S', Items.STICK)
                .unlockedBy("has_linen_bale", has(ModBlocks.LINEN_BALE.get()))
                .save(recipeOutput, modLoc("white_banner_from_linen_bale"));

        shaped(RecipeCategory.DECORATIONS, Items.WHITE_BED)
                .pattern("LLL")
                .pattern("PPP")
                .define('L', ModBlocks.LINEN_BALE.get())
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_linen_bale", has(ModBlocks.LINEN_BALE.get()))
                .save(recipeOutput, modLoc("white_bed_from_linen_bale"));

        shapeless(RecipeCategory.MISC, ModItems.SPRUCE_CONE.get(), 1)
                .requires(ModBlocks.NEEDLE_LITTER.get())
                .unlockedBy("has_needle_litter", has(ModBlocks.NEEDLE_LITTER.get()))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModItems.PEBBLES.get(), 1)
                .requires(ModBlocks.SCATTERED_PEBBLES.get())
                .unlockedBy("has_scattered_pebbles", has(ModBlocks.SCATTERED_PEBBLES.get()))
                .save(recipeOutput, modLoc("pebbles_from_scattered_pebbles"));

        shapeless(RecipeCategory.MISC, Items.LIGHT_BLUE_DYE, 1)
                .requires(ModItems.FLAX_FLOWER.get())
                .unlockedBy("has_flax_flower", has(ModItems.FLAX_FLOWER.get()))
                .save(recipeOutput, modLoc("light_blue_dye_from_flax_flower"));

        shapeless(RecipeCategory.MISC, ModItems.SPRUCE_NUTS.get(), 2)
                .requires(ModItems.SPRUCE_CONE.get())
                .unlockedBy("has_spruce_cone", has(ModItems.SPRUCE_CONE.get()))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, Items.STICK, 2)
                .requires(ModItems.TWIG.get())
                .unlockedBy("has_twig", has(ModItems.TWIG.get()))
                .save(recipeOutput, modLoc("stick_from_twig"));

        shapeless(RecipeCategory.MISC, ModItems.TRAIL_MIX.get(), 3)
                .requires(tag(ModTags.Items.TOASTED_SPRUCE_NUTS))
                .requires(Items.PUMPKIN_SEEDS)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.HONEY_BOTTLE)
                .unlockedBy("has_toasted_spruce_nuts", has(ModItems.TOASTED_SPRUCE_NUTS.get()))
                .unlockedBy("has_pumpkin_seeds", has(Items.PUMPKIN_SEEDS))
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE))
                .save(recipeOutput);

        shapeless(RecipeCategory.FOOD, ModItems.MIXED_BERRIES.get(), 3)
                .requires(ModItems.BLUEBERRIES.get())
                .requires(ModItems.STRAWBERRY.get())
                .requires(tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:foods/berries"))))
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .unlockedBy("has_strawberry", has(ModItems.STRAWBERRY.get()))
                .unlockedBy("has_berries", has(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, net.minecraft.resources.ResourceLocation.parse("forge:foods/berries"))))
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

        shapeless(RecipeCategory.MISC, ModItems.LINEN_THREAD.get(), 2)
                .requires(tag(ModTags.Items.LINEN_FIBERS), 4)
                .unlockedBy("has_flax_fiber", has(ModTags.Items.LINEN_FIBERS))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModItems.LINEN_FABRIC.get(), 2)
                .requires(tag(ModTags.Items.LINEN_THREADS), 4)
                .unlockedBy("has_linen_thread", has(ModTags.Items.LINEN_THREADS))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModBlocks.LINEN_BALE.get(), 1)
                .requires(ModItems.LINEN_FABRIC.get(), 4)
                .unlockedBy("has_linen_fabric", has(ModItems.LINEN_FABRIC.get()))
                .save(recipeOutput);



        shapeless(RecipeCategory.MISC, ModBlocks.FLAX_BALE.get(), 1)
                .requires(tag(ModTags.Items.LINEN_FIBERS), 9)
                .unlockedBy("has_flax_fiber", has(ModTags.Items.LINEN_FIBERS))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, ModItems.FLAX_FIBER.get(), 9)
                .requires(ModBlocks.FLAX_BALE.get(), 1)
                .unlockedBy("has_flax_bale", has(ModBlocks.FLAX_BALE.get()))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, Items.BROWN_DYE, 1)
                .requires(ModBlocks.CHERRY_PIT.get(), 1)
                .unlockedBy("has_cherry_pit", has(ModBlocks.CHERRY_PIT.get()))
                .save(recipeOutput, modLoc("brown_dye_from_cherry_pit"));

        shapeless(RecipeCategory.MISC, Items.BROWN_DYE, 1)
                .requires(ModBlocks.APPLE_CORE.get(), 1)
                .unlockedBy("has_apple_core", has(ModBlocks.APPLE_CORE.get()))
                .save(recipeOutput, modLoc("brown_dye_from_apple_core"));

        shapeless(RecipeCategory.FOOD, ModItems.CHERRY_JUICE.get(), 1)
                .requires(ModItems.CHERRIES.get(), 3)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_cherries", has(ModItems.CHERRIES.get()))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        shapeless(RecipeCategory.FOOD, ModItems.CACTUS_JUICE.get(), 1)
                .requires(ModItems.CACTUS_SLICE.get(), 2)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_cactus_slice", has(ModItems.CACTUS_SLICE.get()))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        shapeless(RecipeCategory.FOOD, ModItems.BLUEBERRY_JUICE.get(), 1)
                .requires(ModItems.BLUEBERRIES.get(), 3)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_blueberries", has(ModItems.BLUEBERRIES.get()))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        jam(ModItems.BLUEBERRY_JAM.get(), ModItems.BLUEBERRIES.get());
        jam(ModItems.STRAWBERRY_JAM.get(), ModItems.STRAWBERRY.get());
        shapeless(RecipeCategory.FOOD, ModItems.STRAWBERRY_JUICE.get(), 1)
                .requires(ModItems.STRAWBERRY.get(), 3).requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_strawberry", has(ModItems.STRAWBERRY.get()))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE)).save(recipeOutput);
        jam(ModItems.CHERRY_JAM.get(), ModItems.CHERRIES.get());
        jam(ModItems.APPLE_JAM.get(), Items.APPLE);

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

        shapeless(RecipeCategory.FOOD, ModItems.APPLE_JUICE.get(), 1)
                .requires(Items.APPLE, 2)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_apples", has(Items.APPLE))
                .unlockedBy("has_glass_bottle", has(Items.GLASS_BOTTLE))
                .save(recipeOutput);

        shapeless(RecipeCategory.MISC, Items.PINK_DYE, 1)
                .requires(ModBlocks.GIANT_CACTUS_SHOOT.get())
                .unlockedBy("has_giant_cactus_shoot", has(ModBlocks.GIANT_CACTUS_SHOOT.get()))
                .save(recipeOutput, modLoc("pink_dye_from_giant_cactus_shoot"));

        shapeless(RecipeCategory.MISC, Items.PINK_DYE, 1)
                .requires(ModItems.PRICKLY_PEAR.get())
                .unlockedBy("has_prickly_pear", has(ModItems.PRICKLY_PEAR.get()))
                .save(recipeOutput, modLoc("pink_dye_from_prickly_pear"));

        foodSmelting(recipeOutput, ModItems.SPRUCE_NUTS.get(), ModItems.TOASTED_SPRUCE_NUTS.get(), 0.1f, 100);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.SMALL_CACTUS.get()),
                        RecipeCategory.MISC,
                        Items.GREEN_DYE,
                        0.2f,
                        200
                )
                .unlockedBy("has_small_cactus", has(ModBlocks.SMALL_CACTUS.get()))
                .save(recipeOutput, modLoc("green_dye_from_smelting_small_cactus"));

        foodSmoking(recipeOutput, ModItems.SPRUCE_NUTS.get(), ModItems.TOASTED_SPRUCE_NUTS.get(), 0.1f, 50);

        foodCampfireCooking(recipeOutput, ModItems.SPRUCE_NUTS.get(), ModItems.TOASTED_SPRUCE_NUTS.get(), 0.1f, 150);
    }

    private void calciteOreCooking(RecipeOutput output, ItemLike ore, ItemLike result, float experience) {
        oreSmelting(output, List.of(ore), RecipeCategory.MISC, result, experience, 200, getItemName(result));
        oreBlasting(output, List.of(ore), RecipeCategory.MISC, result, experience, 100, getItemName(result));
    }

    private static void oreSmelting(@NotNull RecipeOutput recipeOutput, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group) {
        oreCooking(recipeOutput, ingredients, category, result,
                experience, cookingTime, group, "_from_smelting");
    }

    private static void oreBlasting(@NotNull RecipeOutput recipeOutput, List<ItemLike> ingredients, @NotNull RecipeCategory category, @NotNull ItemLike result, float experience, int cookingTime, @NotNull String group) {
        oreCooking(recipeOutput, ingredients, category, result,
                experience, cookingTime, group, "_from_blasting");
    }

    private void foodSmelting(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_smelting");
    }

    private void foodSmoking(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_smoking");
    }

    private void foodCampfireCooking(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float exp, int time) {
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, output, exp, time)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(output) + "_from_campfire_cooking");
    }

    private static void oreCooking(RecipeOutput recipeOutput, List<ItemLike> ingredients, RecipeCategory category, ItemLike result, float experience, int cookingTime, String group, String recipeName) {
        for(ItemLike input:ingredients) (recipeName.contains("blasting") ? SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), category, result, experience, cookingTime) : SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), category, result, experience, cookingTime))
            .group(group).unlockedBy(getHasName(input), has(input)).save(recipeOutput, FloraExpansion.MODID + ":" + getItemName(result) + recipeName + "_" + getItemName(input));
    }

    private void jam(ItemLike result, ItemLike fruit) {
        shapeless(RecipeCategory.FOOD, result, 1).requires(fruit, 4)
                .requires(Items.SUGAR).requires(ModItems.GLASS_JAR.get())
                .unlockedBy(getHasName(fruit), has(fruit))
                .unlockedBy("has_glass_jar", has(ModItems.GLASS_JAR.get())).save(output);
    }

    private static @NotNull ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, path);
    }
}
