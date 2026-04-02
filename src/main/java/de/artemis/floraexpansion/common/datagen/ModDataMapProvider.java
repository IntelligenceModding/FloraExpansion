package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.@NotNull Provider provider) {
        this.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(ModItems.PINE_CONE.getId(), new Compostable(0.3f), false)
                .add(ModBlocks.PINE_LITTER.getId(), new Compostable(0.3f), false)
                .add(ModItems.FLAX_SEED.getId(), new Compostable(0.3f), false)
                .add(ModItems.FLAX_FLOWER.getId(), new Compostable(0.3f), false)
                .add(ModItems.FLAX_FIBER.getId(), new Compostable(0.3f), false)
                .add(ModItems.CACTUS_SLICE.getId(), new Compostable(0.2f), false)
                .add(ModBlocks.CHERRY_PIT.getId(), new Compostable(0.3f), false)
                .add(ModItems.CHERRIES.getId(), new Compostable(0.3f), false)
                .add(ModBlocks.APPLE_CORE.getId(), new Compostable(0.3f), false)
                .add(ModBlocks.GIANT_CACTUS_BLOSSOM.getId(), new Compostable(0.3f), false)
                .add(ModBlocks.DESERT_MOSS.getId(), new Compostable(0.3f), false)
                .add(ModBlocks.CACTUS_CLUSTER.getId(), new Compostable(0.3f), false)
                .add(ModBlocks.FRUITING_OAK_LEAVES.getId(), new Compostable(0.5f), false)
                .add(ModBlocks.FRUITING_CHERRY_LEAVES.getId(), new Compostable(0.5f), false)
                .add(ModBlocks.OPUNTIA_CACTUS.getId(), new Compostable(0.3f), false);

        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(ModItems.TWIG.getId(), new FurnaceFuel(200), false)
                .add(ModItems.PINE_CONE.getId(), new FurnaceFuel(50), false)
                .add(ModItems.PINE_NUTS.getId(), new FurnaceFuel(50), false)
                .add(ModItems.TOASTED_PINE_NUTS.getId(), new FurnaceFuel(50), false)
                .add(ModBlocks.PINE_LITTER.getId(), new FurnaceFuel(100), false)
                .add(ModBlocks.TWIG_LADDER.getId(), new FurnaceFuel(300), false)
                .add(ModItems.FLAX_FLOWER.getId(), new FurnaceFuel(50), false)
                .add(ModBlocks.CHERRY_PIT.getId(), new FurnaceFuel(50), false)
                .add(ModBlocks.GIANT_CACTUS_BASE.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.GIANT_CACTUS_WOOD.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_BASE.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.GIANT_CACTUS_STEM.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.CACTUS_PLANKS.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.CACTUS_MOSAIC.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.LINEN_BLOCK.getId(), new FurnaceFuel(100), false)
                .add(ModBlocks.LINEN_CARPET.getId(), new FurnaceFuel(67), false)
                .add(ModBlocks.CACTUS_STAIRS.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.CACTUS_SLAB.getId(), new FurnaceFuel(150), false)
                .add(ModBlocks.CACTUS_FENCE.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.CACTUS_FENCE_GATE.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.CACTUS_BUTTON.getId(), new FurnaceFuel(100), false)
                .add(ModBlocks.CACTUS_PRESSURE_PLATE.getId(), new FurnaceFuel(300), false)
                .add(ModBlocks.CACTUS_DOOR.getId(), new FurnaceFuel(200), false)
                .add(ModBlocks.CACTUS_TRAPDOOR.getId(), new FurnaceFuel(300), false)
                .add(ModItems.CACTUS_SIGN.getId(), new FurnaceFuel(200), false)
                .add(ModItems.CACTUS_HANGING_SIGN.getId(), new FurnaceFuel(800), false)
                .add(ModItems.CACTUS_BOAT.getId(), new FurnaceFuel(1200), false)
                .add(ModItems.CACTUS_CHEST_BOAT.getId(), new FurnaceFuel(1200), false);
    }
}
