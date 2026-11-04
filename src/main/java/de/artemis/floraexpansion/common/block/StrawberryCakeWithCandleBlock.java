package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.block.entity.StrawberryCakeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A strawberry cake with a candle. Extending CandleCakeBlock would overwrite
 * vanilla's private candle-to-cake lookup during construction.
 */
public class StrawberryCakeWithCandleBlock extends AbstractCandleBlock implements EntityBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return useWithoutItem(state, level, pos, player, hit);
    }


 public static final ResourceLocation CANDLE_DROPS = ResourceLocation.fromNamespaceAndPath("floraexpansion", "cake_candles");
    private static final Map<CandleBlock, StrawberryCakeWithCandleBlock> BY_CANDLE = new HashMap<>();
    public static final int MAX_CANDLES = 3;
    public static final IntegerProperty CANDLES = IntegerProperty.create("candles", 1, MAX_CANDLES);
    private static final List<List<Vec3>> CANDLE_OFFSETS = List.of(
            List.of(new Vec3(8, 16, 8).scale(0.0625)),
            List.of(new Vec3(5, 15, 8).scale(0.0625), new Vec3(11, 16, 8).scale(0.0625)),
            List.of(new Vec3(5, 15, 6).scale(0.0625), new Vec3(11, 17, 6).scale(0.0625), new Vec3(8, 16, 11).scale(0.0625)));
    private static final VoxelShape[] SHAPES = createShapes();
    private final CandleBlock candle;

    public StrawberryCakeWithCandleBlock(Block candleBlock, Properties properties) {
        super(properties);
        if (!(candleBlock instanceof CandleBlock matchingCandle)) {
            throw new IllegalArgumentException("Expected CandleBlock but got " + candleBlock.getClass());
        }
        candle = matchingCandle;
        BY_CANDLE.put(candle, this);
        registerDefaultState(defaultBlockState().setValue(LIT, false).setValue(CANDLES, 1));
    }

    public static List<Vec3> candleOffsets(int count) {
        return CANDLE_OFFSETS.get(count - 1);
    }

    private static VoxelShape[] createShapes() {
        var shapes = new VoxelShape[MAX_CANDLES];
        for (int count = 1; count <= MAX_CANDLES; count++) {
            VoxelShape shape = Block.box(1, 0, 1, 15, 8, 15);
            for (Vec3 offset : candleOffsets(count)) {
                double x = offset.x * 16, z = offset.z * 16;
                shape = Shapes.or(shape, Block.box(x - 1, 8, z - 1, x + 1, offset.y * 16 - 2, z + 1));
            }
            shapes[count - 1] = shape;
        }
        return shapes;
    }

    public CandleBlock getCandle() {
        return candle;
    }

    public static BlockState byCandle(CandleBlock candle) {
        return BY_CANDLE.get(candle).defaultBlockState();
    }

    @Override
    @NotNull public Iterable<Vec3> getParticleOffsets(BlockState state) {
        return candleOffsets(state.getValue(CANDLES));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES[state.getValue(CANDLES) - 1];
    }

    public @NotNull net.minecraft.world.InteractionResult useWithItem(ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                   @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (stack.is(ItemTags.CANDLES)) {
            int count = state.getValue(CANDLES);
            if (count >= MAX_CANDLES || !(Block.byItem(stack.getItem()) instanceof CandleBlock addedCandle)
                    || !(level.getBlockEntity(pos) instanceof StrawberryCakeBlockEntity cakeEntity)) return net.minecraft.world.InteractionResult.FAIL;
            var colors = new java.util.ArrayList<>(cakeEntity.getCandles());
            colors.add(addedCandle);
            de.artemis.floraexpansion.common.util.ItemData.consume(stack, 1, player);
            level.setBlock(pos, state.setValue(CANDLES, count + 1), 3);
            cakeEntity.setCandles(colors);
            level.playSound(null, pos, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1, 1);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            player.awardStat(Stats.ITEM_USED.get(addedCandle.asItem()));
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        if (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE)) {
            return net.minecraft.world.InteractionResult.PASS;
        }
        if (hit.getLocation().y - pos.getY() > 0.5 && stack.isEmpty() && state.getValue(LIT)) {
            extinguish(player, state, level, pos);
            return net.minecraft.world.InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        BlockEntity cakeEntity = level.getBlockEntity(pos);
        InteractionResult result = StrawberryCakeBlock.eatSlice(level, pos, ModBlocks.STRAWBERRY_CAKE.get().defaultBlockState(), player);
        if (result.consumesAction()) {
            dropResources(state, level, pos, cakeEntity);
        }
        return result;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull net.minecraft.world.level.BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(ModBlocks.STRAWBERRY_CAKE.get());
    }

    @Override
    public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, CANDLES);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.core.Direction direction, @NotNull net.minecraft.world.level.block.state.BlockState neighborState, @NotNull net.minecraft.world.level.LevelAccessor level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.core.BlockPos neighborPos) {
        return direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isSolid();
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return CakeBlock.FULL_CAKE_SIGNAL;
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull net.minecraft.world.level.BlockGetter level, @NotNull net.minecraft.core.BlockPos pos, @NotNull PathComputationType type) {
        return false;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new StrawberryCakeBlockEntity(pos, state);
    }

    @Override
    @NotNull public List<ItemStack> getDrops(@NotNull BlockState state, LootParams.Builder params) {
        var colors = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof StrawberryCakeBlockEntity cakeEntity
                ? cakeEntity.getCandles() : java.util.Collections.nCopies(state.getValue(CANDLES), candle);
        params.withDynamicDrop(CANDLE_DROPS, output -> colors.forEach(color -> output.accept(new ItemStack(color))));
        return super.getDrops(state, params);
    }
}
