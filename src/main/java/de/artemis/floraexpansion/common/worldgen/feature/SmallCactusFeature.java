package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.util.PlantSoils;
import com.mojang.serialization.MapCodec;

import de.artemis.floraexpansion.common.block.SmallCactusBlock;
import de.artemis.floraexpansion.common.block.DesertMossBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class SmallCactusFeature implements Feature {
    public static final MapCodec<SmallCactusFeature> CODEC = MapCodec.unit(SmallCactusFeature::new);

    @Override
    public MapCodec<SmallCactusFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featureOrigin) {
        BlockPos origin = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, featureOrigin);

        int placed = 0;
        int attempts = 14 + random.nextInt(9);

        for (int i = 0; i < attempts; i++) {
            int dx = Math.round((random.nextFloat() - random.nextFloat()) * 4.0F);
            int dz = Math.round((random.nextFloat() - random.nextFloat()) * 4.0F);

            double distSq = dx * dx + dz * dz;
            if (distSq > 20.25D) {
                continue;
            }

            float skipChance;
            if (distSq <= 1.0D) {
                skipChance = 0.22F;
            } else if (distSq <= 4.0D) {
                skipChance = 0.36F;
            } else if (distSq <= 9.0D) {
                skipChance = 0.56F;
            } else {
                skipChance = 0.74F;
            }

            if (random.nextFloat() < skipChance) {
                continue;
            }

            BlockPos sample = origin.offset(dx, 0, dz);
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, sample);

            if (!PlantSoils.canPlaceCactus(level, pos)) {
                continue;
            }

            float mossChance;
            if (distSq <= 1.0D) {
                mossChance = 0.14F;
            } else if (distSq <= 4.0D) {
                mossChance = 0.24F;
            } else if (distSq <= 9.0D) {
                mossChance = 0.36F;
            } else {
                mossChance = 0.48F;
            }

            if (random.nextFloat() < mossChance) {
                if (tryPlaceDesertMoss(level, pos, random)) {
                    placed++;
                }
                continue;
            }

            float pricklyPearChance;
            if (distSq <= 1.0D) {
                pricklyPearChance = 0.08F;
            } else if (distSq <= 4.0D) {
                pricklyPearChance = 0.18F;
            } else if (distSq <= 9.0D) {
                pricklyPearChance = 0.28F;
            } else {
                pricklyPearChance = 0.34F;
            }

            if (random.nextFloat() < pricklyPearChance) {
                if (tryPlacePricklyPearCactus(level, pos)) {
                    placed++;
                }
                continue;
            }

            int amount = pickClusterAmount(random, distSq);

            BlockState state = ModBlocks.SMALL_CACTUS.get().defaultBlockState()
                    .setValue(SmallCactusBlock.PICKLES, amount);

            if (!state.canSurvive(level, pos)) {
                continue;
            }

            level.setBlock(pos, state, 2);
            placed++;
        }

        return placed > 0;
    }

    private static boolean tryPlaceDesertMoss(WorldGenLevel level, BlockPos pos, RandomSource random) {
        BlockState moss = ModBlocks.DESERT_MOSS.get().defaultBlockState();

        if (moss.hasProperty(DesertMossBlock.VARIANT)) {
            moss = moss.setValue(DesertMossBlock.VARIANT, random.nextInt(4));
        }

        if (!moss.canSurvive(level, pos)) {
            return false;
        }

        level.setBlock(pos, moss, 2);
        return true;
    }

    private static boolean tryPlacePricklyPearCactus(WorldGenLevel level, BlockPos pos) {
        BlockState state = ModBlocks.PRICKLY_PEAR_CACTUS.get().defaultBlockState();

        if (!state.canSurvive(level, pos)) {
            return false;
        }

        level.setBlock(pos, state, 2);
        return true;
    }

    private static int pickClusterAmount(RandomSource random, double distSq) {
        float roll = random.nextFloat();

        if (distSq <= 1.0D) {
            if (roll < 0.12F) return 4;
            if (roll < 0.34F) return 3;
            if (roll < 0.72F) return 2;
            return 1;
        }

        if (distSq <= 4.0D) {
            if (roll < 0.06F) return 4;
            if (roll < 0.22F) return 3;
            if (roll < 0.58F) return 2;
            return 1;
        }

        if (distSq <= 9.0D) {
            if (roll < 0.03F) return 4;
            if (roll < 0.12F) return 3;
            if (roll < 0.42F) return 2;
            return 1;
        }

        if (roll < 0.02F) return 4;
        if (roll < 0.08F) return 3;
        if (roll < 0.26F) return 2;
        return 1;
    }
}
