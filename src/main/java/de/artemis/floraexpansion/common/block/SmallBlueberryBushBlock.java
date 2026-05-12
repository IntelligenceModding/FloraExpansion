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
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class SmallBlueberryBushBlock extends SweetBerryBushBlock {
    public static final MapCodec<SweetBerryBushBlock> CODEC = simpleCodec(SmallBlueberryBushBlock::new);
    protected static final int ROOTED_BUSH_GROWTH_CHANCE = 33;

    public SmallBlueberryBushBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<SweetBerryBushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull net.minecraft.world.entity.Entity entity) {
        // Blueberry bushes are a forage plant, not a hazard.
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull net.minecraft.world.level.LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(ModItems.BLUEBERRIES.get());
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.is(BlockTags.DIRT)
                || state.is(Blocks.FARMLAND)
                || state.is(Blocks.MOSS_BLOCK)
                || state.is(Blocks.ROOTED_DIRT);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 3 && random.nextInt(5) == 0 && level.getRawBrightness(pos.above(), 0) >= 9) {
            BlockState grownState = getGrowthState(level, pos, state, random, age + 1);
            level.setBlock(pos, grownState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grownState));
        }
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        int nextAge = Math.min(3, state.getValue(AGE) + 1);
        BlockState grownState = getGrowthState(level, pos, state, random, nextAge);
        level.setBlock(pos, grownState, 2);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grownState));
    }

    protected @NotNull BlockState getGrowthState(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState currentState, @NotNull RandomSource random, int nextAge) {
        if (shouldBecomeRootedBush(currentState, nextAge, random)) {
            level.setBlock(pos.below(), Blocks.ROOTED_DIRT.defaultBlockState(), 2);
            return ModBlocks.LARGE_BLUEBERRY_BUSH.get().defaultBlockState().setValue(LargeBlueberryBushBlock.AGE, nextAge);
        }

        return currentState.setValue(AGE, nextAge);
    }

    protected boolean shouldBecomeRootedBush(@NotNull BlockState currentState, int nextAge, @NotNull RandomSource random) {
        int currentAge = currentState.getValue(AGE);
        boolean advancingIntoLeafyStage = (currentAge == 0 && nextAge == 1) || (currentAge == 1 && nextAge == 2);

        return advancingIntoLeafyStage
                && currentState.is(ModBlocks.BLUEBERRY_BUSH.get())
                && random.nextInt(ROOTED_BUSH_GROWTH_CHANCE) == 0;
    }

    protected int getHarvestAmount(@NotNull RandomSource random) {
        return 1 + random.nextInt(2);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age < 3 && stack.is(Items.BONE_MEAL)) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age < 3) {
            return InteractionResult.PASS;
        }

        int amount = getHarvestAmount(level.random);
        if (!level.isClientSide) {
            popResource(level, pos, new ItemStack(ModItems.BLUEBERRIES.get(), amount));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F,
                    0.8F + level.random.nextFloat() * 0.4F);

            BlockState resetState = state.setValue(AGE, 2);
            level.setBlock(pos, resetState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, resetState));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
