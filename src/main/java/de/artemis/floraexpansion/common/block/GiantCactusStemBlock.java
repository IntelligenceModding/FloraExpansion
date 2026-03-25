package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class GiantCactusStemBlock extends Block {
    public static final MapCodec<GiantCactusStemBlock> CODEC = simpleCodec(GiantCactusStemBlock::new);
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public GiantCactusStemBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        // Like vanilla cactus flower behavior:
        // once there is already a flower on top, no more growth.
        BlockPos abovePos = BlockPos.ZERO.above(); // not used directly; just keeping logic grouped below
        return true;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void entityInside(@NotNull BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        entity.hurt(level.damageSources().cactus(), 1.0F);
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state,
                                              @NotNull Direction direction,
                                              @NotNull BlockState neighborState,
                                              @NotNull LevelAccessor level,
                                              @NotNull BlockPos pos,
                                              @NotNull BlockPos neighborPos) {
        if (!state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        // Find the top of this giant cactus column
        BlockPos top = pos;
        while (level.getBlockState(top.above()).is(ModBlocks.GIANT_CACTUS_STEM.get())) {
            top = top.above();
        }

        BlockPos flowerPos = top.above();

        // If flower already exists, stop further growth
        if (level.getBlockState(flowerPos).is(ModBlocks.CACTUS_FLOWER.get())) {
            return;
        }

        // Must be empty above to do anything
        if (!level.isEmptyBlock(flowerPos)) {
            return;
        }

        // Count total cactus height (base + stem)
        int totalHeight = 0;
        BlockPos cursor = top;
        while (true) {
            BlockState check = level.getBlockState(cursor);
            if (check.is(ModBlocks.GIANT_CACTUS_STEM.get()) || check.is(ModBlocks.GIANT_CACTUS_BASE.get())) {
                totalHeight++;
                cursor = cursor.below();
            } else {
                break;
            }
        }

        // Require clear horizontal neighbors around flower position, like vanilla cactus flower rules
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (!level.isEmptyBlock(flowerPos.relative(dir))) {
                return;
            }
        }

        // Vanilla-like split:
        // shorter cactus: low flower chance
        // taller cactus: higher flower chance
        float flowerChance = totalHeight >= 3 ? 0.25F : 0.10F;

        if (random.nextFloat() < flowerChance) {
            level.setBlock(flowerPos, ModBlocks.CACTUS_FLOWER.get().defaultBlockState(), Block.UPDATE_ALL);
            return;
        }

        // Otherwise grow taller, but stop at a sane cap for your giant cactus
        if (totalHeight < 6) {
            level.setBlock(flowerPos, ModBlocks.GIANT_CACTUS_STEM.get().defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(ModBlocks.GIANT_CACTUS_BASE.get())
                || below.is(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                || below.is(ModBlocks.GIANT_CACTUS_WOOD.get())
                || below.is(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get())
                || below.is(ModBlocks.GIANT_CACTUS_STEM.get());
    }
}