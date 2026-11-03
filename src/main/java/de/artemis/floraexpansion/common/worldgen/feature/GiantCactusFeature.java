package de.artemis.floraexpansion.common.worldgen.feature;


import net.minecraft.world.level.levelgen.feature.Feature;

public class GiantCactusFeature extends Feature<GiantCactusFeature.Config> {

    public GiantCactusFeature() {
        super(Config.CODEC);
    }

    @Override
    public boolean place(net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<GiantCactusFeature.Config> context) {
        var level = context.level();
        var random = context.random();
        var origin = context.origin();
        return GiantCactusGenerator.generate(level, origin, random, true, context.config().minHeight(), context.config().maxHeight());
    }
    public record Config(int minHeight, int maxHeight) implements net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration {
        public static final com.mojang.serialization.Codec<Config> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.<Config>create(i -> i.group(
            com.mojang.serialization.Codec.intRange(4, 16).optionalFieldOf("min_height", 4).forGetter(Config::minHeight),
            com.mojang.serialization.Codec.intRange(4, 16).optionalFieldOf("max_height", 7).forGetter(Config::maxHeight)
        ).apply(i, Config::new)).validate(c -> c.maxHeight >= c.minHeight ? com.mojang.serialization.DataResult.success(c) : com.mojang.serialization.DataResult.error(() -> "max_height must be at least min_height"));
    }

}
