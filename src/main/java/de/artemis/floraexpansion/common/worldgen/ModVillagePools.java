package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.resources.ResourceLocation;

public class ModVillagePools {

    public static final ResourceLocation PLAINS_HOUSES =
            ResourceLocation.parse("minecraft:village/plains/houses");

    public static final ResourceLocation FLAX_FARM_SMALL =
            ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "village/plains/flax_farm_small");

    public static final ResourceLocation FLAX_FARM_LARGE =
            ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "village/plains/flax_farm_large");

    public static void register() {
    }
}