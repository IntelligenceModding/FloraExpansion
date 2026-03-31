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
public class LeafFluffParticles extends SingleQuadParticle {
    private final SpriteSet spriteSet;

    protected LeafFluffParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet,
                                 double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, spriteSet.first());
        this.spriteSet = spriteSet;

        double angle = this.random.nextDouble() * Math.PI * 2.0;
        double strength = 0.03 + this.random.nextDouble() * 0.015;
        this.xd = Math.cos(angle) * strength;
        this.yd = 0.04 + this.random.nextDouble() * 0.02;
        this.zd = Math.sin(angle) * strength;

        this.friction = 0.94F;
        this.gravity = 0.025F;
        this.quadSize = 0.35F + this.random.nextFloat() * 0.1F;
        this.lifetime = 200 + this.random.nextInt(40);

        this.setSpriteFromAge(this.spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age % 10 == 0) {
            this.xd += (this.random.nextDouble() - 0.5D) * 0.005D;
            this.zd += (this.random.nextDouble() - 0.5D) * 0.005D;
        }

        this.xd *= 0.97D;
        this.zd *= 0.97D;

        float ageRatio = (float) this.age / this.lifetime;
        if (ageRatio > 0.75F) {
            this.alpha = 1.0F - (ageRatio - 0.75F) / 0.25F;
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
            return new LeafFluffParticles(clientLevel, x, y, z, this.spriteSet, xSpeed, ySpeed, zSpeed);
        }
    }
}