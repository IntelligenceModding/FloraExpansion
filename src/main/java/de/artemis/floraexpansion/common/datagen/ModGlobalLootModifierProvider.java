package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.ItemLike;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, FloraExpansion.MODID);
    }

    @Override
    protected void start() {
        addFromBlock("spruce_cone_to_spruce_leaves", Blocks.SPRUCE_LEAVES, 0.2f, ModItems.SPRUCE_CONE.get());

        addFromBlock("needle_litter_to_spruce_leaves", Blocks.SPRUCE_LEAVES, 0.1f, ModBlocks.NEEDLE_LITTER.get().asItem());

        addFromLootTable("flax_seeds_from_abandoned_mineshaft", "chests/abandoned_mineshaft", 0.27314936f, ModItems.FLAX_SEEDS.get(), 2, 4);

        addFromLootTable("flax_seeds_from_simple_dungeon", "chests/simple_dungeon", 0.18462976f, ModItems.FLAX_SEEDS.get(), 2, 4);

        addFromLootTable("flax_seeds_from_village_taiga_house", "chests/village/village_taiga_house", 0.406f, ModItems.FLAX_SEEDS.get(), 1, 5);

        addFromLootTable("flax_seeds_from_village_plains_house", "chests/village/village_plains_house", 0.575f, ModItems.FLAX_SEEDS.get(), 1, 3);

        addFromLootTable("flax_seeds_from_woodland_mansion", "chests/woodland_mansion", 0.12f, ModItems.FLAX_SEEDS.get(), 2, 4);

        addFromLootTable("prickly_pear_from_desert_pyramid", "chests/desert_pyramid", 0.18f, ModItems.PRICKLY_PEAR.get(), 2, 4);

        addFromLootTable("cactus_spine_from_desert_pyramid", "chests/desert_pyramid", 0.14f, ModBlocks.CACTUS_SPINE.get().asItem(), 1, 2);

        addFromLootTable("cactus_slice_from_desert_pyramid", "chests/desert_pyramid", 0.12f, ModItems.CACTUS_SLICE.get(), 1, 2);

        addFromLootTable("cactus_juice_from_desert_pyramid", "chests/desert_pyramid", 0.07f, ModItems.CACTUS_JUICE.get());

        addFromLootTable("cactus_mosaic_from_desert_pyramid", "chests/desert_pyramid", 0.05f, ModBlocks.CACTUS_MOSAIC.get().asItem());

        addFromLootTable("spiked_leather_helmet_from_desert_pyramid", "chests/desert_pyramid", 0.025f, ModItems.SPIKED_LEATHER_HELMET.get());

        addFromLootTable("spiked_leather_boots_from_desert_pyramid", "chests/desert_pyramid", 0.025f, ModItems.SPIKED_LEATHER_BOOTS.get());

        addFromLootTable("cactus_spine_from_desert_pyramid_archaeology", "archaeology/desert_pyramid", 0.05f, ModBlocks.CACTUS_SPINE.get().asItem());

        addFromLootTable("giant_cactus_shoot_from_desert_pyramid_archaeology", "archaeology/desert_pyramid", 0.05f, ModBlocks.GIANT_CACTUS_SHOOT.get().asItem());

        addFromLootTable("prickly_pear_from_desert_pyramid_archaeology", "archaeology/desert_pyramid", 0.04f, ModItems.PRICKLY_PEAR.get());

        addFromLootTable("cactus_slice_from_desert_pyramid_archaeology", "archaeology/desert_pyramid", 0.025f, ModItems.CACTUS_SLICE.get());

        addFromLootTable("cactus_mosaic_from_desert_pyramid_archaeology", "archaeology/desert_pyramid", 0.02f, ModBlocks.CACTUS_MOSAIC.get().asItem());

        addFromLootTable("prickly_pear_from_desert_well_archaeology", "archaeology/desert_well", 0.06f, ModItems.PRICKLY_PEAR.get());

        addFromLootTable("giant_cactus_shoot_from_desert_well_archaeology", "archaeology/desert_well", 0.05f, ModBlocks.GIANT_CACTUS_SHOOT.get().asItem());

        addFromLootTable("cactus_spine_from_desert_well_archaeology", "archaeology/desert_well", 0.02f, ModBlocks.CACTUS_SPINE.get().asItem());

        addFromLootTable("prickly_pear_from_village_desert_house", "chests/village/village_desert_house", 0.25f, ModItems.PRICKLY_PEAR.get(), 1, 3);

        addFromLootTable("cactus_slice_from_village_desert_house", "chests/village/village_desert_house", 0.18f, ModItems.CACTUS_SLICE.get(), 1, 2);

        addFromLootTable("cactus_juice_from_village_desert_house", "chests/village/village_desert_house", 0.08f, ModItems.CACTUS_JUICE.get());

        addFromLootTable("giant_cactus_shoot_from_village_desert_house", "chests/village/village_desert_house", 0.06f, ModBlocks.GIANT_CACTUS_SHOOT.get().asItem());

        addFromLootTable("cactus_mosaic_from_village_desert_house", "chests/village/village_desert_house", 0.03f, ModBlocks.CACTUS_MOSAIC.get().asItem());

        addFromLootTable("cactus_spine_from_village_toolsmith", "chests/village/village_toolsmith", 0.16f, ModBlocks.CACTUS_SPINE.get().asItem(), 1, 3);

        addFromLootTable("cactus_mosaic_from_village_toolsmith", "chests/village/village_toolsmith", 0.05f, ModBlocks.CACTUS_MOSAIC.get().asItem());

        addFromLootTable("spiked_leather_helmet_from_village_toolsmith", "chests/village/village_toolsmith", 0.02f, ModItems.SPIKED_LEATHER_HELMET.get());

        addFromLootTable("spiked_leather_boots_from_village_toolsmith", "chests/village/village_toolsmith", 0.02f, ModItems.SPIKED_LEATHER_BOOTS.get());

        addFromLootTable("cactus_spine_from_village_weaponsmith", "chests/village/village_weaponsmith", 0.18f, ModBlocks.CACTUS_SPINE.get().asItem(), 1, 3);

        addFromLootTable("cactus_mosaic_from_village_weaponsmith", "chests/village/village_weaponsmith", 0.03f, ModBlocks.CACTUS_MOSAIC.get().asItem());

        addFromLootTable("spiked_leather_helmet_from_village_weaponsmith", "chests/village/village_weaponsmith", 0.015f, ModItems.SPIKED_LEATHER_HELMET.get());

        addFromLootTable("spiked_leather_boots_from_village_weaponsmith", "chests/village/village_weaponsmith", 0.02f, ModItems.SPIKED_LEATHER_BOOTS.get());

        addFromLootTable("prickly_pear_from_abandoned_mineshaft", "chests/abandoned_mineshaft", 0.14f, ModItems.PRICKLY_PEAR.get(), 1, 3);

        addFromLootTable("cactus_slice_from_abandoned_mineshaft", "chests/abandoned_mineshaft", 0.12f, ModItems.CACTUS_SLICE.get(), 1, 2);

        addFromLootTable("cactus_juice_from_abandoned_mineshaft", "chests/abandoned_mineshaft", 0.05f, ModItems.CACTUS_JUICE.get());

        addFromLootTable("cactus_spine_from_abandoned_mineshaft", "chests/abandoned_mineshaft", 0.11f, ModBlocks.CACTUS_SPINE.get().asItem(), 1, 2);
    }

    private void addFromLootTable(String name, String lootTable, float chance, ItemLike item) {
        addFromLootTable(name, lootTable, chance, item, 1, 1);
    }

    private void addFromLootTable(String name, String lootTable, float chance, ItemLike item, int minCount, int maxCount) {
        addWithChance(name, LootTableIdCondition.builder(Identifier.withDefaultNamespace(lootTable)).build(),
                chance, item, minCount, maxCount);
    }

    private void addFromBlock(String name, Block block, float chance, ItemLike item) {
        addWithChance(name, MatchBlock.blockMatches(registries.lookupOrThrow(Registries.BLOCK), block).build(),
                chance, item, 1, 1);
    }

    private void addWithChance(String name, LootItemCondition source, float chance, ItemLike item, int minCount, int maxCount) {
        add(name, new AddItemModifier(new LootItemCondition[]{
                source, LootItemRandomChanceCondition.randomChance(chance).build()
        }, item.asItem(), minCount, maxCount));
    }
}
