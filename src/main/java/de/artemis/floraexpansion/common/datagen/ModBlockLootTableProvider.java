package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.common.block.FruitingCherryLeavesBlock;
import de.artemis.floraexpansion.common.block.FruitingOakLeavesBlock;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
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
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        add(ModBlocks.FRUITING_CHERRY_LEAVES.get(), createFruitingCherryLeavesDrops());
        add(ModBlocks.FRUITING_OAK_LEAVES.get(), createFruitingOakLeavesDrops());

        dropPottedContents(ModBlocks.POTTED_CHERRY_PIT.get());
        dropPottedContents(ModBlocks.POTTED_APPLE_CORE.get());
    }

    private LootTable.Builder createFruitingCherryLeavesDrops() {
        var enchantmentLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
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
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FRUITING_CHERRY_LEAVES.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FruitingCherryLeavesBlock.AGE, 1)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FRUITING_CHERRY_LEAVES.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FruitingCherryLeavesBlock.AGE, 2)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FRUITING_CHERRY_LEAVES.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FruitingCherryLeavesBlock.AGE, 3)))
                        .add(LootItem.lootTableItem(ModItems.CHERRIES.get())
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())));
    }

    private LootTable.Builder createFruitingOakLeavesDrops() {
        var enchantmentLookup = registries.lookupOrThrow(Registries.ENCHANTMENT);
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
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.FRUITING_OAK_LEAVES.get())
                                .setProperties(StatePropertiesPredicate.Builder.properties()
                                        .hasProperty(FruitingOakLeavesBlock.AGE, 2)))
                        .add(LootItem.lootTableItem(Items.APPLE)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F)))
                                .apply(ApplyExplosionDecay.explosionDecay())));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return List.of(
                ModBlocks.FRUITING_CHERRY_LEAVES.get(),
                ModBlocks.FRUITING_OAK_LEAVES.get(),
                ModBlocks.POTTED_CHERRY_PIT.get(),
                ModBlocks.POTTED_APPLE_CORE.get()
        );
    }
}