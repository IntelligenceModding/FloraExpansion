package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModParticles;
import de.artemis.floraexpansion.common.util.FruitingLeavesHelper;
import de.artemis.floraexpansion.common.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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
import org.jetbrains.annotations.NotNull;

public class FruitingOakLeavesBlock extends LeavesBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    public static final int MAX_AGE = 2;

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
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        return FruitingLeavesHelper.isRandomlyTicking(super.isRandomlyTicking(state), state, AGE, MAX_AGE);
    }

    @Override
    protected void randomTick(@NotNull BlockState blockState, @NotNull ServerLevel level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        super.randomTick(blockState, level, blockPos, random);

        FruitingLeavesHelper.growIfAble(blockState, level, blockPos, random, AGE, MAX_AGE, 24);
    }

    @Override
    public void animateTick(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        super.animateTick(blockState, level, blockPos, random);

        FruitingLeavesHelper.animateFruit(blockState, level, blockPos, random, AGE,
                age -> age > 0, ModParticles.FALLING_APPLE.get());
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult result) {
        if (state.getValue(AGE) < 2) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            spawnHarvestedApple(level, pos, result);
            FruitingLeavesHelper.resetFruitAge(level, pos, state, AGE);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void spawnHarvestedApple(Level level, BlockPos pos, BlockHitResult hitResult) {
        ModUtils.spawnItemAtClickedSide(level, pos, hitResult, new ItemStack(Items.APPLE, 1));
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader levelReader, @NotNull BlockPos pos, BlockState state) {
        return FruitingLeavesHelper.canGrow(state, AGE, MAX_AGE);
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, BlockState state) {
        return FruitingLeavesHelper.canGrow(state, AGE, MAX_AGE);
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, BlockState state) {
        FruitingLeavesHelper.applyBonemeal(level, pos, state, AGE, MAX_AGE);
    }
}

