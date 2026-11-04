package de.artemis.floraexpansion.mixin;
@org.spongepowered.asm.mixin.Mixin(net.minecraft.client.renderer.texture.atlas.SpriteSources.class)
public interface SpriteSourcesAccessor {
 @org.spongepowered.asm.mixin.gen.Accessor("TYPES")
 static com.google.common.collect.BiMap<net.minecraft.resources.ResourceLocation, net.minecraft.client.renderer.texture.atlas.SpriteSourceType> floraexpansion$types() { throw new AssertionError(); }
}
