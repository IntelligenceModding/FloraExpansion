package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.worldgen.SaltWaterEvaporation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class SaltWaterBlock extends LiquidBlock {
    public SaltWaterBlock(Properties properties) { super(ModFluids.SALT_WATER.get(),properties); }
    @Override protected void onPlace(BlockState state,net.minecraft.world.level.Level level,BlockPos pos,
                                     BlockState oldState,boolean movedByPiston) {
        if (!level.isClientSide() && state.getFluidState().isSource()
                && level.environmentAttributes().getValue(net.minecraft.world.attribute.EnvironmentAttributes.WATER_EVAPORATES,pos)) {
            // Also covers direct fluid placement and callers using the legacy bucket overload.
            ModFluids.SALT_WATER_TYPE.get().onVaporize(null,level,pos,
                    new net.neoforged.neoforge.fluids.FluidStack(ModFluids.SALT_WATER.get(),
                            net.neoforged.neoforge.fluids.FluidType.BUCKET_VOLUME));
            return;
        }
        super.onPlace(state,level,pos,oldState,movedByPiston);
    }
    @Override protected boolean isRandomlyTicking(BlockState state) { return state.getFluidState().isSource(); }
    @Override protected void randomTick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random) {
        super.randomTick(state,level,pos,random);
        SaltWaterEvaporation.randomTick(level,pos,random);
    }
}
