package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_PINE_LITTER = registerKey("add_pine_litter");
    public static final ResourceKey<BiomeModifier> ADD_LEAF_LITTER = registerKey("add_leaf_litter");
    public static final ResourceKey<BiomeModifier> ADD_WILD_FLAX = registerKey("add_wild_flax");
    public static final ResourceKey<BiomeModifier> ADD_PEBBLE_CLUSTERS = registerKey("add_pebble_clusters");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_PEBBLE_CLUSTERS, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.WINDSWEPT_HILLS),
                        biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.WINDSWEPT_GRAVELLY_HILLS),
                        biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.WINDSWEPT_FOREST),
                        biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.STONY_PEAKS)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PEBBLE_CLUSTER_PLACED_KEY)),
                GenerationStep.Decoration.LOCAL_MODIFICATIONS));

        context.register(ADD_PINE_LITTER, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_TAIGA),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.PINE_LITTER_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_LEAF_LITTER, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_FOREST),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.LEAF_LITTER_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_WILD_FLAX, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(Biomes.PLAINS),
                        biomes.getOrThrow(Biomes.MEADOW),
                        biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS)
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WILD_FLAX_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(
                NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, name)
        );
    }
}