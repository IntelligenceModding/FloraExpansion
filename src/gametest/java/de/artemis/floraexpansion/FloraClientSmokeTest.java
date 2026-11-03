package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RenderShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class FloraClientSmokeTest {
    private static boolean complete;
    private static boolean cratesChecked;
    private static boolean creatingWorld;
    private static int worldTicks;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (Boolean.getBoolean("floraexpansion.handMillRenderSmokeTest")) return;
        if (complete || !Boolean.getBoolean("floraexpansion.clientSmokeTest")) return;
        var minecraft = Minecraft.getInstance();
        if (!creatingWorld && minecraft.getOverlay() == null && !(minecraft.screen instanceof TitleScreen)) {
            FloraExpansion.LOGGER.info("Smoke test dismissing initial screen: {}", minecraft.screen);
            minecraft.setScreen(new TitleScreen());
            return;
        }
        if (!creatingWorld && minecraft.screen instanceof TitleScreen && minecraft.getOverlay() == null) {
            creatingWorld = true;
            minecraft.createWorldOpenFlows().createFreshLevel("port-smoke-" + System.currentTimeMillis(),
                    new net.minecraft.world.level.LevelSettings("Flora 26.1 smoke test", net.minecraft.world.level.GameType.CREATIVE, false,
                            net.minecraft.world.Difficulty.NORMAL, true,
                            new net.minecraft.world.level.GameRules(net.minecraft.world.level.WorldDataConfiguration.DEFAULT.enabledFeatures()), net.minecraft.world.level.WorldDataConfiguration.DEFAULT),
                    new net.minecraft.world.level.levelgen.WorldOptions(12345L, true, false),
                    net.minecraft.world.level.levelgen.presets.WorldPresets::createNormalWorldDimensions,
                    minecraft.screen);
            return;
        }
        if (minecraft.level == null || minecraft.player == null
                || minecraft.screen != null && !(minecraft.screen instanceof net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen)
                        && !(minecraft.screen instanceof JamIconSmokeTest.PreviewScreen)
                || ++worldTicks < 100) return;
        if (!cratesChecked) {
            if (!SaltSmokeTest.check(minecraft, worldTicks)) return;
            if (!FloweringMossSmokeTest.check(minecraft, worldTicks)) return;
            if (!CrateRenderSmokeTest.check(minecraft, worldTicks)) return;
            cratesChecked = true;
        }
        if (!JarRenderSmokeTest.check(minecraft, worldTicks)) return;
        if (!SignRenderSmokeTest.check(minecraft, worldTicks)) return;
        if (!JamIconSmokeTest.check(minecraft, worldTicks)) return;
        complete = true;
        ClientCustomizationSmokeTest.verify(minecraft);
        var manager = minecraft.getModelManager();
        var tab = de.artemis.floraexpansion.common.registry.ModCreativeModeTabs.FLORA_EXPANSION_CREATIVE_TAB.get();
        tab.buildContents(new net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters(
                minecraft.level.enabledFeatures(), true, minecraft.level.registryAccess()));
        var models = manager.getBlockModelShaper();
        int states = 0, items = 0;
        for (var entry : ModBlocks.BLOCKS.getEntries()) {
            for (var state : entry.get().getStateDefinition().getPossibleStates()) {
                if (state.getRenderShape() == RenderShape.MODEL && models.getBlockModel(state) == models.getModelManager().getMissingBlockStateModel()) {
                    throw new IllegalStateException("Missing block model: " + state);
                }
                states++;
            }
        }
        for (var entry : ModItems.ITEMS.getEntries()) {
            var id = new ItemStack(entry.get()).get(DataComponents.ITEM_MODEL);
            if (id == null || manager.getItemModel(id) == manager.getItemModel(net.minecraft.resources.ResourceLocation.withDefaultNamespace("__missing__"))) {
                throw new IllegalStateException("Missing item model: " + entry.getId());
            }
            items++;
        }
        FloraExpansion.LOGGER.info("Client smoke test passed: {} block states and {} item models; client and integrations loaded", states, items);
        net.minecraft.client.Screenshot.grab(minecraft.gameDirectory, "placeable-jars.png",
                minecraft.getMainRenderTarget(), 1, message -> minecraft.execute(() -> {
                    FloraExpansion.LOGGER.info("Crate render screenshot: {}", message.getString());
                    minecraft.stop();
                }));
    }
}
