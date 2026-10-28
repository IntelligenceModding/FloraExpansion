package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.util.ModUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.FallingParticlesLeavesBlock;
import net.minecraft.world.level.block.sounds.AmbientLeavesBlockSoundPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import de.artemis.floraexpansion.common.particle.FallingFruitParticle;
import org.jetbrains.annotations.NotNull;

/** Leaves that retain vanilla decay behavior while growing a harvestable fruit. */
public abstract class FruitLeavesBlock extends FallingParticlesLeavesBlock implements BonemealableBlock, HarvestableBlock {
    protected FruitLeavesBlock(float fallingLeavesChance, Properties properties) {
        super(fallingLeavesChance, AmbientLeavesBlockSoundPlayer.noAmbientSound(), properties);
        registerDefaultState(defaultBlockState().setValue(getAgeProperty(), 0));
    }

    // Block constructs its state definition before instance fields are initialized.
    protected abstract IntegerProperty getAgeProperty();
    protected abstract int getMaxAge();
    protected abstract int getGrowthChance();
    protected abstract boolean shouldSpawnFruitParticle(int age);
    protected abstract SimpleParticleType getFruitParticle();

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getAgeProperty());
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        return super.isRandomlyTicking(state) || canGrowFruit(state);
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (canGrowFruit(state) && random.nextInt(getGrowthChance()) == 0) {
            level.setBlock(pos, state.cycle(getAgeProperty()), 2);
        }
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (shouldSpawnFruitParticle(state.getValue(getAgeProperty())) && random.nextInt(100) == 0) {
            FallingFruitParticle.spawnFromFruitLeaves(level, pos, getFruitParticle());
        }
    }

    @Override
    protected void spawnFallingLeavesParticle(@NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
    }

    @Override
    public BlockState getHarvestedState(BlockState state) {
        return state.setValue(getAgeProperty(), 0);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!canHarvest(state)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            ItemStack harvest = getHarvest(state, level.getRandom());
            ModUtils.spawnItemAtClickedSide(level, pos, hitResult, harvest);
            level.setBlock(pos, getHarvestedState(state), 2);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    private boolean canGrowFruit(BlockState state) {
        return state.getValue(getAgeProperty()) < getMaxAge();
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, BonemealSource source) {
        return canGrowFruit(state);
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state, BonemealSource source) {
        return canGrowFruit(state);
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state, BonemealSource source) {
        int age = state.getValue(getAgeProperty());
        int nextAge = Math.min(getMaxAge(), age + 1);
        if (nextAge != age) {
            level.setBlock(pos, state.setValue(getAgeProperty(), nextAge), 2);
        }
    }
}
