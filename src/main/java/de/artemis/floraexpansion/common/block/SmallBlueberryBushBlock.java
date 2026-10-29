package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.util.PlantSoils;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.InsideBlockEffectApplier;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.config.ServerConfig;
import de.artemis.floraexpansion.common.util.PlantGrowth;

public class SmallBlueberryBushBlock extends SweetBerryBushBlock implements HarvestableBlock {
    protected static final int ROOTED_BUSH_GROWTH_CHANCE = 33;

    public SmallBlueberryBushBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull net.minecraft.world.entity.Entity entity, @NotNull InsideBlockEffectApplier effectApplier, boolean inside) {
    }

    @Override
    protected @NotNull ItemStack getCloneItemStack(@NotNull net.minecraft.world.level.LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean includeData) {
        return new ItemStack(ModItems.BLUEBERRIES.get());
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return PlantSoils.supportsBlueberry(state);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int age = state.getValue(AGE);
        if (age < 3 && PlantGrowth.canGrow(level, pos, state, random, 5, ServerConfig.berryGrowth.get()) && level.getRawBrightness(pos.above(), 0) >= 9) {
            BlockState grownState = getGrowthState(level, pos, state, random, age + 1);
            level.setBlock(pos, grownState, 2);
            PlantGrowth.grown(level, pos, state);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grownState));
        }
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state, net.minecraft.world.level.block.BonemealSource source) {
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

    @Override
    public boolean canHarvest(BlockState state) {
        return state.getValue(AGE) >= 3;
    }


    @Override
    public BlockState getHarvestedState(BlockState state) {
        return state.setValue(AGE, 2);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull BlockHitResult hitResult) {
        int age = state.getValue(AGE);
        if (age < 3 && stack.is(Items.BONE_MEAL)) {
            return InteractionResult.PASS;
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hitResult) {
        return harvest(state, level, pos, player);
    }
}
