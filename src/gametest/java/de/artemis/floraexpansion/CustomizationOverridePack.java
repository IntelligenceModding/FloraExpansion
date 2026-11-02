package de.artemis.floraexpansion;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class CustomizationOverridePack {
    @SubscribeEvent
    public static void packs(AddPackFindersEvent event) {
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) {
            event.addPackFinders(Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "customization_override_pack"),
                    PackType.SERVER_DATA, Component.literal("Customization regression overrides"), PackSource.BUILT_IN, true, Pack.Position.TOP);
        }
    }
}
