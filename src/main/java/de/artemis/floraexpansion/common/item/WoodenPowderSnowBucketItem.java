package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

public class WoodenPowderSnowBucketItem extends SolidBucketItem {
    public WoodenPowderSnowBucketItem(Properties properties) {
        super(Blocks.POWDER_SNOW, net.minecraft.sounds.SoundEvents.BUCKET_EMPTY_POWDER_SNOW, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = super.useOn(context);
        Player player = context.getPlayer();
        if (result.consumesAction() && player != null) {
            ItemStack emptiedResult = WoodenBucketUtil.damageInto(context.getItemInHand(), player, ModItems.WOODEN_BUCKET.get());
            player.setItemInHand(context.getHand(), emptiedResult);
        }

        return result;
    }
}
