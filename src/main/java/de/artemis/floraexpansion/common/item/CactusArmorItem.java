package de.artemis.floraexpansion.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class CactusArmorItem extends Item {
    public CactusArmorItem(Properties properties) {
        super(properties);
    }

    public static boolean isCactusArmor(ItemStack stack) {
        return stack.getItem() instanceof CactusArmorItem;
    }

    public boolean supportsEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        return !enchantment.is(Enchantments.THORNS);
    }

    public boolean isPrimaryItemFor(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        return !enchantment.is(Enchantments.THORNS);
    }
}