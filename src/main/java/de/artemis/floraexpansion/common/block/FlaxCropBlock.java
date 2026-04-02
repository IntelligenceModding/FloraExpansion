package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.particle.ModParticles;
import de.artemis.floraexpansion.common.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FlaxCropBlock extends CropBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final BooleanProperty WILD = ModBlockStateProperties.WILD_FLAX;
    public static final EnumProperty<@NotNull DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
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
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0).setValue(HALF, DoubleBlockHalf.LOWER).setValue(WILD, false));
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

    private void setLower(LevelAccessor level, BlockPos pos, int age, boolean wild) {
        level.setBlock(pos, this.defaultBlockState()
                .setValue(AGE, age)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(WILD, wild), 2);
    }

    private void setUpper(LevelAccessor level, BlockPos pos, int age, boolean wild) {
        level.setBlock(pos, this.defaultBlockState()
                .setValue(AGE, age)
                .setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(WILD, wild), 2);
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

    public void growCropBy(Level level, BlockPos pos, BlockState state, int increment) {
        if (isUpper(state)) {
            pos = pos.below();
            state = level.getBlockState(pos);
        }

        int newAge = Mth.clamp(getAge(state) + increment, 0, getMaxAge());
        boolean wild = state.getValue(WILD);

        setLower(level, pos, newAge, wild);

        BlockPos abovePos = pos.above();
        if (newAge >= DOUBLE_AGE) {
            setUpper(level, abovePos, newAge, wild);
        } else if (level.getBlockState(abovePos).getBlock() == this) {
            level.removeBlock(abovePos, false);
        }
    }
    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        if (isUpper(state)) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this)
                    && isLower(below)
                    && getAge(below) >= DOUBLE_AGE;
        } else {
            BlockState soil = level.getBlockState(pos.below());

            if (state.getValue(WILD)) {
                return soil.is(Blocks.GRASS_BLOCK)
                        || soil.is(Blocks.DIRT)
                        || soil.is(Blocks.COARSE_DIRT)
                        || soil.is(Blocks.PODZOL)
                        || soil.is(Blocks.MOSS_BLOCK)
                        || soil.is(Blocks.ROOTED_DIRT);
            }

            return mayPlaceOn(soil, level, pos.below());
        }
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state,
                                           @NotNull LevelReader level,
                                           @NotNull ScheduledTickAccess scheduledTickAccess,
                                           @NotNull BlockPos pos,
                                           @NotNull Direction direction,
                                           @NotNull BlockPos neighborPos,
                                           @NotNull BlockState neighborState,
                                           @NotNull RandomSource random) {
        if (direction.getAxis() == Direction.Axis.Y) {
            if (isLower(state) && direction == Direction.UP) {
                if (neighborState.is(this) && isUpper(neighborState)) {
                    return state;
                }

                if (getAge(state) >= DOUBLE_AGE) {
                    scheduledTickAccess.scheduleTick(pos, this, 1);
                }
            } else if (isUpper(state) && direction == Direction.DOWN) {
                if (!neighborState.is(this) || !isLower(neighborState)) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (isUpper(state)) {
            if (!level.isClientSide()) {
                BlockPos below = pos.below();
                BlockState belowState = level.getBlockState(below);

                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, pos, Block.getId(state));

                if (belowState.getBlock() == this && isLower(belowState)) {
                    if (player.getAbilities().instabuild) {
                        level.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
                        level.levelEvent(player, 2001, below, Block.getId(belowState));
                    } else {
                        level.destroyBlock(below, true, player);
                    }
                }
            }
        } else {
            BlockPos above = pos.above();
            BlockState aboveState = level.getBlockState(above);
            if (aboveState.getBlock() == this && isUpper(aboveState)) {
                if (!level.isClientSide()) {
                    level.removeBlock(above, false);
                    level.levelEvent(player, 2001, above, Block.getId(aboveState));
                }
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack itemStack,
                                                   @NotNull BlockState blockState,
                                                   @NotNull Level level,
                                                   @NotNull BlockPos blockPos,
                                                   @NotNull Player player,
                                                   @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult result) {
        if (!isUpper(blockState)) {
            return super.useItemOn(itemStack, blockState, level, blockPos, player, hand, result);
        }

        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof ShearsItem)) {
            return super.useItemOn(itemStack, blockState, level, blockPos, player, hand, result);
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockPos belowPos = blockPos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (!(belowState.getBlock() == this && isLower(belowState))) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        int age = getAge(belowState);
        if (age < DOUBLE_AGE) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        Block.dropResources(belowState, level, belowPos, null, player, held);

        level.levelEvent(2001, blockPos, Block.getId(blockState));
        level.levelEvent(2001, belowPos, Block.getId(belowState));

        setLower(level, belowPos, 1, belowState.getValue(WILD));
        if (level.getBlockState(blockPos).is(this)) {
            level.removeBlock(blockPos, false);
        }

        EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        held.hurtAndBreak(1, player, slot);

        return InteractionResult.CONSUME;
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
    public void animateTick(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {

        if (!level.isClientSide()) return;

        if (!isLower(blockState)) return;

        if (getAge(blockState) < MAX_AGE) return;

        if (random.nextFloat() < 0.05f) {
            BlockPos topPos = blockPos.above();
            double x = topPos.getX() + 0.25 + random.nextDouble() * 0.5;
            double z = topPos.getZ() + 0.25 + random.nextDouble() * 0.5;
            double y = topPos.getY() + 0.6 + random.nextDouble() * 0.3; // around crop top

            double vx = (random.nextDouble() - 0.5) * 0.2;  // ±0.01
            double vz = (random.nextDouble() - 0.5) * 0.2;  // ±0.01

            double vy = 0.0 + random.nextDouble() * 0.01;    // 0–0.01

            level.addParticle(ModParticles.FLAX_FLOWER.get(), x, y, z, vx, vy, vz);
        }
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, @NotNull BlockState> builder) {
        builder.add(AGE, HALF, WILD);
    }
}
