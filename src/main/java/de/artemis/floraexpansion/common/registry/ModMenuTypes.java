package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.inventory.BasketMenu;
import de.artemis.floraexpansion.common.inventory.CrateMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, FloraExpansion.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<CrateMenu>> CRATE =
            MENU_TYPES.register("crate", () -> IMenuTypeExtension.create((windowId, inventory, data) -> new CrateMenu(windowId, inventory)));

    public static final DeferredHolder<MenuType<?>, MenuType<BasketMenu>> BASKET =
            MENU_TYPES.register("basket", () -> IMenuTypeExtension.create(BasketMenu::new));

    private ModMenuTypes() {
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
