package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.block.*;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModParticles;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SaltSpikeBlock extends PointedDripstoneBlock {
    public static final MapCodec<PointedDripstoneBlock> CODEC = simpleCodec(SaltSpikeBlock::new);
    public static final EnumProperty<Direction> TIP_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
    public static final EnumProperty<DripstoneThickness> THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final float WATER_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.17578125F;
    public static final float LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK = 0.05859375F;
    private static final VoxelShape SHAPE_TIP_MERGE = Block.box(5, 0.0, 5, 11, 16.0, 11);
    private static final VoxelShape SHAPE_TIP_UP = Block.box(5, 0.0, 5, 11, 11.0, 11);
    private static final VoxelShape SHAPE_TIP_DOWN = Block.box(5, 5.0, 5, 11, 16.0, 11);
    private static final VoxelShape SHAPE_FRUSTUM = Block.box(4, 0.0, 4, 12, 16.0, 12);
    private static final VoxelShape SHAPE_MIDDLE = Block.box(3, 0.0, 3, 13, 16.0, 13);
    private static final VoxelShape SHAPE_BASE = Block.box(2, 0.0, 2, 14, 16.0, 14);
    private static final double STALACTITE_DRIP_START_PIXEL = SHAPE_TIP_DOWN.min(Direction.Axis.Y);
    private static final float MAX_HORIZONTAL_OFFSET = (float)SHAPE_BASE.min(Direction.Axis.X);
    private static final VoxelShape REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK = Block.box(6, 0.0, 6, 10, 16.0, 10);

    @Override
    @NotNull public MapCodec<PointedDripstoneBlock> codec() {
        return CODEC;
    }

    public SaltSpikeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
            this.stateDefinition.any().setValue(TIP_DIRECTION, Direction.UP).setValue(THICKNESS, DripstoneThickness.TIP).setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TIP_DIRECTION, THICKNESS, WATERLOGGED);
    }

    @Override
    protected boolean canSurvive(BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return isValidPointedDripstonePlacement(level, pos, state.getValue(TIP_DIRECTION));
    }

    @Override
    @NotNull protected BlockState updateShape(net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.core.Direction directionToNeighbour, @NotNull net.minecraft.world.level.block.state.BlockState neighbourState, @NotNull net.minecraft.world.level.LevelAccessor level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.core.BlockPos neighbourPos) {
        var ticks = level;
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        if (directionToNeighbour != Direction.UP && directionToNeighbour != Direction.DOWN) {
            return state;
        } else {
            Direction tipDirection = state.getValue(TIP_DIRECTION);
            if (tipDirection == Direction.DOWN && ticks.getBlockTicks().hasScheduledTick(pos, this)) {
                return state;
            } else if (directionToNeighbour == tipDirection.getOpposite() && !this.canSurvive(state, level, pos)) {
                if (tipDirection == Direction.DOWN) {
                    ticks.scheduleTick(pos, this, 2);
                } else {
                    ticks.scheduleTick(pos, this, 1);
                }

                return state;
            } else {
                boolean mergeOpposingTips = state.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
                DripstoneThickness newThickness = calculateDripstoneThickness(level, pos, tipDirection, mergeOpposingTips);
                return state.setValue(THICKNESS, newThickness);
            }
        }
    }

    @Override
    protected void onProjectileHit(Level level, @NotNull BlockState state, @NotNull BlockHitResult blockHit, @NotNull Projectile projectile) {
        if (!level.isClientSide) {
            BlockPos blockPos = blockHit.getBlockPos();
            if (level instanceof ServerLevel serverLevel
                && projectile.mayInteract(serverLevel, blockPos)
                && projectile.mayBreak(serverLevel)
                && projectile instanceof ThrownTrident
                && projectile.getDeltaMovement().length() > 0.6) {
                level.destroyBlock(blockPos, true);
            }
        }
    }

    @Override
    public void fallOn(@NotNull Level level, BlockState state, @NotNull BlockPos pos, @NotNull Entity entity, float fallDistance) {
        if (state.getValue(TIP_DIRECTION) == Direction.UP && state.getValue(THICKNESS) == DripstoneThickness.TIP) {
            entity.causeFallDamage(fallDistance + 2.5F, 2.0F, level.damageSources().stalagmite());
        } else {
            super.fallOn(level, state, pos, entity, fallDistance);
        }
    }

    /** Resolve seepage through the whole hanging column, never inventing water in a dry cave. */
    public Fluid getDripFluid(LevelReader level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!state.is(this) || state.getValue(TIP_DIRECTION) != Direction.DOWN || state.getValue(WATERLOGGED))
            return Fluids.EMPTY;
        for (int distance = 1; distance <= 11; distance++) {
            var above = pos.above(distance);
            var next = level.getBlockState(above);
            if (next.is(this) && next.getValue(TIP_DIRECTION) == Direction.DOWN) continue;
            if (next.is(this) || !next.isFaceSturdy(level, above, Direction.DOWN)) break;
            var fluid = level.getFluidState(above.above());
            if (fluid.is(ModFluids.SALT_WATER.get()) && fluid.isSource()) return ModFluids.SALT_WATER.get();
            break;
        }
        return Fluids.EMPTY;
    }

    @Override public void animateTick(BlockState state,
                                      @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!(state.getValue(TIP_DIRECTION) == Direction.DOWN && state.getValue(THICKNESS) == DripstoneThickness.TIP && !state.getValue(WATERLOGGED)) || random.nextFloat() > .12F || getDripFluid(level, pos) != ModFluids.SALT_WATER.get()) return;
        var offset = state.getOffset(level, pos);
        level.addParticle(ModParticles.DRIPPING_SALT_WATER.get(),
                pos.getX() + .5 + offset.x, pos.getY() + .25, pos.getZ() + .5 + offset.z, 0, 0, 0);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (isStalagmite(state) && !this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        } else {
            spawnFallingStalactite(state, level, pos);
        }
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (getDripFluid(level, pos) != ModFluids.SALT_WATER.get()) return;
        maybeTransferFluid(state, level, pos, random.nextFloat());
        if (random.nextFloat() < 0.011377778F && isStalactiteStartPos(state, level, pos)) {
            growStalactiteOrStalagmiteIfPossible(state, level, pos, random);
        }
    }

    @VisibleForTesting
    public static void maybeTransferFluid(BlockState state, ServerLevel level, BlockPos pos, float randomValue) {
        // Salt spikes transfer vanilla fluids and registered brine.
        if (isStalactiteStartPos(state, level, pos)) {
            Optional<SaltSpikeBlock.FluidInfo> fluidInfo = getFluidAboveStalactite(level, pos, state);
            if (!fluidInfo.isEmpty()) {
                Fluid fluid = fluidInfo.get().fluid;
                de.artemis.floraexpansion.common.registry.ModFluids.DripstoneDripInfo dripInfo = de.artemis.floraexpansion.common.registry.ModFluids.getDripInfo(fluid);
                if (dripInfo != null && !(randomValue >= dripInfo.chance())) {
                    BlockPos stalactiteTipPos = findTip(state, level, pos, 11, false);
                    if (stalactiteTipPos != null) {
                        if (fluidInfo.get().sourceState.is(Blocks.MUD) && fluid == Fluids.WATER) {
                            BlockState newState = Blocks.CLAY.defaultBlockState();
                            level.setBlockAndUpdate(fluidInfo.get().pos, newState);
                            Block.pushEntitiesUp(fluidInfo.get().sourceState, newState, level, fluidInfo.get().pos);
                            level.gameEvent(GameEvent.BLOCK_CHANGE, fluidInfo.get().pos, GameEvent.Context.of(newState));
                            level.levelEvent(1504, stalactiteTipPos, 0);
                        } else {
                            BlockPos cauldronPos = findFillableCauldronBelowStalactiteTip(level, stalactiteTipPos, fluid);
                            if (cauldronPos != null) {
                                level.levelEvent(1504, stalactiteTipPos, 0);
                                int fallDistance = stalactiteTipPos.getY() - cauldronPos.getY();
                                int delay = 50 + fallDistance;
                                BlockState cauldronState = level.getBlockState(cauldronPos);
                                level.scheduleTick(cauldronPos, cauldronState.getBlock(), delay);
                            }
                        }
                    }
                }
            }
        }

    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction defaultTipDirection = context.getNearestLookingVerticalDirection().getOpposite();
        Direction tipDirection = calculateTipDirection(level, pos, defaultTipDirection);
        if (tipDirection == null) {
            return null;
        } else {
            boolean mergeOpposingTips = !context.isSecondaryUseActive();
            DripstoneThickness thickness = calculateDripstoneThickness(level, pos, tipDirection, mergeOpposingTips);
            return this.defaultBlockState()
                .setValue(TIP_DIRECTION, tipDirection)
                .setValue(THICKNESS, thickness)
                .setValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
        }
    }

    @Override
    @NotNull protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @NotNull protected VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        VoxelShape shape = switch ((DripstoneThickness)state.getValue(THICKNESS)) {
            case TIP_MERGE -> SHAPE_TIP_MERGE;
            case TIP -> state.getValue(TIP_DIRECTION) == Direction.DOWN ? SHAPE_TIP_DOWN : SHAPE_TIP_UP;
            case FRUSTUM -> SHAPE_FRUSTUM;
            case MIDDLE -> SHAPE_MIDDLE;
            case BASE -> SHAPE_BASE;
        };
        var offset = state.getOffset(level, pos);
        return shape.move(offset.x, offset.y, offset.z);
    }

    @Override
    protected boolean isCollisionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    protected float getMaxHorizontalOffset() {
        return MAX_HORIZONTAL_OFFSET;
    }

    @Override
    public void onBrokenAfterFall(@NotNull Level level, @NotNull BlockPos pos, FallingBlockEntity entity) {
        if (!entity.isSilent()) {
            level.levelEvent(1045, pos, 0);
        }
    }

    @Override
    @NotNull public DamageSource getFallDamageSource(Entity entity) {
        return entity.damageSources().fallingStalactite(entity);
    }

    private static void spawnFallingStalactite(BlockState state, ServerLevel level, BlockPos pos) {
        BlockPos.MutableBlockPos fallPos = pos.mutable();
        BlockState fallState = state;

        while (isStalactite(fallState)) {
            FallingBlockEntity entity = FallingBlockEntity.fall(level, fallPos, fallState);
            if (isTip(fallState, true)) {
                int size = Math.max(1 + pos.getY() - fallPos.getY(), 6);
                float damagePerFallDistance = 1.0F * size;
                entity.setHurtsEntities(damagePerFallDistance, 40);
                break;
            }

            fallPos.move(Direction.DOWN);
            fallState = level.getBlockState(fallPos);
        }
    }

    @VisibleForTesting
    public static void growStalactiteOrStalagmiteIfPossible(
        BlockState stalactiteStartState, ServerLevel level, BlockPos stalactiteStartPos, RandomSource random
    ) {
        BlockState rootState = level.getBlockState(stalactiteStartPos.above(1));
        BlockState stateAbove = level.getBlockState(stalactiteStartPos.above(2));
        if (canGrow(rootState, stateAbove)) {
            BlockPos stalactiteTipPos = findTip(stalactiteStartState, level, stalactiteStartPos, 7, false);
            if (stalactiteTipPos != null) {
                BlockState stalactiteTipState = level.getBlockState(stalactiteTipPos);
                if (canDrip(stalactiteTipState) && canTipGrow(stalactiteTipState, level, stalactiteTipPos)) {
                    if (random.nextBoolean()) {
                        grow(level, stalactiteTipPos, Direction.DOWN);
                    } else {
                        growStalagmiteBelow(level, stalactiteTipPos);
                    }
                }
            }
        }
    }

    private static void growStalagmiteBelow(ServerLevel level, BlockPos posAboveStalagmite) {
        BlockPos.MutableBlockPos pos = posAboveStalagmite.mutable();

        for (int i = 0; i < 10; i++) {
            pos.move(Direction.DOWN);
            BlockState state = level.getBlockState(pos);
            if (!state.getFluidState().isEmpty()) {
                return;
            }

            if (isUnmergedTipWithDirection(state, Direction.UP) && canTipGrow(state, level, pos)) {
                grow(level, pos, Direction.UP);
                return;
            }

            if (isValidPointedDripstonePlacement(level, pos, Direction.UP) && !level.isWaterAt(pos.below())) {
                grow(level, pos.below(), Direction.UP);
                return;
            }

            if (!canDripThrough(level, pos, state)) {
                return;
            }
        }
    }

    private static void grow(ServerLevel level, BlockPos growFromPos, Direction growToDirection) {
        BlockPos targetPos = growFromPos.relative(growToDirection);
        BlockState existingStateAtTargetPos = level.getBlockState(targetPos);
        if (isUnmergedTipWithDirection(existingStateAtTargetPos, growToDirection.getOpposite())) {
            createMergedTips(existingStateAtTargetPos, level, targetPos);
        } else if (existingStateAtTargetPos.isAir() || existingStateAtTargetPos.is(Blocks.WATER)) {
            createDripstone(level, targetPos, growToDirection, DripstoneThickness.TIP);
        }
    }

    private static void createDripstone(LevelAccessor level, BlockPos pos, Direction direction, DripstoneThickness thickness) {
        BlockState state = ModBlocks.SALT_SPIKE.get()
            .defaultBlockState()
            .setValue(TIP_DIRECTION, direction)
            .setValue(THICKNESS, thickness)
            .setValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
        level.setBlock(pos, state, 3);
    }

    private static void createMergedTips(BlockState tipState, LevelAccessor level, BlockPos tipPos) {
        BlockPos stalactitePos;
        BlockPos stalagmitePos;
        if (tipState.getValue(TIP_DIRECTION) == Direction.UP) {
            stalagmitePos = tipPos;
            stalactitePos = tipPos.above();
        } else {
            stalactitePos = tipPos;
            stalagmitePos = tipPos.below();
        }

        createDripstone(level, stalactitePos, Direction.DOWN, DripstoneThickness.TIP_MERGE);
        createDripstone(level, stalagmitePos, Direction.UP, DripstoneThickness.TIP_MERGE);
    }

    public static void spawnDripParticle(Level level, BlockPos stalactiteTipPos, BlockState stalactiteTipState) {
        getFluidAboveStalactite(level, stalactiteTipPos, stalactiteTipState)
            .ifPresent(fluidAbove -> spawnDripParticle(level, stalactiteTipPos, stalactiteTipState, fluidAbove.fluid, fluidAbove.pos));
    }

    private static void spawnDripParticle(Level level, BlockPos stalactiteTipPos, BlockState stalactiteTipState, Fluid fluidAbove, BlockPos posAbove) {
        Vec3 offset = stalactiteTipState.getOffset(level, stalactiteTipPos);
        double x = stalactiteTipPos.getX() + 0.5 + offset.x;
        double y = stalactiteTipPos.getY() + STALACTITE_DRIP_START_PIXEL - 0.0625;
        double z = stalactiteTipPos.getZ() + 0.5 + offset.z;
        ParticleOptions dripParticle = getDripParticle(level, fluidAbove, posAbove);
        level.addParticle(dripParticle, x, y, z, 0.0, 0.0, 0.0);
    }

    private static @Nullable BlockPos findTip(
        BlockState dripstoneState, LevelAccessor level, BlockPos dripstonePos, int maxSearchLength, boolean includeMergedTip
    ) {
        if (isTip(dripstoneState, includeMergedTip)) {
            return dripstonePos;
        } else {
            Direction searchDirection = dripstoneState.getValue(TIP_DIRECTION);
            BiPredicate<BlockPos, BlockState> pathPredicate = (pos, state) -> state.is(ModBlocks.SALT_SPIKE.get())
                && state.getValue(TIP_DIRECTION) == searchDirection;
            return findBlockVertical(
                    level, dripstonePos, searchDirection.getAxisDirection(), pathPredicate, dripstone -> isTip(dripstone, includeMergedTip), maxSearchLength
                )
                .orElse(null);
        }
    }

    private static @Nullable Direction calculateTipDirection(LevelReader level, BlockPos pos, Direction defaultTipDirection) {
        Direction tipDirection;
        if (isValidPointedDripstonePlacement(level, pos, defaultTipDirection)) {
            tipDirection = defaultTipDirection;
        } else {
            if (!isValidPointedDripstonePlacement(level, pos, defaultTipDirection.getOpposite())) {
                return null;
            }

            tipDirection = defaultTipDirection.getOpposite();
        }

        return tipDirection;
    }

    private static DripstoneThickness calculateDripstoneThickness(LevelReader level, BlockPos pos, Direction tipDirection, boolean mergeOpposingTips) {
        Direction baseDirection = tipDirection.getOpposite();
        BlockState inFrontState = level.getBlockState(pos.relative(tipDirection));
        if (isPointedDripstoneWithDirection(inFrontState, baseDirection)) {
            return !mergeOpposingTips && inFrontState.getValue(THICKNESS) != DripstoneThickness.TIP_MERGE
                ? DripstoneThickness.TIP
                : DripstoneThickness.TIP_MERGE;
        } else if (!isPointedDripstoneWithDirection(inFrontState, tipDirection)) {
            return DripstoneThickness.TIP;
        } else {
            DripstoneThickness inFrontThickness = inFrontState.getValue(THICKNESS);
            if (inFrontThickness != DripstoneThickness.TIP && inFrontThickness != DripstoneThickness.TIP_MERGE) {
                BlockState behindState = level.getBlockState(pos.relative(baseDirection));
                return !isPointedDripstoneWithDirection(behindState, tipDirection) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
            } else {
                return DripstoneThickness.FRUSTUM;
            }
        }
    }

    public static boolean canDrip(BlockState state) {
        return isStalactite(state) && state.getValue(THICKNESS) == DripstoneThickness.TIP && !state.getValue(WATERLOGGED);
    }

    private static boolean canTipGrow(BlockState tipState, ServerLevel level, BlockPos tipPos) {
        Direction growDirection = tipState.getValue(TIP_DIRECTION);
        BlockPos growPos = tipPos.relative(growDirection);
        BlockState stateAtGrowPos = level.getBlockState(growPos);
        if (!stateAtGrowPos.getFluidState().isEmpty()) {
            return false;
        } else {
            return stateAtGrowPos.isAir() ? true : isUnmergedTipWithDirection(stateAtGrowPos, growDirection.getOpposite());
        }
    }

    private static Optional<BlockPos> findRootBlock(Level level, BlockPos pos, BlockState dripStoneState, int maxSearchLength) {
        Direction tipDirection = dripStoneState.getValue(TIP_DIRECTION);
        BiPredicate<BlockPos, BlockState> pathPredicate = (pathPos, state) -> state.is(ModBlocks.SALT_SPIKE.get())
            && state.getValue(TIP_DIRECTION) == tipDirection;
        return findBlockVertical(
            level, pos, tipDirection.getOpposite().getAxisDirection(), pathPredicate, state -> !state.is(ModBlocks.SALT_SPIKE.get()), maxSearchLength
        );
    }

    private static boolean isValidPointedDripstonePlacement(LevelReader level, BlockPos pos, Direction tipDirection) {
        BlockPos behindPos = pos.relative(tipDirection.getOpposite());
        BlockState behindState = level.getBlockState(behindPos);
        return behindState.isFaceSturdy(level, behindPos, tipDirection) || isPointedDripstoneWithDirection(behindState, tipDirection);
    }

    private static boolean isTip(BlockState state, boolean includeMergedTip) {
        if (!state.is(ModBlocks.SALT_SPIKE.get())) {
            return false;
        } else {
            DripstoneThickness thickness = state.getValue(THICKNESS);
            return thickness == DripstoneThickness.TIP || includeMergedTip && thickness == DripstoneThickness.TIP_MERGE;
        }
    }

    private static boolean isUnmergedTipWithDirection(BlockState state, Direction tipDirection) {
        return isTip(state, false) && state.getValue(TIP_DIRECTION) == tipDirection;
    }

    private static boolean isStalactite(BlockState state) {
        return isPointedDripstoneWithDirection(state, Direction.DOWN);
    }

    private static boolean isStalagmite(BlockState state) {
        return isPointedDripstoneWithDirection(state, Direction.UP);
    }

    private static boolean isStalactiteStartPos(BlockState state, LevelReader level, BlockPos pos) {
        return isStalactite(state) && !level.getBlockState(pos.above()).is(ModBlocks.SALT_SPIKE.get());
    }

    @Override
    protected boolean isPathfindable(@NotNull BlockState state, @NotNull PathComputationType type) {
        return false;
    }

    private static boolean isPointedDripstoneWithDirection(BlockState blockState, Direction tipDirection) {
        return blockState.is(ModBlocks.SALT_SPIKE.get()) && blockState.getValue(TIP_DIRECTION) == tipDirection;
    }

    private static @Nullable BlockPos findFillableCauldronBelowStalactiteTip(Level level, BlockPos stalactiteTipPos, Fluid fluid) {
        Predicate<BlockState> cauldronPredicate = state -> state.is(Blocks.CAULDRON) || state.is(ModBlocks.SALT_WATER_CAULDRON.get())
            && state.getValue(LayeredCauldronBlock.LEVEL) < 3;
        BiPredicate<BlockPos, BlockState> pathPredicate = (pos, state) -> canDripThrough(level, pos, state);
        return findBlockVertical(level, stalactiteTipPos, Direction.DOWN.getAxisDirection(), pathPredicate, cauldronPredicate, 11).orElse(null);
    }

    public static @Nullable BlockPos findStalactiteTipAboveCauldron(Level level, BlockPos cauldronPos) {
        BiPredicate<BlockPos, BlockState> pathPredicate = (pos, state) -> canDripThrough(level, pos, state);
        return findBlockVertical(level, cauldronPos, Direction.UP.getAxisDirection(), pathPredicate, SaltSpikeBlock::canDrip, 11).orElse(null);
    }

    public static Fluid getCauldronFillFluidType(ServerLevel level, BlockPos stalactitePos) {
        return getFluidAboveStalactite(level, stalactitePos, level.getBlockState(stalactitePos))
            .map(fluidSource -> fluidSource.fluid)
            .filter(SaltSpikeBlock::canFillCauldron)
            .orElse(Fluids.EMPTY);
    }

    private static Optional<FluidInfo> getFluidAboveStalactite(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof SaltSpikeBlock spike) || spike.getDripFluid(level, pos) != ModFluids.SALT_WATER.get()) return Optional.empty();
        return findRootBlock(level, pos, state, 11).map(root -> new FluidInfo(root.above(), ModFluids.SALT_WATER.get(), level.getBlockState(root.above())));
    }

    private static boolean canFillCauldron(Fluid fluidAbove) {
        return de.artemis.floraexpansion.common.registry.ModFluids.getDripInfo(fluidAbove) != null;
    }

    private static boolean canGrow(BlockState rootState, BlockState aboveState) {
        FluidState fluidState = aboveState.getFluidState();
        return rootState.is(ModBlocks.CRYSTALLINE_SALT.get()) && fluidState.is(ModFluids.SALT_WATER.get()) && fluidState.isSource();
    }

    private static ParticleOptions getDripParticle(Level level, Fluid fluidAbove, BlockPos posAbove) {
        if (fluidAbove.isSame(Fluids.EMPTY)) {
            return (level.dimensionType().ultraWarm() ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER);
        } else {
            ParticleOptions options = de.artemis.floraexpansion.common.registry.ModFluids.getDripInfo(fluidAbove) != null ? de.artemis.floraexpansion.common.registry.ModFluids.getDripInfo(fluidAbove).dripParticle() : ParticleTypes.DRIPPING_DRIPSTONE_WATER;
            if (options == null) options = (level.dimensionType().ultraWarm() ? ParticleTypes.DRIPPING_DRIPSTONE_LAVA : ParticleTypes.DRIPPING_DRIPSTONE_WATER);
            return options;
        }
    }

    private static Optional<BlockPos> findBlockVertical(
        LevelAccessor level,
        BlockPos pos,
        Direction.AxisDirection axisDirection,
        BiPredicate<BlockPos, BlockState> pathPredicate,
        Predicate<BlockState> targetPredicate,
        int maxSteps
    ) {
        Direction direction = Direction.get(axisDirection, Direction.Axis.Y);
        BlockPos.MutableBlockPos mutablePos = pos.mutable();

        for (int i = 1; i < maxSteps; i++) {
            mutablePos.move(direction);
            BlockState state = level.getBlockState(mutablePos);
            if (targetPredicate.test(state)) {
                return Optional.of(mutablePos.immutable());
            }

            if (level.isOutsideBuildHeight(mutablePos.getY()) || !pathPredicate.test(mutablePos, state)) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private static boolean canDripThrough(BlockGetter level, BlockPos pos, BlockState state) {
        if (state.isAir()) {
            return true;
        } else if (state.isSolidRender(level, pos)) {
            return false;
        } else if (!state.getFluidState().isEmpty()) {
            return false;
        } else {
            VoxelShape collisionShape = state.getCollisionShape(level, pos);
            return !Shapes.joinIsNotEmpty(REQUIRED_SPACE_TO_DRIP_THROUGH_NON_SOLID_BLOCK, collisionShape, BooleanOp.AND);
        }
    }

    record FluidInfo(BlockPos pos, Fluid fluid, BlockState sourceState) {
    }
}
