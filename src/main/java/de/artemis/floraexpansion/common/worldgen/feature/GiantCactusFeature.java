package de.artemis.floraexpansion.common.worldgen.feature;

import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.NotNull;

public class GiantCactusFeature extends Feature<@NotNull NoneFeatureConfiguration> {

    public GiantCactusFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<@NotNull NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        return GiantCactusGenerator.generate(level, context.origin(), context.random(), true);
    }
}