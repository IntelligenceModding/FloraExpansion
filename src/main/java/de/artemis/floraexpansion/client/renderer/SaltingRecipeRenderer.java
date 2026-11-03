package de.artemis.floraexpansion.client.renderer;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
/** The same animated cutaway rendered with the 1.21.1 GUI buffer. */
public final class SaltingRecipeRenderer {
    private static final SaltingRecipeRenderer INSTANCE = new SaltingRecipeRenderer();
    // GuiGraphics looks along -Z; show the open north/east sides and the top.
    static final float PITCH = 25, YAW = 215;
    private final SaltingRecipeParticles particles = new SaltingRecipeParticles();
    private final ContentsFillRenderer fill = new ContentsFillRenderer();
    public record State(ItemStack food, ItemStack salt, ItemStack result, float time) {}
    public static void draw(GuiGraphics graphics, State state) {
        graphics.flush();
        var poses = graphics.pose();
        poses.pushPose();
        poses.translate(88, 99, 150);
        poses.scale(61, 61, 61);
        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();
        INSTANCE.renderScene(state, poses, new GeometryCollector(Minecraft.getInstance().renderBuffers().bufferSource()));
        graphics.flush();
        poses.popPose();
        Lighting.setupFor3DItems();
    }
    private void renderScene(State state, PoseStack poses, GeometryCollector collector) {
        Lighting.setupForEntityInInventory();
        poses.scale(1, -1, 1);
        poses.mulPose(Axis.XP.rotationDegrees(PITCH));
        poses.mulPose(Axis.YP.rotationDegrees(YAW));
        poses.translate(-0.5F, -0.5F, -0.5F);
        SaltingSceneModel.INSTANCE.submit(poses, collector);

        // 0..1 empty, 1..5 four additions, 5..6 salt, 6..9 cure, 9..11 ready, 11..12 empty.
        float time = state.time();
        if (time <= 1 || time >= 11) return;
        boolean ready = time >= 9;
        float added = Math.min(4, time - 1);
        int complete = (int)added;
        float fraction = Math.min(1, (added - complete) / 0.7F);
        float eased = fraction * fraction * (3 - 2 * fraction);
        float previous = complete == 0 ? 2F/16 : ContentsFillRenderer.height(complete/4F);
        float next = ContentsFillRenderer.height(Math.min(4,complete+1)/4F);
        float top = ready ? 1F : previous + (next-previous)*eased;
        var pixels = fill.pattern(ready ? state.result() : state.food(), Minecraft.getInstance().level);
        fill.submitCutaway(poses, collector, pixels, top, 15728880);
        if (time >= 6 && !ready) SaltingTubBlockEntityRenderer.submitSaltCutawayCover(poses, collector, 2F/16, 15728880);
        particles.submit(state, poses, collector);
    }
}
