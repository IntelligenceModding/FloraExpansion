package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.worldgen.ModTreeGrowers;
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
                    .sound(SoundType.WOOL)));

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
                    .sound(SoundType.GRASS)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}