package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FloraExpansion.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS).add(blockItemKey(ModBlocks.SCATTERED_PEBBLES));
        tag(Tags.Items.FOODS).add(blockItemKey(ModBlocks.GLASSWORT), ModItems.SALTED_GLASSWORT.getKey());
        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(ModItems.MUSIC_DISC_INTO_THE_CAVES.getKey());
        tag(Tags.Items.MUSIC_DISCS).add(ModItems.MUSIC_DISC_INTO_THE_CAVES.getKey());
        tag(Tags.Items.ORE_RATES_DENSE).add(blockItemKey(ModBlocks.CALCITE_COPPER_ORE),
                blockItemKey(ModBlocks.CALCITE_LAPIS_ORE), blockItemKey(ModBlocks.CALCITE_REDSTONE_ORE));
        tag(Tags.Items.ORE_RATES_SINGULAR).add(blockItemKey(ModBlocks.CALCITE_COAL_ORE), blockItemKey(ModBlocks.CALCITE_IRON_ORE),
                blockItemKey(ModBlocks.CALCITE_GOLD_ORE), blockItemKey(ModBlocks.CALCITE_EMERALD_ORE), blockItemKey(ModBlocks.CALCITE_DIAMOND_ORE));
        tag(BlockItemTags.COAL_ORES.item()).add(blockItemKey(ModBlocks.CALCITE_COAL_ORE));
        tag(BlockItemTags.IRON_ORES.item()).add(blockItemKey(ModBlocks.CALCITE_IRON_ORE));
        tag(BlockItemTags.COPPER_ORES.item()).add(blockItemKey(ModBlocks.CALCITE_COPPER_ORE));
        tag(BlockItemTags.GOLD_ORES.item()).add(blockItemKey(ModBlocks.CALCITE_GOLD_ORE));
        tag(BlockItemTags.REDSTONE_ORES.item()).add(blockItemKey(ModBlocks.CALCITE_REDSTONE_ORE));
        tag(BlockItemTags.EMERALD_ORES.item()).add(blockItemKey(ModBlocks.CALCITE_EMERALD_ORE));
        tag(BlockItemTags.LAPIS_ORES.item()).add(blockItemKey(ModBlocks.CALCITE_LAPIS_ORE));
        tag(BlockItemTags.DIAMOND_ORES.item()).add(blockItemKey(ModBlocks.CALCITE_DIAMOND_ORE));
        tag(BlockItemTags.STAIRS.item()).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_STAIRS));
        tag(BlockItemTags.SLABS.item()).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_SLAB));
        tag(ItemTags.WALLS).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_WALL));
        tag(Tags.Items.FOODS).add(ModItems.SALTED_SPRUCE_NUTS.getKey(), ModItems.SALTED_TOASTED_SPRUCE_NUTS.getKey(),
                ModItems.CURED_MEAT.getKey(), ModItems.CURED_FISH.getKey());
        tag(ItemTags.BEE_FOOD).add(blockItemKey(ModBlocks.FLOWERING_DESERT_MOSS));
        tag(ItemTags.WOODEN_STAIRS).add(blockItemKey(ModBlocks.CACTUS_STAIRS)).add(blockItemKey(ModBlocks.CACTUS_MOSAIC_STAIRS));
        tag(ItemTags.WOODEN_SLABS).add(blockItemKey(ModBlocks.CACTUS_SLAB)).add(blockItemKey(ModBlocks.CACTUS_MOSAIC_SLAB));
        tag(ItemTags.CHICKEN_FOOD)
                .add(ModItems.SPRUCE_NUTS.getKey())
                .add(ModItems.FLAX_SEEDS.getKey());

        tag(ItemTags.PARROT_FOOD)
                .add(ModItems.SPRUCE_NUTS.getKey())
                .add(ModItems.FLAX_SEEDS.getKey());

        tag(ItemTags.PARROT_POISONOUS_FOOD)
                .add(ModItems.TOASTED_SPRUCE_NUTS.getKey());

        tag(ItemTags.DAMPENS_VIBRATIONS)
                .add(blockItemKey(ModBlocks.NEEDLE_LITTER))
                .add(blockItemKey(ModBlocks.LINEN_BALE))
                .add(blockItemKey(ModBlocks.LINEN_CARPET));

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.FLAX_SEEDS.getKey())
                .add(ModItems.STRAWBERRY.getKey());

        tag(ItemTags.SAPLINGS)
                .add(blockItemKey(ModBlocks.APPLE_CORE))
                .add(blockItemKey(ModBlocks.CHERRY_PIT));

        tag(ItemTags.LOGS_THAT_BURN)
                .add(blockItemKey(ModBlocks.STRIPPED_CACTUS_LOG))
                .add(blockItemKey(ModBlocks.CACTUS_LOG))
                .add(blockItemKey(ModBlocks.STRIPPED_CACTUS_WOOD))
                .add(blockItemKey(ModBlocks.CACTUS_WOOD));

        tag(ItemTags.PLANKS)
                .add(blockItemKey(ModBlocks.CACTUS_PLANKS));

        tag(ItemTags.BOATS)
                .add(ModItems.CACTUS_BOAT.getKey());

        tag(ItemTags.CHEST_BOATS)
                .add(ModItems.CACTUS_CHEST_BOAT.getKey());

        tag(ItemTags.SIGNS)
                .add(ModItems.CACTUS_SIGN.getKey());

        tag(ItemTags.HANGING_SIGNS)
                .add(ModItems.CACTUS_HANGING_SIGN.getKey());

        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_HELMET.getKey())
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.getKey())
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.getKey())
                .add(ModItems.SPIKED_LEATHER_BOOTS.getKey());

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_HELMET.getKey())
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.getKey())
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.getKey())
                .add(ModItems.SPIKED_LEATHER_BOOTS.getKey());

        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_HELMET.getKey());

        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.getKey());

        tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.getKey());

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_BOOTS.getKey());

        tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.SPIKED_LEATHER_HELMET.getKey());

        tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.getKey());

        tag(ItemTags.LEG_ARMOR)
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.getKey());

        tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.SPIKED_LEATHER_BOOTS.getKey());

        tag(ItemTags.FOX_FOOD)
                .add(ModItems.PRICKLY_PEAR.getKey())
                .add(ModItems.BLUEBERRIES.getKey())
                .add(ModItems.STRAWBERRY.getKey());

        tag(Tags.Items.FOODS)
                .add(ModItems.BLUEBERRIES.getKey())
                .add(ModItems.BLUEBERRY_COOKIE.getKey())
                .add(ModItems.BLUEBERRY_PIE.getKey())
                .add(ModItems.SLICE_OF_BLUEBERRY_PIE.getKey())
                .add(ModItems.BLUEBERRY_JUICE.getKey())
                .add(ModItems.STRAWBERRY_JUICE.getKey())
                .add(ModItems.STRAWBERRY.getKey())
                .add(ModItems.STRAWBERRY_JAM.getKey())
                .add(ModItems.BLUEBERRY_JAM.getKey())
                .add(ModItems.CHERRY_JAM.getKey())
                .add(ModItems.APPLE_JAM.getKey())
                .add(ModItems.SPRUCE_NUTS.getKey())
                .add(ModItems.TOASTED_SPRUCE_NUTS.getKey())
                .add(ModItems.CHERRIES.getKey())
                .add(ModItems.MIXED_BERRIES.getKey())
                .add(ModItems.CHERRY_JUICE.getKey())
                .add(ModItems.CACTUS_SLICE.getKey())
                .add(ModItems.APPLE_JUICE.getKey())
                .add(ModItems.CACTUS_JUICE.getKey())
                .add(ModItems.TRAIL_MIX.getKey())
                .add(ModItems.PRICKLY_PEAR.getKey());

        tag(Tags.Items.STRINGS)
                .add(ModItems.LINEN_THREAD.getKey());

        tag(Tags.Items.SEEDS)
                .add(ModItems.FLAX_SEEDS.getKey())
                .add(ModItems.STRAWBERRY.getKey());

        tag(Tags.Items.FOODS_BERRY)
                .add(ModItems.PRICKLY_PEAR.getKey())
                .add(ModItems.BLUEBERRIES.getKey())
                .add(ModItems.STRAWBERRY.getKey());

        tag(Tags.Items.CROPS).add(ModItems.FLAX_FIBER.getKey()).add(ModItems.FLAX_FLOWER.getKey());
        tag(Tags.Items.FOODS_FRUIT).add(ModItems.CHERRIES.getKey()).add(ModItems.PRICKLY_PEAR.getKey());

        tag(Tags.Items.ANIMAL_FOODS)
                .add(ModItems.PRICKLY_PEAR.getKey());
    }

    private static ResourceKey<Item> blockItemKey(DeferredBlock<? extends Block> block) {
        return ResourceKey.create(Registries.ITEM, block.getId());
    }
}
