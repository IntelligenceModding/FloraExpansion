package de.artemis.floraexpansion.client.texture;

/** A reusable 16-pixel salt-crystal mask, clipped to the original food silhouette. */
public final class SaltFoodOverlay {
    private SaltFoodOverlay() {}

    public static void apply(int[] pixels, int width, int height, int seed) {
        boolean[] interior = new boolean[256];
        int opaque = 0;
        for (int y=0; y<16; y++) for (int x=0; x<16; x++) {
            int color = sample(pixels,width,height,x,y);
            if ((color >>> 24) < 128) continue;
            opaque++;
            int light = ((color >> 16 & 255)*3 + (color >> 8 & 255)*6 + (color & 255))/10;
            // Keep dark outlines, eyes, fine fins, and existing bright food highlights intact.
            interior[y*16+x] = light >= 45 && light < 215 && neighbors(pixels,width,height,x,y) >= 3;
        }
        int[] mask = new int[256];
        int crystals = Math.max(1, Math.min(8, opaque/18));
        for (int crystal=0; crystal<crystals; crystal++) {
            int best = -1, score = -1;
            for (int y=0; y<16; y++) for (int x=0; x<16; x++) {
                if (!interior[y*16+x] || nearCrystal(mask,x,y)) continue;
                int rank = hash(x,y,seed);
                if (rank > score) { score=rank; best=y*16+x; }
            }
            if (best < 0) break;
            int x=best%16, y=best/16;
            mask[best] = 0xF8F5E9;
            // Sparse two-pixel grains give the crystals depth without frosting the whole item.
            if ((hash(x,y,seed+7)&3) == 0 && x<15 && interior[best+1]) mask[best+1]=0xC8C6BC;
            else if ((hash(x,y,seed+13)&3) == 0 && y<15 && interior[best+16]) mask[best+16]=0xE3DFD2;
        }
        for (int y=0; y<height; y++) for (int x=0; x<width; x++) {
            int index=y*width+x, salt=mask[(y*16/height)*16+x*16/width];
            if (salt != 0 && (pixels[index] >>> 24) >= 128) pixels[index]=(pixels[index]&0xFF000000)|salt;
        }
    }
    private static boolean nearCrystal(int[] mask,int x,int y) {
        for (int dy=-2; dy<=2; dy++) for (int dx=-2; dx<=2; dx++) {
            int xx=x+dx, yy=y+dy;
            if (xx>=0 && xx<16 && yy>=0 && yy<16 && mask[yy*16+xx]!=0) return true;
        }
        return false;
    }
    private static int neighbors(int[] pixels,int width,int height,int x,int y) {
        int n=0;
        if (x>0 && sample(pixels,width,height,x-1,y)>>>24>=128) n++;
        if (x<15 && sample(pixels,width,height,x+1,y)>>>24>=128) n++;
        if (y>0 && sample(pixels,width,height,x,y-1)>>>24>=128) n++;
        if (y<15 && sample(pixels,width,height,x,y+1)>>>24>=128) n++;
        return n;
    }
    private static int sample(int[] pixels,int width,int height,int x,int y) {
        int sx=Math.min(width-1,(2*x+1)*width/32), sy=Math.min(height-1,(2*y+1)*height/32);
        return pixels[sy*width+sx];
    }
    private static int hash(int x,int y,int seed) {
        int h=seed ^ (x*0x1f123bb5) ^ (y*0x5f356495);
        h ^= h >>> 16; h *= 0x45d9f3b; h ^= h >>> 16;
        return h & 0x7FFFFFFF;
    }
}
