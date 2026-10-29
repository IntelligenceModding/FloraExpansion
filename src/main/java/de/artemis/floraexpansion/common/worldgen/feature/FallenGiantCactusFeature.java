package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.MapCodec;

import de.artemis.floraexpansion.common.block.CactusSpineBlock;
import de.artemis.floraexpansion.common.block.DesertMossBlock;
import de.artemis.floraexpansion.common.block.GiantCactusShootBlock;
import de.artemis.floraexpansion.common.block.CactusWoodBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class FallenGiantCactusFeature implements Feature {
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    };

    public static final MapCodec<FallenGiantCactusFeature> CODEC = MapCodec.unit(FallenGiantCactusFeature::new);

    @Override
    public MapCodec<FallenGiantCactusFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featureOrigin) {
        BlockPos origin = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, featureOrigin);

        Direction direction = HORIZONTAL_DIRECTIONS[random.nextInt(HORIZONTAL_DIRECTIONS.length)];
        int length = 3 + random.nextInt(4);
        boolean woodAtFront = random.nextBoolean();

        if (!canPlaceFallenCactus(level, origin, direction, length)) {
            return false;
        }

        BlockState baseState = ModBlocks.CACTUS_LOG.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, direction.getAxis());

        BlockState woodState = ModBlocks.CACTUS_WOOD.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, direction.getAxis())
                .setValue(CactusWoodBlock.GENERATED, true);

        for (int i = 0; i < length; i++) {
            BlockPos placePos = origin.relative(direction, i);
            boolean isWood = woodAtFront ? i == 0 : i == length - 1;
            level.setBlock(placePos, isWood ? woodState : baseState, 2);
        }

        placeBodySpines(level, random, origin, direction, length);
        placeFlowers(level, random, origin, direction, length);
        placeDesertMossAround(level, random, origin, direction, length);

        return true;
    }

    private static boolean canPlaceFallenCactus(WorldGenLevel level, BlockPos start, Direction direction, int length) {
        for (int i = 0; i < length; i++) {
            BlockPos pos = start.relative(direction, i);
            BlockPos belowPos = pos.below();

            BlockState below = level.getBlockState(belowPos);
            BlockState current = level.getBlockState(pos);

            if (!below.is(Blocks.SAND) && !below.is(Blocks.RED_SAND)) {
                return false;
            }

            if (!(level.isEmptyBlock(pos) || current.canBeReplaced())) {
                return false;
            }
        }

        return true;
    }

    private static void placeBodySpines(WorldGenLevel level, RandomSource random, BlockPos start, Direction direction, int length) {
        for (int i = 0; i < length; i++) {
            BlockPos cactusPos = start.relative(direction, i);

            for (Direction side : Direction.Plane.HORIZONTAL) {
                if (side.getAxis() == direction.getAxis()) {
                    continue;
                }

                float chance = (i == 0 || i == length - 1) ? 0.14F : 0.28F;
                if (random.nextFloat() < chance) {
                    tryPlaceSpine(level, cactusPos, side);
                }
            }
        }
    }

    private static void placeFlowers(WorldGenLevel level, RandomSource random, BlockPos start, Direction direction, int length) {
        int flowerAttempts = 1 + random.nextInt(2);

        for (int i = 0; i < flowerAttempts; i++) {
            if (random.nextFloat() >= 0.55F) {
                continue;
            }

            int flowerSegment = random.nextInt(length);
            BlockPos flowerPos = start.relative(direction, flowerSegment).above();

            if (!level.isEmptyBlock(flowerPos)) {
                continue;
            }

            BlockState flower = GiantCactusShootBlock.withRandomVariant(
                    ModBlocks.GIANT_CACTUS_SHOOT.get().defaultBlockState(),
                    random
            );
            if (flower.canSurvive(level, flowerPos)) {
                level.setBlock(flowerPos, flower, 2);
            }
        }
    }

    private static void placeDesertMossAround(WorldGenLevel level, RandomSource random, BlockPos start, Direction direction, int length) {
        BlockPos center = start.relative(direction, length / 2);

        int attempts = 18 + random.nextInt(10);

        for (int i = 0; i < attempts; i++) {
            int dx = Math.round((random.nextFloat() - random.nextFloat()) * 4.0F);
            int dz = Math.round((random.nextFloat() - random.nextFloat()) * 4.0F);

            double distSq = dx * dx + dz * dz;
            if (distSq > 25.0D) {
                continue;
            }

            float skipChance;
            if (distSq <= 2.25D) {
                skipChance = 0.10F;
            } else if (distSq <= 6.25D) {
                skipChance = 0.28F;
            } else if (distSq <= 12.25D) {
                skipChance = 0.55F;
            } else {
                skipChance = 0.80F;
            }

            if (random.nextFloat() < skipChance) {
                continue;
            }

            BlockPos sample = center.offset(dx, 0, dz);
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, sample);

            tryPlaceDesertMoss(level, pos, random);
        }
    }

    private static void tryPlaceDesertMoss(WorldGenLevel level, BlockPos pos, RandomSource random) {
        if (!level.isEmptyBlock(pos)) {
            return;
        }

        BlockState below = level.getBlockState(pos.below());
        if (!isSandOrRedSand(below)) {
            return;
        }

        BlockState moss = DesertMossBlock.naturalState(random);

        if (moss.canSurvive(level, pos)) {
            level.setBlock(pos, moss, 2);
        }
    }

    private static boolean isSandOrRedSand(BlockState state) {
        return state.is(Blocks.SAND) || state.is(Blocks.RED_SAND);
    }

    private static void tryPlaceSpine(WorldGenLevel level, BlockPos cactusPos, Direction side) {
        BlockPos spinePos = cactusPos.relative(side);

        if (!level.isEmptyBlock(spinePos)) {
            return;
        }

        BlockState spineState = ModBlocks.CACTUS_SPINE.get().defaultBlockState()
                .setValue(CactusSpineBlock.FACING, side);

        if (spineState.canSurvive(level, spinePos)) {
            level.setBlock(spinePos, spineState, 2);
        }
    }
}
