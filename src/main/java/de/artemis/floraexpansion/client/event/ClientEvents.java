package de.artemis.floraexpansion.client.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.particle.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public class ClientEvents {

    /**@SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BlockColors blockColors = event.getBlockColors();

        blockColors.register(
                (state, level, pos, tintIndex) ->
                        level != null && pos != null
                                ? BiomeColors.getAverageFoliageColor(level, pos)
                                : FoliageColor.getDefaultColor(),
                ModBlocks.LEAF_LITTER.get()
        );
    }**/

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.LEAF_FLUFF_PARTICLES.get(), LeafFluffParticles.Provider::new);
        event.registerSpriteSet(ModParticles.PINE_LEAF_FLUFF_PARTICLES.get(), PineLeafFluffParticles.Provider::new);
        event.registerSpriteSet(ModParticles.PINE_PARTICLES.get(), PineParticles.Provider::new);
        event.registerSpriteSet(ModParticles.FLAX_FLOWER.get(), FlaxFlowerParticles.Provider::new);
    }
}
