package de.artemis.floraexpansion.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class FlaxFlowerParticles extends SingleQuadParticle {
    private final float wobblePhase;
    private final float rotationSpeed;

    protected FlaxFlowerParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet,
                                  double vx, double vy, double vz) {
        super(level, x, y, z, spriteSet.first());

        this.wobblePhase = this.random.nextFloat() * (float) Math.PI * 2.0F;
        this.rotationSpeed = (this.random.nextFloat() - 0.5F) * 0.01F;

        this.friction = 0.96F;
        this.gravity = 0.20F;
        this.quadSize = 0.5F + this.random.nextFloat() * 0.04F;
        this.lifetime = 50 + this.random.nextInt(15);

        this.xd = (this.random.nextDouble() - 0.5D) * 0.02D;
        this.yd = -0.012D - this.random.nextDouble() * 0.006D;
        this.zd = (this.random.nextDouble() - 0.5D) * 0.002D;

        this.roll = this.random.nextFloat() * (float) Math.PI * 2.0F;
        this.oRoll = this.roll;

        this.alpha = 0.95F;
    }

    @Override
    public void tick() {
        super.tick();

        this.oRoll = this.roll;
        this.roll += this.rotationSpeed;

        float t = this.age * 0.12F;
        double flutter = 0.0035D;
        this.xd += Math.cos(t + this.wobblePhase) * flutter * 0.08D;
        this.zd += Math.sin(t + this.wobblePhase) * flutter * 0.08D;

        this.xd *= 0.986D;
        this.zd *= 0.986D;

        float a = (float) this.age / (float) this.lifetime;
        if (a > 0.8F) {
            this.alpha = 1.0F - (a - 0.8F) / 0.2F;
        }
    }

    @Override
    protected @NotNull Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<@NotNull SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull SimpleParticleType simpleParticleType,
                                                 @NotNull ClientLevel clientLevel,
                                                 double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed,
                                                 @NotNull RandomSource random) {
            return new FlaxFlowerParticles(clientLevel, x, y, z, this.spriteSet, xSpeed, ySpeed, zSpeed);
        }
    }
}