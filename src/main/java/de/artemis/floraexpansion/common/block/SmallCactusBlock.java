package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.util.PlantSoils;
import de.artemis.floraexpansion.common.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.loot.HarvestLoot;

public class SmallCactusBlock extends BushBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return super.use(state, level, pos, player, hand, hit);
    }



    public static final IntegerProperty PICKLES = BlockStateProperties.PICKLES;

    private static final VoxelShape SHAPE_ONE = Block.box(6, 0.0, 6, 10, 6.0, 10);
    private static final VoxelShape SHAPE_TWO = Block.box(3, 0.0, 3, 13, 6.0, 13);
    private static final VoxelShape SHAPE_THREE = Block.box(2, 0.0, 2, 14, 6.0, 14);
    private static final VoxelShape SHAPE_FOUR = Block.box(2, 0.0, 2, 14, 7.0, 14);

    public SmallCactusBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PICKLES, 1));
    }

    @Override
    public boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return PlantSoils.supportsCactus(state);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return this.mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
        return (!context.isSecondaryUseActive()
                && context.getItemInHand().is(this.asItem())
                && state.getValue(PICKLES) < 4)
                || super.canBeReplaced(state, context);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (existing.is(this)) {
            return existing.setValue(PICKLES, Math.min(4, existing.getValue(PICKLES) + 1));
        }

        return this.defaultBlockState();
    }

    public @NotNull net.minecraft.world.InteractionResult useWithItem(@NotNull ItemStack stack,
                                                   @NotNull BlockState state,
                                                   @NotNull Level level,
                                                   @NotNull BlockPos pos,
                                                   @NotNull Player player,
                                                   @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult hitResult) {
        if (!stack.is(Items.GLASS_BOTTLE)) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            var loot = HarvestLoot.drops((net.minecraft.server.level.ServerLevel) level, pos, state, player, stack);
            ItemStack juiceStack = loot.isEmpty() ? ItemStack.EMPTY : loot.get(0);
            for (int i = 1; i < loot.size(); i++) InventoryUtils.giveOrDrop(player, loot.get(i));

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (!player.getAbilities().instabuild && stack.isEmpty()) {
                player.setItemInHand(hand, juiceStack);
            } else {
                InventoryUtils.giveOrDrop(player, juiceStack);
            }

            int count = state.getValue(PICKLES);
            if (count > 1) {
                level.setBlock(pos, state.setValue(PICKLES, count - 1), Block.UPDATE_ALL);
            } else {
                level.removeBlock(pos, false);
            }

            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state,
                                           @NotNull BlockGetter level,
                                           @NotNull BlockPos pos,
                                           @NotNull CollisionContext context) {
        return switch (state.getValue(PICKLES)) {
            case 2 -> SHAPE_TWO;
            case 3 -> SHAPE_THREE;
            case 4 -> SHAPE_FOUR;
            default -> SHAPE_ONE;
        };
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, @NotNull BlockState> builder) {
        builder.add(PICKLES);
    }
}
