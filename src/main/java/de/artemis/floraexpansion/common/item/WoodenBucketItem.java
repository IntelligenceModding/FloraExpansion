package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WoodenBucketItem extends BucketItem {
    public WoodenBucketItem(Fluid content, Properties properties) {
        super(content, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(
                level,
                player,
                this.content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE
        );

        if (hitResult.getType() == HitResult.Type.MISS || hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos blockPos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos relativePos = blockPos.relative(direction);
        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(relativePos, direction, itemStack)) {
            return InteractionResult.FAIL;
        }

        if (this.content == Fluids.EMPTY) {
            BlockState blockState = level.getBlockState(blockPos);
            if (blockState.getBlock() instanceof BucketPickup bucketPickup) {
                ItemStack pickedUpStack = bucketPickup.pickupBlock(player, level, blockPos, blockState);
                if (!pickedUpStack.isEmpty() && WoodenBucketUtil.isSupportedFluidBucket(pickedUpStack)) {
                    player.awardStat(Stats.ITEM_USED.get(this));
                    bucketPickup.getPickupSound(blockState).ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
                    level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);

                    ItemStack filledResult = getFilledPickupResult(itemStack, player, level, pickedUpStack);
                    if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                        CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, pickedUpStack);
                    }

                    return InteractionResult.SUCCESS.heldItemTransformedTo(filledResult);
                }
            }

            return InteractionResult.FAIL;
        }

        BlockState blockState = level.getBlockState(blockPos);
        BlockPos placePos = canBlockContainFluid(player, level, blockPos, blockState) ? blockPos : relativePos;
        if (!this.emptyContents(player, level, placePos, hitResult, itemStack)) {
            return InteractionResult.FAIL;
        }

        this.checkExtraContent(player, level, itemStack, placePos);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, placePos, itemStack);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        ItemStack emptiedResult = WoodenBucketUtil.damageInto(itemStack, player, ModItems.WOODEN_BUCKET.get());
        return InteractionResult.SUCCESS.heldItemTransformedTo(emptiedResult);
    }

    private ItemStack getFilledPickupResult(ItemStack sourceStack, Player player, Level level, ItemStack pickedUpStack) {
        if (pickedUpStack.is(Items.WATER_BUCKET)) {
            return WoodenBucketUtil.transformInto(sourceStack, player, ModItems.WOODEN_WATER_BUCKET.get());
        }

        if (pickedUpStack.is(Items.LAVA_BUCKET)) {
            return WoodenBucketUtil.transformInto(sourceStack, player, ModItems.WOODEN_LAVA_BUCKET.get(),
                    result -> WoodenBucketUtil.initializeLavaTimeout(result, level.getGameTime(), level.getRandom()));
        }

        if (pickedUpStack.is(Items.POWDER_SNOW_BUCKET)) {
            return WoodenBucketUtil.transformInto(sourceStack, player, ModItems.WOODEN_POWDER_SNOW_BUCKET.get());
        }

        return ItemStack.EMPTY;
    }
}
