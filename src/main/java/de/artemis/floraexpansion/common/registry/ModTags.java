package de.artemis.floraexpansion.common.registry;


import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("unused")
public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> GIANT_CACTUS_GROWTH_SOILS = createTag("giant_cactus_growth_soils");
        public static final TagKey<Block> CACTUS_STEM_SUPPORTS = createTag("cactus_stem_supports");
        public static final TagKey<Block> BLUEBERRY_SOILS = createTag("blueberry_soils");
        public static final TagKey<Block> WILD_STRAWBERRY_SOILS = createTag("wild_strawberry_soils");
        public static final TagKey<Block> WILD_FLAX_SOILS = createTag("wild_flax_soils");
        public static final TagKey<Block> CROP_SOILS = createTag("crop_soils");
        public static final TagKey<Block> CACTUS_SOILS = createTag("cactus_soils");
        public static final TagKey<Block> GIANT_CACTUS_SUPPORTS = createTag("giant_cactus_supports");
        public static final TagKey<Block> BASKET_GATHERABLE = createTag("basket_gatherable");

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name));
        }
    }

    public static class Biomes {
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_PRICKLY_PEAR_CACTUS = createTag("has_prickly_pear_cactus");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_SMALL_CACTUS = createTag("has_small_cactus");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_FALLEN_GIANT_CACTUS = createTag("has_fallen_giant_cactus");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_PEBBLE_CLUSTERS = createTag("has_pebble_clusters");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_NEEDLE_LITTER = createTag("has_needle_litter");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_BLUEBERRY_BUSHES = createTag("has_blueberry_bushes");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_WILD_FLAX = createTag("has_wild_flax");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_WILD_STRAWBERRIES = createTag("has_wild_strawberries");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_ORCHARD_CHERRY_TREES = createTag("has_orchard_cherry_trees");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_APPLE_TREES_SPARSE = createTag("has_apple_trees_sparse");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_APPLE_TREES_FOREST = createTag("has_apple_trees_forest");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_FANCY_APPLE_TREES_RARE = createTag("has_fancy_apple_trees_rare");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_FANCY_APPLE_TREES_JUNGLE = createTag("has_fancy_apple_trees_jungle");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_APPLE_TREES_DARK_FOREST = createTag("has_apple_trees_dark_forest");
        public static final TagKey<net.minecraft.world.level.biome.Biome> HAS_GIANT_CACTUS = createTag("has_giant_cactus");
        private static TagKey<net.minecraft.world.level.biome.Biome> createTag(String path) {
            return TagKey.create(net.minecraft.core.registries.Registries.BIOME, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path));
        }
    }

    public static class Items {
        public static final TagKey<Item> LINEN_FIBERS = createTag("linen_fibers");
        public static final TagKey<Item> LINEN_THREADS = createTag("linen_threads");
        public static final TagKey<Item> CRATE_STORABLE = createTag("crate_storable");
        public static final TagKey<Item> SPIKED_LEATHER_ARMOR_REPAIRABLE = createTag("spiked_leather_armor_repairable");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name));
        }
    }
}
