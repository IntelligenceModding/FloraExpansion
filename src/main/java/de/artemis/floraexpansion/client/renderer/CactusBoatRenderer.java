package de.artemis.floraexpansion.client.renderer;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CactusBoatRenderer extends BoatRenderer {
    public static final ModelLayerLocation CACTUS_BOAT_LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "boat/cactus"),
            "main"
    );
    public static final ModelLayerLocation CACTUS_CHEST_BOAT_LAYER = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "chest_boat/cactus"),
            "main"
    );

    public CactusBoatRenderer(EntityRendererProvider.@NotNull Context context, boolean chestBoat) {
        super(context, chestBoat ? CACTUS_CHEST_BOAT_LAYER : CACTUS_BOAT_LAYER);
    }
}
