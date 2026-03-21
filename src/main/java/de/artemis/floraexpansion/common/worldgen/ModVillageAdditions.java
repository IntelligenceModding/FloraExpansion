package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.resources.ResourceLocation;

public class ModVillageAdditions {

    public static void register() {
        ResourceLocation plainsHouses = ResourceLocation.parse("minecraft:village/plains/houses");

        ResourceLocation flaxFarmSmall =
                ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "village/plains/flax_farm_small");
        ResourceLocation flaxFarmLarge =
                ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "village/plains/flax_farm_large");
    }
}