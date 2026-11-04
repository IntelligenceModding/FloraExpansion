package de.artemis.floraexpansion.client;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/** Local animation time is unaffected by server world-time synchronization packets. */
@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class ClientAnimationClock {
    private static long ticks;
    private static double lastTime;

    @SubscribeEvent
    public static void tick(net.minecraftforge.event.TickEvent.LevelTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        if (event.level.isClientSide && event.level.tickRateManager().runsNormally()) ticks++;
    }

    public static double time(double partialTick) {
        // Network callbacks can run after the timer residual wraps but before the
        // level tick. Never let that intermediate ordering rewind a rendered frame.
        lastTime = Math.max(lastTime, ticks + Math.clamp(partialTick, 0D, 1D));
        return lastTime;
    }

    private ClientAnimationClock() {}
}
