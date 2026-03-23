package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FloraExpansion.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //Minecraft
        tag(BlockTags.CLIMBABLE).add(ModBlocks.TWIG_LADDER.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.PEBBLE_BLOCK.get());
        tag(BlockTags.MINEABLE_WITH_SHOVEL).add(ModBlocks.PEBBLE_BLOCK.get());
        tag(BlockTags.MINEABLE_WITH_HOE).add(ModBlocks.LINEN_BLOCK.get()).add(ModBlocks.FLAX_BALE.get()).add(ModBlocks.FRUITING_CHERRY_LEAVES.get());
        tag(BlockTags.MAINTAINS_FARMLAND).add(ModBlocks.FLAX_CROP.get());
        tag(BlockTags.CROPS).add(ModBlocks.FLAX_CROP.get());
        tag(BlockTags.SWORD_EFFICIENT).add(ModBlocks.FLAX_CROP.get());
        tag(BlockTags.BEE_GROWABLES).add(ModBlocks.FLAX_CROP.get()).add(ModBlocks.FRUITING_CHERRY_LEAVES.get());
        tag(BlockTags.SAPLINGS).add(ModBlocks.CHERRY_PIT.get());
        tag(BlockTags.LEAVES).add(ModBlocks.FRUITING_CHERRY_LEAVES.get());
        tag(BlockTags.FLOWERS).add(ModBlocks.FRUITING_CHERRY_LEAVES.get());
    }
}