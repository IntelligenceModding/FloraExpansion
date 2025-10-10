package de.artemis.floraexpansion.common.item.projectile;

import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
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
        return (ParticleOptions) (!itemstack.isEmpty() && !itemstack.is(this.getDefaultItem()) ? new ItemParticleOption(ParticleTypes.ITEM, itemstack) : ParticleTypes.ITEM_SNOWBALL);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleoptions = this.getParticle();

            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), (double) 0.0F, (double) 0.0F, (double) 0.0F);
            }
        }

    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) 1);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}
