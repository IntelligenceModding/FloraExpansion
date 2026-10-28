package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.block.*;
import de.artemis.floraexpansion.common.item.CrateBlockItem;

import java.util.Optional;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FloraExpansion.MODID);

    public static final DeferredBlock<Block> NEEDLE_LITTER = registerBlock("needle_litter",
            () -> new NeedleLitterBlock(blockProperties("needle_litter", BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.POPPED))));

    public static final DeferredBlock<Block> TWIG_LADDER = registerBlock("twig_ladder",
            () -> new LadderBlock(blockProperties("twig_ladder", BlockBehaviour.Properties.of().forceSolidOff().strength(0.4F).sound(SoundType.CHERRY_WOOD).noOcclusion().pushReaction(PushReaction.POPPED))));

    public static final DeferredBlock<CrateBlock> CRATE = registerBlock("crate",
            () -> new CrateBlock(blockProperties("crate", BlockBehaviour.Properties.ofFullCopy(Blocks.COMPOSTER))),
            new Item.Properties(), CrateBlockItem::new);

    public static final DeferredBlock<StrawberryCakeBlock> STRAWBERRY_CAKE = registerBlock("strawberry_cake",
            () -> new StrawberryCakeBlock(blockProperties("strawberry_cake", BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE))), new Item.Properties().stacksTo(1));

    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_CANDLE = registerCandleCake("strawberry_cake_with_candle", Blocks.CANDLE);
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_WHITE_CANDLE = registerCandleCake("strawberry_cake_with_white_candle", Blocks.DYED_CANDLE.white());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_ORANGE_CANDLE = registerCandleCake("strawberry_cake_with_orange_candle", Blocks.DYED_CANDLE.orange());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_MAGENTA_CANDLE = registerCandleCake("strawberry_cake_with_magenta_candle", Blocks.DYED_CANDLE.magenta());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_LIGHT_BLUE_CANDLE = registerCandleCake("strawberry_cake_with_light_blue_candle", Blocks.DYED_CANDLE.lightBlue());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_YELLOW_CANDLE = registerCandleCake("strawberry_cake_with_yellow_candle", Blocks.DYED_CANDLE.yellow());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_LIME_CANDLE = registerCandleCake("strawberry_cake_with_lime_candle", Blocks.DYED_CANDLE.lime());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_PINK_CANDLE = registerCandleCake("strawberry_cake_with_pink_candle", Blocks.DYED_CANDLE.pink());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_GRAY_CANDLE = registerCandleCake("strawberry_cake_with_gray_candle", Blocks.DYED_CANDLE.gray());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_LIGHT_GRAY_CANDLE = registerCandleCake("strawberry_cake_with_light_gray_candle", Blocks.DYED_CANDLE.lightGray());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_CYAN_CANDLE = registerCandleCake("strawberry_cake_with_cyan_candle", Blocks.DYED_CANDLE.cyan());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_PURPLE_CANDLE = registerCandleCake("strawberry_cake_with_purple_candle", Blocks.DYED_CANDLE.purple());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_BLUE_CANDLE = registerCandleCake("strawberry_cake_with_blue_candle", Blocks.DYED_CANDLE.blue());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_BROWN_CANDLE = registerCandleCake("strawberry_cake_with_brown_candle", Blocks.DYED_CANDLE.brown());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_GREEN_CANDLE = registerCandleCake("strawberry_cake_with_green_candle", Blocks.DYED_CANDLE.green());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_RED_CANDLE = registerCandleCake("strawberry_cake_with_red_candle", Blocks.DYED_CANDLE.red());
    public static final DeferredBlock<StrawberryCakeWithCandleBlock> STRAWBERRY_CAKE_WITH_BLACK_CANDLE = registerCandleCake("strawberry_cake_with_black_candle", Blocks.DYED_CANDLE.black());

    public static final List<DeferredBlock<StrawberryCakeWithCandleBlock>> STRAWBERRY_CANDLE_CAKES = List.of(
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

    public static final DeferredBlock<SmallBlueberryBushBlock> BLUEBERRY_BUSH =
            BLOCKS.register("blueberry_bush", () -> new SmallBlueberryBushBlock(blockProperties("blueberry_bush", BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks())));

    public static final DeferredBlock<LargeBlueberryBushBlock> LARGE_BLUEBERRY_BUSH = registerBlock("large_blueberry_bush",
            () -> new LargeBlueberryBushBlock(blockProperties("large_blueberry_bush", BlockBehaviour.Properties.ofFullCopy(Blocks.FERN).randomTicks())));

    public static final DeferredBlock<StrawberryCropBlock> STRAWBERRY_PLANT =
            BLOCKS.register("strawberry_plant", () -> new StrawberryCropBlock(blockProperties("strawberry_plant", BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.POPPED))));

    public static final DeferredBlock<FlaxBlock> FLAX =
            BLOCKS.register("flax", () -> new FlaxBlock(blockProperties("flax", BlockBehaviour.Properties.of().noCollision().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.POPPED))));

    public static final DeferredBlock<CarpetBlock> LINEN_CARPET = registerBlock("linen_carpet",
            () -> new CarpetBlock(blockProperties("linen_carpet", BlockBehaviour.Properties.of().strength(0.1F).mapColor(MapColor.COLOR_BROWN).sound(SoundType.CAVE_VINES).ignitedByLava())));

    public static final DeferredBlock<Block> LINEN_BALE = registerBlock("linen_bale",
            () -> new Block(blockProperties("linen_bale", BlockBehaviour.Properties.of().strength(0.8F).mapColor(MapColor.COLOR_BROWN).sound(SoundType.CAVE_VINES).ignitedByLava())));

    public static final DeferredBlock<Block> SCATTERED_PEBBLES = registerBlock("scattered_pebbles",
            () -> new ScatteredPebblesBlock(blockProperties("scattered_pebbles", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noCollision().strength(0.1F).sound(SoundType.POINTED_DRIPSTONE).pushReaction(PushReaction.POPPED))));

    public static final DeferredBlock<Block> PACKED_PEBBLES = registerBlock("packed_pebbles",
            () -> new Block(blockProperties("packed_pebbles", BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.6F, 0.8F).sound(SoundType.DRIPSTONE_BLOCK))));

    public static final DeferredBlock<HayBlock> FLAX_BALE = registerBlock("flax_bale",
            () -> new HayBlock(blockProperties("flax_bale", BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.BANJO).strength(0.5F).sound(SoundType.MOSS))));

    public static final DeferredBlock<Block> CHERRY_PIT = registerBlock("cherry_pit",
            () -> new SaplingBlock(ModTreeGrowers.CHERRY_PIT, blockProperties("cherry_pit", BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_SAPLING))));

    public static final DeferredBlock<Block> POTTED_CHERRY_PIT = BLOCKS.register("potted_cherry_pit",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CHERRY_PIT, blockProperties("potted_cherry_pit", BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_CHERRY_SAPLING).noOcclusion())));

    public static final DeferredBlock<Block> ORCHARD_CHERRY_LEAVES = registerBlock("orchard_cherry_leaves",
            () -> new OrchardCherryLeavesBlock(blockProperties("orchard_cherry_leaves", BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES).randomTicks())));

    public static final DeferredBlock<Block> APPLE_CORE = registerBlock("apple_core",
            () -> new SaplingBlock(ModTreeGrowers.APPLE_CORE, blockProperties("apple_core", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING))));

    public static final DeferredBlock<Block> POTTED_APPLE_CORE = BLOCKS.register("potted_apple_core",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, APPLE_CORE, blockProperties("potted_apple_core", BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING).noOcclusion())));

    public static final DeferredBlock<Block> APPLE_LEAVES = registerBlock("apple_leaves",
            () -> new AppleLeavesBlock(blockProperties("apple_leaves", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).randomTicks())));

    public static final DeferredBlock<Block> CACTUS_LOG = registerBlock("cactus_log",
            () -> new CactusLogBlock(blockProperties("cactus_log", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
                    .randomTicks())));

    public static final DeferredBlock<Block> STRIPPED_CACTUS_LOG = registerBlock("stripped_cactus_log",
            () -> new RotatedPillarBlock(blockProperties("stripped_cactus_log", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.0F)
                    .ignitedByLava()
                    .sound(SoundType.WOOD))));

    public static final DeferredBlock<Block> GIANT_CACTUS_STEM = registerBlock("giant_cactus_stem",
            () -> new GiantCactusStemBlock(blockProperties("giant_cactus_stem", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.8F)
                    .noOcclusion()
                    .sound(SoundType.WOOL)
                    .randomTicks())));

    public static final DeferredBlock<Block> POTTED_GIANT_CACTUS_STEM = BLOCKS.register("potted_giant_cactus_stem",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GIANT_CACTUS_STEM, blockProperties("potted_giant_cactus_stem", BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_CHERRY_SAPLING).noOcclusion())));

    public static final DeferredBlock<Block> CACTUS_SPINE = registerBlock("cactus_spine",
            () -> new CactusSpineBlock(blockProperties("cactus_spine", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .instabreak()
                    .sound(SoundType.WOOL)
                    .noCollision())));

    public static final DeferredBlock<Block> GIANT_CACTUS_SHOOT = registerBlock("giant_cactus_shoot",
            () -> new GiantCactusShootBlock(blockProperties("giant_cactus_shoot", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .instabreak()
                    .noCollision()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.POPPED)
                    .randomTicks())));

    public static final DeferredBlock<Block> CACTUS_WOOD = registerBlock("cactus_wood",
            () -> new CactusWoodBlock(blockProperties("cactus_wood", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava())));

    public static final DeferredBlock<Block> STRIPPED_CACTUS_WOOD = registerBlock("stripped_cactus_wood",
            () -> new CactusWoodBlock(blockProperties("stripped_cactus_wood", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava())));

    public static final DeferredBlock<Block> DESERT_MOSS = registerBlock("desert_moss",
            () -> new DesertMossBlock(blockProperties("desert_moss", BlockBehaviour.Properties.of()
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)
                    .noOcclusion()
                    .replaceable()
            )));

    public static final DeferredBlock<Block> SMALL_CACTUS = registerBlock("small_cactus",
            () -> new SmallCactusBlock(blockProperties("small_cactus", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.MOSS)
                    .pushReaction(PushReaction.POPPED))));

    public static final DeferredBlock<Block> PRICKLY_PEAR_CACTUS = BLOCKS.register("prickly_pear_cactus",
            () -> new PricklyPearCactusBlock(blockProperties("prickly_pear_cactus", BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.MOSS)
                    .pushReaction(PushReaction.POPPED)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .randomTicks())));

    public static final DeferredBlock<Block> CACTUS_PLANKS = registerBlock("cactus_planks",
            () -> new Block(blockProperties("cactus_planks", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS))));

    public static final DeferredBlock<Block> CACTUS_MOSAIC = registerBlock("cactus_mosaic",
            () -> new Block(blockProperties("cactus_mosaic", BlockBehaviour.Properties.ofFullCopy(ModBlocks.CACTUS_PLANKS.get()))));

    public static final DeferredBlock<Block> CACTUS_STAIRS = registerBlock("cactus_stairs",
            () -> new StairBlock(ModBlocks.CACTUS_PLANKS.get().defaultBlockState(),
                    blockProperties("cactus_stairs", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS))));

    public static final DeferredBlock<Block> CACTUS_SLAB = registerBlock("cactus_slab",
            () -> new SlabBlock(blockProperties("cactus_slab", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB))));

    public static final DeferredBlock<Block> CACTUS_FENCE = registerBlock("cactus_fence",
            () -> new FenceBlock(blockProperties("cactus_fence", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE))));

    public static final DeferredBlock<Block> CACTUS_FENCE_GATE = registerBlock("cactus_fence_gate",
            () -> new FenceGateBlock(ModWoodTypes.CACTUS_WOOD_TYPE,
                    blockProperties("cactus_fence_gate", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE))));

    public static final DeferredBlock<Block> CACTUS_BUTTON = registerBlock("cactus_button",
            () -> new ButtonBlock(ModWoodTypes.CACTUS_SET_TYPE, 30,
                    blockProperties("cactus_button", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON).noCollision())));

    public static final DeferredBlock<Block> CACTUS_PRESSURE_PLATE = registerBlock("cactus_pressure_plate",
            () -> new PressurePlateBlock(ModWoodTypes.CACTUS_SET_TYPE,
                    blockProperties("cactus_pressure_plate", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE))));

    public static final DeferredBlock<Block> CACTUS_DOOR = registerBlock("cactus_door",
            () -> new DoorBlock(ModWoodTypes.CACTUS_SET_TYPE,
                    blockProperties("cactus_door", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).noOcclusion())));

    public static final DeferredBlock<Block> CACTUS_TRAPDOOR = registerBlock("cactus_trapdoor",
            () -> new TrapDoorBlock(ModWoodTypes.CACTUS_SET_TYPE,
                    blockProperties("cactus_trapdoor", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).noOcclusion())));

    public static final DeferredBlock<Block> CACTUS_SIGN = BLOCKS.register("cactus_sign",
            () -> new CactusStandingSignBlock(blockProperties("cactus_sign", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).noCollision().strength(1.0F))));

    public static final DeferredBlock<Block> CACTUS_WALL_SIGN = BLOCKS.register("cactus_wall_sign",
            () -> new CactusWallSignBlock(blockProperties("cactus_wall_sign", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).noCollision().strength(1.0F))));

    public static final DeferredBlock<Block> CACTUS_HANGING_SIGN = BLOCKS.register("cactus_hanging_sign",
            () -> new CactusCeilingHangingSignBlock(blockProperties("cactus_hanging_sign", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).noCollision().strength(1.0F))));

    public static final DeferredBlock<Block> CACTUS_WALL_HANGING_SIGN = BLOCKS.register("cactus_wall_hanging_sign",
            () -> new CactusWallHangingSignBlock(blockProperties("cactus_wall_hanging_sign", BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).noCollision().strength(1.0F))));
    private static DeferredBlock<StrawberryCakeWithCandleBlock> registerCandleCake(String name, Block candle) {
        return registerBlockWithoutItem(name, () -> new StrawberryCakeWithCandleBlock(candle,
                blockProperties(name, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE))));
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, new Item.Properties());
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, Item.Properties properties) {
        return registerBlock(name, block, properties, BlockItem::new);
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, Item.Properties properties,
                                                                   BiFunction<Block, Item.Properties, ? extends BlockItem> itemFactory) {
        DeferredBlock<T> registered = BLOCKS.register(name, block);
        ModItems.ITEMS.register(name, () -> itemFactory.apply(registered.get(), itemProperties(name, properties)));
        return registered;
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithoutItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static BlockBehaviour.Properties blockProperties(String name, BlockBehaviour.Properties properties) {
        Identifier id = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name);
        return properties
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .overrideLootTable(Optional.of(ResourceKey.create(Registries.LOOT_TABLE, id.withPrefix("blocks/"))));
    }

    private static Item.Properties itemProperties(String name, Item.Properties properties) {
        return properties.setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
