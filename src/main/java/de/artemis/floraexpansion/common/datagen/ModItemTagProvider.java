package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;

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
        tag(Tags.Items.FOODS).add(blockItemKey(ModBlocks.GLASSWORT), ModItems.SALTED_GLASSWORT.get());
        tag(ItemTags.CREEPER_DROP_MUSIC_DISCS).add(ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
        tag(Tags.Items.MUSIC_DISCS).add(ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
        tag(Tags.Items.ORE_RATES_DENSE).add(blockItemKey(ModBlocks.CALCITE_COPPER_ORE),
                blockItemKey(ModBlocks.CALCITE_LAPIS_ORE), blockItemKey(ModBlocks.CALCITE_REDSTONE_ORE));
        tag(Tags.Items.ORE_RATES_SINGULAR).add(blockItemKey(ModBlocks.CALCITE_COAL_ORE), blockItemKey(ModBlocks.CALCITE_IRON_ORE),
                blockItemKey(ModBlocks.CALCITE_GOLD_ORE), blockItemKey(ModBlocks.CALCITE_EMERALD_ORE), blockItemKey(ModBlocks.CALCITE_DIAMOND_ORE));
        tag(ItemTags.COAL_ORES).add(blockItemKey(ModBlocks.CALCITE_COAL_ORE));
        tag(ItemTags.IRON_ORES).add(blockItemKey(ModBlocks.CALCITE_IRON_ORE));
        tag(ItemTags.COPPER_ORES).add(blockItemKey(ModBlocks.CALCITE_COPPER_ORE));
        tag(ItemTags.GOLD_ORES).add(blockItemKey(ModBlocks.CALCITE_GOLD_ORE));
        tag(ItemTags.REDSTONE_ORES).add(blockItemKey(ModBlocks.CALCITE_REDSTONE_ORE));
        tag(ItemTags.EMERALD_ORES).add(blockItemKey(ModBlocks.CALCITE_EMERALD_ORE));
        tag(ItemTags.LAPIS_ORES).add(blockItemKey(ModBlocks.CALCITE_LAPIS_ORE));
        tag(ItemTags.DIAMOND_ORES).add(blockItemKey(ModBlocks.CALCITE_DIAMOND_ORE));
        tag(ItemTags.STAIRS).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_STAIRS));
        tag(ItemTags.SLABS).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_SLAB));
        tag(ItemTags.WALLS).add(blockItemKey(ModBlocks.CRYSTALLINE_SALT_WALL));
        tag(Tags.Items.FOODS).add(ModItems.SALTED_SPRUCE_NUTS.get(), ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(),
                ModItems.CURED_MEAT.get(), ModItems.CURED_FISH.get());
        tag(ItemTags.BEE_FOOD).add(blockItemKey(ModBlocks.FLOWERING_DESERT_MOSS));
        tag(ItemTags.WOODEN_STAIRS).add(blockItemKey(ModBlocks.CACTUS_STAIRS)).add(blockItemKey(ModBlocks.CACTUS_MOSAIC_STAIRS));
        tag(ItemTags.WOODEN_SLABS).add(blockItemKey(ModBlocks.CACTUS_SLAB)).add(blockItemKey(ModBlocks.CACTUS_MOSAIC_SLAB));
        tag(ItemTags.CHICKEN_FOOD)
                .add(ModItems.SPRUCE_NUTS.get())
                .add(ModItems.FLAX_SEEDS.get());

        tag(ItemTags.PARROT_FOOD)
                .add(ModItems.SPRUCE_NUTS.get())
                .add(ModItems.FLAX_SEEDS.get());

        tag(ItemTags.PARROT_POISONOUS_FOOD)
                .add(ModItems.TOASTED_SPRUCE_NUTS.get());

        tag(ItemTags.DAMPENS_VIBRATIONS)
                .add(blockItemKey(ModBlocks.NEEDLE_LITTER))
                .add(blockItemKey(ModBlocks.LINEN_BALE))
                .add(blockItemKey(ModBlocks.LINEN_CARPET));

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.FLAX_SEEDS.get())
                .add(ModItems.STRAWBERRY.get());

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
                .add(ModItems.CACTUS_BOAT.get());

        tag(ItemTags.CHEST_BOATS)
                .add(ModItems.CACTUS_CHEST_BOAT.get());

        tag(ItemTags.SIGNS)
                .add(ModItems.CACTUS_SIGN.get());

        tag(ItemTags.HANGING_SIGNS)
                .add(ModItems.CACTUS_HANGING_SIGN.get());

        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_HELMET.get())
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.get())
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.get())
                .add(ModItems.SPIKED_LEATHER_BOOTS.get());

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_HELMET.get())
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.get())
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.get())
                .add(ModItems.SPIKED_LEATHER_BOOTS.get());

        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_HELMET.get());

        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.get());

        tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.get());

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(ModItems.SPIKED_LEATHER_BOOTS.get());

        tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.SPIKED_LEATHER_HELMET.get());

        tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.SPIKED_LEATHER_CHESTPLATE.get());

        tag(ItemTags.LEG_ARMOR)
                .add(ModItems.SPIKED_LEATHER_LEGGINGS.get());

        tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.SPIKED_LEATHER_BOOTS.get());

        tag(ItemTags.FOX_FOOD)
                .add(ModItems.PRICKLY_PEAR.get())
                .add(ModItems.BLUEBERRIES.get())
                .add(ModItems.STRAWBERRY.get());

        tag(Tags.Items.FOODS)
                .add(ModItems.BLUEBERRIES.get())
                .add(ModItems.BLUEBERRY_COOKIE.get())
                .add(ModItems.BLUEBERRY_PIE.get())
                .add(ModItems.SLICE_OF_BLUEBERRY_PIE.get())
                .add(ModItems.BLUEBERRY_JUICE.get())
                .add(ModItems.STRAWBERRY_JUICE.get())
                .add(ModItems.STRAWBERRY.get())
                .add(ModItems.STRAWBERRY_JAM.get())
                .add(ModItems.BLUEBERRY_JAM.get())
                .add(ModItems.CHERRY_JAM.get())
                .add(ModItems.APPLE_JAM.get())
                .add(ModItems.SPRUCE_NUTS.get())
                .add(ModItems.TOASTED_SPRUCE_NUTS.get())
                .add(ModItems.CHERRIES.get())
                .add(ModItems.MIXED_BERRIES.get())
                .add(ModItems.CHERRY_JUICE.get())
                .add(ModItems.CACTUS_SLICE.get())
                .add(ModItems.APPLE_JUICE.get())
                .add(ModItems.CACTUS_JUICE.get())
                .add(ModItems.TRAIL_MIX.get())
                .add(ModItems.PRICKLY_PEAR.get());

        tag(Tags.Items.STRINGS)
                .add(ModItems.LINEN_THREAD.get());

        tag(Tags.Items.SEEDS)
                .add(ModItems.FLAX_SEEDS.get())
                .add(ModItems.STRAWBERRY.get());

        tag(Tags.Items.FOODS_BERRY)
                .add(ModItems.PRICKLY_PEAR.get())
                .add(ModItems.BLUEBERRIES.get())
                .add(ModItems.STRAWBERRY.get());

        tag(Tags.Items.CROPS).add(ModItems.FLAX_FIBER.get()).add(ModItems.FLAX_FLOWER.get());
        tag(Tags.Items.FOODS_FRUIT).add(ModItems.CHERRIES.get()).add(ModItems.PRICKLY_PEAR.get());

        tag(Tags.Items.ANIMAL_FOODS)
                .add(ModItems.PRICKLY_PEAR.get());
    }

    private static Item blockItemKey(DeferredBlock<? extends Block> block) {
        return block.get().asItem();
    }
}
