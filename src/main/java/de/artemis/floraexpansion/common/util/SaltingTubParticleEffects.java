package de.artemis.floraexpansion.common.util;

import net.minecraft.util.RandomSource;

/** Block-relative emission settings shared by gameplay and the JEI scene. */
public final class SaltingTubParticleEffects {
    public static final int FOOD_COUNT = 6, SALT_COUNT = 10, FINISH_COUNT = 8;
    public static final int FINISH_COLOR = 0xF1E9DD;
    public static final float FINISH_SCALE = 0.6F;
    public static final double ADD_HEIGHT = 1.04, FINISH_HEIGHT = 1.05;
    public static final double FINISH_SPREAD_XZ = 0.22, FINISH_SPREAD_Y = 0.04;
    public record Emission(double x, double y, double z, double vx, double vy, double vz) {}
    public static Emission addition(RandomSource random) {
        return new Emission(0.5 + (random.nextDouble()-0.5)*0.5, ADD_HEIGHT,
                0.5 + (random.nextDouble()-0.5)*0.5,
                (random.nextDouble()-0.5)*0.045, 0.025 + random.nextDouble()*0.025,
                (random.nextDouble()-0.5)*0.045);
    }
    public static Emission finish(RandomSource random) {
        return new Emission(0.5 + random.nextGaussian()*FINISH_SPREAD_XZ,
                FINISH_HEIGHT + random.nextGaussian()*FINISH_SPREAD_Y,
                0.5 + random.nextGaussian()*FINISH_SPREAD_XZ, 0,0,0);
    }
    private SaltingTubParticleEffects() {}
}
