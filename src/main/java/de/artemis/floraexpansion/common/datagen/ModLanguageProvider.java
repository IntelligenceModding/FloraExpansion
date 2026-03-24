package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends net.neoforged.neoforge.common.data.LanguageProvider {
    public ModLanguageProvider(PackOutput output, String locale) {
        super(output, FloraExpansion.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        //Miscellaneous

        add("floraexpansion.creative_tab", "Flora Expansion");

        //Blocks
        add(ModBlocks.PINE_LITTER.get(), "Pine Litter");
        add(ModBlocks.LEAF_LITTER.get(), "Leaf Litter");
        add(ModBlocks.TWIG_LADDER.get(), "Twig Ladder");
        add(ModBlocks.FLAX_CROP.get(), "Flax Crop");
        add(ModBlocks.LINEN_CARPET.get(), "Linen Carpet");
        add(ModBlocks.PEBBLE_PATCH.get(), "Pebble Patch");
        add(ModBlocks.PEBBLE_BLOCK.get(), "Pebble Block");
        add(ModBlocks.LINEN_BLOCK.get(), "Linen Block");
        add(ModBlocks.FLAX_BALE.get(), "Flax Bale");
        add(ModBlocks.CHERRY_PIT.get(), "Cherry Pit");
        add(ModBlocks.FRUITING_CHERRY_LEAVES.get(), "Fruiting Cherry Leaves");
        add(ModBlocks.POTTED_CHERRY_PIT.get(), "Potted Cherry Pit");
        add(ModBlocks.APPLE_CORE.get(), "Apple Core");
        add(ModBlocks.POTTED_APPLE_CORE.get(), "Potted Apple Core");
        add(ModBlocks.FRUITING_OAK_LEAVES.get(), "Fruiting Oak Leaves");

        //Items
        add(ModItems.PINE_CONE.get(), "Pine Cone");
        add(ModItems.PINE_NUTS.get(), "Pine Nuts");
        add(ModItems.TOASTED_PINE_NUTS.get(), "Toasted Pine Nuts");
        add(ModItems.TWIG.get(), "Twig");
        add(ModItems.FOREST_SNACK.get(), "Forest Snack");
        add(ModItems.FLAX_SEED.get(), "Flax Seed");
        add(ModItems.FLAX_FIBER.get(), "Flax Fiber");
        add(ModItems.FLAX_FLOWER.get(), "Flax Flower");
        add(ModItems.LINEN_THREAD.get(), "Linen Thread");
        add(ModItems.LINEN_CLOTH.get(), "Linen Cloth");
        add(ModItems.PEBBLES.get(), "Pebbles");
        add(ModItems.CHERRIES.get(), "Cherries");
        add(ModItems.SWEET_BERRY_MIX.get(), "Sweet Berry Mix");
        add(ModItems.CHERRY_JUICE.get(), "Cherry Juice");
        add(ModItems.APPLE_JUICE.get(), "Apple Juice");
    }
}
