package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.block.entity.CactusHangingSignBlockEntity;
import de.artemis.floraexpansion.common.block.entity.CactusSignBlockEntity;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.block.entity.StrawberryCakeBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, FloraExpansion.MODID);
    public static final RegistryObject<BlockEntityType<CactusSignBlockEntity>> CACTUS_SIGN =
            BLOCK_ENTITIES.register("cactus_sign",
                    () -> BlockEntityType.Builder.of(
                            CactusSignBlockEntity::new,
                            ModBlocks.CACTUS_SIGN.get(),
                            ModBlocks.CACTUS_WALL_SIGN.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<CactusHangingSignBlockEntity>> CACTUS_HANGING_SIGN =
            BLOCK_ENTITIES.register("cactus_hanging_sign",
                    () -> BlockEntityType.Builder.of(
                            CactusHangingSignBlockEntity::new,
                            ModBlocks.CACTUS_HANGING_SIGN.get(),
                            ModBlocks.CACTUS_WALL_HANGING_SIGN.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity>> HAND_MILL =
            BLOCK_ENTITIES.register("hand_mill", () -> BlockEntityType.Builder.of(
                    de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity::new, ModBlocks.HAND_MILL.get()).build(null));

    public static final RegistryObject<BlockEntityType<de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity>> HAND_CRANK =
            BLOCK_ENTITIES.register("hand_crank", () -> BlockEntityType.Builder.of(
                    de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity::new, ModBlocks.HAND_CRANK.get()).build(null));

    public static final RegistryObject<BlockEntityType<CrateBlockEntity>> CRATE =
            BLOCK_ENTITIES.register("crate",
                    () -> BlockEntityType.Builder.of(
                            CrateBlockEntity::new,
                            ModBlocks.CRATE.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity>> SALTING_TUB =
            BLOCK_ENTITIES.register("salting_tub", () -> BlockEntityType.Builder.of(
                    de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity::new, ModBlocks.SALTING_TUB.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }

    public static final RegistryObject<BlockEntityType<StrawberryCakeBlockEntity>> STRAWBERRY_CAKE =
            BLOCK_ENTITIES.register("strawberry_cake", () -> BlockEntityType.Builder.of(StrawberryCakeBlockEntity::new,
                    ModBlocks.STRAWBERRY_CANDLE_CAKES.stream().map(entry -> (net.minecraft.world.level.block.Block) entry.get())
                            .toArray(net.minecraft.world.level.block.Block[]::new)).build(null));
}
