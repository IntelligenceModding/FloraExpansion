package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.MapCodec;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class OpuntiaCactusFeature implements Feature {
    public static final MapCodec<OpuntiaCactusFeature> CODEC = MapCodec.unit(OpuntiaCactusFeature::new);

    @Override
    public MapCodec<OpuntiaCactusFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featureOrigin) {
        BlockPos origin = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, featureOrigin);

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
                || below.is(Blocks.DYED_TERRACOTTA.white())
                || below.is(Blocks.DYED_TERRACOTTA.orange())
                || below.is(Blocks.DYED_TERRACOTTA.yellow());
    }
}
