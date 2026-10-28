package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.MapCodec;

import de.artemis.floraexpansion.common.block.StrawberryCropBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WildStrawberryFeature implements Feature {
    public static final MapCodec<WildStrawberryFeature> CODEC = MapCodec.unit(WildStrawberryFeature::new);

    @Override
    public MapCodec<WildStrawberryFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos featureOrigin) {
        BlockPos pos = featureOrigin;

        if (!level.isEmptyBlock(pos)) {
            return false;
        }

        int roll = random.nextInt(10);
        int age;
        if (roll <= 1) {
            age = 0;
        } else if (roll <= 4) {
            age = 1;
        } else if (roll <= 7) {
            age = 2;
        } else {
            age = 3;
        }

        BlockState state = ModBlocks.STRAWBERRY_PLANT.get().defaultBlockState()
                .setValue(StrawberryCropBlock.WILD, true)
                .setValue(StrawberryCropBlock.AGE, age);

        if (!state.canSurvive(level, pos)) {
            return false;
        }

        level.setBlock(pos, state, 2);
        return true;
    }
}
