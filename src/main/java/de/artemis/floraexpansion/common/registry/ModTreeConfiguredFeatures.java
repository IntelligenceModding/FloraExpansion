package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.worldgen.treedecorator.FruitingCherryLeavesDecorator;
import de.artemis.floraexpansion.common.worldgen.treedecorator.FruitingOakLeavesDecorator;
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
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.List;

public class ModTreeConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> FRUITING_CHERRY_TREE_KEY =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "fruiting_cherry_tree")
            );

    public static final ResourceKey<ConfiguredFeature<?, ?>> FRUITING_OAK_TREE_KEY =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "fruiting_oak_tree")
            );

    public static final ResourceKey<ConfiguredFeature<?, ?>> FANCY_FRUITING_OAK_TREE_KEY =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "fancy_fruiting_oak_tree")
            );

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        TreeConfiguration fruitingCherryConfig = new TreeConfiguration.TreeConfigurationBuilder(
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
                .decorators(List.of(FruitingCherryLeavesDecorator.INSTANCE))
                .ignoreVines()
                .build();

        FeatureUtils.register(context, FRUITING_CHERRY_TREE_KEY, Feature.TREE, fruitingCherryConfig);

        TreeConfiguration fruitingOakConfig = new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(Blocks.OAK_LEAVES),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        )
                .decorators(List.of(FruitingOakLeavesDecorator.INSTANCE))
                .ignoreVines()
                .build();

        FeatureUtils.register(context, FRUITING_OAK_TREE_KEY, Feature.TREE, fruitingOakConfig);

        TreeConfiguration fancyFruitingOakConfig = new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(Blocks.OAK_LOG),
                new FancyTrunkPlacer(3, 11, 0),
                BlockStateProvider.simple(Blocks.OAK_LEAVES),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0)
        )
                .decorators(List.of(FruitingOakLeavesDecorator.INSTANCE))
                .ignoreVines()
                .build();

        FeatureUtils.register(context, FANCY_FRUITING_OAK_TREE_KEY, Feature.TREE, fancyFruitingOakConfig);
    }
}
