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
            java.util.Map.entry("legacy_registry_and_block_states", LegacyMigrationTests::registriesAndBlockStates),
            java.util.Map.entry("legacy_item_components", LegacyMigrationTests::itemData),
            java.util.Map.entry("legacy_nested_containers", LegacyMigrationTests::nestedContainers),
            java.util.Map.entry("actual_041_save_data", LegacyMigrationTests::actualOldSave),
            java.util.Map.entry("powder_snow_bucket_placement", FloraPortTests::powderSnowBuckets),
            java.util.Map.entry("resource_projectiles", FloraPortTests::resourceProjectiles),
            java.util.Map.entry("all_block_states", FloraPortTests::blockStates),
            java.util.Map.entry("food_consumption_and_remainders", FloraPortTests::consumables),
            java.util.Map.entry("all_plant_harvest_stages", FloraPortTests::plantHarvestStages),
            java.util.Map.entry("vanilla_and_strawberry_candle_cakes", FloraPortTests::candleCakes),
            java.util.Map.entry("crops_and_fruit_leaves", FloraPortTests::crops),
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
        for (Block block : new Block[]{ModBlocks.FLAX.get(), ModBlocks.STRAWBERRY_PLANT.get(),
                ModBlocks.BLUEBERRY_BUSH.get(), ModBlocks.LARGE_BLUEBERRY_BUSH.get(),
                ModBlocks.APPLE_LEAVES.get(), ModBlocks.ORCHARD_CHERRY_LEAVES.get()}) {
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
            if (block == ModBlocks.FLAX.get()) {
                check(state.getValue(FlaxBlock.AGE) == 4, "Flax age");
                check(level.getBlockState(pos.above()).getValue(FlaxBlock.HALF) == DoubleBlockHalf.UPPER, "Flax upper half");
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
        var unpacked = new CrateBlockEntity(h.absolutePos(POS), ModBlocks.CRATE.get().defaultBlockState());
        unpacked.applyComponentsFromItemStack(packed);
        check(unpacked.getTotalItemCount() == 127, "Packed crate contents missing");
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
            for (int age = 1; age <= OrchardCherryLeavesBlock.MAX_AGE; age++) {
                states.add(ModBlocks.ORCHARD_CHERRY_LEAVES.get().defaultBlockState().setValue(OrchardCherryLeavesBlock.AGE, age));
            }
            states.add(ModBlocks.APPLE_LEAVES.get().defaultBlockState().setValue(AppleLeavesBlock.AGE, AppleLeavesBlock.MAX_AGE));
            states.add(ModBlocks.BLUEBERRY_BUSH.get().defaultBlockState().setValue(SmallBlueberryBushBlock.AGE, 3));
            states.add(ModBlocks.LARGE_BLUEBERRY_BUSH.get().defaultBlockState().setValue(LargeBlueberryBushBlock.AGE, 3));
            states.add(Blocks.SWEET_BERRY_BUSH.defaultBlockState().setValue(SweetBerryBushBlock.AGE, 3));
            states.add(Blocks.CAVE_VINES.defaultBlockState().setValue(CaveVines.BERRIES, true));
            states.add(Blocks.CAVE_VINES_PLANT.defaultBlockState().setValue(CaveVines.BERRIES, true));
            states.add(ModBlocks.PRICKLY_PEAR_CACTUS.get().defaultBlockState().setValue(PricklyPearCactusBlock.AGE, 1));
            for (var state : states) {
                h.setBlock(POS, Blocks.AIR);
                h.setBlock(POS.below(), state.is(ModBlocks.PRICKLY_PEAR_CACTUS.get()) ? Blocks.SAND : Blocks.DIRT);
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
            for (var block : new Block[]{ModBlocks.ORCHARD_CHERRY_LEAVES.get(), ModBlocks.APPLE_LEAVES.get()}) {
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
        for (var armor : new net.minecraft.world.item.Item[]{ModItems.SPIKED_LEATHER_HELMET.get(), ModItems.SPIKED_LEATHER_CHESTPLATE.get(),
                ModItems.SPIKED_LEATHER_LEGGINGS.get(), ModItems.SPIKED_LEATHER_BOOTS.get()}) {
            var stack = new ItemStack(armor);
            check(stack.has(DataComponents.EQUIPPABLE) && stack.isDamageableItem(), "Spiked leather armor components");
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
        h.setBlock(POS, ModBlocks.CACTUS_LOG.get());
        h.useBlock(POS, player);
        check(h.getBlockState(POS).is(ModBlocks.STRIPPED_CACTUS_LOG.get()), "Cactus stripping");
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
            check(count(drops(h, ModBlocks.NEEDLE_LITTER.get().defaultBlockState().setValue(NeedleLitterBlock.AMOUNT, amount)),
                    ModBlocks.NEEDLE_LITTER.get()) == amount, "Needle litter drop count " + amount);
            check(count(drops(h, ModBlocks.SMALL_CACTUS.get().defaultBlockState().setValue(SmallCactusBlock.PICKLES, amount)),
                    ModBlocks.SMALL_CACTUS.get()) == amount, "Small cactus drop count " + amount);
        }
        for (int amount = 1; amount <= 5; amount++) {
            check(count(drops(h, ModBlocks.SCATTERED_PEBBLES.get().defaultBlockState().setValue(ScatteredPebblesBlock.AMOUNT, amount)),
                    ModBlocks.SCATTERED_PEBBLES.get()) == amount, "Scattered pebbles drop count " + amount);
        }
        var flax = ModBlocks.FLAX.get().defaultBlockState().setValue(FlaxBlock.AGE, 4);
        var mature = drops(h, flax);
        check(count(mature, ModItems.FLAX_SEEDS.get()) >= 2 && count(mature, ModItems.FLAX_FIBER.get()) >= 2
                && count(mature, ModItems.FLAX_FLOWER.get()) >= 1, "Mature flax drops");
        check(drops(h, flax.setValue(FlaxBlock.HALF, DoubleBlockHalf.UPPER)).isEmpty(), "Upper flax must not duplicate loot");
        var young = drops(h, flax.setValue(FlaxBlock.AGE, 0));
        check(count(young, ModItems.FLAX_SEEDS.get()) == 1 && count(young, ModItems.FLAX_FIBER.get()) == 0, "Immature flax drops");
        check(count(drops(h, ModBlocks.APPLE_CORE.get().defaultBlockState()), ModBlocks.APPLE_CORE.get()) == 1, "Apple core drop");
        var generatedWood = ModBlocks.CACTUS_WOOD.get().defaultBlockState().setValue(CactusWoodBlock.GENERATED, true);
        check(count(drops(h, generatedWood), ModBlocks.CACTUS_LOG.get()) == 1, "Generated cactus wood drop");
        check(count(drops(h, generatedWood.setValue(CactusWoodBlock.GENERATED, false)), ModBlocks.CACTUS_WOOD.get()) == 1, "Crafted cactus wood drop");
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

    private static void consumables(GameTestHelper h) {
        var level = h.getLevel();
        for (var juice : new net.minecraft.world.item.Item[]{ModItems.BLUEBERRY_JUICE.get(), ModItems.CHERRY_JUICE.get(),
                ModItems.APPLE_JUICE.get(), ModItems.CACTUS_JUICE.get()}) {
            var player = h.makeMockPlayer(GameType.SURVIVAL);
            player.getFoodData().setFoodLevel(0);
            var stack = new ItemStack(juice);
            check(stack.getUseAnimation() == net.minecraft.world.item.ItemUseAnimation.DRINK, "Juice must be drunk");
            check(stack.finishUsingItem(level, player).is(Items.GLASS_BOTTLE), "Juice must return its last bottle");
            var stackedJuice = new ItemStack(juice, 2).finishUsingItem(level, player);
            check(stackedJuice.is(juice) && stackedJuice.getCount() == 1
                    && player.getInventory().contains(new ItemStack(Items.GLASS_BOTTLE)), "Stacked juice must deliver bottle");

            var creative = h.makeMockPlayer(GameType.CREATIVE);
            GameType.CREATIVE.updatePlayerAbilities(creative.getAbilities());
            var creativeStack = new ItemStack(juice, 2).finishUsingItem(level, creative);
            check(creativeStack.is(juice) && creativeStack.getCount() == 2, "Creative juice must not be consumed");
        }

        for (var jam : new net.minecraft.world.item.Item[]{ModItems.BLUEBERRY_JAM.get(), ModItems.STRAWBERRY_JAM.get()}) {
            var player = h.makeMockPlayer(GameType.SURVIVAL);
            player.getFoodData().setFoodLevel(0);
            check(new ItemStack(jam).finishUsingItem(level, player).is(ModItems.GLASS_JAR.get()), "Last jam must return jar");
            var remaining = new ItemStack(jam, 2).finishUsingItem(level, player);
            check(remaining.is(jam) && remaining.getCount() == 1, "Stacked jam count");
            check(player.getInventory().contains(new ItemStack(ModItems.GLASS_JAR.get())), "Stacked jam must deliver jar");

            var fullPlayer = h.makeMockPlayer(GameType.SURVIVAL);
            fullPlayer.setPos(Vec3.atCenterOf(h.absolutePos(POS)));
            fullPlayer.getFoodData().setFoodLevel(0);
            for (int i = 0; i < fullPlayer.getInventory().getContainerSize(); i++) {
                fullPlayer.getInventory().setItem(i, new ItemStack(Items.STONE, 64));
            }
            new ItemStack(jam, 2).finishUsingItem(level, fullPlayer);
            check(!level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                    new net.minecraft.world.phys.AABB(h.absolutePos(POS)).inflate(2),
                    entity -> entity.getItem().is(ModItems.GLASS_JAR.get())).isEmpty(), "Full inventory must drop jar");
            var area = new net.minecraft.world.phys.AABB(h.absolutePos(POS)).inflate(2);
            int jarsBefore = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, area).stream()
                    .filter(entity -> entity.getItem().is(ModItems.GLASS_JAR.get())).mapToInt(entity -> entity.getItem().getCount()).sum();
            var consumer = net.minecraft.world.entity.EntityTypes.COW.create(level, EntitySpawnReason.COMMAND);
            check(consumer != null, "Non-player food consumer");
            consumer.setPos(Vec3.atCenterOf(h.absolutePos(POS)));
            var animalFood = new ItemStack(jam, 2).finishUsingItem(level, consumer);
            check(animalFood.is(jam) && animalFood.getCount() == 1, "Non-player food consumption");
            int jarsAfter = level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, area).stream()
                    .filter(entity -> entity.getItem().is(ModItems.GLASS_JAR.get())).mapToInt(entity -> entity.getItem().getCount()).sum();
            check(jarsAfter == jarsBefore + 1, "Non-player consumer must drop its jar");

            var creative = h.makeMockPlayer(GameType.CREATIVE);
            GameType.CREATIVE.updatePlayerAbilities(creative.getAbilities());
            var creativeJam = new ItemStack(jam, 2).finishUsingItem(level, creative);
            check(creativeJam.is(jam) && creativeJam.getCount() == 2, "Creative jam must not be consumed");
        }

        var player = h.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(0);
        var pie = new ItemStack(ModItems.BLUEBERRY_PIE.get());
        check(pie.getUseDuration(player) == 128, "Pie eating duration");
        pie.finishUsingItem(level, player);
        var regeneration = player.getEffect(net.minecraft.world.effect.MobEffects.REGENERATION);
        check(regeneration != null && regeneration.getDuration() == 100 && regeneration.getAmplifier() == 0, "Pie regeneration");
    }

    private static void plantHarvestStages(GameTestHelper h) {
        var level = h.getLevel();
        var pos = h.absolutePos(POS);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        Block[] plants = {ModBlocks.BLUEBERRY_BUSH.get(), ModBlocks.LARGE_BLUEBERRY_BUSH.get(),
                ModBlocks.APPLE_LEAVES.get(), ModBlocks.ORCHARD_CHERRY_LEAVES.get()};
        int[] firstHarvestAge = {3, 2, 2, 1};
        int[] resetAge = {2, 1, 0, 0};
        net.minecraft.world.item.Item[] produce = {ModItems.BLUEBERRIES.get(), ModItems.BLUEBERRIES.get(), Items.APPLE, ModItems.CHERRIES.get()};
        for (int plant = 0; plant < plants.length; plant++) {
            var ageProperty = (net.minecraft.world.level.block.state.properties.IntegerProperty)
                    plants[plant].defaultBlockState().getProperties().stream().filter(p -> p.getName().equals("age")).findFirst().orElseThrow();
            for (int age : ageProperty.getPossibleValues()) {
                h.setBlock(POS.below(), Blocks.DIRT);
                h.setBlock(POS, plants[plant].defaultBlockState().setValue(ageProperty, age));
                var player = h.makeMockPlayer(GameType.SURVIVAL);
                var basket = new ItemStack(ModItems.BASKET.get());
                player.setItemInHand(InteractionHand.MAIN_HAND, basket);
                var result = basket.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
                final var output = produce[plant];
                int harvested = BasketItem.getContents(basket).stream().filter(stack -> stack.is(output)).mapToInt(ItemStack::getCount).sum();
                if (age < firstHarvestAge[plant]) {
                    check(harvested == 0 && h.getBlockState(POS).getValue(ageProperty) == age, "Unripe plant must stay intact");
                } else {
                    check(result.consumesAction() && harvested > 0, "Ripe plant harvest");
                    check(h.getBlockState(POS).getValue(ageProperty) == resetAge[plant], "Plant regrowth stage");
                    if (plant == 0 || (plant == 1 && age == 2)) check(harvested == 1 || harvested == 2, "Early blueberry yield");
                    if (plant == 1 && age == 3) check(harvested == 2 || harvested == 4, "Ripe large blueberry yield");
                    if (plant == 2 || (plant == 3 && age == 1)) check(harvested == 1, "Single fruit yield");
                    if (plant == 3 && age >= 2) check(harvested >= age && harvested <= age + 1, "Cherry yield");
                }
            }
        }
    }

    private static void candleCakes(GameTestHelper h) {
        var level = h.getLevel();
        var pos = h.absolutePos(POS);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        var candleHit = new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.4, 0), Direction.UP, pos, false);
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        h.setBlock(POS.below(), Blocks.STONE);
        check(ModBlocks.STRAWBERRY_CANDLE_CAKES.size() == 17, "All candle colors must remain registered");
        for (var entry : ModBlocks.STRAWBERRY_CANDLE_CAKES) {
            var cake = entry.get();
            check(CandleCakeBlock.byCandle(cake.getCandle()).getBlock() instanceof CandleCakeBlock, "Vanilla cake lookup was overwritten");
            check(StrawberryCakeWithCandleBlock.byCandle(cake.getCandle()).is(cake), "Strawberry candle lookup");

            h.setBlock(POS, ModBlocks.STRAWBERRY_CAKE.get());
            var candle = new ItemStack(cake.getCandle());
            player.setItemInHand(InteractionHand.MAIN_HAND, candle);
            h.getBlockState(POS).useItemOn(candle, level, player, InteractionHand.MAIN_HAND, hit);
            check(h.getBlockState(POS).is(cake) && candle.isEmpty(), "Candle placement on strawberry cake");

            var lighter = new ItemStack(Items.FLINT_AND_STEEL);
            player.setItemInHand(InteractionHand.MAIN_HAND, lighter);
            lighter.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
            check(h.getBlockState(POS).getValue(AbstractCandleBlock.LIT), "Strawberry candle lighting");
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            h.getBlockState(POS).useItemOn(ItemStack.EMPTY, level, player, InteractionHand.MAIN_HAND, candleHit);
            check(!h.getBlockState(POS).getValue(AbstractCandleBlock.LIT), "Strawberry candle extinguishing");
            check(h.getBlockState(POS).getAnalogOutputSignal(level, pos, Direction.UP) == CakeBlock.FULL_CAKE_SIGNAL, "Cake comparator");

            player.getFoodData().setFoodLevel(0);
            h.getBlockState(POS).useWithoutItem(level, player, hit);
            check(h.getBlockState(POS).is(ModBlocks.STRAWBERRY_CAKE.get())
                    && h.getBlockState(POS).getValue(CakeBlock.BITES) == 1, "Candle cake must leave partially eaten strawberry cake");
            check(count(Block.getDrops(cake.defaultBlockState(), level, pos, null), cake.getCandle()) == 1, "Colored candle drop");
        }
    }

    private static void resourceProjectiles(GameTestHelper h) {
        var level = h.getLevel();
        var pos = Vec3.atCenterOf(h.absolutePos(POS));
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(pos);
        for (var item : new net.minecraft.world.item.Item[]{ModItems.SPRUCE_CONE.get(), ModItems.PEBBLES.get()}) {
            var projectile = ((net.minecraft.world.item.ProjectileItem) item).asProjectile(level, pos, new ItemStack(item), Direction.UP);
            check(projectile.getType() != net.minecraft.world.entity.EntityTypes.SNOWBALL, "Resource must retain its entity type");
            var replica = projectile.getType().create(level, EntitySpawnReason.COMMAND);
            check(replica != null && replica.getClass() == projectile.getClass(), "Spawn reconstruction must preserve projectile behavior");
            check(((net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile) replica).getItem().is(item), "Projectile default item");

            var held = new ItemStack(item, 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, held);
            item.use(level, player, InteractionHand.MAIN_HAND);
            check(held.getCount() == 1, "Throwing must consume one resource");
        }

        class TestPebble extends de.artemis.floraexpansion.common.projectile.PebblesProjectile {
            TestPebble() { super(level, player, new ItemStack(ModItems.PEBBLES.get())); }
            void impact(net.minecraft.world.entity.Entity target) {
                setPos(pos);
                onHit(new net.minecraft.world.phys.EntityHitResult(target));
            }
        }
        var target = net.minecraft.world.entity.EntityTypes.COW.create(level, EntitySpawnReason.COMMAND);
        check(target != null, "Projectile target");
        target.setPos(pos);
        float health = target.getHealth();
        var pebble = new TestPebble();
        pebble.impact(target);
        check(target.getHealth() == health - 2.0F, "Pebble damage");
        check(pebble.isRemoved(), "Pebble must disappear on impact");
        check(!level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                new net.minecraft.world.phys.AABB(h.absolutePos(POS)).inflate(2),
                entity -> entity.getItem().is(ModItems.PEBBLES.get())).isEmpty(), "Pebble must drop itself");

        class TestCone extends de.artemis.floraexpansion.common.projectile.SpruceConeProjectile {
            TestCone() { super(level, player, new ItemStack(ModItems.SPRUCE_CONE.get())); }
            void impact(net.minecraft.world.entity.Entity target) {
                setPos(pos);
                long seed = 0;
                for (;; seed++) {
                    var sample = net.minecraft.util.RandomSource.create(seed);
                    if (sample.nextInt(3) == 0 && sample.nextFloat() < 0.10F) break;
                }
                random.setSeed(seed);
                onHit(new net.minecraft.world.phys.EntityHitResult(target));
            }
        }
        var burningTarget = net.minecraft.world.entity.EntityTypes.COW.create(level, EntitySpawnReason.COMMAND);
        check(burningTarget != null, "Cone target");
        burningTarget.setPos(pos);
        burningTarget.igniteForSeconds(5);
        health = burningTarget.getHealth();
        var cone = new TestCone();
        cone.impact(burningTarget);
        check(burningTarget.getHealth() == health - 1.0F, "Cone damage");
        var slowness = burningTarget.getEffect(net.minecraft.world.effect.MobEffects.SLOWNESS);
        check(slowness != null && slowness.getDuration() == 60 && slowness.getAmplifier() == 0, "Cone slowing effect");
        check(cone.isRemoved(), "Cone must disappear on impact");
        check(level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                new net.minecraft.world.phys.AABB(h.absolutePos(POS)).inflate(2),
                entity -> entity.getItem().is(ModItems.TOASTED_SPRUCE_NUTS.get())).stream()
                .mapToInt(entity -> entity.getItem().getCount()).sum() == 2, "Burning impact must preserve double toasted-nut drop");
    }

    private static void powderSnowBuckets(GameTestHelper h) {
        var level = h.getLevel();
        var support = h.absolutePos(POS.below());
        var hit = new BlockHitResult(Vec3.atCenterOf(support).add(0, 0.5, 0), Direction.UP, support, false);
        h.setBlock(POS.below(), Blocks.STONE);
        for (boolean creative : new boolean[]{false, true}) {
            for (int damage : new int[]{3, 9}) {
                h.setBlock(POS, Blocks.AIR);
                var player = h.makeMockPlayer(creative ? GameType.CREATIVE : GameType.SURVIVAL);
                if (creative) GameType.CREATIVE.updatePlayerAbilities(player.getAbilities());
                var bucket = new ItemStack(ModItems.WOODEN_POWDER_SNOW_BUCKET.get());
                bucket.setDamageValue(damage);
                bucket.set(DataComponents.CUSTOM_NAME, Component.literal("Snow carrier"));
                player.setItemInHand(InteractionHand.MAIN_HAND, bucket);
                check(bucket.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit)).consumesAction(), "Powder snow placement");
                check(h.getBlockState(POS).is(Blocks.POWDER_SNOW), "Powder snow must be placed");
                var held = player.getMainHandItem();
                if (creative) {
                    check(held.is(ModItems.WOODEN_POWDER_SNOW_BUCKET.get()) && held.getDamageValue() == damage, "Creative powder bucket");
                } else if (damage == 9) {
                    check(held.isEmpty(), "Powder bucket must break on last use");
                } else {
                    check(held.is(ModItems.WOODEN_BUCKET.get()) && held.getDamageValue() == damage + 1, "Powder bucket durability");
                    check(Component.literal("Snow carrier").equals(held.get(DataComponents.CUSTOM_NAME)), "Powder bucket custom name");
                }
            }
        }
        h.setBlock(POS, Blocks.STONE);
        var player = h.makeMockPlayer(GameType.SURVIVAL);
        var bucket = new ItemStack(ModItems.WOODEN_POWDER_SNOW_BUCKET.get());
        bucket.setDamageValue(3);
        player.setItemInHand(InteractionHand.MAIN_HAND, bucket);
        check(!bucket.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit)).consumesAction(), "Blocked placement must fail");
        check(player.getMainHandItem().is(ModItems.WOODEN_POWDER_SNOW_BUCKET.get())
                && player.getMainHandItem().getDamageValue() == 3, "Failed placement must preserve bucket");
    }
}
