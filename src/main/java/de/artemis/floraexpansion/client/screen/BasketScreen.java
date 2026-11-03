package de.artemis.floraexpansion.client.screen;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.inventory.BasketMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BasketScreen extends AbstractContainerScreen<BasketMenu> {
    private static final ResourceLocation CONTAINER_TEXTURE = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "textures/gui/container/basket.png");

    public BasketScreen(BasketMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176; imageHeight = 132;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;
        guiGraphics.blit(CONTAINER_TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
