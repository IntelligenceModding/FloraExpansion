package de.artemis.floraexpansion.common.block;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.Level;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import de.artemis.floraexpansion.common.registry.ModParticles;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.PointedDripstoneBlock;

/** Vanilla pointed-spike behavior, with salt-bearing seepage required for growth. */
public class SaltSpikeBlock extends PointedDripstoneBlock {
    public SaltSpikeBlock(Properties properties) { super(ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState(), properties); }

    @Override protected boolean canGrow(LevelReader level, BlockPos pos) {
        var fluid = level.getFluidState(pos.above(2));
        return level.getBlockState(pos.above()).is(blockToGrowOn.getBlock())
                && fluid.is(ModFluids.SALT_WATER.get()) && fluid.isSource();
    }

    /** Resolve seepage through the whole hanging column, never inventing water in a dry cave. */
    public Fluid getDripFluid(LevelReader level, BlockPos pos) {
        var state = level.getBlockState(pos);
        if (!state.is(this) || state.getValue(TIP_DIRECTION) != Direction.DOWN || state.getValue(WATERLOGGED))
            return Fluids.EMPTY;
        for (int distance = 1; distance <= 11; distance++) {
            var above = pos.above(distance);
            var next = level.getBlockState(above);
            if (next.is(this) && next.getValue(TIP_DIRECTION) == Direction.DOWN) continue;
            if (next.is(this) || !next.isFaceSturdy(level, above, Direction.DOWN)) break;
            var fluid = level.getFluidState(above.above());
            if (fluid.is(ModFluids.SALT_WATER.get()) && fluid.isSource()) return ModFluids.SALT_WATER.get();
            break;
        }
        return Fluids.EMPTY;
    }

    @Override public void animateTick(BlockState state,
                                      Level level, BlockPos pos, RandomSource random) {
        if (!isFreeHangingStalactite(state) || random.nextFloat() > .12F || getDripFluid(level, pos) != ModFluids.SALT_WATER.get()) return;
        var offset = state.getOffset(pos);
        level.addParticle(ModParticles.DRIPPING_SALT_WATER.get(),
                pos.getX() + .5 + offset.x, pos.getY() + .25, pos.getZ() + .5 + offset.z, 0, 0, 0);
    }

    @Override protected void randomTick(BlockState state,
                                        ServerLevel level, BlockPos pos, RandomSource random) {
        // Do not inherit freshwater/mud/lava transfer when regular water is above a salt spike.
        // Salt Water uses vanilla's lava-rate cauldron transfer; other fluids never transfer through salt spikes.
        if (getDripFluid(level, pos) == ModFluids.SALT_WATER.get()) super.randomTick(state, level, pos, random);
    }
}
