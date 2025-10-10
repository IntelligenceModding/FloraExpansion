package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FloraExpansion.MODID);

    public static final DeferredItem<PineConeItem> PINE_CONE = ITEMS.register("pine_cone",
            () -> new PineConeItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
