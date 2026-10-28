package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FloraExpansion.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.CLIMBABLE)
                .add(ModBlocks.TWIG_LADDER.key());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.PEBBLE_BLOCK.key());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.CRATE.key())
                .add(ModBlocks.GIANT_CACTUS_BASE.key())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.key())
                .add(ModBlocks.GIANT_CACTUS_WOOD.key())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.key())
                .add(ModBlocks.GIANT_CACTUS_STEM.key())
                .add(ModBlocks.TWIG_LADDER.key())
                .add(ModBlocks.CACTUS_MOSAIC.key());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.PEBBLE_BLOCK.key());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.LINEN_BLOCK.key())
                .add(ModBlocks.FLAX_BALE.key())
                .add(ModBlocks.FRUITING_OAK_LEAVES.key())
                .add(ModBlocks.LINEN_CARPET.key())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.key())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.key())
                .add(ModBlocks.BLUEBERRY_BUSH.key());

        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(ModBlocks.FLAX_CROP.key())
                .add(ModBlocks.STRAWBERRY_PLANT.key());

        tag(BlockTags.CROPS)
                .add(ModBlocks.FLAX_CROP.key())
                .add(ModBlocks.STRAWBERRY_PLANT.key());

        tag(BlockTags.CANDLE_CAKES)
                .add(ModBlocks.STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.WHITE_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.ORANGE_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.MAGENTA_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.LIGHT_BLUE_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.YELLOW_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.LIME_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.PINK_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.GRAY_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.LIGHT_GRAY_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.CYAN_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.PURPLE_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.BLUE_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.BROWN_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.GREEN_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.RED_STRAWBERRY_CANDLE_CAKE.key())
                .add(ModBlocks.BLACK_STRAWBERRY_CANDLE_CAKE.key());

        tag(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.FLAX_CROP.key())
                .add(ModBlocks.STRAWBERRY_PLANT.key())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.key())
                .add(ModBlocks.BLUEBERRY_BUSH.key());

        tag(BlockTags.BEE_GROWABLES)
                .add(ModBlocks.FLAX_CROP.key())
                .add(ModBlocks.STRAWBERRY_PLANT.key())
                .add(ModBlocks.FRUITING_OAK_LEAVES.key())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.key())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.key())
                .add(ModBlocks.BLUEBERRY_BUSH.key());

        tag(BlockTags.LEAVES)
                .add(ModBlocks.FRUITING_OAK_LEAVES.key())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.key());

        tag(BlockTags.FLOWERS)
                .add(ModBlocks.FRUITING_OAK_LEAVES.key())
                .add(ModBlocks.GIANT_CACTUS_BLOSSOM.key())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.key());

        tag(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.GIANT_CACTUS_BLOSSOM.key());

        tag(BlockTags.LOGS)
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.key())
                .add(ModBlocks.GIANT_CACTUS_BASE.key())
                .add(ModBlocks.GIANT_CACTUS_WOOD.key())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.key());

        tag(BlockTags.PLANKS)
                .add(ModBlocks.CACTUS_PLANKS.key());

        tag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.CACTUS_STAIRS.key());

        tag(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.CACTUS_SLAB.key());

        tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.CACTUS_FENCE.key());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.CACTUS_FENCE_GATE.key());

        tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.CACTUS_BUTTON.key());

        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.CACTUS_PRESSURE_PLATE.key());

        tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.CACTUS_DOOR.key());

        tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.CACTUS_TRAPDOOR.key());

        tag(BlockTags.STANDING_SIGNS)
                .add(ModBlocks.CACTUS_SIGN.key());

        tag(BlockTags.WALL_SIGNS)
                .add(ModBlocks.CACTUS_WALL_SIGN.key());

        tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_HANGING_SIGN.key());

        tag(BlockTags.WALL_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_WALL_HANGING_SIGN.key());
    }
}

