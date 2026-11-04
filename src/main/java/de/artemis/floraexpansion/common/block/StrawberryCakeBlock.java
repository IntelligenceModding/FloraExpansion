package de.artemis.floraexpansion.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class StrawberryCakeBlock extends CakeBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return useWithoutItem(state, level, pos, player, hit);
    }

    public static final BooleanProperty HALF_SLICE = BooleanProperty.create("half_slice");
    public static final int PORTIONS = 14;
    private static final VoxelShape[] SHAPES = createShapes();

    public StrawberryCakeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(HALF_SLICE, false));
    }

    private static VoxelShape[] createShapes() {
        var shapes = new VoxelShape[PORTIONS];
        for (int portion = 0; portion < PORTIONS; portion++) {
            int x = 1 + (portion / 2) * 2;
            shapes[portion] = portion % 2 == 0 ? Block.box(x, 0, 1, 15, 8, 15)
                    : Shapes.or(Block.box(x, 0, 8, x + 2, 8, 15),
                            x + 2 < 15 ? Block.box(x + 2, 0, 1, 15, 8, 15) : Shapes.empty());
        }
        return shapes;
    }

    public static int portionsEaten(BlockState state) {
        return state.getValue(BITES) * 2 + (state.getValue(HALF_SLICE) ? 1 : 0);
    }

    @Override
    public void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF_SLICE);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES[portionsEaten(state)];
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return PORTIONS - portionsEaten(state);
    }

    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        InteractionResult result = eatSlice(level, pos, state, player);
        if (level.isClientSide && !result.consumesAction() && player.getMainHandItem().isEmpty()) {
            return InteractionResult.CONSUME;
        }
        return result;
    }

    static InteractionResult eatSlice(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) return InteractionResult.PASS;

        player.awardStat(Stats.EAT_CAKE_SLICE);
        player.getFoodData().eat(1, 0.1F);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (portionsEaten(state) == PORTIONS - 1) {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        } else if (state.getValue(HALF_SLICE)) {
            level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) + 1).setValue(HALF_SLICE, false), 3);
        } else {
            level.setBlock(pos, state.setValue(HALF_SLICE, true), 3);
        }
        return InteractionResult.SUCCESS;
    }

    public @NotNull net.minecraft.world.InteractionResult useWithItem(
            ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult
    ) {
        Item item = stack.getItem();
        if (stack.is(ItemTags.CANDLES) && portionsEaten(state) == 0 && Block.byItem(item) instanceof CandleBlock candleBlock) {
            de.artemis.floraexpansion.common.util.ItemData.consume(stack, 1, player);
            level.playSound(null, pos, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlock(pos, StrawberryCakeWithCandleBlock.byCandle(candleBlock), 3);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.awardStat(Stats.ITEM_USED.get(item));
            return net.minecraft.world.InteractionResult.SUCCESS;
        }

        return net.minecraft.world.InteractionResult.PASS;
    }
}
