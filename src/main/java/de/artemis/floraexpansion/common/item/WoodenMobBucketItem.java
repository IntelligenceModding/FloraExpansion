package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WoodenMobBucketItem extends MobBucketItem {
    public WoodenMobBucketItem(EntityType<? extends Mob> entityType, net.minecraft.sounds.SoundEvent emptySound, Properties properties) {
        super(entityType, Fluids.WATER, emptySound, properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hitResult.getType() == HitResult.Type.MISS || hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        BlockPos blockPos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos relativePos = blockPos.relative(direction);
        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(relativePos, direction, itemStack)) {
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
}
