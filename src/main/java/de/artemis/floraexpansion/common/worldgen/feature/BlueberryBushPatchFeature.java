package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.Codec;
import de.artemis.floraexpansion.common.block.LargeBlueberryBushBlock;
import de.artemis.floraexpansion.common.block.SmallBlueberryBushBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class BlueberryBushPatchFeature extends Feature<NoneFeatureConfiguration> {
    private static final int TRIES = 32;
    private static final int XZ_SPREAD = 3;
    private static final int Y_SPREAD = 1;
    private static final int LARGE_BUSH_CHANCE = 10;

    public BlueberryBushPatchFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos patchOrigin = context.origin().above();
        boolean placedAny = false;

        for (int i = 0; i < TRIES; i++) {
            int dx = random.nextInt(XZ_SPREAD * 2 + 1) - XZ_SPREAD;
            int dz = random.nextInt(XZ_SPREAD * 2 + 1) - XZ_SPREAD;
            int distanceSq = dx * dx + dz * dz;

            if (distanceSq > XZ_SPREAD * XZ_SPREAD + 1 && random.nextBoolean()) {
                continue;
            }

            BlockPos pos = patchOrigin.offset(
                    dx,
                    random.nextInt(Y_SPREAD * 2 + 1) - Y_SPREAD,
                    dz
            );

            if (!level.isEmptyBlock(pos)) {
                continue;
            }

            BlockPos soilPos = pos.below();
            BlockState soilState = level.getBlockState(soilPos);
            if (!isValidGround(soilState)) {
                continue;
            }

            BlockState bushState = pickBushState(random);
            if (bushState.is(ModBlocks.LARGE_BLUEBERRY_BUSH.get())) {
                level.setBlock(soilPos, Blocks.ROOTED_DIRT.defaultBlockState(), 2);
            }

            if (!bushState.canSurvive(level, pos)) {
                continue;
            }

            level.setBlock(pos, bushState, 2);
            placedAny = true;
        }

        return placedAny;
    }

    private static boolean isValidGround(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(Blocks.ROOTED_DIRT);
    }

    private static BlockState pickBushState(RandomSource random) {
        if (random.nextInt(LARGE_BUSH_CHANCE) == 0) {
            int rootedAge = random.nextInt(6) == 0 ? 3 : (random.nextBoolean() ? 2 : 1);
            return ModBlocks.LARGE_BLUEBERRY_BUSH.get().defaultBlockState().setValue(LargeBlueberryBushBlock.AGE, rootedAge);
        }

        int roll = random.nextInt(14);
        int age;
        if (roll < 2) {
            age = 0;
        } else if (roll < 7) {
            age = 1;
        } else if (roll < 13) {
            age = 2;
        } else {
            age = 3;
        }

        return ModBlocks.BLUEBERRY_BUSH.get().defaultBlockState().setValue(SmallBlueberryBushBlock.AGE, age);
    }
}
