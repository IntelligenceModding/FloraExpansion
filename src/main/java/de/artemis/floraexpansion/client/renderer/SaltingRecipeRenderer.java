package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

/** A single block-space scene: JSON shell, shared food pattern, and shared salt cover. */
public final class SaltingRecipeRenderer extends PictureInPictureRenderer<SaltingRecipeRenderer.State> {
    public SaltingRecipeRenderer(net.minecraft.client.renderer.MultiBufferSource.BufferSource buffers) { super(buffers); }
    static final float PITCH = -25, YAW = 35;
    private final SaltingRecipeParticles particles = new SaltingRecipeParticles();
    public record State(ItemStack food, ItemStack salt, ItemStack result, float time,
                        int x0, int y0, int x1, int y1, float scale, Matrix3x2f pose,
                        @Nullable ScreenRectangle scissorArea) implements PictureInPictureRenderState {
        @Override public @Nullable ScreenRectangle bounds() {
            return PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea);
        }
    }
    private final ContentsFillRenderer fill = new ContentsFillRenderer(Minecraft.getInstance().getItemModelResolver());
    @Override public Class<State> getRenderStateClass() { return State.class; }
    @Override protected String getTextureLabel() { return "salting recipe"; }
    @Override protected float getTranslateY(int height, int guiScale) { return height * 0.55F; }

    @Override protected void renderToTexture(State state, PoseStack poses) {
        var dispatcher = Minecraft.getInstance().gameRenderer.getFeatureRenderDispatcher();
        var collector = dispatcher.getSubmitNodeStorage();
        renderScene(state, poses, collector);
        dispatcher.renderAllFeatures();
    }
    private void renderScene(State state, PoseStack poses, SubmitNodeCollector collector) {
        Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
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
        if (time >= 6 && !ready) SaltingTubBlockEntityRenderer.submitSaltCover(poses, collector, 2F/16, 15728880);
        particles.submit(state, poses, collector);
    }
}
