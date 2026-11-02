package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBiomeModifiers;
import de.artemis.floraexpansion.common.registry.ModConfiguredFeatures;
import de.artemis.floraexpansion.common.registry.ModPlacedFeatures;
import de.artemis.floraexpansion.common.registry.ModTreeConfiguredFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        addProviders(event.getGenerator(), event.getLookupProvider());
    }

    @SubscribeEvent
    public static void gatherServerData(GatherDataEvent.Server event) {
        addProviders(event.getGenerator(), event.getLookupProvider());
    }

    private static void addProviders(DataGenerator generator, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        PackOutput output = generator.getPackOutput();
 var datapack = new ModDatapackProvider(output, lookupProvider);
 generator.addProvider(true, datapack);
 lookupProvider = datapack.getRegistryProvider();
 generator.addProvider(true, new LootTableProvider(output, java.util.Set.of(), java.util.List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
 generator.addProvider(true, new ModRecipeProvider.Runner(output, lookupProvider));
 generator.addProvider(true, new net.minecraft.data.advancements.AdvancementProvider(output, lookupProvider, java.util.List.of(ModAdvancementProvider.tree())));
 generator.addProvider(true, new ModDataMapProvider(output, lookupProvider));
        generator.addProvider(true, new ModBlockTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModItemTagProvider(output, lookupProvider));
        generator.addProvider(true, new ModModelProvider(output));
        generator.addProvider(true, new ModLanguageProvider(output, "en_us"));
        generator.addProvider(true, new ModGlobalLootModifierProvider(output, lookupProvider));
    }
}
