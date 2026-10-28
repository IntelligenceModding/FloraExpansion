package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.PineLitterBlock;
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
    public static final ResourceKey<Feature> PINE_LITTER_KEY = registerKey("pine_litter");
    public static final ResourceKey<Feature> BLUEBERRY_BUSH_KEY = registerKey("blueberry_bush");
    public static final ResourceKey<Feature> WILD_FLAX_KEY = registerKey("wild_flax");
    public static final ResourceKey<Feature> WILD_STRAWBERRY_KEY = registerKey("wild_strawberry");
    public static final ResourceKey<Feature> PILE_FLAX_KEY = registerKey("pile_flax");
    public static final ResourceKey<Feature> PEBBLE_CLUSTER_KEY = registerKey("pebble_cluster");
    public static final ResourceKey<Feature> GIANT_CACTUS_KEY = registerKey("giant_cactus");
    public static final ResourceKey<Feature> FALLEN_GIANT_CACTUS_KEY = registerKey("fallen_giant_cactus");
    public static final ResourceKey<Feature> CACTUS_CLUSTER_KEY = registerKey("cactus_cluster");
    public static final ResourceKey<Feature> OPUNTIA_CACTUS_KEY = registerKey("opuntia_cactus");

    public static void bootstrap(BootstrapContext<Feature> context) {

        WeightedList.Builder<BlockState> pineLitterRandomStates = WeightedList.builder();
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

        register(context, OPUNTIA_CACTUS_KEY, new OpuntiaCactusFeature());

        register(context, CACTUS_CLUSTER_KEY, new CactusClusterFeature());

        register(context, PEBBLE_CLUSTER_KEY, new PebbleClusterFeature());

        register(context, PINE_LITTER_KEY,
                new SimpleBlockFeature(new WeightedStateProvider(pineLitterRandomStates.build())));

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

