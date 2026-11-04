package de.artemis.floraexpansion.mixin;

import de.artemis.floraexpansion.common.registry.ModBiomes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiNoiseBiomeSourceParameterList.class)
public abstract class OverworldBiomeParametersMixin {
    @Shadow @Final @Mutable private Climate.ParameterList<Holder<Biome>> parameters;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void floraexpansion$addSaltCaves(MultiNoiseBiomeSourceParameterList.Preset preset,
                                            HolderGetter<Biome> biomes, CallbackInfo ci) {
        // Vanilla datagen bootstraps this list before mod biomes exist.
        if (net.minecraftforge.fml.loading.FMLLoader.getLaunchHandler().isData()) return;
        if (preset != MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD) return;
        var saltBiome = biomes instanceof HolderLookup<Biome> lookup
                ? lookup.listElements().filter(holder -> holder.is(ModBiomes.SALT_CAVES)).findFirst()
                : biomes.get(ModBiomes.SALT_CAVES);
        saltBiome.ifPresent(salt -> {
            parameters = new Climate.ParameterList<>(ModBiomes.withSaltCaves(parameters.values(), salt));
        });
    }
}
