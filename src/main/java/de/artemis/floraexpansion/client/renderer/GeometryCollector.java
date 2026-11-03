package de.artemis.floraexpansion.client.renderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import java.util.function.BiConsumer;
/** Emits the shared world and JEI geometry through 1.21.1's immediate renderer. */
final class GeometryCollector {
    private final MultiBufferSource buffers;
    GeometryCollector(MultiBufferSource buffers) { this.buffers = buffers; }
    void submitCustomGeometry(PoseStack poses, RenderType type, BiConsumer<PoseStack.Pose, VertexConsumer> geometry) {
        geometry.accept(poses.last(), buffers.getBuffer(type));
    }
}
