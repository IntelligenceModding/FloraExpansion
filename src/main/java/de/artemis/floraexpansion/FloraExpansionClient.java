package de.artemis.floraexpansion;

import de.artemis.floraexpansion.client.renderer.CactusBoatRenderer;
import de.artemis.floraexpansion.common.block.entity.ModBlockEntities;
import de.artemis.floraexpansion.common.entity.ModEntityTypes;
import de.artemis.floraexpansion.common.particle.*;
import de.artemis.floraexpansion.common.util.ModWoodTypes;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = FloraExpansion.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public class FloraExpansionClient {
    public FloraExpansionClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.LEAF_FLUFF_PARTICLES.get(), LeafFluffParticles.Provider::new);
        event.registerSpriteSet(ModParticles.PINE_LEAF_FLUFF_PARTICLES.get(), PineLeafFluffParticles.Provider::new);
        event.registerSpriteSet(ModParticles.PINE_PARTICLES.get(), PineParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FLAX_FLOWER.get(), FlaxFlowerParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FALLING_CHERRY.get(), FallingFruitParticle.Provider::new);
        event.registerSpriteSet(ModParticles.FALLING_APPLE.get(), FallingFruitParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        FloraExpansion.LOGGER.info("Running cactus client setup");
        event.enqueueWork(() -> Sheets.addWoodType(ModWoodTypes.CACTUS_WOOD_TYPE));
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.CACTUS_BOAT.get(),
                context -> new CactusBoatRenderer<>(context, false));

        event.registerEntityRenderer(ModEntityTypes.CACTUS_CHEST_BOAT.get(),
                context -> new CactusBoatRenderer<>(context, true));
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        FloraExpansion.LOGGER.info("Registering cactus sign renderers");
        event.registerBlockEntityRenderer(ModBlockEntities.CACTUS_SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CACTUS_HANGING_SIGN.get(), HangingSignRenderer::new);
    }
}
