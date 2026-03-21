package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> PINE_LITTER_PLACED_KEY = registerKey("pine_litter_placed");
    public static final ResourceKey<PlacedFeature> LEAF_LITTER_PLACED_KEY = registerKey("leaf_litter_placed");
    public static final ResourceKey<PlacedFeature> WILD_FLAX_PLACED_KEY = registerKey("wild_flax_placed");
    public static final ResourceKey<PlacedFeature> PEBBLE_CLUSTER_PLACED_KEY = registerKey("pebble_cluster_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, PEBBLE_CLUSTER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.PEBBLE_CLUSTER_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(5),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()
                ));

        register(context, PINE_LITTER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.PINE_LITTER_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()
                ));

        register(context, LEAF_LITTER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.LEAF_LITTER_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()
                ));

        register(context, WILD_FLAX_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WILD_FLAX_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(6),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        BiomeFilter.biome()
                ));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}