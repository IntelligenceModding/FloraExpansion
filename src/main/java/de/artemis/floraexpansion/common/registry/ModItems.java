package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModArmorMaterials;
import de.artemis.floraexpansion.common.registry.ModFoods;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.item.*;

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

    public static final DeferredItem<Item> SWEET_BERRY_MIX = ITEMS.register("sweet_berry_mix",
            () -> new Item(new Item.Properties().food(ModFoods.SWEET_BERRY_MIX)));

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

    public static final DeferredItem<Item> CHERRIES = ITEMS.register("cherries",
            () -> new CherriesItem(new Item.Properties().food(ModFoods.CHERRIES)));

    public static final DeferredItem<Item> CHERRY_JUICE = ITEMS.register("cherry_juice",
            () -> new BottledJuiceItem(new Item.Properties().food(ModFoods.CHERRY_JUICE).stacksTo(16)));

    public static final DeferredItem<Item> APPLE_JUICE = ITEMS.register("apple_juice",
            () -> new BottledJuiceItem(new Item.Properties().food(ModFoods.APPLE_JUICE).stacksTo(16)));

    public static final DeferredItem<Item> CACTUS_SIGN = ITEMS.register("cactus_sign",
            () -> new net.minecraft.world.item.SignItem(new Item.Properties().stacksTo(16),
                    ModBlocks.CACTUS_SIGN.get(),
                    ModBlocks.CACTUS_WALL_SIGN.get()));

    public static final DeferredItem<Item> CACTUS_HANGING_SIGN = ITEMS.register("cactus_hanging_sign",
            () -> new net.minecraft.world.item.HangingSignItem(
                    ModBlocks.CACTUS_HANGING_SIGN.get(),
                    ModBlocks.CACTUS_WALL_HANGING_SIGN.get(),
                    new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> CACTUS_BOAT = ITEMS.register("cactus_boat",
            () -> new CactusBoatItem(false, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> CACTUS_CHEST_BOAT = ITEMS.register("cactus_chest_boat",
            () -> new CactusBoatItem(true, new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> CACTUS_HELMET = ITEMS.register("cactus_helmet",
            () -> new CactusArmorItem(
                    ModArmorMaterials.CACTUS,
                    ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(7))
            ));

    public static final DeferredItem<Item> PRICKLY_PEAR = ITEMS.register("prickly_pear",
            () -> new ItemNameBlockItem(ModBlocks.OPUNTIA_CACTUS.get(), new Item.Properties().food(ModFoods.PRICKLY_PEAR)));

    public static final DeferredItem<Item> CACTUS_SLICE = ITEMS.register("cactus_slice",
            () -> new Item(new Item.Properties().food(ModFoods.CACTUS_SLICE)));

    public static final DeferredItem<Item> CACTUS_JUICE = ITEMS.register("cactus_juice",
            () -> new BottledJuiceItem(new Item.Properties().food(ModFoods.CACTUS_JUICE).stacksTo(16)));

    public static final DeferredItem<Item> CACTUS_CHESTPLATE = ITEMS.register("cactus_chestplate",
            () -> new CactusArmorItem(
                    ModArmorMaterials.CACTUS,
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(7))
            ));

    public static final DeferredItem<Item> CACTUS_LEGGINGS = ITEMS.register("cactus_leggings",
            () -> new CactusArmorItem(
                    ModArmorMaterials.CACTUS,
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(7))
            ));

    public static final DeferredItem<Item> CACTUS_BOOTS = ITEMS.register("cactus_boots",
            () -> new CactusArmorItem(
                    ModArmorMaterials.CACTUS,
                    ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(7))
            ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}



