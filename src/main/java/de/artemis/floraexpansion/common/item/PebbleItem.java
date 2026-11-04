package de.artemis.floraexpansion.common.item;

import org.jetbrains.annotations.NotNull;

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

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** One resource for placement, throwing and dispenser projectiles. */
public final class PebbleItem extends BlockItem {
    @NotNull @Override public String getDescriptionId() { return "item.floraexpansion.pebbles"; }
    public PebbleItem(Properties properties) { super(ModBlocks.SCATTERED_PEBBLES.get(), properties); }
    @Override public void registerBlocks(@NotNull java.util.Map<net.minecraft.world.level.block.Block, net.minecraft.world.item.Item> map,
                                        @NotNull net.minecraft.world.item.Item item) {
        // Keep the old scattered_pebbles BlockItem mapped to its block for inventory/save compatibility.
    }

    @NotNull @Override public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player != null && player.isShiftKeyDown()) return use(context.getLevel(), player, context.getHand()).getResult();
        var placed = super.useOn(context);
        // A failed placement must not accidentally throw a stone at the block or entity behind it.
        return placed.consumesAction() ? placed : InteractionResult.FAIL;
    }

    @NotNull @Override public net.minecraft.world.InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack.getItem())) return net.minecraft.world.InteractionResultHolder.fail(player.getItemInHand(hand));
        if (!level.isClientSide) {
            var projectile = new PebblesProjectile(level, player, stack);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 1);
            level.addFreshEntity(projectile);
        }
        player.getCooldowns().addCooldown(stack.getItem(), 8);
        player.awardStat(Stats.ITEM_USED.get(this));
        de.artemis.floraexpansion.common.util.ItemData.consume(stack, 1, player);
        return net.minecraft.world.InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @NotNull public Projectile asProjectile(@NotNull Level level, Position pos, @NotNull ItemStack stack, @NotNull Direction direction) {
        return new PebblesProjectile(level, pos.x(), pos.y(), pos.z(), stack);
    }
}
