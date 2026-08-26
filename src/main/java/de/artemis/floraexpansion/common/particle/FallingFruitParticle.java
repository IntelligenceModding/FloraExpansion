package de.artemis.floraexpansion.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class FallingFruitParticle extends SingleQuadParticle {

    private boolean restingOnGround = false;
    private int restAge = 0;
    private final int maxRestAge;

    protected FallingFruitParticle(ClientLevel level,
                                   double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed,
                                   SpriteSet sprites) {
        super(level, x, y, z, sprites.first());

        this.gravity = 0.08F;
        this.friction = 1.0F;
        this.quadSize *= 1.05F + this.random.nextFloat() * 0.225F;
        this.lifetime = 200;
        this.maxRestAge = 20 + this.random.nextInt(20);
        this.hasPhysics = true;

        this.alpha = 1.0F;

        this.xd = 0.0D;
        this.yd = -0.01D;
        this.zd = 0.0D;

        this.roll = 0.0F;
        this.oRoll = 0.0F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.removed) {
            return;
        }

        if (this.restingOnGround) {
            this.xd = 0.0D;
            this.yd = 0.0D;
            this.zd = 0.0D;
            this.roll = 0.0F;
            this.oRoll = 0.0F;

            this.restAge++;

            int fadeTime = 10;
            if (this.restAge > this.maxRestAge - fadeTime) {
                float remaining = (this.maxRestAge - this.restAge) / (float) fadeTime;
                this.alpha = Mth.clamp(remaining, 0.0F, 1.0F);
            }

            if (this.restAge >= this.maxRestAge) {
                this.remove();
            }
            return;
        }

        this.xd = 0.0D;
        this.zd = 0.0D;

        super.tick();

        this.roll = 0.0F;
        this.oRoll = 0.0F;

        if (this.onGround) {
            this.restingOnGround = true;
            this.xd = 0.0D;
            this.yd = 0.0D;
            this.zd = 0.0D;
        }
    }

    @Override
    protected @NotNull Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    public static void spawnFromFruitingLeaves(Level level, BlockPos pos, SimpleParticleType particleType) {
        RandomSource random = level.getRandom();

        if (level.isEmptyBlock(pos.below())) {
            double x = pos.getX() + 0.5D + randomOffset(random, 0.18D);
            double y = pos.getY() - 0.02D;
            double z = pos.getZ() + 0.5D + randomOffset(random, 0.18D);

            level.addParticle(particleType, x, y, z, 0.0D, -0.01D, 0.0D);
            return;
        }

        List<Direction> sides = new ArrayList<>(List.of(
                Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
        ));
        Collections.shuffle(sides, new java.util.Random(random.nextLong()));

        for (Direction face : sides) {
            BlockPos adjacent = pos.relative(face);
            if (!level.isEmptyBlock(adjacent)) {
                continue;
            }

            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.5D + randomOffset(random, 0.18D);
            double z = pos.getZ() + 0.5D;

            double extraOutward = 2.0D / 16.0D;

            switch (face) {
                case NORTH -> {
                    x += randomOffset(random, 0.18D);
                    z = pos.getZ() + 0.02D - extraOutward;
                }
                case SOUTH -> {
                    x += randomOffset(random, 0.18D);
                    z = pos.getZ() + 0.98D + extraOutward;
                }
                case WEST -> {
                    x = pos.getX() + 0.02D - extraOutward;
                    z += randomOffset(random, 0.18D);
                }
                case EAST -> {
                    x = pos.getX() + 0.98D + extraOutward;
                    z += randomOffset(random, 0.18D);
                }
            }

            level.addParticle(particleType, x, y, z, 0.0D, -0.01D, 0.0D);
            return;
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static double randomOffset(RandomSource random, double spread) {
        return (random.nextDouble() - 0.5D) * 2.0D * spread;
    }

    public static class Provider implements ParticleProvider<@NotNull SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type,
                                       @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed,
                                       @NotNull RandomSource random) {
            return new FallingFruitParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
