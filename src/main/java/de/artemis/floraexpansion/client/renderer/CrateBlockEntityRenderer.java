package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.artemis.floraexpansion.client.renderer.state.CrateRenderState;
import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class CrateBlockEntityRenderer implements BlockEntityRenderer<CrateBlockEntity, CrateRenderState> {
    private static final float MIN = 2.0F / 16.0F;
    private static final float MAX = 14.0F / 16.0F;
    private static final float MIN_Y = 2.0F / 16.0F;
    private static final float MAX_Y = 15.0F / 16.0F;
    private static final int COLOR = 0xFFFFFFFF;
    private static final Material DEFAULT_CONTENTS =
            contentsMaterial("default");
    private static final Material BLUEBERRIES_CONTENTS =
            contentsMaterial("blueberries");
    private final MaterialSet materials;

    public CrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.materials = context.materials();
    }

    @Override
    public @NotNull CrateRenderState createRenderState() {
        return new CrateRenderState();
    }

    @Override
    public void extractRenderState(@NotNull CrateBlockEntity blockEntity,
                                   @NotNull CrateRenderState renderState,
                                   float partialTick,
                                   @NotNull Vec3 cameraPosition,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, renderState, partialTick, cameraPosition, breakProgress);
        ItemStack storedType = blockEntity.getStoredTypeCopy();

        renderState.fillRatio = blockEntity.getFillRatio();
        renderState.contentsSprite = storedType.isEmpty() ? null : this.materials.get(getContentsMaterial(storedType));
    }

    @Override
    public void submit(@NotNull CrateRenderState state,
                       @NotNull PoseStack poseStack,
                       @NotNull SubmitNodeCollector collector,
                       @NotNull CameraRenderState cameraState) {
        if (state.blockState.hasProperty(CrateBlock.PACKED) && state.blockState.getValue(CrateBlock.PACKED)) {
            return;
        }

        if (state.blockState.hasProperty(CrateBlock.POWERED) && state.blockState.getValue(CrateBlock.POWERED)) {
            return;
        }

        if (state.contentsSprite == null || state.fillRatio <= 0.0F) {
            return;
        }

        float visibleFillRatio = Math.max(0.12F, state.fillRatio);
        float y = MIN_Y + (MAX_Y - MIN_Y) * visibleFillRatio;
        TextureAtlasSprite sprite = state.contentsSprite;
        int light = state.lightCoords;

        poseStack.pushPose();
        collector.submitCustomGeometry(poseStack, RenderTypes.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS), (pose, consumer) ->
                renderContentsCuboid(pose, consumer, sprite, y, light));
        poseStack.popPose();
    }

    private static Material getContentsMaterial(ItemStack storedType) {
        if (storedType.is(ModItems.BLUEBERRIES.get())) {
            return BLUEBERRIES_CONTENTS;
        }

        return DEFAULT_CONTENTS;
    }

    private static Material contentsMaterial(String path) {
        return new Material(
                TextureAtlas.LOCATION_BLOCKS,
                Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "block/crate_contents/" + path)
        );
    }

    private static void renderContentsCuboid(PoseStack.Pose pose,
                                             VertexConsumer consumer,
                                             TextureAtlasSprite sprite,
                                             float y,
                                             int light) {
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float sideV0 = sprite.getV(1.0F - (y - MIN_Y) / (MAX_Y - MIN_Y));

        quad(pose, consumer, MIN, y, MIN, MAX, y, MIN, MAX, y, MAX, MIN, y, MAX, u0, v0, u1, v1, light, 0.0F, 1.0F, 0.0F);
        quad(pose, consumer, MIN, MIN_Y, MIN, MIN, y, MIN, MAX, y, MIN, MAX, MIN_Y, MIN, u0, v1, u1, sideV0, light, 0.0F, 0.0F, -1.0F);
        quad(pose, consumer, MAX, MIN_Y, MAX, MAX, y, MAX, MIN, y, MAX, MIN, MIN_Y, MAX, u0, v1, u1, sideV0, light, 0.0F, 0.0F, 1.0F);
        quad(pose, consumer, MIN, MIN_Y, MAX, MIN, y, MAX, MIN, y, MIN, MIN, MIN_Y, MIN, u0, v1, u1, sideV0, light, -1.0F, 0.0F, 0.0F);
        quad(pose, consumer, MAX, MIN_Y, MIN, MAX, y, MIN, MAX, y, MAX, MAX, MIN_Y, MAX, u0, v1, u1, sideV0, light, 1.0F, 0.0F, 0.0F);
    }

    private static void quad(PoseStack.Pose pose,
                             VertexConsumer consumer,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             float x4, float y4, float z4,
                             float u0, float v0, float u1, float v1,
                             int light,
                             float normalX, float normalY, float normalZ) {
        vertex(pose, consumer, x1, y1, z1, u0, v0, light, normalX, normalY, normalZ);
        vertex(pose, consumer, x2, y2, z2, u0, v1, light, normalX, normalY, normalZ);
        vertex(pose, consumer, x3, y3, z3, u1, v1, light, normalX, normalY, normalZ);
        vertex(pose, consumer, x4, y4, z4, u1, v0, light, normalX, normalY, normalZ);
    }

    private static void vertex(PoseStack.Pose pose,
                               VertexConsumer consumer,
                               float x, float y, float z,
                               float u, float v,
                               int light,
                               float normalX, float normalY, float normalZ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(COLOR)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
