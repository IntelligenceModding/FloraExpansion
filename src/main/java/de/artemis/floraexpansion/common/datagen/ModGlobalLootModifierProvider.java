package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.loot.AddItemModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
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
        this.add("pine_cone_to_spruce_leaves",
                new AddItemModifier(new LootItemCondition[]{
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SPRUCE_LEAVES).build(),
                        LootItemRandomChanceCondition.randomChance(0.2f).build()
                }, ModItems.PINE_CONE.get()));

        this.add("pine_litter_to_spruce_leaves",
                new AddItemModifier(new LootItemCondition[]{
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SPRUCE_LEAVES).build(),
                        LootItemRandomChanceCondition.randomChance(0.1f).build()
                }, ModBlocks.PINE_LITTER.get().asItem()));

        this.add("leaf_litter_to_oak_leaves",
                new AddItemModifier(new LootItemCondition[]{
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.OAK_LEAVES).build(),
                        LootItemRandomChanceCondition.randomChance(0.1f).build()
                }, ModBlocks.LEAF_LITTER.get().asItem()));

        this.add("leaf_litter_to_dark_oak_leaves",
                new AddItemModifier(new LootItemCondition[]{
                        LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.DARK_OAK_LEAVES).build(),
                        LootItemRandomChanceCondition.randomChance(0.1f).build()
                }, ModBlocks.LEAF_LITTER.get().asItem()));

        this.add("flax_seed_from_abandoned_mineshaft",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.27314936f).build()
                }, ModItems.FLAX_SEED.get(), 2, 4));

        this.add("flax_seed_from_simple_dungeon",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/simple_dungeon")).build(),
                        LootItemRandomChanceCondition.randomChance(0.18462976f).build()
                }, ModItems.FLAX_SEED.get(), 2, 4));

        this.add("flax_seed_from_village_taiga_house",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_taiga_house")).build(),
                        LootItemRandomChanceCondition.randomChance(0.406f).build()
                }, ModItems.FLAX_SEED.get(), 1, 5));

        this.add("flax_seed_from_village_plains_house",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_plains_house")).build(),
                        LootItemRandomChanceCondition.randomChance(0.575f).build()
                }, ModItems.FLAX_SEED.get(), 1, 3));

        this.add("flax_seed_from_woodland_mansion",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/woodland_mansion")).build(),
                        LootItemRandomChanceCondition.randomChance(0.12f).build()
                }, ModItems.FLAX_SEED.get(), 2, 4));

        this.add("prickly_pear_from_desert_pyramid",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.18f).build()
                }, ModItems.PRICKLY_PEAR.get(), 2, 4));

        this.add("cactus_thorn_from_desert_pyramid",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.14f).build()
                }, ModBlocks.CACTUS_THORN.get().asItem(), 1, 2));

        this.add("cactus_slice_from_desert_pyramid",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.12f).build()
                }, ModItems.CACTUS_SLICE.get(), 1, 2));

        this.add("cactus_juice_from_desert_pyramid",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.07f).build()
                }, ModItems.CACTUS_JUICE.get()));

        this.add("cactus_mosaic_from_desert_pyramid",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                }, ModBlocks.CACTUS_MOSAIC.get().asItem()));

        this.add("cactus_helmet_from_desert_pyramid",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.025f).build()
                }, ModItems.CACTUS_HELMET.get()));

        this.add("cactus_boots_from_desert_pyramid",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.025f).build()
                }, ModItems.CACTUS_BOOTS.get()));

        this.add("cactus_thorn_from_desert_pyramid_archaeology",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "archaeology/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                }, ModBlocks.CACTUS_THORN.get().asItem()));

        this.add("cactus_flower_from_desert_pyramid_archaeology",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "archaeology/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                }, ModBlocks.CACTUS_FLOWER.get().asItem()));

        this.add("prickly_pear_from_desert_pyramid_archaeology",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "archaeology/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.04f).build()
                }, ModItems.PRICKLY_PEAR.get()));

        this.add("cactus_slice_from_desert_pyramid_archaeology",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "archaeology/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.025f).build()
                }, ModItems.CACTUS_SLICE.get()));

        this.add("cactus_mosaic_from_desert_pyramid_archaeology",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "archaeology/desert_pyramid")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                }, ModBlocks.CACTUS_MOSAIC.get().asItem()));

        this.add("prickly_pear_from_desert_well_archaeology",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "archaeology/desert_well")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06f).build()
                }, ModItems.PRICKLY_PEAR.get()));

        this.add("cactus_flower_from_desert_well_archaeology",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "archaeology/desert_well")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                }, ModBlocks.CACTUS_FLOWER.get().asItem()));

        this.add("cactus_thorn_from_desert_well_archaeology",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "archaeology/desert_well")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                }, ModBlocks.CACTUS_THORN.get().asItem()));

        this.add("prickly_pear_from_village_desert_house",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_desert_house")).build(),
                        LootItemRandomChanceCondition.randomChance(0.25f).build()
                }, ModItems.PRICKLY_PEAR.get(), 1, 3));

        this.add("cactus_slice_from_village_desert_house",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_desert_house")).build(),
                        LootItemRandomChanceCondition.randomChance(0.18f).build()
                }, ModItems.CACTUS_SLICE.get(), 1, 2));

        this.add("cactus_juice_from_village_desert_house",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_desert_house")).build(),
                        LootItemRandomChanceCondition.randomChance(0.08f).build()
                }, ModItems.CACTUS_JUICE.get()));

        this.add("cactus_flower_from_village_desert_house",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_desert_house")).build(),
                        LootItemRandomChanceCondition.randomChance(0.06f).build()
                }, ModBlocks.CACTUS_FLOWER.get().asItem()));

        this.add("cactus_mosaic_from_village_desert_house",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_desert_house")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                }, ModBlocks.CACTUS_MOSAIC.get().asItem()));

        this.add("cactus_thorn_from_village_toolsmith",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_toolsmith")).build(),
                        LootItemRandomChanceCondition.randomChance(0.16f).build()
                }, ModBlocks.CACTUS_THORN.get().asItem(), 1, 3));

        this.add("cactus_mosaic_from_village_toolsmith",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_toolsmith")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                }, ModBlocks.CACTUS_MOSAIC.get().asItem()));

        this.add("cactus_helmet_from_village_toolsmith",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_toolsmith")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                }, ModItems.CACTUS_HELMET.get()));

        this.add("cactus_boots_from_village_toolsmith",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_toolsmith")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                }, ModItems.CACTUS_BOOTS.get()));

        this.add("cactus_thorn_from_village_weaponsmith",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_weaponsmith")).build(),
                        LootItemRandomChanceCondition.randomChance(0.18f).build()
                }, ModBlocks.CACTUS_THORN.get().asItem(), 1, 3));

        this.add("cactus_mosaic_from_village_weaponsmith",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_weaponsmith")).build(),
                        LootItemRandomChanceCondition.randomChance(0.03f).build()
                }, ModBlocks.CACTUS_MOSAIC.get().asItem()));

        this.add("cactus_helmet_from_village_weaponsmith",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_weaponsmith")).build(),
                        LootItemRandomChanceCondition.randomChance(0.015f).build()
                }, ModItems.CACTUS_HELMET.get()));

        this.add("cactus_boots_from_village_weaponsmith",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/village/village_weaponsmith")).build(),
                        LootItemRandomChanceCondition.randomChance(0.02f).build()
                }, ModItems.CACTUS_BOOTS.get()));

        this.add("prickly_pear_from_abandoned_mineshaft",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.14f).build()
                }, ModItems.PRICKLY_PEAR.get(), 1, 3));

        this.add("cactus_slice_from_abandoned_mineshaft",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.12f).build()
                }, ModItems.CACTUS_SLICE.get(), 1, 2));

        this.add("cactus_juice_from_abandoned_mineshaft",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.05f).build()
                }, ModItems.CACTUS_JUICE.get()));

        this.add("cactus_thorn_from_abandoned_mineshaft",
                new AddItemModifier(new LootItemCondition[]{
                        LootTableIdCondition.builder(Identifier.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft")).build(),
                        LootItemRandomChanceCondition.randomChance(0.11f).build()
                }, ModBlocks.CACTUS_THORN.get().asItem(), 1, 2));
    }
}