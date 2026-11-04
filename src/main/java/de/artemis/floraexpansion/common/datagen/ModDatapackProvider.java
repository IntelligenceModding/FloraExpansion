package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBiomeModifiers;
import de.artemis.floraexpansion.common.registry.ModConfiguredFeatures;
import de.artemis.floraexpansion.common.registry.ModPlacedFeatures;
import de.artemis.floraexpansion.common.registry.ModTreeConfiguredFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModDatapackProvider extends DatapackBuiltinEntriesProvider {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
 .add(Registries.JUKEBOX_SONG, de.artemis.floraexpansion.common.registry.ModJukeboxSongs::bootstrap)
 .add(Registries.BIOME, de.artemis.floraexpansion.common.registry.ModBiomes::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, context -> {
                ModConfiguredFeatures.bootstrap(context);
                ModTreeConfiguredFeatures.bootstrap(context);
            })
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);

    public ModDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(FloraExpansion.MODID));
    }
}
