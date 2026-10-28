package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.component.Compostable;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class ItemComponentEvents {
    @SubscribeEvent
    public static void modifyComponents(ModifyDefaultComponentsEvent event) {
        compost(event, ContextIntProviders.COMPOSTABLE_LOW,
                ModBlocks.APPLE_CORE.get(), ModItems.BLUEBERRIES.get(), ModItems.STRAWBERRY.get(),
                ModItems.CHERRIES.get(), ModBlocks.CHERRY_PIT.get(), ModBlocks.DESERT_MOSS.get(),
                ModItems.FLAX_FIBER.get(), ModItems.FLAX_SEEDS.get(), ModItems.SPRUCE_CONE.get(),
                ModBlocks.NEEDLE_LITTER.get(), ModItems.SPRUCE_NUTS.get(), ModItems.TOASTED_SPRUCE_NUTS.get());
        compost(event, ContextIntProviders.COMPOSTABLE_LOW_MEDIUM,
                ModBlocks.SMALL_CACTUS.get(), ModItems.CACTUS_SLICE.get(),
                ModBlocks.ORCHARD_CHERRY_LEAVES.get(), ModBlocks.APPLE_LEAVES.get(),
                ModItems.PRICKLY_PEAR.get());
        compost(event, ContextIntProviders.COMPOSTABLE_MEDIUM,
                ModBlocks.LARGE_BLUEBERRY_BUSH.get(), ModItems.BLUEBERRY_JAM.get(),
                ModItems.STRAWBERRY_JAM.get(), ModItems.FLAX_FLOWER.get(), ModBlocks.GIANT_CACTUS_SHOOT.get());
        compost(event, ContextIntProviders.COMPOSTABLE_MEDIUM_HIGH,
                ModItems.BLUEBERRY_COOKIE.get(), ModItems.SLICE_OF_BLUEBERRY_PIE.get(), ModBlocks.FLAX_BALE.get());
        compost(event, ContextIntProviders.COMPOSTABLE_ALWAYS_ADD_ONE, ModItems.BLUEBERRY_PIE.get());

        fuel(event, ContextIntProviders.COOKING_TIME_BOATS, ModItems.CACTUS_BOAT.get(), ModItems.CACTUS_CHEST_BOAT.get());
        fuel(event, ContextIntProviders.COOKING_TIME_WOOD_ITEMS_EXTRA_SMALL, ModBlocks.CACTUS_BUTTON.get());
        fuel(event, ContextIntProviders.COOKING_TIME_WOOD_ITEMS_LARGE,
                ModBlocks.CACTUS_DOOR.get(), ModItems.CACTUS_SIGN.get(), ModItems.TWIG.get());
        fuel(event, ContextIntProviders.COOKING_TIME_HANGING_SIGNS, ModItems.CACTUS_HANGING_SIGN.get());
        fuel(event, ContextIntProviders.COOKING_TIME_WOOD_SLABS, ModBlocks.CACTUS_SLAB.get());
        fuel(event, ContextIntProviders.COOKING_TIME_WOOL_CARPETS, ModBlocks.LINEN_CARPET.get());
        fuel(event, ContextIntProviders.COOKING_TIME_DRY_PLANTS, ModBlocks.LARGE_BLUEBERRY_BUSH.get(),
                ModBlocks.LINEN_BALE.get(), ModItems.SPRUCE_CONE.get(), ModBlocks.NEEDLE_LITTER.get());
        fuel(event, ContextIntProviders.COOKING_TIME_WOOD_BLOCKS,
                ModBlocks.CACTUS_FENCE.get(), ModBlocks.CACTUS_FENCE_GATE.get(), ModBlocks.CACTUS_MOSAIC.get(),
                ModBlocks.CACTUS_PLANKS.get(), ModBlocks.CRATE.get(), ModBlocks.CACTUS_PRESSURE_PLATE.get(),
                ModBlocks.CACTUS_STAIRS.get(), ModBlocks.CACTUS_TRAPDOOR.get(), ModBlocks.CACTUS_LOG.get(),
                ModBlocks.GIANT_CACTUS_STEM.get(), ModBlocks.CACTUS_WOOD.get(),
                ModBlocks.STRIPPED_CACTUS_LOG.get(), ModBlocks.STRIPPED_CACTUS_WOOD.get(),
                ModBlocks.TWIG_LADDER.get());
    }

    private static void compost(ModifyDefaultComponentsEvent event, ResourceKey<ContextIntProvider> layers, ItemLike... items) {
        for (ItemLike item : items) {
            event.modify(item, (components, context, key) -> components.set(DataComponents.COMPOSTABLE, new Compostable(layers)));
        }
    }

    private static void fuel(ModifyDefaultComponentsEvent event, ResourceKey<ContextIntProvider> burnTime, ItemLike... items) {
        for (ItemLike item : items) {
            event.modify(item, (components, context, key) -> components.set(DataComponents.COOKING_FUEL,
                    new CookingFuel(burnTime, ContextFloatProviders.COOKING_DEFAULT_SPEED_MULTIPLIER)));
        }
    }
}
