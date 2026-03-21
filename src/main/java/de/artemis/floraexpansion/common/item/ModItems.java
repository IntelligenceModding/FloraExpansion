package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.util.ModFoods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FloraExpansion.MODID);

    public static final DeferredItem<PineConeItem> PINE_CONE = ITEMS.register("pine_cone",
            () -> new PineConeItem(new Item.Properties()));

    public static final DeferredItem<PebblesItem> PEBBLES = ITEMS.register("pebbles",
            () -> new PebblesItem(new Item.Properties()));

    public static final DeferredItem<Item> PINE_NUTS = ITEMS.register("pine_nuts",
            () -> new Item(new Item.Properties().food(ModFoods.PINE_NUTS)));

    public static final DeferredItem<Item> TOASTED_PINE_NUTS = ITEMS.register("toasted_pine_nuts",
            () -> new Item(new Item.Properties().food(ModFoods.TOASTED_PINE_NUTS)));

    public static final DeferredItem<Item> TWIG = ITEMS.register("twig",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FOREST_SNACK = ITEMS.register("forest_snack",
            () -> new Item(new Item.Properties().food(ModFoods.FOREST_SNACK)));

    public static final DeferredItem<Item> FLAX_SEED = ITEMS.register("flax_seed",
            () -> new ItemNameBlockItem(ModBlocks.FLAX_CROP.get(), new Item.Properties()));

    public static final DeferredItem<Item> FLAX_FIBER = ITEMS.register("flax_fiber",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FLAX_FLOWER = ITEMS.register("flax_flower",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> LINEN_THREAD = ITEMS.register("linen_thread",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> LINEN_CLOTH = ITEMS.register("linen_cloth",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
