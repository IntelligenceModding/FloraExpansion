package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

/** Small clumps on already-created brine shores; never replaces water or existing plants. */
final class GlasswortPatch {
    static void tryPlace(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (random.nextInt(9) != 0 || !place(level, pos)) return;
        for (var direction : Direction.Plane.HORIZONTAL) {
            if (random.nextBoolean()) place(level, pos.relative(direction));
        }
    }

    private static boolean place(WorldGenLevel level, BlockPos pos) {
        if (!level.ensureCanWrite(pos) || !level.getBlockState(pos).isAir()) return false;
        var state = ModBlocks.GLASSWORT.get().defaultBlockState();
        if (!state.canSurvive(level, pos)) return false;
        return level.setBlock(pos, state, 2);
    }
    private GlasswortPatch() {}
}
