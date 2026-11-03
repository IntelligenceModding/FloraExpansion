package de.artemis.floraexpansion.common.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.ArmorItem;
import org.jetbrains.annotations.NotNull;

public class SpikedLeatherArmorItem extends ArmorItem {
    public SpikedLeatherArmorItem(ArmorItem.Type type, Properties properties) {
        super(de.artemis.floraexpansion.common.registry.ModArmorMaterials.SPIKED_LEATHER_ARMOR_MATERIAL, type, properties.durability(type.getDurability(7)));
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

    public static boolean supportsSpikedLeatherArmorEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.THORNS)) {
            return false;
        }

        if (enchantment.is(Enchantments.FIRE_ASPECT)) {
            return true;
        }

        ItemStack reference = getVanillaArmorReference(stack);
        return !reference.isEmpty() && enchantment.value().isSupportedItem(reference);
    }

    public static boolean hasOnlySupportedEnchantments(@NotNull ItemStack stack, ItemEnchantments enchantments) {
        for (var entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();
            int level = entry.getIntValue();
            if (level <= 0 || level > enchantment.value().getMaxLevel() || !supportsSpikedLeatherArmorEnchantment(stack, enchantment)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return supportsSpikedLeatherArmorEnchantment(stack, enchantment);
    }

    @Override
    public boolean isPrimaryItemFor(@NotNull ItemStack stack, @NotNull Holder<Enchantment> enchantment) {
        return supportsSpikedLeatherArmorEnchantment(stack, enchantment);
    }
}
