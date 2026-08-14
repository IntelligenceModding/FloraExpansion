package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;

public class OpuntiaCactusFeature extends Feature<@NotNull NoneFeatureConfiguration> {
    public OpuntiaCactusFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<@NotNull NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, context.origin());

        int targetCount = random.nextFloat() < 0.72F ? 1 : 2;
        int placed = 0;

        for (int i = 0; i < 6 && placed < targetCount; i++) {
            int dx = random.nextInt(5) - 2;
            int dz = random.nextInt(5) - 2;

            BlockPos sample = origin.offset(dx, 0, dz);
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, sample);

            if (!canPlaceAt(level, pos)) {
                continue;
            }

            BlockState state = ModBlocks.OPUNTIA_CACTUS.get().defaultBlockState();
            if (!state.canSurvive(level, pos)) {
                continue;
            }

            level.setBlock(pos, state, 2);
            placed++;
        }

        return placed > 0;
    }

    private static boolean canPlaceAt(WorldGenLevel level, BlockPos pos) {
        if (!level.isEmptyBlock(pos) && !level.getBlockState(pos).canBeReplaced()) {
            return false;
        }

        BlockState below = level.getBlockState(pos.below());
        return below.is(Blocks.SAND)
                || below.is(Blocks.RED_SAND)
                || below.is(Blocks.TERRACOTTA)
                || below.is(Blocks.WHITE_TERRACOTTA)
                || below.is(Blocks.ORANGE_TERRACOTTA)
                || below.is(Blocks.YELLOW_TERRACOTTA);
    }
}
