package de.artemis.floraexpansion.common.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.NotNull;

public class CactusArmorItem extends Item {
    public CactusArmorItem(Properties properties) {
        super(properties);
    }

    public static boolean isCactusArmor(ItemStack stack) {
        return stack.getItem() instanceof CactusArmorItem;
    }

    private static @NotNull ItemStack getVanillaArmorReference(@NotNull ItemStack stack) {
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return ItemStack.EMPTY;
        }

        return switch (equippable.slot()) {
            case HEAD -> new ItemStack(Items.DIAMOND_HELMET);
            case CHEST -> new ItemStack(Items.DIAMOND_CHESTPLATE);
            case LEGS -> new ItemStack(Items.DIAMOND_LEGGINGS);
            case FEET -> new ItemStack(Items.DIAMOND_BOOTS);
            default -> ItemStack.EMPTY;
        };
    }

    public static boolean supportsCactusArmorEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
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
            if (level <= 0 || level > enchantment.value().getMaxLevel() || !supportsCactusArmorEnchantment(stack, enchantment)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean supportsEnchantment(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        return supportsCactusArmorEnchantment(stack, enchantment);
    }

    @Override
    public boolean isPrimaryItemFor(@NotNull ItemStack stack, Holder<Enchantment> enchantment) {
        return supportsCactusArmorEnchantment(stack, enchantment);
    }
}
