package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class WoodenMilkBucketItem extends MilkBucketItem {
    public WoodenMilkBucketItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            serverPlayer.awardStat(Stats.ITEM_USED.get(this));
        }

        if (!level.isClientSide) {
            entity.removeEffectsCuredBy(net.neoforged.neoforge.common.EffectCures.MILK);
        }

        if (entity instanceof Player player) {
            if (player.getAbilities().instabuild) {
                return stack;
            }

            return WoodenBucketUtil.damageInto(stack, player, ModItems.WOODEN_BUCKET.get());
        }

        stack.consume(1, entity);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
}
