package de.artemis.floraexpansion;

import de.artemis.floraexpansion.client.renderer.CrateBlockEntityRenderer;
import de.artemis.floraexpansion.client.renderer.state.CrateRenderState;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.config.ClientConfig;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;

final class ClientCustomizationSmokeTest {
    private ClientCustomizationSmokeTest() {}

    static void verify(Minecraft minecraft) {
        check(ClientConfig.SPEC.isLoaded(), "Client config was not registered/loaded");
        boolean fill = ClientConfig.crateFill.get(), hints = ClientConfig.interactionHints.get();
        double distance = ClientConfig.crateFillDistance.get();
        var detail = ClientConfig.tooltipDetail.get();
        var pos = minecraft.player.blockPosition();
        var crate = new CrateBlockEntity(pos, ModBlocks.CRATE.get().defaultBlockState());
        crate.setLevel(minecraft.level);
        crate.insertItem(new ItemStack(Items.WHEAT_SEEDS, 16));
        var renderer = (CrateBlockEntityRenderer) minecraft.getBlockEntityRenderDispatcher()
                .<CrateBlockEntity, CrateRenderState>getRenderer(crate);
        check(renderer != null, "Missing crate renderer");
        var state = renderer.createRenderState();
        try {
            ClientConfig.crateFill.set(false);
            renderer.extractRenderState(crate, state, 0, Vec3.atCenterOf(pos), null);
            check(state.contentsPixels.length == 0, "Disabled fill remained visible");
            ClientConfig.crateFill.set(true);
            ClientConfig.crateFillDistance.set(8.0);
            renderer.extractRenderState(crate, state, 0, Vec3.atCenterOf(pos).add(9, 0, 0), null);
            check(state.contentsPixels.length == 0, "Fill distance was ignored");
            ClientConfig.crateFillDistance.set(96.0);
            check(renderer.getViewDistance() == 96, "Dispatcher distance remained capped at vanilla 64");
            renderer.extractRenderState(crate, state, 0, Vec3.atCenterOf(pos).add(9, 0, 0), null);
            check(state.contentsPixels.length == 144 && crate.getTotalItemCount() == 16,
                    "Rendering preference changed inventory or lost pixel-aligned appearance");

            var basket = new ItemStack(ModItems.BASKET.get());
            var contents = NonNullList.withSize(9, ItemStack.EMPTY);
            Item[] items = {Items.STONE, Items.DIRT, Items.DIAMOND, Items.WHEAT, Items.APPLE,
                    Items.CARROT, Items.RED_MUSHROOM, Items.BROWN_MUSHROOM, ModItems.SPRUCE_NUTS.get()};
            for (int i = 0; i < items.length; i++) contents.set(i, new ItemStack(items[i]));
            de.artemis.floraexpansion.common.item.BasketItem.saveContents(basket, contents);
            ClientConfig.interactionHints.set(false);
            ClientConfig.tooltipDetail.set(ClientConfig.TooltipDetail.COMPACT);
            var compact = tooltip(minecraft, basket);
            ClientConfig.tooltipDetail.set(ClientConfig.TooltipDetail.EXPANDED);
            var expanded = tooltip(minecraft, basket);
            check(compact.size() == 5 && expanded.size() == 9, "Tooltip detail preference failed");
            checkFullBasketTooltip(minecraft, basket, expanded);
            ClientConfig.tooltipDetail.set(ClientConfig.TooltipDetail.COMPACT);
            checkFullBasketTooltip(minecraft, basket, compact);
            ClientConfig.tooltipDetail.set(ClientConfig.TooltipDetail.ON_SHIFT);
            var shifted = new TooltipFlag() {
                public boolean isAdvanced() { return false; }
                public boolean isCreative() { return false; }
                public boolean hasShiftDown() { return true; }
            };
            var shiftLines = new java.util.ArrayList<Component>();
            basket.getItem().appendHoverText(basket, Item.TooltipContext.of(minecraft.level), TooltipDisplay.DEFAULT, shiftLines::add, shifted);
            check(shiftLines.size() == 9, "Shift tooltip preference failed");
            ClientConfig.interactionHints.set(true);
            check(!tooltip(minecraft, new ItemStack(ModItems.BASKET.get())).isEmpty(), "Empty basket has no enabled control hint");

            var mod = net.neoforged.fml.ModList.get().getModContainerById(FloraExpansion.MODID).orElseThrow();
            var configScreen = new net.neoforged.neoforge.client.gui.ConfigurationScreen(mod, null);
            configScreen.init(minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
            check(!configScreen.children().isEmpty(), "NeoForge configuration screen did not initialize");
        } finally {
            ClientConfig.crateFill.set(fill); ClientConfig.crateFillDistance.set(distance);
            ClientConfig.interactionHints.set(hints); ClientConfig.tooltipDetail.set(detail);
        }
        FloraExpansion.LOGGER.info("Client customization passed: live rendering/distance preferences, tooltips, hints, and NeoForge config screen");
    }

    private static java.util.List<Component> tooltip(Minecraft minecraft, ItemStack stack) {
        var lines = new java.util.ArrayList<Component>();
        stack.getItem().appendHoverText(stack, Item.TooltipContext.of(minecraft.level), TooltipDisplay.DEFAULT, lines::add, TooltipFlag.NORMAL);
        return lines;
    }

    private static void checkFullBasketTooltip(Minecraft minecraft, ItemStack basket, java.util.List<Component> preview) {
        var lines = basket.getTooltipLines(Item.TooltipContext.of(minecraft.level), minecraft.player, TooltipFlag.NORMAL);
        check(lines.stream().noneMatch(line -> line.getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents text
                        && (text.getKey().equals("item.container.item_count") || text.getKey().equals("item.container.more_items"))),
                "Basket still includes the automatic container preview alongside its grouped preview");
        for (var entry : preview) {
            check(lines.stream().filter(line -> line.getString().equals(entry.getString())).count() == 1,
                    "Basket preview entry is missing or duplicated: " + entry.getString());
        }
    }

    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
}
