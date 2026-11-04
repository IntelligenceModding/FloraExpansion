package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.NeedleLitterBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import de.artemis.floraexpansion.common.worldgen.feature.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> BRINE_SPRING_KEY = registerKey("brine_spring");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ROOTED_BRINE_SPRING_KEY = registerKey("rooted_brine_spring");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SALT_CAVE_SURFACE_KEY = registerKey("salt_cave_surface");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SALT_FORMATION_KEY = registerKey("salt_formation");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SALT_DEPOSIT_KEY = registerKey("salt_deposit");
    public static final ResourceKey<ConfiguredFeature<?, ?>> NEEDLE_LITTER_KEY = registerKey("needle_litter");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLUEBERRY_BUSH_KEY = registerKey("blueberry_bush");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_FLAX_KEY = registerKey("wild_flax");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WILD_STRAWBERRY_KEY = registerKey("wild_strawberry");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PILE_FLAX_KEY = registerKey("pile_flax");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PEBBLE_CLUSTER_KEY = registerKey("pebble_cluster");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GIANT_CACTUS_KEY = registerKey("giant_cactus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FALLEN_GIANT_CACTUS_KEY = registerKey("fallen_giant_cactus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SMALL_CACTUS_KEY = registerKey("small_cactus");
    public static final ResourceKey<ConfiguredFeature<?, ?>> PRICKLY_PEAR_CACTUS_KEY = registerKey("prickly_pear_cactus");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        register(context, BRINE_SPRING_KEY, ModFeatures.BRINE_SPRING_FEATURE.get());
        register(context, ROOTED_BRINE_SPRING_KEY, Feature.ROOT_SYSTEM, new RootSystemConfiguration(
                net.minecraft.data.worldgen.placement.PlacementUtils.inlinePlaced(context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(BRINE_SPRING_KEY)),
                // The pool validates its own basin. A second, eight-block flatness
                // test rejects almost every site in the inland cave terrain.
                3, 3, ModTags.Blocks.BRINE_SPRING_ROOT_REPLACEABLE,
                net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider.simple(ModBlocks.CRYSTALLINE_SALT.get()),
                20, 384, 1, 1,
                net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider.simple(ModBlocks.SALT_SPIKE.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN)),
                // Without solid support in the predicate, vanilla's search aborts
                // on the second air block of an intermediate cave instead of continuing up.
                1, 1, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.allOf(
                        net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.replaceable(),
                        net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.solid(Direction.DOWN.getNormal()))));
        register(context, SALT_CAVE_SURFACE_KEY, ModFeatures.SALT_CAVE_SURFACE_FEATURE.get());
        register(context, SALT_DEPOSIT_KEY, ModFeatures.SALT_DEPOSIT_FEATURE.get());
        register(context, SALT_FORMATION_KEY, ModFeatures.SALT_FORMATION_FEATURE.get());

        SimpleWeightedRandomList.Builder<BlockState> needleLitterRandomStates = SimpleWeightedRandomList.builder();
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            for (int amount = 1; amount <= 4; amount++) {
                needleLitterRandomStates.add(
                        ModBlocks.NEEDLE_LITTER.get().defaultBlockState()
                                .setValue(NeedleLitterBlock.FACING, dir)
                                .setValue(NeedleLitterBlock.AMOUNT, amount),
                        1
                );
            }
        }

        register(context, PRICKLY_PEAR_CACTUS_KEY, ModFeatures.PRICKLY_PEAR_CACTUS_FEATURE.get());

        register(context, SMALL_CACTUS_KEY, ModFeatures.SMALL_CACTUS_FEATURE.get());

        register(context, PEBBLE_CLUSTER_KEY, ModFeatures.PEBBLE_CLUSTER_FEATURE.get(), new PebbleClusterFeature.Config(1, 3));

        register(context, NEEDLE_LITTER_KEY,
                Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(new WeightedStateProvider(needleLitterRandomStates.build())));

        register(context, BLUEBERRY_BUSH_KEY, ModFeatures.BLUEBERRY_BUSH_PATCH_FEATURE.get());

        register(context, WILD_FLAX_KEY, ModFeatures.WILD_FLAX_FEATURE.get());

        register(context, WILD_STRAWBERRY_KEY, ModFeatures.WILD_STRAWBERRY_FEATURE.get());

        register(context, PILE_FLAX_KEY,
                Feature.BLOCK_PILE, new BlockPileConfiguration(new RotatedBlockProvider(ModBlocks.FLAX_BALE.get()))
        );

        register(context, GIANT_CACTUS_KEY, ModFeatures.GIANT_CACTUS_FEATURE.get(), new GiantCactusFeature.Config(4, 7));
        register(context, FALLEN_GIANT_CACTUS_KEY, ModFeatures.FALLEN_GIANT_CACTUS_FEATURE.get());
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, name)
        );
    }

    private static void register(
            BootstapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key,
            Feature<NoneFeatureConfiguration> feature
    ) {
        context.register(key, new ConfiguredFeature<>(feature, NoneFeatureConfiguration.INSTANCE));
    }

    private static <C extends FeatureConfiguration> void register(BootstapContext<ConfiguredFeature<?, ?>> context,
            ResourceKey<ConfiguredFeature<?, ?>> key, Feature<C> feature, C config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }
}
