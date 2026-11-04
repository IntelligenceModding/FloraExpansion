package de.artemis.floraexpansion.common.item;


import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;

public class SpikedLeatherArmorItem extends ArmorItem implements net.minecraft.world.item.DyeableLeatherItem {
    public SpikedLeatherArmorItem(ArmorItem.Type type, Properties properties) {
        super(de.artemis.floraexpansion.common.registry.ModArmorMaterials.SPIKED_LEATHER_ARMOR_MATERIAL, type, properties);
    }

    public static boolean isSpikedLeatherArmor(ItemStack stack) {
        return stack.getItem() instanceof SpikedLeatherArmorItem;
    }

    private static @NotNull ItemStack getVanillaArmorReference(@NotNull ItemStack stack) {
        if (!(stack.getItem() instanceof ArmorItem armor)) return ItemStack.EMPTY;
        return switch (armor.getEquipmentSlot()) {
            case HEAD -> new ItemStack(Items.DIAMOND_HELMET);
            case CHEST -> new ItemStack(Items.DIAMOND_CHESTPLATE);
            case LEGS -> new ItemStack(Items.DIAMOND_LEGGINGS);
            case FEET -> new ItemStack(Items.DIAMOND_BOOTS);
            default -> ItemStack.EMPTY;
        };
    }

    public static boolean supportsSpikedLeatherArmorEnchantment(@NotNull ItemStack stack, Enchantment enchantment) {
        if ((enchantment == Enchantments.THORNS)) {
            return false;
        }

        if ((enchantment == Enchantments.FIRE_ASPECT)) {
            return true;
        }

        ItemStack reference = getVanillaArmorReference(stack);
        return !reference.isEmpty() && enchantment.canEnchant(reference);
    }

    public static boolean hasOnlySupportedEnchantments(@NotNull ItemStack stack, java.util.Map<Enchantment,Integer> enchantments) {
        for (var entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();
            if (level <= 0 || level > enchantment.getMaxLevel() || !supportsSpikedLeatherArmorEnchantment(stack, enchantment)) {
                return false;
            }
        }

        return true;
    }

    @Override public boolean isBookEnchantable(ItemStack stack, ItemStack book) { return hasOnlySupportedEnchantments(stack, net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantments(book)); }
    @Override public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if ((enchantment == Enchantments.THORNS)) return false;
        return (enchantment == Enchantments.FIRE_ASPECT) || enchantment.canEnchant(getVanillaArmorReference(stack));
    }
}
