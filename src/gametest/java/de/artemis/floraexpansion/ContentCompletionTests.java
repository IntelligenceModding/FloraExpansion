package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

final class ContentCompletionTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var dye = (ShapelessRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE,
                id("pink_dye_from_prickly_pear"))).orElseThrow().value();
        var dyeInput = CraftingInput.of(1, 1, List.of(new ItemStack(ModItems.PRICKLY_PEAR.get())));
        check(dye.matches(dyeInput, level) && dye.assemble(dyeInput).is(Items.DYE.pink())
                && dye.assemble(dyeInput).getCount() == 1, "One prickly pear must craft one pink dye");
        check(!dye.matches(CraftingInput.of(1, 1, List.of(new ItemStack(ModItems.STRAWBERRY.get()))), level),
                "Prickly pear dye recipe accepts other fruit");
        var berries = (ShapelessRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id("mixed_berries"))).orElseThrow().value();
        for (var extra : List.of(Items.SWEET_BERRIES, Items.GLOW_BERRIES, ModItems.BLUEBERRIES.get(), ModItems.STRAWBERRY.get())) {
            var input = CraftingInput.of(3, 1, List.of(new ItemStack(ModItems.BLUEBERRIES.get()), new ItemStack(ModItems.STRAWBERRY.get()), new ItemStack(extra)));
            check(berries.matches(input, level) && berries.assemble(input).is(ModItems.MIXED_BERRIES.get())
                    && berries.assemble(input).getCount() == 3, "Actual berry combination did not craft three servings");
        }
        var cherries = CraftingInput.of(3, 1, List.of(new ItemStack(ModItems.BLUEBERRIES.get()), new ItemStack(ModItems.STRAWBERRY.get()), new ItemStack(ModItems.CHERRIES.get())));
        check(!berries.matches(cherries, level), "Mixed berries retained cherry ingredient");
        var sameBerry = CraftingInput.of(3, 1, List.of(new ItemStack(ModItems.BLUEBERRIES.get()), new ItemStack(ModItems.BLUEBERRIES.get()), new ItemStack(ModItems.BLUEBERRIES.get())));
        check(!berries.matches(sameBerry, level), "Mixed berries no longer requires a mixture");
        var juice = (ShapelessRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id("strawberry_juice"))).orElseThrow().value();
        var juiceInput = CraftingInput.of(2, 2, List.of(new ItemStack(ModItems.STRAWBERRY.get()), new ItemStack(ModItems.STRAWBERRY.get()),
                new ItemStack(ModItems.STRAWBERRY.get()), new ItemStack(Items.GLASS_BOTTLE)));
        check(juice.matches(juiceInput, level) && juice.assemble(juiceInput).is(ModItems.STRAWBERRY_JUICE.get())
                && juice.assemble(juiceInput).getCount() == 1, "Strawberry juice recipe failed");
        var slab = (ShapedRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id("cactus_mosaic_slab"))).orElseThrow().value();
        var mosaic = new ItemStack(ModBlocks.CACTUS_MOSAIC.get());
        var slabInput = CraftingInput.of(3, 1, List.of(mosaic.copy(), mosaic.copy(), mosaic.copy()));
        check(slab.matches(slabInput, level) && slab.assemble(slabInput).getCount() == 6
                && slab.assemble(slabInput).is(ModBlocks.CACTUS_MOSAIC_SLAB.get().asItem()), "Mosaic slab recipe failed");
        var stairs = (ShapedRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id("cactus_mosaic_stairs"))).orElseThrow().value();
        var stairInput = CraftingInput.of(3, 3, List.of(mosaic.copy(), ItemStack.EMPTY, ItemStack.EMPTY,
                mosaic.copy(), mosaic.copy(), ItemStack.EMPTY, mosaic.copy(), mosaic.copy(), mosaic.copy()));
        check(stairs.matches(stairInput, level) && stairs.assemble(stairInput).getCount() == 4
                && stairs.assemble(stairInput).is(ModBlocks.CACTUS_MOSAIC_STAIRS.get().asItem()), "Mosaic stair recipe failed");
        var doubleSlab = ModBlocks.CACTUS_MOSAIC_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        var drops = Block.getDrops(doubleSlab, level, helper.absolutePos(new BlockPos(1, 2, 1)), null);
        check(drops.stream().filter(s -> s.is(ModBlocks.CACTUS_MOSAIC_SLAB.get().asItem())).mapToInt(ItemStack::getCount).sum() == 2,
                "Double mosaic slab did not return both slabs");
        check(new ItemStack(ModBlocks.CACTUS_MOSAIC_SLAB.get()).is(ItemTags.WOODEN_SLABS)
                && new ItemStack(ModBlocks.CACTUS_MOSAIC_STAIRS.get()).is(ItemTags.WOODEN_STAIRS), "Mosaic variants missing item tags");
        check(doubleSlab.is(BlockTags.MINEABLE_WITH_AXE) && ModBlocks.CACTUS_MOSAIC_STAIRS.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE),
                "Mosaic variants missing axe behavior");

        var lookup = level.getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.VILLAGER_TRADE);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(player.blockPosition()))
                .withParameter(LootContextParams.THIS_ENTITY, player).withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, net.minecraft.util.Unit.INSTANCE)
                .create(LootContextParamSets.VILLAGER_TRADE);
        var context = new LootContext.Builder(params).create(Optional.empty());
        var jams = List.of(ModItems.BLUEBERRY_JAM.get(), ModItems.STRAWBERRY_JAM.get(), ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get());
        String[] flavors = {"blueberry", "strawberry", "cherry", "apple"};
        for (int i = 0; i < flavors.length; i++) for (String merchant : List.of("farmer/3/", "wandering_trader/")) {
            var key = ResourceKey.create(Registries.VILLAGER_TRADE, id(merchant + "emerald_" + flavors[i] + "_jam"));
            var trade = lookup.getOrThrow(key);
            var offer = trade.value().getOffer(context);
            check(offer != null && offer.getResult().is(jams.get(i)) && offer.getResult().getCount() == 1
                    && offer.getBaseCostA().is(Items.EMERALD) && offer.getBaseCostA().getCount() == 3, "Inconsistent jam trade: " + key);
            var tag = TagKey.create(Registries.VILLAGER_TRADE, Identifier.withDefaultNamespace(merchant.startsWith("farmer")
                    ? "farmer/level_3" : "wandering_trader/uncommon"));
            check(trade.is(tag), "Jam trade is registered but absent from merchant's trade pool");
        }

        record TradeCheck(String path, String pool, net.minecraft.world.level.ItemLike item, int cost, int count, boolean buys) {}
        var additions = List.of(
                new TradeCheck("wandering_trader/emerald_flax_seeds", "wandering_trader/common", ModItems.FLAX_SEEDS.get(), 1, 1, false),
                new TradeCheck("wandering_trader/emerald_cherry_pit", "wandering_trader/common", ModBlocks.CHERRY_PIT.get(), 5, 1, false),
                new TradeCheck("wandering_trader/emerald_apple_core", "wandering_trader/common", ModBlocks.APPLE_CORE.get(), 5, 1, false),
                new TradeCheck("wandering_trader/emerald_giant_cactus_shoot", "wandering_trader/common", ModBlocks.GIANT_CACTUS_SHOOT.get(), 3, 1, false),
                new TradeCheck("wandering_trader/emerald_prickly_pear", "wandering_trader/common", ModItems.PRICKLY_PEAR.get(), 1, 3, false),
                new TradeCheck("wandering_trader/emerald_small_cactus", "wandering_trader/common", ModBlocks.SMALL_CACTUS.get(), 3, 1, false),
                new TradeCheck("wandering_trader/emerald_desert_moss", "wandering_trader/common", ModBlocks.DESERT_MOSS.get(), 1, 2, false),
                new TradeCheck("farmer/1/cherries_emerald", "farmer/level_1", ModItems.CHERRIES.get(), 16, 1, true),
                new TradeCheck("farmer/1/prickly_pear_emerald", "farmer/level_1", ModItems.PRICKLY_PEAR.get(), 16, 1, true),
                new TradeCheck("farmer/4/emerald_strawberry_cake", "farmer/level_4", ModBlocks.STRAWBERRY_CAKE.get(), 3, 1, false),
                new TradeCheck("shepherd/3/emerald_basket", "shepherd/level_3", ModItems.BASKET.get(), 2, 1, false),
                new TradeCheck("leatherworker/3/emerald_spiked_leather_helmet", "leatherworker/level_3", ModItems.SPIKED_LEATHER_HELMET.get(), 8, 1, false));
        for (var expected : additions) {
            var trade = lookup.getOrThrow(ResourceKey.create(Registries.VILLAGER_TRADE, id(expected.path())));
            check(trade.is(TagKey.create(Registries.VILLAGER_TRADE, Identifier.withDefaultNamespace(expected.pool()))),
                    "New trade missing from merchant pool: " + expected.path());
            var offer = trade.value().getOffer(context);
            check(offer != null && offer.getBaseCostA().is(expected.buys() ? expected.item().asItem() : Items.EMERALD)
                    && offer.getBaseCostA().getCount() == expected.cost()
                    && offer.getResult().is(expected.buys() ? Items.EMERALD : expected.item().asItem())
                    && offer.getResult().getCount() == expected.count(), "Incorrect new trade: " + expected.path());
        }
        int tradeCount = 0;
        for (var trade : lookup.listElements().filter(holder -> holder.key().identifier().getNamespace().equals(FloraExpansion.MODID)).toList()) {
            String path = trade.key().identifier().getPath();
            String[] parts = path.split("/");
            String pool = parts[0].equals("wandering_trader") ? "wandering_trader/" + (path.endsWith("_jam") ? "uncommon" : "common")
                    : parts[0] + "/level_" + parts[1];
            check(trade.is(TagKey.create(Registries.VILLAGER_TRADE, Identifier.withDefaultNamespace(pool))),
                    "Mod trade is unreachable: " + path);
            var offer = trade.value().getOffer(context);
            check(offer != null && !offer.getBaseCostA().isEmpty() && !offer.getResult().isEmpty(), "Invalid trade: " + path);
            tradeCount++;
        }
        FloraExpansion.LOGGER.info("Verified {} mod trades, including {} targeted additions, and prickly pear pink dye", tradeCount, additions.size());
    }

    private static Identifier id(String path) { return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path); }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
}
