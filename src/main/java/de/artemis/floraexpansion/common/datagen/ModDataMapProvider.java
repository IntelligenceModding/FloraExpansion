package de.artemis.floraexpansion.common.datagen;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = "floraexpansion")
public final class ModDataMapProvider {
    public static void registerCompostables() {
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.APPLE_CORE.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUEBERRIES.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.STRAWBERRY.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.CHERRIES.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.CHERRY_PIT.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.DESERT_MOSS.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.FLOWERING_DESERT_MOSS.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.FLAX_FIBER.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.FLAX_SEEDS.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SPRUCE_CONE.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.NEEDLE_LITTER.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SPRUCE_NUTS.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.TOASTED_SPRUCE_NUTS.get().asItem(), .3f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.SMALL_CACTUS.get().asItem(), .5f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.GLASSWORT.get().asItem(), .5f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.CACTUS_SLICE.get().asItem(), .5f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.ORCHARD_CHERRY_LEAVES.get().asItem(), .5f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.APPLE_LEAVES.get().asItem(), .5f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.PRICKLY_PEAR.get().asItem(), .5f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.LARGE_BLUEBERRY_BUSH.get().asItem(), .65f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUEBERRY_JAM.get().asItem(), .65f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.STRAWBERRY.get().asItem(), .65f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.CHERRY_JAM.get().asItem(), .65f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.APPLE_JAM.get().asItem(), .65f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.FLAX_FLOWER.get().asItem(), .65f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.GIANT_CACTUS_SHOOT.get().asItem(), .65f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUEBERRY_COOKIE.get().asItem(), .85f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.SLICE_OF_BLUEBERRY_PIE.get().asItem(), .85f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModBlocks.FLAX_BALE.get().asItem(), .85f);
        net.minecraft.world.level.block.ComposterBlock.COMPOSTABLES.put(ModItems.BLUEBERRY_PIE.get().asItem(), 1f);
    }
    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void fuel(net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(ModItems.CACTUS_BOAT.get().asItem())) event.setBurnTime(1200);
        if (event.getItemStack().is(ModItems.CACTUS_CHEST_BOAT.get().asItem())) event.setBurnTime(1200);
        if (event.getItemStack().is(ModBlocks.CACTUS_BUTTON.get().asItem())) event.setBurnTime(100);
        if (event.getItemStack().is(ModBlocks.CACTUS_DOOR.get().asItem())) event.setBurnTime(200);
        if (event.getItemStack().is(ModItems.CACTUS_SIGN.get().asItem())) event.setBurnTime(200);
        if (event.getItemStack().is(ModItems.TWIG.get().asItem())) event.setBurnTime(200);
        if (event.getItemStack().is(ModItems.CACTUS_HANGING_SIGN.get().asItem())) event.setBurnTime(800);
        if (event.getItemStack().is(ModBlocks.CACTUS_SLAB.get().asItem())) event.setBurnTime(150);
        if (event.getItemStack().is(ModBlocks.CACTUS_MOSAIC.get().asItem())) event.setBurnTime(150);
        if (event.getItemStack().is(ModBlocks.LINEN_CARPET.get().asItem())) event.setBurnTime(67);
        if (event.getItemStack().is(ModBlocks.LARGE_BLUEBERRY_BUSH.get().asItem())) event.setBurnTime(100);
        if (event.getItemStack().is(ModBlocks.LINEN_BALE.get().asItem())) event.setBurnTime(100);
        if (event.getItemStack().is(ModItems.SPRUCE_CONE.get().asItem())) event.setBurnTime(100);
        if (event.getItemStack().is(ModBlocks.NEEDLE_LITTER.get().asItem())) event.setBurnTime(100);
        if (event.getItemStack().is(ModBlocks.CACTUS_FENCE.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_FENCE.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_MOSAIC.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_MOSAIC.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_PLANKS.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CRATE.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_PRESSURE_PLATE.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_STAIRS.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_TRAPDOOR.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_LOG.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.GIANT_CACTUS_STEM.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.CACTUS_WOOD.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.STRIPPED_CACTUS_LOG.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.STRIPPED_CACTUS_WOOD.get().asItem())) event.setBurnTime(300);
        if (event.getItemStack().is(ModBlocks.TWIG_LADDER.get().asItem())) event.setBurnTime(300);
    }
}
