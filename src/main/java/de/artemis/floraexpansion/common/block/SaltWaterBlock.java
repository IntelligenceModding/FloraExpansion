package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.worldgen.SaltWaterEvaporation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class SaltWaterBlock extends LiquidBlock {
    public SaltWaterBlock(Properties properties) { super(ModFluids.SALT_WATER,properties); }
    @Override public void onPlace(@NotNull BlockState state,net.minecraft.world.level.Level level,@NotNull BlockPos pos,
                                     @NotNull BlockState oldState,boolean movedByPiston) {
        if (!level.isClientSide && state.getFluidState().isSource()
                && level.dimensionType().ultraWarm()) {
            // Also covers direct fluid placement and callers using the legacy bucket overload.
            ModFluids.SALT_WATER_TYPE.get().onVaporize(null,level,pos,
                    new net.minecraftforge.fluids.FluidStack(ModFluids.SALT_WATER.get(),
                            net.minecraftforge.fluids.FluidType.BUCKET_VOLUME));
            return;
        }
        super.onPlace(state,level,pos,oldState,movedByPiston);
    }
    @Override public boolean isRandomlyTicking(BlockState state) { return state.getFluidState().isSource(); }
    @Override public void randomTick(@NotNull BlockState state,@NotNull ServerLevel level,@NotNull BlockPos pos,@NotNull RandomSource random) {
        super.randomTick(state,level,pos,random);
        SaltWaterEvaporation.randomTick(level,pos,random);
    }
}
