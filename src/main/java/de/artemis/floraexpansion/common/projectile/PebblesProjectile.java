package de.artemis.floraexpansion.common.projectile;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class PebblesProjectile extends ThrowableItemProjectile {

    public PebblesProjectile(EntityType<? extends Snowball> entityType, Level level) {
        super(entityType, level);
    }

    public PebblesProjectile(Level level, LivingEntity shooter) {
        super(EntityType.SNOWBALL, shooter, level);
    }

    public PebblesProjectile(Level level, double x, double y, double z) {
        super(EntityType.SNOWBALL, x, y, z, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.PEBBLES.get();
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItem();
        return !itemstack.isEmpty() && !itemstack.is(this.getDefaultItem())
                ? new ItemParticleOption(ParticleTypes.ITEM, itemstack)
                : ParticleTypes.CRIT;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(
                        particleoptions,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        0.0D,
                        0.0D,
                        0.0D
                );
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 2.0F);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);

            ItemEntity itemEntity = new ItemEntity(
                    this.level(),
                    this.getX(),
                    this.getY(),
                    this.getZ(),
                    new ItemStack(ModItems.PEBBLES.get())
            );

            itemEntity.setDeltaMovement(
                    (this.random.nextDouble() - 0.5D) * 0.1D,
                    0.1D,
                    (this.random.nextDouble() - 0.5D) * 0.1D
            );

            this.level().addFreshEntity(itemEntity);
            this.discard();
        }
    }
}

