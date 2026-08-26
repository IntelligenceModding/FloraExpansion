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
                .add(ModItems.PINE_NUTS.getKey())
                .add(ModItems.FLAX_SEED.getKey());

        tag(ItemTags.PARROT_FOOD)
                .add(ModItems.PINE_NUTS.getKey())
                .add(ModItems.FLAX_SEED.getKey());

        tag(ItemTags.PARROT_POISONOUS_FOOD)
                .add(ModItems.TOASTED_PINE_NUTS.getKey());

        tag(ItemTags.DAMPENS_VIBRATIONS)
                .add(blockItemKey(ModBlocks.PINE_LITTER))
                .add(blockItemKey(ModBlocks.LINEN_BLOCK))
                .add(blockItemKey(ModBlocks.LINEN_CARPET));

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.FLAX_SEED.getKey())
                .add(ModItems.STRAWBERRY.getKey());

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
                .add(ModItems.CACTUS_BOAT.getKey());

        tag(ItemTags.CHEST_BOATS)
                .add(ModItems.CACTUS_CHEST_BOAT.getKey());

        tag(ItemTags.SIGNS)
                .add(ModItems.CACTUS_SIGN.getKey());

        tag(ItemTags.HANGING_SIGNS)
                .add(ModItems.CACTUS_HANGING_SIGN.getKey());

        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.getKey())
                .add(ModItems.CACTUS_CHESTPLATE.getKey())
                .add(ModItems.CACTUS_LEGGINGS.getKey())
                .add(ModItems.CACTUS_BOOTS.getKey());

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.getKey())
                .add(ModItems.CACTUS_CHESTPLATE.getKey())
                .add(ModItems.CACTUS_LEGGINGS.getKey())
                .add(ModItems.CACTUS_BOOTS.getKey());

        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.getKey());

        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_CHESTPLATE.getKey());

        tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_LEGGINGS.getKey());

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_BOOTS.getKey());

        tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.CACTUS_HELMET.getKey());

        tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.CACTUS_CHESTPLATE.getKey());

        tag(ItemTags.LEG_ARMOR)
                .add(ModItems.CACTUS_LEGGINGS.getKey());

        tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.CACTUS_BOOTS.getKey());

        tag(ItemTags.FOX_FOOD)
                .add(ModItems.PRICKLY_PEAR.getKey())
                .add(ModItems.BLUEBERRIES.getKey())
                .add(ModItems.STRAWBERRY.getKey());

        tag(Tags.Items.FOODS)
                .add(ModItems.BLUEBERRIES.getKey())
                .add(ModItems.BLUEBERRY_COOKIE.getKey())
                .add(ModItems.BLUEBERRY_PIE.getKey())
                .add(ModItems.BLUEBERRY_PIE_SLICE.getKey())
                .add(ModItems.BLUEBERRY_JUICE.getKey())
                .add(ModItems.STRAWBERRY.getKey())
                .add(ModItems.STRAWBERRY_JAM.getKey())
                .add(ModItems.PINE_NUTS.getKey())
                .add(ModItems.TOASTED_PINE_NUTS.getKey())
                .add(ModItems.CHERRIES.getKey())
                .add(ModItems.SWEET_BERRY_MIX.getKey())
                .add(ModItems.CHERRY_JUICE.getKey())
                .add(ModItems.CACTUS_SLICE.getKey())
                .add(ModItems.APPLE_JUICE.getKey())
                .add(ModItems.CACTUS_JUICE.getKey())
                .add(ModItems.FOREST_SNACK.getKey())
                .add(ModItems.PRICKLY_PEAR.getKey());

        tag(Tags.Items.STRINGS)
                .add(ModItems.LINEN_THREAD.getKey());

        tag(Tags.Items.SEEDS)
                .add(ModItems.FLAX_SEED.getKey())
                .add(ModItems.STRAWBERRY.getKey());

        tag(Tags.Items.FOODS_BERRY)
                .add(ModItems.PRICKLY_PEAR.getKey())
                .add(ModItems.BLUEBERRIES.getKey())
                .add(ModItems.STRAWBERRY.getKey());

        tag(Tags.Items.ANIMAL_FOODS)
                .add(ModItems.PRICKLY_PEAR.getKey());
    }

    private static ResourceKey<Item> blockItemKey(DeferredBlock<? extends Block> block) {
        return ResourceKey.create(Registries.ITEM, block.getId());
    }
}

