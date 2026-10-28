package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.common.block.*;
import net.minecraft.advancements.predicates.ItemPredicate;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
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
        dropSelf(ModBlocks.LINEN_BALE.get());
        dropSelf(ModBlocks.LINEN_CARPET.get());
        dropSelf(ModBlocks.PACKED_PEBBLES.get());
        dropSelf(ModBlocks.TWIG_LADDER.get());
        dropPottedContents(ModBlocks.POTTED_GIANT_CACTUS_STEM.get());
        add(ModBlocks.NEEDLE_LITTER.get(), layeredDrops(ModBlocks.NEEDLE_LITTER.get(), NeedleLitterBlock.AMOUNT));
        add(ModBlocks.SCATTERED_PEBBLES.get(), layeredDrops(ModBlocks.SCATTERED_PEBBLES.get(), ScatteredPebblesBlock.AMOUNT));
        add(ModBlocks.FLAX.get(), flaxDrops());
        add(ModBlocks.BLUEBERRY_BUSH.get(), createBlueberryBushDrops(ModBlocks.BLUEBERRY_BUSH.get(), uniform(2.0F, 3.0F)));
        add(ModBlocks.LARGE_BLUEBERRY_BUSH.get(), createLargeBlueberryBushDrops());
        add(ModBlocks.STRAWBERRY_PLANT.get(), createStrawberryCropDrops());
        add(ModBlocks.STRAWBERRY_CAKE.get(), LootTable.lootTable());
        ModBlocks.STRAWBERRY_CANDLE_CAKES.forEach(cake -> add(cake.get(), createSingleItemTable(cake.get().getCandle())));
        add(ModBlocks.ORCHARD_CHERRY_LEAVES.get(), createOrchardCherryLeavesDrops());
        add(ModBlocks.APPLE_LEAVES.get(), createAppleLeavesDrops());
        dropSelf(ModBlocks.CRATE.get());

        dropPottedContents(ModBlocks.POTTED_CHERRY_PIT.get());
        dropPottedContents(ModBlocks.POTTED_APPLE_CORE.get());

        dropSelf(ModBlocks.CACTUS_LOG.get());
        dropSelf(ModBlocks.STRIPPED_CACTUS_LOG.get());
        dropSelf(ModBlocks.GIANT_CACTUS_STEM.get());
        dropSelf(ModBlocks.CACTUS_SPINE.get());
        dropSelf(ModBlocks.GIANT_CACTUS_SHOOT.get());

        add(ModBlocks.CACTUS_WOOD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1))
                        .add(LootItem.lootTableItem(ModBlocks.CACTUS_LOG.get())
                                .when(MatchBlock.blockMatches(blocks, ModBlocks.CACTUS_WOOD.get(), StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(CactusWoodBlock.GENERATED, true))))
                        .add(LootItem.lootTableItem(ModBlocks.CACTUS_WOOD.get())
                                .when(MatchBlock.blockMatches(blocks, ModBlocks.CACTUS_WOOD.get(), StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(CactusWoodBlock.GENERATED, false))))
                        .when(ExplosionCondition.survivesExplosion())));

        add(ModBlocks.STRIPPED_CACTUS_WOOD.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1))
                        .add(LootItem.lootTableItem(ModBlocks.STRIPPED_CACTUS_LOG.get())
                                .when(MatchBlock.blockMatches(blocks, ModBlocks.STRIPPED_CACTUS_WOOD.get(), StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(CactusWoodBlock.GENERATED, true))))
                        .add(LootItem.lootTableItem(ModBlocks.STRIPPED_CACTUS_WOOD.get())
                                .when(MatchBlock.blockMatches(blocks, ModBlocks.STRIPPED_CACTUS_WOOD.get(), StatePropertiesPredicate.Builder.properties()
                                                .hasProperty(CactusWoodBlock.GENERATED, false))))
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

        add(ModBlocks.SMALL_CACTUS.get(), createSmallCactusDrops());

        add(ModBlocks.PRICKLY_PEAR_CACTUS.get(), createPricklyPearCactusDrops());
    }

    private LootTable.Builder createPricklyPearCactusDrops() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.PRICKLY_PEAR_CACTUS.get(),
                                LootItem.lootTableItem(ModItems.PRICKLY_PEAR.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.PRICKLY_PEAR_CACTUS.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(PricklyPearCactusBlock.AGE, 1)))
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
        var block = ModBlocks.FLAX.get();
        var lower = MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties()
                .hasProperty(FlaxBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER));
        var mature = MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties()
                .hasProperty(FlaxBlock.HALF, net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER)
                .hasProperty(FlaxBlock.AGE, 4));
        var table = LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(constant(1)).when(lower).when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(ModItems.FLAX_SEEDS.get())))
                .withPool(LootPool.lootPool().setRolls(constant(1)).when(mature).when(ExplosionCondition.survivesExplosion())
                        .add(LootItem.lootTableItem(ModItems.FLAX_SEEDS.get()).apply(SetItemCountFunction.setCount(uniform(1, 2)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool().setRolls(constant(1)).when(mature)
                        .add(LootItem.lootTableItem(ModItems.FLAX_FIBER.get()).apply(SetItemCountFunction.setCount(uniform(2, 4)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool().setRolls(constant(1)).when(mature)
                        .add(LootItem.lootTableItem(ModItems.FLAX_FLOWER.get()).apply(SetItemCountFunction.setCount(uniform(1, 2)))
                                .apply(ApplyExplosionDecay.explosionDecay())));
        for (int age = 2; age <= 3; age++) {
            table.withPool(LootPool.lootPool().setRolls(constant(1)).when(lower)
                    .when(MatchBlock.blockMatches(blocks, block, StatePropertiesPredicate.Builder.properties().hasProperty(FlaxBlock.AGE, age)))
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

    private LootTable.Builder createSmallCactusDrops() {
        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.SMALL_CACTUS.get(),
                                LootItem.lootTableItem(ModBlocks.SMALL_CACTUS.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.SMALL_CACTUS.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(SmallCactusBlock.PICKLES, 1)))
                                        .apply(SetItemCountFunction.setCount(constant(1.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.SMALL_CACTUS.get(),
                                LootItem.lootTableItem(ModBlocks.SMALL_CACTUS.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.SMALL_CACTUS.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(SmallCactusBlock.PICKLES, 2)))
                                        .apply(SetItemCountFunction.setCount(constant(2.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.SMALL_CACTUS.get(),
                                LootItem.lootTableItem(ModBlocks.SMALL_CACTUS.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.SMALL_CACTUS.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(SmallCactusBlock.PICKLES, 3)))
                                        .apply(SetItemCountFunction.setCount(constant(3.0F))))))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .add(applyExplosionCondition(ModBlocks.SMALL_CACTUS.get(),
                                LootItem.lootTableItem(ModBlocks.SMALL_CACTUS.get())
                                        .when(MatchBlock.blockMatches(blocks, ModBlocks.SMALL_CACTUS.get(), StatePropertiesPredicate.Builder.properties()
                                                        .hasProperty(SmallCactusBlock.PICKLES, 4)))
                                        .apply(SetItemCountFunction.setCount(constant(4.0F))))));
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

    private LootTable.Builder createOrchardCherryLeavesDrops() {
        var enchantmentLookup = output.lookup(Registries.ENCHANTMENT);
        var fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);

        return createSilkTouchOrShearsDispatchTable(
                ModBlocks.ORCHARD_CHERRY_LEAVES.get(),
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
                        .when(MatchBlock.blockMatches(blocks, ModBlocks.ORCHARD_CHERRY_LEAVES.get(), StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(OrchardCherryLeavesBlock.AGE, 1)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(constant(1.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .when(MatchBlock.blockMatches(blocks, ModBlocks.ORCHARD_CHERRY_LEAVES.get(), StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(OrchardCherryLeavesBlock.AGE, 2)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(uniform(1.0F, 2.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool()
                        .setRolls(constant(1.0F))
                        .when(MatchBlock.blockMatches(blocks, ModBlocks.ORCHARD_CHERRY_LEAVES.get(), StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(OrchardCherryLeavesBlock.AGE, 3)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(constant(2.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())));
    }

    private LootTable.Builder createAppleLeavesDrops() {
        var enchantmentLookup = output.lookup(Registries.ENCHANTMENT);
        var fortune = enchantmentLookup.getOrThrow(Enchantments.FORTUNE);

        return createSilkTouchOrShearsDispatchTable(
                ModBlocks.APPLE_LEAVES.get(),
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
                        .when(MatchBlock.blockMatches(blocks, ModBlocks.APPLE_LEAVES.get(), StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(AppleLeavesBlock.AGE, 2)))
                        .add(LootItem.lootTableItem(Items.APPLE)
                                .apply(SetItemCountFunction.setCount(constant(1.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(entry -> (Block) entry.get()).toList();
    }
}
