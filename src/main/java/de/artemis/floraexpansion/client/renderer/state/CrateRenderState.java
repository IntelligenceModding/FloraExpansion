package de.artemis.floraexpansion.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CrateRenderState extends BlockEntityRenderState {
    public @Nullable TextureAtlasSprite contentsSprite;
    public float fillRatio;
    public BlockState blockState = Blocks.AIR.defaultBlockState();
}
