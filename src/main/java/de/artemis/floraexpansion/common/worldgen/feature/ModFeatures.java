package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE, FloraExpansion.MODID);

    public static final Supplier<Feature<NoneFeatureConfiguration>> WILD_FLAX_FEATURE =
            FEATURES.register("wild_flax_feature",
                    () -> new WildFlaxFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<Feature<NoneFeatureConfiguration>> PEBBLE_CLUSTER_FEATURE =
            FEATURES.register("pebble_cluster_feature",
                    () -> new PebbleClusterFeature(NoneFeatureConfiguration.CODEC));

    public static final Supplier<Feature<NoneFeatureConfiguration>> GIANT_CACTUS_FEATURE =
            FEATURES.register("giant_cactus_feature",
                    () -> new GiantCactusFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}