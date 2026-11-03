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
 @NotNull @Override public com.mojang.serialization.MapCodec<HandMillBlock> codec() { return simpleCodec(HandMillBlock::new); }
    public HandMillBlock(Properties properties) { super(properties); }

    @Override
    @NotNull protected VoxelShape getShape(@NotNull BlockState state, BlockGetter level, BlockPos pos, @NotNull CollisionContext context) {
        return level.getBlockEntity(pos.above()) instanceof HandCrankBlockEntity crank && crank.isOnMill()
                ? crank.getMillShape() : HandMillShapes.restingMill();
    }

    @Override
    protected boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) { return true; }

    @Override
    protected int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) { return 0; }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) { return new HandMillBlockEntity(pos, state); }

    @Override
    @NotNull protected RenderShape getRenderShape(@NotNull BlockState state) { return RenderShape.MODEL; }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState replacement, boolean movedByPiston) {
        if (!state.is(replacement.getBlock())) {
            if (level.getBlockEntity(pos) instanceof HandMillBlockEntity mill) {
                net.minecraft.world.Containers.dropContents(level, pos, mill);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, replacement, movedByPiston);
        }
    }

    @Override
    @NotNull protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (stack.is(de.artemis.floraexpansion.common.registry.ModBlocks.HAND_CRANK.get().asItem())) return net.minecraft.world.ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    @NotNull protected InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof HandMillBlockEntity mill) player.openMenu(mill);
        return InteractionResult.CONSUME;
    }
}
