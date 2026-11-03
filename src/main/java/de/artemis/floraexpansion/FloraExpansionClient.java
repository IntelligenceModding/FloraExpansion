package de.artemis.floraexpansion;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.client.renderer.CactusBoatRenderer;
import de.artemis.floraexpansion.client.renderer.CrateBlockEntityRenderer;
import de.artemis.floraexpansion.client.renderer.StrawberryCakeBlockEntityRenderer;
import de.artemis.floraexpansion.client.screen.BasketScreen;
import de.artemis.floraexpansion.client.screen.CrateScreen;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;
import de.artemis.floraexpansion.common.registry.ModMenuTypes;
import de.artemis.floraexpansion.common.particle.FallingFruitParticle;
import de.artemis.floraexpansion.common.particle.SaltWaterDripParticle;
import de.artemis.floraexpansion.common.particle.FlaxFlowerParticles;
import de.artemis.floraexpansion.common.particle.NeedleFluffParticles;
import de.artemis.floraexpansion.common.particle.NeedleParticles;
import de.artemis.floraexpansion.common.registry.ModParticles;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import de.artemis.floraexpansion.common.config.ClientConfig;
import de.artemis.floraexpansion.client.texture.JamPortionSpriteSource;
import de.artemis.floraexpansion.client.texture.FloweringMossSpriteSource;
import net.neoforged.neoforge.client.event.RegisterSpriteSourceTypesEvent;
import net.minecraft.resources.ResourceLocation;

