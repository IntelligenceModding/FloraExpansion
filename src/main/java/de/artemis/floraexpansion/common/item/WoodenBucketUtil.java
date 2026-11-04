package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import de.artemis.floraexpansion.common.util.ItemData;
import de.artemis.floraexpansion.common.util.ItemData.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import de.artemis.floraexpansion.common.util.ItemData.CustomData;

import java.util.function.Consumer;
import de.artemis.floraexpansion.common.config.ServerConfig;

public final class WoodenBucketUtil {
    private static final String LAVA_EXPIRE_TIME_KEY = "LavaExpireTime";

    private WoodenBucketUtil() {
    }

    public static net.minecraft.world.InteractionResultHolder<ItemStack> emptyContents(BucketItem bucket, Level level, Player player, ItemStack stack,
                                                   BlockPos placePos, BlockHitResult hitResult) {
        if (!bucket.emptyContents(player, level, placePos, hitResult, stack)) {
            return net.minecraft.world.InteractionResultHolder.fail(stack);
        }
        bucket.checkExtraContent(player, level, stack, placePos);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, placePos, stack);
        }
        player.awardStat(Stats.ITEM_USED.get(bucket));
        return net.minecraft.world.InteractionResultHolder.sidedSuccess(damageInto(stack, player, ModItems.WOODEN_BUCKET.get()), level.isClientSide);
    }

    public static ItemStack damageInto(ItemStack sourceStack, Player player, Item targetItem) {
        if (player.getAbilities().instabuild) {
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
        if (!player.getAbilities().instabuild) {
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
        return stack.is(Items.WATER_BUCKET) || stack.is(Items.LAVA_BUCKET) || stack.is(Items.POWDER_SNOW_BUCKET)
                || stack.is(ModItems.SALT_WATER_BUCKET.get());
    }

    public static void initializeLavaTimeout(ItemStack stack, long gameTime, RandomSource random) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag ->
                tag.putLong(LAVA_EXPIRE_TIME_KEY, gameTime + ServerConfig.lavaMinTicks.get() + random.nextInt(Math.max(0, ServerConfig.lavaMaxTicks.get() - ServerConfig.lavaMinTicks.get()) + 1)));
    }

    public static boolean hasExpiredLavaTimeout(ItemStack stack, long gameTime) {
        long expireTime = ItemData.getOrDefault(stack, DataComponents.CUSTOM_DATA, CustomData.EMPTY)
                .copyTag()
                .getLong(LAVA_EXPIRE_TIME_KEY);
        return expireTime > 0L && gameTime >= expireTime;
    }

    private static void copyCustomName(ItemStack sourceStack, ItemStack result) {
        Component customName = ItemData.get(sourceStack, DataComponents.CUSTOM_NAME);
        if (customName != null) {
            ItemData.set(result, DataComponents.CUSTOM_NAME, customName);
        }
    }
}
