package de.artemis.floraexpansion.client.texture;

import org.jetbrains.annotations.NotNull;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public record JamPortionSpriteSource(ResourceLocation resource) implements SpriteSource {
    public static final Codec<JamPortionSpriteSource> MAP_CODEC = ResourceLocation.CODEC.fieldOf("resource")
            .xmap(JamPortionSpriteSource::new, JamPortionSpriteSource::resource).codec();
    private static final ResourceLocation EMPTY_JAR = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "item/glass_jar");

    public static final net.minecraft.client.renderer.texture.atlas.SpriteSourceType TYPE = new net.minecraft.client.renderer.texture.atlas.SpriteSourceType(MAP_CODEC);
    @Override
    @NotNull public net.minecraft.client.renderer.texture.atlas.SpriteSourceType type() { return TYPE; }

    @Override
    public void run(ResourceManager manager, @NotNull Output output) {
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
            output.add(sprite, () -> SpriteLoader.load(sprite, full.get(), (id, frame, image, metadata) -> {
                try (var stream = empty.get().open(); var emptyImage = NativeImage.read(stream)) {
                    applyPortions(image, frame, emptyImage, amount);
                    return new SpriteContents(id, frame, image, metadata);
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
                NativePixels.set(image, x, y, NativePixels.get(empty, localX * empty.getWidth() / frame.width(),
                        localY * empty.getHeight() / frame.height()));
            }
        }
    }
}
