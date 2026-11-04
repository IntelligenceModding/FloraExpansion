package de.artemis.floraexpansion.common.datagen;


import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.data.event.GatherDataEvent;


import java.util.concurrent.CompletableFuture;

public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) { addProviders(event.getGenerator(), event.getLookupProvider(), event.getExistingFileHelper()); }
    private static void addProviders(DataGenerator generator, CompletableFuture<net.minecraft.core.HolderLookup.Provider> lookupProvider, net.minecraftforge.common.data.ExistingFileHelper existingFiles) {
        PackOutput output = generator.getPackOutput();
 var datapack = new ModDatapackProvider(output, lookupProvider);
 generator.addProvider(true, datapack);
 lookupProvider = datapack.getRegistryProvider();
 generator.addProvider(true, new LootTableProvider(output, java.util.Set.of(), java.util.List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK))));
 generator.addProvider(true, new ModRecipeProvider(output, lookupProvider));
 generator.addProvider(true, new net.minecraft.data.advancements.AdvancementProvider(output, lookupProvider, java.util.List.of(ModAdvancementProvider.tree())));

        generator.addProvider(true, new ModBlockTagProvider(output, lookupProvider, existingFiles));
        generator.addProvider(true, new ModItemTagProvider(output, lookupProvider, existingFiles));
        generator.addProvider(true, new ModModelProvider(output));
        generator.addProvider(true, new ModLanguageProvider(output, "en_us"));
        generator.addProvider(true, new ModGlobalLootModifierProvider(output, lookupProvider));
    }
}
