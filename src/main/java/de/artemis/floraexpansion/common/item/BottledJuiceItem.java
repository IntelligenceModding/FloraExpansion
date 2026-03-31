package de.artemis.floraexpansion.common.item;

import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BottledJuiceItem extends Item {

    public BottledJuiceItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack result = super.finishUsingItem(itemStack, level, entity);

        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return result;
        }

        ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);

        if (result.isEmpty()) {
            return emptyBottle;
        }

        if (entity instanceof Player player) {
            if (!player.getInventory().add(emptyBottle)) {
                player.drop(emptyBottle, false);
            }
        } else if (!level.isClientSide()) {
            Containers.dropItemStack(level, entity.getX(), entity.getY(), entity.getZ(), emptyBottle);
        }

        return result;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack itemStack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity entity) {
        return 32;
    }
}