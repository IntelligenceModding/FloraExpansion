package de.artemis.floraexpansion.common.projectile;

import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class PebblesProjectile extends ResourceProjectile {

    public PebblesProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public PebblesProjectile(Level level, LivingEntity shooter, ItemStack stack) {
        super(ModEntityTypes.PEBBLES.get(), level, shooter, stack);
    }

    public PebblesProjectile(Level level, double x, double y, double z, ItemStack stack) {
        super(ModEntityTypes.PEBBLES.get(), level, x, y, z, stack);
    }

    @Override
    protected ParticleOptions getDefaultImpactParticle() {
        return ParticleTypes.CRIT;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.PEBBLES.get();
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

        if (!this.level().isClientSide()) {
            this.level().broadcastEntityEvent(this, (byte) 3);

            spawnDrop(new ItemStack(ModItems.PEBBLES.get()), getX(), getY(), getZ(), 0.1D, 0.1D);
            this.discard();
        }
    }
}
