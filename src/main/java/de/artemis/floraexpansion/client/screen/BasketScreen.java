package de.artemis.floraexpansion.client.screen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.inventory.BasketMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class BasketScreen extends AbstractContainerScreen<BasketMenu> {
    private static final Identifier CONTAINER_TEXTURE = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "textures/gui/container/basket.png");

    public BasketScreen(BasketMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 132);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(guiGraphics, mouseX, mouseY, partialTick);
        int left = this.leftPos;
        int top = this.topPos;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
