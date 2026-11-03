package de.artemis.floraexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class CompatibilityInteractionAudit {
    private static final List<CompatibilityTestHelper> TESTS = new ArrayList<>();
    private static final List<Runnable> START = new ArrayList<>();
    private static int ticks;
    private static void run(ServerLevel level, String name, Consumer<CompatibilityTestHelper> test) {
        var helper = new CompatibilityTestHelper(level, new BlockPos(128 + TESTS.size() * 48, 190, 0), name);
        TESTS.add(helper);
        START.add(() -> {
            helper.clearDrops();
            try { test.accept(helper); } catch (Throwable error) { throw new IllegalStateException("Interaction test: " + name, error); }
        });
    }
    static void run(ServerLevel level) {
        run(level, "jar portions, refilling, eating, drops, hoppers", AuditJarPortionTests::verify);
        run(level, "jar placement and interactions", AuditJarTests::verify);
        run(level, "jar nutrition and containers", AuditJarNutritionTests::verify);
        run(level, "hand mill recipes and blocked output", AuditHandMillTests::verify);
        run(level, "hand mill hopper automation", AuditHandMillHopperTests::automation);
        run(level, "crank mounting directions", AuditHandCrankMountTests::verify);
        run(level, "crank modes, redstone pulse and decay", AuditHandCrankRedstoneTests::timing);
        run(level, "salting food, consumption, undo and collection", AuditSaltingTubTests::verify);
        run(level, "salting hopper automation", AuditSaltingTubHopperTests::automation);
        run(level, "salt water containers", AuditSaltWaterTests::verify);
        run(level, "salt water flow", AuditSaltWaterTests::flow);
        run(level, "cauldron conversion", AuditSaltCauldronTests::conversion);
        run(level, "cauldron drips", AuditSaltCauldronTests::drips);
        run(level, "salt spikes", AuditSaltSpikeTests::verify);
        run(level, "salt spike brine seepage", AuditSaltSpikeTests::brineSeepage);
        run(level, "salt spike support loss", AuditSaltSpikeTests::supportLoss);
        run(level, "salt placement", AuditSaltTests::verify);
        run(level, "salt waterlogging", AuditSaltTests::waterlogging);
        run(level, "salt merging", AuditSaltTests::merging);
        run(level, "salt support", AuditSaltTests::support);
    }
    static void tick() {
        if (++ticks < 20) return;
        if (ticks == 20) { START.forEach(Runnable::run); return; }
        TESTS.forEach(CompatibilityTestHelper::tick);
    }
    static void verifyFinished() {
        TESTS.forEach(CompatibilityTestHelper::verifyFinished);
        FloraExpansion.LOGGER.info("INTERACTION AUDIT PASSED: {} detailed jar, crank, mill, salting, automation and salt test scenarios", TESTS.size());
    }
}
