package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, FloraExpansion.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.PINE_CONE.get());
        basicItem(ModItems.PINE_NUTS.get());
        basicItem(ModItems.TOASTED_PINE_NUTS.get());
        basicItem(ModItems.TWIG.get());
        basicItem(ModItems.FOREST_SNACK.get());
        basicItem(ModItems.FLAX_SEED.get());
        basicItem(ModItems.FLAX_FLOWER.get());
        basicItem(ModItems.FLAX_FIBER.get());
        basicItem(ModItems.LINEN_THREAD.get());
        basicItem(ModItems.LINEN_CLOTH.get());
        basicItem(ModItems.PEBBLES.get());
        basicItem(ModItems.CHERRIES.get());
        basicItem(ModItems.SWEET_BERRY_MIX.get());
        basicItem(ModItems.CHERRY_JUICE.get());

        basicItem(ModBlocks.APPLE_CORE.get().asItem());
    }
}