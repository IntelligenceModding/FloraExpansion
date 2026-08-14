package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.Codec;
import de.artemis.floraexpansion.common.block.FlaxCropBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;

public class WildFlaxFeature extends Feature<@NotNull NoneFeatureConfiguration> {

    public WildFlaxFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<@NotNull NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();

        if (!level.isEmptyBlock(pos)) {
            return false;
        }

        int roll = random.nextInt(10);
        int age;
        if (roll == 0) age = 0;
        else if (roll <= 2) age = 1;
        else if (roll <= 5) age = 2;
        else if (roll <= 7) age = 3;
        else age = 4;

        BlockState lower = ModBlocks.FLAX_CROP.get().defaultBlockState()
                .setValue(FlaxCropBlock.WILD, true)
                .setValue(FlaxCropBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(FlaxCropBlock.AGE, age);

        if (!lower.canSurvive(level, pos)) {
            return false;
        }

        if (age >= 2) {
            BlockPos above = pos.above();

            if (!level.isEmptyBlock(above)) {
                return false;
            }

            BlockState upper = ModBlocks.FLAX_CROP.get().defaultBlockState()
                    .setValue(FlaxCropBlock.WILD, true)
                    .setValue(FlaxCropBlock.HALF, DoubleBlockHalf.UPPER)
                    .setValue(FlaxCropBlock.AGE, age);

            level.setBlock(pos, lower, 2);
            level.setBlock(above, upper, 2);
        } else {
            level.setBlock(pos, lower, 2);
        }

        return true;
    }
}
