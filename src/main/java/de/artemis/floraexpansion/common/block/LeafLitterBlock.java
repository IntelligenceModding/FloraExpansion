package de.artemis.floraexpansion.common.block;



import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/** Retains 0.4.1 leaf litter on 1.20.1, which has no vanilla leaf-litter replacement. */
public final class LeafLitterBlock extends NeedleLitterBlock {
    public LeafLitterBlock(Properties properties) { super(properties); }

    @Override public ParticleOptions getFootstepFluffParticle() {
        return new DustParticleOptions(new org.joml.Vector3f(.55F, .36F, .16F), .5F);
    }
    @Override public SoundEvent getFootstepSound() { return SoundEvents.CHERRY_LEAVES_STEP; }
    @Override public void spawnExtraFootstepParticles(Level level, BlockPos pos, RandomSource random) {}
}
