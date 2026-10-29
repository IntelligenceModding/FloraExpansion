package de.artemis.floraexpansion;

import de.artemis.floraexpansion.client.renderer.CactusBoatRenderer;
import de.artemis.floraexpansion.client.renderer.CrateBlockEntityRenderer;
import de.artemis.floraexpansion.client.renderer.StrawberryCakeBlockEntityRenderer;
import de.artemis.floraexpansion.client.screen.BasketScreen;
import de.artemis.floraexpansion.client.screen.CrateScreen;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;
import de.artemis.floraexpansion.common.registry.ModMenuTypes;
import de.artemis.floraexpansion.common.particle.FallingFruitParticle;
import de.artemis.floraexpansion.common.particle.FlaxFlowerParticles;
import de.artemis.floraexpansion.common.particle.NeedleFluffParticles;
import de.artemis.floraexpansion.common.particle.NeedleParticles;
import de.artemis.floraexpansion.common.registry.ModParticles;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.StandingSignRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import de.artemis.floraexpansion.common.config.ClientConfig;
import de.artemis.floraexpansion.client.texture.JamPortionSpriteSource;
import de.artemis.floraexpansion.client.texture.FloweringMossSpriteSource;
import net.neoforged.neoforge.client.event.RegisterSpriteSourcesEvent;
import net.minecraft.resources.Identifier;

@Mod(value = FloraExpansion.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public class FloraExpansionClient {
    public FloraExpansionClient(ModContainer container) {
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerExtensionPoint(net.neoforged.neoforge.client.gui.IConfigScreenFactory.class, net.neoforged.neoforge.client.gui.ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.NEEDLE_FLUFF_PARTICLES.get(), NeedleFluffParticles.Provider::new);
        event.registerSpriteSet(ModParticles.NEEDLE_PARTICLES.get(), NeedleParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FLAX_FLOWER.get(), FlaxFlowerParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FALLING_CHERRY.get(), FallingFruitParticle.Provider::new);
        event.registerSpriteSet(ModParticles.FALLING_APPLE.get(), FallingFruitParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerSpriteSources(RegisterSpriteSourcesEvent event) {
        event.register(Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "jam_portions"), JamPortionSpriteSource.MAP_CODEC);
        event.register(Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "flowering_moss"), FloweringMossSpriteSource.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CRATE.get(), CrateScreen::new);
        event.register(ModMenuTypes.BASKET.get(), BasketScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.SPRUCE_CONE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.PEBBLES.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CACTUS_BOAT.get(),
                context -> new CactusBoatRenderer(context, false));

        event.registerEntityRenderer(ModEntityTypes.CACTUS_CHEST_BOAT.get(),
                context -> new CactusBoatRenderer(context, true));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CactusBoatRenderer.CACTUS_BOAT_LAYER, BoatModel::createBoatModel);
        event.registerLayerDefinition(CactusBoatRenderer.CACTUS_CHEST_BOAT_LAYER, BoatModel::createChestBoatModel);
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        FloraExpansion.LOGGER.info("Registering cactus sign renderers");
        event.registerBlockEntityRenderer(ModBlockEntities.CACTUS_SIGN.get(), StandingSignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CACTUS_HANGING_SIGN.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRATE.get(), CrateBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.STRAWBERRY_CAKE.get(), StrawberryCakeBlockEntityRenderer::new);
    }
}
