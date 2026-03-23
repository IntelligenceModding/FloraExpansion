package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.item.ModItems;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CherryLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FruitingCherryLeavesBlock extends CherryLeavesBlock implements BonemealableBlock {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = 3;

    public FruitingCherryLeavesBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return super.isRandomlyTicking(state) || state.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        // Only regrow fruit here. Breaking/decay drops are handled by loot tables.
        int age = state.getValue(AGE);
        if (age < MAX_AGE && random.nextInt(8) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int age = state.getValue(AGE);

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
                spawnHarvestedCherries(level, pos, hitResult, amount);
            }

            level.setBlock(pos, state.setValue(AGE, 0), 2);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F,
                    0.8F + level.random.nextFloat() * 0.4F);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void spawnHarvestedCherries(Level level, BlockPos pos, BlockHitResult hitResult, int amount) {
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
                new ItemStack(ModItems.CHERRIES.get(), amount)
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
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return blockState.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int newAge = Math.min(MAX_AGE, state.getValue(AGE) + 1);
        level.setBlock(pos, state.setValue(AGE, newAge), 2);
    }
}