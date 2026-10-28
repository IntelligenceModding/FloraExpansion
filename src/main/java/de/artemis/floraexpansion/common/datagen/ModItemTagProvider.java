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
        tag(ItemTags.CHICKEN_FOOD)
                .add(ModItems.PINE_NUTS.key())
                .add(ModItems.FLAX_SEED.key());

        tag(ItemTags.PARROT_FOOD)
                .add(ModItems.PINE_NUTS.key())
                .add(ModItems.FLAX_SEED.key());

        tag(ItemTags.PARROT_POISONOUS_FOOD)
                .add(ModItems.TOASTED_PINE_NUTS.key());

        tag(ItemTags.DAMPENS_VIBRATIONS)
                .add(blockItemKey(ModBlocks.PINE_LITTER))
                .add(blockItemKey(ModBlocks.LINEN_BLOCK))
                .add(blockItemKey(ModBlocks.LINEN_CARPET));

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.FLAX_SEED.key())
                .add(ModItems.STRAWBERRY.key());

        tag(ItemTags.SAPLINGS)
                .add(blockItemKey(ModBlocks.APPLE_CORE))
                .add(blockItemKey(ModBlocks.CHERRY_PIT));

        tag(ItemTags.LOGS_THAT_BURN)
                .add(blockItemKey(ModBlocks.STRIPPED_GIANT_CACTUS_BASE))
                .add(blockItemKey(ModBlocks.GIANT_CACTUS_BASE))
                .add(blockItemKey(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD))
                .add(blockItemKey(ModBlocks.GIANT_CACTUS_WOOD));

        tag(ItemTags.PLANKS)
                .add(blockItemKey(ModBlocks.CACTUS_PLANKS));

        tag(ItemTags.BOATS)
                .add(ModItems.CACTUS_BOAT.key());

        tag(ItemTags.CHEST_BOATS)
                .add(ModItems.CACTUS_CHEST_BOAT.key());

        tag(ItemTags.SIGNS)
                .add(ModItems.CACTUS_SIGN.key());

        tag(ItemTags.HANGING_SIGNS)
                .add(ModItems.CACTUS_HANGING_SIGN.key());

        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.key())
                .add(ModItems.CACTUS_CHESTPLATE.key())
                .add(ModItems.CACTUS_LEGGINGS.key())
                .add(ModItems.CACTUS_BOOTS.key());

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.key())
                .add(ModItems.CACTUS_CHESTPLATE.key())
                .add(ModItems.CACTUS_LEGGINGS.key())
                .add(ModItems.CACTUS_BOOTS.key());

        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.key());

        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_CHESTPLATE.key());

        tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_LEGGINGS.key());

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_BOOTS.key());

        tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.CACTUS_HELMET.key());

        tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.CACTUS_CHESTPLATE.key());

        tag(ItemTags.LEG_ARMOR)
                .add(ModItems.CACTUS_LEGGINGS.key());

        tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.CACTUS_BOOTS.key());

        tag(ItemTags.FOX_FOOD)
                .add(ModItems.PRICKLY_PEAR.key())
                .add(ModItems.BLUEBERRIES.key())
                .add(ModItems.STRAWBERRY.key());

        tag(Tags.Items.FOODS)
                .add(ModItems.BLUEBERRIES.key())
                .add(ModItems.BLUEBERRY_COOKIE.key())
                .add(ModItems.BLUEBERRY_PIE.key())
                .add(ModItems.BLUEBERRY_PIE_SLICE.key())
                .add(ModItems.BLUEBERRY_JUICE.key())
                .add(ModItems.STRAWBERRY.key())
                .add(ModItems.STRAWBERRY_JAM.key())
                .add(ModItems.PINE_NUTS.key())
                .add(ModItems.TOASTED_PINE_NUTS.key())
                .add(ModItems.CHERRIES.key())
                .add(ModItems.SWEET_BERRY_MIX.key())
                .add(ModItems.CHERRY_JUICE.key())
                .add(ModItems.CACTUS_SLICE.key())
                .add(ModItems.APPLE_JUICE.key())
                .add(ModItems.CACTUS_JUICE.key())
                .add(ModItems.FOREST_SNACK.key())
                .add(ModItems.PRICKLY_PEAR.key());

        tag(Tags.Items.STRINGS)
                .add(ModItems.LINEN_THREAD.key());

        tag(Tags.Items.SEEDS)
                .add(ModItems.FLAX_SEED.key())
                .add(ModItems.STRAWBERRY.key());

        tag(Tags.Items.FOODS_BERRY)
                .add(ModItems.PRICKLY_PEAR.key())
                .add(ModItems.BLUEBERRIES.key())
                .add(ModItems.STRAWBERRY.key());

        tag(Tags.Items.ANIMAL_FOODS)
                .add(ModItems.PRICKLY_PEAR.key());
    }

    private static ResourceKey<Item> blockItemKey(DeferredBlock<? extends Block> block) {
        return ResourceKey.create(Registries.ITEM, block.getId());
    }
}

