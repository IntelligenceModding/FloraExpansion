package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

public class LargeBlueberryBushBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<LargeBlueberryBushBlock> CODEC = simpleCodec(LargeBlueberryBushBlock::new);
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
    @SuppressWarnings("unchecked")
    public @NotNull MapCodec<BushBlock> codec() {
        return (MapCodec<BushBlock>) (MapCodec<?>) CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(AGE);
    }

    @Override
    protected @NotNull ItemStack getCloneItemStack(@NotNull net.minecraft.world.level.LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData) {
        return new ItemStack(ModBlocks.LARGE_BLUEBERRY_BUSH.get());
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES_BY_AGE[state.getValue(AGE)];
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.FARMLAND)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(Blocks.ROOTED_DIRT);
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int age = state.getValue(AGE);
        if (age < MAX_AGE && random.nextInt(5) == 0 && level.getRawBrightness(pos.above(), 0) >= 9) {
            BlockState grownState = state.setValue(AGE, age + 1);
            level.setBlock(pos, grownState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grownState));
        }
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
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
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (state.getValue(AGE) < MAX_AGE && stack.is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age < HARVESTABLE_AGE) {
            return InteractionResult.PASS;
        }

        int amount = 1 + level.getRandom().nextInt(2);
        if (age == MAX_AGE) {
            amount *= 2;
        }

        if (!level.isClientSide()) {
            popResource(level, pos, new ItemStack(ModItems.BLUEBERRIES.get(), amount));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F,
                    0.8F + level.getRandom().nextFloat() * 0.4F);

            BlockState resetState = state.setValue(AGE, RESET_AGE);
            level.setBlock(pos, resetState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, resetState));
        }

        return ((level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER));
    }
}
