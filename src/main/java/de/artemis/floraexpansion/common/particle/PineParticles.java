package de.artemis.floraexpansion.common.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PineParticles extends TextureSheetParticle {

    protected PineParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        // 🎯 Short toss outward — like a pine cone kicked loose
        double angle = random.nextDouble() * Math.PI * 2;
        double strength = 0.04 + random.nextDouble() * 0.25; // short horizontal toss
        this.xd = Math.cos(angle) * strength;
        this.yd = 0.12 + random.nextDouble() * 0.05; // fast upward pop
        this.zd = Math.sin(angle) * strength;

        // ⚙️ Physics tuning
        this.friction = 0.85F;   // loses horizontal speed quickly
        this.gravity = 0.12F;    // strong downward pull
        this.quadSize = 0.2F + random.nextFloat() * 0.1F;
        this.lifetime = 35 + random.nextInt(10); // short, quick fall
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();

        // strong gravity pull
        this.yd -= 0.04; // accelerates fall slightly

        // quick slowdown on hit ground
        this.xd *= this.friction;
        this.zd *= this.friction;

        // fade near end
        float ageRatio = (float) this.age / this.lifetime;
        if (ageRatio > 0.8F) {
            this.alpha = 1.0F - (ageRatio - 0.8F) / 0.2F;
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
            return new PineParticles(clientLevel, pX, pY, pZ, this.spriteSet, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}
