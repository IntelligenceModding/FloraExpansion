package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.block.CactusThornBlock;
import de.artemis.floraexpansion.common.block.GiantCactusWoodBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class GiantCactusGenerator {

    private GiantCactusGenerator() {
    }

    public static boolean canGrowFromBelow(BlockState below) {
        return below.is(BlockTags.SAND)
                || below.is(Blocks.RED_SAND)
                || below.is(Blocks.TERRACOTTA)
                || below.is(Blocks.WHITE_TERRACOTTA)
                || below.is(Blocks.ORANGE_TERRACOTTA)
                || below.is(Blocks.YELLOW_TERRACOTTA)
                || below.is(Blocks.CACTUS)
                || below.is(ModBlocks.GIANT_CACTUS_BASE.get())
                || below.is(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                || below.is(ModBlocks.GIANT_CACTUS_WOOD.get())
                || below.is(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get())
                || below.is(ModBlocks.GIANT_CACTUS_STEM.get());
    }

    public static boolean canReplace(LevelAccessor level, BlockPos pos) {
        return level.isEmptyBlock(pos) || level.getBlockState(pos).canBeReplaced();
    }

    public static boolean canGenerate(LevelAccessor level, BlockPos origin, RandomSource random) {
        GenerationPlan plan = createPlan(level, origin, random);
        if (plan == null) {
            return false;
        }

        for (BlockPos checkPos : plan.plannedBlocks) {
            // When validating from a flower block, origin is still occupied by that flower.
            // Allow that one exact position.
            if (checkPos.equals(origin)) {
                continue;
            }

            if (!canReplace(level, checkPos)) {
                return false;
            }
        }

        return true;
    }

    public static boolean generate(LevelAccessor level, BlockPos origin, RandomSource random, boolean worldgenPlaced) {
        GenerationPlan plan = createPlan(level, origin, random);
        if (plan == null) {
            return false;
        }

        for (BlockPos checkPos : plan.plannedBlocks) {
            if (!canReplace(level, checkPos)) {
                return false;
            }
        }

        BlockState verticalBase = ModBlocks.GIANT_CACTUS_BASE.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);

        BlockState verticalWood = ModBlocks.GIANT_CACTUS_WOOD.get().defaultBlockState()
                .setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y)
                .setValue(GiantCactusWoodBlock.GENERATED, worldgenPlaced);

        // Main trunk: base blocks, with wood on the very top
        for (int y = 0; y < plan.trunkHeight; y++) {
            BlockPos trunkPos = origin.above(y);

            if (y == plan.trunkHeight - 1) {
                level.setBlock(trunkPos, verticalWood, 2);
            } else {
                level.setBlock(trunkPos, verticalBase, 2);
            }
        }

        // Arms:
        // horizontal = base blocks, end cap = wood, upward section = stems
        for (ArmData arm : plan.arms) {
            BlockPos armStart = origin.above(arm.startY());
            BlockPos current = armStart;

            BlockState horizontalBase = ModBlocks.GIANT_CACTUS_BASE.get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, arm.direction().getAxis());

            BlockState horizontalWood = ModBlocks.GIANT_CACTUS_WOOD.get().defaultBlockState()
                    .setValue(RotatedPillarBlock.AXIS, arm.direction().getAxis())
                    .setValue(GiantCactusWoodBlock.GENERATED, worldgenPlaced);

            for (int i = 1; i <= arm.horizontalLength(); i++) {
                current = armStart.relative(arm.direction(), i);

                if (i == arm.horizontalLength()) {
                    level.setBlock(current, horizontalWood, 2);
                } else {
                    level.setBlock(current, horizontalBase, 2);
                }
            }

            for (int y = 1; y <= arm.verticalStemHeight(); y++) {
                level.setBlock(current.above(y), ModBlocks.GIANT_CACTUS_STEM.get().defaultBlockState(), 2);
            }
        }

        placeThorns(level, random, origin, plan.trunkHeight, plan.arms);
        placeFlowers(level, random, plan.flowerCandidates);

        return true;
    }

    private static GenerationPlan createPlan(LevelAccessor level, BlockPos origin, RandomSource random) {
        BlockPos groundPos = origin.below();
        BlockState ground = level.getBlockState(groundPos);

        if (!canGrowFromBelow(ground)) {
            return null;
        }

        int trunkHeight = 4 + random.nextInt(4); // 4-7

        int armCount = 1 + random.nextInt(2); // 1-2
        if (trunkHeight >= 6 && random.nextFloat() < 0.35F) {
            armCount = 3;
        }

        List<Direction> directions = new ArrayList<>(List.of(
                Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
        ));
        Collections.shuffle(directions, new Random(random.nextLong()));

        List<ArmData> arms = new ArrayList<>();
        int usableDirections = Math.min(armCount, directions.size());

        for (int i = 0; i < usableDirections; i++) {
            Direction dir = directions.get(i);

            // Arms only start from trunk base blocks, not too low and not at the very top
            int minStartY = Math.max(1, trunkHeight / 3);
            int maxStartY = Math.max(minStartY, trunkHeight - 2);
            int startY = minStartY + random.nextInt(maxStartY - minStartY + 1);

            int horizontalLength = 1 + random.nextInt(2);   // 1-2
            int verticalStemHeight = 2 + random.nextInt(3); // 2-4

            arms.add(new ArmData(dir, startY, horizontalLength, verticalStemHeight));
        }

        List<BlockPos> plannedBlocks = new ArrayList<>();
        List<FlowerCandidate> flowerCandidates = new ArrayList<>();

        // Main trunk positions
        for (int y = 0; y < trunkHeight; y++) {
            plannedBlocks.add(origin.above(y));
        }

        // Arm positions
        for (ArmData arm : arms) {
            BlockPos armStart = origin.above(arm.startY());
            BlockPos current = armStart;

            // Horizontal arm section
            for (int i = 1; i <= arm.horizontalLength(); i++) {
                current = armStart.relative(arm.direction(), i);
                plannedBlocks.add(current);
            }

            // Upward stem section
            for (int y = 1; y <= arm.verticalStemHeight(); y++) {
                plannedBlocks.add(current.above(y));
            }

            // Flowers only on top of some arm stems, never on main trunk
            flowerCandidates.add(new FlowerCandidate(
                    current.above(arm.verticalStemHeight() + 1),
                    current.above(arm.verticalStemHeight())
            ));
        }

        return new GenerationPlan(trunkHeight, arms, plannedBlocks, flowerCandidates);
    }

    private static void placeFlowers(LevelAccessor level, RandomSource random, List<FlowerCandidate> candidates) {
        for (FlowerCandidate candidate : candidates) {
            // Only some arm stems get flowers
            if (random.nextFloat() >= 0.55F) {
                continue;
            }

            if (!level.isEmptyBlock(candidate.flowerPos())) {
                continue;
            }

            if (!level.getBlockState(candidate.stemTopPos()).is(ModBlocks.GIANT_CACTUS_STEM.get())) {
                continue;
            }

            if (!hasHorizontalClearance(level, candidate.flowerPos())) {
                continue;
            }

            level.setBlock(candidate.flowerPos(), ModBlocks.CACTUS_FLOWER.get().defaultBlockState(), 2);
        }
    }

    private static void placeThorns(LevelAccessor level, RandomSource random, BlockPos origin, int trunkHeight, List<ArmData> arms) {
        // Thorns on main trunk
        for (int y = 0; y < trunkHeight; y++) {
            BlockPos cactusPos = origin.above(y);

            for (Direction side : Direction.Plane.HORIZONTAL) {
                float chance = y >= trunkHeight - 2 ? 0.12F : 0.24F;
                if (random.nextFloat() < chance) {
                    tryPlaceThorn(level, cactusPos, side);
                }
            }
        }

        // Thorns on arms and stem tips
        for (ArmData arm : arms) {
            BlockPos armStart = origin.above(arm.startY());
            BlockPos current = armStart;

            for (int i = 1; i <= arm.horizontalLength(); i++) {
                current = armStart.relative(arm.direction(), i);

                for (Direction side : Direction.Plane.HORIZONTAL) {
                    if (side == arm.direction().getOpposite()) {
                        continue;
                    }

                    if (random.nextFloat() < 0.20F) {
                        tryPlaceThorn(level, current, side);
                    }
                }
            }

            for (int y = 1; y <= arm.verticalStemHeight(); y++) {
                BlockPos stemPos = current.above(y);

                for (Direction side : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() < 0.16F) {
                        tryPlaceThorn(level, stemPos, side);
                    }
                }
            }
        }
    }

    private static void tryPlaceThorn(LevelAccessor level, BlockPos cactusPos, Direction side) {
        BlockPos thornPos = cactusPos.relative(side);

        if (!level.isEmptyBlock(thornPos)) {
            return;
        }

        BlockState thorn = ModBlocks.CACTUS_THORN.get().defaultBlockState()
                .setValue(CactusThornBlock.FACING, side);

        if (thorn.canSurvive(level, thornPos)) {
            level.setBlock(thornPos, thorn, 2);
        }
    }

    private static boolean hasHorizontalClearance(LevelAccessor level, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (!level.isEmptyBlock(pos.relative(dir))) {
                return false;
            }
        }
        return true;
    }

    private record ArmData(Direction direction, int startY, int horizontalLength, int verticalStemHeight) {
    }

    private record FlowerCandidate(BlockPos flowerPos, BlockPos stemTopPos) {
    }

    private record GenerationPlan(
            int trunkHeight,
            List<ArmData> arms,
            List<BlockPos> plannedBlocks,
            List<FlowerCandidate> flowerCandidates
    ) {
    }
}