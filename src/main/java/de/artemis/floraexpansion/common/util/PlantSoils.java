package de.artemis.floraexpansion.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Exact soil rules shared by placed plants and their world-generation features. */
public final class PlantSoils {
    private PlantSoils() {
    }

    public static boolean supportsCactus(BlockState state) {
        return state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.TERRACOTTA)
                || state.is(Blocks.DYED_TERRACOTTA.white()) || state.is(Blocks.DYED_TERRACOTTA.orange())
                || state.is(Blocks.DYED_TERRACOTTA.yellow());
    }

    public static boolean supportsBlueberry(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(BlockTags.GRASS_BLOCKS) || state.is(Blocks.PODZOL)
                || state.is(Blocks.MYCELIUM) || state.is(Blocks.FARMLAND) || state.is(Blocks.MOSS_BLOCK)
                || state.is(Blocks.ROOTED_DIRT);
    }

    public static boolean canPlaceCactus(LevelReader level, BlockPos pos) {
        return (level.isEmptyBlock(pos) || level.getBlockState(pos).canBeReplaced())
                && supportsCactus(level.getBlockState(pos.below()));
    }
}
