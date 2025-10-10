package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
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


        //Items
        add(ModItems.PINE_CONE.get(), "Pine Cone");
    }
}
