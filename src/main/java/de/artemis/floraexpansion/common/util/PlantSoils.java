package de.artemis.floraexpansion.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import de.artemis.floraexpansion.common.registry.ModTags;

/** Exact soil rules shared by placed plants and their world-generation features. */
public final class PlantSoils {
    private PlantSoils() {
    }

    public static boolean supportsCactus(BlockState state) {
        return state.is(ModTags.Blocks.CACTUS_SOILS);
    }

    public static boolean supportsBlueberry(BlockState state) {
        return state.is(ModTags.Blocks.BLUEBERRY_SOILS);
    }

    public static boolean canPlaceCactus(LevelReader level, BlockPos pos) {
        return (level.isEmptyBlock(pos) || level.getBlockState(pos).canBeReplaced())
                && supportsCactus(level.getBlockState(pos.below()));
    }
}
