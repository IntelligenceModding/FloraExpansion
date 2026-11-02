package de.artemis.floraexpansion.mixin;

import de.artemis.floraexpansion.common.block.SaltSpikeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Let vanilla's delayed cauldron tick resolve salt spikes on 26.1.2. */
@Mixin(PointedDripstoneBlock.class)
public abstract class SaltSpikeCauldronMixin {
    @Inject(method = "findStalactiteTipAboveCauldron", at = @At("RETURN"), cancellable = true)
    private static void saltTip(Level level, BlockPos pos, CallbackInfoReturnable<BlockPos> result) {
        if (result.getReturnValue() == null) {
            var tip = SaltSpikeBlock.findStalactiteTipAboveCauldron(level, pos);
            if (tip != null) result.setReturnValue(tip);
        }
    }

    @Inject(method = "getCauldronFillFluidType", at = @At("HEAD"), cancellable = true)
    private static void saltFluid(ServerLevel level, BlockPos pos, CallbackInfoReturnable<Fluid> result) {
        if (level.getBlockState(pos).getBlock() instanceof SaltSpikeBlock)
            result.setReturnValue(SaltSpikeBlock.getCauldronFillFluidType(level, pos));
    }
}
