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
                .add(ModBlocks.TWIG_LADDER.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.PEBBLE_BLOCK.getKey());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.CRATE.getKey())
                .add(ModBlocks.GIANT_CACTUS_BASE.getKey())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.getKey())
                .add(ModBlocks.GIANT_CACTUS_WOOD.getKey())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.getKey())
                .add(ModBlocks.GIANT_CACTUS_STEM.getKey())
                .add(ModBlocks.TWIG_LADDER.getKey())
                .add(ModBlocks.CACTUS_MOSAIC.getKey());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.PEBBLE_BLOCK.getKey());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.LINEN_BLOCK.getKey())
                .add(ModBlocks.FLAX_BALE.getKey())
                .add(ModBlocks.FRUITING_OAK_LEAVES.getKey())
                .add(ModBlocks.LINEN_CARPET.getKey())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.getKey())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.getKey())
                .add(ModBlocks.BLUEBERRY_BUSH.getKey());

        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(ModBlocks.FLAX_CROP.getKey())
                .add(ModBlocks.STRAWBERRY_PLANT.getKey());

        tag(BlockTags.CROPS)
                .add(ModBlocks.FLAX_CROP.getKey())
                .add(ModBlocks.STRAWBERRY_PLANT.getKey());

        tag(BlockTags.CANDLE_CAKES)
                .add(ModBlocks.STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.WHITE_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.ORANGE_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.MAGENTA_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.LIGHT_BLUE_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.YELLOW_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.LIME_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.PINK_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.GRAY_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.LIGHT_GRAY_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.CYAN_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.PURPLE_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.BLUE_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.BROWN_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.GREEN_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.RED_STRAWBERRY_CANDLE_CAKE.getKey())
                .add(ModBlocks.BLACK_STRAWBERRY_CANDLE_CAKE.getKey());

        tag(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.FLAX_CROP.getKey())
                .add(ModBlocks.STRAWBERRY_PLANT.getKey())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.getKey())
                .add(ModBlocks.BLUEBERRY_BUSH.getKey());

        tag(BlockTags.BEE_GROWABLES)
                .add(ModBlocks.FLAX_CROP.getKey())
                .add(ModBlocks.STRAWBERRY_PLANT.getKey())
                .add(ModBlocks.FRUITING_OAK_LEAVES.getKey())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.getKey())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.getKey())
                .add(ModBlocks.BLUEBERRY_BUSH.getKey());

        tag(BlockTags.LEAVES)
                .add(ModBlocks.FRUITING_OAK_LEAVES.getKey())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.getKey());

        tag(BlockTags.FLOWERS)
                .add(ModBlocks.FRUITING_OAK_LEAVES.getKey())
                .add(ModBlocks.GIANT_CACTUS_BLOSSOM.getKey())
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.getKey());

        tag(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.GIANT_CACTUS_BLOSSOM.getKey());

        tag(BlockTags.LOGS)
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.getKey())
                .add(ModBlocks.GIANT_CACTUS_BASE.getKey())
                .add(ModBlocks.GIANT_CACTUS_WOOD.getKey())
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.getKey());

        tag(BlockTags.PLANKS)
                .add(ModBlocks.CACTUS_PLANKS.getKey());

        tag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.CACTUS_STAIRS.getKey());

        tag(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.CACTUS_SLAB.getKey());

        tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.CACTUS_FENCE.getKey());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.CACTUS_FENCE_GATE.getKey());

        tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.CACTUS_BUTTON.getKey());

        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.CACTUS_PRESSURE_PLATE.getKey());

        tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.CACTUS_DOOR.getKey());

        tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.CACTUS_TRAPDOOR.getKey());

        tag(BlockTags.STANDING_SIGNS)
                .add(ModBlocks.CACTUS_SIGN.getKey());

        tag(BlockTags.WALL_SIGNS)
                .add(ModBlocks.CACTUS_WALL_SIGN.getKey());

        tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_HANGING_SIGN.getKey());

        tag(BlockTags.WALL_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_WALL_HANGING_SIGN.getKey());
    }
}

