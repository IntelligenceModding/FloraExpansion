package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.worldgen.treedecorator.CherryStoneLeafDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;

import java.util.List;

public class ModTreeConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> CHERRY_STONE_TEST_TREE_KEY =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "cherry_stone_test_tree")
            );

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        TreeConfiguration config = new TreeConfiguration.TreeConfigurationBuilder(
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
                .build();

        FeatureUtils.register(context, CHERRY_STONE_TEST_TREE_KEY, Feature.TREE, config);
    }
}