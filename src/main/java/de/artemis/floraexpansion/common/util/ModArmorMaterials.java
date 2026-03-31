package de.artemis.floraexpansion.common.util;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.EnumMap;

public final class ModArmorMaterials {
    static ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID =
            ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));

    public static final ResourceKey<EquipmentAsset> CACTUS =
            ResourceKey.create(ROOT_ID, Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "cactus"));

    public static final ArmorMaterial CACTUS_ARMOR_MATERIAL = new ArmorMaterial(
            7,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 1);
                map.put(ArmorType.LEGGINGS, 3);
                map.put(ArmorType.CHESTPLATE, 4);
                map.put(ArmorType.HELMET, 1);
                map.put(ArmorType.BODY, 4);
            }),
            18,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            ModTags.Items.CACTUS_ARMOR_REPAIRABLE,
            CACTUS
    );

    private ModArmorMaterials() {
    }
}