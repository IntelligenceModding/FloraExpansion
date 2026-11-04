package de.artemis.floraexpansion.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.LinkedHashMap;
import java.util.Map;

/** The shared crate/tub surface: item-colored pixels at a height determined by fullness. */
final class ContentsFillRenderer {
    private static final ResourceLocation WHITE = ResourceLocation.fromNamespaceAndPath("floraexpansion", "dynamic/crate_fill_pixel");
    private final Map<AppearanceKey, int[]> patterns = new LinkedHashMap<>(64, 0.75F, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<AppearanceKey, int[]> eldest) { return size() > 256; }
    };
    ContentsFillRenderer() {
        var white = new NativeImage(1, 1, false);
        white.setPixelRGBA(0, 0, 0xFFFFFFFF);
        Minecraft.getInstance().getTextureManager().register(WHITE, new DynamicTexture(white));
    }
    int[] pattern(ItemStack stack, Level level) {
        if (stack.isEmpty()) return new int[0];
        var mc = Minecraft.getInstance();
        var model = mc.getItemRenderer().getModel(stack, level, null, 0);
        var sprite = model.getParticleIcon(net.minecraftforge.client.model.data.ModelData.EMPTY);
        int tint = mc.getItemColors().getColor(stack, 0);
        if (tint == -1) tint = 0xFFFFFFFF;
        else tint |= 0xFF000000;
        return patterns.computeIfAbsent(new AppearanceKey(sprite, tint), key -> CrateContentsPattern.create(key.sprite, key.tint));
    }
    static float height(float fillRatio) { return (2 + Math.max(1, (int) Math.ceil(13 * fillRatio))) / 16F; }
    void submit(PoseStack poses, GeometryCollector collector, int[] pixels, float fillRatio, int light) {
        if (pixels.length == 0 || fillRatio <= 0) return;
        submitAtHeight(poses, collector, pixels, height(fillRatio), light);
    }
    void submitAtHeight(PoseStack poses, GeometryCollector collector, int[] pixels, float y, int light) {
        if (pixels.length == 0) return;
        collector.submitCustomGeometry(poses, RenderType.entitySolid(WHITE), (pose, out) -> {
            for (int z = 0; z < CrateContentsPattern.SIZE; z++) for (int x = 0; x < CrateContentsPattern.SIZE; x++) {
                float x0 = (x+2)/16F, z0 = (z+2)/16F, x1 = (x+3)/16F, z1 = (z+3)/16F;
                int color = pixels[z * CrateContentsPattern.SIZE + x];
                vertex(pose, out, x0, y, z0, color, light); vertex(pose, out, x0, y, z1, color, light);
                vertex(pose, out, x1, y, z1, color, light); vertex(pose, out, x1, y, z0, color, light);
            }
        });
    }
    /** The two open sides and top of the JEI fill, sampled from the world fill pattern. */
    void submitCutaway(PoseStack poses, GeometryCollector collector, int[] pixels, float top, int light) {
        if (pixels.length == 0 || top <= 2F/16) return;
        // Keep the exposed cut surfaces visible through the GUI's mirrored transform.
        collector.submitCustomGeometry(poses, RenderType.entityCutoutNoCull(WHITE), (pose, out) -> {
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
            out.vertex(pose.pose(),
                            coordinate(corner.xFace, x0,y0,z0,x1,y1,z1),
                            coordinate(corner.yFace, x0,y0,z0,x1,y1,z1),
                            coordinate(corner.zFace, x0,y0,z0,x1,y1,z1))
                    .color(color).uv(0.5F,0.5F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                    .normal(pose.normal(),direction.getStepX(),direction.getStepY(),direction.getStepZ()).endVertex();
        }
    }    private static void vertex(PoseStack.Pose pose, VertexConsumer out, float x, float y, float z, int color, int light) {
        out.vertex(pose.pose(), x, y, z).color(color).uv(0.5F, 0.5F)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0, 1, 0).endVertex();
    }
    static float coordinate(int face, float x0,float y0,float z0,float x1,float y1,float z1) {
        return switch (net.minecraft.core.Direction.from3DDataValue(face)) {
            case DOWN -> y0; case UP -> y1; case NORTH -> z0; case SOUTH -> z1; case WEST -> x0; case EAST -> x1;
        };
    }
    private record AppearanceKey(TextureAtlasSprite sprite, int tint) {}
}
