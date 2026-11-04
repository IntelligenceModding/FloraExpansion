package de.artemis.floraexpansion.common.projectile;

import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.config.ServerConfig;

public class PebblesProjectile extends ResourceProjectile {
    private int skips;
    private boolean resolved;

    @Override public void tick() {
        double oldY = getY();
        var incoming = getDeltaMovement();
        super.tick();
        if (level().isClientSide || !isAlive() || resolved) return;
        var pos = blockPosition();
        var water = level().getFluidState(pos);
        if (water.is(net.minecraft.tags.FluidTags.WATER)
                || water.is(de.artemis.floraexpansion.common.registry.ModTags.Fluids.SALT_WATER)) {
            double surface = pos.getY() + water.getHeight(level(), pos);
            double speed = incoming.horizontalDistance();
            double impactSlope = -incoming.y / Math.max(speed, .001);
            // Steep impacts sink. Glancing impacts keep more horizontal energy;
            // shrinking hops and drag eventually end a skim without a fixed skip count.
            if (incoming.y < 0 && impactSlope < .5 && speed > .35
                    && oldY >= surface - .02 && getY() <= surface) {
                skips++;
                if (skips == 3 && getOwner() instanceof net.minecraft.world.entity.player.Player player)
                    de.artemis.floraexpansion.common.advancement.FloraActionTrigger.fire(player, "skipping_stones");
                setPos(getX(), surface + .04, getZ());
                double retainedSpeed = .98 - impactSlope * .9;
                setDeltaMovement(incoming.x * retainedSpeed, Math.min(.2, speed * .12), incoming.z * retainedSpeed);
                ((net.minecraft.server.level.ServerLevel)level()).sendParticles(net.minecraft.core.particles.ParticleTypes.SPLASH,
                        getX(), surface, getZ(), 5, .08, .01, .08, .02);
                level().playSound(null, getX(), surface, getZ(), net.minecraft.sounds.SoundEvents.FISHING_BOBBER_SPLASH,
                        net.minecraft.sounds.SoundSource.NEUTRAL, .2F, 1.5F);
            }
        }
        if (tickCount >= 200) finish();
    }

    @Override public void addAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag output) {
        super.addAdditionalSaveData(output); output.putInt("PebbleSkips", skips);
    }
    @Override public void readAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag input) {
        super.readAdditionalSaveData(input); skips = net.minecraft.util.Mth.clamp(input.getInt("PebbleSkips"), 0, 200);
    }

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
    protected @NotNull Item getDefaultItem() {
        return ModItems.PEBBLES.get();
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) ServerConfig.pebbleDamage.get().doubleValue());
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (resolved) return;
        super.onHit(result);

        if (!this.level().isClientSide) {
            level().playSound(null, getX(), getY(), getZ(), net.minecraft.sounds.SoundEvents.STONE_HIT,
                    net.minecraft.sounds.SoundSource.NEUTRAL, .4F, 1.1F);
            finish();
        }
    }

    private void finish() {
        if (resolved || level().isClientSide) return;
        resolved = true;
        level().broadcastEntityEvent(this, (byte)3);
        if (!(getOwner() instanceof net.minecraft.world.entity.player.Player player && player.getAbilities().instabuild))
            spawnDrop(getItem().copyWithCount(1), getX(), getY(), getZ(), .1, .1);
        discard();
    }
}
