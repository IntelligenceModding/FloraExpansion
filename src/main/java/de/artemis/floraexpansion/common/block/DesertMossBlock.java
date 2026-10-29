package de.artemis.floraexpansion.common.block;

import net.minecraft.core.BlockPos;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BonemealSource;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DesertMossBlock extends BushBlock implements BonemealableBlock {
    private final boolean flowering;

    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 3);

    protected static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public DesertMossBlock(Properties properties) {
        this(properties, false);
    }

    public DesertMossBlock(Properties properties, boolean flowering) {
        super(properties);
        this.flowering = flowering;
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, 0));
    }


    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return stateForPosition(context.getClickedPos());
    }

    public BlockState stateForPosition(BlockPos pos) {
        int variant = Math.floorMod((int) Mth.getSeed(pos), 4);
        return this.defaultBlockState().setValue(VARIANT, variant);
    }

    public static BlockState naturalState(RandomSource random) {
        int appearance = random.nextInt(8);
        Block moss = (appearance & 1) == 0 ? ModBlocks.DESERT_MOSS.get() : ModBlocks.FLOWERING_DESERT_MOSS.get();
        return moss.defaultBlockState().setValue(VARIANT, appearance >>> 1);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
        return !flowering || !spreadPositions(level, pos).isEmpty();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
        var flowers = (DesertMossBlock) ModBlocks.FLOWERING_DESERT_MOSS.get();
        if (!flowering) {
            level.setBlockAndUpdate(pos, flowers.defaultBlockState().setValue(VARIANT, state.getValue(VARIANT)));
            return;
        }
        var positions = spreadPositions(level, pos);
        for (int i = 0, count = Math.min(4, positions.size()); i < count; i++) {
            BlockPos target = positions.remove(random.nextInt(positions.size()));
            level.setBlockAndUpdate(target, flowers.stateForPosition(target));
        }
    }

    private List<BlockPos> spreadPositions(LevelReader level, BlockPos pos) {
        var positions = new ArrayList<BlockPos>();
        for (BlockPos target : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 1, 2))) {
            var state = level.getBlockState(target);
            if ((state.isAir() || state.is(ModBlocks.DESERT_MOSS.get())) && defaultBlockState().canSurvive(level, target)) {
                positions.add(target.immutable());
            }
        }
        return positions;
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.isFaceSturdy(level, pos, net.minecraft.core.Direction.UP);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        return this.mayPlaceOn(belowState, level, belowPos);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, @NotNull BlockState> builder) {
        builder.add(VARIANT);
    }
}
