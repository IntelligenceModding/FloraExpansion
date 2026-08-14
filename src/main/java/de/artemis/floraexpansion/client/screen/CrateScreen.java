package de.artemis.floraexpansion.client.screen;

import de.artemis.floraexpansion.common.inventory.CrateMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class CrateScreen extends AbstractContainerScreen<CrateMenu> {
    private static final Identifier CONTAINER_BACKGROUND = Identifier.fromNamespaceAndPath("floraexpansion", "textures/gui/container/crate.png");
    private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/creative_inventory/scroller");
    private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/creative_inventory/scroller_disabled");
    private static final int SCROLLER_X = 156;
    private static final int SCROLLER_Y = 18;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int SCROLLER_TRAVEL = 91;
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;

    private float scrollOffs;
    private boolean scrolling;

    public CrateScreen(CrateMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
        this.scrollOffs = menu.getScrollForRowIndex(menu.getScrollRow());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, left, top, 0, 0, this.imageWidth, this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.menu.canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE, left + SCROLLER_X, top + SCROLLER_Y + (int) (SCROLLER_TRAVEL * this.scrollOffs), SCROLLER_WIDTH, SCROLLER_HEIGHT);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && this.insideScrollbar(event.x(), event.y())) {
            this.scrolling = this.menu.canScroll();
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.scrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!this.scrolling || !this.menu.canScroll()) {
            return super.mouseDragged(event, dragX, dragY);
        }

        int top = this.topPos + SCROLLER_Y;
        this.scrollOffs = ((float) event.y() - (float) top - 7.5F) / (float) SCROLLER_TRAVEL;
        this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
        this.applyScroll();
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.menu.canScroll()) {
            return false;
        }

        this.scrollOffs = this.menu.subtractInputFromScroll(this.scrollOffs, scrollY);
        this.applyScroll();
        return true;
    }

    private boolean insideScrollbar(double mouseX, double mouseY) {
        int x = this.leftPos + SCROLLER_X;
        int y = this.topPos + SCROLLER_Y;
        return mouseX >= x && mouseX < x + SCROLLER_WIDTH && mouseY >= y && mouseY < y + 112;
    }

    private void applyScroll() {
        int rowIndex = this.menu.getRowIndexForScroll(this.scrollOffs);
        this.scrollOffs = this.menu.getScrollForRowIndex(rowIndex);
        this.menu.setScrollRow(rowIndex);
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, rowIndex);
        }
    }
}
