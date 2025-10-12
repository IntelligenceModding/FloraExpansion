package de.artemis.floraexpansion.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class LeafFluffParticles extends TextureSheetParticle {

    protected LeafFluffParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        double angle = random.nextDouble() * Math.PI * 2;
        double strength = 0.03 + random.nextDouble() * 0.015;
        this.xd = Math.cos(angle) * strength;
        this.yd = 0.04 + random.nextDouble() * 0.02;
        this.zd = Math.sin(angle) * strength;

        this.friction = 0.94F;
        this.gravity = 0.025F;
        this.quadSize = 0.35F + random.nextFloat() * 0.1F;
        this.lifetime = 200 + random.nextInt(40);

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age % 10 == 0) {
            this.xd += (random.nextDouble() - 0.5) * 0.005;
            this.zd += (random.nextDouble() - 0.5) * 0.005;
        }

        this.xd *= 0.97;
        this.zd *= 0.97;

        float ageRatio = (float) this.age / this.lifetime;
        if (ageRatio > 0.75F) {
            this.alpha = 1.0F - (ageRatio - 0.75F) / 0.25F;
        }
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
            return new LeafFluffParticles(clientLevel, pX, pY, pZ, this.spriteSet, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}