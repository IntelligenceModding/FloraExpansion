package de.artemis.floraexpansion.common.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;

/** Common constructors, impact particles, and scattered drops for throwable resources. */
public abstract class ResourceProjectile extends ThrowableItemProjectile {
    protected ResourceProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    protected ResourceProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level, LivingEntity owner, ItemStack stack) {
        super(type, owner, level, stack);
    }

    protected ResourceProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level,
                                 double x, double y, double z, ItemStack stack) {
        super(type, x, y, z, level, stack);
    }

    protected abstract ParticleOptions getDefaultImpactParticle();

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ItemStack stack = getItem();
            ParticleOptions particle = !stack.isEmpty() && !stack.is(getDefaultItem())
                    ? new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(stack))
                    : getDefaultImpactParticle();
            for (int i = 0; i < 8; i++) {
                level().addParticle(particle, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    protected void spawnDrop(ItemStack stack, double x, double y, double z, double scatter, double upwardSpeed) {
        ItemEntity drop = new ItemEntity(level(), x, y, z, stack);
        drop.setDeltaMovement((random.nextDouble() - 0.5D) * scatter, upwardSpeed, (random.nextDouble() - 0.5D) * scatter);
        level().addFreshEntity(drop);
    }
}
