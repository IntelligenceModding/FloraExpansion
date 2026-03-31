package de.artemis.floraexpansion.common.block.entity;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FloraExpansion.MODID);

    public static final DeferredHolder<BlockEntityType<?>, @NotNull BlockEntityType<@NotNull CactusSignBlockEntity>> CACTUS_SIGN =
            BLOCK_ENTITIES.register("cactus_sign",
                    () -> new BlockEntityType<>(
                            CactusSignBlockEntity::new,
                            false,
                            ModBlocks.CACTUS_SIGN.get(),
                            ModBlocks.CACTUS_WALL_SIGN.get()
                    ));

    public static final DeferredHolder<BlockEntityType<?>, @NotNull BlockEntityType<@NotNull CactusHangingSignBlockEntity>> CACTUS_HANGING_SIGN =
            BLOCK_ENTITIES.register("cactus_hanging_sign",
                    () -> new BlockEntityType<>(
                            CactusHangingSignBlockEntity::new,
                            false,
                            ModBlocks.CACTUS_HANGING_SIGN.get(),
                            ModBlocks.CACTUS_WALL_HANGING_SIGN.get()
                    ));

    private ModBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}