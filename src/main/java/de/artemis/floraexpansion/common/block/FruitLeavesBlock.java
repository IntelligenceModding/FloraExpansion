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
import net.minecraft.world.level.block.LeavesBlock;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import de.artemis.floraexpansion.common.particle.FallingFruitParticle;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.config.ServerConfig;
import de.artemis.floraexpansion.common.config.ClientConfig;
import de.artemis.floraexpansion.common.util.PlantGrowth;

/** Leaves that retain vanilla decay behavior while growing a harvestable fruit. */
public abstract class FruitLeavesBlock extends LeavesBlock implements BonemealableBlock, HarvestableBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        return useWithoutItem(state, level, pos, player, hit);
    }

    public FruitLeavesBlock(float fallingLeavesChance, Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(getAgeProperty(), 0));
    }

    // Block constructs its state definition before instance fields are initialized.
    public abstract IntegerProperty getAgeProperty();
    public abstract int getMaxAge();
    public abstract int getGrowthChance();
    public abstract boolean shouldSpawnFruitParticle(int age);
    public abstract SimpleParticleType getFruitParticle();

    @Override
    public void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(getAgeProperty());
    }

    @Override
    public boolean isRandomlyTicking(@NotNull BlockState state) {
        return super.isRandomlyTicking(state) || canGrowFruit(state);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (canGrowFruit(state) && PlantGrowth.canGrow(level, pos, state, random, getGrowthChance(), ServerConfig.fruitGrowth.get())) {
            level.setBlock(pos, state.cycle(getAgeProperty()), 2);
            PlantGrowth.grown(level, pos, state);
        }
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (shouldSpawnFruitParticle(state.getValue(getAgeProperty())) && random.nextInt(100) == 0
                && ClientConfig.fruitParticles.get() > 0 && (ClientConfig.fruitParticles.get() == 1 || random.nextDouble() < ClientConfig.fruitParticles.get())) {
            FallingFruitParticle.spawnFromFruitLeaves(level, pos, getFruitParticle());
        }
    }

    public void spawnFallingLeavesParticle(@NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
    }

    @Override
    public BlockState getHarvestedState(BlockState state) {
        return state.setValue(getAgeProperty(), 0);
    }

    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (!canHarvest(state)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            for (ItemStack harvest : getHarvestDrops(state, (ServerLevel) level, pos, player))
                ModUtils.spawnItemAtClickedSide(level, pos, hitResult, harvest);
            level.setBlock(pos, getHarvestedState(state), 2);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
        }
        return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    private boolean canGrowFruit(BlockState state) {
        return state.getValue(getAgeProperty()) < getMaxAge();
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClientSide) {
        return canGrowFruit(state);
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        return canGrowFruit(state);
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        int age = state.getValue(getAgeProperty());
        int nextAge = Math.min(getMaxAge(), age + 1);
        if (nextAge != age) {
            level.setBlock(pos, state.setValue(getAgeProperty(), nextAge), 2);
        }
    }
}
