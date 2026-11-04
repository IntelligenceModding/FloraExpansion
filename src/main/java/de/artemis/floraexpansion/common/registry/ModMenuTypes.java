package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.inventory.BasketMenu;
import de.artemis.floraexpansion.common.inventory.CrateMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(net.minecraft.core.registries.Registries.MENU, FloraExpansion.MODID);

    public static final RegistryObject<MenuType<CrateMenu>> CRATE =
            MENU_TYPES.register("crate", () -> IForgeMenuType.create((windowId, inventory, data) -> new CrateMenu(windowId, inventory)));

    public static final RegistryObject<MenuType<BasketMenu>> BASKET =
            MENU_TYPES.register("basket", () -> IForgeMenuType.create(BasketMenu::new));

    public static final RegistryObject<MenuType<de.artemis.floraexpansion.common.inventory.HandMillMenu>> HAND_MILL =
            MENU_TYPES.register("hand_mill", () -> IForgeMenuType.create((id, inventory, data) ->
                    new de.artemis.floraexpansion.common.inventory.HandMillMenu(id, inventory)));

    private ModMenuTypes() {
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
