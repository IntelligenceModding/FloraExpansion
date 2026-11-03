package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;

/** Samples actual client block events at fractional tick boundaries, independent of frame rate. */
final class CompatibilityAnimationAudit {
    private static float partial;
    private static void near(float expected, float actual, String message) {
        float difference = net.minecraft.util.Mth.wrapDegrees(actual - expected);
        if (Math.abs(difference) > .005F) throw new IllegalStateException(message + ": before=" + expected + ", after=" + actual);
    }
    static void run(Minecraft mc) {
        var level = mc.level;
        long originalTime = level.getGameTime();
        int samples = 0;
        try {
            HandCrankBlockEntity.setClientPartialTickSupplier(() -> partial);
            HandCrankBlockEntity.setClientAnimationTimeSupplier(fraction -> level.getGameTime() + fraction);
            for (boolean stateFirst : new boolean[]{true, false})
            for (var direction : Direction.values()) for (float arrival : new float[]{.05F, .25F, .5F, .9F}) {
                level.setGameTime(originalTime);
                partial = arrival;
                var state = ModBlocks.HAND_CRANK.get().defaultBlockState().setValue(HandCrankBlock.FACING, direction);
                var crank = new HandCrankBlockEntity(mc.player.blockPosition(), state);
                crank.setLevel(level);
                float previous = crank.getAngle(partial);
                for (int turn = 1; turn <= 12; turn++) {
                    float before = crank.getAngle(partial);
                    state = state.setValue(HandCrankBlock.TURN, turn % 4);
                    if (stateFirst) {
                        crank.setBlockState(state);
                        near(before, crank.getAngle(partial), "State packet exposed final orientation before turn event");
                        near(before, crank.getShapeAngle(), "State packet jumped interaction shape before turn event");
                    }
                    crank.triggerEvent(HandCrankBlockEntity.TURN_EVENT, turn);
                    near(before, crank.getAngle(partial), "Crank jumped when turn arrived: " + direction + ", partial=" + arrival + ", turn=" + turn);
                    if (!stateFirst) {
                        crank.setBlockState(state);
                        near(before, crank.getAngle(partial), "State packet after turn event jumped rotation");
                    }
                    // A new click arrives two ticks later, while the previous eight-tick turn is active.
                    for (int frame = 0; frame < 40; frame++) {
                        double time = (turn - 1) * 2 + arrival + frame / 20D;
                        level.setGameTime(originalTime + (long) time);
                        partial = (float) (time - Math.floor(time));
                        float angle = crank.getAngle(partial);
                        float delta = net.minecraft.util.Mth.wrapDegrees(angle - previous);
                        if (delta < -.005F || delta > 15F) throw new IllegalStateException("Unsmooth crank frame: " + delta);
                        near(angle, crank.getShapeAngle(), "Rendered angle differs from interaction shape");
                        previous = angle;
                        samples++;
                    }
                    level.setGameTime(originalTime + turn * 2);
                    partial = arrival;
                }
                level.setGameTime(originalTime + 40);
                near((12 % 4) * 90, crank.getAngle(0), "Crank did not settle on its saved orientation");
            }
            // State packets also reach loaded chunks outside the block-event radius.
            // They must animate and settle correctly even without any turn event.
            for (int initial = 0; initial < 4; initial++) {
                level.setGameTime(originalTime);
                partial = .35F;
                var state = ModBlocks.HAND_CRANK.get().defaultBlockState().setValue(HandCrankBlock.TURN, initial);
                var crank = new HandCrankBlockEntity(mc.player.blockPosition(), state);
                crank.setLevel(level);
                near(initial * 90, crank.getAngle(partial), "Loaded crank orientation incorrect");
                state = state.setValue(HandCrankBlock.TURN, (initial + 2) % 4);
                crank.setBlockState(state);
                near(initial * 90, crank.getAngle(partial), "Coalesced state update jumped before animation");
                level.setGameTime(originalTime + 9);
                near(state.getValue(HandCrankBlock.TURN) * 90, crank.getAngle(partial), "State-only turn failed to settle");
                crank.setBlockState(state.setValue(HandCrankBlock.POWER, 15));
                near(state.getValue(HandCrankBlock.TURN) * 90, crank.getAngle(partial), "Power update restarted rotation");
            }
            var millPos = mc.player.blockPosition().above(10);
            var crankPos = millPos.above();
            var previousMill = level.getBlockState(millPos);
            var previousCrank = level.getBlockState(crankPos);
            try {
                level.setGameTime(originalTime);
                level.setBlock(millPos, ModBlocks.HAND_MILL.get().defaultBlockState(), 2);
                level.setBlock(crankPos, ModBlocks.HAND_CRANK.get().defaultBlockState(), 2);
                var mill = (de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity) level.getBlockEntity(millPos);
                var crank = (HandCrankBlockEntity) level.getBlockEntity(crankPos);
                if (mill == null || crank == null || !crank.isOnMill()) throw new IllegalStateException("Client mill fixture missing");
                partial = .65F;
                crank.setBlockState(crank.getBlockState().setValue(HandCrankBlock.TURN, 1));
                crank.triggerEvent(HandCrankBlockEntity.TURN_EVENT, 1);
                var crankRenderer = new de.artemis.floraexpansion.client.renderer.HandCrankBlockEntityRenderer(null);
                var millRenderer = new de.artemis.floraexpansion.client.renderer.HandMillBlockEntityRenderer(null);
                for (int frame = 0; frame < 180; frame++) {
                    double time = .65 + frame / 20D;
                    level.setGameTime(originalTime + (long) time);
                    partial = (float) (time - Math.floor(time));
                    var crankState = crankRenderer.createRenderState();
                    var millState = millRenderer.createRenderState();
                    crankRenderer.extractRenderState(crank, crankState, partial, net.minecraft.world.phys.Vec3.ZERO, null);
                    millRenderer.extractRenderState(mill, millState, partial, net.minecraft.world.phys.Vec3.ZERO, null);
                    near(crankState.angle, millState.angle, "Millstone and crank renderers drifted apart");
                    near(crankState.angle, crank.getShapeAngle(), "Rendered crank and selection shape drifted apart");
                    level.getBlockState(millPos).getShape(level, millPos);
                    level.getBlockState(crankPos).getShape(level, crankPos);
                    samples++;
                }
            } finally {
                level.setBlock(crankPos, previousCrank, 2);
                level.setBlock(millPos, previousMill, 2);
            }
            FloraExpansion.LOGGER.info("ANIMATION AUDIT PASSED: {} fractional-frame samples, six mount directions, rapid turns, full rotations and synchronized crank/mill renderers", samples);
        } finally {
            level.setGameTime(originalTime);
            HandCrankBlockEntity.setClientPartialTickSupplier(() -> mc.getTimer().getGameTimeDeltaPartialTick(false));
            HandCrankBlockEntity.setClientAnimationTimeSupplier(de.artemis.floraexpansion.client.ClientAnimationClock::time);
        }
    }
}
