package de.artemis.floraexpansion.client.renderer;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.vertex.PoseStack;
import static de.artemis.floraexpansion.client.renderer.MillRenderGeometry.rect;
import static de.artemis.floraexpansion.client.renderer.MillRenderGeometry.box;
import com.mojang.math.Axis;
import de.artemis.floraexpansion.client.renderer.state.HandMillRenderState;
import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import net.minecraft.client.renderer.RenderType;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** The runner and connecting axle share the crank's rotation. */
public class HandMillBlockEntityRenderer implements BlockEntityRenderer<HandMillBlockEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/block/polished_andesite.png");
    private static final ResourceLocation WOOD = ResourceLocation.withDefaultNamespace("textures/block/stripped_spruce_log.png");
    // A ten-pixel square stays within the sixteen-pixel housing even at 45 degrees.
    private static final float HALF = 5.0F / 16;
    private static final float SOCKET = 1F / 16;
    private static final float BOTTOM = 6.25F / 16;
    private static final float TOP = 11.5F / 16;

    public HandMillBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}
    public HandMillRenderState createRenderState() { return new HandMillRenderState(); }
    public void extractRenderState(HandMillBlockEntity mill, HandMillRenderState state, float partialTick,
                                             Vec3 camera, @Nullable Object breakProgress) {

        state.angle = mill.getMillstoneAngle(partialTick);
        if (mill.getLevel() != null) {
            // Use ambient light around the exposed stone and shaft.
            for (var direction : Direction.Plane.HORIZONTAL) {
                state.lightCoords = maxLight(state.lightCoords,
                        net.minecraft.client.renderer.LevelRenderer.getLightColor(mill.getLevel(), mill.getBlockPos().relative(direction)));
            }
        }
    }
    private static int maxLight(int first, int second) {
        return LightTexture.pack(Math.max(LightTexture.block(first), LightTexture.block(second)), Math.max(LightTexture.sky(first), LightTexture.sky(second)));
    }
    private void submit(HandMillRenderState state, PoseStack poses, GeometryCollector collector) {
        poses.pushPose();
        poses.translate(0.5, 0, 0.5);
        // Crank models advance (x, z) -> (-z, x), the negative Y rotation in JOML.
        poses.mulPose(Axis.YP.rotationDegrees(-state.angle));
        int light = state.lightCoords;
        collector.submitCustomGeometry(poses, RenderType.entitySolid(TEXTURE), (pose, out) -> {
            // Fit the stone surface flush against the axle so its hollow interior cannot show.
            rect(pose, out, Direction.UP, TOP, -HALF, -HALF, HALF, -SOCKET, 0xFFFFFFFF, light);
            rect(pose, out, Direction.UP, TOP, -HALF, SOCKET, HALF, HALF, 0xFFFFFFFF, light);
            rect(pose, out, Direction.UP, TOP, -HALF, -SOCKET, -SOCKET, SOCKET, 0xFFFFFFFF, light);
            rect(pose, out, Direction.UP, TOP, SOCKET, -SOCKET, HALF, SOCKET, 0xFFFFFFFF, light);
            rect(pose, out, Direction.DOWN, BOTTOM, -HALF, -HALF, HALF, HALF, 0xFFFFFFFF, light);
            for (var side : Direction.Plane.HORIZONTAL) {
                float plane = side == Direction.NORTH || side == Direction.WEST ? -HALF : HALF;
                rect(pose, out, side, plane, -HALF, BOTTOM, HALF, TOP, 0xFFFFFFFF, light);
            }
        });
        collector.submitCustomGeometry(poses, RenderType.entitySolid(WOOD), (pose, out) ->
                box(pose, out, -1F / 16, 6F / 16, -1F / 16, 1F / 16, 1, 1F / 16, light));
        poses.popPose();

    }

    @Override public void render(@NotNull HandMillBlockEntity entity, float partialTick, @NotNull PoseStack poses,
            @NotNull net.minecraft.client.renderer.MultiBufferSource buffers, int light, int overlay) {
        var state = createRenderState();
        state.lightCoords = light;
        extractRenderState(entity, state, partialTick,
                net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), null);
        submit(state, poses, new GeometryCollector(buffers));
    }
}
