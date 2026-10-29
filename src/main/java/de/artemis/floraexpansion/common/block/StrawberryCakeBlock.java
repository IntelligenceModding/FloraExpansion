package de.artemis.floraexpansion.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF_SLICE);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[portionsEaten(state)];
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        return PORTIONS - portionsEaten(state);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        InteractionResult result = eatSlice(level, pos, state, player);
        if (level.isClientSide() && !result.consumesAction() && player.getMainHandItem().isEmpty()) {
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
            level.setBlockAndUpdate(pos, state.setValue(BITES, state.getValue(BITES) + 1).setValue(HALF_SLICE, false));
        } else {
            level.setBlockAndUpdate(pos, state.setValue(HALF_SLICE, true));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        Item item = stack.getItem();
        if (stack.is(ItemTags.CANDLES) && portionsEaten(state) == 0 && Block.byItem(item) instanceof CandleBlock candleBlock) {
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.setBlockAndUpdate(pos, StrawberryCakeWithCandleBlock.byCandle(candleBlock));
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.awardStat(Stats.ITEM_USED.get(item));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}
