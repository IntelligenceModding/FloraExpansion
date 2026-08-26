package de.artemis.floraexpansion.client;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.client.color.block.BlockTintSources;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public class ModColorHandlers {

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(java.util.List.of(BlockTintSources.foliage()), ModBlocks.FRUITING_OAK_LEAVES.get());
    }

}
