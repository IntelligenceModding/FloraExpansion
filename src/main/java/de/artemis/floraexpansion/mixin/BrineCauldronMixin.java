package de.artemis.floraexpansion.mixin;
import de.artemis.floraexpansion.common.registry.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(CauldronBlock.class)
public abstract class BrineCauldronMixin {
 @Inject(method="canReceiveStalactiteDrip", at=@At("HEAD"), cancellable=true)
 private void brineAccepted(Fluid fluid, CallbackInfoReturnable<Boolean> result) {
  if(fluid==ModFluids.SALT_WATER.get()) result.setReturnValue(true);
 }
 @Inject(method="receiveStalactiteDrip", at=@At("HEAD"), cancellable=true)
 private void fillBrine(BlockState state, Level level, BlockPos pos, Fluid fluid, CallbackInfo result) {
  if(ModFluids.handleCauldronDrip(fluid, level, pos)) result.cancel();
 }
}
