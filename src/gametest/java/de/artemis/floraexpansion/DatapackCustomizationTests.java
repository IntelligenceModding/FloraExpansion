package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.AppleLeavesBlock;
import de.artemis.floraexpansion.common.block.FlaxBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.item.BasketHarvestHelper;
import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class DatapackCustomizationTests {
    private static final BlockPos POS = new BlockPos(1, 2, 1);
    private DatapackCustomizationTests() {}

    public static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(POS);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.setBlock(POS, ModBlocks.CRATE.get());
        var crate = (CrateBlockEntity) level.getBlockEntity(pos);
        check(crate.insertItem(new ItemStack(Items.DIAMOND, 7)) == 7, "Datapack cannot add a crate item");
        crate.clearContent();
        check(crate.insertItem(new ItemStack(Items.WHEAT_SEEDS)) == 0, "Crate tag replacement did not remove defaults");
        check(BasketHarvestHelper.canGather(Blocks.HAY_BLOCK.defaultBlockState()), "Datapack cannot add a gatherable block");
        check(!BasketHarvestHelper.canGather(Blocks.BROWN_MUSHROOM.defaultBlockState()), "Basket tag replacement did not remove defaults");
        helper.setBlock(POS, Blocks.HAY_BLOCK);
        var basket = new ItemStack(ModItems.BASKET.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, basket);
        var context = new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
        check(basket.useOn(context).consumesAction() && BasketItem.getContents(basket).stream().anyMatch(s -> s.is(Items.HAY_BLOCK)), "Custom gathering did not reach server harvest logic");

        helper.setBlock(POS.below(), Blocks.STONE);
        check(ModBlocks.FLAX.get().defaultBlockState().setValue(FlaxBlock.WILD, false).canSurvive(level, pos), "Modpack soil was not accepted");
        helper.setBlock(POS.below(), Blocks.FARMLAND);
        check(!ModBlocks.FLAX.get().defaultBlockState().setValue(FlaxBlock.WILD, false).canSurvive(level, pos), "Soil replacement did not remove farmland");
        var biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
        var selected = biomes.getOrThrow(ModTags.Biomes.HAS_BLUEBERRY_BUSHES);
        check(selected.size() == 1 && selected.contains(biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.DESERT)), "Biome selection tag was not replaced");

        var recipe = level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, id("linen_thread"))).orElseThrow().value();
        check(recipe.placementInfo().ingredients().getFirst().test(new ItemStack(Items.STRING)), "Textile recipe ignored ingredient tag override");
        check(!recipe.placementInfo().ingredients().getFirst().test(new ItemStack(ModItems.FLAX_FIBER.get())), "Ingredient replacement retained old fiber");
        var params = new net.minecraft.world.level.storage.loot.LootParams.Builder(level).create(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.EMPTY);
        var lootContext = new net.minecraft.world.level.storage.loot.LootContext.Builder(params).create(java.util.Optional.empty());
        var compost = ModBlocks.APPLE_CORE.get().asItem().builtInRegistryHolder().getData(net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps.COMPOSTABLES);
 check(compost != null && compost.chance() == .99f, "Compost data map ignored datapack");

        helper.setBlock(POS.below(), Blocks.DIRT);
        var ripe = ModBlocks.APPLE_LEAVES.get().defaultBlockState().setValue(AppleLeavesBlock.AGE, 2);
        helper.setBlock(POS, ripe);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.useBlock(POS, player);
        int handCount = 0;
        for (var entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(2))) {
            if (entity.getItem().is(Items.EMERALD)) handCount += entity.getItem().getCount();
            entity.discard();
        }
        check(handCount == 7, "Hand harvest ignored overridden loot table");
        helper.setBlock(POS, ripe);
        basket = new ItemStack(ModItems.BASKET.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, basket);
        check(basket.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false))).consumesAction(), "Basket did not harvest custom rewards");
        check(BasketItem.getContents(basket).stream().filter(s -> s.is(Items.EMERALD)).mapToInt(ItemStack::getCount).sum() == 7, "Basket and hand do not share overridden rewards");
        var farmerTrades = net.minecraft.world.entity.npc.VillagerTrades.TRADES.get(net.minecraft.world.entity.npc.VillagerProfession.FARMER).get(3);
        var ownTrades = java.util.Arrays.stream(farmerTrades).filter(t -> t instanceof de.artemis.floraexpansion.common.event.LegacyTradeResources.Trade).toList();
        check(ownTrades.size() == 2, "Trade pool replacement was not applied");
        var offer = ownTrades.getFirst().getOffer(level, player, level.getRandom());
        check(offer.getBaseCostA().getCount() == 7 && offer.getResult().is(ModItems.STRAWBERRY_JAM.get()), "Trade JSON override was not applied");
        var extraOffer = ownTrades.get(1).getOffer(level, player, level.getRandom());
        check(extraOffer.getBaseCostA().getCount() == 9 && extraOffer.getResult().is(ModItems.APPLE_JAM.get()), "Nested pool tag or custom trade namespace was not loaded");
        FloraExpansion.LOGGER.info("Real datapack overrides passed: crate, basket, soils, biomes, recipes, compost and shared harvest loot");
    }

    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, path); }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
}
