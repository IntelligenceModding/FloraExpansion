package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class LeafLitterBlock extends PineLitterBlock {
    public LeafLitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected List<ItemStack> getHarvestDrops(Level level, BlockState state) {
        return List.of(new ItemStack(ModItems.TWIG.get(), level.random.nextInt(2 * state.getValue(AMOUNT))));
    }

    @Override
    protected ParticleOptions getFootstepFluffParticle() {
        return ModParticles.LEAF_FLUFF_PARTICLES.get();
    }

    @Override
    protected net.minecraft.sounds.SoundEvent getFootstepSound() {
        return net.minecraft.sounds.SoundEvents.CHERRY_LEAVES_STEP;
    }
}

