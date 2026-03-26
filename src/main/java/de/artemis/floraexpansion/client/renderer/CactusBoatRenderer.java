package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

@net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
public class CactusBoatRenderer<T extends Boat> extends EntityRenderer<T> {
    private final Pair<ResourceLocation, ListModel<Boat>> boatResource;

    public CactusBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context);
        this.shadowRadius = 0.8F;
        this.boatResource = Pair.of(getTextureLocation(chestBoat), this.createBoatModel(context, chestBoat));
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, boolean chestBoat) {
        ModelLayerLocation modelLayerLocation = chestBoat
                ? ModelLayers.createChestBoatModelName(Boat.Type.OAK)
                : ModelLayers.createBoatModelName(Boat.Type.OAK);

        ModelPart modelPart = context.bakeLayer(modelLayerLocation);

        return chestBoat
                ? new ChestBoatModel(modelPart)
                : new BoatModel(modelPart);
    }

    private static ResourceLocation getTextureLocation(boolean chestBoat) {
        return chestBoat
                ? ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "textures/entity/chest_boat/cactus.png")
                : ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "textures/entity/boat/cactus.png");
    }

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        float hurtTime = (float) entity.getHurtTime() - partialTicks;
        float damage = entity.getDamage() - partialTicks;
        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (hurtTime > 0.0F) {
            poseStack.mulPose(
                    Axis.XP.rotationDegrees(
                            Mth.sin(hurtTime) * hurtTime * damage / 10.0F * (float) entity.getHurtDir()
                    )
            );
        }

        float bubbleAngle = entity.getBubbleAngle(partialTicks);
        if (!Mth.equal(bubbleAngle, 0.0F)) {
            poseStack.mulPose(
                    new Quaternionf().setAngleAxis(
                            entity.getBubbleAngle(partialTicks) * ((float) Math.PI / 180F),
                            1.0F,
                            0.0F,
                            1.0F
                    )
            );
        }

        Pair<ResourceLocation, ListModel<Boat>> pair = this.getModelWithLocation(entity);
        ResourceLocation texture = pair.getFirst();
        ListModel<Boat> model = pair.getSecond();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));

        model.setupAnim(entity, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);

        VertexConsumer vertexConsumer = buffer.getBuffer(model.renderType(texture));
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);

        if (!entity.isUnderWater()) {
            VertexConsumer waterMaskConsumer = buffer.getBuffer(RenderType.waterMask());
            if (model instanceof WaterPatchModel waterPatchModel) {
                waterPatchModel.waterPatch().render(
                        poseStack,
                        waterMaskConsumer,
                        packedLight,
                        OverlayTexture.NO_OVERLAY
                );
            }
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return this.getModelWithLocation(entity).getFirst();
    }

    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(T boat) {
        return this.boatResource;
    }
}