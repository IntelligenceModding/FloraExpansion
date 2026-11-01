package de.artemis.floraexpansion.client.renderer.state;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
public class HandCrankRenderState extends BlockEntityRenderState {
    public float angle;
    public boolean strengthMode;
    public boolean hasBase;
    public net.minecraft.core.Direction facing = net.minecraft.core.Direction.UP;
    public int signal;
}
