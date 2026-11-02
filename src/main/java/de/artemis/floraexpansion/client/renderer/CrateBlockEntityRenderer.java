package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import de.artemis.floraexpansion.client.renderer.state.CrateRenderState;
import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import de.artemis.floraexpansion.common.config.ClientConfig;

public class CrateBlockEntityRenderer implements BlockEntityRenderer<CrateBlockEntity, CrateRenderState> {
    private static final int[] EMPTY = new int[0];
    private final ContentsFillRenderer fill;

    public CrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        fill = new ContentsFillRenderer(context.itemModelResolver());
    }

    @Override
    public int getViewDistance() {
        return (int) Math.ceil(ClientConfig.crateFillDistance.get());
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
        renderState.fillRatio = blockEntity.getFillRatio();
        renderState.contentsPixels = EMPTY;
        if (!ClientConfig.crateFill.get() || cameraPosition.distanceToSqr(Vec3.atCenterOf(blockEntity.getBlockPos())) > Math.pow(ClientConfig.crateFillDistance.get(), 2)
                || renderState.fillRatio <= 0.0F || blockEntity.getBlockState().getValue(CrateBlock.PACKED)
                || blockEntity.getBlockState().getValue(CrateBlock.POWERED)) {
            return;
        }

        renderState.contentsPixels = fill.pattern(blockEntity.getStoredTypeCopy(), blockEntity.getLevel());
    }

    @Override
    public void submit(@NotNull CrateRenderState state,
                       @NotNull PoseStack poseStack,
                       @NotNull SubmitNodeCollector collector,
                       @NotNull CameraRenderState cameraState) {
        fill.submit(poseStack, collector, state.contentsPixels, state.fillRatio, state.lightCoords);
    }
}
