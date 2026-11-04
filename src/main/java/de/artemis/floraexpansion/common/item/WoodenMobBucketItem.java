package de.artemis.floraexpansion.common.item;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
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
    @NotNull public net.minecraft.world.InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return net.minecraft.world.InteractionResultHolder.pass(player.getItemInHand(hand));
        }

        BlockPos blockPos = hitResult.getBlockPos();
        Direction direction = hitResult.getDirection();
        BlockPos relativePos = blockPos.relative(direction);
        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(relativePos, direction, itemStack)) {
            return net.minecraft.world.InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        BlockState blockState = level.getBlockState(blockPos);
        BlockPos placePos = canBlockContainFluid(level, blockPos, blockState) ? blockPos : relativePos;
        return WoodenBucketUtil.emptyContents(this, level, player, itemStack, placePos, hitResult);
    }
}
