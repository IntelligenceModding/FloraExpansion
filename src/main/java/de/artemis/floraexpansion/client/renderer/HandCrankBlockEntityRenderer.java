package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.artemis.floraexpansion.client.renderer.state.HandCrankRenderState;
import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity;
import de.artemis.floraexpansion.common.util.CrankOrientation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import static de.artemis.floraexpansion.client.renderer.MillRenderGeometry.*;

public class HandCrankBlockEntityRenderer implements BlockEntityRenderer<HandCrankBlockEntity, HandCrankRenderState> {
    private static final Identifier WOOD = Identifier.withDefaultNamespace("textures/block/stripped_spruce_log.png");
    private static final Identifier STONE = Identifier.withDefaultNamespace("textures/block/polished_andesite.png");
    private static final String[] DIGITS = {"111101101101111", "010110010010111", "111001111100111", "111001111001111",
            "101101111001001", "111100111001111", "111100111101111", "111001010010010", "111101111101111", "111101111001111"};
    public HandCrankBlockEntityRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public HandCrankRenderState createRenderState() { return new HandCrankRenderState(); }
    @Override public void extractRenderState(HandCrankBlockEntity crank, HandCrankRenderState state, float partialTick,
                                             Vec3 camera, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(crank, state, partialTick, camera, breakProgress);
        state.angle = crank.getAngle(partialTick);
        state.strengthMode = crank.getBlockState().getValue(HandCrankBlock.STRENGTH_MODE);
        state.signal = crank.getBlockState().getValue(HandCrankBlock.POWER);
        state.hasBase = !crank.isOnMill();
        state.facing = crank.getBlockState().getValue(HandCrankBlock.FACING);
    }
    @Override public void submit(HandCrankRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
        int light = state.lightCoords;
        poses.pushPose();
        poses.translate(0.5, 0.5, 0.5);
        switch (state.facing) {
            case DOWN -> poses.mulPose(Axis.XP.rotationDegrees(180));
            case NORTH -> poses.mulPose(Axis.XP.rotationDegrees(-90));
            case SOUTH -> poses.mulPose(Axis.XP.rotationDegrees(90));
            case EAST -> poses.mulPose(Axis.ZP.rotationDegrees(-90));
            case WEST -> poses.mulPose(Axis.ZP.rotationDegrees(90));
            default -> {}
        }
        // Orient the entire model, including the base texture, in the same wall frame.
        poses.mulPose(Axis.YP.rotationDegrees(CrankOrientation.of(state.facing).mountingRoll()));
        poses.translate(0, -0.5, 0);
        if (state.hasBase) {
            collector.submitCustomGeometry(poses, RenderTypes.entitySolid(STONE), (pose, out) -> {
                box(pose, out, -4F / 16, 0, -4F / 16, 4F / 16, 2F / 16, 4F / 16, light);
            });
            if (state.strengthMode) {
                collector.submitCustomGeometry(poses, RenderTypes.entitySolid(STONE), (pose, out) -> {
                    // The strength display stays fixed on the base, clear of the rotating shaft.
                    String number = Integer.toString(state.signal);
                    float pixel = 0.025F;
                    float start = -(number.length() * 4 - 1) * pixel / 2;
                    for (int digit = 0; digit < number.length(); digit++) {
                        String glyph = DIGITS[number.charAt(digit) - '0'];
                        for (int row = 0; row < 5; row++) for (int column = 0; column < 3; column++) {
                            if (glyph.charAt(row * 3 + column) != '1') continue;
                            float x = start + (digit * 4 + column) * pixel;
                            float z = 2.5F / 16 + (row - 2.5F) * pixel;
                            rect(pose, out, Direction.UP, 2F / 16 + 0.001F,
                                    x, z, x + pixel, z + pixel, 0xFF292929, light);
                        }
                    }
                });
            }
        }
        poses.mulPose(Axis.YP.rotationDegrees(-state.angle - CrankOrientation.of(state.facing).restingAngle()));
        collector.submitCustomGeometry(poses, RenderTypes.entitySolid(WOOD), (pose, out) -> {
            box(pose, out, -1F / 16, 0, -1F / 16, 1F / 16, 7F / 16, 1F / 16, light);
            box(pose, out, 1F / 16, 5F / 16, -1F / 16, 6F / 16, 7F / 16, 1F / 16, light);
        });
        collector.submitCustomGeometry(poses, RenderTypes.entitySolid(WOOD), (pose, out) ->
                box(pose, out, 4F / 16, 7F / 16, -1F / 16, 6F / 16, 11F / 16, 1F / 16, light));
        poses.popPose();
    }
}
