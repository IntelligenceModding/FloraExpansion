package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.LeafLitterBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.block.PineLitterBlock;
import de.artemis.floraexpansion.common.worldgen.feature.ModFeatures;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> PINE_LITTER_KEY = registerKey("pine_litter");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEAF_LITTER_KEY = registerKey("leaf_litter");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_FLAX_KEY = registerKey("wild_flax");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_CLUSTER_KEY = registerKey("pebble_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_CACTUS_KEY = registerKey("giant_cactus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FALLEN_GIANT_CACTUS_KEY = registerKey("fallen_giant_cactus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CACTUS_CLUSTER_KEY = registerKey("cactus_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OPUNTIA_CACTUS_KEY = registerKey("opuntia_cactus");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {

        SimpleWeightedRandomList.Builder<BlockState> pineLitterRandomStates = SimpleWeightedRandomList.builder();
        SimpleWeightedRandomList.Builder<BlockState> leafLitterRandomStates = SimpleWeightedRandomList.builder();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            for (int amount = 1; amount <= 4; amount++) {
                pineLitterRandomStates.add(
                        ModBlocks.PINE_LITTER.get().defaultBlockState()
                                .setValue(PineLitterBlock.FACING, dir)
                                .setValue(PineLitterBlock.AMOUNT, amount),
                        1
                );
            }
        }

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            for (int amount = 1; amount <= 4; amount++) {
                leafLitterRandomStates.add(
                        ModBlocks.LEAF_LITTER.get().defaultBlockState()
                                .setValue(LeafLitterBlock.FACING, dir)
                                .setValue(LeafLitterBlock.AMOUNT, amount),
                        1
                );
            }
        }

        register(context, OPUNTIA_CACTUS_KEY, ModFeatures.OPUNTIA_CACTUS_FEATURE.get(), NoneFeatureConfiguration.NONE);

        register(context, CACTUS_CLUSTER_KEY, ModFeatures.CACTUS_CLUSTER_FEATURE.get(), NoneFeatureConfiguration.NONE);

        register(context, PEBBLE_CLUSTER_KEY, ModFeatures.PEBBLE_CLUSTER_FEATURE.get(), NoneFeatureConfiguration.NONE);

        register(context, PINE_LITTER_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(pineLitterRandomStates.build())),
                        List.of(Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.COARSE_DIRT)
                )
        );

        register(context, LEAF_LITTER_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(new WeightedStateProvider(leafLitterRandomStates.build())),
                        List.of(Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.COARSE_DIRT)
                )
        );

        register(context, WILD_FLAX_KEY, Feature.RANDOM_PATCH,
                FeatureUtils.simplePatchConfiguration(
                        ModFeatures.WILD_FLAX_FEATURE.get(),
                        NoneFeatureConfiguration.NONE,
                        List.of(
                                Blocks.GRASS_BLOCK,
                                Blocks.DIRT,
                                Blocks.COARSE_DIRT,
                                Blocks.PODZOL,
                                Blocks.MOSS_BLOCK,
                                Blocks.ROOTED_DIRT
                        ),
                        24
                )
        );

        register(context, GIANT_CACTUS_KEY, ModFeatures.GIANT_CACTUS_FEATURE.get(), NoneFeatureConfiguration.NONE);
        register(context, FALLEN_GIANT_CACTUS_KEY, ModFeatures.FALLEN_GIANT_CACTUS_FEATURE.get(), NoneFeatureConfiguration.NONE);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, name)
        );
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
            BootstrapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            F feature,
            FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