@Mod(value = FloraExpansion.MODID, dist = Dist.CLIENT)
public class FloraExpansionClient {
    public FloraExpansionClient(ModContainer container, net.neoforged.bus.api.IEventBus modEventBus) {
        modEventBus.register(FloraExpansionClient.class);
        modEventBus.register(de.artemis.floraexpansion.client.ModColorHandlers.class);
        de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity.setClientPartialTickSupplier(
                () -> net.minecraft.client.Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity.setClientAnimationTimeSupplier(
                de.artemis.floraexpansion.client.ClientAnimationClock::time);
        container.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerExtensionPoint(net.neoforged.neoforge.client.gui.IConfigScreenFactory.class, net.neoforged.neoforge.client.gui.ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var jam : java.util.List.of(
                    de.artemis.floraexpansion.common.registry.ModItems.BLUEBERRY_JAM.get(),
                    de.artemis.floraexpansion.common.registry.ModItems.STRAWBERRY_JAM.get(),
                    de.artemis.floraexpansion.common.registry.ModItems.CHERRY_JAM.get(),
                    de.artemis.floraexpansion.common.registry.ModItems.APPLE_JAM.get())) {
                net.minecraft.client.renderer.item.ItemProperties.register(jam,
                        ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "eaten_portions"),
                        (stack, level, entity, seed) -> 4 - de.artemis.floraexpansion.common.block.JamJarBlock.itemPortions(stack));
            }
            net.minecraft.client.renderer.Sheets.addWoodType(de.artemis.floraexpansion.common.registry.ModWoodTypes.CACTUS_WOOD_TYPE);
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get(), net.minecraft.client.renderer.RenderType.translucent());
            net.minecraft.client.renderer.ItemBlockRenderTypes.setRenderLayer(de.artemis.floraexpansion.common.registry.ModFluids.FLOWING_SALT_WATER.get(), net.minecraft.client.renderer.RenderType.translucent());
        });
    }

    @SubscribeEvent
    public static void registerSaltingSceneReload(net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(
                de.artemis.floraexpansion.client.renderer.SaltingSceneModel.INSTANCE);
    }
    @SubscribeEvent
    public static void registerMachineRecipeCategories(net.neoforged.neoforge.client.event.RegisterRecipeBookCategoriesEvent event) {
        // These machines use their own GUI/JEI, rather than a vanilla crafting or furnace book.
        event.registerRecipeCategoryFinder(de.artemis.floraexpansion.common.registry.ModRecipes.GRINDING.get(),
                recipe -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
        event.registerRecipeCategoryFinder(de.artemis.floraexpansion.common.registry.ModRecipes.SALTING.get(),
                recipe -> net.minecraft.client.RecipeBookCategories.UNKNOWN);
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.DRIPPING_SALT_WATER.get(), sprites -> new SaltWaterDripParticle.Provider(sprites, SaltWaterDripParticle.Stage.HANGING));
        event.registerSpriteSet(ModParticles.FALLING_SALT_WATER.get(), sprites -> new SaltWaterDripParticle.Provider(sprites, SaltWaterDripParticle.Stage.FALLING));
        event.registerSpriteSet(ModParticles.LANDING_SALT_WATER.get(), sprites -> new SaltWaterDripParticle.Provider(sprites, SaltWaterDripParticle.Stage.LANDING));
        event.registerSpriteSet(ModParticles.NEEDLE_FLUFF_PARTICLES.get(), NeedleFluffParticles.Provider::new);
        event.registerSpriteSet(ModParticles.NEEDLE_PARTICLES.get(), NeedleParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FLAX_FLOWER.get(), FlaxFlowerParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FALLING_CHERRY.get(), FallingFruitParticle.Provider::new);
        event.registerSpriteSet(ModParticles.FALLING_APPLE.get(), FallingFruitParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerSpriteSources(RegisterSpriteSourceTypesEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "jam_portions"), JamPortionSpriteSource.TYPE);
        event.register(ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "flowering_moss"), FloweringMossSpriteSource.TYPE);
        event.register(ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "salt_item"),
                de.artemis.floraexpansion.client.texture.SaltSpriteSource.TYPE);
    }

    @SubscribeEvent
    public static void registerFluidExtensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
        event.registerFluidType(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
            @NotNull @Override public ResourceLocation getStillTexture() { return ResourceLocation.withDefaultNamespace("block/water_still"); }
            @NotNull @Override public ResourceLocation getFlowingTexture() { return ResourceLocation.withDefaultNamespace("block/water_flow"); }
            @Override public ResourceLocation getOverlayTexture() { return ResourceLocation.withDefaultNamespace("block/water_overlay"); }
            @Override public int getTintColor() { return de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER_COLOR; }
            @Override public ResourceLocation getRenderOverlayTexture(@NotNull net.minecraft.client.Minecraft minecraft) {
                return ResourceLocation.withDefaultNamespace("textures/misc/underwater.png");
            }
            @NotNull @Override public org.joml.Vector3f modifyFogColor(@NotNull net.minecraft.client.Camera camera, float partialTick,
                    @NotNull net.minecraft.client.multiplayer.ClientLevel level, int renderDistance, float darkenWorldAmount, @NotNull org.joml.Vector3f color) {
                return color.set(0.18F, 0.38F, 0.44F);
            }
        }, de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER_TYPE.get());
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CRATE.get(), CrateScreen::new);
        event.register(ModMenuTypes.HAND_MILL.get(), de.artemis.floraexpansion.client.screen.HandMillScreen::new);
        event.register(ModMenuTypes.BASKET.get(), BasketScreen::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.SPRUCE_CONE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.PEBBLES.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CACTUS_BOAT.get(),
                context -> new CactusBoatRenderer<>(context, false));

        event.registerEntityRenderer(ModEntityTypes.CACTUS_CHEST_BOAT.get(),
                context -> new CactusBoatRenderer<>(context, true));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CACTUS_SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CACTUS_HANGING_SIGN.get(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRATE.get(), CrateBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SALTING_TUB.get(),
                de.artemis.floraexpansion.client.renderer.SaltingTubBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HAND_MILL.get(), de.artemis.floraexpansion.client.renderer.HandMillBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HAND_CRANK.get(), de.artemis.floraexpansion.client.renderer.HandCrankBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.STRAWBERRY_CAKE.get(), StrawberryCakeBlockEntityRenderer::new);
    }
}
