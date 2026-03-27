package de.artemis.floraexpansion.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jetbrains.annotations.NotNull;

public class CactusArmorItem extends ArmorItem {
    public CactusArmorItem(Holder<ArmorMaterial> material, Type type, Item.Properties properties) {
        super(material, type, properties);
    }

    public static boolean isCactusArmor(ItemStack stack) {
        return stack.getItem() instanceof CactusArmorItem;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.THORNS)) {
            return false;
        }

        return super.supportsEnchantment(stack, enchantment);
    }

    @Override
    public boolean isPrimaryItemFor(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.THORNS)) {
            return false;
        }

        return super.isPrimaryItemFor(stack, enchantment);
    }
}