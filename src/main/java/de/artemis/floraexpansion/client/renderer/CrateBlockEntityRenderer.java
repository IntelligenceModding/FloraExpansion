package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CrateBlockEntityRenderer implements BlockEntityRenderer<CrateBlockEntity> {
    private static final String CONTENT_TEXTURE_ROOT = "textures/block/crate_contents/";
    private static final ResourceLocation DEFAULT_CONTENT_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            FloraExpansion.MODID,
            CONTENT_TEXTURE_ROOT + "default.png"
    );
    private static final ResourceLocation FALLBACK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            FloraExpansion.MODID,
            "textures/block/linen_block.png"
    );
    private static final float MIN_X = 2.0F / 16.0F;
    private static final float MAX_X = 14.0F / 16.0F;
    private static final float MIN_Z = 2.0F / 16.0F;
    private static final float MAX_Z = 14.0F / 16.0F;
    private static final float MIN_Y = 3.0F / 16.0F;
    private static final float MAX_Y = 13.0F / 16.0F;
    private static final float SURFACE_OFFSET = 0.001F;

    public CrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull CrateBlockEntity blockEntity, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        float fillRatio = blockEntity.getFillRatio();
        if (fillRatio <= 0.0F) {
            return;
        }

        float surfaceY = MIN_Y + (MAX_Y - MIN_Y) * fillRatio + SURFACE_OFFSET;
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.resolveFillTexture(blockEntity)));
        PoseStack.Pose pose = poseStack.last();

        this.addVertex(vertexConsumer, pose, MIN_X, surfaceY, MAX_Z, 0.0F, 1.0F, packedLight, packedOverlay);
        this.addVertex(vertexConsumer, pose, MAX_X, surfaceY, MAX_Z, 1.0F, 1.0F, packedLight, packedOverlay);
        this.addVertex(vertexConsumer, pose, MAX_X, surfaceY, MIN_Z, 1.0F, 0.0F, packedLight, packedOverlay);
        this.addVertex(vertexConsumer, pose, MIN_X, surfaceY, MIN_Z, 0.0F, 0.0F, packedLight, packedOverlay);
    }

    private ResourceLocation resolveFillTexture(CrateBlockEntity blockEntity) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemStack storedType = blockEntity.getStoredTypeCopy();

        if (!storedType.isEmpty()) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(storedType.getItem());

            ResourceLocation namespacedTexture = ResourceLocation.fromNamespaceAndPath(
                    FloraExpansion.MODID,
                    CONTENT_TEXTURE_ROOT + itemId.getNamespace() + "/" + itemId.getPath() + ".png"
            );
            if (minecraft.getResourceManager().getResource(namespacedTexture).isPresent()) {
                return namespacedTexture;
            }

            ResourceLocation flatTexture = ResourceLocation.fromNamespaceAndPath(
                    FloraExpansion.MODID,
                    CONTENT_TEXTURE_ROOT + itemId.getPath() + ".png"
            );
            if (minecraft.getResourceManager().getResource(flatTexture).isPresent()) {
                return flatTexture;
            }
        }

        if (minecraft.getResourceManager().getResource(DEFAULT_CONTENT_TEXTURE).isPresent()) {
            return DEFAULT_CONTENT_TEXTURE;
        }

        return FALLBACK_TEXTURE;
    }

    private void addVertex(VertexConsumer vertexConsumer, PoseStack.Pose pose, float x, float y, float z, float u, float v, int packedLight, int packedOverlay) {
        vertexConsumer.addVertex(pose.pose(), x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
