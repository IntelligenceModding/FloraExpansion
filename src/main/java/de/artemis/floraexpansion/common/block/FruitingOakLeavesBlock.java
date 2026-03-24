package de.artemis.floraexpansion.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FruitingOakLeavesBlock extends LeavesBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_1;
    public static final int MAX_AGE = 1;

    public FruitingOakLeavesBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState blockState) {
        return super.isRandomlyTicking(blockState) || blockState.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(@NotNull BlockState blockState, @NotNull ServerLevel level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        super.randomTick(blockState, level, blockPos, random);

        if (blockState.getValue(AGE) < MAX_AGE && random.nextInt(24) == 0) {
            level.setBlock(blockPos, blockState.setValue(AGE, 1), 2);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult result) {
        if (blockState.getValue(AGE) <= 0) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            spawnHarvestedApple(level, blockPos, result);
            level.setBlock(blockPos, blockState.setValue(AGE, 0), 2);
            level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F,
                    0.8F + level.random.nextFloat() * 0.4F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void spawnHarvestedApple(Level level, BlockPos pos, BlockHitResult hitResult) {
        Vec3 center = Vec3.atCenterOf(pos);
        Direction face = hitResult.getDirection();

        double offset = 0.55D;
        double spawnX = center.x + face.getStepX() * offset;
        double spawnY = center.y + face.getStepY() * offset;
        double spawnZ = center.z + face.getStepZ() * offset;

        ItemEntity itemEntity = new ItemEntity(
                level,
                spawnX,
                spawnY,
                spawnZ,
                new ItemStack(Items.APPLE, 1)
        );

        double motion = 0.12D;
        itemEntity.setDeltaMovement(
                face.getStepX() * motion,
                face == Direction.UP ? 0.08D : face == Direction.DOWN ? -0.02D : 0.04D,
                face.getStepZ() * motion
        );

        level.addFreshEntity(itemEntity);
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader levelReader, @NotNull BlockPos blockPos, BlockState blockState) {
        return blockState.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos blockPos, BlockState blockState) {
        return blockState.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos blockPos, BlockState blockState) {
        level.setBlock(blockPos, blockState.setValue(AGE, 1), 2);
    }
}