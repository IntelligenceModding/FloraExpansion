package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.block.CactusThornBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class GiantCactusFeature extends Feature<NoneFeatureConfiguration> {

    public GiantCactusFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        BlockPos groundPos = origin.below();
        BlockState ground = level.getBlockState(groundPos);

        if (!(ground.is(BlockTags.SAND)
                || ground.is(net.minecraft.world.level.block.Blocks.RED_SAND)
                || ground.is(net.minecraft.world.level.block.Blocks.TERRACOTTA)
                || ground.is(net.minecraft.world.level.block.Blocks.WHITE_TERRACOTTA)
                || ground.is(net.minecraft.world.level.block.Blocks.ORANGE_TERRACOTTA)
                || ground.is(net.minecraft.world.level.block.Blocks.YELLOW_TERRACOTTA))) {
            return false;
        }

        int baseHeight = 1 + random.nextInt(2);
        int stemHeight = 2 + random.nextInt(3);

        for (int i = 0; i < baseHeight + stemHeight + 1; i++) {
            if (!level.isEmptyBlock(origin.above(i))) {
                return false;
            }
        }

        BlockState base = ModBlocks.GIANT_CACTUS_BASE.get().defaultBlockState();
        BlockState stem = ModBlocks.GIANT_CACTUS_STEM.get().defaultBlockState();
        BlockState flower = ModBlocks.CACTUS_FLOWER.get().defaultBlockState();

        for (int i = 0; i < baseHeight; i++) {
            BlockPos pos = origin.above(i);
            level.setBlock(pos, base, 2);

            for (Direction side : List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
                if (random.nextInt(3) != 0) {
                    continue;
                }

                BlockPos thornPos = pos.relative(side);
                if (!level.isEmptyBlock(thornPos)) {
                    continue;
                }

                BlockState thorn = ModBlocks.CACTUS_THORN.get().defaultBlockState()
                        .setValue(CactusThornBlock.FACING, side);

                if (thorn.canSurvive(level, thornPos)) {
                    level.setBlock(thornPos, thorn, 2);
                }
            }
        }

        for (int i = 0; i < stemHeight; i++) {
            level.setBlock(origin.above(baseHeight + i), stem, 2);
        }

        if (random.nextBoolean()) {
            level.setBlock(origin.above(baseHeight + stemHeight), flower, 2);
        }

        return true;
    }
}