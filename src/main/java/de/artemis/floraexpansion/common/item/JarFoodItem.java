package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class JarFoodItem extends Item {
    public JarFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity entity) {
        ItemStack result = super.finishUsingItem(itemStack, level, entity);

        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return result;
        }

        ItemStack emptyJar = new ItemStack(ModItems.EMPTY_JAR.get());

        if (result.isEmpty()) {
            return emptyJar;
        }

        if (entity instanceof Player player) {
            if (!player.getInventory().add(emptyJar)) {
                player.drop(emptyJar, false);
            }
        } else if (level instanceof ServerLevel serverLevel) {
            entity.spawnAtLocation(serverLevel, emptyJar);
        }

        return result;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack itemStack) {
        return ItemUseAnimation.DRINK;
    }

    public @NotNull SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK.value();
    }

    public @NotNull SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK.value();
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity entity) {
        return 32;
    }
}
