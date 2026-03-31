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
public class PineParticles extends SingleQuadParticle {
    private final SpriteSet spriteSet;

    protected PineParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet,
                            double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, spriteSet.first());
        this.spriteSet = spriteSet;

        double angle = this.random.nextDouble() * Math.PI * 2.0;
        double strength = 0.04 + this.random.nextDouble() * 0.25;
        this.xd = Math.cos(angle) * strength;
        this.yd = 0.12 + this.random.nextDouble() * 0.05;
        this.zd = Math.sin(angle) * strength;

        this.friction = 0.85F;
        this.gravity = 0.12F;
        this.quadSize = 0.2F + this.random.nextFloat() * 0.1F;
        this.lifetime = 35 + this.random.nextInt(10);
        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        this.yd -= 0.04;
        this.xd *= this.friction;
        this.zd *= this.friction;

        float ageRatio = (float) this.age / this.lifetime;
        if (ageRatio > 0.8F) {
            this.alpha = 1.0F - (ageRatio - 0.8F) / 0.2F;
        }

        this.setSpriteFromAge(this.spriteSet);
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
            return new PineParticles(clientLevel, x, y, z, this.spriteSet, xSpeed, ySpeed, zSpeed);
        }
    }
}