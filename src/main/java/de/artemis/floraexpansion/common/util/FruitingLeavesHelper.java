package de.artemis.floraexpansion.common.util;

import de.artemis.floraexpansion.common.particle.FallingFruitParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.function.IntPredicate;

public final class FruitingLeavesHelper {
    private FruitingLeavesHelper() {
    }

    public static boolean isRandomlyTicking(boolean superIsRandomlyTicking, BlockState state,
                                            IntegerProperty ageProperty, int maxAge) {
        return superIsRandomlyTicking || state.getValue(ageProperty) < maxAge;
    }

    public static void growIfAble(BlockState state, ServerLevel level, BlockPos pos, RandomSource random,
                                  IntegerProperty ageProperty, int maxAge, int growthChance) {
        int age = state.getValue(ageProperty);
        if (age < maxAge && random.nextInt(growthChance) == 0) {
            level.setBlock(pos, state.setValue(ageProperty, age + 1), 2);
        }
    }

    public static void animateFruit(BlockState state, Level level, BlockPos pos, RandomSource random,
                                    IntegerProperty ageProperty, IntPredicate shouldSpawnAtAge,
                                    SimpleParticleType particle) {
        if (!shouldSpawnAtAge.test(state.getValue(ageProperty))) {
            return;
        }

        if (random.nextInt(100) != 0) {
            return;
        }

        FallingFruitParticle.spawnFromFruitingLeaves(level, pos, particle);
    }

    public static boolean canGrow(BlockState state, IntegerProperty ageProperty, int maxAge) {
        return state.getValue(ageProperty) < maxAge;
    }

    public static void applyBonemeal(ServerLevel level, BlockPos pos, BlockState state,
                                     IntegerProperty ageProperty, int maxAge) {
        int currentAge = state.getValue(ageProperty);
        int newAge = Math.min(maxAge, currentAge + 1);

        if (newAge != currentAge) {
            level.setBlock(pos, state.setValue(ageProperty, newAge), 2);
        }
    }

    public static void resetFruitAge(Level level, BlockPos pos, BlockState state, IntegerProperty ageProperty) {
        level.setBlock(pos, state.setValue(ageProperty, 0), 2);
        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F,
                0.8F + level.getRandom().nextFloat() * 0.4F);
    }
}
