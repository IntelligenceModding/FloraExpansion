package de.artemis.floraexpansion.common.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

public final class WoodenBucketUtil {
    private static final String LAVA_EXPIRE_TIME_KEY = "LavaExpireTime";

    private WoodenBucketUtil() {
    }

    public static ItemStack damageInto(ItemStack sourceStack, Player player, Item targetItem) {
        if (player.hasInfiniteMaterials()) {
            return sourceStack;
        }

        int nextDamage = sourceStack.getDamageValue() + 1;
        if (nextDamage >= sourceStack.getMaxDamage()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(targetItem);
        result.setDamageValue(nextDamage);
        copyCustomName(sourceStack, result);
        return result;
    }

    public static ItemStack transformInto(ItemStack sourceStack, Player player, Item targetItem) {
        return transformInto(sourceStack, player, targetItem, null);
    }

    public static ItemStack transformInto(ItemStack sourceStack, Player player, Item targetItem, Consumer<ItemStack> extraSetup) {
        int damage = sourceStack.getDamageValue();
        if (!player.hasInfiniteMaterials()) {
            damage++;
            if (damage >= sourceStack.getMaxDamage()) {
                return ItemStack.EMPTY;
            }
        }

        ItemStack result = new ItemStack(targetItem);
        result.setDamageValue(damage);
        copyCustomName(sourceStack, result);
        if (extraSetup != null) {
            extraSetup.accept(result);
        }

        return result;
    }

    public static boolean isSupportedFluidBucket(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET) || stack.is(Items.LAVA_BUCKET) || stack.is(Items.POWDER_SNOW_BUCKET);
    }

    public static void initializeLavaTimeout(ItemStack stack, long gameTime, RandomSource random) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag ->
                tag.putLong(LAVA_EXPIRE_TIME_KEY, gameTime + 100L + random.nextInt(101)));
    }

    public static boolean hasExpiredLavaTimeout(ItemStack stack, long gameTime) {
        long expireTime = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getLong(LAVA_EXPIRE_TIME_KEY).orElse(0L);
        return expireTime > 0L && gameTime >= expireTime;
    }

    private static void copyCustomName(ItemStack sourceStack, ItemStack result) {
        Component customName = sourceStack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            result.set(DataComponents.CUSTOM_NAME, customName);
        }
    }
}
