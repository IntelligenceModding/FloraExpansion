package de.artemis.floraexpansion.common.projectile;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class PineConeProjectile extends ThrowableItemProjectile {
    public PineConeProjectile(EntityType<? extends Snowball> entityType, Level level) {
        super(entityType, level);
    }

    public PineConeProjectile(Level level, LivingEntity shooter) {
        super(EntityType.SNOWBALL, shooter, level);
    }

    public PineConeProjectile(Level level, double x, double y, double z) {
        super(EntityType.SNOWBALL, x, y, z, level);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.PINE_CONE.get();
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItem();
        return !itemstack.isEmpty() && !itemstack.is(this.getDefaultItem())
                ? new ItemParticleOption(ParticleTypes.ITEM, itemstack)
                : ParticleTypes.ITEM_SNOWBALL;
    }

    private void spawnPineNuts(double x, double y, double z, boolean toasted) {
        if (!this.level().isClientSide && this.random.nextInt(3) == 0) {
            int count = this.random.nextFloat() < 0.10F ? 2 : 1;

            ItemStack drops = new ItemStack(
                    toasted ? ModItems.TOASTED_PINE_NUTS.get() : ModItems.PINE_NUTS.get(),
                    count
            );

            ItemEntity itemEntity = new ItemEntity(this.level(), x, y, z, drops);

            itemEntity.setDeltaMovement(
                    (this.random.nextDouble() - 0.5D) * 0.15D,
                    0.15D,
                    (this.random.nextDouble() - 0.5D) * 0.15D
            );

            this.level().addFreshEntity(itemEntity);
        }
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
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 1.0F);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
        }

        boolean toasted = this.isOnFire() || entity.isOnFire();
        spawnPineNuts(entity.getX(), entity.getY() + 0.25D, entity.getZ(), toasted);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (result.getType() != HitResult.Type.ENTITY) {
                spawnPineNuts(this.getX(), this.getY(), this.getZ(), this.isOnFire());
            }

            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}
