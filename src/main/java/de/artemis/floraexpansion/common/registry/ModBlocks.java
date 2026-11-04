package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.block.*;
import de.artemis.floraexpansion.common.item.CrateBlockItem;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, FloraExpansion.MODID);

    public static final RegistryObject<DropExperienceBlock> CALCITE_COAL_ORE = registerCalciteOre("coal", Blocks.COAL_ORE, 0, 2);
    public static final RegistryObject<DropExperienceBlock> CALCITE_IRON_ORE = registerCalciteOre("iron", Blocks.IRON_ORE, 0, 0);
    public static final RegistryObject<DropExperienceBlock> CALCITE_COPPER_ORE = registerCalciteOre("copper", Blocks.COPPER_ORE, 0, 0);
    public static final RegistryObject<DropExperienceBlock> CALCITE_GOLD_ORE = registerCalciteOre("gold", Blocks.GOLD_ORE, 0, 0);
    public static final RegistryObject<RedStoneOreBlock> CALCITE_REDSTONE_ORE = registerBlock("calcite_redstone_ore",
            () -> new RedStoneOreBlock(blockProperties("calcite_redstone_ore", BlockBehaviour.Properties.copy(Blocks.REDSTONE_ORE)
                    .mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.CALCITE))), new Item.Properties());
    public static final RegistryObject<DropExperienceBlock> CALCITE_EMERALD_ORE = registerCalciteOre("emerald", Blocks.EMERALD_ORE, 3, 7);
    public static final RegistryObject<DropExperienceBlock> CALCITE_LAPIS_ORE = registerCalciteOre("lapis", Blocks.LAPIS_ORE, 2, 5);
    public static final RegistryObject<DropExperienceBlock> CALCITE_DIAMOND_ORE = registerCalciteOre("diamond", Blocks.DIAMOND_ORE, 3, 7);
    public static final List<RegistryObject<? extends Block>> CALCITE_ORES = List.of(CALCITE_COAL_ORE, CALCITE_IRON_ORE,
            CALCITE_COPPER_ORE, CALCITE_GOLD_ORE, CALCITE_REDSTONE_ORE, CALCITE_EMERALD_ORE, CALCITE_LAPIS_ORE, CALCITE_DIAMOND_ORE);

    public static final RegistryObject<Block> NEEDLE_LITTER = registerBlock("needle_litter",
            () -> new NeedleLitterBlock(blockProperties("needle_litter", BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY))));

    public static final RegistryObject<LeafLitterBlock> LEAF_LITTER = registerBlock("leaf_litter",
            () -> new LeafLitterBlock(blockProperties("leaf_litter", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY))));

    public static final RegistryObject<Block> TWIG_LADDER = registerBlock("twig_ladder",
            () -> new LadderBlock(blockProperties("twig_ladder", BlockBehaviour.Properties.of().forceSolidOff().strength(0.4F).sound(SoundType.CHERRY_WOOD).noOcclusion().pushReaction(PushReaction.DESTROY))));

    public static final RegistryObject<CrateBlock> CRATE = registerBlock("crate",
            () -> new CrateBlock(blockProperties("crate", BlockBehaviour.Properties.copy(Blocks.COMPOSTER))),
            new Item.Properties(), CrateBlockItem::new);

    public static final RegistryObject<JarBlock> GLASS_JAR = registerJar("glass_jar");
    public static final RegistryObject<LiquidBlock> SALT_WATER = registerBlockWithoutItem("salt_water",
            () -> new SaltWaterBlock(blockProperties("salt_water", BlockBehaviour.Properties.copy(Blocks.WATER).randomTicks())));
    public static final RegistryObject<SaltWaterCauldronBlock> SALT_WATER_CAULDRON = registerBlockWithoutItem("salt_water_cauldron",
            () -> new SaltWaterCauldronBlock(blockProperties("salt_water_cauldron", BlockBehaviour.Properties.copy(Blocks.WATER_CAULDRON))));
    public static final RegistryObject<GlasswortBlock> GLASSWORT = registerBlock("glasswort",
            () -> new GlasswortBlock(blockProperties("glasswort", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT).noCollission().instabreak().sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY).noOcclusion())), new Item.Properties()
                    .food(ModFoods.GLASSWORT));
    public static final RegistryObject<FlowerPotBlock> POTTED_GLASSWORT = registerBlockWithoutItem("potted_glasswort",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GLASSWORT,
                    blockProperties("potted_glasswort", BlockBehaviour.Properties.copy(Blocks.POTTED_FERN))));
    public static final RegistryObject<SaltingTubBlock> SALTING_TUB = registerBlock("salting_tub",
            () -> new SaltingTubBlock(blockProperties("salting_tub", BlockBehaviour.Properties.copy(Blocks.COMPOSTER)
                    .noOcclusion())));
    public static final RegistryObject<Block> SALT_DEPOSIT = registerBlockWithoutItem("salt_deposit",
            () -> new SaltDepositBlock(blockProperties("salt_deposit", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.SNOW).strength(0.5F).noOcclusion().sound(SoundType.SAND))));
    public static final RegistryObject<HandCrankBlock> HAND_CRANK = registerBlock("hand_crank",
            () -> new HandCrankBlock(blockProperties("hand_crank", BlockBehaviour.Properties.of()
                    .strength(0.5F).sound(SoundType.WOOD).noOcclusion().dynamicShape().pushReaction(PushReaction.DESTROY))));
    public static final RegistryObject<HandMillBlock> HAND_MILL = registerBlock("hand_mill",
            () -> new HandMillBlock(blockProperties("hand_mill", BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(2.0F).noOcclusion().dynamicShape())));
    public static final RegistryObject<Block> CRYSTALLINE_SALT = registerBlock("crystalline_salt",
            () -> new Block(blockProperties("crystalline_salt", BlockBehaviour.Properties.copy(Blocks.CALCITE)
                    .requiresCorrectToolForDrops())));
    public static final RegistryObject<SaltSpikeBlock> SALT_SPIKE = registerBlock("salt_spike",
            () -> new SaltSpikeBlock(blockProperties("salt_spike", BlockBehaviour.Properties.copy(Blocks.POINTED_DRIPSTONE)
                    .mapColor(MapColor.SNOW))));
    public static final RegistryObject<Block> SMOOTH_SALT = registerBlock("smooth_salt",
            () -> new Block(blockProperties("smooth_salt", BlockBehaviour.Properties.copy(CRYSTALLINE_SALT.get()))));
    public static final RegistryObject<Block> CUT_SALT = registerBlock("cut_salt",
            () -> new Block(blockProperties("cut_salt", BlockBehaviour.Properties.copy(CRYSTALLINE_SALT.get()))));
    public static final RegistryObject<Block> CHISELED_SALT = registerBlock("chiseled_salt",
            () -> new Block(blockProperties("chiseled_salt", BlockBehaviour.Properties.copy(CRYSTALLINE_SALT.get()))));
    public static final RegistryObject<StairBlock> CRYSTALLINE_SALT_STAIRS = registerBlock("crystalline_salt_stairs",
            () -> new StairBlock(CRYSTALLINE_SALT.get().defaultBlockState(),
                    blockProperties("crystalline_salt_stairs", BlockBehaviour.Properties.copy(CRYSTALLINE_SALT.get()))));
    public static final RegistryObject<SlabBlock> CRYSTALLINE_SALT_SLAB = registerBlock("crystalline_salt_slab",
            () -> new SlabBlock(blockProperties("crystalline_salt_slab", BlockBehaviour.Properties.copy(CRYSTALLINE_SALT.get()))));
    public static final RegistryObject<WallBlock> CRYSTALLINE_SALT_WALL = registerBlock("crystalline_salt_wall",
            () -> new WallBlock(blockProperties("crystalline_salt_wall", BlockBehaviour.Properties.copy(CRYSTALLINE_SALT.get()))));
    public static final RegistryObject<JamJarBlock> BLUEBERRY_JAM = registerJamJar("blueberry_jam");
    public static final RegistryObject<JamJarBlock> STRAWBERRY_JAM = registerJamJar("strawberry_jam");
    public static final RegistryObject<JamJarBlock> CHERRY_JAM = registerJamJar("cherry_jam");
    public static final RegistryObject<JamJarBlock> APPLE_JAM = registerJamJar("apple_jam");

    public static final RegistryObject<StrawberryCakeBlock> STRAWBERRY_CAKE = registerBlock("strawberry_cake",
            () -> new StrawberryCakeBlock(blockProperties("strawberry_cake", BlockBehaviour.Properties.copy(Blocks.CAKE))), new Item.Properties().stacksTo(1));

    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_CANDLE = registerCandleCake("strawberry_cake_with_candle", Blocks.CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_WHITE_CANDLE = registerCandleCake("strawberry_cake_with_white_candle", Blocks.WHITE_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_ORANGE_CANDLE = registerCandleCake("strawberry_cake_with_orange_candle", Blocks.ORANGE_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_MAGENTA_CANDLE = registerCandleCake("strawberry_cake_with_magenta_candle", Blocks.MAGENTA_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_LIGHT_BLUE_CANDLE = registerCandleCake("strawberry_cake_with_light_blue_candle", Blocks.LIGHT_BLUE_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_YELLOW_CANDLE = registerCandleCake("strawberry_cake_with_yellow_candle", Blocks.YELLOW_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_LIME_CANDLE = registerCandleCake("strawberry_cake_with_lime_candle", Blocks.LIME_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_PINK_CANDLE = registerCandleCake("strawberry_cake_with_pink_candle", Blocks.PINK_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_GRAY_CANDLE = registerCandleCake("strawberry_cake_with_gray_candle", Blocks.GRAY_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_LIGHT_GRAY_CANDLE = registerCandleCake("strawberry_cake_with_light_gray_candle", Blocks.LIGHT_GRAY_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_CYAN_CANDLE = registerCandleCake("strawberry_cake_with_cyan_candle", Blocks.CYAN_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_PURPLE_CANDLE = registerCandleCake("strawberry_cake_with_purple_candle", Blocks.PURPLE_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_BLUE_CANDLE = registerCandleCake("strawberry_cake_with_blue_candle", Blocks.BLUE_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_BROWN_CANDLE = registerCandleCake("strawberry_cake_with_brown_candle", Blocks.BROWN_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_GREEN_CANDLE = registerCandleCake("strawberry_cake_with_green_candle", Blocks.GREEN_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_RED_CANDLE = registerCandleCake("strawberry_cake_with_red_candle", Blocks.RED_CANDLE);
    public static final RegistryObject<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_BLACK_CANDLE = registerCandleCake("strawberry_cake_with_black_candle", Blocks.BLACK_CANDLE);

    public static final List<RegistryObject<StrawberryCakeWithCandleBlock>> STRAWBERRY_CANDLE_CAKES = List.of(
            STRAWBERRY_CAKE_WITH_CANDLE,
            STRAWBERRY_CAKE_WITH_WHITE_CANDLE,
            STRAWBERRY_CAKE_WITH_ORANGE_CANDLE,
            STRAWBERRY_CAKE_WITH_MAGENTA_CANDLE,
            STRAWBERRY_CAKE_WITH_LIGHT_BLUE_CANDLE,
            STRAWBERRY_CAKE_WITH_YELLOW_CANDLE,
            STRAWBERRY_CAKE_WITH_LIME_CANDLE,
            STRAWBERRY_CAKE_WITH_PINK_CANDLE,
            STRAWBERRY_CAKE_WITH_GRAY_CANDLE,
            STRAWBERRY_CAKE_WITH_LIGHT_GRAY_CANDLE,
            STRAWBERRY_CAKE_WITH_CYAN_CANDLE,
            STRAWBERRY_CAKE_WITH_PURPLE_CANDLE,
            STRAWBERRY_CAKE_WITH_BLUE_CANDLE,
            STRAWBERRY_CAKE_WITH_BROWN_CANDLE,
            STRAWBERRY_CAKE_WITH_GREEN_CANDLE,
            STRAWBERRY_CAKE_WITH_RED_CANDLE,
            STRAWBERRY_CAKE_WITH_BLACK_CANDLE
    );

    public static final RegistryObject<SmallBlueberryBushBlock> BLUEBERRY_BUSH =
            BLOCKS.register("blueberry_bush", () -> new SmallBlueberryBushBlock(blockProperties("blueberry_bush", BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH).randomTicks())));

    public static final RegistryObject<LargeBlueberryBushBlock> LARGE_BLUEBERRY_BUSH = registerBlock("large_blueberry_bush",
            () -> new LargeBlueberryBushBlock(blockProperties("large_blueberry_bush", BlockBehaviour.Properties.copy(Blocks.FERN).randomTicks())));

    public static final RegistryObject<StrawberryCropBlock> STRAWBERRY_PLANT =
            BLOCKS.register("strawberry_plant", () -> new StrawberryCropBlock(blockProperties("strawberry_plant", BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY))));

    public static final RegistryObject<FlaxBlock> FLAX =
            BLOCKS.register("flax", () -> new FlaxBlock(blockProperties("flax", BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY))));

    public static final RegistryObject<CarpetBlock> LINEN_CARPET = registerBlock("linen_carpet",
            () -> new CarpetBlock(blockProperties("linen_carpet", BlockBehaviour.Properties.of().strength(0.1F).mapColor(MapColor.COLOR_BROWN).sound(SoundType.CAVE_VINES).ignitedByLava())));

    public static final RegistryObject<Block> LINEN_BALE = registerBlock("linen_bale",
            () -> new Block(blockProperties("linen_bale", BlockBehaviour.Properties.of().strength(0.8F).mapColor(MapColor.COLOR_BROWN).sound(SoundType.CAVE_VINES).ignitedByLava())));

    public static final RegistryObject<Block> SCATTERED_PEBBLES = registerBlock("scattered_pebbles",
            () -> new ScatteredPebblesBlock(blockProperties("scattered_pebbles", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noCollission().strength(0.1F).sound(SoundType.POINTED_DRIPSTONE).pushReaction(PushReaction.DESTROY))));


    public static final RegistryObject<HayBlock> FLAX_BALE = registerBlock("flax_bale",
            () -> new HayBlock(blockProperties("flax_bale", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.BANJO).strength(0.5F).sound(SoundType.MOSS))));

    public static final RegistryObject<Block> CHERRY_PIT = registerBlock("cherry_pit",
            () -> new SaplingBlock(ModTreeGrowers.CHERRY_PIT, blockProperties("cherry_pit", BlockBehaviour.Properties.copy(Blocks.CHERRY_SAPLING))));

    public static final RegistryObject<Block> POTTED_CHERRY_PIT = BLOCKS.register("potted_cherry_pit",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CHERRY_PIT, blockProperties("potted_cherry_pit", BlockBehaviour.Properties.copy(Blocks.POTTED_CHERRY_SAPLING).noOcclusion())));

    public static final RegistryObject<Block> ORCHARD_CHERRY_LEAVES = registerBlock("orchard_cherry_leaves",
            () -> new OrchardCherryLeavesBlock(blockProperties("orchard_cherry_leaves", BlockBehaviour.Properties.copy(Blocks.CHERRY_LEAVES).randomTicks())));

    public static final RegistryObject<Block> APPLE_CORE = registerBlock("apple_core",
            () -> new SaplingBlock(ModTreeGrowers.APPLE_CORE, blockProperties("apple_core", BlockBehaviour.Properties.copy(Blocks.OAK_SAPLING))));

    public static final RegistryObject<Block> POTTED_APPLE_CORE = BLOCKS.register("potted_apple_core",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, APPLE_CORE, blockProperties("potted_apple_core", BlockBehaviour.Properties.copy(Blocks.POTTED_OAK_SAPLING).noOcclusion())));

    public static final RegistryObject<Block> APPLE_LEAVES = registerBlock("apple_leaves",
            () -> new AppleLeavesBlock(blockProperties("apple_leaves", BlockBehaviour.Properties.copy(Blocks.OAK_LEAVES).randomTicks())));

    public static final RegistryObject<Block> CACTUS_LOG = registerBlock("cactus_log",
            () -> new CactusLogBlock(blockProperties("cactus_log", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
                    .randomTicks())));

    public static final RegistryObject<Block> STRIPPED_CACTUS_LOG = registerBlock("stripped_cactus_log",
            () -> new RotatedPillarBlock(blockProperties("stripped_cactus_log", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.0F)
                    .ignitedByLava()
                    .sound(SoundType.WOOD))));

    public static final RegistryObject<Block> GIANT_CACTUS_STEM = registerBlock("giant_cactus_stem",
            () -> new GiantCactusStemBlock(blockProperties("giant_cactus_stem", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.8F)
                    .noOcclusion()
                    .sound(SoundType.WOOL)
                    .randomTicks())));

    public static final RegistryObject<Block> POTTED_GIANT_CACTUS_STEM = BLOCKS.register("potted_giant_cactus_stem",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GIANT_CACTUS_STEM, blockProperties("potted_giant_cactus_stem", BlockBehaviour.Properties.copy(Blocks.POTTED_CHERRY_SAPLING).noOcclusion())));

    public static final RegistryObject<Block> CACTUS_SPINE = registerBlock("cactus_spine",
            () -> new CactusSpineBlock(blockProperties("cactus_spine", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .instabreak()
                    .sound(SoundType.WOOL)
                    .noCollission())));

    public static final RegistryObject<Block> GIANT_CACTUS_SHOOT = registerBlock("giant_cactus_shoot",
            () -> new GiantCactusShootBlock(blockProperties("giant_cactus_shoot", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .instabreak()
                    .noCollission()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks())));

    public static final RegistryObject<Block> CACTUS_WOOD = registerBlock("cactus_wood",
            () -> new CactusWoodBlock(blockProperties("cactus_wood", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava())));

    public static final RegistryObject<Block> STRIPPED_CACTUS_WOOD = registerBlock("stripped_cactus_wood",
            () -> new CactusWoodBlock(blockProperties("stripped_cactus_wood", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava())));

    public static final RegistryObject<Block> DESERT_MOSS = registerBlock("desert_moss",
            () -> new DesertMossBlock(blockProperties("desert_moss", BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)
                    .noOcclusion()
                    .replaceable()
            )));

    public static final RegistryObject<Block> FLOWERING_DESERT_MOSS = registerBlock("flowering_desert_moss",
            () -> new DesertMossBlock(blockProperties("flowering_desert_moss",
                    BlockBehaviour.Properties.copy(DESERT_MOSS.get())), true));

    public static final RegistryObject<Block> SMALL_CACTUS = registerBlock("small_cactus",
            () -> new SmallCactusBlock(blockProperties("small_cactus", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS)
                    .pushReaction(PushReaction.DESTROY))));

    public static final RegistryObject<Block> PRICKLY_PEAR_CACTUS = BLOCKS.register("prickly_pear_cactus",
            () -> new PricklyPearCactusBlock(blockProperties("prickly_pear_cactus", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .randomTicks())));

    public static final RegistryObject<Block> CACTUS_PLANKS = registerBlock("cactus_planks",
            () -> new Block(blockProperties("cactus_planks", BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS))));

    public static final RegistryObject<Block> CACTUS_MOSAIC = registerBlock("cactus_mosaic",
            () -> new Block(blockProperties("cactus_mosaic", BlockBehaviour.Properties.copy(ModBlocks.CACTUS_PLANKS.get()))));

    public static final RegistryObject<Block> CACTUS_MOSAIC_STAIRS = registerBlock("cactus_mosaic_stairs",
            () -> new StairBlock(ModBlocks.CACTUS_MOSAIC.get().defaultBlockState(),
                    blockProperties("cactus_mosaic_stairs", BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS))));

    public static final RegistryObject<Block> CACTUS_MOSAIC_SLAB = registerBlock("cactus_mosaic_slab",
            () -> new SlabBlock(blockProperties("cactus_mosaic_slab", BlockBehaviour.Properties.copy(Blocks.OAK_SLAB))));

    public static final RegistryObject<Block> CACTUS_STAIRS = registerBlock("cactus_stairs",
            () -> new StairBlock(ModBlocks.CACTUS_PLANKS.get().defaultBlockState(),
                    blockProperties("cactus_stairs", BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS))));

    public static final RegistryObject<Block> CACTUS_SLAB = registerBlock("cactus_slab",
            () -> new SlabBlock(blockProperties("cactus_slab", BlockBehaviour.Properties.copy(Blocks.OAK_SLAB))));

    public static final RegistryObject<Block> CACTUS_FENCE = registerBlock("cactus_fence",
            () -> new FenceBlock(blockProperties("cactus_fence", BlockBehaviour.Properties.copy(Blocks.OAK_FENCE))));

    public static final RegistryObject<Block> CACTUS_FENCE_GATE = registerBlock("cactus_fence_gate",
            () -> new FenceGateBlock(blockProperties("cactus_fence_gate", BlockBehaviour.Properties.copy(Blocks.OAK_FENCE_GATE)), ModWoodTypes.CACTUS_WOOD_TYPE));

    public static final RegistryObject<Block> CACTUS_BUTTON = registerBlock("cactus_button",
            () -> new ButtonBlock(blockProperties("cactus_button", BlockBehaviour.Properties.copy(Blocks.OAK_BUTTON).noCollission()), ModWoodTypes.CACTUS_SET_TYPE, 30, true));

    public static final RegistryObject<Block> CACTUS_PRESSURE_PLATE = registerBlock("cactus_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, blockProperties("cactus_pressure_plate", BlockBehaviour.Properties.copy(Blocks.OAK_PRESSURE_PLATE)), ModWoodTypes.CACTUS_SET_TYPE));

    public static final RegistryObject<Block> CACTUS_DOOR = registerBlock("cactus_door",
            () -> new DoorBlock(blockProperties("cactus_door", BlockBehaviour.Properties.copy(Blocks.OAK_DOOR).noOcclusion()), ModWoodTypes.CACTUS_SET_TYPE));

    public static final RegistryObject<Block> CACTUS_TRAPDOOR = registerBlock("cactus_trapdoor",
            () -> new TrapDoorBlock(blockProperties("cactus_trapdoor", BlockBehaviour.Properties.copy(Blocks.OAK_TRAPDOOR).noOcclusion()), ModWoodTypes.CACTUS_SET_TYPE));

    public static final RegistryObject<Block> CACTUS_SIGN = BLOCKS.register("cactus_sign",
            () -> new CactusStandingSignBlock(blockProperties("cactus_sign", BlockBehaviour.Properties.copy(Blocks.OAK_SIGN).noCollission().strength(1.0F))));

    public static final RegistryObject<Block> CACTUS_WALL_SIGN = BLOCKS.register("cactus_wall_sign",
            () -> new CactusWallSignBlock(blockProperties("cactus_wall_sign", BlockBehaviour.Properties.copy(Blocks.OAK_WALL_SIGN).noCollission().strength(1.0F))));

    public static final RegistryObject<Block> CACTUS_HANGING_SIGN = BLOCKS.register("cactus_hanging_sign",
            () -> new CactusCeilingHangingSignBlock(blockProperties("cactus_hanging_sign", BlockBehaviour.Properties.copy(Blocks.OAK_HANGING_SIGN).noCollission().strength(1.0F))));

    public static final RegistryObject<Block> CACTUS_WALL_HANGING_SIGN = BLOCKS.register("cactus_wall_hanging_sign",
            () -> new CactusWallHangingSignBlock(blockProperties("cactus_wall_hanging_sign", BlockBehaviour.Properties.copy(Blocks.OAK_WALL_HANGING_SIGN).noCollission().strength(1.0F))));
    private static RegistryObject<DropExperienceBlock> registerCalciteOre(String mineral, Block original, int minXp, int maxXp) {
        String name = "calcite_" + mineral + "_ore";
        return registerBlock(name, () -> new DropExperienceBlock(blockProperties(name, BlockBehaviour.Properties.copy(original)
                        .mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.CALCITE)), net.minecraft.util.valueproviders.UniformInt.of(minXp, maxXp)), new Item.Properties());
    }

    private static RegistryObject<StrawberryCakeWithCandleBlock> registerCandleCake(String name, Block candle) {
        return registerBlockWithoutItem(name, () -> new StrawberryCakeWithCandleBlock(candle,
                blockProperties(name, BlockBehaviour.Properties.copy(Blocks.CANDLE_CAKE)
                        .lightLevel(state -> state.getValue(StrawberryCakeWithCandleBlock.LIT)
                                ? 3 * state.getValue(StrawberryCakeWithCandleBlock.CANDLES) : 0))));
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, new Item.Properties());
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Item.Properties properties) {
        return registerBlock(name, block, properties, BlockItem::new);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, Item.Properties properties,
                                                                   BiFunction<Block, Item.Properties, ? extends BlockItem> itemFactory) {
        RegistryObject<T> registered = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> itemFactory.apply(registered.get(), itemProperties(name, properties)));
        return registered;
    }

    private static <T extends Block> RegistryObject<T> registerBlockWithoutItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static RegistryObject<JarBlock> registerJar(String name) {
        return registerBlockWithoutItem(name, () -> new JarBlock(blockProperties(name,
                BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(0.3F)
                        .sound(SoundType.GLASS).noOcclusion().forceSolidOff().pushReaction(PushReaction.DESTROY))));
    }

    private static RegistryObject<JamJarBlock> registerJamJar(String name) {
        return registerBlockWithoutItem(name, () -> new JamJarBlock(blockProperties(name,
                BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(0.3F)
                        .sound(SoundType.GLASS).noOcclusion().forceSolidOff().pushReaction(PushReaction.DESTROY))));
    }

    private static BlockBehaviour.Properties blockProperties(String name, BlockBehaviour.Properties properties) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, name);
        properties.drops = id.withPrefix("blocks/");
        return properties;
    }

    private static Item.Properties itemProperties(String name, Item.Properties properties) {
        return properties;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
