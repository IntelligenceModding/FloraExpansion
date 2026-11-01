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
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.SYNCED, ServerConfig.SPEC);
        LegacyContentAliases.register();
        modEventBus.addListener(ModPayloads::register);
        modEventBus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) -> event.enqueueWork(() ->
                net.minecraft.world.level.block.DispenserBlock.registerProjectileBehavior(ModItems.PEBBLES.get())));

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

