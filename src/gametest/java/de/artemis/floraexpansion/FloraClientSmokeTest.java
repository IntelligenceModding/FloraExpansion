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
    private static boolean creatingWorld;
    private static int worldTicks;

    @SubscribeEvent
    public static void tick(ClientTickEvent.Post event) {
        if (complete || !Boolean.getBoolean("floraexpansion.clientSmokeTest")) return;
        var minecraft = Minecraft.getInstance();
        if (!creatingWorld && minecraft.gui.screen() instanceof TitleScreen && minecraft.gui.overlay() == null) {
            creatingWorld = true;
            minecraft.createWorldOpenFlows().createFreshLevel("port-smoke-" + System.currentTimeMillis(),
                    new net.minecraft.world.level.LevelSettings("Flora 26.3 smoke test", net.minecraft.world.level.GameType.CREATIVE,
                            net.minecraft.world.level.LevelSettings.DifficultySettings.DEFAULT, true,
                            net.minecraft.world.level.WorldDataConfiguration.DEFAULT),
                    new net.minecraft.world.level.levelgen.WorldOptions(12345L, true, false),
                    net.minecraft.world.level.levelgen.presets.WorldPresets::createNormalWorldDimensions,
                    minecraft.gui.screen());
            return;
        }
        if (minecraft.level == null || minecraft.player == null || minecraft.gui.screen() != null || ++worldTicks < 100) return;
        complete = true;
        var manager = minecraft.getModelManager();
        var tab = de.artemis.floraexpansion.common.registry.ModCreativeModeTabs.FLORA_EXPANSION_CREATIVE_TAB.get();
        tab.buildContents(new net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters(
                minecraft.level.enabledFeatures(), true, minecraft.level.registryAccess()));
        var models = manager.getBlockStateModelSet();
        int states = 0, items = 0;
        for (var entry : ModBlocks.BLOCKS.getEntries()) {
            for (var state : entry.get().getStateDefinition().getPossibleStates()) {
                if (state.getRenderShape() == RenderShape.MODEL && models.get(state) == models.missingModel()) {
                    throw new IllegalStateException("Missing block model: " + state);
                }
                states++;
            }
        }
        for (var entry : ModItems.ITEMS.getEntries()) {
            var id = new ItemStack(entry.get()).get(DataComponents.ITEM_MODEL);
            if (id == null || manager.getItemModel(id) == manager.getMissingModels().item()) {
                throw new IllegalStateException("Missing item model: " + entry.getId());
            }
            items++;
        }
        FloraExpansion.LOGGER.info("Client smoke test passed: {} block states and {} item models; client and integrations loaded", states, items);
        minecraft.stop();
    }
}
