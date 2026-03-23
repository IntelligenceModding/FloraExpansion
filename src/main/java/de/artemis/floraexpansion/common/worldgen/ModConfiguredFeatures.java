package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.LeafLitterBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.block.PineLitterBlock;
import de.artemis.floraexpansion.common.worldgen.feature.ModFeatures;
import de.artemis.floraexpansion.common.worldgen.treedecorator.CherryStoneLeafDecorator;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> PINE_LITTER_KEY = registerKey("pine_litter");
    public static final ResourceKey<ConfiguredFeature<?, ?>> LEAF_LITTER_KEY = registerKey("leaf_litter");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_FLAX_KEY = registerKey("wild_flax");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_CLUSTER_KEY = registerKey("pebble_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> CHERRY_STONE_TEST_TREE_KEY = registerKey("cherry_stone_test_tree");

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

        register(context, CHERRY_STONE_TEST_TREE_KEY, Feature.TREE,
                new TreeConfiguration.TreeConfigurationBuilder(
                        BlockStateProvider.simple(Blocks.CHERRY_LOG),
                        new CherryTrunkPlacer(
                                7,
                                1,
                                0,
                                ConstantInt.of(3),
                                ConstantInt.of(2),
                                UniformInt.of(-4, -3),
                                ConstantInt.of(-1)
                        ),
                        BlockStateProvider.simple(Blocks.CHERRY_LEAVES),
                        new CherryFoliagePlacer(
                                ConstantInt.of(4),
                                ConstantInt.of(0),
                                ConstantInt.of(5),
                                0.25F,
                                0.5F,
                                0.16666667F,
                                0.33333334F
                        ),
                        new TwoLayersFeatureSize(1, 0, 2)
                )
                        .decorators(List.of(CherryStoneLeafDecorator.INSTANCE))
                        .ignoreVines()
                        .build()
        );
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