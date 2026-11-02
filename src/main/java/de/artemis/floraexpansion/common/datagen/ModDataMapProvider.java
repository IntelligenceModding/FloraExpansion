package de.artemis.floraexpansion.common.datagen;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.*;
public class ModDataMapProvider extends DataMapProvider {
    public ModDataMapProvider(PackOutput output, java.util.concurrent.CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void gather(HolderLookup.Provider provider) {
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.APPLE_CORE.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.BLUEBERRIES.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.STRAWBERRY.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.CHERRIES.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.CHERRY_PIT.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.DESERT_MOSS.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.FLOWERING_DESERT_MOSS.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.FLAX_FIBER.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.FLAX_SEEDS.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.SPRUCE_CONE.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.NEEDLE_LITTER.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.SPRUCE_NUTS.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.TOASTED_SPRUCE_NUTS.getId(), new Compostable(.3f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.SMALL_CACTUS.getId(), new Compostable(.5f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.GLASSWORT.getId(), new Compostable(.5f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.CACTUS_SLICE.getId(), new Compostable(.5f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.ORCHARD_CHERRY_LEAVES.getId(), new Compostable(.5f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.APPLE_LEAVES.getId(), new Compostable(.5f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.PRICKLY_PEAR.getId(), new Compostable(.5f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.LARGE_BLUEBERRY_BUSH.getId(), new Compostable(.65f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.BLUEBERRY_JAM.getId(), new Compostable(.65f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.STRAWBERRY.getId(), new Compostable(.65f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.CHERRY_JAM.getId(), new Compostable(.65f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.APPLE_JAM.getId(), new Compostable(.65f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.FLAX_FLOWER.getId(), new Compostable(.65f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.GIANT_CACTUS_SHOOT.getId(), new Compostable(.65f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.BLUEBERRY_COOKIE.getId(), new Compostable(.85f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.SLICE_OF_BLUEBERRY_PIE.getId(), new Compostable(.85f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModBlocks.FLAX_BALE.getId(), new Compostable(.85f), false);
        builder(NeoForgeDataMaps.COMPOSTABLES).add(ModItems.BLUEBERRY_PIE.getId(), new Compostable(1f), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModItems.CACTUS_BOAT.getId(), new FurnaceFuel(1200), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModItems.CACTUS_CHEST_BOAT.getId(), new FurnaceFuel(1200), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_BUTTON.getId(), new FurnaceFuel(100), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_DOOR.getId(), new FurnaceFuel(200), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModItems.CACTUS_SIGN.getId(), new FurnaceFuel(200), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModItems.TWIG.getId(), new FurnaceFuel(200), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModItems.CACTUS_HANGING_SIGN.getId(), new FurnaceFuel(800), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_SLAB.getId(), new FurnaceFuel(150), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_MOSAIC.getId(), new FurnaceFuel(150), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.LINEN_CARPET.getId(), new FurnaceFuel(67), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.LARGE_BLUEBERRY_BUSH.getId(), new FurnaceFuel(100), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.LINEN_BALE.getId(), new FurnaceFuel(100), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModItems.SPRUCE_CONE.getId(), new FurnaceFuel(100), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.NEEDLE_LITTER.getId(), new FurnaceFuel(100), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_FENCE.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_FENCE.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_MOSAIC.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_MOSAIC.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_PLANKS.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CRATE.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_PRESSURE_PLATE.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_STAIRS.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_TRAPDOOR.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_LOG.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.GIANT_CACTUS_STEM.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.CACTUS_WOOD.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.STRIPPED_CACTUS_LOG.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.STRIPPED_CACTUS_WOOD.getId(), new FurnaceFuel(300), false);
        builder(NeoForgeDataMaps.FURNACE_FUELS).add(ModBlocks.TWIG_LADDER.getId(), new FurnaceFuel(300), false);
    }
}
