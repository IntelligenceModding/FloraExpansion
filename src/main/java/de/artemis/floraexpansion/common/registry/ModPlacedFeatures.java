package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> NEEDLE_LITTER_PLACED_KEY = registerKey("needle_litter_placed");
    public static final ResourceKey<PlacedFeature> BLUEBERRY_BUSH_PLACED_KEY = registerKey("blueberry_bush_placed");
    public static final ResourceKey<PlacedFeature> WILD_FLAX_PLACED_KEY = registerKey("wild_flax_placed");
    public static final ResourceKey<PlacedFeature> WILD_STRAWBERRY_PLACED_KEY = registerKey("wild_strawberry_placed");
    public static final ResourceKey<PlacedFeature> PILE_FLAX_PLACED_KEY = registerKey("pile_flax");
    public static final ResourceKey<PlacedFeature> PEBBLE_CLUSTER_PLACED_KEY = registerKey("pebble_cluster_placed");
    public static final ResourceKey<PlacedFeature> ORCHARD_CHERRY_TREE_PLACED_KEY = registerKey("orchard_cherry_tree_placed");
    public static final ResourceKey<PlacedFeature> APPLE_SPARSE_PLACED_KEY = registerKey("apple_sparse_placed");
    public static final ResourceKey<PlacedFeature> APPLE_FOREST_PLACED_KEY = registerKey("apple_forest_placed");
    public static final ResourceKey<PlacedFeature> FANCY_APPLE_RARE_PLACED_KEY = registerKey("fancy_apple_rare_placed");
    public static final ResourceKey<PlacedFeature> FANCY_APPLE_JUNGLE_PLACED_KEY = registerKey("fancy_apple_jungle_placed");
    public static final ResourceKey<PlacedFeature> APPLE_DARK_FOREST_PLACED_KEY = registerKey("apple_dark_forest_placed");
    public static final ResourceKey<PlacedFeature> GIANT_CACTUS_PLACED_KEY = registerKey("giant_cactus_placed");
    public static final ResourceKey<PlacedFeature> FALLEN_GIANT_CACTUS_PLACED_KEY = registerKey("fallen_giant_cactus_placed");
    public static final ResourceKey<PlacedFeature> SMALL_CACTUS_PLACED_KEY = registerKey("small_cactus_placed");
    public static final ResourceKey<PlacedFeature> PRICKLY_PEAR_CACTUS_PLACED_KEY = registerKey("prickly_pear_cactus_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.FEATURE);

        register(context, PRICKLY_PEAR_CACTUS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.PRICKLY_PEAR_CACTUS_KEY),
                surfacePatch(5, PlacementUtils.HEIGHTMAP_WORLD_SURFACE));

        register(context, SMALL_CACTUS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SMALL_CACTUS_KEY),
                surfacePatch(4, PlacementUtils.HEIGHTMAP_WORLD_SURFACE));

        register(context, FALLEN_GIANT_CACTUS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.FALLEN_GIANT_CACTUS_KEY),
                surfacePatch(14, PlacementUtils.HEIGHTMAP_WORLD_SURFACE));

        register(context, PEBBLE_CLUSTER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.PEBBLE_CLUSTER_KEY),
                surfacePatch(5, PlacementUtils.HEIGHTMAP_TOP_SOLID));

        register(context, NEEDLE_LITTER_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.NEEDLE_LITTER_KEY),
                surfacePatch(4, PlacementUtils.HEIGHTMAP_TOP_SOLID));

        register(context, BLUEBERRY_BUSH_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BLUEBERRY_BUSH_KEY),
                surfacePatch(3, PlacementUtils.HEIGHTMAP_TOP_SOLID));

        register(context, WILD_FLAX_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WILD_FLAX_KEY),
                surfacePatch(6, PlacementUtils.HEIGHTMAP_TOP_SOLID));

        register(context, WILD_STRAWBERRY_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WILD_STRAWBERRY_KEY),
                surfacePatch(6, PlacementUtils.HEIGHTMAP_TOP_SOLID));

        register(context, PILE_FLAX_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.PILE_FLAX_KEY),
                List.of());

        register(context, ORCHARD_CHERRY_TREE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModTreeConfiguredFeatures.ORCHARD_CHERRY_TREE_KEY),
                List.of(
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        PlacementUtils.filteredByBlockSurvival(Blocks.CHERRY_SAPLING),
                        BiomeFilter.biome()
                ));

        register(context, APPLE_SPARSE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModTreeConfiguredFeatures.APPLE_TREE_KEY),
                appleTreePlacement(18));

        register(context, APPLE_FOREST_PLACED_KEY,
                configuredFeatures.getOrThrow(ModTreeConfiguredFeatures.APPLE_TREE_KEY),
                appleTreePlacement(8));

        register(context, FANCY_APPLE_RARE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModTreeConfiguredFeatures.FANCY_APPLE_TREE_KEY),
                appleTreePlacement(30));

        register(context, FANCY_APPLE_JUNGLE_PLACED_KEY,
                configuredFeatures.getOrThrow(ModTreeConfiguredFeatures.FANCY_APPLE_TREE_KEY),
                appleTreePlacement(22));

        register(context, APPLE_DARK_FOREST_PLACED_KEY,
                configuredFeatures.getOrThrow(ModTreeConfiguredFeatures.APPLE_TREE_KEY),
                appleTreePlacement(24));

        register(context, GIANT_CACTUS_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.GIANT_CACTUS_KEY),
                surfacePatch(8, PlacementUtils.HEIGHTMAP_WORLD_SURFACE));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(
                Registries.PLACED_FEATURE,
                Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name)
        );
    }

    private static void register(BootstrapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<Feature> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static List<PlacementModifier> surfacePatch(int rarity, PlacementModifier heightmap) {
        return List.of(RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), heightmap, BiomeFilter.biome());
    }

    private static List<PlacementModifier> appleTreePlacement(int rarity) {
        return List.of(RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP_WORLD_SURFACE, PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING),
                BiomeFilter.biome());
    }
}
