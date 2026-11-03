package de.artemis.floraexpansion.client.texture;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public record FloweringMossSpriteSource() implements SpriteSource {
    public static final MapCodec<FloweringMossSpriteSource> MAP_CODEC = MapCodec.unit(FloweringMossSpriteSource::new);
    private static final ResourceLocation SPRITE = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "item/flowering_desert_moss");
    private static final ResourceLocation MOSS = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "block/desert_moss_2");
    private static final ResourceLocation FLOWERS = ResourceLocation.withDefaultNamespace("block/pink_petals");

    public static final net.minecraft.client.renderer.texture.atlas.SpriteSourceType TYPE = new net.minecraft.client.renderer.texture.atlas.SpriteSourceType(MAP_CODEC);
    @Override
    @NotNull public net.minecraft.client.renderer.texture.atlas.SpriteSourceType type() { return TYPE; }

    @Override
    public void run(ResourceManager manager, @NotNull Output output) {
        var replacement = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(SPRITE));
        if (replacement.isPresent()) {
            output.add(SPRITE, replacement.get());
            return;
        }
        var moss = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(MOSS));
        var flowers = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(FLOWERS));
        if (moss.isEmpty() || flowers.isEmpty()) {
            FloraExpansion.LOGGER.warn("Missing source texture for flowering desert moss icon");
            return;
        }
        output.add(SPRITE, loader -> loader.loadSprite(SPRITE, moss.get(), (id, frame, image, metadata) -> {
            try (var stream = flowers.get().open(); var petals = NativeImage.read(stream)) {
                applyFlowers(image, frame, petals);
                return new SpriteContents(id, frame, image, metadata);
            } catch (java.io.IOException | RuntimeException error) {
                image.close();
                FloraExpansion.LOGGER.error("Unable to generate flowering desert moss icon", error);
                return null;
            }
        }));
    }

    public static void applyFlowers(NativeImage image, FrameSize frame, NativeImage petals) {
        for (int frameY = 0; frameY < image.getHeight(); frameY += frame.height()) {
            for (int frameX = 0; frameX < image.getWidth(); frameX += frame.width()) {
                for (int[] flower : new int[][]{{3, 2}, {11, 5}, {5, 10}}) {
                    int left = flower[0] * frame.width() / 16;
                    int top = flower[1] * frame.height() / 16;
                    for (int y = top; y < (flower[1] + 3) * frame.height() / 16; y++) {
                        for (int x = left; x < (flower[0] + 3) * frame.width() / 16; x++) {
                            int sourceX = 3 * petals.getWidth() / 16 + (x - left) * petals.getWidth() / frame.width();
                            int sourceY = (y - top) * petals.getHeight() / frame.height();
                            int color = NativePixels.get(petals, sourceX, sourceY);
                            if ((color >>> 24) != 0) NativePixels.set(image, frameX + x, frameY + y, color);
                        }
                    }
                }
            }
        }
    }
}
