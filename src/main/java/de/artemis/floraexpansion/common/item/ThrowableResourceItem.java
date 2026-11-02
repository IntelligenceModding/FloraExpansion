package de.artemis.floraexpansion.common.item;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** A throwable resource with the same use/dispenser behavior and a configurable projectile. */
public class ThrowableResourceItem extends SnowballItem {
    private final SoundEvent throwSound;
    private final OwnerFactory ownerFactory;
    private final PositionFactory positionFactory;

    public ThrowableResourceItem(Properties properties, SoundEvent throwSound, OwnerFactory ownerFactory, PositionFactory positionFactory) {
        super(properties);
        this.throwSound = throwSound;
        this.ownerFactory = ownerFactory;
        this.positionFactory = positionFactory;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), throwSound, SoundSource.NEUTRAL,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide()) {
            ThrowableItemProjectile projectile = ownerFactory.create(level, player, stack);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }

    @Override
    public @NotNull Projectile asProjectile(@NotNull Level level, Position pos, @NotNull ItemStack stack, @NotNull Direction direction) {
        return positionFactory.create(level, pos.x(), pos.y(), pos.z(), stack);
    }

    @FunctionalInterface
    public interface OwnerFactory {
        ThrowableItemProjectile create(Level level, LivingEntity owner, ItemStack stack);
    }

    @FunctionalInterface
    public interface PositionFactory {
        ThrowableItemProjectile create(Level level, double x, double y, double z, ItemStack stack);
    }
}
