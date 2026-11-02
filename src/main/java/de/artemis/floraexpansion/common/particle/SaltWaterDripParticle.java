package de.artemis.floraexpansion.common.particle;

import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DripParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.material.Fluid;

/** A real Salt Water particle throughout its hanging, falling and landing phases. */
public final class SaltWaterDripParticle extends DripParticle {
    public enum Stage { HANGING, FALLING, LANDING }
    private final Stage stage;

    public SaltWaterDripParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, Stage stage) {
        super(level, x, y, z, ModFluids.SALT_WATER.get(), sprite);
        this.stage = stage;
        int color = ModFluids.SALT_WATER_COLOR;
        setColor((color >> 16 & 255) / 255F, (color >> 8 & 255) / 255F, (color & 255) / 255F);
        lifetime = switch (stage) { case HANGING -> 40; case FALLING -> 120; case LANDING -> 16 + random.nextInt(12); };
        if (stage == Stage.HANGING) gravity *= .02F;
        if (stage == Stage.LANDING) gravity = 0;
    }

    @Override public Fluid getType() { return super.getType(); }

    @Override protected void preMoveUpdate() {
        if (lifetime-- > 0) return;
        remove();
        if (stage == Stage.HANGING)
            level.addParticle(ModParticles.FALLING_SALT_WATER.get(), x, y, z, 0, 0, 0);
    }

    @Override protected void postMoveUpdate() {
        if (stage == Stage.HANGING) { xd *= .02; yd *= .02; zd *= .02; }
        if (stage == Stage.FALLING && onGround) {
            remove();
            level.addParticle(ModParticles.LANDING_SALT_WATER.get(), x, y, z, 0, 0, 0);
            level.playLocalSound(x, y, z, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER,
                    SoundSource.BLOCKS, .3F + random.nextFloat() * .7F, 1F, false);
        }
    }

    public record Provider(SpriteSet sprites, Stage stage) implements ParticleProvider<SimpleParticleType> {
        @Override public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                                  double vx, double vy, double vz, RandomSource random) {
            return new SaltWaterDripParticle(level, x, y, z, sprites.get(random), stage);
        }
    }
}
