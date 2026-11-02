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
        tag(Tags.Blocks.ORE_RATES_DENSE).add(ModBlocks.CALCITE_COPPER_ORE.getKey(),
                ModBlocks.CALCITE_LAPIS_ORE.getKey(), ModBlocks.CALCITE_REDSTONE_ORE.getKey());
        tag(Tags.Blocks.ORE_RATES_SINGULAR).add(ModBlocks.CALCITE_COAL_ORE.getKey(), ModBlocks.CALCITE_IRON_ORE.getKey(),
                ModBlocks.CALCITE_GOLD_ORE.getKey(), ModBlocks.CALCITE_EMERALD_ORE.getKey(), ModBlocks.CALCITE_DIAMOND_ORE.getKey());
        for (var ore : ModBlocks.CALCITE_ORES) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ore.getKey());
        tag(BlockItemTags.COAL_ORES.block()).add(ModBlocks.CALCITE_COAL_ORE.getKey());
        tag(BlockTags.IRON_ORES).add(ModBlocks.CALCITE_IRON_ORE.getKey());
        tag(BlockTags.COPPER_ORES).add(ModBlocks.CALCITE_COPPER_ORE.getKey());
        tag(BlockTags.GOLD_ORES).add(ModBlocks.CALCITE_GOLD_ORE.getKey());
        tag(BlockItemTags.REDSTONE_ORES.block()).add(ModBlocks.CALCITE_REDSTONE_ORE.getKey());
        tag(BlockItemTags.EMERALD_ORES.block()).add(ModBlocks.CALCITE_EMERALD_ORE.getKey());
        tag(BlockItemTags.LAPIS_ORES.block()).add(ModBlocks.CALCITE_LAPIS_ORE.getKey());
        tag(BlockItemTags.DIAMOND_ORES.block()).add(ModBlocks.CALCITE_DIAMOND_ORE.getKey());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.CALCITE_IRON_ORE.getKey(), ModBlocks.CALCITE_COPPER_ORE.getKey(), ModBlocks.CALCITE_LAPIS_ORE.getKey());
        tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.CALCITE_GOLD_ORE.getKey(), ModBlocks.CALCITE_REDSTONE_ORE.getKey(),
                ModBlocks.CALCITE_EMERALD_ORE.getKey(), ModBlocks.CALCITE_DIAMOND_ORE.getKey());
        tag(de.artemis.floraexpansion.common.registry.ModTags.Blocks.GLASSWORT_PLANTABLE_ON)
                .addTag(BlockTags.DIRT).addTag(BlockTags.SAND)
                .add(Blocks.GRASS_BLOCK.builtInRegistryHolder().key(), Blocks.PODZOL.builtInRegistryHolder().key(),
                        Blocks.MYCELIUM.builtInRegistryHolder().key(), Blocks.MUD.builtInRegistryHolder().key(),
                        Blocks.MUDDY_MANGROVE_ROOTS.builtInRegistryHolder().key(), Blocks.MOSS_BLOCK.builtInRegistryHolder().key(),
                        Blocks.PALE_MOSS_BLOCK.builtInRegistryHolder().key())
                .add(ModBlocks.CRYSTALLINE_SALT.getKey(),ModBlocks.SMOOTH_SALT.getKey(),ModBlocks.CUT_SALT.getKey(),ModBlocks.CHISELED_SALT.getKey())
                .add(net.minecraft.world.level.block.Blocks.CALCITE.builtInRegistryHolder().key(),
                        net.minecraft.world.level.block.Blocks.DIORITE.builtInRegistryHolder().key());
        tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_GLASSWORT.getKey());
        tag(BlockTags.REPLACEABLE).add(ModBlocks.GLASSWORT.getKey());
        tag(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.GLASSWORT.getKey());
        tag(BlockTags.SWORD_EFFICIENT).add(ModBlocks.GLASSWORT.getKey());
        tag(BlockTags.CLIMBABLE)
                .add(ModBlocks.TWIG_LADDER.getKey());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SALT_WATER_CAULDRON.getKey())
                .add(ModBlocks.CRYSTALLINE_SALT.getKey())
                .add(ModBlocks.SALT_SPIKE.getKey())
                .add(ModBlocks.SMOOTH_SALT.getKey())
                .add(ModBlocks.CUT_SALT.getKey())
                .add(ModBlocks.CHISELED_SALT.getKey())
                .add(ModBlocks.CRYSTALLINE_SALT_STAIRS.getKey(), ModBlocks.CRYSTALLINE_SALT_SLAB.getKey(), ModBlocks.CRYSTALLINE_SALT_WALL.getKey())
                .add(ModBlocks.HAND_MILL.getKey());

        tag(BlockTags.STAIRS).add(ModBlocks.CRYSTALLINE_SALT_STAIRS.getKey());
        tag(BlockTags.SPELEOTHEMS).add(ModBlocks.SALT_SPIKE.getKey());
        tag(BlockTags.CAULDRONS).add(ModBlocks.SALT_WATER_CAULDRON.getKey());
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, net.minecraft.resources.Identifier.withDefaultNamespace("dangerous_for_teleportation"))).add(ModBlocks.SALT_SPIKE.getKey());
        tag(BlockTags.SLABS).add(ModBlocks.CRYSTALLINE_SALT_SLAB.getKey());
        tag(BlockTags.WALLS).add(ModBlocks.CRYSTALLINE_SALT_WALL.getKey());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.SALTING_TUB.getKey())
                .add(ModBlocks.HAND_CRANK.getKey())
                .add(ModBlocks.CRATE.getKey())
                .add(ModBlocks.CACTUS_LOG.getKey())
                .add(ModBlocks.STRIPPED_CACTUS_LOG.getKey())
                .add(ModBlocks.CACTUS_WOOD.getKey())
                .add(ModBlocks.STRIPPED_CACTUS_WOOD.getKey())
                .add(ModBlocks.GIANT_CACTUS_STEM.getKey())
                .add(ModBlocks.TWIG_LADDER.getKey())
                .add(ModBlocks.CACTUS_MOSAIC.getKey());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.SALT_DEPOSIT.getKey());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.LINEN_BALE.getKey())
                .add(ModBlocks.FLAX_BALE.getKey())
                .add(ModBlocks.APPLE_LEAVES.getKey())
                .add(ModBlocks.LINEN_CARPET.getKey())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.getKey())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.getKey())
                .add(ModBlocks.BLUEBERRY_BUSH.getKey());

        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(ModBlocks.FLAX.getKey())
                .add(ModBlocks.STRAWBERRY_PLANT.getKey());

        tag(BlockTags.CROPS)
                .add(ModBlocks.FLAX.getKey())
                .add(ModBlocks.STRAWBERRY_PLANT.getKey());

        var candleCakes = tag(BlockTags.CANDLE_CAKES);
        ModBlocks.STRAWBERRY_CANDLE_CAKES.forEach(cake -> candleCakes.add(cake.getKey()));

        tag(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.FLAX.getKey())
                .add(ModBlocks.STRAWBERRY_PLANT.getKey())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.getKey())
                .add(ModBlocks.BLUEBERRY_BUSH.getKey());

        tag(BlockTags.BEE_GROWABLES)
                .add(ModBlocks.FLAX.getKey())
                .add(ModBlocks.STRAWBERRY_PLANT.getKey())
                .add(ModBlocks.APPLE_LEAVES.getKey())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.getKey())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.getKey())
                .add(ModBlocks.BLUEBERRY_BUSH.getKey());

        tag(BlockTags.LEAVES)
                .add(ModBlocks.APPLE_LEAVES.getKey())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.getKey());

        tag(BlockTags.FLOWERS)
                .add(ModBlocks.FLOWERING_DESERT_MOSS.getKey())
                .add(ModBlocks.APPLE_LEAVES.getKey())
                .add(ModBlocks.GIANT_CACTUS_SHOOT.getKey())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.getKey());

        tag(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.GIANT_CACTUS_SHOOT.getKey());

        tag(BlockTags.LOGS)
                .add(ModBlocks.STRIPPED_CACTUS_LOG.getKey())
                .add(ModBlocks.CACTUS_LOG.getKey())
                .add(ModBlocks.CACTUS_WOOD.getKey())
                .add(ModBlocks.STRIPPED_CACTUS_WOOD.getKey());

        tag(BlockTags.PLANKS)
                .add(ModBlocks.CACTUS_PLANKS.getKey());

        tag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.CACTUS_STAIRS.getKey())
                .add(ModBlocks.CACTUS_MOSAIC_STAIRS.getKey());

        tag(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.CACTUS_SLAB.getKey())
                .add(ModBlocks.CACTUS_MOSAIC_SLAB.getKey());

        tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.CACTUS_FENCE.getKey());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.CACTUS_FENCE_GATE.getKey());

        tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.CACTUS_BUTTON.getKey());

        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.CACTUS_PRESSURE_PLATE.getKey());

        tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.CACTUS_DOOR.getKey());

        tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.CACTUS_TRAPDOOR.getKey());

        tag(BlockTags.STANDING_SIGNS)
                .add(ModBlocks.CACTUS_SIGN.getKey());

        tag(BlockTags.WALL_SIGNS)
                .add(ModBlocks.CACTUS_WALL_SIGN.getKey());

        tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_HANGING_SIGN.getKey());

        tag(BlockTags.WALL_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_WALL_HANGING_SIGN.getKey());
    }
}
