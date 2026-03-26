package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import org.jetbrains.annotations.NotNull;

public class CactusBoatRenderer<T extends Boat> extends EntityRenderer<T> {
    private static final ResourceLocation CACTUS_BOAT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "textures/entity/boat/cactus.png");

    private static final ResourceLocation CACTUS_CHEST_BOAT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "textures/entity/chest_boat/cactus.png");

    private final ListModel<Boat> model;
    private final ResourceLocation texture;

    @SuppressWarnings("unchecked")
    public CactusBoatRenderer(EntityRendererProvider.Context context, boolean chestBoat) {
        super(context);
        this.shadowRadius = 0.8F;

        if (chestBoat) {
            this.model = (ListModel<Boat>) new ChestBoatModel(context.bakeLayer(ModelLayers.createChestBoatModelName(Boat.Type.OAK)));
            this.texture = CACTUS_CHEST_BOAT_TEXTURE;
        } else {
            this.model = (ListModel<Boat>) new BoatModel(context.bakeLayer(ModelLayers.createBoatModelName(Boat.Type.OAK)));
            this.texture = CACTUS_BOAT_TEXTURE;
        }
    }

    @Override
    public void render(@NotNull T boat, float entityYaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));

        float hurtTime = boat.getHurtTime() - partialTick;
        float damage = boat.getDamage() - partialTick;
        if (damage < 0.0F) {
            damage = 0.0F;
        }

        if (hurtTime > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(
                    Mth.sin(hurtTime) * hurtTime * damage / 10.0F * (float) boat.getHurtDir()
            ));
        }

        float bubbleAngle = boat.getBubbleAngle(partialTick);
        if (!Mth.equal(bubbleAngle, 0.0F)) {
            poseStack.mulPose(Axis.XP.rotationDegrees(bubbleAngle));
        }

        poseStack.scale(-1.0F, -1.0F, 1.0F);

        this.model.setupAnim(boat, partialTick, 0.0F, -0.1F, 0.0F, 0.0F);
        var vertexConsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(boat)));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(boat, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T entity) {
        return this.texture;
    }
}