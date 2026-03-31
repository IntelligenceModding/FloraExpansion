package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.util.ModWoodTypes;
import de.artemis.floraexpansion.common.worldgen.ModTreeGrowers;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

@SuppressWarnings({"all"})
public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FloraExpansion.MODID);

    public static final DeferredBlock<PineLitterBlock> PINE_LITTER = registerBlock("pine_litter",
            PineLitterBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .sound(SoundType.PINK_PETALS)
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<LeafLitterBlock> LEAF_LITTER = registerBlock("leaf_litter",
            LeafLitterBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .sound(SoundType.PINK_PETALS)
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<LadderBlock> TWIG_LADDER = registerBlock("twig_ladder",
            LadderBlock::new,
            BlockBehaviour.Properties.of()
                    .forceSolidOff()
                    .strength(0.4F)
                    .sound(SoundType.CHERRY_WOOD)
                    .noOcclusion()
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<FlaxCropBlock> FLAX_CROP = registerBlockWithoutItem("flax_crop",
            FlaxCropBlock::new,
            BlockBehaviour.Properties.of()
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.CROP)
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<CarpetBlock> LINEN_CARPET = registerBlock("linen_carpet",
            CarpetBlock::new,
            BlockBehaviour.Properties.of()
                    .strength(0.1F)
                    .mapColor(MapColor.COLOR_BROWN)
                    .sound(SoundType.CAVE_VINES)
                    .ignitedByLava());

    public static final DeferredBlock<Block> LINEN_BLOCK = registerBlock("linen_block",
            Block::new,
            BlockBehaviour.Properties.of()
                    .strength(0.8F)
                    .mapColor(MapColor.COLOR_BROWN)
                    .sound(SoundType.CAVE_VINES)
                    .ignitedByLava());

    public static final DeferredBlock<PebblePatchBlock> PEBBLE_PATCH = registerBlock("pebble_patch",
            PebblePatchBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .noCollision()
                    .strength(0.1F)
                    .sound(SoundType.POINTED_DRIPSTONE)
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<Block> PEBBLE_BLOCK = registerBlock("pebble_block",
            Block::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.6F, 0.8F)
                    .sound(SoundType.DRIPSTONE_BLOCK));

    public static final DeferredBlock<HayBlock> FLAX_BALE = registerBlock("flax_bale",
            HayBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .instrument(NoteBlockInstrument.BANJO)
                    .strength(0.5F)
                    .sound(SoundType.MOSS));

    public static final DeferredBlock<SaplingBlock> CHERRY_PIT = registerBlock("cherry_pit",
            properties -> new SaplingBlock(ModTreeGrowers.CHERRY_PIT, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_SAPLING));

    public static final DeferredBlock<FlowerPotBlock> POTTED_CHERRY_PIT = registerBlockWithoutItem("potted_cherry_pit",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, CHERRY_PIT, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_CHERRY_SAPLING).noOcclusion());

    public static final DeferredBlock<FruitingCherryLeavesBlock> FRUITING_CHERRY_LEAVES = registerBlock("fruiting_cherry_leaves",
            FruitingCherryLeavesBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.CHERRY_LEAVES).randomTicks());

    public static final DeferredBlock<SaplingBlock> APPLE_CORE = registerBlock("apple_core",
            properties -> new SaplingBlock(ModTreeGrowers.APPLE_CORE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING));

    public static final DeferredBlock<FlowerPotBlock> POTTED_APPLE_CORE = registerBlockWithoutItem("potted_apple_core",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, APPLE_CORE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_OAK_SAPLING).noOcclusion());

    public static final DeferredBlock<FruitingOakLeavesBlock> FRUITING_OAK_LEAVES = registerBlock("fruiting_oak_leaves",
            FruitingOakLeavesBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).randomTicks());

    public static final DeferredBlock<GiantCactusBaseBlock> GIANT_CACTUS_BASE = registerBlock("giant_cactus_base",
            GiantCactusBaseBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava()
                    .randomTicks());

    public static final DeferredBlock<RotatedPillarBlock> STRIPPED_GIANT_CACTUS_BASE = registerBlock("stripped_giant_cactus_base",
            RotatedPillarBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.0F)
                    .ignitedByLava()
                    .sound(SoundType.WOOD));

    public static final DeferredBlock<GiantCactusStemBlock> GIANT_CACTUS_STEM = registerBlock("giant_cactus_stem",
            GiantCactusStemBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.8F)
                    .noOcclusion()
                    .sound(SoundType.WOOL)
                    .randomTicks());

    public static final DeferredBlock<FlowerPotBlock> POTTED_GIANT_CACTUS_STEM = registerBlockWithoutItem("potted_giant_cactus_stem",
            properties -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, GIANT_CACTUS_STEM, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_CHERRY_SAPLING).noOcclusion());

    public static final DeferredBlock<CactusThornBlock> CACTUS_THORN = registerBlock("cactus_thorn",
            CactusThornBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .instabreak()
                    .sound(SoundType.WOOL)
                    .noCollision());

    public static final DeferredBlock<CactusFlowerBlock> CACTUS_FLOWER = registerBlock("cactus_flower",
            CactusFlowerBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PINK)
                    .instabreak()
                    .noCollision()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
                    .randomTicks());

    public static final DeferredBlock<GiantCactusWoodBlock> GIANT_CACTUS_WOOD = registerBlock("giant_cactus_wood",
            GiantCactusWoodBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava());

    public static final DeferredBlock<GiantCactusWoodBlock> STRIPPED_GIANT_CACTUS_WOOD = registerBlock("stripped_giant_cactus_wood",
            GiantCactusWoodBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(1.0F)
                    .sound(SoundType.WOOD)
                    .ignitedByLava());

    public static final DeferredBlock<DesertMossBlock> DESERT_MOSS = registerBlock("desert_moss",
            DesertMossBlock::new,
            BlockBehaviour.Properties.of()
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)
                    .noOcclusion()
                    .replaceable());

    public static final DeferredBlock<CactusClusterBlock> CACTUS_CLUSTER = registerBlock("cactus_cluster",
            CactusClusterBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.MOSS)
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<CactusFruitPlantBlock> OPUNTIA_CACTUS = registerBlockWithoutItem("opuntia_cactus",
            CactusFruitPlantBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .noCollision()
                    .instabreak()
                    .sound(SoundType.MOSS)
                    .pushReaction(PushReaction.DESTROY)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .randomTicks());

    public static final DeferredBlock<Block> CACTUS_PLANKS = registerBlock("cactus_planks",
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));

    public static final DeferredBlock<Block> CACTUS_MOSAIC = registerBlock("cactus_mosaic",
            Block::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS));

    public static final DeferredBlock<StairBlock> CACTUS_STAIRS = registerBlock("cactus_stairs",
            properties -> new StairBlock(CACTUS_PLANKS.get().defaultBlockState(), properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS));

    public static final DeferredBlock<SlabBlock> CACTUS_SLAB = registerBlock("cactus_slab",
            SlabBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB));

    public static final DeferredBlock<FenceBlock> CACTUS_FENCE = registerBlock("cactus_fence",
            FenceBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE));

    public static final DeferredBlock<FenceGateBlock> CACTUS_FENCE_GATE = registerBlock("cactus_fence_gate",
            properties -> new FenceGateBlock(ModWoodTypes.CACTUS_WOOD_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE));

    public static final DeferredBlock<ButtonBlock> CACTUS_BUTTON = registerBlock("cactus_button",
            properties -> new ButtonBlock(ModWoodTypes.CACTUS_SET_TYPE, 30, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON).noCollision());

    public static final DeferredBlock<PressurePlateBlock> CACTUS_PRESSURE_PLATE = registerBlock("cactus_pressure_plate",
            properties -> new PressurePlateBlock(ModWoodTypes.CACTUS_SET_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE));

    public static final DeferredBlock<DoorBlock> CACTUS_DOOR = registerBlock("cactus_door",
            properties -> new DoorBlock(ModWoodTypes.CACTUS_SET_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR).noOcclusion());

    public static final DeferredBlock<TrapDoorBlock> CACTUS_TRAPDOOR = registerBlock("cactus_trapdoor",
            properties -> new TrapDoorBlock(ModWoodTypes.CACTUS_SET_TYPE, properties),
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR).noOcclusion());

    public static final DeferredBlock<CactusStandingSignBlock> CACTUS_SIGN = registerBlockWithoutItem("cactus_sign",
            CactusStandingSignBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN).noCollision().strength(1.0F));

    public static final DeferredBlock<CactusWallSignBlock> CACTUS_WALL_SIGN = registerBlockWithoutItem("cactus_wall_sign",
            CactusWallSignBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).noCollision().strength(1.0F));

    public static final DeferredBlock<CactusCeilingHangingSignBlock> CACTUS_HANGING_SIGN = registerBlockWithoutItem("cactus_hanging_sign",
            CactusCeilingHangingSignBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN).noCollision().strength(1.0F));

    public static final DeferredBlock<CactusWallHangingSignBlock> CACTUS_WALL_HANGING_SIGN = registerBlockWithoutItem("cactus_wall_hanging_sign",
            CactusWallHangingSignBlock::new,
            BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).noCollision().strength(1.0F));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name,
                                                                    Function<BlockBehaviour.Properties, T> blockFactory,
                                                                    BlockBehaviour.Properties properties) {
        DeferredBlock<T> block = BLOCKS.registerBlock(name, blockFactory, properties);
        registerBlockItem(name, block);
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithoutItem(String name,
                                                                               Function<BlockBehaviour.Properties, T> blockFactory,
                                                                               BlockBehaviour.Properties properties) {
        return BLOCKS.registerBlock(name, blockFactory, properties);
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.registerSimpleBlockItem(name, block);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}