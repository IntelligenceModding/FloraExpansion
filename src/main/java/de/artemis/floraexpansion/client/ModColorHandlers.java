package de.artemis.floraexpansion.client;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public class ModColorHandlers {

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tint) -> de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER_COLOR,
                ModBlocks.SALT_WATER_CAULDRON.get());
        event.register((state, level, pos, tint) -> level != null && pos != null ? net.minecraft.client.renderer.BiomeColors.getAverageFoliageColor(level, pos) : net.minecraft.world.level.FoliageColor.FOLIAGE_DEFAULT, ModBlocks.APPLE_LEAVES.get());
        event.register((state, level, pos, tint) -> 0xA0FFFFFF,
                ModBlocks.BLUEBERRY_JAM.get(), ModBlocks.STRAWBERRY_JAM.get(),
                ModBlocks.CHERRY_JAM.get(), ModBlocks.APPLE_JAM.get());
    }

}
