package de.artemis.floraexpansion.common.util;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public final class ModArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, FloraExpansion.MODID);

    public static final Holder<ArmorMaterial> CACTUS = ARMOR_MATERIALS.register("cactus",
            () -> new ArmorMaterial(
                    Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                        map.put(ArmorItem.Type.BOOTS, 1);
                        map.put(ArmorItem.Type.LEGGINGS, 3);
                        map.put(ArmorItem.Type.CHESTPLATE, 4);
                        map.put(ArmorItem.Type.HELMET, 1);
                        map.put(ArmorItem.Type.BODY, 4);
                    }),
                    18,
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.of(ModBlocks.CACTUS_THORN.get()),
                    List.of(
                            new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "cactus")
                            ),
                            new ArmorMaterial.Layer(
                                    ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "cactus"),
                                    "_overlay",
                                    false
                            )
                    ),
                    0.0F,
                    0.0F
            )
    );

    private ModArmorMaterials() {
    }

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }
}