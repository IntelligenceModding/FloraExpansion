package de.artemis.floraexpansion.common.migration;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFeatures;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModParticles;
import de.artemis.floraexpansion.common.registry.ModTreeDecorators;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Map;

/**
 * Permanent read aliases for IDs renamed in 0.4.2.
 *
 * Register before registry events, on both sides. Aliases return the current object/holder,
 * so vanilla codecs retain state properties and components and save the canonical ID.
 * Do not gate these by the last-played version: old chunks and nested containers may
 * remain unloaded through any number of newer sessions.
 */
public final class LegacyContentAliases {
    public static final Map<String, String> BLOCKS = Map.ofEntries(
            Map.entry("pine_litter", "needle_litter"),
            Map.entry("flax_crop", "flax"),
            Map.entry("linen_block", "linen_bale"),
            Map.entry("pebble_patch", "scattered_pebbles"),
            Map.entry("pebble_block", "packed_pebbles"),
            Map.entry("fruiting_cherry_leaves", "orchard_cherry_leaves"),
            Map.entry("fruiting_oak_leaves", "apple_leaves"),
            Map.entry("giant_cactus_base", "cactus_log"),
            Map.entry("stripped_giant_cactus_base", "stripped_cactus_log"),
            Map.entry("cactus_thorn", "cactus_spine"),
            Map.entry("giant_cactus_blossom", "giant_cactus_shoot"),
            Map.entry("giant_cactus_wood", "cactus_wood"),
            Map.entry("stripped_giant_cactus_wood", "stripped_cactus_wood"),
            Map.entry("cactus_cluster", "small_cactus"),
            Map.entry("opuntia_cactus", "prickly_pear_cactus"),
            Map.entry("strawberry_candle_cake", "strawberry_cake_with_candle"),
            Map.entry("white_strawberry_candle_cake", "strawberry_cake_with_white_candle"),
            Map.entry("orange_strawberry_candle_cake", "strawberry_cake_with_orange_candle"),
            Map.entry("magenta_strawberry_candle_cake", "strawberry_cake_with_magenta_candle"),
            Map.entry("light_blue_strawberry_candle_cake", "strawberry_cake_with_light_blue_candle"),
            Map.entry("yellow_strawberry_candle_cake", "strawberry_cake_with_yellow_candle"),
            Map.entry("lime_strawberry_candle_cake", "strawberry_cake_with_lime_candle"),
            Map.entry("pink_strawberry_candle_cake", "strawberry_cake_with_pink_candle"),
            Map.entry("gray_strawberry_candle_cake", "strawberry_cake_with_gray_candle"),
            Map.entry("light_gray_strawberry_candle_cake", "strawberry_cake_with_light_gray_candle"),
            Map.entry("cyan_strawberry_candle_cake", "strawberry_cake_with_cyan_candle"),
            Map.entry("purple_strawberry_candle_cake", "strawberry_cake_with_purple_candle"),
            Map.entry("blue_strawberry_candle_cake", "strawberry_cake_with_blue_candle"),
            Map.entry("brown_strawberry_candle_cake", "strawberry_cake_with_brown_candle"),
            Map.entry("green_strawberry_candle_cake", "strawberry_cake_with_green_candle"),
            Map.entry("red_strawberry_candle_cake", "strawberry_cake_with_red_candle"),
            Map.entry("black_strawberry_candle_cake", "strawberry_cake_with_black_candle")
    );

    public static final Map<String, String> ITEMS = Map.ofEntries(
            Map.entry("pine_litter", "needle_litter"),
            Map.entry("linen_block", "linen_bale"),
            Map.entry("pebble_patch", "scattered_pebbles"),
            Map.entry("pebble_block", "packed_pebbles"),
            Map.entry("fruiting_cherry_leaves", "orchard_cherry_leaves"),
            Map.entry("fruiting_oak_leaves", "apple_leaves"),
            Map.entry("giant_cactus_base", "cactus_log"),
            Map.entry("stripped_giant_cactus_base", "stripped_cactus_log"),
            Map.entry("cactus_thorn", "cactus_spine"),
            Map.entry("giant_cactus_blossom", "giant_cactus_shoot"),
            Map.entry("giant_cactus_wood", "cactus_wood"),
            Map.entry("stripped_giant_cactus_wood", "stripped_cactus_wood"),
            Map.entry("cactus_cluster", "small_cactus"),
            Map.entry("pine_cone", "spruce_cone"),
            Map.entry("pine_nuts", "spruce_nuts"),
            Map.entry("toasted_pine_nuts", "toasted_spruce_nuts"),
            Map.entry("forest_snack", "trail_mix"),
            Map.entry("sweet_berry_mix", "mixed_berries"),
            Map.entry("blueberry_pie_slice", "slice_of_blueberry_pie"),
            Map.entry("empty_jar", "glass_jar"),
            Map.entry("flax_seed", "flax_seeds"),
            Map.entry("linen_cloth", "linen_fabric"),
            Map.entry("cactus_helmet", "spiked_leather_helmet"),
            Map.entry("cactus_chestplate", "spiked_leather_chestplate"),
            Map.entry("cactus_leggings", "spiked_leather_leggings"),
            Map.entry("cactus_boots", "spiked_leather_boots")
    );

    public static final Map<String, String> FEATURE_TYPES = Map.of(
            "cactus_cluster_feature", "small_cactus_feature",
            "opuntia_cactus_feature", "prickly_pear_cactus_feature"
    );

    public static final Map<String, String> TREE_DECORATOR_TYPES = Map.of(
            "fruiting_cherry_leaves_decorator", "orchard_cherry_leaves_decorator",
            "fruiting_oak_leaves_decorator", "apple_leaves_decorator"
    );

    public static final Map<String, String> PARTICLE_TYPES = Map.of(
            "pine_leaf_fluff_particles", "needle_fluff_particles",
            "pine_particles", "needle_particles"
    );

    private LegacyContentAliases() {
    }

    public static void register() {
        addAliases(ModBlocks.BLOCKS, BLOCKS);
        addAliases(ModItems.ITEMS, ITEMS);
        addAliases(ModFeatures.FEATURES, FEATURE_TYPES);
        addAliases(ModTreeDecorators.TREE_DECORATORS, TREE_DECORATOR_TYPES);
        addAliases(ModParticles.PARTICLE_TYPES, PARTICLE_TYPES);
    }

    private static void addAliases(DeferredRegister<?> registry, Map<String, String> renames) {
        renames.forEach((oldName, newName) -> registry.addAlias(id(oldName), id(newName)));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path);
    }
}
