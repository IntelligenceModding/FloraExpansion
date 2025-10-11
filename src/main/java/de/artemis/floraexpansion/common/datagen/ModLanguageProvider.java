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

        add("floraexpansion.creative_tab", "Artemis' Flora Expansion");

        //Blocks

        add(ModBlocks.PINE_LITTER.get(), "Pine Litter");
        add(ModBlocks.LEAF_LITTER.get(), "Leaf Litter");

        //Items
        add(ModItems.PINE_CONE.get(), "Pine Cone");
        add(ModItems.PINE_NUTS.get(), "Pine Nuts");
        add(ModItems.TOASTED_PINE_NUTS.get(), "Toasted Pine Nuts");
        add(ModItems.TWIG.get(), "Twig");
        add(ModItems.FOREST_SNACK.get(), "Forest Snack");
    }
}
