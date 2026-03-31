package de.artemis.floraexpansion.client;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class ModColorHandlers {

    private ModColorHandlers() {
    }

    @SubscribeEvent
    public static void registerBlockColors(@NotNull RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            if (level != null && pos != null) {
                return BiomeColors.getAverageFoliageColor(level, pos);
            }

            return event.getBlockColors().getColor(Blocks.OAK_LEAVES.defaultBlockState(), null, null, 0);
        }, ModBlocks.FRUITING_OAK_LEAVES.get());
    }
}
