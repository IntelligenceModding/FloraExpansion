package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class GiantCactusFeature implements Feature {

    private final int minHeight;
    private final int maxHeight;
    public GiantCactusFeature() { this(4, 7); }
    public GiantCactusFeature(int minHeight, int maxHeight) { this.minHeight = minHeight; this.maxHeight = maxHeight; }
    public static final MapCodec<GiantCactusFeature> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.<GiantCactusFeature>mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.intRange(4, 16).optionalFieldOf("min_height", 4).forGetter(f -> f.minHeight),
            com.mojang.serialization.Codec.intRange(4, 16).optionalFieldOf("max_height", 7).forGetter(f -> f.maxHeight)
    ).apply(instance, GiantCactusFeature::new)).validate(f -> f.maxHeight >= f.minHeight ? com.mojang.serialization.DataResult.success(f) : com.mojang.serialization.DataResult.error(() -> "max_height must be at least min_height"));

    @Override
    public MapCodec<GiantCactusFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        return GiantCactusGenerator.generate(level, origin, random, true, minHeight, maxHeight);
    }
}
