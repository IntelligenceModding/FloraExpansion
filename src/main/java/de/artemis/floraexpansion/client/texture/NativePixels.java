package de.artemis.floraexpansion.client.texture;
import com.mojang.blaze3d.platform.NativeImage;
/** Keep 0.5's ARGB algorithms independent of 1.20.1's native ABGR storage. */
public final class NativePixels {
    private static int swap(int color) { return (color & 0xFF00FF00) | ((color >>> 16) & 255) | ((color & 255) << 16); }
    public static int get(NativeImage image, int x, int y) { return swap(image.getPixelRGBA(x,y)); }
    public static void set(NativeImage image, int x, int y, int argb) { image.setPixelRGBA(x,y,swap(argb)); }
    private NativePixels() {}
}
