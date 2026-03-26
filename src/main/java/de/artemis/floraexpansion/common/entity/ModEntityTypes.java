package de.artemis.floraexpansion.common.entity;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, FloraExpansion.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<CactusBoatEntity>> CACTUS_BOAT =
            ENTITY_TYPES.register("cactus_boat",
                    () -> EntityType.Builder.<CactusBoatEntity>of(CactusBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("cactus_boat"));

    public static final DeferredHolder<EntityType<?>, EntityType<CactusChestBoatEntity>> CACTUS_CHEST_BOAT =
            ENTITY_TYPES.register("cactus_chest_boat",
                    () -> EntityType.Builder.<CactusChestBoatEntity>of(CactusChestBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build("cactus_chest_boat"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}