package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/** The shared crate/tub surface: item-colored pixels at a height determined by fullness. */
final class ContentsFillRenderer {
    private static final Identifier WHITE = Identifier.fromNamespaceAndPath("floraexpansion", "dynamic/crate_fill_pixel");
    private final ItemModelResolver items;
    private final AppearanceState appearance = new AppearanceState();
    private final RandomSource random = RandomSource.create(0);
    private final Map<AppearanceKey, int[]> patterns = new LinkedHashMap<>(64, 0.75F, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<AppearanceKey, int[]> eldest) { return size() > 256; }
    };
    ContentsFillRenderer(ItemModelResolver items) {
        this.items = items;
        var white = new NativeImage(1, 1, false);
        white.setPixel(0, 0, 0xFFFFFFFF);
        Minecraft.getInstance().getTextureManager().register(WHITE, new DynamicTexture(() -> "Container fill pixels", white));
    }
    int[] pattern(ItemStack stack, Level level) {
        items.updateForTopItem(appearance, stack, ItemDisplayContext.GUI, level, null, 0);
        if (appearance.isEmpty()) return new int[0];
        random.setSeed(0);
        int layer = random.nextInt(appearance.layers.size());
        random.setSeed(0);
        var material = appearance.pickParticleMaterial(random);
        var sprite = material != null ? material.sprite()
                : Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.ITEMS).missingSprite();
        var tints = appearance.layers.get(layer).tintLayers();
        int tint = tints.isEmpty() ? 0xFFFFFFFF : tints.getInt(0);
        return patterns.computeIfAbsent(new AppearanceKey(sprite, tint), key -> CrateContentsPattern.create(key.sprite, key.tint));
    }
    static float height(float fillRatio) { return (2 + Math.max(1, (int) Math.ceil(13 * fillRatio))) / 16F; }
    void submit(PoseStack poses, SubmitNodeCollector collector, int[] pixels, float fillRatio, int light) {
        if (pixels.length == 0 || fillRatio <= 0) return;
        submitAtHeight(poses, collector, pixels, height(fillRatio), light);
    }
    void submitAtHeight(PoseStack poses, SubmitNodeCollector collector, int[] pixels, float y, int light) {
        if (pixels.length == 0) return;
        collector.submitCustomGeometry(poses, RenderTypes.entitySolid(WHITE), (pose, out) -> {
            for (int z = 0; z < CrateContentsPattern.SIZE; z++) for (int x = 0; x < CrateContentsPattern.SIZE; x++) {
                float x0 = (x+2)/16F, z0 = (z+2)/16F, x1 = (x+3)/16F, z1 = (z+3)/16F;
                int color = pixels[z * CrateContentsPattern.SIZE + x];
                vertex(pose, out, x0, y, z0, color, light); vertex(pose, out, x0, y, z1, color, light);
                vertex(pose, out, x1, y, z1, color, light); vertex(pose, out, x1, y, z0, color, light);
            }
        });
    }
    /** The two open sides and top of the JEI fill, sampled from the world fill pattern. */
    void submitCutaway(PoseStack poses, SubmitNodeCollector collector, int[] pixels, float top, int light) {
        if (pixels.length == 0 || top <= 2F/16) return;
        collector.submitCustomGeometry(poses, RenderTypes.entityCutout(WHITE), (pose, out) -> {
            for (int row = 0; row < 14; row++) {
                float bottom = (2 + row) / 16F;
                if (bottom >= top) break;
                float upper = Math.min(top, bottom + 1F/16);
                for (int i = 0; i < 12; i++) {
                    float lo = (i+2)/16F, hi = lo + 1F/16;
                    int color = pixels[(row % 12)*12+i];
                    sectionFace(pose,out, net.minecraft.core.Direction.NORTH,
                            lo,bottom,2F/16, hi,upper,2F/16, color,light);
                    sectionFace(pose,out, net.minecraft.core.Direction.EAST,
                            14F/16,bottom,lo, 14F/16,upper,hi, color,light);
                }
            }
            for (int z = 0; z < 12; z++) for (int x = 0; x < 12; x++) {
                float a = (x+2)/16F, b = (z+2)/16F;
                sectionFace(pose,out, net.minecraft.core.Direction.UP,
                        a,top,b, a+1F/16,top,b+1F/16, pixels[z*12+x],light);
            }
        });
    }
    private static void sectionFace(PoseStack.Pose pose, VertexConsumer out, net.minecraft.core.Direction direction,
                                    float x0,float y0,float z0, float x1,float y1,float z1, int color,int light) {
        var info = net.minecraft.client.renderer.FaceInfo.fromFacing(direction);
        for (int i = 0; i < 4; i++) {
            var corner = info.getVertexInfo(i);
            out.addVertex(pose,
                            corner.xFace().select(x0,y0,z0,x1,y1,z1),
                            corner.yFace().select(x0,y0,z0,x1,y1,z1),
                            corner.zFace().select(x0,y0,z0,x1,y1,z1))
                    .setColor(color).setUv(0.5F,0.5F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                    .setNormal(pose,direction.getStepX(),direction.getStepY(),direction.getStepZ());
        }
    }    private static void vertex(PoseStack.Pose pose, VertexConsumer out, float x, float y, float z, int color, int light) {
        out.addVertex(pose, x, y, z).setColor(color).setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0, 1, 0);
    }
    private record AppearanceKey(TextureAtlasSprite sprite, int tint) {}
    private static final class AppearanceState extends ItemStackRenderState {
        private final ArrayList<LayerRenderState> layers = new ArrayList<>();
        @Override public LayerRenderState newLayer() { var layer = super.newLayer(); layers.add(layer); return layer; }
        @Override public void clear() { super.clear(); layers.clear(); }
    }
}





