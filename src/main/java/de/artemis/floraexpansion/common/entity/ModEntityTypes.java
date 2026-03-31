package de.artemis.floraexpansion.common.entity;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;
public final class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, FloraExpansion.MODID);

    public static final DeferredHolder<EntityType<?>, @NotNull EntityType<@NotNull CactusBoatEntity>> CACTUS_BOAT =
            ENTITY_TYPES.register("cactus_boat",
                    () -> EntityType.Builder.<CactusBoatEntity>of(CactusBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build(entityKey("cactus_boat")));

    public static final DeferredHolder<EntityType<?>, @NotNull EntityType<@NotNull CactusChestBoatEntity>> CACTUS_CHEST_BOAT =
            ENTITY_TYPES.register("cactus_chest_boat",
                    () -> EntityType.Builder.<CactusChestBoatEntity>of(CactusChestBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build(entityKey("cactus_chest_boat")));

    private ModEntityTypes() {
    }

    private static ResourceKey<EntityType<?>> entityKey(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}