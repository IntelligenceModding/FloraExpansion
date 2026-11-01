package de.artemis.floraexpansion.common.block;

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
    public HandMillBlock(Properties properties) { super(properties); }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return level.getBlockEntity(pos.above()) instanceof HandCrankBlockEntity crank && crank.isOnMill()
                ? crank.getMillShape() : HandMillShapes.restingMill();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) { return true; }

    @Override
    protected int getLightDampening(BlockState state) { return 0; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new HandMillBlockEntity(pos, state); }

    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                          Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(de.artemis.floraexpansion.common.registry.ModBlocks.HAND_CRANK.get().asItem())) return InteractionResult.PASS;
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof HandMillBlockEntity mill) player.openMenu(mill);
        return InteractionResult.SUCCESS_SERVER;
    }
}
