package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModParticles;
import de.artemis.floraexpansion.common.util.FruitingLeavesHelper;
import de.artemis.floraexpansion.common.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CherryLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class FruitingCherryLeavesBlock extends CherryLeavesBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = 3;

    public FruitingCherryLeavesBlock(Properties properties) {
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
        return FruitingLeavesHelper.isRandomlyTicking(super.isRandomlyTicking(blockState), blockState, AGE, MAX_AGE);
    }

    @Override
    protected void randomTick(@NotNull BlockState blockState, @NotNull ServerLevel level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        super.randomTick(blockState, level, blockPos, random);

        FruitingLeavesHelper.growIfAble(blockState, level, blockPos, random, AGE, MAX_AGE, 8);
    }

    @Override
    public void animateTick(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull RandomSource random) {
        super.animateTick(blockState, level, blockPos, random);

        FruitingLeavesHelper.animateFruit(blockState, level, blockPos, random, AGE,
                age -> age != 2, ModParticles.FALLING_CHERRY.get());
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull BlockHitResult result) {
        int age = blockState.getValue(AGE);

        // Stage 0 = normal leaves, nothing to harvest
        if (age <= 0) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide) {
            int amount = switch (age) {
                case 1 -> 1;
                case 2 -> 2 + level.random.nextInt(2); // 2-3
                case 3 -> 3 + level.random.nextInt(2); // 3-4
                default -> 0;
            };

            if (amount > 0) {
                spawnHarvestedCherries(level, blockPos, result, amount);
            }

            FruitingLeavesHelper.resetFruitAge(level, blockPos, blockState, AGE);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void spawnHarvestedCherries(Level level, BlockPos blockPos, BlockHitResult hitResult, int amount) {
        ModUtils.spawnItemAtClickedSide(level, blockPos, hitResult, new ItemStack(ModItems.CHERRIES.get(), amount));
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader levelReader, @NotNull BlockPos blockPos, BlockState blockState) {
        return FruitingLeavesHelper.canGrow(blockState, AGE, MAX_AGE);
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos blockPos, BlockState blockState) {
        return FruitingLeavesHelper.canGrow(blockState, AGE, MAX_AGE);
    }

    @Override
    public void performBonemeal(ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos blockPos, BlockState blockState) {
        FruitingLeavesHelper.applyBonemeal(level, blockPos, blockState, AGE, MAX_AGE);
    }
}

