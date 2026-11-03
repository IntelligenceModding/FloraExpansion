package de.artemis.floraexpansion.client;

import de.artemis.floraexpansion.FloraExpansion;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** Local animation time is unaffected by server world-time synchronization packets. */
@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class ClientAnimationClock {
    private static long ticks;
    private static double lastTime;

    @SubscribeEvent
    public static void tick(net.neoforged.neoforge.event.tick.LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide && event.getLevel().tickRateManager().runsNormally()) ticks++;
    }

    public static double time(double partialTick) {
        // Network callbacks can run after the timer residual wraps but before the
        // level tick. Never let that intermediate ordering rewind a rendered frame.
        lastTime = Math.max(lastTime, ticks + Math.clamp(partialTick, 0D, 1D));
        return lastTime;
    }

    private ClientAnimationClock() {}
}
