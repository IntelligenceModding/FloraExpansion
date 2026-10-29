package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.client.renderer.state.CrateRenderState;
import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import de.artemis.floraexpansion.common.config.ClientConfig;

public class CrateBlockEntityRenderer implements BlockEntityRenderer<CrateBlockEntity, CrateRenderState> {
    private static final Identifier WHITE_TEXTURE = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "dynamic/crate_fill_pixel");
    private static final int[] EMPTY = new int[0];
    private final ItemModelResolver itemModels;
    private final AppearanceState appearance = new AppearanceState();
    private final RandomSource materialSelector = RandomSource.create(0);
    private final Map<AppearanceKey, int[]> patterns = new LinkedHashMap<>(64, 0.75F, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<AppearanceKey, int[]> eldest) {
            return size() > 256;
        }
    };

    public CrateBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModels = context.itemModelResolver();
        var white = new NativeImage(1, 1, false);
        white.setPixel(0, 0, 0xFFFFFFFF);
        Minecraft.getInstance().getTextureManager().register(WHITE_TEXTURE, new DynamicTexture(() -> "Crate fill pixels", white));
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

        this.itemModels.updateForTopItem(this.appearance, blockEntity.getStoredTypeCopy(),
                ItemDisplayContext.GUI, blockEntity.getLevel(), null, 0);
        if (this.appearance.isEmpty()) return;

        this.materialSelector.setSeed(0);
        int layer = this.materialSelector.nextInt(this.appearance.layers.size());
        this.materialSelector.setSeed(0);
        var material = this.appearance.pickParticleMaterial(this.materialSelector);
        var sprite = material != null ? material.sprite()
                : Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.ITEMS).missingSprite();
        var tints = this.appearance.layers.get(layer).tintLayers();
        int tint = tints.isEmpty() ? 0xFFFFFFFF : tints.getInt(0);
        var key = new AppearanceKey(sprite, tint);
        renderState.contentsPixels = this.patterns.computeIfAbsent(key, entry -> CrateContentsPattern.create(entry.sprite, entry.tint));
    }

    @Override
    public void submit(@NotNull CrateRenderState state,
                       @NotNull PoseStack poseStack,
                       @NotNull SubmitNodeCollector collector,
                       @NotNull CameraRenderState cameraState) {
        if (state.contentsPixels.length == 0 || state.fillRatio <= 0.0F) return;
        float y = (2 + Math.max(1, (int) Math.ceil(13 * state.fillRatio))) / 16.0F;
        int[] pixels = state.contentsPixels;
        int light = state.lightCoords;
        collector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(WHITE_TEXTURE), (pose, consumer) -> {
            for (int z = 0; z < CrateContentsPattern.SIZE; z++) {
                for (int x = 0; x < CrateContentsPattern.SIZE; x++) {
                    float x0 = (x + 2) / 16.0F;
                    float z0 = (z + 2) / 16.0F;
                    float x1 = (x + 3) / 16.0F;
                    float z1 = (z + 3) / 16.0F;
                    int color = pixels[z * CrateContentsPattern.SIZE + x];
                    vertex(pose, consumer, x0, y, z0, color, light);
                    vertex(pose, consumer, x0, y, z1, color, light);
                    vertex(pose, consumer, x1, y, z1, color, light);
                    vertex(pose, consumer, x1, y, z0, color, light);
                }
            }
        });
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer consumer, float x, float y, float z, int color, int light) {
        consumer.addVertex(pose, x, y, z).setColor(color).setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
    }

    private record AppearanceKey(TextureAtlasSprite sprite, int tint) {}

    private static final class AppearanceState extends ItemStackRenderState {
        private final ArrayList<LayerRenderState> layers = new ArrayList<>();

        @Override
        public LayerRenderState newLayer() {
            var layer = super.newLayer();
            this.layers.add(layer);
            return layer;
        }

        @Override
        public void clear() {
            super.clear();
            this.layers.clear();
        }
    }
}
