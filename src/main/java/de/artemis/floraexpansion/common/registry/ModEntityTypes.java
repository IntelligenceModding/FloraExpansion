package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.projectile.SpruceConeProjectile;
import de.artemis.floraexpansion.common.projectile.PebblesProjectile;
import de.artemis.floraexpansion.common.projectile.ResourceProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.entity.CactusBoatEntity;
import de.artemis.floraexpansion.common.entity.CactusChestBoatEntity;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, FloraExpansion.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<CactusBoatEntity>> CACTUS_BOAT =
            ENTITY_TYPES.register("cactus_boat",
                    () -> EntityType.Builder.<CactusBoatEntity>of(CactusBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build(key("cactus_boat")));

    public static final DeferredHolder<EntityType<?>, EntityType<CactusChestBoatEntity>> CACTUS_CHEST_BOAT =
            ENTITY_TYPES.register("cactus_chest_boat",
                    () -> EntityType.Builder.<CactusChestBoatEntity>of(CactusChestBoatEntity::new, MobCategory.MISC)
                            .sized(1.375F, 0.5625F)
                            .clientTrackingRange(10)
                            .updateInterval(1)
                            .build(key("cactus_chest_boat")));

    public static final DeferredHolder<EntityType<?>, EntityType<SpruceConeProjectile>> SPRUCE_CONE =
            registerProjectile("spruce_cone", SpruceConeProjectile::new);

    public static final DeferredHolder<EntityType<?>, EntityType<PebblesProjectile>> PEBBLES =
            registerProjectile("pebbles", PebblesProjectile::new);

    private static <T extends ResourceProjectile> DeferredHolder<EntityType<?>, EntityType<T>> registerProjectile(
            String name, EntityType.EntityFactory<T> factory) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, MobCategory.MISC)
                .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build(key(name)));
    }

    private static ResourceKey<EntityType<?>> key(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name));
    }

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
