package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.texture.OverlayTexture;

final class MillRenderGeometry {
    static void box(PoseStack.Pose pose, VertexConsumer out, float x0, float y0, float z0,
                            float x1, float y1, float z1, int light) {
        rect(pose, out, Direction.UP, y1, x0, z0, x1, z1, 0xFFFFFFFF, light);
        rect(pose, out, Direction.DOWN, y0, x0, z0, x1, z1, 0xFFFFFFFF, light);
        rect(pose, out, Direction.NORTH, z0, x0, y0, x1, y1, 0xFFFFFFFF, light);
        rect(pose, out, Direction.SOUTH, z1, x0, y0, x1, y1, 0xFFFFFFFF, light);
        rect(pose, out, Direction.WEST, x0, z0, y0, z1, y1, 0xFFFFFFFF, light);
        rect(pose, out, Direction.EAST, x1, z0, y0, z1, y1, 0xFFFFFFFF, light);
    }
    static void rect(PoseStack.Pose pose, VertexConsumer out, Direction face, float plane,
                             float a0, float b0, float a1, float b1, int color, int light) {
        switch (face) {
            case UP -> {
                vertex(pose, out, a0, plane, b0, face, color, light); vertex(pose, out, a0, plane, b1, face, color, light);
                vertex(pose, out, a1, plane, b1, face, color, light); vertex(pose, out, a1, plane, b0, face, color, light);
            }
            case DOWN -> {
                vertex(pose, out, a1, plane, b0, face, color, light); vertex(pose, out, a1, plane, b1, face, color, light);
                vertex(pose, out, a0, plane, b1, face, color, light); vertex(pose, out, a0, plane, b0, face, color, light);
            }
            case NORTH -> {
                vertex(pose, out, a0, b0, plane, face, color, light); vertex(pose, out, a0, b1, plane, face, color, light);
                vertex(pose, out, a1, b1, plane, face, color, light); vertex(pose, out, a1, b0, plane, face, color, light);
            }
            case SOUTH -> {
                vertex(pose, out, a1, b0, plane, face, color, light); vertex(pose, out, a1, b1, plane, face, color, light);
                vertex(pose, out, a0, b1, plane, face, color, light); vertex(pose, out, a0, b0, plane, face, color, light);
            }
            case WEST -> {
                vertex(pose, out, plane, b0, a1, face, color, light); vertex(pose, out, plane, b1, a1, face, color, light);
                vertex(pose, out, plane, b1, a0, face, color, light); vertex(pose, out, plane, b0, a0, face, color, light);
            }
            case EAST -> {
                vertex(pose, out, plane, b0, a0, face, color, light); vertex(pose, out, plane, b1, a0, face, color, light);
                vertex(pose, out, plane, b1, a1, face, color, light); vertex(pose, out, plane, b0, a1, face, color, light);
            }
        }
    }
    private static void vertex(PoseStack.Pose pose, VertexConsumer out, float x, float y, float z,
                               Direction face, int color, int light) {
        float u = 0.5F + (face.getAxis() == Direction.Axis.X ? z : x);
        float v = face.getAxis() == Direction.Axis.Y ? 0.5F + z : 1 - y;
        out.vertex(pose.pose(), x, y, z).color(color).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(pose.normal(), face.getStepX(), face.getStepY(), face.getStepZ()).endVertex();
    }
    private MillRenderGeometry() {}
}
