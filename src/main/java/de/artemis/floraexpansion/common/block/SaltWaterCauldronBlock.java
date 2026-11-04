package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.event.SaltWaterInteractions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class SaltWaterCauldronBlock extends LayeredCauldronBlock {
    public SaltWaterCauldronBlock(Properties properties) { super(Biome.Precipitation.NONE, SaltWaterInteractions.CAULDRON, properties); }
    @Override public void handlePrecipitation(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Biome.Precipitation precipitation) {}
    @Override protected boolean canReceiveStalactiteDrip(@NotNull Fluid fluid) {
        return fluid == de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get();
    }
    @Override protected void receiveStalactiteDrip(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Fluid fluid) {
        if (!isFull(state) && canReceiveStalactiteDrip(fluid)) de.artemis.floraexpansion.common.registry.ModFluids.handleCauldronDrip(fluid, level, pos);
    }
}
