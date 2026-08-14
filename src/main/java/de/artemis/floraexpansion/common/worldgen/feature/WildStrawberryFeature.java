package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.Codec;
import de.artemis.floraexpansion.common.block.StrawberryCropBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class WildStrawberryFeature extends Feature<NoneFeatureConfiguration> {
    public WildStrawberryFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

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
