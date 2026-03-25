package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FloweredCactusFeature extends Feature<NoneFeatureConfiguration> {

    public FloweredCactusFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        int height = getVanillaLikeHeight(random);

        if (!canPlaceFloweredCactus(level, origin, height)) {
            return false;
        }

        BlockState cactusState = Blocks.CACTUS.defaultBlockState().setValue(CactusBlock.AGE, 0);

        for (int i = 0; i < height; i++) {
            level.setBlock(origin.above(i), cactusState, 2);
        }

        BlockPos flowerPos = origin.above(height);
        level.setBlock(flowerPos, ModBlocks.CACTUS_FLOWER.get().defaultBlockState(), 2);

        return true;
    }

    private int getVanillaLikeHeight(RandomSource random) {
        // Rough vanilla cactus height distribution:
        // 1 block: 11/18
        // 2 blocks: 5/18
        // 3 blocks: 2/18
        int roll = random.nextInt(18);

        if (roll < 11) {
            return 1;
        } else if (roll < 16) {
            return 2;
        } else {
            return 3;
        }
    }

    private boolean canPlaceFloweredCactus(WorldGenLevel level, BlockPos origin, int height) {
        BlockState ground = level.getBlockState(origin.below());

        // Keep this one strict and vanilla-like
        if (!(ground.is(BlockTags.SAND) || ground.is(Blocks.RED_SAND))) {
            return false;
        }

        // Space for cactus body + flower must be empty
        for (int i = 0; i <= height; i++) {
            if (!level.isEmptyBlock(origin.above(i))) {
                return false;
            }
        }

        // Vanilla cactus side-survival style checks for every cactus block
        for (int i = 0; i < height; i++) {
            BlockPos cactusPos = origin.above(i);

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockState sideState = level.getBlockState(cactusPos.relative(dir));
                if (sideState.isSolid() || !sideState.getFluidState().isEmpty()) {
                    return false;
                }
            }
        }

        // Flower must be able to sit on top of the cactus
        BlockPos flowerPos = origin.above(height);
        return ModBlocks.CACTUS_FLOWER.get().defaultBlockState().canSurvive(level, flowerPos);
    }
}