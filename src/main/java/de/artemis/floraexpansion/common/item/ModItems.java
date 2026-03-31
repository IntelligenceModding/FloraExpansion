package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.util.ModArmorMaterials;
import de.artemis.floraexpansion.common.util.ModFoods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("all")
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FloraExpansion.MODID);

    public static final DeferredItem<PineConeItem> PINE_CONE =
            ITEMS.registerItem("pine_cone", PineConeItem::new, new Item.Properties());

    public static final DeferredItem<PebblesItem> PEBBLES =
            ITEMS.registerItem("pebbles", PebblesItem::new, new Item.Properties());

    public static final DeferredItem<Item> PINE_NUTS =
            ITEMS.registerSimpleItem("pine_nuts", new Item.Properties().food(ModFoods.PINE_NUTS));

    public static final DeferredItem<Item> TOASTED_PINE_NUTS =
            ITEMS.registerSimpleItem("toasted_pine_nuts", new Item.Properties().food(ModFoods.TOASTED_PINE_NUTS));

    public static final DeferredItem<Item> TWIG =
            ITEMS.registerSimpleItem("twig", new Item.Properties());

    public static final DeferredItem<Item> FOREST_SNACK =
            ITEMS.registerSimpleItem("forest_snack", new Item.Properties().food(ModFoods.FOREST_SNACK));

    public static final DeferredItem<Item> SWEET_BERRY_MIX =
            ITEMS.registerSimpleItem("sweet_berry_mix", new Item.Properties().food(ModFoods.SWEET_BERRY_MIX));

    public static final DeferredItem<BlockItem> FLAX_SEED =
            ITEMS.registerItem("flax_seed",
                    properties -> new BlockItem(ModBlocks.FLAX_CROP.get(), properties),
                    new Item.Properties());

    public static final DeferredItem<Item> FLAX_FIBER =
            ITEMS.registerSimpleItem("flax_fiber", new Item.Properties());

    public static final DeferredItem<Item> FLAX_FLOWER =
            ITEMS.registerSimpleItem("flax_flower", new Item.Properties());

    public static final DeferredItem<Item> LINEN_THREAD =
            ITEMS.registerSimpleItem("linen_thread", new Item.Properties());

    public static final DeferredItem<Item> LINEN_CLOTH =
            ITEMS.registerSimpleItem("linen_cloth", new Item.Properties());

    public static final DeferredItem<CherriesItem> CHERRIES =
            ITEMS.registerItem("cherries", CherriesItem::new, new Item.Properties().food(ModFoods.CHERRIES));

    public static final DeferredItem<BottledJuiceItem> CHERRY_JUICE =
            ITEMS.registerItem("cherry_juice", BottledJuiceItem::new,
                    new Item.Properties().food(ModFoods.CHERRY_JUICE).stacksTo(16));

    public static final DeferredItem<BottledJuiceItem> APPLE_JUICE =
            ITEMS.registerItem("apple_juice", BottledJuiceItem::new,
                    new Item.Properties().food(ModFoods.APPLE_JUICE).stacksTo(16));

    public static final DeferredItem<SignItem> CACTUS_SIGN =
            ITEMS.registerItem("cactus_sign",
                    properties -> new SignItem(ModBlocks.CACTUS_SIGN.get(), ModBlocks.CACTUS_WALL_SIGN.get(), properties),
                    new Item.Properties().stacksTo(16));

    public static final DeferredItem<HangingSignItem> CACTUS_HANGING_SIGN =
            ITEMS.registerItem("cactus_hanging_sign",
                    properties -> new HangingSignItem(ModBlocks.CACTUS_HANGING_SIGN.get(), ModBlocks.CACTUS_WALL_HANGING_SIGN.get(), properties),
                    new Item.Properties().stacksTo(16));

    public static final DeferredItem<CactusBoatItem> CACTUS_BOAT =
            ITEMS.registerItem("cactus_boat",
                    properties -> new CactusBoatItem(false, properties),
                    new Item.Properties().stacksTo(1));

    public static final DeferredItem<CactusBoatItem> CACTUS_CHEST_BOAT =
            ITEMS.registerItem("cactus_chest_boat",
                    properties -> new CactusBoatItem(true, properties),
                    new Item.Properties().stacksTo(1));

    public static final DeferredItem<CactusArmorItem> CACTUS_HELMET =
            ITEMS.registerItem("cactus_helmet",
                    properties -> new CactusArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.CACTUS_ARMOR_MATERIAL, ArmorType.HELMET)
                    ),
                    new Item.Properties());

    public static final DeferredItem<BlockItem> PRICKLY_PEAR =
            ITEMS.registerItem("prickly_pear",
                    properties -> new BlockItem(ModBlocks.OPUNTIA_CACTUS.get(), properties.food(ModFoods.PRICKLY_PEAR)),
                    new Item.Properties());

    public static final DeferredItem<Item> CACTUS_SLICE =
            ITEMS.registerSimpleItem("cactus_slice", new Item.Properties().food(ModFoods.CACTUS_SLICE));

    public static final DeferredItem<BottledJuiceItem> CACTUS_JUICE =
            ITEMS.registerItem("cactus_juice", BottledJuiceItem::new,
                    new Item.Properties().food(ModFoods.CACTUS_JUICE).stacksTo(16));

    public static final DeferredItem<CactusArmorItem> CACTUS_CHESTPLATE =
            ITEMS.registerItem("cactus_chestplate",
                    properties -> new CactusArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.CACTUS_ARMOR_MATERIAL, ArmorType.CHESTPLATE)
                    ),
                    new Item.Properties());

    public static final DeferredItem<CactusArmorItem> CACTUS_LEGGINGS =
            ITEMS.registerItem("cactus_leggings",
                    properties -> new CactusArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.CACTUS_ARMOR_MATERIAL, ArmorType.LEGGINGS)
                    ),
                    new Item.Properties());

    public static final DeferredItem<CactusArmorItem> CACTUS_BOOTS =
            ITEMS.registerItem("cactus_boots",
                    properties -> new CactusArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.CACTUS_ARMOR_MATERIAL, ArmorType.BOOTS)
                    ),
                    new Item.Properties());

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}