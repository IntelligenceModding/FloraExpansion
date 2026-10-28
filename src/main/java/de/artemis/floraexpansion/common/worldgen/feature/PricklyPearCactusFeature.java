package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.util.PlantSoils;
import com.mojang.serialization.MapCodec;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class PricklyPearCactusFeature implements Feature {
    public static final MapCodec<PricklyPearCactusFeature> CODEC = MapCodec.unit(PricklyPearCactusFeature::new);

    @Override
    public MapCodec<PricklyPearCactusFeature> codec() { return CODEC; }

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

            if (!PlantSoils.canPlaceCactus(level, pos)) {
                continue;
            }

            BlockState state = ModBlocks.PRICKLY_PEAR_CACTUS.get().defaultBlockState();
            if (!state.canSurvive(level, pos)) {
                continue;
            }

            level.setBlock(pos, state, 2);
            placed++;
        }

        return placed > 0;
    }

}
