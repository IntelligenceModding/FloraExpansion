package de.artemis.floraexpansion.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public record JamPortionSpriteSource(Identifier resource) implements SpriteSource {
    public static final MapCodec<JamPortionSpriteSource> MAP_CODEC = Identifier.CODEC.fieldOf("resource")
            .xmap(JamPortionSpriteSource::new, JamPortionSpriteSource::resource);
    private static final Identifier EMPTY_JAR = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "item/glass_jar");

    @Override
    public MapCodec<? extends SpriteSource> codec() { return MAP_CODEC; }

    @Override
    public void run(ResourceManager manager, Output output) {
        var full = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(resource));
        var empty = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(EMPTY_JAR));
        if (full.isEmpty() || empty.isEmpty()) {
            FloraExpansion.LOGGER.warn("Missing source texture for jam portion icons: {}", resource);
            return;
        }
        for (int portions = 1; portions < 4; portions++) {
            int amount = portions;
            var sprite = resource.withSuffix("_portion_" + portions);
            var replacement = manager.getResource(TEXTURE_ID_CONVERTER.idToFile(sprite));
            if (replacement.isPresent()) {
                output.add(sprite, replacement.get());
                continue;
            }
            output.add(sprite, loader -> loader.loadSprite(sprite, full.get(), (id, frame, image, animation, metadata, texture) -> {
                try (var stream = empty.get().open(); var emptyImage = NativeImage.read(stream)) {
                    applyPortions(image, frame, emptyImage, amount);
                    return new SpriteContents(id, frame, image, animation, metadata, texture);
                } catch (java.io.IOException | RuntimeException error) {
                    image.close();
                    FloraExpansion.LOGGER.error("Unable to generate jam portion icon {}", id, error);
                    return null;
                }
            }));
        }
    }

    public static void applyPortions(NativeImage image, FrameSize frame, NativeImage empty, int portions) {
        if (portions < 1 || portions > 3) throw new IllegalArgumentException("Partial icons require one to three portions");
        int liquidRows = (10 * portions + 3) / 4;
        int cutoff = (14 - liquidRows) * frame.height() / 16;
        for (int y = 0; y < image.getHeight(); y++) {
            int localY = y % frame.height();
            if (localY >= cutoff) continue;
            for (int x = 0; x < image.getWidth(); x++) {
                int localX = x % frame.width();
                image.setPixel(x, y, empty.getPixel(localX * empty.getWidth() / frame.width(),
                        localY * empty.getHeight() / frame.height()));
            }
        }
    }
}
