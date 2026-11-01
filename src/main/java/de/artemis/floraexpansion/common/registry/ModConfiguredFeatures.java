package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.NeedleLitterBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.BlockPileFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.core.Holder;
import de.artemis.floraexpansion.common.worldgen.feature.*;
import net.minecraft.world.level.levelgen.feature.stateproviders.RotatedBlockProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public class ModConfiguredFeatures {
    public static final ResourceKey<Feature> BRINE_SPRING_KEY = registerKey("brine_spring");
    public static final ResourceKey<Feature> ROOTED_BRINE_SPRING_KEY = registerKey("rooted_brine_spring");
    public static final ResourceKey<Feature> SALT_CAVE_SURFACE_KEY = registerKey("salt_cave_surface");
    public static final ResourceKey<Feature> SALT_FORMATION_KEY = registerKey("salt_formation");
    public static final ResourceKey<Feature> SALT_DEPOSIT_KEY = registerKey("salt_deposit");
    public static final ResourceKey<Feature> NEEDLE_LITTER_KEY = registerKey("needle_litter");
    public static final ResourceKey<Feature> BLUEBERRY_BUSH_KEY = registerKey("blueberry_bush");
    public static final ResourceKey<Feature> WILD_FLAX_KEY = registerKey("wild_flax");
    public static final ResourceKey<Feature> WILD_STRAWBERRY_KEY = registerKey("wild_strawberry");
    public static final ResourceKey<Feature> PILE_FLAX_KEY = registerKey("pile_flax");
    public static final ResourceKey<Feature> PEBBLE_CLUSTER_KEY = registerKey("pebble_cluster");
    public static final ResourceKey<Feature> GIANT_CACTUS_KEY = registerKey("giant_cactus");
    public static final ResourceKey<Feature> FALLEN_GIANT_CACTUS_KEY = registerKey("fallen_giant_cactus");
    public static final ResourceKey<Feature> SMALL_CACTUS_KEY = registerKey("small_cactus");
    public static final ResourceKey<Feature> PRICKLY_PEAR_CACTUS_KEY = registerKey("prickly_pear_cactus");

    public static void bootstrap(BootstrapContext<Feature> context) {
        register(context, BRINE_SPRING_KEY, new BrineSpringFeature());
        register(context, ROOTED_BRINE_SPRING_KEY, new net.minecraft.world.level.levelgen.feature.RootSystemFeature(
                net.minecraft.data.worldgen.placement.PlacementUtils.inlinePlaced(context.lookup(Registries.FEATURE).getOrThrow(BRINE_SPRING_KEY)),
                // The pool validates its own basin. A second, eight-block flatness
                // test rejects almost every site in the inland cave terrain.
                3, 0, 2, 3, context.lookup(Registries.BLOCK).getOrThrow(ModTags.Blocks.BRINE_SPRING_ROOT_REPLACEABLE),
                net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider.holderOf(ModBlocks.CRYSTALLINE_SALT.get()),
                20, 384, 1, 1,
                net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider.holderOf(ModBlocks.SALT_SPIKE.get().defaultBlockState()
                        .setValue(net.minecraft.world.level.block.SpeleothemBlock.TIP_DIRECTION, Direction.DOWN)),
                // Without solid support in the predicate, vanilla's search aborts
                // on the second air block of an intermediate cave instead of continuing up.
                1, 1, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.allOf(
                        net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.replaceable(),
                        net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate.solid(Direction.DOWN))));
        register(context, SALT_CAVE_SURFACE_KEY, new SaltCaveSurfaceFeature());
        register(context, SALT_DEPOSIT_KEY, new SaltDepositFeature());
        register(context, SALT_FORMATION_KEY, new SaltFormationFeature());

        WeightedList.Builder<BlockState> needleLitterRandomStates = WeightedList.builder();
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

        register(context, PRICKLY_PEAR_CACTUS_KEY, new PricklyPearCactusFeature());

        register(context, SMALL_CACTUS_KEY, new SmallCactusFeature());

        register(context, PEBBLE_CLUSTER_KEY, new PebbleClusterFeature());

        register(context, NEEDLE_LITTER_KEY,
                new SimpleBlockFeature(new WeightedStateProvider(needleLitterRandomStates.build())));

        register(context, BLUEBERRY_BUSH_KEY, new BlueberryBushPatchFeature());

        register(context, WILD_FLAX_KEY, new WildFlaxFeature());

        register(context, WILD_STRAWBERRY_KEY, new WildStrawberryFeature());

        register(context, PILE_FLAX_KEY,
                new BlockPileFeature(Holder.direct(new RotatedBlockProvider(new SimpleStateProvider(ModBlocks.FLAX_BALE.get().defaultBlockState()))))
        );

        register(context, GIANT_CACTUS_KEY, new GiantCactusFeature());
        register(context, FALLEN_GIANT_CACTUS_KEY, new FallenGiantCactusFeature());
    }

    public static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(
                Registries.FEATURE,
                Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name)
        );
    }

    private static void register(
            BootstrapContext<Feature> context,
            ResourceKey<Feature> key,
            Feature feature
    ) {
        context.register(key, feature);
    }
}


