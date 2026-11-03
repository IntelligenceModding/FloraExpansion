package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.migration.LegacyContentAliases;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModLootModifiers;
import de.artemis.floraexpansion.common.registry.ModMenuTypes;
import de.artemis.floraexpansion.common.registry.ModParticles;
import de.artemis.floraexpansion.common.registry.ModFeatures;
import de.artemis.floraexpansion.common.registry.ModTreeDecorators;
import de.artemis.floraexpansion.common.network.ModPayloads;
import org.slf4j.Logger;
import de.artemis.floraexpansion.common.registry.ModCreativeModeTabs;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import de.artemis.floraexpansion.common.config.ServerConfig;

@Mod(FloraExpansion.MODID)
public class FloraExpansion {
    public static final String MODID = "floraexpansion";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FloraExpansion(net.neoforged.fml.ModContainer container, IEventBus modEventBus) {
        // Register explicitly so older 1.21.1 loaders also use the correct bus.
        modEventBus.register(de.artemis.floraexpansion.common.datagen.DataGenerators.class);
        modEventBus.register(de.artemis.floraexpansion.common.event.SaltWaterInteractions.class);
        modEventBus.register(de.artemis.floraexpansion.common.event.WoodenBucketCauldronInteractions.class);
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.SERVER, ServerConfig.SPEC);
        LegacyContentAliases.register();
        modEventBus.addListener(ModPayloads::register);
        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) -> event.enqueueWork(() ->
                net.minecraft.world.level.block.DispenserBlock.registerProjectileBehavior(ModItems.PEBBLES.get())));

        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) -> event.enqueueWork(() -> {
            var pot = (net.minecraft.world.level.block.FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT;
            pot.addPlant(ModBlocks.APPLE_CORE.getId(), ModBlocks.POTTED_APPLE_CORE);
            pot.addPlant(ModBlocks.CHERRY_PIT.getId(), ModBlocks.POTTED_CHERRY_PIT);
            pot.addPlant(ModBlocks.GIANT_CACTUS_STEM.getId(), ModBlocks.POTTED_GIANT_CACTUS_STEM);
            pot.addPlant(ModBlocks.GLASSWORT.getId(), ModBlocks.POTTED_GLASSWORT);
        }));
        de.artemis.floraexpansion.common.registry.ModArmorMaterials.register(modEventBus);
        ModItems.register(modEventBus);
        de.artemis.floraexpansion.common.advancement.FloraActionTrigger.register(modEventBus);
        de.artemis.floraexpansion.common.registry.ModFluids.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModParticles.register(modEventBus);
        de.artemis.floraexpansion.common.registry.ModSounds.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModTreeDecorators.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        de.artemis.floraexpansion.common.registry.ModRecipes.register(modEventBus);
    }

}
