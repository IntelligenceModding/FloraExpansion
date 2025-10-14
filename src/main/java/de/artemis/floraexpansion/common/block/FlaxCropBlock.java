package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FlaxCropBlock extends CropBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final int DOUBLE_AGE = 2;
    private static final int MAX_AGE = 4;

    private static final VoxelShape[] SHAPES_BOTTOM = new VoxelShape[]{
            Block.box(4, 0, 4, 12, 5, 12),
            Block.box(3, 0, 3, 13, 15, 13),
            Block.box(3, 0, 3, 13, 16, 13),
            Block.box(2, 0, 2, 14, 16, 14),
            Block.box(2, 0, 2, 14, 16, 14)
    };

    private static final VoxelShape[] SHAPES_TOP = new VoxelShape[]{
            Block.box(0, 0, 0, 0, 0, 0),
            Block.box(0, 0, 0, 0, 0, 0),
            Block.box(3, 0, 3, 13, 7, 13),
            Block.box(2, 0, 2, 14, 9, 14),
            Block.box(2, 0, 2, 14, 14, 14)
    };

    public FlaxCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    private static boolean isUpper(BlockState blockState) {
        return blockState.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    private static boolean isLower(BlockState blockState) {
        return blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    private boolean ensureSpaceForTop(LevelReader level, BlockPos blockPos, int nextAge) {
        if (nextAge == DOUBLE_AGE) return level.getBlockState(blockPos.above()).canBeReplaced();
        return true;
    }

    private void setLower(LevelAccessor level, BlockPos blockPos, int age) {
        level.setBlock(blockPos, getStateForAge(age).setValue(HALF, DoubleBlockHalf.LOWER), 2);
    }

    private void setUpper(LevelAccessor level, BlockPos blockPos, int age) {
        level.setBlock(blockPos, getStateForAge(age).setValue(HALF, DoubleBlockHalf.UPPER), 2);
    }

    @Override
    protected @NotNull IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState blockState) {
        return !isMaxAge(blockState) && isLower(blockState);
    }

    @Override
    public void randomTick(@NotNull BlockState blockState, @NotNull ServerLevel serverLevel, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        if (isUpper(blockState)) return;
        if (serverLevel.getRawBrightness(blockPos, 0) < 9) return;

        int age = getAge(blockState);
        if (age >= getMaxAge()) return;
        if (!ensureSpaceForTop(serverLevel, blockPos, age + 1)) return;

        float speed = getGrowthSpeed(blockState, serverLevel, blockPos);
        if (random.nextInt((int) (25.0F / speed) + 1) == 0) {
            growCropBy(serverLevel, blockPos, blockState, 1);
        }
    }

    public void growCropBy(Level level, BlockPos blockPos, BlockState blockState, int increment) {
        if (isUpper(blockState)) {
            blockPos = blockPos.below();
            blockState = level.getBlockState(blockPos);
        }

        int newAge = Mth.clamp(getAge(blockState) + increment, 0, getMaxAge());
        setLower(level, blockPos, newAge);

        BlockPos abovePos = blockPos.above();
        if (newAge >= DOUBLE_AGE) {
            setUpper(level, abovePos, newAge);
        } else if (level.getBlockState(abovePos).getBlock() == this) {
            level.removeBlock(abovePos, false);
        }
    }

    @Override
    public boolean canSurvive(@NotNull BlockState blockState, @NotNull LevelReader level, @NotNull BlockPos blockPos) {
        if (isUpper(blockState)) {
            BlockState below = level.getBlockState(blockPos.below());
            return below.is(this) && isLower(below) && getAge(below) >= DOUBLE_AGE;
        } else {
            BlockState soil = level.getBlockState(blockPos.below());
            return mayPlaceOn(soil, level, blockPos.below());
        }
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState blockState, Direction facing, @NotNull BlockState facingBlockState, @NotNull LevelAccessor level, @NotNull BlockPos blockPos, @NotNull BlockPos facingBlockPos) {
        if (facing.getAxis() == Direction.Axis.Y) {
            if (isLower(blockState) && facing == Direction.UP) {
                if (facingBlockState.is(this) && isUpper(facingBlockState)) return blockState;
                if (getAge(blockState) >= DOUBLE_AGE) {
                    level.setBlock(blockPos.above(), getStateForAge(getAge(blockState)).setValue(HALF, DoubleBlockHalf.UPPER), 18);
                }
            } else if (isUpper(blockState) && facing == Direction.DOWN) {
                if (!facingBlockState.is(this) || !isLower(facingBlockState)) return Blocks.AIR.defaultBlockState();
            }
        }
        return super.updateShape(blockState, facing, facingBlockState, level, blockPos, facingBlockPos);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (isUpper(state)) {
            if (!level.isClientSide) {
                BlockPos below = pos.below();
                BlockState belowState = level.getBlockState(below);

                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, pos, Block.getId(state)); // particles at top

                if (belowState.getBlock() == this && isLower(belowState)) {
                    if (player.getAbilities().instabuild) {
                        level.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
                        level.levelEvent(player, 2001, below, Block.getId(belowState));
                    } else {
                        level.destroyBlock(below, true, player);
                    }
                }
            }
            return super.playerWillDestroy(level, pos, state, player);
        } else {
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.getBlock() == this && isUpper(aboveState)) {
                if (!level.isClientSide) {
                    level.removeBlock(above, false);
                    level.levelEvent(player, 2001, above, Block.getId(aboveState));
                }
            }
            return super.playerWillDestroy(level, pos, state, player);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
        return isUpper(blockState) ? SHAPES_TOP[getAge(blockState)] : SHAPES_BOTTOM[getAge(blockState)];
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        if (isUpper(blockState)) {
            blockPos = blockPos.below();
            blockState = level.getBlockState(blockPos);
            if (blockState.getBlock() != this) return false;
        }
        if (this.isMaxAge(blockState)) return false;
        int age = this.getAge(blockState);
        if (age + 1 == DOUBLE_AGE) return level.getBlockState(blockPos.above()).canBeReplaced();
        return true;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        if (isUpper(blockState)) {
            blockPos = blockPos.below();
            blockState = level.getBlockState(blockPos);
        }
        if (blockState.getBlock() == this) {
            growCropBy(level, blockPos, blockState, Mth.nextInt(random, 1, 2));
        }
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return ModItems.FLAX_SEED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF);
    }
}