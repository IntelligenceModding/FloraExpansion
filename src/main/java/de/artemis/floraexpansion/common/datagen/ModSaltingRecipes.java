package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModTags;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

final class ModSaltingRecipes {
    static void register(RecipeOutput output, java.util.function.Function<net.minecraft.tags.TagKey<Item>, Ingredient> tags) {
        recipes(output, "salted_glasswort", Ingredient.of(de.artemis.floraexpansion.common.registry.ModBlocks.GLASSWORT.get()), ModItems.SALTED_GLASSWORT.get());
        recipes(output, "salted_raw_fish", tags.apply(ModTags.Items.CURABLE_FISH), ModItems.SALTED_RAW_FISH.get());
        recipes(output, "salted_raw_meat", tags.apply(ModTags.Items.CURABLE_MEAT), ModItems.SALTED_RAW_MEAT.get());
        recipes(output, "salted_spruce_nuts", Ingredient.of(ModItems.SPRUCE_NUTS.get()), ModItems.SALTED_SPRUCE_NUTS.get());
        recipes(output, "salted_toasted_spruce_nuts", tags.apply(ModTags.Items.TOASTED_SPRUCE_NUTS), ModItems.SALTED_TOASTED_SPRUCE_NUTS.get());
    }
    private static void recipes(RecipeOutput output, String name, Ingredient food, Item result) {
        var key = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "salting/" + name);
        output.accept(key, new SaltingRecipe(false, food,
                Ingredient.of(ModItems.SALT.get()), new ItemStack(result, SaltingRecipe.MAX_LAYERS),
                SaltingRecipe.DEFAULT_PROCESSING_TIME), null);
    }
    private ModSaltingRecipes() {}
}
