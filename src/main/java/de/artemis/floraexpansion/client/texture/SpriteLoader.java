package de.artemis.floraexpansion.client.texture;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import com.mojang.blaze3d.platform.NativeImage;
public final class SpriteLoader {
 @FunctionalInterface public interface Transform { SpriteContents apply(ResourceLocation id, FrameSize frame, NativeImage image, ResourceMetadata metadata); }
 public static SpriteContents load(ResourceLocation id, Resource resource, Transform transform) {
  try (var stream = resource.open()) {
   NativeImage image = NativeImage.read(stream);
   var metadata = resource.metadata();
   var animation = metadata.getSection(net.minecraft.client.resources.metadata.animation.AnimationMetadataSection.SERIALIZER).orElse(net.minecraft.client.resources.metadata.animation.AnimationMetadataSection.EMPTY);
   return transform.apply(id, animation.calculateFrameSize(image.getWidth(), image.getHeight()), image, metadata);
  } catch (java.io.IOException error) { throw new java.io.UncheckedIOException(error); }
 }
}
