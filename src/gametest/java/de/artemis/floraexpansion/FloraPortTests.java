package de.artemis.floraexpansion;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.block.*;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.item.WoodenBucketUtil;
import de.artemis.floraexpansion.common.registry.*;
import de.artemis.floraexpansion.common.util.CactusStripHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class FloraPortTests {
    private static final BlockPos POS = new BlockPos(1, 2, 1);
    private static final java.util.Map<String, Consumer<GameTestHelper>> TESTS = java.util.Map.ofEntries(
            java.util.Map.entry("all_block_states", FloraPortTests::blockStates),
            java.util.Map.entry("crops_and_fruiting_leaves", FloraPortTests::crops),
            java.util.Map.entry("harvest_and_basket", FloraPortTests::harvest),
            java.util.Map.entry("basket_harvest_sounds", FloraPortTests::harvestSounds),
            java.util.Map.entry("crate_storage_and_packing", FloraPortTests::crate),
            java.util.Map.entry("wooden_bucket_durability", FloraPortTests::buckets),
            java.util.Map.entry("compost_and_furnace_fuels", FloraPortTests::components),
            java.util.Map.entry("signs_and_flower_pots", FloraPortTests::signsAndPots),
            java.util.Map.entry("cactus_stripping", FloraPortTests::stripping),
            java.util.Map.entry("boats_and_projectiles", FloraPortTests::entities),
            java.util.Map.entry("worldgen_and_loot_registries", FloraPortTests::registries),
            java.util.Map.entry("block_drop_conditions_and_counts", FloraPortTests::drops),
            java.util.Map.entry("feature_placement", FloraPortTests::features),
            java.util.Map.entry("creative_tab_contents", FloraPortTests::creativeTab));

    @SubscribeEvent
    public static void registerFunctions(net.neoforged.neoforge.registries.RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, registry -> TESTS.forEach((name, body) ->
                registry.register(id(name), helper -> { body.accept(helper); helper.succeed(); })));
    }

    @SubscribeEvent
    public static void register(RegisterGameTestsEvent event) {
        TESTS.forEach((name, body) -> test(event, name));
    }

    private static void test(RegisterGameTestsEvent event, String name) {
        var environment = event.registerEnvironment(id(name));
        var data = new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 1, true);
        event.registerTest(id(name), new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, id(name)), data));
    }

    private static Identifier id(String name) { return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name); }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    private static void blockStates(GameTestHelper helper) {
        int blocks = 0;
        int states = 0;
        for (var entry : ModBlocks.BLOCKS.getEntries()) {
            var block = entry.get();
            for (var state : block.getStateDefinition().getPossibleStates()) {
                state.getShape(helper.getLevel(), helper.absolutePos(POS));
                state.getCollisionShape(helper.getLevel(), helper.absolutePos(POS));
                state.getFluidState();
                states++;
            }
            blocks++;
        }
        check(blocks > 50 && states > blocks, "Expected every registered block and state");
        FloraExpansion.LOGGER.info("Port regression checked {} blocks and {} states", blocks, states);
    }

    private static void crops(GameTestHelper h) {
        var level = h.getLevel();
        var pos = h.absolutePos(POS);
        h.setBlock(POS.below(), Blocks.GRASS_BLOCK);
        check(ModBlocks.BLUEBERRY_BUSH.get().defaultBlockState().canSurvive(level, pos), "Blueberries on grass");
        check(ModBlocks.LARGE_BLUEBERRY_BUSH.get().defaultBlockState().canSurvive(level, pos), "Large blueberries on grass");
        check(ModBlocks.STRAWBERRY_PLANT.get().defaultBlockState().setValue(StrawberryCropBlock.WILD, true).canSurvive(level, pos), "Wild strawberries on grass");
        h.setBlock(POS.below(), Blocks.FARMLAND);
        for (Block block : new Block[]{ModBlocks.FLAX_CROP.get(), ModBlocks.STRAWBERRY_PLANT.get(),
                ModBlocks.BLUEBERRY_BUSH.get(), ModBlocks.LARGE_BLUEBERRY_BUSH.get(),
                ModBlocks.FRUITING_OAK_LEAVES.get(), ModBlocks.FRUITING_CHERRY_LEAVES.get()}) {
            h.setBlock(POS.above(), Blocks.AIR);
            h.setBlock(POS, block);
            var grower = (BonemealableBlock) block;
            for (int i = 0; i < 8; i++) {
                var state = level.getBlockState(pos);
                if (grower.isValidBonemealTarget(level, pos, state, BonemealSource.INTERACTION)) {
                    grower.performBonemeal(level, level.getRandom(), pos, state, BonemealSource.INTERACTION);
                }
            }
            var state = level.getBlockState(pos);
            check(!grower.isValidBonemealTarget(level, pos, state, BonemealSource.INTERACTION), "Crop did not mature: " + block);
            if (block == ModBlocks.FLAX_CROP.get()) {
                check(state.getValue(FlaxCropBlock.AGE) == 4, "Flax age");
                check(level.getBlockState(pos.above()).getValue(FlaxCropBlock.HALF) == DoubleBlockHalf.UPPER, "Flax upper half");
            }
        }
    }

    private static void harvest(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var basket = new ItemStack(ModItems.BASKET.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, basket);
        h.setBlock(POS.below(), Blocks.DIRT);
        h.setBlock(POS, ModBlocks.LARGE_BLUEBERRY_BUSH.get().defaultBlockState().setValue(LargeBlueberryBushBlock.AGE, 3));
        var pos = h.absolutePos(POS);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        var result = basket.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        check(result.consumesAction(), "Basket harvest interaction");
        check(h.getBlockState(POS).getValue(LargeBlueberryBushBlock.AGE) == 1, "Blueberry harvest reset");
        check(BasketItem.getContents(basket).stream().anyMatch(s -> s.is(ModItems.BLUEBERRIES.get())), "Harvested berries missing");
        check(BasketItem.insertItem(basket, new ItemStack(ModItems.STRAWBERRY.get(), 64)) == 64, "Basket insert");
        check(BasketItem.getContents(basket.copy()).stream().anyMatch(s -> s.is(ModItems.STRAWBERRY.get()) && s.getCount() == 64), "Basket component persistence");
    }

    private static void crate(GameTestHelper h) {
        h.setBlock(POS, ModBlocks.CRATE.get());
        var crate = (CrateBlockEntity) h.getLevel().getBlockEntity(h.absolutePos(POS));
        check(crate != null, "Crate block entity");
        check(crate.insertItem(new ItemStack(Items.APPLE, 128)) == 128, "Crate bulk insert");
        check(crate.insertItem(new ItemStack(Items.STICK)) == 0, "Crate must reject mixed items");
        check(crate.extractSingleItem().getCount() == 1 && crate.getTotalItemCount() == 127, "Crate extraction");
        var packed = new ItemStack(ModBlocks.CRATE.get());
        crate.saveToItem(packed, h.getLevel().registryAccess());
        check(packed.has(DataComponents.BLOCK_ENTITY_DATA), "Packed crate contents missing");
        h.setBlock(POS, h.getBlockState(POS).setValue(CrateBlock.PACKED, true));
        check(crate.insertItem(new ItemStack(Items.APPLE)) == 0, "Packed crate must lock storage");
    }

    private static void harvestSounds(GameTestHelper h) {
        var level = h.getLevel();
        var pos = h.absolutePos(POS);
        var sounds = new java.util.ArrayList<net.neoforged.neoforge.event.PlayLevelSoundEvent.AtPosition>();
        Consumer<net.neoforged.neoforge.event.PlayLevelSoundEvent.AtPosition> listener = event -> {
            if (event.getLevel() == level && BlockPos.containing(event.getPosition()).equals(pos)) sounds.add(event);
        };
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener(listener);
        try {
            var player = h.makeMockPlayer(GameType.SURVIVAL);
            var states = new java.util.ArrayList<net.minecraft.world.level.block.state.BlockState>();
            for (int age = 1; age <= FruitingCherryLeavesBlock.MAX_AGE; age++) {
                states.add(ModBlocks.FRUITING_CHERRY_LEAVES.get().defaultBlockState().setValue(FruitingCherryLeavesBlock.AGE, age));
            }
            states.add(ModBlocks.FRUITING_OAK_LEAVES.get().defaultBlockState().setValue(FruitingOakLeavesBlock.AGE, FruitingOakLeavesBlock.MAX_AGE));
            states.add(ModBlocks.BLUEBERRY_BUSH.get().defaultBlockState().setValue(SmallBlueberryBushBlock.AGE, 3));
            states.add(ModBlocks.LARGE_BLUEBERRY_BUSH.get().defaultBlockState().setValue(LargeBlueberryBushBlock.AGE, 3));
            states.add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3));
            states.add(Blocks.CAVE_VINES.defaultBlockState().setValue(CaveVines.BERRIES, true));
            states.add(Blocks.CAVE_VINES_PLANT.defaultBlockState().setValue(CaveVines.BERRIES, true));
            states.add(ModBlocks.OPUNTIA_CACTUS.get().defaultBlockState().setValue(CactusFruitPlantBlock.AGE, 1));
            for (var state : states) {
                h.setBlock(POS, Blocks.AIR);
                h.setBlock(POS.below(), state.is(ModBlocks.OPUNTIA_CACTUS.get()) ? Blocks.SAND : Blocks.DIRT);
                h.setBlock(POS.above(), Blocks.STONE);
                h.setBlock(POS, state);
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.BASKET.get()));
                var context = new UseOnContext(player, InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
                sounds.clear();
                check(player.getMainHandItem().useOn(context).consumesAction(), "Basket must harvest " + state);
                check(sounds.size() == 1, "Expected one harvest sound for " + state + ", got " + sounds.size());
                check(sounds.getFirst().getSource() == net.minecraft.sounds.SoundSource.BLOCKS, "Harvest sound category");
                check(sounds.getFirst().getNewVolume() > 0, "Harvest sound must be audible");
                check(BasketItem.getContents(player.getMainHandItem()).stream().anyMatch(stack -> !stack.isEmpty()), "Harvest drops missing");
            }
            for (var block : new Block[]{ModBlocks.FRUITING_CHERRY_LEAVES.get(), ModBlocks.FRUITING_OAK_LEAVES.get()}) {
                h.setBlock(POS, block);
                sounds.clear();
                var context = new UseOnContext(player, InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
                check(!player.getMainHandItem().useOn(context).consumesAction(), "Empty leaves must not harvest");
                check(sounds.isEmpty(), "Failed harvest must stay silent");
            }
        } finally {
            net.neoforged.neoforge.common.NeoForge.EVENT_BUS.unregister(listener);
        }
    }

    private static void buckets(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var bucket = new ItemStack(ModItems.WOODEN_BUCKET.get());
        bucket.set(DataComponents.CUSTOM_NAME, Component.literal("Test bucket"));
        var water = WoodenBucketUtil.transformInto(bucket, player, ModItems.WOODEN_WATER_BUCKET.get());
        check(water.getDamageValue() == 1 && water.getHoverName().getString().equals("Test bucket"), "Bucket transform data");
        var empty = WoodenBucketUtil.damageInto(water, player, ModItems.WOODEN_BUCKET.get());
        check(empty.getDamageValue() == 2, "Bucket empty durability");
        empty.setDamageValue(empty.getMaxDamage() - 1);
        check(WoodenBucketUtil.damageInto(empty, player, ModItems.WOODEN_BUCKET.get()).isEmpty(), "Bucket must break");
        var lava = new ItemStack(ModItems.WOODEN_LAVA_BUCKET.get());
        WoodenBucketUtil.initializeLavaTimeout(lava, 1000, h.getLevel().getRandom());
        check(!WoodenBucketUtil.hasExpiredLavaTimeout(lava, 1000) && WoodenBucketUtil.hasExpiredLavaTimeout(lava, 1200), "Lava timeout");
    }

    private static void components(GameTestHelper h) {
        int compostables = 0, fuels = 0;
        for (var entry : ModItems.ITEMS.getEntries()) {
            var stack = new ItemStack(entry.get());
            if (stack.has(DataComponents.COMPOSTABLE)) compostables++;
            if (stack.has(DataComponents.COOKING_FUEL)) fuels++;
        }
        check(compostables == 26, "Expected 26 compostables, got " + compostables);
        check(fuels == 27, "Expected 27 furnace fuels, got " + fuels);
        for (var armor : new net.minecraft.world.item.Item[]{ModItems.CACTUS_HELMET.get(), ModItems.CACTUS_CHESTPLATE.get(),
                ModItems.CACTUS_LEGGINGS.get(), ModItems.CACTUS_BOOTS.get()}) {
            var stack = new ItemStack(armor);
            check(stack.has(DataComponents.EQUIPPABLE) && stack.isDamageableItem(), "Cactus armor components");
        }
    }

    private static void signsAndPots(GameTestHelper h) {
        var pot = (FlowerPotBlock) Blocks.FLOWER_POT;
        check(pot.getFullPot(ModBlocks.APPLE_CORE.get()) == ModBlocks.POTTED_APPLE_CORE.get(), "Apple core flower pot");
        check(pot.getFullPot(ModBlocks.CHERRY_PIT.get()) == ModBlocks.POTTED_CHERRY_PIT.get(), "Cherry pit flower pot");
        check(pot.getFullPot(ModBlocks.GIANT_CACTUS_STEM.get()) == ModBlocks.POTTED_GIANT_CACTUS_STEM.get(), "Cactus flower pot");
        for (var block : new Block[]{ModBlocks.CACTUS_SIGN.get(), ModBlocks.CACTUS_WALL_SIGN.get(),
                ModBlocks.CACTUS_HANGING_SIGN.get(), ModBlocks.CACTUS_WALL_HANGING_SIGN.get()}) {
            h.setBlock(POS, block);
            check(h.getLevel().getBlockEntity(h.absolutePos(POS)) instanceof net.minecraft.world.level.block.entity.SignBlockEntity, "Sign entity: " + block);
        }
    }

    private static void stripping(GameTestHelper h) {
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var axe = new ItemStack(Items.IRON_AXE);
        check(CactusStripHelper.canStrip(axe) && !CactusStripHelper.canStrip(new ItemStack(Items.STICK)), "Axe detection");
        player.setItemInHand(InteractionHand.MAIN_HAND, axe);
        h.setBlock(POS, ModBlocks.GIANT_CACTUS_BASE.get());
        h.useBlock(POS, player);
        check(h.getBlockState(POS).is(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get()), "Cactus stripping");
        check(axe.getDamageValue() == 1, "Stripping durability");
    }

    private static void entities(GameTestHelper h) {
        var level = h.getLevel();
        for (var entry : ModEntityTypes.ENTITY_TYPES.getEntries()) {
            var entity = entry.get().create(level, EntitySpawnReason.COMMAND);
            check(entity != null, "Entity factory: " + entry.getId());
            entity.discard();
        }
    }

    private static void registries(GameTestHelper h) {
        var lookup = h.getLevel().registryAccess();
        long features = lookup.lookupOrThrow(Registries.FEATURE).listElements().filter(e -> e.key().identifier().getNamespace().equals(FloraExpansion.MODID)).count();
        long placed = lookup.lookupOrThrow(Registries.PLACED_FEATURE).listElements().filter(e -> e.key().identifier().getNamespace().equals(FloraExpansion.MODID)).count();
        check(features == 13 && placed == 16, "Worldgen registry counts: " + features + " features, " + placed + " placements");
        var loot = h.getLevel().getServer().reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE);
        for (var entry : ModBlocks.BLOCKS.getEntries()) {
            entry.get().getLootTable().ifPresent(key -> check(loot.get(key).isPresent(), "Missing block loot: " + key));
        }
    }

    private static void creativeTab(GameTestHelper h) {
        var tab = ModCreativeModeTabs.FLORA_EXPANSION_CREATIVE_TAB.get();
        tab.buildContents(new net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters(
                h.getLevel().enabledFeatures(), true, h.getLevel().registryAccess()));
        for (var entry : ModItems.ITEMS.getEntries()) {
            check(tab.getSearchTabDisplayItems().stream().anyMatch(stack -> stack.is(entry.get())), "Item missing from creative search: " + entry.getId());
        }
    }

    private static java.util.List<ItemStack> drops(GameTestHelper h, net.minecraft.world.level.block.state.BlockState state) {
        h.setBlock(POS, state);
        return Block.getDrops(state, h.getLevel(), h.absolutePos(POS), h.getLevel().getBlockEntity(h.absolutePos(POS)));
    }

    private static int count(java.util.List<ItemStack> drops, net.minecraft.world.level.ItemLike item) {
        return drops.stream().filter(s -> s.is(item.asItem())).mapToInt(ItemStack::getCount).sum();
    }

    private static void drops(GameTestHelper h) {
        h.setBlock(POS.below(), Blocks.FARMLAND);
        for (int amount = 1; amount <= 4; amount++) {
            check(count(drops(h, ModBlocks.PINE_LITTER.get().defaultBlockState().setValue(PineLitterBlock.AMOUNT, amount)),
                    ModBlocks.PINE_LITTER.get()) == amount, "Pine litter drop count " + amount);
            check(count(drops(h, ModBlocks.CACTUS_CLUSTER.get().defaultBlockState().setValue(CactusClusterBlock.PICKLES, amount)),
                    ModBlocks.CACTUS_CLUSTER.get()) == amount, "Cactus cluster drop count " + amount);
        }
        for (int amount = 1; amount <= 5; amount++) {
            check(count(drops(h, ModBlocks.PEBBLE_PATCH.get().defaultBlockState().setValue(PebblePatchBlock.AMOUNT, amount)),
                    ModBlocks.PEBBLE_PATCH.get()) == amount, "Pebble patch drop count " + amount);
        }
        var flax = ModBlocks.FLAX_CROP.get().defaultBlockState().setValue(FlaxCropBlock.AGE, 4);
        var mature = drops(h, flax);
        check(count(mature, ModItems.FLAX_SEED.get()) >= 2 && count(mature, ModItems.FLAX_FIBER.get()) >= 2
                && count(mature, ModItems.FLAX_FLOWER.get()) >= 1, "Mature flax drops");
        check(drops(h, flax.setValue(FlaxCropBlock.HALF, DoubleBlockHalf.UPPER)).isEmpty(), "Upper flax must not duplicate loot");
        var young = drops(h, flax.setValue(FlaxCropBlock.AGE, 0));
        check(count(young, ModItems.FLAX_SEED.get()) == 1 && count(young, ModItems.FLAX_FIBER.get()) == 0, "Immature flax drops");
        check(count(drops(h, ModBlocks.APPLE_CORE.get().defaultBlockState()), ModBlocks.APPLE_CORE.get()) == 1, "Apple core drop");
        var generatedWood = ModBlocks.GIANT_CACTUS_WOOD.get().defaultBlockState().setValue(GiantCactusWoodBlock.GENERATED, true);
        check(count(drops(h, generatedWood), ModBlocks.GIANT_CACTUS_BASE.get()) == 1, "Generated cactus wood drop");
        check(count(drops(h, generatedWood.setValue(GiantCactusWoodBlock.GENERATED, false)), ModBlocks.GIANT_CACTUS_WOOD.get()) == 1, "Crafted cactus wood drop");
    }

    private static void features(GameTestHelper h) {
        var level = h.getLevel();
        var origin = h.absolutePos(new BlockPos(2, 3, 2));
        var registry = level.registryAccess().lookupOrThrow(Registries.FEATURE);
        for (var entry : registry.listElements().filter(e -> e.key().identifier().getNamespace().equals(FloraExpansion.MODID)).toList()) {
            String name = entry.key().identifier().getPath();
            boolean desert = name.contains("cactus");
            for (int x = -9; x <= 9; x++) {
                for (int z = -9; z <= 9; z++) {
                    for (int y = 0; y < 22; y++) level.setBlock(origin.offset(x, y, z), Blocks.AIR.defaultBlockState(), 2);
                    level.setBlock(origin.offset(x, -1, z), (desert ? Blocks.SAND : Blocks.GRASS_BLOCK).defaultBlockState(), 2);
                    level.setBlock(origin.offset(x, -2, z), (desert ? Blocks.SAND : Blocks.DIRT).defaultBlockState(), 2);
                }
            }
            boolean placed = false;
            for (int x = (origin.getX() - 9) >> 4; x <= (origin.getX() + 9) >> 4; x++) {
                for (int z = (origin.getZ() - 9) >> 4; z <= (origin.getZ() + 9) >> 4; z++) {
                    net.minecraft.world.level.levelgen.Heightmap.primeHeightmaps(level.getChunk(x, z),
                            java.util.EnumSet.of(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG));
                }
            }
            for (int seed = 1; seed <= 10 && !placed; seed++) {
                placed = entry.value().place(level, level.getChunkSource().getGenerator(), net.minecraft.util.RandomSource.create(seed), origin);
            }
            check(placed, "Feature failed on suitable terrain: " + name);
        }
    }
}
