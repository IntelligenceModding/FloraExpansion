package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.util.ModFoods;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FloraExpansion.MODID);

    public static final DeferredItem<PineConeItem> PINE_CONE = ITEMS.register("pine_cone",
            () -> new PineConeItem(new Item.Properties()));

    public static final DeferredItem<Item> PINE_NUTS = ITEMS.register("pine_nuts",
            () -> new Item(new Item.Properties().food(ModFoods.PINE_NUTS)));

    public static final DeferredItem<Item> TOASTED_PINE_NUTS = ITEMS.register("toasted_pine_nuts",
            () -> new Item(new Item.Properties().food(ModFoods.TOASTED_PINE_NUTS)));

    public static final DeferredItem<Item> TWIG = ITEMS.register("twig",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
