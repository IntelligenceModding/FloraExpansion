package de.artemis.floraexpansion.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class FlaxFlowerParticles extends TextureSheetParticle {
    private final float wobblePhase = random.nextFloat() * (float) Math.PI * 2f;
    private final float rotationSpeed;

    protected FlaxFlowerParticles(ClientLevel level, double x, double y, double z,
                                  SpriteSet sprites, double vx, double vy, double vz) {
        super(level, x, y, z, 0, 0, 0);

        this.friction = 0.96F;
        this.gravity = 0.20F;
        this.quadSize = 0.5F + random.nextFloat() * 0.04F;
        this.lifetime = 50 + random.nextInt(15);

        this.xd = (random.nextDouble() - 0.5) * 0.02;
        this.yd = -0.012 - random.nextDouble() * 0.006;
        this.zd = (random.nextDouble() - 0.5) * 0.002;

        this.roll = random.nextFloat() * (float) Math.PI * 2f;
        this.oRoll = this.roll;
        this.rotationSpeed = (random.nextFloat() - 0.5f) * 0.01f;

        this.pickSprite(sprites);
        this.alpha = 0.95F;
    }

    @Override
    public void tick() {
        super.tick();

        this.roll += this.rotationSpeed;

        float t = this.age * 0.12f;
        double flutter = 0.0035;
        this.xd += Math.cos(t + wobblePhase) * flutter * 0.08;
        this.zd += Math.sin(t + wobblePhase) * flutter * 0.08;

        this.xd *= 0.986;
        this.zd *= 0.986;

        float a = (float) this.age / (float) this.lifetime;
        if (a > 0.8f) this.alpha = 1.0f - (a - 0.8f) / 0.2f;

    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Nullable
        @Override
        public Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new FlaxFlowerParticles(clientLevel, pX, pY, pZ, this.spriteSet, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}