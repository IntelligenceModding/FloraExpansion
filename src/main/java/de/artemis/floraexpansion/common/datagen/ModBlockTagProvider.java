package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, FloraExpansion.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(Tags.BlockItems.ORE_RATES_DENSE.block()).add(ModBlocks.CALCITE_COPPER_ORE.key(),
                ModBlocks.CALCITE_LAPIS_ORE.key(), ModBlocks.CALCITE_REDSTONE_ORE.key());
        tag(Tags.BlockItems.ORE_RATES_SINGULAR.block()).add(ModBlocks.CALCITE_COAL_ORE.key(), ModBlocks.CALCITE_IRON_ORE.key(),
                ModBlocks.CALCITE_GOLD_ORE.key(), ModBlocks.CALCITE_EMERALD_ORE.key(), ModBlocks.CALCITE_DIAMOND_ORE.key());
        for (var ore : ModBlocks.CALCITE_ORES) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ore.key());
        tag(BlockItemTags.COAL_ORES.block()).add(ModBlocks.CALCITE_COAL_ORE.key());
        tag(BlockTags.IRON_ORES).add(ModBlocks.CALCITE_IRON_ORE.key());
        tag(BlockTags.COPPER_ORES).add(ModBlocks.CALCITE_COPPER_ORE.key());
        tag(BlockTags.GOLD_ORES).add(ModBlocks.CALCITE_GOLD_ORE.key());
        tag(BlockItemTags.REDSTONE_ORES.block()).add(ModBlocks.CALCITE_REDSTONE_ORE.key());
        tag(BlockItemTags.EMERALD_ORES.block()).add(ModBlocks.CALCITE_EMERALD_ORE.key());
        tag(BlockItemTags.LAPIS_ORES.block()).add(ModBlocks.CALCITE_LAPIS_ORE.key());
        tag(BlockItemTags.DIAMOND_ORES.block()).add(ModBlocks.CALCITE_DIAMOND_ORE.key());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.CALCITE_IRON_ORE.key(), ModBlocks.CALCITE_COPPER_ORE.key(), ModBlocks.CALCITE_LAPIS_ORE.key());
        tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.CALCITE_GOLD_ORE.key(), ModBlocks.CALCITE_REDSTONE_ORE.key(),
                ModBlocks.CALCITE_EMERALD_ORE.key(), ModBlocks.CALCITE_DIAMOND_ORE.key());
        tag(de.artemis.floraexpansion.common.registry.ModTags.Blocks.GLASSWORT_PLANTABLE_ON)
                .addTag(BlockTags.DIRT).addTag(BlockTags.SAND)
                .add(Blocks.GRASS_BLOCK.builtInRegistryHolder().key(), Blocks.PODZOL.builtInRegistryHolder().key(),
                        Blocks.MYCELIUM.builtInRegistryHolder().key(), Blocks.MUD.builtInRegistryHolder().key(),
                        Blocks.MUDDY_MANGROVE_ROOTS.builtInRegistryHolder().key(), Blocks.MOSS_BLOCK.builtInRegistryHolder().key(),
                        Blocks.PALE_MOSS_BLOCK.builtInRegistryHolder().key())
                .add(ModBlocks.CRYSTALLINE_SALT.key(),ModBlocks.SMOOTH_SALT.key(),ModBlocks.CUT_SALT.key(),ModBlocks.CHISELED_SALT.key())
                .add(net.minecraft.world.level.block.Blocks.CALCITE.builtInRegistryHolder().key(),
                        net.minecraft.world.level.block.Blocks.DIORITE.builtInRegistryHolder().key());
        tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_GLASSWORT.key());
        tag(BlockTags.REPLACEABLE).add(ModBlocks.GLASSWORT.key());
        tag(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.GLASSWORT.key());
        tag(BlockTags.SWORD_EFFICIENT).add(ModBlocks.GLASSWORT.key());
        tag(BlockTags.CLIMBABLE)
                .add(ModBlocks.TWIG_LADDER.key());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SALT_WATER_CAULDRON.key())
                .add(ModBlocks.CRYSTALLINE_SALT.key())
                .add(ModBlocks.SALT_SPIKE.key())
                .add(ModBlocks.SMOOTH_SALT.key())
                .add(ModBlocks.CUT_SALT.key())
                .add(ModBlocks.CHISELED_SALT.key())
                .add(ModBlocks.CRYSTALLINE_SALT_STAIRS.key(), ModBlocks.CRYSTALLINE_SALT_SLAB.key(), ModBlocks.CRYSTALLINE_SALT_WALL.key())
                .add(ModBlocks.HAND_MILL.key());

        tag(BlockTags.STAIRS).add(ModBlocks.CRYSTALLINE_SALT_STAIRS.key());
        tag(BlockTags.SPELEOTHEMS).add(ModBlocks.SALT_SPIKE.key());
        tag(BlockTags.CAULDRONS).add(ModBlocks.SALT_WATER_CAULDRON.key());
        tag(BlockTags.DANGEROUS_FOR_TELEPORTATION).add(ModBlocks.SALT_SPIKE.key());
        tag(BlockTags.SLABS).add(ModBlocks.CRYSTALLINE_SALT_SLAB.key());
        tag(BlockTags.WALLS).add(ModBlocks.CRYSTALLINE_SALT_WALL.key());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.SALTING_TUB.key())
                .add(ModBlocks.HAND_CRANK.key())
                .add(ModBlocks.CRATE.key())
                .add(ModBlocks.CACTUS_LOG.key())
                .add(ModBlocks.STRIPPED_CACTUS_LOG.key())
                .add(ModBlocks.CACTUS_WOOD.key())
                .add(ModBlocks.STRIPPED_CACTUS_WOOD.key())
                .add(ModBlocks.GIANT_CACTUS_STEM.key())
                .add(ModBlocks.TWIG_LADDER.key())
                .add(ModBlocks.CACTUS_MOSAIC.key());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.SALT_DEPOSIT.key());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.LINEN_BALE.key())
                .add(ModBlocks.FLAX_BALE.key())
                .add(ModBlocks.APPLE_LEAVES.key())
                .add(ModBlocks.LINEN_CARPET.key())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.key())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.key())
                .add(ModBlocks.BLUEBERRY_BUSH.key());

        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(ModBlocks.FLAX.key())
                .add(ModBlocks.STRAWBERRY_PLANT.key());

        tag(BlockTags.CROPS)
                .add(ModBlocks.FLAX.key())
                .add(ModBlocks.STRAWBERRY_PLANT.key());

        var candleCakes = tag(BlockTags.CANDLE_CAKES);
        ModBlocks.STRAWBERRY_CANDLE_CAKES.forEach(cake -> candleCakes.add(cake.key()));

        tag(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.FLAX.key())
                .add(ModBlocks.STRAWBERRY_PLANT.key())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.key())
                .add(ModBlocks.BLUEBERRY_BUSH.key());

        tag(BlockTags.BEE_GROWABLES)
                .add(ModBlocks.FLAX.key())
                .add(ModBlocks.STRAWBERRY_PLANT.key())
                .add(ModBlocks.APPLE_LEAVES.key())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.key())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.key())
                .add(ModBlocks.BLUEBERRY_BUSH.key());

        tag(BlockTags.LEAVES)
                .add(ModBlocks.APPLE_LEAVES.key())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.key());

        tag(BlockTags.FLOWERS)
                .add(ModBlocks.FLOWERING_DESERT_MOSS.key())
                .add(ModBlocks.APPLE_LEAVES.key())
                .add(ModBlocks.GIANT_CACTUS_SHOOT.key())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.key());

        tag(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.GIANT_CACTUS_SHOOT.key());

        tag(BlockTags.LOGS)
                .add(ModBlocks.STRIPPED_CACTUS_LOG.key())
                .add(ModBlocks.CACTUS_LOG.key())
                .add(ModBlocks.CACTUS_WOOD.key())
                .add(ModBlocks.STRIPPED_CACTUS_WOOD.key());

        tag(BlockTags.PLANKS)
                .add(ModBlocks.CACTUS_PLANKS.key());

        tag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.CACTUS_STAIRS.key())
                .add(ModBlocks.CACTUS_MOSAIC_STAIRS.key());

        tag(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.CACTUS_SLAB.key())
                .add(ModBlocks.CACTUS_MOSAIC_SLAB.key());

        tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.CACTUS_FENCE.key());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.CACTUS_FENCE_GATE.key());

        tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.CACTUS_BUTTON.key());

        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.CACTUS_PRESSURE_PLATE.key());

        tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.CACTUS_DOOR.key());

        tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.CACTUS_TRAPDOOR.key());

        tag(BlockTags.STANDING_SIGNS)
                .add(ModBlocks.CACTUS_SIGN.key());

        tag(BlockTags.WALL_SIGNS)
                .add(ModBlocks.CACTUS_WALL_SIGN.key());

        tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_HANGING_SIGN.key());

        tag(BlockTags.WALL_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_WALL_HANGING_SIGN.key());
    }
}


