package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.util.PlantGrowth;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.config.ServerConfig;
import de.artemis.floraexpansion.common.registry.ModTags;
import de.artemis.floraexpansion.common.loot.HarvestLoot;

public class GiantCactusStemBlock extends Block {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return super.use(state, level, pos, player, hand, hit);
    }

    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public GiantCactusStemBlock(Properties properties) {
        super(properties);
    }


    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                           @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                    @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Entity entity) {
        entity.hurt(level.damageSources().cactus(), 1.0F);
    }

    @Override
    public @NotNull BlockState updateShape(net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.core.Direction direction, @NotNull net.minecraft.world.level.block.state.BlockState neighborState, @NotNull net.minecraft.world.level.LevelAccessor level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.core.BlockPos neighborPos) {
        var scheduledTickAccess = level;
        if (!state.canSurvive(level, pos)) {
            scheduledTickAccess.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                        @NotNull RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                              @NotNull RandomSource random) {
        double multiplier = ServerConfig.cactusGrowth.get();
        BlockPos top = pos;
        while (level.getBlockState(top.above()).is(ModBlocks.GIANT_CACTUS_STEM.get())) {
            top = top.above();
        }

        BlockPos flowerPos = top.above();

        if (level.getBlockState(flowerPos).is(ModBlocks.GIANT_CACTUS_SHOOT.get())) {
            return;
        }

        if (!level.isEmptyBlock(flowerPos)) {
            return;
        }

        if (!PlantGrowth.canGrow(level, pos, state, random, 1, multiplier)) return;
        int totalHeight = 0;
        BlockPos cursor = top;
        while (true) {
            BlockState check = level.getBlockState(cursor);
            if (check.is(ModBlocks.GIANT_CACTUS_STEM.get()) || check.is(ModBlocks.CACTUS_LOG.get())) {
                totalHeight++;
                cursor = cursor.below();
            } else {
                break;
            }
        }

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (!level.isEmptyBlock(flowerPos.relative(dir))) {
                return;
            }
        }

        float flowerChance = totalHeight >= 3 ? 0.25F : 0.10F;

        if (random.nextFloat() < Math.min(1, flowerChance * Math.max(1, multiplier))) {
            level.setBlock(
                    flowerPos,
                    GiantCactusShootBlock.withRandomVariant(ModBlocks.GIANT_CACTUS_SHOOT.get().defaultBlockState(), random),
                    Block.UPDATE_ALL
            );
            PlantGrowth.grown(level, pos, state);
            return;
        }

        if (totalHeight < 6) {
            level.setBlock(flowerPos, ModBlocks.GIANT_CACTUS_STEM.get().defaultBlockState(), Block.UPDATE_ALL);
            PlantGrowth.grown(level, pos, state);
        }
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(ModTags.Blocks.CACTUS_STEM_SUPPORTS);
    }

    public @NotNull net.minecraft.world.InteractionResult useWithItem(@NotNull ItemStack stack,
                                                   @NotNull BlockState state,
                                                   @NotNull Level level,
                                                   @NotNull BlockPos pos,
                                                   @NotNull Player player,
                                                   @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult hitResult) {
        if (!de.artemis.floraexpansion.common.util.CactusStripHelper.canStrip(stack)) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            int harvestedStemCount = harvestStemColumn(level, pos);

            if (harvestedStemCount > 0) {
                for (int i = 0; i < harvestedStemCount; i++) {
                    for (var drop : HarvestLoot.drops((ServerLevel) level, pos, state, player, stack))
                        Block.popResource(level, pos, drop);
                }
                level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

                de.artemis.floraexpansion.common.util.ItemData.damage(stack,
                        harvestedStemCount,
                        player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND
                );
            }
        }

        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    private int harvestStemColumn(Level level, BlockPos startPos) {
        int harvested = 0;

        BlockPos top = startPos;
        while (level.getBlockState(top.above()).is(ModBlocks.GIANT_CACTUS_STEM.get())) {
            top = top.above();
        }

        BlockPos flowerPos = top.above();
        if (level.getBlockState(flowerPos).is(ModBlocks.GIANT_CACTUS_SHOOT.get())) {
            level.destroyBlock(flowerPos, true);
        }

        for (BlockPos current = top; current.getY() >= startPos.getY(); current = current.below()) {
            if (!level.getBlockState(current).is(ModBlocks.GIANT_CACTUS_STEM.get())) {
                continue;
            }

            level.destroyBlock(current, false);
            harvested++;
        }

        return harvested;
    }
}
