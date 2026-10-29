package de.artemis.floraexpansion.client.renderer;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ARGB;
import net.minecraft.util.RandomSource;

public final class CrateContentsPattern {
    public static final int SIZE = 12;
    private static final int ICON_SIZE = 16;

    private CrateContentsPattern() {}

    public static int[] create(TextureAtlasSprite sprite, int tint) {
        var contents = sprite.contents();
        var icon = new int[ICON_SIZE * ICON_SIZE];
        for (int y = 0; y < ICON_SIZE; y++) {
            for (int x = 0; x < ICON_SIZE; x++) {
                int color = sprite.getPixelARGB(0, x * contents.width() / ICON_SIZE, y * contents.height() / ICON_SIZE);
                if (ARGB.alpha(color) >= 128) icon[y * ICON_SIZE + x] = ARGB.opaque(ARGB.multiply(color, tint));
            }
        }
        return compose(icon);
    }

    public static int[] compose(int[] icon) {
        if (icon.length != ICON_SIZE * ICON_SIZE) throw new IllegalArgumentException("Expected a 16x16 item icon");
        var opaque = new int[icon.length];
        int opaqueCount = 0;
        for (int color : icon) {
            if (ARGB.alpha(color) >= 128) opaque[opaqueCount++] = ARGB.opaque(color);
        }
        if (opaqueCount == 0) return new int[0];

        var pixels = new int[SIZE * SIZE];
        var random = RandomSource.create(0x4352415445L);
        int remaining = pixels.length;
        for (int stamp = 0; stamp < 64 && remaining > 0; stamp++) {
            int offsetX = stamp == 0 ? 0 : random.nextInt(ICON_SIZE);
            int offsetY = stamp == 0 ? 0 : random.nextInt(ICON_SIZE);
            int rotation = stamp == 0 ? 0 : random.nextInt(4);
            for (int y = 0; y < SIZE; y++) {
                for (int x = 0; x < SIZE; x++) {
                    int target = y * SIZE + x;
                    if (pixels[target] != 0) continue;
                    int sx = (x + 2 + offsetX) % ICON_SIZE;
                    int sy = (y + 2 + offsetY) % ICON_SIZE;
                    for (int turn = 0; turn < rotation; turn++) {
                        int previousX = sx;
                        sx = ICON_SIZE - 1 - sy;
                        sy = previousX;
                    }
                    int color = icon[sy * ICON_SIZE + sx];
                    if (ARGB.alpha(color) >= 128) {
                        pixels[target] = ARGB.opaque(color);
                        remaining--;
                    }
                }
            }
        }
        for (int i = 0; i < pixels.length; i++) {
            if (pixels[i] == 0) pixels[i] = opaque[random.nextInt(opaqueCount)];
        }
        return pixels;
    }
}
