package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;
import de.artemis.floraexpansion.common.config.ServerConfig;

public final class WoodenBucketUtil {
    private static final String LAVA_EXPIRE_TIME_KEY = "LavaExpireTime";

    private WoodenBucketUtil() {
    }

    public static InteractionResult emptyContents(BucketItem bucket, Level level, Player player, ItemStack stack,
                                                   BlockPos placePos, BlockHitResult hitResult) {
        if (!bucket.emptyContents(player, level, placePos, hitResult, stack)) {
            return InteractionResult.FAIL;
        }
        bucket.checkExtraContent(player, level, stack, placePos);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, placePos, stack);
        }
        player.awardStat(Stats.ITEM_USED.get(bucket));
        return InteractionResult.SUCCESS.heldItemTransformedTo(damageInto(stack, player, ModItems.WOODEN_BUCKET.get()));
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
                tag.putLong(LAVA_EXPIRE_TIME_KEY, gameTime + ServerConfig.lavaMinTicks.get() + random.nextInt(Math.max(0, ServerConfig.lavaMaxTicks.get() - ServerConfig.lavaMinTicks.get()) + 1)));
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
