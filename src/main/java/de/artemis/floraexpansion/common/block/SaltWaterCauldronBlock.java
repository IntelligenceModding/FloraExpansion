package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.event.SaltWaterInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class SaltWaterCauldronBlock extends LayeredCauldronBlock {
    public SaltWaterCauldronBlock(Properties properties) { super(Biome.Precipitation.NONE, SaltWaterInteractions.CAULDRON, properties); }
    @Override public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {}
    @Override protected boolean canReceiveStalactiteDrip(Fluid fluid) {
        return fluid == de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get();
    }
    @Override protected void receiveStalactiteDrip(BlockState state, Level level, BlockPos pos, Fluid fluid) {
        if (!isFull(state) && canReceiveStalactiteDrip(fluid)) fluid.getFluidType().handleCauldronDrip(fluid, level, pos);
    }
}
