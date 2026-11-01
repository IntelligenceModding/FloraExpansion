package de.artemis.floraexpansion.client.texture;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public record SaltSpriteSource(Identifier resource, Identifier sprite, String effect) implements SpriteSource {
    public static final MapCodec<SaltSpriteSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("resource").forGetter(SaltSpriteSource::resource),
            Identifier.CODEC.fieldOf("sprite").forGetter(SaltSpriteSource::sprite),
            com.mojang.serialization.Codec.STRING.fieldOf("effect").forGetter(SaltSpriteSource::effect)
    ).apply(instance, SaltSpriteSource::new));

    @Override public MapCodec<? extends SpriteSource> codec() { return MAP_CODEC; }

    @Override
    public void run(ResourceManager manager, Output output) {
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
        output.add(sprite, loader -> loader.loadSprite(sprite, source.get(), (id, frame, image, animation, metadata, texture) -> {
            for (int y = 0; y < image.getHeight(); y++) for (int x = 0; x < image.getWidth(); x++) {
                int color = image.getPixel(x, y);
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
                    image.setPixel(x, y, color);
                    continue;
                }
                if (effect.equals("cured")) {
                    int red = ((color >> 16) & 255) * 3 / 4;
                    int green = ((color >> 8) & 255) * 4 / 5;
                    int blue = (color & 255) * 4 / 5;
                    color = (color & 0xFF000000) | red << 16 | green << 8 | blue;
                }
                image.setPixel(x, y, color);
            }
            if (effect.equals("salted") || effect.equals("cured")) {
                // Work on complete frame snapshots so crystal placement never depends on write order.
                for (int fy=0; fy<image.getHeight(); fy+=frame.height()) {
                    for (int fx=0; fx<image.getWidth(); fx+=frame.width()) {
                        int[] pixels = new int[frame.width()*frame.height()];
                        for (int y=0; y<frame.height(); y++) for (int x=0; x<frame.width(); x++)
                            pixels[y*frame.width()+x]=image.getPixel(fx+x,fy+y);
                        SaltFoodOverlay.apply(pixels,frame.width(),frame.height(),resource.getPath().hashCode());
                        for (int y=0; y<frame.height(); y++) for (int x=0; x<frame.width(); x++)
                            image.setPixel(fx+x,fy+y,pixels[y*frame.width()+x]);
                    }
                }
            }
            return new SpriteContents(id, frame, image, animation, metadata, texture);
        }));
    }
}

