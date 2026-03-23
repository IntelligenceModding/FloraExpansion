package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, FloraExpansion.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //Minecraft
        tag(ItemTags.CHICKEN_FOOD).add(ModItems.PINE_NUTS.get()).add(ModItems.FLAX_SEED.get());
        tag(ItemTags.PARROT_FOOD).add(ModItems.PINE_NUTS.get()).add(ModItems.FLAX_SEED.get());
        tag(ItemTags.PARROT_POISONOUS_FOOD).add(ModItems.TOASTED_PINE_NUTS.get());
        tag(ItemTags.ARROWS).add(ModItems.PINE_CONE.get()).add(ModItems.PEBBLES.get());
        tag(ItemTags.DAMPENS_VIBRATIONS).add(ModBlocks.LEAF_LITTER.get().asItem()).add(ModBlocks.PINE_LITTER.get().asItem()).add(ModBlocks.LINEN_CARPET.get().asItem());
        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS).add(ModItems.FLAX_SEED.get());
        tag(ItemTags.SAPLINGS).add(ModBlocks.CHERRY_PIT.get().asItem());

        //NeoForge
        tag(Tags.Items.FOODS).add(ModItems.PINE_NUTS.get()).add(ModItems.TOASTED_PINE_NUTS.get()).add(ModItems.CHERRIES.get()).add(ModItems.SWEET_BERRY_MIX.get()).add(ModItems.FOREST_SNACK.get());
        tag(Tags.Items.STRINGS).add(ModItems.LINEN_THREAD.get());
        tag(Tags.Items.SEEDS).add(ModItems.FLAX_SEED.get());
    }
}
