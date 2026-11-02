package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.artemis.floraexpansion.client.renderer.state.StrawberryCakeRenderState;
import de.artemis.floraexpansion.common.block.StrawberryCakeWithCandleBlock;
import de.artemis.floraexpansion.common.block.entity.StrawberryCakeBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class StrawberryCakeBlockEntityRenderer implements BlockEntityRenderer<StrawberryCakeBlockEntity, StrawberryCakeRenderState> {
    public StrawberryCakeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public StrawberryCakeRenderState createRenderState() {
        return new StrawberryCakeRenderState();
    }

    @Override
    public void extractRenderState(StrawberryCakeBlockEntity entity, StrawberryCakeRenderState state, float partialTick,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, breakProgress);
        boolean lit = entity.getBlockState().getValue(StrawberryCakeWithCandleBlock.LIT);
        state.textures = entity.getCandles().stream().map(candle -> {
            var id = BuiltInRegistries.BLOCK.getKey(candle);
            return Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/block/" + id.getPath() + (lit ? "_lit" : "") + ".png");
        }).toList();
    }

    @Override
    public void submit(StrawberryCakeRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        if (state.textures.isEmpty()) return;
        var offsets = StrawberryCakeWithCandleBlock.candleOffsets(state.textures.size());
        for (int slot = 0; slot < state.textures.size(); slot++) {
            var position = offsets.get(slot);
            int light = state.lightCoords;
            collector.submitCustomGeometry(poses, RenderTypes.entityCutout(state.textures.get(slot)), (pose, vertices) -> {
                float x = (float) position.x * 16, z = (float) position.z * 16, top = (float) position.y * 16 - 2;
                quad(pose, vertices, light, new float[]{x-1,8,z-1, x-1,top,z-1, x+1,top,z-1, x+1,8,z-1}, 0,8,2,top, 0,0,-1);
                quad(pose, vertices, light, new float[]{x+1,8,z+1, x+1,top,z+1, x-1,top,z+1, x-1,8,z+1}, 0,8,2,top, 0,0,1);
                quad(pose, vertices, light, new float[]{x-1,8,z+1, x-1,top,z+1, x-1,top,z-1, x-1,8,z-1}, 0,8,2,top, -1,0,0);
                quad(pose, vertices, light, new float[]{x+1,8,z-1, x+1,top,z-1, x+1,top,z+1, x+1,8,z+1}, 0,8,2,top, 1,0,0);
                quad(pose, vertices, light, new float[]{x-1,top,z-1, x-1,top,z+1, x+1,top,z+1, x+1,top,z-1}, 0,6,2,8, 0,1,0);
                float halfDiagonal = (float) (Math.sqrt(0.5) / 2);
                for (int sign : new int[]{-1, 1}) {
                    float dz = halfDiagonal * sign;
                    quad(pose, vertices, light, new float[]{x-halfDiagonal,top,z-dz, x-halfDiagonal,top+1,z-dz,
                            x+halfDiagonal,top+1,z+dz, x+halfDiagonal,top,z+dz}, 0,5,1,6, -sign * 0.7071F,0,0.7071F);
                }
            });
        }
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer vertices, int light, float[] xyz,
                             float u1, float v1, float u2, float v2, float nx, float ny, float nz) {
        for (int corner = 0; corner < 4; corner++) {
            float u = corner < 2 ? u1 : u2;
            float v = corner == 0 || corner == 3 ? v2 : v1;
            vertices.addVertex(pose, xyz[corner*3]/16, xyz[corner*3+1]/16, xyz[corner*3+2]/16)
                    .setColor(0xFFFFFFFF).setUv(u/16, v/16).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(pose, nx, ny, nz);
        }
    }
}
