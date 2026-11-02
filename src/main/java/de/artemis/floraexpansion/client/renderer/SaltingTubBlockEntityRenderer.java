package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.artemis.floraexpansion.client.renderer.state.SaltingTubRenderState;
import de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class SaltingTubBlockEntityRenderer implements BlockEntityRenderer<SaltingTubBlockEntity, SaltingTubRenderState> {
    private static final Identifier SALT = Identifier.fromNamespaceAndPath("floraexpansion", "textures/block/salt_deposit.png");
    private final ContentsFillRenderer fill;

    public SaltingTubBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        fill = new ContentsFillRenderer(context.itemModelResolver());
    }
    @Override public SaltingTubRenderState createRenderState() { return new SaltingTubRenderState(); }
    @Override public void extractRenderState(SaltingTubBlockEntity tub, SaltingTubRenderState state, float partialTick,
                                             Vec3 camera, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(tub, state, partialTick, camera, breakProgress);
        state.layers = tub.layers();
        state.salted = tub.isSalted();
        state.foodPixels = new int[0];
        state.ready = tub.isReady();
        if (state.layers == 0 || (state.salted && !state.ready)) return;
        state.foodPixels = fill.pattern(tub.displayFood(), tub.getLevel());
    }
    @Override public void submit(SaltingTubRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        submitContents(fill, poses, collector, state.foodPixels, state.layers, state.salted, state.ready, state.lightCoords);
    }

    /** Shared by the world renderer and the JEI scene, including the exact textures and heights. */
    static void submitContents(ContentsFillRenderer fill, PoseStack poses, SubmitNodeCollector collector,
                               int[] foodPixels, int layers, boolean salted, boolean ready, int light) {
        if (layers == 0) return;
        float top = ready ? 1F : (15.1F / 16F);
        if (salted && !ready) {
            submitSaltCover(poses, collector, 2F/16, light);
        } else {
            if (ready) fill.submitAtHeight(poses, collector, foodPixels, top, light);
            else fill.submit(poses, collector, foodPixels, layers / 4F, light);
        }
    }

    /** One model pixel of salt above the full food surface; clipped at the JEI cut plane. */
    static void submitSaltCover(PoseStack poses, SubmitNodeCollector collector, float front, int light) {
        submitSaltCover(poses, collector, front, light, RenderTypes.entityCutout(SALT));
    }

    static void submitSaltCutawayCover(PoseStack poses, SubmitNodeCollector collector, float front, int light) {
        submitSaltCover(poses, collector, front, light, RenderTypes.entityCutoutNoCull(SALT));
    }

    private static void submitSaltCover(PoseStack poses, SubmitNodeCollector collector, float front, int light,
                                        net.minecraft.client.renderer.rendertype.RenderType renderType) {
        float left = 2F / 16F, right = 14F / 16F, back = 14F / 16F;
        float bottom = ContentsFillRenderer.height(1F), top = bottom + 1F / 16F;
        collector.submitCustomGeometry(poses, renderType, (pose, out) -> {
            float v = (front - left) / (right - left);
            // Top and bottom retain the same texture mapping in the full tub and cutaway.
            saltQuad(pose,out, left,top,front, left,top,back, right,top,back, right,top,front,
                    0,v,1,1, 0,1,0,light);
            saltQuad(pose,out, left,bottom,front, right,bottom,front, right,bottom,back, left,bottom,back,
                    0,v,1,1, 0,-1,0,light);
            // Side strips are exactly one texture pixel high, including the exposed JEI section.
            saltQuad(pose,out, left,bottom,front, left,top,front, right,top,front, right,bottom,front,
                    0,0,1,1F/16, 0,0,-1,light);
            saltQuad(pose,out, right,bottom,back, right,top,back, left,top,back, left,bottom,back,
                    0,0,1,1F/16, 0,0,1,light);
            saltQuad(pose,out, left,bottom,back, left,top,back, left,top,front, left,bottom,front,
                    v,0,1,1F/16, -1,0,0,light);
            saltQuad(pose,out, right,bottom,front, right,top,front, right,top,back, right,bottom,back,
                    v,0,1,1F/16, 1,0,0,light);
        });
    }
    private static void saltQuad(PoseStack.Pose pose, VertexConsumer out,
                                 float x0,float y0,float z0, float x1,float y1,float z1,
                                 float x2,float y2,float z2, float x3,float y3,float z3,
                                 float u0,float v0,float u1,float v1, float nx,float ny,float nz,int light) {
        saltVertex(pose,out,x0,y0,z0,u0,v0,nx,ny,nz,light);
        saltVertex(pose,out,x1,y1,z1,u0,v1,nx,ny,nz,light);
        saltVertex(pose,out,x2,y2,z2,u1,v1,nx,ny,nz,light);
        saltVertex(pose,out,x3,y3,z3,u1,v0,nx,ny,nz,light);
    }
    private static void saltVertex(PoseStack.Pose pose, VertexConsumer out, float x,float y,float z,
                                   float u,float v,float nx,float ny,float nz,int light) {
        out.addVertex(pose,x,y,z).setColor(0xFFFFFFFF).setUv(u,v)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose,nx,ny,nz);
    }
}
