package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CactusThornBlock extends FaceAttachedHorizontalDirectionalBlock {
    public static final MapCodec<CactusThornBlock> CODEC = simpleCodec(CactusThornBlock::new);

    private static final VoxelShape FLOOR_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D);
    private static final VoxelShape CEILING_SHAPE = Block.box(2.0D, 13.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final VoxelShape NORTH_SHAPE = Block.box(2.0D, 2.0D, 0.0D, 14.0D, 14.0D, 3.0D);
    private static final VoxelShape SOUTH_SHAPE = Block.box(2.0D, 2.0D, 13.0D, 14.0D, 14.0D, 16.0D);
    private static final VoxelShape WEST_SHAPE = Block.box(0.0D, 2.0D, 2.0D, 3.0D, 14.0D, 14.0D);
    private static final VoxelShape EAST_SHAPE = Block.box(13.0D, 2.0D, 2.0D, 16.0D, 14.0D, 14.0D);

    public CactusThornBlock(Properties properties) {
        super(properties.noCollision());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACE, AttachFace.WALL)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, @NotNull BlockState> builder) {
        builder.add(FACE, FACING);
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACE)) {
            case FLOOR -> FLOOR_SHAPE;
            case CEILING -> CEILING_SHAPE;
            case WALL -> switch (state.getValue(FACING)) {
                case NORTH -> SOUTH_SHAPE;
                case SOUTH -> NORTH_SHAPE;
                case WEST -> EAST_SHAPE;
                case EAST -> WEST_SHAPE;
                case UP, DOWN -> SOUTH_SHAPE;
            };
        };
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected void entityInside(@NotNull BlockState state,
                                @NotNull Level level,
                                @NotNull BlockPos pos,
                                @NotNull Entity entity,
                                @NotNull InsideBlockEffectApplier applier,
                                boolean intersects) {
        if (level instanceof ServerLevel serverLevel && entity instanceof LivingEntity livingEntity) {
            livingEntity.hurtServer(serverLevel, serverLevel.damageSources().cactus(), 1.0F);
        }
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state,
                                              @NotNull LevelReader level,
                                              @NotNull ScheduledTickAccess scheduledTickAccess,
                                              @NotNull BlockPos pos,
                                              @NotNull Direction direction,
                                              @NotNull BlockPos neighborPos,
                                              @NotNull BlockState neighborState,
                                              @NotNull RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            scheduledTickAccess.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        Direction supportSide = getConnectedDirection(state).getOpposite();
        BlockPos supportPos = pos.relative(supportSide);
        BlockState supportState = level.getBlockState(supportPos);
        return supportState.isFaceSturdy(level, supportPos, getConnectedDirection(state));
    }
}