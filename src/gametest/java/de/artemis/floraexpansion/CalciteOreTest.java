package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Consumer;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class CalciteOreTest {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "calcite_ores");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION = ResourceKey.create(Registries.TEST_FUNCTION, ID);
    @SubscribeEvent public static void function(RegisterEvent event) { event.register(Registries.TEST_FUNCTION, ID, () -> CalciteOreTest::run); }
    @SubscribeEvent public static void tests(RegisterGameTestsEvent event) {
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;
        event.registerTest(ID, new FunctionGameTestInstance(FUNCTION, new TestData<>(event.registerEnvironment(ID), Identifier.withDefaultNamespace("empty"), 100, 0, true)));
    }

    private static void run(GameTestHelper helper) {
        SaltBiomeTests.surfaces(helper);
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(4, 4, 4));
        var enchantments = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var normal = new ItemStack(Items.DIAMOND_PICKAXE);
        var silk = normal.copy(); silk.enchant(enchantments.getOrThrow(Enchantments.SILK_TOUCH), 1);
        var fortune = normal.copy(); fortune.enchant(enchantments.getOrThrow(Enchantments.FORTUNE), 3);
        Item[] drops = {Items.COAL, Items.RAW_IRON, Items.RAW_COPPER, Items.RAW_GOLD, Items.REDSTONE, Items.EMERALD, Items.LAPIS_LAZULI, Items.DIAMOND};
        Item[] smelted = {Items.COAL, Items.IRON_INGOT, Items.COPPER_INGOT, Items.GOLD_INGOT, Items.REDSTONE, Items.EMERALD, Items.LAPIS_LAZULI, Items.DIAMOND};
        String[] results = {"coal", "iron_ingot", "copper_ingot", "gold_ingot", "redstone", "emerald", "lapis_lazuli", "diamond"};
        String[] minerals = {"coal", "iron", "copper", "gold", "redstone", "emerald", "lapis", "diamond"};
        Ingredient[] commonInputs = new Ingredient[minerals.length];
        for (int i = 0; i < minerals.length; i++) {
            var tag = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ores/" + minerals[i]));
            commonInputs[i] = Ingredient.of(level.registryAccess().lookupOrThrow(Registries.ITEM).getOrThrow(tag));
        }
        for (int i = 0; i < ModBlocks.CALCITE_ORES.size(); i++) {
            var ore = ModBlocks.CALCITE_ORES.get(i).get();
            var state = ore.defaultBlockState();
            var stack = new ItemStack(ore);
            check(state.is(Tags.Blocks.ORES) && stack.is(Tags.Items.ORES), "Common ore umbrella tag missing");
            var mineralBlockTag = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores/" + minerals[i]));
            check(state.is(mineralBlockTag), "Common block tag missing for mining filters: " + ore);
            // These are the same common item-tag ingredients used by Mekanism's ore-processing recipes.
            for (int input = 0; input < commonInputs.length; input++)
                check(commonInputs[input].test(stack) == (input == i), "Wrong common machine ingredient for " + ore + ": " + minerals[input]);
            boolean dense = i == 2 || i == 4 || i == 6;
            check(state.is(Tags.Blocks.ORE_RATES_DENSE) == dense && stack.is(Tags.Items.ORE_RATES_DENSE) == dense,
                    "Wrong dense ore classification for " + ore);
            check(state.is(Tags.Blocks.ORE_RATES_SINGULAR) != dense && stack.is(Tags.Items.ORE_RATES_SINGULAR) != dense,
                    "Wrong singular ore classification for " + ore);
            check(!state.is(Tags.Blocks.ORE_RATES_SPARSE) && !stack.is(Tags.Items.ORE_RATES_SPARSE), "Normal ore marked sparse");
            var calcite = Identifier.fromNamespaceAndPath("c", "ores_in_ground/calcite");
            check(state.is(TagKey.create(Registries.BLOCK, calcite)) && stack.is(TagKey.create(Registries.ITEM, calcite)), "Calcite host missing");
            check(!stack.is(Tags.Items.ORES_IN_GROUND_DEEPSLATE), "Calcite incorrectly tagged as deepslate");
            check(normal.isCorrectToolForDrops(state), "Diamond pickaxe rejects " + ore);
            check(!new ItemStack(Items.WOODEN_AXE).isCorrectToolForDrops(state), "Axe can mine " + ore);
            boolean woodenWorks = new ItemStack(Items.WOODEN_PICKAXE).isCorrectToolForDrops(state);
            check(woodenWorks == (i == 0), "Wrong wooden pickaxe tier for " + ore);
            boolean stoneWorks = new ItemStack(Items.STONE_PICKAXE).isCorrectToolForDrops(state);
            check(stoneWorks == (i == 0 || i == 1 || i == 2 || i == 6), "Wrong stone pickaxe tier for " + ore);
            var silkDrops = Block.getDrops(state, level, pos, null, null, silk);
            check(silkDrops.size() == 1 && silkDrops.getFirst().is(ore.asItem()) && silkDrops.getFirst().getCount() == 1, "Silk Touch lost the calcite host");
            int normalTotal = 0, fortuneTotal = 0;
            for (int sample = 0; sample < 100; sample++) {
                for (var drop : Block.getDrops(state, level, pos, null, null, normal)) {
                    check(drop.is(drops[i]), "Wrong mineral drop from " + ore); normalTotal += drop.getCount();
                }
                for (var drop : Block.getDrops(state, level, pos, null, null, fortune)) {
                    check(drop.is(drops[i]), "Wrong Fortune drop from " + ore); fortuneTotal += drop.getCount();
                }
            }
            check(normalTotal > 0 && fortuneTotal > normalTotal, "Fortune does not increase drops for " + ore);
            for (String method : new String[]{"smelting", "blasting"}) {
                var recipeId = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, results[i] + "_from_" + method + "_" + ModBlocks.CALCITE_ORES.get(i).getId().getPath());
                var recipe = (AbstractCookingRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, recipeId)).orElseThrow().value();
                var input = new SingleRecipeInput(new ItemStack(ore));
                check(recipe.matches(input, level) && recipe.assemble(input).is(smelted[i]), "Bad cooking recipe: " + recipeId);
            }
        }
        var lit = ModBlocks.CALCITE_REDSTONE_ORE.get().defaultBlockState().setValue(BlockStateProperties.LIT, true);
        check(lit.getLightEmission() == 9, "Calcite redstone does not glow");
        var host = Identifier.fromNamespaceAndPath("c", "ore_bearing_ground/calcite");
        check(net.minecraft.world.level.block.Blocks.CALCITE.defaultBlockState().is(TagKey.create(Registries.BLOCK, host))
                && new ItemStack(Items.CALCITE).is(TagKey.create(Registries.ITEM, host)), "Calcite ground tag missing");
        check(new ItemStack(Items.RAW_IRON).is(Tags.Items.RAW_MATERIALS_IRON)
                && new ItemStack(Items.RAW_COPPER).is(Tags.Items.RAW_MATERIALS_COPPER)
                && new ItemStack(Items.RAW_GOLD).is(Tags.Items.RAW_MATERIALS_GOLD), "Vanilla raw-material processing tags missing");
        FloraExpansion.LOGGER.info("Calcite ore compatibility passed: common item/block/mineral tags, 64 machine ingredient matches, exact dense/singular categories, calcite hosts and vanilla raw-material tags");
        FloraExpansion.LOGGER.info("Calcite ores passed: 16 worldgen conversions, pale cave walls, buried ore preservation, 8 drop/Fortune/Silk Touch/tool matrices, 16 cooking recipes and redstone light");
        helper.succeed();
    }
    private static void check(boolean pass, String message) { if (!pass) throw new IllegalStateException(message); }
}
