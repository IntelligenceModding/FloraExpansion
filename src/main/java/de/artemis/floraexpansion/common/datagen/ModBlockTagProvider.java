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
        tag(BlockTags.CLIMBABLE)
                .add(ModBlocks.TWIG_LADDER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.PEBBLE_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.GIANT_CACTUS_BASE.get())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                .add(ModBlocks.GIANT_CACTUS_WOOD.get())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get())
                .add(ModBlocks.GIANT_CACTUS_STEM.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.PEBBLE_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.LINEN_BLOCK.get())
                .add(ModBlocks.FLAX_BALE.get())
                .add(ModBlocks.FRUITING_OAK_LEAVES.get())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.get());

        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(ModBlocks.FLAX_CROP.get());

        tag(BlockTags.CROPS)
                .add(ModBlocks.FLAX_CROP.get());

        tag(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.FLAX_CROP.get());

        tag(BlockTags.BEE_GROWABLES)
                .add(ModBlocks.FLAX_CROP.get())
                .add(ModBlocks.FRUITING_OAK_LEAVES.get())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.get());

        tag(BlockTags.SAPLINGS)
                .add(ModBlocks.APPLE_CORE.get())
                .add(ModBlocks.CHERRY_PIT.get());

        tag(BlockTags.LEAVES)
                .add(ModBlocks.FRUITING_OAK_LEAVES.get())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.get());

        tag(BlockTags.FLOWERS)
                .add(ModBlocks.FRUITING_OAK_LEAVES.get())
                .add(ModBlocks.CACTUS_FLOWER.get())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.get());

        tag(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.CACTUS_FLOWER.get());

        tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                .add(ModBlocks.GIANT_CACTUS_BASE.get())
                .add(ModBlocks.GIANT_CACTUS_WOOD.get())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get());

        tag(BlockTags.PLANKS)
                .add(ModBlocks.CACTUS_PLANKS.get());

        tag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.CACTUS_STAIRS.get());

        tag(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.CACTUS_SLAB.get());

        tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.CACTUS_FENCE.get());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.CACTUS_FENCE_GATE.get());

        tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.CACTUS_BUTTON.get());

        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.CACTUS_PRESSURE_PLATE.get());

        tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.CACTUS_DOOR.get());

        tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.CACTUS_TRAPDOOR.get());

        tag(BlockTags.STANDING_SIGNS)
                .add(ModBlocks.CACTUS_SIGN.get());

        tag(BlockTags.WALL_SIGNS)
                .add(ModBlocks.CACTUS_WALL_SIGN.get());

        tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_HANGING_SIGN.get());

        tag(BlockTags.WALL_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_WALL_HANGING_SIGN.get());
    }
}