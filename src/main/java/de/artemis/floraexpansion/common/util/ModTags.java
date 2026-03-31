package de.artemis.floraexpansion.common.util;


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

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> CACTUS_ARMOR_REPAIRABLE = createTag("cactus_armor_repairable");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name));
        }
    }
}