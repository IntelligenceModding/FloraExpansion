package de.artemis.floraexpansion.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.common.CommonHooks;

public final class PlantGrowth {
    private PlantGrowth() {}

    public static boolean canGrow(Level level, BlockPos pos, BlockState state, RandomSource random, int oneIn, double multiplier) {
        if (multiplier <= 0) return false;
        boolean rolled = multiplier == 1 ? oneIn == 1 || random.nextInt(oneIn) == 0 : random.nextDouble() < Math.min(1, multiplier / oneIn);
        return CommonHooks.canCropGrow(level, pos, state, rolled);
    }

    public static void grown(Level level, BlockPos pos, BlockState previous) {
        CommonHooks.fireCropGrowPost(level, pos, previous);
    }
}
