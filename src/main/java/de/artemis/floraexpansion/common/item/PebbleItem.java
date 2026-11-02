package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.projectile.PebblesProjectile;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** One resource for placement, throwing and dispenser projectiles. */
public final class PebbleItem extends BlockItem implements ProjectileItem {
    public PebbleItem(Properties properties) { super(ModBlocks.SCATTERED_PEBBLES.get(), properties); }
    @Override public void registerBlocks(java.util.Map<net.minecraft.world.level.block.Block, net.minecraft.world.item.Item> map,
                                        net.minecraft.world.item.Item item) {
        // Keep the old scattered_pebbles BlockItem mapped to its block for inventory/save compatibility.
    }

    @Override public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) return use(context.getLevel(), player, context.getHand());
        var placed = super.useOn(context);
        // A failed placement must not accidentally throw a stone at the block or entity behind it.
        return placed.consumesAction() ? placed : InteractionResult.FAIL;
    }

    @Override public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) return InteractionResult.FAIL;
        if (!level.isClientSide()) {
            var projectile = new PebblesProjectile(level, player, stack);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
            level.addFreshEntity(projectile);
        }
        player.getCooldowns().addCooldown(stack, 8);
        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);
        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }

    @Override public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        return new PebblesProjectile(level, pos.x(), pos.y(), pos.z(), stack);
    }
}
