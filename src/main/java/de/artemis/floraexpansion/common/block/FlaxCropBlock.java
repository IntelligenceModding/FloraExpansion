package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
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
        // Disallow breaking just the top when the plant is exactly age=2 (tall start)
        if (isUpper(state)) {
            BlockPos belowPos = pos.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (belowState.getBlock() == this && isLower(belowState) && getAge(belowState) == DOUBLE_AGE
                    && !player.getAbilities().instabuild) {
                if (!level.isClientSide) {
                    // Ensure upper still matches the lower half (server authority), no drops
                    setUpper(level, pos, DOUBLE_AGE);
                }
                return state; // cancel the normal break path
            }
        }

        // --- Existing behavior unchanged below ---
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
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack itemStack, @NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult result) {
        // Only handle shearing when right-clicking the UPPER half
        if (!isUpper(blockState)) {
            return super.useItemOn(itemStack, blockState, level, blockPos, player, hand, result);
        }

        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof ShearsItem)) {
            return super.useItemOn(itemStack, blockState, level, blockPos, player, hand, result);
        }

        if (level.isClientSide) {
            // client handshake: show the swing/interaction immediately
            return ItemInteractionResult.SUCCESS;
        }

        BlockPos belowPos = blockPos.below();
        BlockState belowState = level.getBlockState(belowPos);
        if (!(belowState.getBlock() == this && isLower(belowState))) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        int age = getAge(belowState);
        if (age < DOUBLE_AGE) {
            // not tall yet – do nothing special
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Drop normal loot as if broken “by hand”
        // (Your loot table already ignores fortune, so passing shears here is fine.)
        Block.dropResources(belowState, level, belowPos, null, player, held);

        // Feedback
        level.levelEvent(2001, blockPos, Block.getId(blockState));       // particles at top
        level.levelEvent(2001, belowPos, Block.getId(belowState));

        // Regress the crop to AGE = 1 (single-block stage) and remove upper
        setLower(level, belowPos, 1);
        if (level.getBlockState(blockPos).is(this)) {
            level.removeBlock(blockPos, false);
        }

        // Damage the shears
        EquipmentSlot slot = (hand == InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
        held.hurtAndBreak(1, player, slot);

        return ItemInteractionResult.CONSUME; // we handled it fully
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

        if (!level.isClientSide) return;

        if (!isLower(blockState)) return;

        if (getAge(blockState) < DOUBLE_AGE) return;

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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF);
    }
}