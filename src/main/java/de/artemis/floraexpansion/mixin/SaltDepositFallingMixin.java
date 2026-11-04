package de.artemis.floraexpansion.mixin;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import net.minecraft.world.entity.item.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 1.20.1 predates the falling-block callback used to merge salt layers. */
@Mixin(FallingBlockEntity.class)
abstract class SaltDepositFallingMixin {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void floraexpansion$mergeSaltLayers(CallbackInfo callback) {
        var falling = (FallingBlockEntity) (Object) this;
        if (falling.getBlockState().getBlock() instanceof SaltDepositBlock salt) {
            salt.fallingTick(falling.level(), falling.blockPosition(), falling);
            if (!falling.isAlive()) callback.cancel();
        }
    }
}
