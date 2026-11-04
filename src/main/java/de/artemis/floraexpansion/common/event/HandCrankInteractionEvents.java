package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class HandCrankInteractionEvents {
    @SubscribeEvent public static void modeSwitch(PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().isSpectator() || !event.getEntity().isShiftKeyDown() || event.getUseBlock() == Result.DENY) return;
        if (!event.getLevel().getBlockState(event.getPos()).is(ModBlocks.HAND_CRANK.get())) return;
        // Sneaking normally bypasses block interaction when holding an item.
        event.setUseBlock(Result.ALLOW);
        event.setUseItem(Result.DENY);
    }
    private HandCrankInteractionEvents() {}
}
