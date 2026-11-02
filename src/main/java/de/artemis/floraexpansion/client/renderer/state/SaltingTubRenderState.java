package de.artemis.floraexpansion.client.renderer.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;

public class SaltingTubRenderState extends BlockEntityRenderState {
    public int layers;
    public boolean salted;
    public boolean ready;
    public int[] foodPixels = new int[0];
}
