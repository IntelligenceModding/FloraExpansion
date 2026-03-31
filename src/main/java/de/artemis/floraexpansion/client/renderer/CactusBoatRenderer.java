package de.artemis.floraexpansion.client.renderer;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CactusBoatRenderer extends BoatRenderer {

    public CactusBoatRenderer(EntityRendererProvider.@NotNull Context context, boolean chestBoat) {
        super(context, chestBoat ? ModelLayers.OAK_CHEST_BOAT : ModelLayers.OAK_BOAT);
    }

    private static ModelLayerLocation getLayer(boolean chestBoat) {
        return chestBoat ? ModelLayers.OAK_CHEST_BOAT : ModelLayers.OAK_BOAT;
    }
}