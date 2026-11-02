package de.artemis.floraexpansion;

import com.mojang.blaze3d.platform.NativeImage;
import de.artemis.floraexpansion.client.texture.JamPortionSpriteSource;
import de.artemis.floraexpansion.common.block.JamJarBlock;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;

final class JamIconSmokeTest {
    private static int openedAt = -1;
    private static boolean complete;

    static boolean check(Minecraft minecraft, int tick) {
        if (complete) return true;
        if (openedAt == -1) {
            verify(minecraft);
            verifyFramesAndScale();
            minecraft.setScreen(new PreviewScreen());
            openedAt = tick;
            return false;
        }
        if (tick - openedAt < 3) return false;
        Screenshot.grab(minecraft.gameDirectory, "jam-item-portions.png", minecraft.getMainRenderTarget(), 1,
                message -> FloraExpansion.LOGGER.info("Jam portion icon screenshot: {}", message.getString()));
        minecraft.setScreen(null);
        complete = true;
        return true;
    }

    private static void verify(Minecraft minecraft) {
        var empty = sprite(minecraft, new ItemStack(ModItems.GLASS_JAR.get()));
        String[] flavors = {"blueberry", "strawberry", "cherry", "apple"};
        Item[] jams = {ModItems.BLUEBERRY_JAM.get(), ModItems.STRAWBERRY_JAM.get(), ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get()};
        for (int flavor = 0; flavor < jams.length; flavor++) {
            var item = new ItemStack(jams[flavor]);
            var full = sprite(minecraft, item);
            for (int portions = 1; portions <= 4; portions++) {
                item.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(JamJarBlock.PORTIONS, portions));
                var before = item.copy();
                var actual = sprite(minecraft, item);
                var expectedId = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "item/" + flavors[flavor] + "_jam"
                        + (portions == 4 ? "" : "_portion_" + portions));
                if (!actual.contents().name().equals(expectedId)) throw new IllegalStateException("Wrong portion item model: " + actual.contents().name());
                if (!ItemStack.isSameItemSameComponents(item, before)) throw new IllegalStateException("Rendering mutated jar contents");
                int cutoff = switch (portions) { case 1 -> 11; case 2 -> 9; case 3 -> 6; default -> 0; };
                for (int y=0; y<16; y++) for (int x=0; x<16; x++) {
                    int expected = (y < cutoff ? empty : full).contents().getOriginalImage().getPixel( x, y);
                    if (actual.contents().getOriginalImage().getPixel( x, y) != expected) throw new IllegalStateException("Jar fill or glass pixels differ: " + expectedId);
                }
            }
            item.remove(DataComponents.BLOCK_STATE);
            if (sprite(minecraft, item) != full) throw new IllegalStateException("Legacy full jar did not retain original icon");
        }
        FloraExpansion.LOGGER.info("Jam item icons passed: all four flavors at every portion amount, exact source pixels, and legacy full-jar fallback");
    }

    private static TextureAtlasSprite sprite(Minecraft minecraft, ItemStack item) {
        var state = new ItemStackRenderState();
        minecraft.getItemModelResolver().updateForTopItem(state, item, ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);
        var material = state.pickParticleIcon(RandomSource.create(0));
        if (material == null) throw new IllegalStateException("Jar item has no visible sprite");
        return material;
    }

    private static void verifyFramesAndScale() {
        try (var image = new NativeImage(32, 64, false); var empty = new NativeImage(16, 16, false)) {
            for (int y=0; y<64; y++) for (int x=0; x<32; x++) image.setPixel(x, y, y < 32 ? 0xFFAD0000 : 0xFF00AD00);
            for (int y=0; y<16; y++) for (int x=0; x<16; x++) empty.setPixel(x, y, 0xFF102030);
            JamPortionSpriteSource.applyPortions(image, new FrameSize(32,32), empty, 1);
            if (image.getPixel(16,21) != 0xFF102030 || image.getPixel(16,22) != 0xFFAD0000
                    || image.getPixel(16,53) != 0xFF102030 || image.getPixel(16,54) != 0xFF00AD00) {
                throw new IllegalStateException("Scaled or animated jam icons lost their per-frame fill levels");
            }
        }
    }

    static final class PreviewScreen extends Screen {
        PreviewScreen() { super(Component.literal("Jam Portions")); }
        @Override public boolean isPauseScreen() { return false; }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int left=(width-320)/2, top=(height-180)/2;
            graphics.fill(left-12,top-12,left+332,top+174,0xFF20242A);
            graphics.drawString(font,"Jam portions",left,top,0xFFFFFFFF);
            for (int column=0;column<4;column++) graphics.drawString(font,(column+1)+"/4",left+120+column*44,top+18,0xFFFFFFFF);
            Item[] jams = {ModItems.BLUEBERRY_JAM.get(),ModItems.STRAWBERRY_JAM.get(),ModItems.CHERRY_JAM.get(),ModItems.APPLE_JAM.get()};
            String[] names={"Blueberry","Strawberry","Cherry","Apple"};
            for (int row=0;row<4;row++) {
                graphics.drawString(font,names[row],left,top+40+row*32,0xFFFFFFFF);
                for (int column=0;column<4;column++) {
                    var item=new ItemStack(jams[row]);
                    if (column<3) item.set(DataComponents.BLOCK_STATE,BlockItemStateProperties.EMPTY.with(JamJarBlock.PORTIONS,column+1));
                    graphics.pose().pushMatrix();
                    graphics.pose().translate(left+116+column*44,top+30+row*32);
                    graphics.pose().scale(2,2);
                    graphics.renderItem(item,0,0);
                    graphics.pose().popMatrix();
                }
            }
            super.render(graphics,mouseX,mouseY,partialTick);
        }
    }
}
