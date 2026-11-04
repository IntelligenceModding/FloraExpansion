package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.worldgen.feature.BlueberryBushPatchFeature;
import de.artemis.floraexpansion.common.worldgen.feature.SmallCactusFeature;
import de.artemis.floraexpansion.common.worldgen.feature.FallenGiantCactusFeature;
import de.artemis.floraexpansion.common.worldgen.feature.GiantCactusFeature;
import de.artemis.floraexpansion.common.worldgen.feature.PricklyPearCactusFeature;
import de.artemis.floraexpansion.common.worldgen.feature.PebbleClusterFeature;
import de.artemis.floraexpansion.common.worldgen.feature.WildFlaxFeature;
import de.artemis.floraexpansion.common.worldgen.feature.WildStrawberryFeature;

import java.util.function.Supplier;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.FEATURE, FloraExpansion.MODID);
    public static final Supplier<de.artemis.floraexpansion.common.worldgen.feature.BrineSpringFeature> BRINE_SPRING_FEATURE =
            FEATURES.register("brine_spring_feature", de.artemis.floraexpansion.common.worldgen.feature.BrineSpringFeature::new);
    public static final Supplier<de.artemis.floraexpansion.common.worldgen.feature.SaltCaveSurfaceFeature> SALT_CAVE_SURFACE_FEATURE =
            FEATURES.register("salt_cave_surface_feature", de.artemis.floraexpansion.common.worldgen.feature.SaltCaveSurfaceFeature::new);
    public static final Supplier<de.artemis.floraexpansion.common.worldgen.feature.SaltDepositFeature> SALT_DEPOSIT_FEATURE =
            FEATURES.register("salt_deposit_feature", de.artemis.floraexpansion.common.worldgen.feature.SaltDepositFeature::new);

    public static final Supplier<de.artemis.floraexpansion.common.worldgen.feature.SaltFormationFeature> SALT_FORMATION_FEATURE =
            FEATURES.register("salt_formation_feature", de.artemis.floraexpansion.common.worldgen.feature.SaltFormationFeature::new);

    public static final Supplier<WildFlaxFeature> WILD_FLAX_FEATURE =
            FEATURES.register("wild_flax_feature",
                    WildFlaxFeature::new);

    public static final Supplier<WildStrawberryFeature> WILD_STRAWBERRY_FEATURE =
            FEATURES.register("wild_strawberry_feature",
                    WildStrawberryFeature::new);

    public static final Supplier<BlueberryBushPatchFeature> BLUEBERRY_BUSH_PATCH_FEATURE =
            FEATURES.register("blueberry_bush_patch_feature",
                    BlueberryBushPatchFeature::new);

    public static final Supplier<PebbleClusterFeature> PEBBLE_CLUSTER_FEATURE =
            FEATURES.register("pebble_cluster_feature",
                    PebbleClusterFeature::new);

    public static final Supplier<GiantCactusFeature> GIANT_CACTUS_FEATURE =
            FEATURES.register("giant_cactus_feature",
                    GiantCactusFeature::new);

    public static final Supplier<FallenGiantCactusFeature> FALLEN_GIANT_CACTUS_FEATURE =
            FEATURES.register("fallen_giant_cactus_feature",
                    FallenGiantCactusFeature::new);

    public static final Supplier<SmallCactusFeature> SMALL_CACTUS_FEATURE =
            FEATURES.register("small_cactus_feature",
                    SmallCactusFeature::new);

    public static final Supplier<PricklyPearCactusFeature> PRICKLY_PEAR_CACTUS_FEATURE =
            FEATURES.register("prickly_pear_cactus_feature",
                    PricklyPearCactusFeature::new);

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}
