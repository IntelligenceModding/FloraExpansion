package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class HandCrankInteractionEvents {
    @SubscribeEvent public static void modeSwitch(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().isSpectator() || !event.getEntity().isShiftKeyDown() || event.getUseBlock() == TriState.FALSE) return;
        if (!event.getLevel().getBlockState(event.getPos()).is(ModBlocks.HAND_CRANK.get())) return;
        // Sneaking normally bypasses block interaction when holding an item.
        event.setUseBlock(TriState.TRUE);
        event.setUseItem(TriState.FALSE);
    }
    private HandCrankInteractionEvents() {}
}
