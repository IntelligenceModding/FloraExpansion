package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.Codec;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.block.PebblePatchBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;

public class PebbleClusterFeature extends Feature<@NotNull NoneFeatureConfiguration> {

    public PebbleClusterFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<@NotNull NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        boolean placedAny = false;

        // radius 1..3 => diameter about 3..7
        int radius = 1 + random.nextInt(3);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double distSq = dx * dx + dz * dz;
                double radiusSq = radius * radius;

                if (distSq > radiusSq) {
                    continue;
                }

                BlockPos topPos = level.getHeightmapPos(
                        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        origin.offset(dx, 0, dz)
                );
                BlockPos groundPos = topPos.below();
                BlockPos decorationPos = groundPos.above();

                BlockState originalGround = level.getBlockState(groundPos);
                if (!isValidGround(originalGround)) {
                    continue;
                }

                double normalizedDist = Math.sqrt(distSq) / radius;

                // -----------------------------
                // 1. Replace ground
                // -----------------------------
                float replaceChance;
                if (normalizedDist <= 0.33D) {
                    replaceChance = 0.92f; // dense center
                } else if (normalizedDist <= 0.66D) {
                    replaceChance = 0.62f; // mixed middle
                } else {
                    replaceChance = 0.28f; // loose edge
                }

                boolean replacedGround = false;
                if (random.nextFloat() < replaceChance) {
                    BlockState replacement = getGroundReplacement(random, normalizedDist);
                    level.setBlock(groundPos, replacement, 2);
                    replacedGround = true;
                    placedAny = true;
                }

                if (!level.getBlockState(decorationPos).canBeReplaced()) {
                    continue;
                }

                // -----------------------------
                // 2. Main pebble decoration
                // -----------------------------
                float patchChance;
                if (normalizedDist <= 0.33D) {
                    patchChance = 0.78f;
                } else if (normalizedDist <= 0.66D) {
                    patchChance = 0.58f;
                } else {
                    patchChance = 0.30f;
                }

                if (random.nextFloat() < patchChance) {
                    int amount = getPatchAmount(random, normalizedDist);
                    Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);

                    BlockState patchState = ModBlocks.PEBBLE_PATCH.get().defaultBlockState()
                            .setValue(PebblePatchBlock.FACING, facing)
                            .setValue(PebblePatchBlock.AMOUNT, amount);

                    if (replacedGround || patchState.canSurvive(level, decorationPos)) {
                        level.setBlock(decorationPos, patchState, 2);
                        placedAny = true;
                        continue;
                    }
                }

                // -----------------------------
                // 3. Edge decoration variation
                // only rarely, mostly near the outer ring
                // -----------------------------
                if (normalizedDist > 0.50D && random.nextFloat() < 0.18f) {
                    BlockState edgeDecoration = getEdgeDecoration(random);

                    if (edgeDecoration != null && edgeDecoration.canSurvive(level, decorationPos)) {
                        level.setBlock(decorationPos, edgeDecoration, 2);
                        placedAny = true;
                    }
                }
            }
        }

        return placedAny;
    }

    private BlockState getGroundReplacement(RandomSource random, double normalizedDist) {
        int roll = random.nextInt(100);

        if (normalizedDist <= 0.33D) {
            // dense center: mostly pebble block
            if (roll < 68) return ModBlocks.PEBBLE_BLOCK.get().defaultBlockState();
            if (roll < 88) return Blocks.GRAVEL.defaultBlockState();
            if (roll < 96) return Blocks.COARSE_DIRT.defaultBlockState();
            return Blocks.ANDESITE.defaultBlockState();
        } else if (normalizedDist <= 0.66D) {
            // mixed middle
            if (roll < 42) return ModBlocks.PEBBLE_BLOCK.get().defaultBlockState();
            if (roll < 72) return Blocks.GRAVEL.defaultBlockState();
            if (roll < 90) return Blocks.COARSE_DIRT.defaultBlockState();
            return Blocks.STONE.defaultBlockState();
        } else {
            // edge transition
            if (roll < 18) return ModBlocks.PEBBLE_BLOCK.get().defaultBlockState();
            if (roll < 52) return Blocks.GRAVEL.defaultBlockState();
            if (roll < 82) return Blocks.COARSE_DIRT.defaultBlockState();
            return Blocks.STONE.defaultBlockState();
        }
    }

    private int getPatchAmount(RandomSource random, double normalizedDist) {
        if (normalizedDist <= 0.33D) {
            int roll = random.nextInt(10);
            if (roll < 1) return 2;
            if (roll < 3) return 3;
            if (roll < 6) return 4;
            return 5;
        } else if (normalizedDist <= 0.66D) {
            int roll = random.nextInt(10);
            if (roll < 2) return 1;
            if (roll < 5) return 2;
            if (roll < 8) return 3;
            return 4;
        } else {
            int roll = random.nextInt(10);
            if (roll < 5) return 1;
            if (roll < 8) return 2;
            return 3;
        }
    }

    private BlockState getEdgeDecoration(RandomSource random) {
        int roll = random.nextInt(10);

        if (roll < 6) {
            return Blocks.SHORT_GRASS.defaultBlockState();
        } else if (roll < 9) {
            return Blocks.FERN.defaultBlockState();
        } else {
            return null;
        }
    }

    private boolean isValidGround(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS_BLOCK
                || block == Blocks.DIRT
                || block == Blocks.COARSE_DIRT
                || block == Blocks.PODZOL
                || block == Blocks.GRAVEL
                || block == Blocks.STONE
                || block == Blocks.ANDESITE
                || block == Blocks.DIORITE
                || block == Blocks.GRANITE
                || block == Blocks.MOSS_BLOCK;
    }
}