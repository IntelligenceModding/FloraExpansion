package de.artemis.floraexpansion.client.screen;

import de.artemis.floraexpansion.common.inventory.HandMillMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class HandMillScreen extends AbstractContainerScreen<HandMillMenu> {
    private static final Identifier ARROW = Identifier.withDefaultNamespace("container/furnace/burn_progress");
    public HandMillScreen(HandMillMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 166);
        inventoryLabelY = 72;
    }
    @Override public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        int x = leftPos, y = topPos;
        graphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF373737);
        graphics.fill(x + 1, y + 1, x + imageWidth - 1, y + imageHeight - 1, 0xFFFFFFFF);
        graphics.fill(x + 3, y + 3, x + imageWidth - 3, y + imageHeight - 3, 0xFFC6C6C6);
        graphics.fill(x + 3, y + imageHeight - 3, x + imageWidth - 1, y + imageHeight - 1, 0xFF555555);
        graphics.fill(x + imageWidth - 3, y + 3, x + imageWidth - 1, y + imageHeight - 1, 0xFF555555);
        for (var slot : menu.slots) slot(graphics, x + slot.x, y + slot.y);
        // Empty arrow silhouette, filled from left to right as the crank advances.
        graphics.fill(x + 76, y + 40, x + 92, y + 45, 0xFF8B8B8B);
        for (int column = 0; column < 8; column++) {
            graphics.fill(x + 92 + column, y + 35 + column, x + 93 + column, y + 50 - column, 0xFF8B8B8B);
        }
        int width = menu.getArrowWidth();
        if (width > 0) graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW, 24, 16, 0, 0, x + 76, y + 35, width, 16);
    }
    private static void slot(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x - 1, y - 1, x + 17, y + 17, 0xFF373737);
        graphics.fill(x, y, x + 17, y + 17, 0xFFFFFFFF);
        graphics.fill(x, y, x + 16, y + 16, 0xFF8B8B8B);
    }
}