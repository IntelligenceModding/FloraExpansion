package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.common.block.*;
import net.minecraft.advancements.predicates.ItemPredicate;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.ints.UniformGenerator;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.registry.ModBlocks;

import java.util.List;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(net.minecraft.data.loot.LootTableSubProvider.Context registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    private static Holder<ContextIntProvider> constant(float value) {
        return Holder.direct(new ConstantValue((int) value));
    }

    private static Holder<ContextIntProvider> uniform(float min, float max) {
        return Holder.direct(new UniformGenerator(constant(min), constant(max)));
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.APPLE_CORE.get());
        dropSelf(ModBlocks.CHERRY_PIT.get());
        dropSelf(ModBlocks.FLAX_BALE.get());
        dropSelf(ModBlocks.LINEN_BLOCK.get());
        dropSelf(ModBlocks.LINEN_CARPET.get());
        dropSelf(ModBlocks.PEBBLE_BLOCK.get());
        dropSelf(ModBlocks.TWIG_LADDER.get());
        dropPottedContents(ModBlocks.POTTED_GIANT_CACTUS_STEM.get());
        add(ModBlocks.PINE_LITTER.get(), layeredDrops(ModBlocks.PINE_LITTER.get(), PineLitterBlock.AMOUNT));
        add(ModBlocks.PEBBLE_PATCH.get(), layeredDrops(ModBlocks.PEBBLE_PATCH.get(), PebblePatchBlock.AMOUNT));
        add(ModBlocks.FLAX_CROP.get(), flaxDrops());
        add(ModBlocks.BLUEBERRY_BUSH.get(), createBlueberryBushDrops(ModBlocks.BLUEBERRY_BUSH.get(), uniform(2.0F, 3.0F)));
        add(ModBlocks.LARGE_BLUEBERRY_BUSH.get(), createLargeBlueberryBushDrops());
        add(ModBlocks.STRAWBERRY_PLANT.get(), createStrawberryCropDrops());
        add(ModBlocks.STRAWBERRY_CAKE.get(), LootTable.lootTable());
        add(ModBlocks.STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.CANDLE));
        add(ModBlocks.WHITE_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.white()));
        add(ModBlocks.ORANGE_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.orange()));
        add(ModBlocks.MAGENTA_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.magenta()));
        add(ModBlocks.LIGHT_BLUE_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.lightBlue()));
        add(ModBlocks.YELLOW_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.yellow()));
        add(ModBlocks.LIME_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.lime()));
        add(ModBlocks.PINK_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.pink()));
        add(ModBlocks.GRAY_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.gray()));
        add(ModBlocks.LIGHT_GRAY_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.lightGray()));
        add(ModBlocks.CYAN_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.cyan()));
        add(ModBlocks.PURPLE_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.purple()));
        add(ModBlocks.BLUE_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.blue()));
        add(ModBlocks.BROWN_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.brown()));
        add(ModBlocks.GREEN_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.green()));
        add(ModBlocks.RED_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.red()));
        add(ModBlocks.BLACK_STRAWBERRY_CANDLE_CAKE.get(), createSingleItemTable(Items.DYED_CANDLE.black()));
        add(ModBlocks.FRUITING_CHERRY_LEAVES.get(), createFruitingCherryLeavesDrops());
        add(ModBlocks.FRUITING_OAK_LEAVES.get(), createFruitingOakLeavesDrops());
        dropSelf(ModBlocks.CRATE.get());

        dropPottedContents(ModBlocks.POTTED_CHERRY_PIT.get());
        dropPottedContents(ModBlocks.POTTED_APPLE_CORE.get());

        dropSelf(ModBlocks.GIANT_CACTUS_BASE.get());
        dropSelf(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get());
        dropSelf(ModBlocks.GIANT_CACTUS_STEM.get());
        dropSelf(ModBlocks.CACTUS_THORN.get());
        dropSelf(ModBlocks.GIANT_CACTUS_BLOSSOM.get());

        add(ModBlocks.GIANT_CACTUS_WOOD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1))
                        .add(LootItem.lootTableItem(ModBlocks.GIANT_CACTUS_BASE.get())
                                .when(MatchBlock.blockMatches(blocks, ModBlocks.GIANT_CACTUS_WOOD.get(), StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(GiantCactusWoodBlock.GENERATED, true))))
                        .add(LootItem.lootTableItem(ModBlocks.GIANT_CACTUS_WOOD.get())
                                .when(MatchBlock.blockMatches(blocks, ModBlocks.GIANT_CACTUS_WOOD.get(), StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(GiantCactusWoodBlock.GENERATED, false))))
                        .when(ExplosionCondition.survivesExplosion())));

        add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1))
                        .add(LootItem.lootTableItem(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get())
                                .when(MatchBlock.blockMatches(blocks, ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(), StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(GiantCactusWoodBlock.GENERATED, true))))
                        .add(LootItem.lootTableItem(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get())
                                .when(MatchBlock.blockMatches(blocks, ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get(), StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(GiantCactusWoodBlock.GENERATED, false))))
                        .when(ExplosionCondition.survivesExplosion())));

        dropSelf(ModBlocks.CACTUS_PLANKS.get());
        dropSelf(ModBlocks.CACTUS_MOSAIC.get());
        dropSelf(ModBlocks.CACTUS_STAIRS.get());
        add(ModBlocks.CACTUS_SLAB.get(), createSlabItemTable(ModBlocks.CACTUS_SLAB.get()));
        dropSelf(ModBlocks.CACTUS_FENCE.get());
        dropSelf(ModBlocks.CACTUS_FENCE_GATE.get());
        dropSelf(ModBlocks.CACTUS_BUTTON.get());
        dropSelf(ModBlocks.CACTUS_PRESSURE_PLATE.get());
        add(ModBlocks.CACTUS_DOOR.get(), createDoorTable(ModBlocks.CACTUS_DOOR.get()));
        dropSelf(ModBlocks.CACTUS_TRAPDOOR.get());
        dropSelf(ModBlocks.DESERT_MOSS.get());

        add(ModBlocks.CACTUS_SIGN.get(), createSingleItemTable(ModItems.CACTUS_SIGN.get()));
        add(ModBlocks.CACTUS_WALL_SIGN.get(), createSingleItemTable(ModItems.CACTUS_SIGN.get()));

        add(ModBlocks.CACTUS_HANGING_SIGN.get(), createSingleItemTable(ModItems.CACTUS_HANGING_SIGN.get()));
        add(ModBlocks.CACTUS_WALL_HANGING_SIGN.get(), createSingleItemTable(ModItems.CACTUS_HANGING_SIGN.get()));

        add(ModBlocks.CACTUS_CLUSTER.get(), createCactusClusterDrops());

        add(ModBlocks.OPUNTIA_CACTUS.get(), createOpuntiaCactusDrops());
    }

    private LootTable.Builder createOpuntiaCactusDrops() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.OPUNTIA_CACTUS.get(),
                                LootItem.lootTableItem(ModItems.PRICKLY_PEAR.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.OPUNTIA_CACTUS.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(CactusFruitPlantBlock.AGE, 1)))
                                        .apply(SetItemCountFunction.setCount(uniform(1.0F, 2.0F)))
                                        .apply(ApplyExplosionDecay.explosionDecay()))));
    }

    private LootTable.Builder layeredDrops(Block block, net.minecraft.world.level.block.state.properties.IntegerProperty amount) {
        var table = LootTable.lootTable();
        for (int count : amount.getPossibleValues()) {
            table.withPool(LootPool.lootPool().setRolls(constant(1))
                    .when(MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties().hasProperty(amount, count)))
                    .add(LootItem.lootTableItem(block).apply(SetItemCountFunction.setCount(constant(count)))
                            .apply(ApplyExplosionDecay.explosionDecay())));
        }
        return table;
    }

    private LootTable.Builder flaxDrops() {
        var block = ModBlocks.FLAX_CROP.get();
        var lower = MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties()
                .hasProperty(FlaxCropBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER));
        var mature = MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties()
                .hasProperty(FlaxCropBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER)
                .hasProperty(FlaxCropBlock.AGE, 4));
        var table = LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(constant(1)).when(lower).when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(ModItems.FLAX_SEED.get())))
                .withPool(LootPool.lootPool().setRolls(constant(1)).when(mature).when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(ModItems.FLAX_SEED.get()).apply(SetItemCountFunction.setCount(uniform(1, 2)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool().setRolls(constant(1)).when(mature)
                        .add(LootItem.lootTableItem(ModItems.FLAX_FIBER.get()).apply(SetItemCountFunction.setCount(uniform(2, 4)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool().setRolls(constant(1)).when(mature)
                        .add(LootItem.lootTableItem(ModItems.FLAX_FLOWER.get()).apply(SetItemCountFunction.setCount(uniform(1, 2)))
                                .apply(ApplyExplosionDecay.explosionDecay())));
        for (int age = 2; age <= 3; age++) {
            table.withPool(LootPool.lootPool().setRolls(constant(1)).when(lower)
                    .when(MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties().hasProperty(FlaxCropBlock.AGE, age)))
                    .when(ExplosionCondition.survivesExplosion())
                    .add(LootItem.lootTableItem(ModItems.FLAX_FIBER.get())
                            .when(net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition.randomChance(age == 2 ? 0.10F : 0.25F))
                            .apply(ApplyExplosionDecay.explosionDecay())));
        }
        return table;
    }

    private LootTable.Builder createBlueberryBushDrops(Block block, Holder<ContextIntProvider> ripeCount) {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(block,
                                LootItem.lootTableItem(ModItems.BLUEBERRIES.get())
                                        .when(MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(SmallBlueberryBushBlock.AGE, 0)))
                                        .apply(SetItemCountFunction.setCount(constant(1.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(block,
                                LootItem.lootTableItem(ModItems.BLUEBERRIES.get())
                                        .when(MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(SmallBlueberryBushBlock.AGE, 1)))
                                        .apply(SetItemCountFunction.setCount(constant(1.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(block,
                                LootItem.lootTableItem(ModItems.BLUEBERRIES.get())
                                        .when(MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(SmallBlueberryBushBlock.AGE, 2)))
                                        .apply(SetItemCountFunction.setCount(constant(1.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(block,
                                LootItem.lootTableItem(ModItems.BLUEBERRIES.get())
                                        .when(MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(SmallBlueberryBushBlock.AGE, 3)))
                                        .apply(SetItemCountFunction.setCount(ripeCount))
                                        .apply(ApplyExplosionDecay.explosionDecay()))));
    }

    private LootTable.Builder createStrawberryCropDrops() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.STRAWBERRY_PLANT.get(),
                                LootItem.lootTableItem(ModItems.STRAWBERRY.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.STRAWBERRY_PLANT.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(StrawberryCropBlock.AGE, 3)))
                                        .apply(SetItemCountFunction.setCount(uniform(2.0F, 4.0F)))
                                        .apply(ApplyExplosionDecay.explosionDecay()))));
    }

    private LootTable.Builder createCactusClusterDrops() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.CACTUS_CLUSTER.get(),
                                LootItem.lootTableItem(ModBlocks.CACTUS_CLUSTER.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.CACTUS_CLUSTER.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(CactusClusterBlock.PICKLES, 1)))
                                        .apply(SetItemCountFunction.setCount(constant(1.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.CACTUS_CLUSTER.get(),
                                LootItem.lootTableItem(ModBlocks.CACTUS_CLUSTER.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.CACTUS_CLUSTER.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(CactusClusterBlock.PICKLES, 2)))
                                        .apply(SetItemCountFunction.setCount(constant(2.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.CACTUS_CLUSTER.get(),
                                LootItem.lootTableItem(ModBlocks.CACTUS_CLUSTER.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.CACTUS_CLUSTER.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(CactusClusterBlock.PICKLES, 3)))
                                        .apply(SetItemCountFunction.setCount(constant(3.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.CACTUS_CLUSTER.get(),
                                LootItem.lootTableItem(ModBlocks.CACTUS_CLUSTER.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.CACTUS_CLUSTER.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(CactusClusterBlock.PICKLES, 4)))
                                        .apply(SetItemCountFunction.setCount(constant(4.0F))))));
    }

    private LootTable.Builder createShearsOnlyDrop(Block block) {
        return createShearsDispatchTable(
                block,
                applyExplosionCondition(block, LootItem.lootTableItem(block))
        );
    }

    private LootTable.Builder createLargeBlueberryBushDrops() {
        var itemLookup = output.lookup(Registries.ITEM);

        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.LARGE_BLUEBERRY_BUSH.get(),
                                LootItem.lootTableItem(ModBlocks.LARGE_BLUEBERRY_BUSH.get())
                                        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(itemLookup, Items.SHEARS))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .when(InvertedLootItemCondition.invert(MatchTool.toolMatches(ItemPredicate.Builder.item().of(itemLookup, Items.SHEARS))))
                        .add(applyExplosionCondition(ModBlocks.LARGE_BLUEBERRY_BUSH.get(),
                                LootItem.lootTableItem(ModItems.TWIG.get())
                                        .apply(SetItemCountFunction.setCount(uniform(1.0F, 2.0F))))));
    }

    private LootTable.Builder createFruitingCherryLeavesDrops() {
        var enchantmentLookup = output.lookup(Registries.ENCHANTMENT);
        var fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);

        return createSilkTouchOrShearsDispatchTable(
                ModBlocks.FRUITING_CHERRY_LEAVES.get(),
                LootItem.lootTableItem(Items.CHERRY_SAPLING)
                        .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                fortune,
                                0.05F, 0.0625F, 0.083333336F, 0.1F
                        ))
                        .otherwise(LootItem.lootTableItem(Items.STICK)
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                        fortune,
                                        0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F
                                )))
        )
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .when(MatchBlock.blockMatches(blocks, ModBlocks.FRUITING_CHERRY_LEAVES.get(), StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FruitingCherryLeavesBlock.AGE, 1)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(constant(1.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .when(MatchBlock.blockMatches(blocks, ModBlocks.FRUITING_CHERRY_LEAVES.get(), StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FruitingCherryLeavesBlock.AGE, 2)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(uniform(1.0F, 2.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .when(MatchBlock.blockMatches(blocks, ModBlocks.FRUITING_CHERRY_LEAVES.get(), StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FruitingCherryLeavesBlock.AGE, 3)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(constant(2.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())));
    }

    private LootTable.Builder createFruitingOakLeavesDrops() {
        var enchantmentLookup = output.lookup(Registries.ENCHANTMENT);
        var fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);

        return createSilkTouchOrShearsDispatchTable(
                ModBlocks.FRUITING_OAK_LEAVES.get(),
                LootItem.lootTableItem(Items.OAK_SAPLING)
                        .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                fortune,
                                0.05F, 0.0625F, 0.083333336F, 0.1F
                        ))
                        .otherwise(LootItem.lootTableItem(Items.STICK)
                                .when(BonusLevelTableCondition.bonusLevelFlatChance(
                                        fortune,
                                        0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F
                                )))
        )
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .when(MatchBlock.blockMatches(blocks, ModBlocks.FRUITING_OAK_LEAVES.get(), StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FruitingOakLeavesBlock.AGE, 2)))
                        .add(LootItem.lootTableItem(Items.APPLE)
                                .apply(SetItemCountFunction.setCount(constant(1.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(entry -> (Block) entry.get()).toList();
    }
}


