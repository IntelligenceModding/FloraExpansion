package de.artemis.floraexpansion.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;

public class WoodenLavaBucketItem extends WoodenBucketItem {
    public WoodenLavaBucketItem(Properties properties) {
        super(Fluids.LAVA, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (level.isClientSide() || !WoodenBucketUtil.hasExpiredLavaTimeout(stack, level.getGameTime())) {
            return;
        }

        if (entity instanceof Player player) {
            int slotId = player.getInventory().findSlotMatchingItem(stack);
            if (slotId != -1) {
                player.getInventory().setItem(slotId, new ItemStack(Items.CHARCOAL));
            }
            player.igniteForSeconds(5.0F);
            level.playSound(null, player.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.7F, 0.9F);
        }
    }

    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide() && WoodenBucketUtil.hasExpiredLavaTimeout(stack, entity.level().getGameTime())) {
            entity.setItem(new ItemStack(Items.CHARCOAL));
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.7F, 0.9F);
        }

        return false;
    }
}
