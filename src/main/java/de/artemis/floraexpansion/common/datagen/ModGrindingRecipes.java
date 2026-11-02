package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
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
        dye(output, Items.INK_SAC, Items.DYE.black(), 1);
        dye(output, Items.WITHER_ROSE, Items.DYE.black(), 1);
        dye(output, Items.LAPIS_LAZULI, Items.DYE.blue(), 1);
        dye(output, Items.CORNFLOWER, Items.DYE.blue(), 1);
        dye(output, Items.COCOA_BEANS, Items.DYE.brown(), 1);
        dye(output, Items.PITCHER_PLANT, Items.DYE.cyan(), 2);
        dye(output, Items.CLOSED_EYEBLOSSOM, Items.DYE.gray(), 1);
        dye(output, Items.BLUE_ORCHID, Items.DYE.lightBlue(), 1);
        dye(output, Items.AZURE_BLUET, Items.DYE.lightGray(), 1);
        dye(output, Items.OXEYE_DAISY, Items.DYE.lightGray(), 1);
        dye(output, Items.WHITE_TULIP, Items.DYE.lightGray(), 1);
        dye(output, Items.ALLIUM, Items.DYE.magenta(), 1);
        dye(output, Items.LILAC, Items.DYE.magenta(), 2);
        dye(output, Items.OPEN_EYEBLOSSOM, Items.DYE.orange(), 1);
        dye(output, Items.ORANGE_TULIP, Items.DYE.orange(), 1);
        dye(output, Items.TORCHFLOWER, Items.DYE.orange(), 1);
        dye(output, Items.CACTUS_FLOWER, Items.DYE.pink(), 1);
        dye(output, Items.PEONY, Items.DYE.pink(), 2);
        dye(output, Items.PINK_PETALS, Items.DYE.pink(), 1);
        dye(output, Items.PINK_TULIP, Items.DYE.pink(), 1);
        dye(output, Items.BEETROOT, Items.DYE.red(), 1);
        dye(output, Items.POPPY, Items.DYE.red(), 1);
        dye(output, Items.ROSE_BUSH, Items.DYE.red(), 2);
        dye(output, Items.RED_TULIP, Items.DYE.red(), 1);
        dye(output, Items.BONE_MEAL, Items.DYE.white(), 1);
        dye(output, Items.LILY_OF_THE_VALLEY, Items.DYE.white(), 1);
        dye(output, Items.DANDELION, Items.DYE.yellow(), 1);
        dye(output, Items.GOLDEN_DANDELION, Items.DYE.yellow(), 1);
        dye(output, Items.SUNFLOWER, Items.DYE.yellow(), 2);
        dye(output, Items.WILDFLOWERS, Items.DYE.yellow(), 1);

        // Existing Flora Expansion dye sources retain their colors and gain the same bonus.
        dye(output, ModItems.FLAX_FLOWER.get(), Items.DYE.lightBlue(), 1);
        dye(output, ModBlocks.CHERRY_PIT.get(), Items.DYE.brown(), 1);
        dye(output, ModBlocks.APPLE_CORE.get(), Items.DYE.brown(), 1);
        dye(output, ModBlocks.GIANT_CACTUS_SHOOT.get(), Items.DYE.pink(), 1);
        dye(output, ModItems.PRICKLY_PEAR.get(), Items.DYE.pink(), 1);

        // Additional plant and carbon pigments that can be extracted by grinding.
        grinding(output, ModItems.BLUEBERRIES.get(), Items.DYE.purple(), 2, 4);
        grinding(output, ModItems.STRAWBERRY.get(), Items.DYE.red(), 2, 4);
        grinding(output, ModItems.CHERRIES.get(), Items.DYE.red(), 2, 4);
        grinding(output, Items.SWEET_BERRIES, Items.DYE.red(), 2, 4);
        grinding(output, Items.CACTUS, Items.DYE.green(), 2, 4);
        grinding(output, ModBlocks.SMALL_CACTUS.get(), Items.DYE.green(), 2, 4);
        grinding(output, ModItems.CACTUS_SLICE.get(), Items.DYE.green(), 2, 4);
        grinding(output, Items.SEA_PICKLE, Items.DYE.lime(), 2, 4);
        grinding(output, Items.COAL, Items.DYE.black(), 2, 6);
        grinding(output, Items.CHARCOAL, Items.DYE.black(), 2, 6);

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
        output.accept(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path)),
                new GrindingRecipe(new Recipe.CommonInfo(false), Ingredient.of(input), new ItemStackTemplate(result.asItem(), count), turns), null);
    }
    private ModGrindingRecipes() {}
}
