package de.artemis.floraexpansion.client.texture;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public record SaltSpriteSource(ResourceLocation resource, ResourceLocation sprite, String effect) implements SpriteSource {
    public static final Codec<SaltSpriteSource> MAP_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("resource").forGetter(SaltSpriteSource::resource),
            ResourceLocation.CODEC.fieldOf("sprite").forGetter(SaltSpriteSource::sprite),
            com.mojang.serialization.Codec.STRING.fieldOf("effect").forGetter(SaltSpriteSource::effect)
    ).apply(instance, SaltSpriteSource::new));

    public static final net.minecraft.client.renderer.texture.atlas.SpriteSourceType TYPE = new net.minecraft.client.renderer.texture.atlas.SpriteSourceType(MAP_CODEC);
    @NotNull @Override public net.minecraft.client.renderer.texture.atlas.SpriteSourceType type() { return TYPE; }

    @Override
    public void run(ResourceManager manager, @NotNull Output output) {
        var replacement = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(sprite));
        if (replacement.isPresent()) {
            output.add(sprite, replacement.get());
            return;
        }
        var source = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(resource));
        if (source.isEmpty()) {
            FloraExpansion.LOGGER.warn("Missing salt item source texture: {}", resource);
            return;
        }
        output.add(sprite, () -> SpriteLoader.load(sprite, source.get(), (id, frame, image, metadata) -> {
            for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) {
                int color = NativePixels.get(image, x, y);
                if ((color >>> 24) == 0) continue;
                if (effect.equals("salt_water")) {
                    int red = (color >>> 16) & 255, green = (color >>> 8) & 255, blue = color & 255;
                    if (blue > red + 12 && blue > green + 4) {
                        int brightness = Math.min(255, 130 + blue / 2);
                        red = brightness * 121 / 255;
                        green = brightness * 184 / 255;
                        blue = brightness * 202 / 255;
                        color = (color & 0xFF000000) | red << 16 | green << 8 | blue;
                    }
                    NativePixels.set(image, x, y, color);
                    continue;
                }
                if (effect.equals("cured")) {
                    int red = ((color >> 16) & 255) * 3 / 4;
                    int green = ((color >> 8) & 255) * 4 / 5;
                    int blue = (color & 255) * 4 / 5;
                    color = (color & 0xFF000000) | red << 16 | green << 8 | blue;
                }
                NativePixels.set(image, x, y, color);
            }
            if (effect.equals("salted") || effect.equals("cured")) {
                // Work on complete frame snapshots so crystal placement never depends on write order.
                for (int fy=0; fy<image.getHeight(); fy+=frame.height()) {
                    for (int fx=0; fx<image.getWidth(); fx+=frame.width()) {
                        int[] pixels = new int[frame.width()*frame.height()];
                        for (int y=0; y<frame.height(); y++) for (int x=0; x<frame.width(); x++)
                            pixels[y*frame.width()+x]=NativePixels.get(image, fx+x,fy+y);
                        SaltFoodOverlay.apply(pixels,frame.width(),frame.height(),resource.getPath().hashCode());
                        for (int y=0; y<frame.height(); y++) for (int x=0; x<frame.width(); x++)
                            NativePixels.set(image, fx+x,fy+y,pixels[y*frame.width()+x]);
                    }
                }
            }
            return new SpriteContents(id, frame, image, metadata);
        }));
    }
}
