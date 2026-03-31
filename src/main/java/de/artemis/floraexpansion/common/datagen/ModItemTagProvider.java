package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FloraExpansion.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        // Minecraft

        tag(ItemTags.CHICKEN_FOOD)
                .add(ModItems.PINE_NUTS.get())
                .add(ModItems.FLAX_SEED.get());

        tag(ItemTags.PARROT_FOOD)
                .add(ModItems.PINE_NUTS.get())
                .add(ModItems.FLAX_SEED.get());

        tag(ItemTags.PARROT_POISONOUS_FOOD)
                .add(ModItems.TOASTED_PINE_NUTS.get());

        tag(ItemTags.ARROWS)
                .add(ModItems.PINE_CONE.get())
                .add(ModItems.PEBBLES.get());

        tag(ItemTags.DAMPENS_VIBRATIONS)
                .add(ModBlocks.PINE_LITTER.get().asItem())
                .add(ModBlocks.LINEN_CARPET.get().asItem());

        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.FLAX_SEED.get());

        tag(ItemTags.SAPLINGS)
                .add(ModBlocks.APPLE_CORE.get().asItem())
                .add(ModBlocks.CHERRY_PIT.get().asItem());

        tag(ItemTags.FLOWERS)
                .add(ModBlocks.GIANT_CACTUS_BLOSSOM.get().asItem());

        tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get().asItem())
                .add(ModBlocks.GIANT_CACTUS_BASE.get().asItem())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get().asItem())
                .add(ModBlocks.GIANT_CACTUS_WOOD.get().asItem());

        tag(ItemTags.PLANKS)
                .add(ModBlocks.CACTUS_PLANKS.get().asItem());

        tag(ItemTags.BOATS)
                .add(ModItems.CACTUS_BOAT.get());

        tag(ItemTags.CHEST_BOATS)
                .add(ModItems.CACTUS_CHEST_BOAT.get());

        tag(ItemTags.SIGNS)
                .add(ModBlocks.CACTUS_SIGN.get().asItem());

        tag(ItemTags.HANGING_SIGNS)
                .add(ModBlocks.CACTUS_HANGING_SIGN.get().asItem());

        tag(ItemTags.ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.get())
                .add(ModItems.CACTUS_CHESTPLATE.get())
                .add(ModItems.CACTUS_LEGGINGS.get())
                .add(ModItems.CACTUS_BOOTS.get());

        tag(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.get())
                .add(ModItems.CACTUS_CHESTPLATE.get())
                .add(ModItems.CACTUS_LEGGINGS.get())
                .add(ModItems.CACTUS_BOOTS.get());

        tag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_HELMET.get());

        tag(ItemTags.CHEST_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_CHESTPLATE.get());

        tag(ItemTags.LEG_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_LEGGINGS.get());

        tag(ItemTags.FOOT_ARMOR_ENCHANTABLE)
                .add(ModItems.CACTUS_BOOTS.get());

        tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.CACTUS_HELMET.get());

        tag(ItemTags.CHEST_ARMOR)
                .add(ModItems.CACTUS_CHESTPLATE.get());

        tag(ItemTags.LEG_ARMOR)
                .add(ModItems.CACTUS_LEGGINGS.get());

        tag(ItemTags.FOOT_ARMOR)
                .add(ModItems.CACTUS_BOOTS.get());

        tag(ItemTags.FOX_FOOD)
                .add(ModItems.PRICKLY_PEAR.get());

        // NeoForge

        tag(Tags.Items.FOODS)
                .add(ModItems.PINE_NUTS.get())
                .add(ModItems.TOASTED_PINE_NUTS.get())
                .add(ModItems.CHERRIES.get())
                .add(ModItems.SWEET_BERRY_MIX.get())
                .add(ModItems.CHERRY_JUICE.get())
                .add(ModItems.CACTUS_SLICE.get())
                .add(ModItems.APPLE_JUICE.get())
                .add(ModItems.CACTUS_JUICE.get())
                .add(ModItems.FOREST_SNACK.get())
                .add(ModItems.PRICKLY_PEAR.get());

        tag(Tags.Items.STRINGS)
                .add(ModItems.LINEN_THREAD.get());

        tag(Tags.Items.SEEDS)
                .add(ModItems.FLAX_SEED.get());

        tag(Tags.Items.FOODS_BERRY)
                .add(ModItems.PRICKLY_PEAR.get());

        tag(Tags.Items.ANIMAL_FOODS)
                .add(ModItems.PRICKLY_PEAR.get());

        // Mod

        tag(ModTags.Items.CACTUS_ARMOR_REPAIRABLE)
                .add(ModBlocks.CACTUS_THORN.get().asItem());
    }
}
