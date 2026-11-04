package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/** Hand-mill recipes: pigment extraction, powders, seed separation and aggregate crushing. */
final class ModGrindingRecipes {
    static void register(RecipeOutput output) {
        recipe(output, "salt_from_grinding", ModItems.RAW_SALT.get(), ModItems.SALT.get(), 1, 4);
        grinding(output, ModBlocks.CRYSTALLINE_SALT.get(), ModItems.RAW_SALT.get(), 4, 8);
        grinding(output, ModBlocks.SALT_SPIKE.get(), ModItems.RAW_SALT.get(), 1, 4);
        grinding(output, ModBlocks.SMOOTH_SALT.get(), ModItems.RAW_SALT.get(), 4, 8);
        grinding(output, ModBlocks.CUT_SALT.get(), ModItems.RAW_SALT.get(), 4, 8);
        grinding(output, ModBlocks.CHISELED_SALT.get(), ModItems.RAW_SALT.get(), 4, 8);

        // Every vanilla single-ingredient dye recipe, at twice its crafting yield.
        dye(output, Items.INK_SAC, Items.BLACK_DYE, 1);
        dye(output, Items.WITHER_ROSE, Items.BLACK_DYE, 1);
        dye(output, Items.LAPIS_LAZULI, Items.BLUE_DYE, 1);
        dye(output, Items.CORNFLOWER, Items.BLUE_DYE, 1);
        dye(output, Items.COCOA_BEANS, Items.BROWN_DYE, 1);
        dye(output, Items.PITCHER_PLANT, Items.CYAN_DYE, 2);
        dye(output, Items.BLUE_ORCHID, Items.LIGHT_BLUE_DYE, 1);
        dye(output, Items.AZURE_BLUET, Items.LIGHT_GRAY_DYE, 1);
        dye(output, Items.OXEYE_DAISY, Items.LIGHT_GRAY_DYE, 1);
        dye(output, Items.WHITE_TULIP, Items.LIGHT_GRAY_DYE, 1);
        dye(output, Items.ALLIUM, Items.MAGENTA_DYE, 1);
        dye(output, Items.LILAC, Items.MAGENTA_DYE, 2);
        dye(output, Items.ORANGE_TULIP, Items.ORANGE_DYE, 1);
        dye(output, Items.TORCHFLOWER, Items.ORANGE_DYE, 1);
        dye(output, Items.PEONY, Items.PINK_DYE, 2);
        dye(output, Items.PINK_PETALS, Items.PINK_DYE, 1);
        dye(output, Items.PINK_TULIP, Items.PINK_DYE, 1);
        dye(output, Items.BEETROOT, Items.RED_DYE, 1);
        dye(output, Items.POPPY, Items.RED_DYE, 1);
        dye(output, Items.ROSE_BUSH, Items.RED_DYE, 2);
        dye(output, Items.RED_TULIP, Items.RED_DYE, 1);
        dye(output, Items.BONE_MEAL, Items.WHITE_DYE, 1);
        dye(output, Items.LILY_OF_THE_VALLEY, Items.WHITE_DYE, 1);
        dye(output, Items.DANDELION, Items.YELLOW_DYE, 1);
        dye(output, Items.SUNFLOWER, Items.YELLOW_DYE, 2);

        // Existing Flora Expansion dye sources retain their colors and gain the same bonus.
        dye(output, ModItems.FLAX_FLOWER.get(), Items.LIGHT_BLUE_DYE, 1);
        dye(output, ModBlocks.CHERRY_PIT.get(), Items.BROWN_DYE, 1);
        dye(output, ModBlocks.APPLE_CORE.get(), Items.BROWN_DYE, 1);
        dye(output, ModBlocks.GIANT_CACTUS_SHOOT.get(), Items.PINK_DYE, 1);
        dye(output, ModItems.PRICKLY_PEAR.get(), Items.PINK_DYE, 1);

        // Additional plant and carbon pigments that can be extracted by grinding.
        grinding(output, ModItems.BLUEBERRIES.get(), Items.PURPLE_DYE, 2, 4);
        grinding(output, ModItems.STRAWBERRY.get(), Items.RED_DYE, 2, 4);
        grinding(output, ModItems.CHERRIES.get(), Items.RED_DYE, 2, 4);
        grinding(output, Items.SWEET_BERRIES, Items.RED_DYE, 2, 4);
        grinding(output, Items.CACTUS, Items.GREEN_DYE, 2, 4);
        grinding(output, ModBlocks.SMALL_CACTUS.get(), Items.GREEN_DYE, 2, 4);
        grinding(output, ModItems.CACTUS_SLICE.get(), Items.GREEN_DYE, 2, 4);
        grinding(output, Items.SEA_PICKLE, Items.LIME_DYE, 2, 4);
        grinding(output, Items.COAL, Items.BLACK_DYE, 2, 6);
        grinding(output, Items.CHARCOAL, Items.BLACK_DYE, 2, 6);

        grinding(output, Items.BONE, Items.BONE_MEAL, 6, 4);
        grinding(output, Items.BLAZE_ROD, Items.BLAZE_POWDER, 4, 8);
        grinding(output, Items.SUGAR_CANE, Items.SUGAR, 2, 4);
        grinding(output, ModItems.SPRUCE_CONE.get(), ModItems.SPRUCE_NUTS.get(), 4, 4);

        // Mineral processing conserves material: no doubling of reversible block recipes.
        grinding(output, Items.COBBLESTONE, Items.GRAVEL, 1, 8);
        grinding(output, Items.COBBLED_DEEPSLATE, Items.GRAVEL, 1, 8);
        grinding(output, Items.GRAVEL, Items.SAND, 1, 8);
        grinding(output, Items.SANDSTONE, Items.SAND, 4, 8);
        grinding(output, Items.RED_SANDSTONE, Items.RED_SAND, 4, 8);
    }

    private static void dye(RecipeOutput output, ItemLike input, ItemLike dye, int craftingYield) {
        grinding(output, input, dye, craftingYield * 2, 4);
    }

    private static void grinding(RecipeOutput output, ItemLike input, ItemLike result, int count, int turns) {
        String inputName = BuiltInRegistries.ITEM.getKey(input.asItem()).getPath();
        String resultName = BuiltInRegistries.ITEM.getKey(result.asItem()).getPath();
        recipe(output, "grinding/" + resultName + "_from_" + inputName, input, result, count, turns);
    }

    private static void recipe(RecipeOutput output, String path, ItemLike input, ItemLike result, int count, int turns) {
        output.accept(ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, path),
                new GrindingRecipe(false, Ingredient.of(input), new ItemStack(result.asItem(), count), turns), null);
    }
    private ModGrindingRecipes() {}
}
