package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_NEEDLE_LITTER = registerKey("add_needle_litter");
    public static final ResourceKey<BiomeModifier> ADD_BLUEBERRY_BUSHES = registerKey("add_blueberry_bushes");
    public static final ResourceKey<BiomeModifier> ADD_WILD_FLAX = registerKey("add_wild_flax");
    public static final ResourceKey<BiomeModifier> ADD_WILD_STRAWBERRIES = registerKey("add_wild_strawberries");
    public static final ResourceKey<BiomeModifier> ADD_PEBBLE_CLUSTERS = registerKey("add_pebble_clusters");
    public static final ResourceKey<BiomeModifier> ADD_ORCHARD_CHERRY_TREES = registerKey("add_orchard_cherry_trees");
    public static final ResourceKey<BiomeModifier> ADD_APPLE_TREES_SPARSE = registerKey("add_apple_trees_sparse");
    public static final ResourceKey<BiomeModifier> ADD_APPLE_TREES_FOREST = registerKey("add_apple_trees_forest");
    public static final ResourceKey<BiomeModifier> ADD_FANCY_APPLE_TREES_RARE = registerKey("add_fancy_apple_trees_rare");
    public static final ResourceKey<BiomeModifier> ADD_FANCY_APPLE_TREES_JUNGLE = registerKey("add_fancy_apple_trees_jungle");
    public static final ResourceKey<BiomeModifier> ADD_APPLE_TREES_DARK_FOREST = registerKey("add_apple_trees_dark_forest");
    public static final ResourceKey<BiomeModifier> ADD_GIANT_CACTUS = registerKey("add_giant_cactus");
    public static final ResourceKey<BiomeModifier> ADD_FALLEN_GIANT_CACTUS = registerKey("add_fallen_giant_cactus");
    public static final ResourceKey<BiomeModifier> ADD_SMALL_CACTUS = registerKey("add_small_cactus");
    public static final ResourceKey<BiomeModifier> ADD_PRICKLY_PEAR_CACTUS = registerKey("add_prickly_pear_cactus");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_PRICKLY_PEAR_CACTUS, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_PRICKLY_PEAR_CACTUS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PRICKLY_PEAR_CACTUS_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_SMALL_CACTUS, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_SMALL_CACTUS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SMALL_CACTUS_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_FALLEN_GIANT_CACTUS, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_FALLEN_GIANT_CACTUS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FALLEN_GIANT_CACTUS_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_PEBBLE_CLUSTERS, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_PEBBLE_CLUSTERS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PEBBLE_CLUSTER_PLACED_KEY)),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS));

        context.register(ADD_NEEDLE_LITTER, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_NEEDLE_LITTER),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.NEEDLE_LITTER_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_BLUEBERRY_BUSHES, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_BLUEBERRY_BUSHES),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BLUEBERRY_BUSH_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_WILD_FLAX, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_WILD_FLAX),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WILD_FLAX_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_WILD_STRAWBERRIES, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_WILD_STRAWBERRIES),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WILD_STRAWBERRY_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_ORCHARD_CHERRY_TREES, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_ORCHARD_CHERRY_TREES),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ORCHARD_CHERRY_TREE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_APPLE_TREES_SPARSE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_APPLE_TREES_SPARSE),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.APPLE_SPARSE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_APPLE_TREES_FOREST, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_APPLE_TREES_FOREST),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.APPLE_FOREST_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_FANCY_APPLE_TREES_RARE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_FANCY_APPLE_TREES_RARE),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FANCY_APPLE_RARE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_FANCY_APPLE_TREES_JUNGLE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_FANCY_APPLE_TREES_JUNGLE),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.FANCY_APPLE_JUNGLE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_APPLE_TREES_DARK_FOREST, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_APPLE_TREES_DARK_FOREST),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.APPLE_DARK_FOREST_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ADD_GIANT_CACTUS, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.HAS_GIANT_CACTUS),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.GIANT_CACTUS_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name)
        );
    }
}
