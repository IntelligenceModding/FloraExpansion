package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;
import de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HandMillBlock extends BaseEntityBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return useWithoutItem(state, level, pos, player, hit);
    }


    public HandMillBlock(Properties properties) { super(properties); }

    @Override
    @NotNull public VoxelShape getShape(@NotNull BlockState state, BlockGetter level, BlockPos pos, @NotNull CollisionContext context) {
        return level.getBlockEntity(pos.above()) instanceof HandCrankBlockEntity crank && crank.isOnMill()
                ? crank.getMillShape() : HandMillShapes.restingMill();
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) { return true; }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) { return 0; }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) { return new HandMillBlockEntity(pos, state); }

    @Override
    @NotNull public RenderShape getRenderShape(@NotNull BlockState state) { return RenderShape.MODEL; }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState replacement, boolean movedByPiston) {
        if (!state.is(replacement.getBlock())) {
            if (level.getBlockEntity(pos) instanceof HandMillBlockEntity mill) {
                net.minecraft.world.Containers.dropContents(level, pos, mill);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, replacement, movedByPiston);
        }
    }

    @NotNull public net.minecraft.world.InteractionResult useWithItem(ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (stack.is(de.artemis.floraexpansion.common.registry.ModBlocks.HAND_CRANK.get().asItem())) return net.minecraft.world.InteractionResult.PASS;
        return net.minecraft.world.InteractionResult.PASS;
    }

    @NotNull public InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof HandMillBlockEntity mill) player.openMenu(mill);
        return InteractionResult.CONSUME;
    }
}
