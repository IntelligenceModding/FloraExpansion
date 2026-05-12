package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
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

import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FloraExpansion.MODID);

    public static final DeferredBlock<Block> PINE_LITTER = registerBlock("pine_litter",
            () -> new PineLitterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> LEAF_LITTER = registerBlock("leaf_litter",
            () -> new LeafLitterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> TWIG_LADDER = registerBlock("twig_ladder",
            () -> new LadderBlock(BlockBehaviour.Properties.of().forceSolidOff().strength(0.4F).sound(SoundType.CHERRY_WOOD).noOcclusion().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<CrateBlock> CRATE = registerBlock("crate",
            () -> new CrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COMPOSTER)));

    public static final DeferredBlock<StrawberryCakeBlock> STRAWBERRY_CAKE = registerBlock("strawberry_cake",
            () -> new StrawberryCakeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE)), new Item.Properties().stacksTo(1));

    public static final DeferredBlock<StrawberryCandleCakeBlock> STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> WHITE_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("white_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.WHITE_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> ORANGE_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("orange_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.ORANGE_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> MAGENTA_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("magenta_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.MAGENTA_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> LIGHT_BLUE_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("light_blue_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.LIGHT_BLUE_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> YELLOW_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("yellow_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.YELLOW_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> LIME_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("lime_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.LIME_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> PINK_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("pink_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.PINK_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> GRAY_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("gray_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.GRAY_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> LIGHT_GRAY_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("light_gray_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.LIGHT_GRAY_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> CYAN_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("cyan_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.CYAN_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> PURPLE_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("purple_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.PURPLE_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> BLUE_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("blue_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.BLUE_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> BROWN_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("brown_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.BROWN_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> GREEN_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("green_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.GREEN_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> RED_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("red_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.RED_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));
    public static final DeferredBlock<StrawberryCandleCakeBlock> BLACK_STRAWBERRY_CANDLE_CAKE = registerBlockWithoutItem("black_strawberry_candle_cake",
            () -> new StrawberryCandleCakeBlock(Blocks.BLACK_CANDLE, BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE_CAKE)));

    public static final DeferredBlock<SmallBlueberryBushBlock> BLUEBERRY_BUSH =
            BLOCKS.register("blueberry_bush", () -> new SmallBlueberryBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SWEET_BERRY_BUSH).randomTicks()));

    public static final DeferredBlock<LargeBlueberryBushBlock> LARGE_BLUEBERRY_BUSH = registerBlock("large_blueberry_bush",
            () -> new LargeBlueberryBushBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FERN).randomTicks()));

    public static final DeferredBlock<StrawberryCropBlock> STRAWBERRY_PLANT =
            BLOCKS.register("strawberry_plant", () -> new StrawberryCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<FlaxCropBlock> FLAX_CROP =
            BLOCKS.register("flax_crop", () -> new FlaxCropBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().instabreak().sound(SoundType.CROP).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<CarpetBlock> LINEN_CARPET = registerBlock("linen_carpet",
            () -> new CarpetBlock(BlockBehaviour.Properties.of().strength(0.1F).mapColor(MapColor.COLOR_BROWN).sound(SoundType.CAVE_VINES).ignitedByLava()));

    public static final DeferredBlock<Block> LINEN_BLOCK = registerBlock("linen_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(0.8F).mapColor(MapColor.COLOR_BROWN).sound(SoundType.CAVE_VINES).ignitedByLava()));

    public static final DeferredBlock<Block> PEBBLE_PATCH = registerBlock("pebble_patch",
            () -> new PebblePatchBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noCollission().strength(0.1F).sound(SoundType.POINTED_DRIPSTONE).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> PEBBLE_BLOCK = registerBlock("pebble_block",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.6F, 0.8F).sound(SoundType.DRIPSTONE_BLOCK)));

    public static final DeferredBlock<HayBlock> FLAX_BALE = registerBlock("flax_bale",
            () -> new HayBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).instrument(NoteBlockInstrument.BANJO).strength(0.5F).sound(SoundType.MOSS)));

    public static final DeferredBlock<Block> CHERRY_PIT = registerBlock("cherry_pit",
            () -> new SaplingBlock(ModTreeGrowers.CHERRY_PIT, BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_SAPLING)));

    public static final DeferredBlock<Block> POTTED_CHERRY_PIT = BLOCKS.register("potted_cherry_pit",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CHERRY_PIT, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_CHERRY_SAPLING).noOcclusion()));

    public static final DeferredBlock<Block> FRUITING_CHERRY_LEAVES = registerBlock("fruiting_cherry_leaves",
            () -> new FruitingCherryLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES).randomTicks()));

    public static final DeferredBlock<Block> APPLE_CORE = registerBlock("apple_core",
            () -> new SaplingBlock(ModTreeGrowers.APPLE_CORE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));

    public static final DeferredBlock<Block> POTTED_APPLE_CORE = BLOCKS.register("potted_apple_core",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, APPLE_CORE, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING).noOcclusion()));

    public static final DeferredBlock<Block> FRUITING_OAK_LEAVES = registerBlock("fruiting_oak_leaves",
            () -> new FruitingOakLeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).randomTicks()));

    public static final DeferredBlock<Block> GIANT_CACTUS_BASE = registerBlock("giant_cactus_base",
            () -> new GiantCactusBaseBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
                    .randomTicks()));

    public static final DeferredBlock<Block> STRIPPED_GIANT_CACTUS_BASE = registerBlock("stripped_giant_cactus_base",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.0F)
                    .ignitedByLava()
                    .sound(SoundType.WOOD)));

    public static final DeferredBlock<Block> GIANT_CACTUS_STEM = registerBlock("giant_cactus_stem",
            () -> new GiantCactusStemBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.8F)
                    .noOcclusion()
                    .sound(SoundType.WOOL)
                    .randomTicks()));

    public static final DeferredBlock<Block> POTTED_GIANT_CACTUS_STEM = BLOCKS.register("potted_giant_cactus_stem",
            () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GIANT_CACTUS_STEM, BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_CHERRY_SAPLING).noOcclusion()));

    public static final DeferredBlock<Block> CACTUS_THORN = registerBlock("cactus_thorn",
            () -> new CactusThornBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .instabreak()
                    .sound(SoundType.WOOL)
                    .noCollission()));

    public static final DeferredBlock<Block> CACTUS_FLOWER = registerBlock("cactus_flower",
            () -> new CactusFlowerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .instabreak()
                    .noCollission()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks()));

    public static final DeferredBlock<Block> GIANT_CACTUS_WOOD = registerBlock("giant_cactus_wood",
            () -> new GiantCactusWoodBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()));

    public static final DeferredBlock<Block> STRIPPED_GIANT_CACTUS_WOOD = registerBlock("stripped_giant_cactus_wood",
            () -> new GiantCactusWoodBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()));

    public static final DeferredBlock<Block> DESERT_MOSS = registerBlock("desert_moss",
            () -> new DesertMossBlock(BlockBehaviour.Properties.of()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)
                    .noOcclusion()
                    .replaceable()
            ));

    public static final DeferredBlock<Block> CACTUS_CLUSTER = registerBlock("cactus_cluster",
            () -> new CactusClusterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS)
                    .pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> OPUNTIA_CACTUS = BLOCKS.register("opuntia_cactus",
            () -> new CactusFruitPlantBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .randomTicks()));

    public static final DeferredBlock<Block> CACTUS_PLANKS = registerBlock("cactus_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final DeferredBlock<Block> CACTUS_MOSAIC = registerBlock("cactus_mosaic",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(ModBlocks.CACTUS_PLANKS.get())));

    public static final DeferredBlock<Block> CACTUS_STAIRS = registerBlock("cactus_stairs",
            () -> new StairBlock(ModBlocks.CACTUS_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS)));

    public static final DeferredBlock<Block> CACTUS_SLAB = registerBlock("cactus_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB)));

    public static final DeferredBlock<Block> CACTUS_FENCE = registerBlock("cactus_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));

    public static final DeferredBlock<Block> CACTUS_FENCE_GATE = registerBlock("cactus_fence_gate",
            () -> new FenceGateBlock(ModWoodTypes.CACTUS_WOOD_TYPE,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));

    public static final DeferredBlock<Block> CACTUS_BUTTON = registerBlock("cactus_button",
            () -> new ButtonBlock(ModWoodTypes.CACTUS_SET_TYPE, 30,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON).noCollission()));

    public static final DeferredBlock<Block> CACTUS_PRESSURE_PLATE = registerBlock("cactus_pressure_plate",
            () -> new PressurePlateBlock(ModWoodTypes.CACTUS_SET_TYPE,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final DeferredBlock<Block> CACTUS_DOOR = registerBlock("cactus_door",
            () -> new DoorBlock(ModWoodTypes.CACTUS_SET_TYPE,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).noOcclusion()));

    public static final DeferredBlock<Block> CACTUS_TRAPDOOR = registerBlock("cactus_trapdoor",
            () -> new TrapDoorBlock(ModWoodTypes.CACTUS_SET_TYPE,
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).noOcclusion()));

    public static final DeferredBlock<Block> CACTUS_SIGN = BLOCKS.register("cactus_sign",
            () -> new CactusStandingSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).noCollission().strength(1.0F)));

    public static final DeferredBlock<Block> CACTUS_WALL_SIGN = BLOCKS.register("cactus_wall_sign",
            () -> new CactusWallSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).noCollission().strength(1.0F).lootFrom(CACTUS_SIGN)));

    public static final DeferredBlock<Block> CACTUS_HANGING_SIGN = BLOCKS.register("cactus_hanging_sign",
            () -> new CactusCeilingHangingSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).noCollission().strength(1.0F)));

    public static final DeferredBlock<Block> CACTUS_WALL_HANGING_SIGN = BLOCKS.register("cactus_wall_hanging_sign",
            () -> new CactusWallHangingSignBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).noCollission().strength(1.0F).lootFrom(CACTUS_HANGING_SIGN)));
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return registerBlock(name, block, new Item.Properties());
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block, Item.Properties itemProperties) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, itemProperties);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithoutItem(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block, Item.Properties itemProperties) {
        ModItems.ITEMS.register(name, () -> block == CRATE
                ? new CrateBlockItem(block.get(), new Item.Properties())
                : new BlockItem(block.get(), itemProperties));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}



