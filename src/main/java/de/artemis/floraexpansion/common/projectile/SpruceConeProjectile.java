package de.artemis.floraexpansion.common.projectile;

import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.config.ServerConfig;

public class SpruceConeProjectile extends ResourceProjectile {

    public SpruceConeProjectile(EntityType<? extends SpruceConeProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SpruceConeProjectile(Level level, LivingEntity shooter, ItemStack stack) {
        super(ModEntityTypes.SPRUCE_CONE.get(), level, shooter, stack);
    }

    public SpruceConeProjectile(Level level, double x, double y, double z, ItemStack stack) {
        super(ModEntityTypes.SPRUCE_CONE.get(), level, x, y, z, stack);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.SPRUCE_CONE.get();
    }

    private void spawnSpruceNuts(double x, double y, double z, boolean toasted) {
        if (!this.level().isClientSide() && this.random.nextInt(3) == 0) {
            int count = this.random.nextFloat() < 0.10F ? 2 : 1;

            ItemStack drops = new ItemStack(
                    toasted ? ModItems.TOASTED_SPRUCE_NUTS.get() : ModItems.SPRUCE_NUTS.get(),
                    count
            );

            spawnDrop(drops, x, y, z, 0.15D, 0.15D);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) ServerConfig.coneDamage.get().doubleValue());

        if (entity instanceof LivingEntity livingEntity && ServerConfig.coneSlownessTicks.get() > 0) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, ServerConfig.coneSlownessTicks.get(), 0));
        }

        boolean toasted = this.isOnFire() || entity.isOnFire();
        spawnSpruceNuts(entity.getX(), entity.getY() + 0.25D, entity.getZ(), toasted);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide()) {
            if (result.getType() != HitResult.Type.ENTITY) {
                spawnSpruceNuts(this.getX(), this.getY(), this.getZ(), this.isOnFire());
            }

            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }
}
