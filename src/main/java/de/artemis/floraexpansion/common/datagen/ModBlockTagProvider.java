package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;

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
        tag(Tags.Blocks.ORE_RATES_DENSE).add(ModBlocks.CALCITE_COPPER_ORE.get(),
                ModBlocks.CALCITE_LAPIS_ORE.get(), ModBlocks.CALCITE_REDSTONE_ORE.get());
        tag(Tags.Blocks.ORE_RATES_SINGULAR).add(ModBlocks.CALCITE_COAL_ORE.get(), ModBlocks.CALCITE_IRON_ORE.get(),
                ModBlocks.CALCITE_GOLD_ORE.get(), ModBlocks.CALCITE_EMERALD_ORE.get(), ModBlocks.CALCITE_DIAMOND_ORE.get());
        for (var ore : ModBlocks.CALCITE_ORES) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ore.get());
        tag(BlockTags.COAL_ORES).add(ModBlocks.CALCITE_COAL_ORE.get());
        tag(BlockTags.IRON_ORES).add(ModBlocks.CALCITE_IRON_ORE.get());
        tag(BlockTags.COPPER_ORES).add(ModBlocks.CALCITE_COPPER_ORE.get());
        tag(BlockTags.GOLD_ORES).add(ModBlocks.CALCITE_GOLD_ORE.get());
        tag(BlockTags.REDSTONE_ORES).add(ModBlocks.CALCITE_REDSTONE_ORE.get());
        tag(BlockTags.EMERALD_ORES).add(ModBlocks.CALCITE_EMERALD_ORE.get());
        tag(BlockTags.LAPIS_ORES).add(ModBlocks.CALCITE_LAPIS_ORE.get());
        tag(BlockTags.DIAMOND_ORES).add(ModBlocks.CALCITE_DIAMOND_ORE.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.CALCITE_IRON_ORE.get(), ModBlocks.CALCITE_COPPER_ORE.get(), ModBlocks.CALCITE_LAPIS_ORE.get());
        tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.CALCITE_GOLD_ORE.get(), ModBlocks.CALCITE_REDSTONE_ORE.get(),
                ModBlocks.CALCITE_EMERALD_ORE.get(), ModBlocks.CALCITE_DIAMOND_ORE.get());
        tag(de.artemis.floraexpansion.common.registry.ModTags.Blocks.GLASSWORT_PLANTABLE_ON)
                .addTag(BlockTags.DIRT).addTag(BlockTags.SAND)
                .add(Blocks.GRASS_BLOCK, Blocks.PODZOL,
                        Blocks.MYCELIUM, Blocks.MUD,
                        Blocks.MUDDY_MANGROVE_ROOTS, Blocks.MOSS_BLOCK,
                        Blocks.PALE_MOSS_BLOCK)
                .add(ModBlocks.CRYSTALLINE_SALT.get(),ModBlocks.SMOOTH_SALT.get(),ModBlocks.CUT_SALT.get(),ModBlocks.CHISELED_SALT.get())
                .add(net.minecraft.world.level.block.Blocks.CALCITE,
                        net.minecraft.world.level.block.Blocks.DIORITE);
        tag(BlockTags.FLOWER_POTS).add(ModBlocks.POTTED_GLASSWORT.get());
        tag(BlockTags.REPLACEABLE).add(ModBlocks.GLASSWORT.get());
        tag(BlockTags.REPLACEABLE_BY_TREES).add(ModBlocks.GLASSWORT.get());
        tag(BlockTags.SWORD_EFFICIENT).add(ModBlocks.GLASSWORT.get());
        tag(BlockTags.CLIMBABLE)
                .add(ModBlocks.TWIG_LADDER.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SALT_WATER_CAULDRON.get())
                .add(ModBlocks.CRYSTALLINE_SALT.get())
                .add(ModBlocks.SALT_SPIKE.get())
                .add(ModBlocks.SMOOTH_SALT.get())
                .add(ModBlocks.CUT_SALT.get())
                .add(ModBlocks.CHISELED_SALT.get())
                .add(ModBlocks.CRYSTALLINE_SALT_STAIRS.get(), ModBlocks.CRYSTALLINE_SALT_SLAB.get(), ModBlocks.CRYSTALLINE_SALT_WALL.get())
                .add(ModBlocks.HAND_MILL.get());

        tag(BlockTags.STAIRS).add(ModBlocks.CRYSTALLINE_SALT_STAIRS.get());

        tag(BlockTags.CAULDRONS).add(ModBlocks.SALT_WATER_CAULDRON.get());
        tag(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK, net.minecraft.resources.Identifier.withDefaultNamespace("dangerous_for_teleportation"))).add(ModBlocks.SALT_SPIKE.get());
        tag(BlockTags.SLABS).add(ModBlocks.CRYSTALLINE_SALT_SLAB.get());
        tag(BlockTags.WALLS).add(ModBlocks.CRYSTALLINE_SALT_WALL.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.SALTING_TUB.get())
                .add(ModBlocks.HAND_CRANK.get())
                .add(ModBlocks.CRATE.get())
                .add(ModBlocks.CACTUS_LOG.get())
                .add(ModBlocks.STRIPPED_CACTUS_LOG.get())
                .add(ModBlocks.CACTUS_WOOD.get())
                .add(ModBlocks.STRIPPED_CACTUS_WOOD.get())
                .add(ModBlocks.GIANT_CACTUS_STEM.get())
                .add(ModBlocks.TWIG_LADDER.get())
                .add(ModBlocks.CACTUS_MOSAIC.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.SALT_DEPOSIT.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.LINEN_BALE.get())
                .add(ModBlocks.FLAX_BALE.get())
                .add(ModBlocks.APPLE_LEAVES.get())
                .add(ModBlocks.LINEN_CARPET.get())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.get())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.get())
                .add(ModBlocks.BLUEBERRY_BUSH.get());

        tag(BlockTags.MAINTAINS_FARMLAND)
                .add(ModBlocks.FLAX.get())
                .add(ModBlocks.STRAWBERRY_PLANT.get());

        tag(BlockTags.CROPS)
                .add(ModBlocks.FLAX.get())
                .add(ModBlocks.STRAWBERRY_PLANT.get());

        var candleCakes = tag(BlockTags.CANDLE_CAKES);
        ModBlocks.STRAWBERRY_CANDLE_CAKES.forEach(cake -> candleCakes.add(cake.get()));

        tag(BlockTags.SWORD_EFFICIENT)
                .add(ModBlocks.FLAX.get())
                .add(ModBlocks.STRAWBERRY_PLANT.get())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.get())
                .add(ModBlocks.BLUEBERRY_BUSH.get());

        tag(BlockTags.BEE_GROWABLES)
                .add(ModBlocks.FLAX.get())
                .add(ModBlocks.STRAWBERRY_PLANT.get())
                .add(ModBlocks.APPLE_LEAVES.get())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.get())
                .add(ModBlocks.LARGE_BLUEBERRY_BUSH.get())
                .add(ModBlocks.BLUEBERRY_BUSH.get());

        tag(BlockTags.LEAVES)
                .add(ModBlocks.APPLE_LEAVES.get())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.get());

        tag(BlockTags.FLOWERS)
                .add(ModBlocks.FLOWERING_DESERT_MOSS.get())
                .add(ModBlocks.APPLE_LEAVES.get())
                .add(ModBlocks.GIANT_CACTUS_SHOOT.get())
                .add(ModBlocks.ORCHARD_CHERRY_LEAVES.get());

        tag(BlockTags.SMALL_FLOWERS)
                .add(ModBlocks.GIANT_CACTUS_SHOOT.get());

        tag(BlockTags.LOGS)
                .add(ModBlocks.STRIPPED_CACTUS_LOG.get())
                .add(ModBlocks.CACTUS_LOG.get())
                .add(ModBlocks.CACTUS_WOOD.get())
                .add(ModBlocks.STRIPPED_CACTUS_WOOD.get());

        tag(BlockTags.PLANKS)
                .add(ModBlocks.CACTUS_PLANKS.get());

        tag(BlockTags.WOODEN_STAIRS)
                .add(ModBlocks.CACTUS_STAIRS.get())
                .add(ModBlocks.CACTUS_MOSAIC_STAIRS.get());

        tag(BlockTags.WOODEN_SLABS)
                .add(ModBlocks.CACTUS_SLAB.get())
                .add(ModBlocks.CACTUS_MOSAIC_SLAB.get());

        tag(BlockTags.WOODEN_FENCES)
                .add(ModBlocks.CACTUS_FENCE.get());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.CACTUS_FENCE_GATE.get());

        tag(BlockTags.WOODEN_BUTTONS)
                .add(ModBlocks.CACTUS_BUTTON.get());

        tag(BlockTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.CACTUS_PRESSURE_PLATE.get());

        tag(BlockTags.WOODEN_DOORS)
                .add(ModBlocks.CACTUS_DOOR.get());

        tag(BlockTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.CACTUS_TRAPDOOR.get());

        tag(BlockTags.STANDING_SIGNS)
                .add(ModBlocks.CACTUS_SIGN.get());

        tag(BlockTags.WALL_SIGNS)
                .add(ModBlocks.CACTUS_WALL_SIGN.get());

        tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_HANGING_SIGN.get());

        tag(BlockTags.WALL_HANGING_SIGNS)
                .add(ModBlocks.CACTUS_WALL_HANGING_SIGN.get());
    }
}
