package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class GiantCactusFeature implements Feature {

    public static final MapCodec<GiantCactusFeature> CODEC = MapCodec.unit(GiantCactusFeature::new);

    @Override
    public MapCodec<GiantCactusFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        return GiantCactusGenerator.generate(level, origin, random, true);
    }
}
