package de.artemis.floraexpansion.common.projectile;

import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class PebblesProjectile extends ThrowableItemProjectile {

    @SuppressWarnings("unused")
    public PebblesProjectile(EntityType<? extends PebblesProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PebblesProjectile(Level level, LivingEntity shooter, ItemStack stack) {
        super(EntityType.SNOWBALL, shooter, level, stack);
    }

    public PebblesProjectile(Level level, double x, double y, double z, ItemStack stack) {
        super(EntityType.SNOWBALL, x, y, z, level, stack);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.PEBBLES.get();
    }

    private ParticleOptions getParticle() {
        ItemStack itemStack = this.getItem();
        return !itemStack.isEmpty() && !itemStack.is(this.getDefaultItem())
                ? new ItemParticleOption(ParticleTypes.ITEM, itemStack)
                : ParticleTypes.CRIT;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleOptions = this.getParticle();

            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(
                        particleOptions,
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

    @SuppressWarnings("deprecation")
    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 2.0F);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide()) {
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