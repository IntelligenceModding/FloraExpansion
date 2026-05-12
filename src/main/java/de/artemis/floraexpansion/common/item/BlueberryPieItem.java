package de.artemis.floraexpansion.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BlueberryPieItem extends Item {
    private static final int REGENERATION_DURATION_TICKS = 100;
    private static final int USE_DURATION_TICKS = 128;

    public BlueberryPieItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (!level.isClientSide) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGENERATION_DURATION_TICKS, 0));
        }

        return result;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity entity) {
        return USE_DURATION_TICKS;
    }
}
