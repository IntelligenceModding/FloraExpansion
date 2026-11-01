package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Sun drying consumes one source and adds exactly one deposit layer, including in deep basins. */
public final class SaltWaterEvaporation {
    // About nine minutes of eligible daylight per source at the default random tick speed.
    private static final int RANDOM_TICK_CHANCE = 8;
    /** Residue from one vaporized placement; the fluid was never placed by the bucket. */
    public static void leaveResidue(net.minecraft.world.level.Level level,BlockPos pos) {
        if (level.isClientSide()) return;
        var state=level.getBlockState(pos);
        if (state.is(ModBlocks.SALT_DEPOSIT.get()) && state.getValue(SaltDepositBlock.LAYERS)<4
                && !state.getValue(SaltDepositBlock.WATERLOGGED)) {
            level.setBlockAndUpdate(pos,state.cycle(SaltDepositBlock.LAYERS).setValue(SaltDepositBlock.NATURAL,false));
        } else if (state.isAir() || state.is(ModBlocks.SALT_WATER.get())
                || state.canBeReplaced() && state.getFluidState().isEmpty()) {
            if (!state.isAir() && !state.is(ModBlocks.SALT_WATER.get())) level.destroyBlock(pos,true);
            level.setBlockAndUpdate(pos,ModBlocks.SALT_DEPOSIT.get().defaultBlockState().setValue(SaltDepositBlock.NATURAL,false));
        } else {
            // Never erase a solid fluid container to make room for the residue.
            net.minecraft.world.level.block.Block.popResource(level,pos,
                    new net.minecraft.world.item.ItemStack(de.artemis.floraexpansion.common.registry.ModItems.RAW_SALT.get()));
        }
    }
    public static void randomTick(ServerLevel level,BlockPos pos,RandomSource random) {
        if (random.nextInt(RANDOM_TICK_CHANCE)==0) tryEvaporate(level,pos);
    }
    public static boolean tryEvaporate(ServerLevel level,BlockPos pos) {
        var original=level.getBlockState(pos);
        if ((!original.is(ModBlocks.SALT_WATER.get()) && !original.is(ModBlocks.SALT_DEPOSIT.get()))
                || !brineSource(original) || !level.dimensionType().hasSkyLight()
                || !level.isBrightOutside() || level.isRaining() || level.isThundering()
                || !sunExposed(level,pos)) return false;

        // Find the first supported salt layer or open sediment location in this column.
        // This avoids falling-block entities displacing additional sources in a deep pool.
        var target=pos;
        while (true) {
            var current=level.getBlockState(target);
            if (current.is(ModBlocks.SALT_DEPOSIT.get()) && current.getValue(SaltDepositBlock.LAYERS)<4) break;
            if (!current.isAir() && !current.is(ModBlocks.SALT_WATER.get())) return false;
            if (target.getY()<=level.getMinY()) return false;
            var below=target.below();
            if (level.getBlockState(below).isFaceSturdy(level,below,Direction.UP)) break;
            target=below;
        }
        var destination=level.getBlockState(target);
        int layers=destination.is(ModBlocks.SALT_DEPOSIT.get())?destination.getValue(SaltDepositBlock.LAYERS):0;
        if (destination.is(ModBlocks.SALT_DEPOSIT.get()) && destination.getValue(SaltDepositBlock.WATERLOGGED)
                && !destination.getValue(SaltDepositBlock.BRINE)) return false;
        boolean retainSource=!target.equals(pos) && brineSource(destination);
        int nextLayers=layers+1;
        var salt=ModBlocks.SALT_DEPOSIT.get().defaultBlockState().setValue(SaltDepositBlock.LAYERS,nextLayers)
                .setValue(SaltDepositBlock.NATURAL,false)
                .setValue(SaltDepositBlock.WATERLOGGED,retainSource && nextLayers<4)
                .setValue(SaltDepositBlock.BRINE,retainSource && nextLayers<4);
        if (!salt.canSurvive(level,target)) return false;

        if (!target.equals(pos)) {
            // Full salt blocks cannot contain fluid. Move the retained bottom source into the
            // vacated surface position instead of silently destroying a second source.
            level.setBlockAndUpdate(pos,retainSource && nextLayers==4
                    ? ModBlocks.SALT_WATER.get().defaultBlockState():Blocks.AIR.defaultBlockState());
        }
        level.setBlockAndUpdate(target,salt);
        level.sendParticles(ParticleTypes.CLOUD,pos.getX()+0.5,pos.getY()+0.9,pos.getZ()+0.5,
                2,0.16,0.03,0.16,0.005);
        return true;
    }
    private static boolean sunExposed(ServerLevel level,BlockPos pos) {
        // Check actual overhead blocks rather than cached sky light, which can lag behind
        // drained water or a newly built roof. Glass passes sunlight; water and foliage shade it.
        int top=level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,pos.getX(),pos.getZ());
        for (var above=pos.above();above.getY()<top;above=above.above()) {
            if (level.getBlockState(above).getLightDampening()>0) return false;
        }
        return true;
    }

    private static boolean brineSource(BlockState state) {
        var fluid=state.getFluidState();
        return fluid.is(ModFluids.SALT_WATER.get()) && fluid.isSource();
    }
    private SaltWaterEvaporation() {}
}



