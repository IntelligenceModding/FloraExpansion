package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.block.entity.CactusHangingSignBlockEntity;
import de.artemis.floraexpansion.common.block.entity.CactusSignBlockEntity;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, FloraExpansion.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CactusSignBlockEntity>> CACTUS_SIGN =
            BLOCK_ENTITIES.register("cactus_sign",
                    () -> new BlockEntityType<>(
                            CactusSignBlockEntity::new,
                            ModBlocks.CACTUS_SIGN.get(),
                            ModBlocks.CACTUS_WALL_SIGN.get()
                    ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CactusHangingSignBlockEntity>> CACTUS_HANGING_SIGN =
            BLOCK_ENTITIES.register("cactus_hanging_sign",
                    () -> new BlockEntityType<>(
                            CactusHangingSignBlockEntity::new,
                            ModBlocks.CACTUS_HANGING_SIGN.get(),
                            ModBlocks.CACTUS_WALL_HANGING_SIGN.get()
                    ));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrateBlockEntity>> CRATE =
            BLOCK_ENTITIES.register("crate",
                    () -> new BlockEntityType<>(
                            CrateBlockEntity::new,
                            ModBlocks.CRATE.get()
                    ));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}


