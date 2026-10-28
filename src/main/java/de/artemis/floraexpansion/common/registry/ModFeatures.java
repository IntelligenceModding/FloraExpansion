package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.worldgen.feature.BlueberryBushPatchFeature;
import de.artemis.floraexpansion.common.worldgen.feature.CactusClusterFeature;
import de.artemis.floraexpansion.common.worldgen.feature.FallenGiantCactusFeature;
import de.artemis.floraexpansion.common.worldgen.feature.GiantCactusFeature;
import de.artemis.floraexpansion.common.worldgen.feature.OpuntiaCactusFeature;
import de.artemis.floraexpansion.common.worldgen.feature.PebbleClusterFeature;
import de.artemis.floraexpansion.common.worldgen.feature.WildFlaxFeature;
import de.artemis.floraexpansion.common.worldgen.feature.WildStrawberryFeature;

import java.util.function.Supplier;

public class ModFeatures {
    public static final DeferredRegister<MapCodec<? extends Feature>> FEATURES =
            DeferredRegister.create(BuiltInRegistries.FEATURE_TYPE, FloraExpansion.MODID);

    public static final Supplier<MapCodec<WildFlaxFeature>> WILD_FLAX_FEATURE =
            FEATURES.register("wild_flax_feature",
                    () -> WildFlaxFeature.CODEC);

    public static final Supplier<MapCodec<WildStrawberryFeature>> WILD_STRAWBERRY_FEATURE =
            FEATURES.register("wild_strawberry_feature",
                    () -> WildStrawberryFeature.CODEC);

    public static final Supplier<MapCodec<BlueberryBushPatchFeature>> BLUEBERRY_BUSH_PATCH_FEATURE =
            FEATURES.register("blueberry_bush_patch_feature",
                    () -> BlueberryBushPatchFeature.CODEC);

    public static final Supplier<MapCodec<PebbleClusterFeature>> PEBBLE_CLUSTER_FEATURE =
            FEATURES.register("pebble_cluster_feature",
                    () -> PebbleClusterFeature.CODEC);

    public static final Supplier<MapCodec<GiantCactusFeature>> GIANT_CACTUS_FEATURE =
            FEATURES.register("giant_cactus_feature",
                    () -> GiantCactusFeature.CODEC);

    public static final Supplier<MapCodec<FallenGiantCactusFeature>> FALLEN_GIANT_CACTUS_FEATURE =
            FEATURES.register("fallen_giant_cactus_feature",
                    () -> FallenGiantCactusFeature.CODEC);

    public static final Supplier<MapCodec<CactusClusterFeature>> CACTUS_CLUSTER_FEATURE =
            FEATURES.register("cactus_cluster_feature",
                    () -> CactusClusterFeature.CODEC);

    public static final Supplier<MapCodec<OpuntiaCactusFeature>> OPUNTIA_CACTUS_FEATURE =
            FEATURES.register("opuntia_cactus_feature",
                    () -> OpuntiaCactusFeature.CODEC);

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}

