package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.util.PlantSoils;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.config.ServerConfig;
import de.artemis.floraexpansion.common.util.PlantGrowth;

public class LargeBlueberryBushBlock extends BushBlock implements BonemealableBlock, HarvestableBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return useWithoutItem(state, level, pos, player, hit);
    }



    public static final IntegerProperty AGE = SmallBlueberryBushBlock.AGE;
    public static final int MAX_AGE = 3;
    private static final int HARVESTABLE_AGE = 2;
    private static final int RESET_AGE = 1;
    private static final VoxelShape[] SHAPES_BY_AGE = new VoxelShape[]{
            Block.box(4.0D, 0.0D, 4.0D, 12.0D, 6.0D, 12.0D),
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D),
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D),
            Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D)
    };

    public LargeBlueberryBushBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull net.minecraft.world.level.BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(ModBlocks.LARGE_BLUEBERRY_BUSH.get());
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES_BY_AGE[state.getValue(AGE)];
    }

    @Override
    public boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return PlantSoils.supportsBlueberry(state);
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE && PlantGrowth.canGrow(level, pos, state, random, 5, ServerConfig.berryGrowth.get()) && level.getRawBrightness(pos.above(), 0) >= 9) {
            BlockState grownState = state.setValue(AGE, age + 1);
            level.setBlock(pos, grownState, 2);
            PlantGrowth.grown(level, pos, state);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grownState));
        }
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClientSide) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        int nextAge = Math.min(MAX_AGE, state.getValue(AGE) + 1);
        BlockState grownState = state.setValue(AGE, nextAge);
        level.setBlock(pos, grownState, 2);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grownState));
    }

    @Override
    public boolean canHarvest(BlockState state) {
        return state.getValue(AGE) >= HARVESTABLE_AGE;
    }


    @Override
    public BlockState getHarvestedState(BlockState state) {
        return state.setValue(AGE, RESET_AGE);
    }

    public @NotNull net.minecraft.world.InteractionResult useWithItem(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (state.getValue(AGE) < MAX_AGE && stack.is(Items.BONE_MEAL)) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hitResult) {
        return harvest(state, level, pos, player);
    }
}
