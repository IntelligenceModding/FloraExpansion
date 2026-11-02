package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.worldgen.treedecorator.OrchardCherryLeavesDecorator;
import de.artemis.floraexpansion.common.worldgen.treedecorator.AppleLeavesDecorator;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.CherryFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.trunkplacers.CherryTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

import java.util.List;

public class ModTreeConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORCHARD_CHERRY_TREE_KEY =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "orchard_cherry_tree")
            );

    public static final ResourceKey<ConfiguredFeature<?, ?>> APPLE_TREE_KEY =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "apple_tree")
            );

    public static final ResourceKey<ConfiguredFeature<?, ?>> FANCY_APPLE_TREE_KEY =
            ResourceKey.create(
                    Registries.CONFIGURED_FEATURE,
                    Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "fancy_apple_tree")
            );

    public static final ResourceKey<ConfiguredFeature<?, ?>> PLANTED_APPLE_TREE_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "planted_apple_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> PLANTED_FANCY_APPLE_TREE_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "planted_fancy_apple_tree"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> PLANTED_ORCHARD_CHERRY_TREE_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "planted_orchard_cherry_tree"));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        var orchardCherryBuilder = new TreeConfiguration.TreeConfigurationBuilder(
                SimpleStateProvider.simple(Blocks.CHERRY_LOG.defaultBlockState()),
                new CherryTrunkPlacer(
                        7,
                        1,
                        0,
                        ConstantInt.of(3),
                        ConstantInt.of(2),
                        UniformInt.of(-4, -3),
                        ConstantInt.of(-1)
                ),
                SimpleStateProvider.simple(Blocks.CHERRY_LEAVES.defaultBlockState()),
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
                .decorators(List.of(OrchardCherryLeavesDecorator.INSTANCE))
                .dirt(SimpleStateProvider.simple(Blocks.DIRT)).ignoreVines();

        context.register(ORCHARD_CHERRY_TREE_KEY, new ConfiguredFeature<>(Feature.TREE, orchardCherryBuilder.build()));
        context.register(PLANTED_ORCHARD_CHERRY_TREE_KEY, new ConfiguredFeature<>(Feature.TREE, orchardCherryBuilder.decorators(List.of(new OrchardCherryLeavesDecorator(2, Integer.MAX_VALUE, false))).build()));

        var appleBuilder = new TreeConfiguration.TreeConfigurationBuilder(
                SimpleStateProvider.simple(Blocks.OAK_LOG.defaultBlockState()),
                new StraightTrunkPlacer(4, 2, 0),
                SimpleStateProvider.simple(Blocks.OAK_LEAVES.defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1)
        )
                .decorators(List.of(AppleLeavesDecorator.INSTANCE))
                .dirt(SimpleStateProvider.simple(Blocks.DIRT)).ignoreVines();

        context.register(APPLE_TREE_KEY, new ConfiguredFeature<>(Feature.TREE, appleBuilder.build()));
        context.register(PLANTED_APPLE_TREE_KEY, new ConfiguredFeature<>(Feature.TREE, appleBuilder.decorators(List.of(new AppleLeavesDecorator(2, Integer.MAX_VALUE, false))).build()));

        var fancyAppleBuilder = new TreeConfiguration.TreeConfigurationBuilder(
                SimpleStateProvider.simple(Blocks.OAK_LOG.defaultBlockState()),
                new FancyTrunkPlacer(3, 11, 0),
                SimpleStateProvider.simple(Blocks.OAK_LEAVES.defaultBlockState()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4), 4),
                new TwoLayersFeatureSize(0, 0, 0)
        )
                .decorators(List.of(AppleLeavesDecorator.INSTANCE))
                .dirt(SimpleStateProvider.simple(Blocks.DIRT)).ignoreVines();

        context.register(FANCY_APPLE_TREE_KEY, new ConfiguredFeature<>(Feature.TREE, fancyAppleBuilder.build()));
        context.register(PLANTED_FANCY_APPLE_TREE_KEY, new ConfiguredFeature<>(Feature.TREE, fancyAppleBuilder.decorators(List.of(new AppleLeavesDecorator(2, Integer.MAX_VALUE, false))).build()));
    }
}
