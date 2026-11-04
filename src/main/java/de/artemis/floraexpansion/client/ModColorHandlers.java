package de.artemis.floraexpansion.client;

import de.artemis.floraexpansion.common.registry.ModBlocks;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public class ModColorHandlers {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> tint == 0 ? net.minecraft.world.level.FoliageColor.getDefaultColor() : -1,
                ModBlocks.APPLE_LEAVES.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tint) -> de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER_COLOR,
                ModBlocks.SALT_WATER_CAULDRON.get());
        event.register((state, level, pos, tint) -> level != null && pos != null ? net.minecraft.client.renderer.BiomeColors.getAverageFoliageColor(level, pos) : net.minecraft.world.level.FoliageColor.getDefaultColor(), ModBlocks.APPLE_LEAVES.get());
        event.register((state, level, pos, tint) -> 0xA0FFFFFF,
                ModBlocks.BLUEBERRY_JAM.get(), ModBlocks.STRAWBERRY_JAM.get(),
                ModBlocks.CHERRY_JAM.get(), ModBlocks.APPLE_JAM.get());
    }

}
