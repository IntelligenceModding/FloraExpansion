package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.item.*;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FloraExpansion.MODID);

    public static final DeferredItem<PineConeItem> PINE_CONE = ITEMS.registerItem("pine_cone", PineConeItem::new);

    public static final DeferredItem<PebblesItem> PEBBLES = ITEMS.registerItem("pebbles", PebblesItem::new);

    public static final DeferredItem<Item> PINE_NUTS = ITEMS.registerItem("pine_nuts",
            properties -> new Item(properties.food(ModFoods.PINE_NUTS)));

    public static final DeferredItem<Item> TOASTED_PINE_NUTS = ITEMS.registerItem("toasted_pine_nuts",
            properties -> new Item(properties.food(ModFoods.TOASTED_PINE_NUTS)));

    public static final DeferredItem<Item> TWIG = ITEMS.registerSimpleItem("twig");

    public static final DeferredItem<Item> FOREST_SNACK = ITEMS.registerItem("forest_snack",
            properties -> new Item(properties.food(ModFoods.FOREST_SNACK)));

    public static final DeferredItem<Item> SWEET_BERRY_MIX = ITEMS.registerItem("sweet_berry_mix",
            properties -> new Item(properties.food(ModFoods.SWEET_BERRY_MIX)));

    public static final DeferredItem<Item> BLUEBERRIES = ITEMS.registerItem("blueberries",
            properties -> new BlockItem(ModBlocks.BLUEBERRY_BUSH.get(), properties.food(ModFoods.BLUEBERRIES)));

    public static final DeferredItem<Item> BLUEBERRY_COOKIE = ITEMS.registerItem("blueberry_cookie",
            properties -> new Item(properties.food(ModFoods.BLUEBERRY_COOKIE)));

    public static final DeferredItem<Item> BLUEBERRY_PIE = ITEMS.registerItem("blueberry_pie",
            properties -> new BlueberryPieItem(properties.food(ModFoods.BLUEBERRY_PIE).stacksTo(1)));

    public static final DeferredItem<Item> BLUEBERRY_PIE_SLICE = ITEMS.registerItem("blueberry_pie_slice",
            properties -> new Item(properties.food(ModFoods.BLUEBERRY_PIE_SLICE)));

    public static final DeferredItem<Item> BLUEBERRY_JUICE = ITEMS.registerItem("blueberry_juice",
            properties -> new BottledJuiceItem(properties.food(ModFoods.BLUEBERRY_JUICE).stacksTo(16)));

    public static final DeferredItem<Item> BASKET = ITEMS.registerItem("basket",
            properties -> new BasketItem(properties.stacksTo(1)));

    public static final DeferredItem<Item> EMPTY_JAR = ITEMS.registerSimpleItem("empty_jar",
            properties -> properties.stacksTo(16));

    public static final DeferredItem<Item> BLUEBERRY_JAM = ITEMS.registerItem("blueberry_jam",
            properties -> new JarFoodItem(properties.food(ModFoods.BLUEBERRY_JAM).stacksTo(16)));

    public static final DeferredItem<Item> STRAWBERRY = ITEMS.registerItem("strawberry",
            properties -> new BlockItem(ModBlocks.STRAWBERRY_PLANT.get(), properties.food(ModFoods.STRAWBERRY)));

    public static final DeferredItem<Item> STRAWBERRY_JAM = ITEMS.registerItem("strawberry_jam",
            properties -> new JarFoodItem(properties.food(ModFoods.STRAWBERRY_JAM).stacksTo(16)));

    public static final DeferredItem<Item> WOODEN_BUCKET = ITEMS.registerItem("wooden_bucket",
            properties -> new WoodenBucketItem(Fluids.EMPTY, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> WOODEN_WATER_BUCKET = ITEMS.registerItem("wooden_water_bucket",
            properties -> new WoodenBucketItem(Fluids.WATER, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> WOODEN_LAVA_BUCKET = ITEMS.registerItem("wooden_lava_bucket",
            properties -> new WoodenLavaBucketItem(properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> WOODEN_POWDER_SNOW_BUCKET = ITEMS.registerItem("wooden_powder_snow_bucket",
            properties -> new WoodenPowderSnowBucketItem(properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> WOODEN_MILK_BUCKET = ITEMS.registerItem("wooden_milk_bucket",
            properties -> new WoodenMilkBucketItem(properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> COD_WOODEN_BUCKET = ITEMS.registerItem("cod_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.COD, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> SALMON_WOODEN_BUCKET = ITEMS.registerItem("salmon_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.SALMON, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> PUFFERFISH_WOODEN_BUCKET = ITEMS.registerItem("pufferfish_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.PUFFERFISH, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> TROPICAL_FISH_WOODEN_BUCKET = ITEMS.registerItem("tropical_fish_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.TROPICAL_FISH, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> AXOLOTL_WOODEN_BUCKET = ITEMS.registerItem("axolotl_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.AXOLOTL, SoundEvents.BUCKET_EMPTY_AXOLOTL, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> TADPOLE_WOODEN_BUCKET = ITEMS.registerItem("tadpole_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.TADPOLE, SoundEvents.BUCKET_EMPTY_TADPOLE, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> FLAX_SEED = ITEMS.registerItem("flax_seed",
            properties -> new BlockItem(ModBlocks.FLAX_CROP.get(), properties));

    public static final DeferredItem<Item> FLAX_FIBER = ITEMS.registerSimpleItem("flax_fiber");

    public static final DeferredItem<Item> FLAX_FLOWER = ITEMS.registerSimpleItem("flax_flower");

    public static final DeferredItem<Item> LINEN_THREAD = ITEMS.registerSimpleItem("linen_thread");

    public static final DeferredItem<Item> LINEN_CLOTH = ITEMS.registerSimpleItem("linen_cloth");

    public static final DeferredItem<Item> CHERRIES = ITEMS.registerItem("cherries",
            properties -> new CherriesItem(properties.food(ModFoods.CHERRIES)));

    public static final DeferredItem<Item> CHERRY_JUICE = ITEMS.registerItem("cherry_juice",
            properties -> new BottledJuiceItem(properties.food(ModFoods.CHERRY_JUICE).stacksTo(16)));

    public static final DeferredItem<Item> APPLE_JUICE = ITEMS.registerItem("apple_juice",
            properties -> new BottledJuiceItem(properties.food(ModFoods.APPLE_JUICE).stacksTo(16)));

    public static final DeferredItem<Item> CACTUS_SIGN = ITEMS.registerItem("cactus_sign",
            properties -> new net.minecraft.world.item.SignItem(
                    ModBlocks.CACTUS_SIGN.get(),
                    ModBlocks.CACTUS_WALL_SIGN.get(),
                    properties.stacksTo(16)));

    public static final DeferredItem<Item> CACTUS_HANGING_SIGN = ITEMS.registerItem("cactus_hanging_sign",
            properties -> new net.minecraft.world.item.HangingSignItem(
                    ModBlocks.CACTUS_HANGING_SIGN.get(),
                    ModBlocks.CACTUS_WALL_HANGING_SIGN.get(),
                    properties.stacksTo(16)));

    public static final DeferredItem<Item> CACTUS_BOAT = ITEMS.registerItem("cactus_boat",
            properties -> new CactusBoatItem(false, properties.stacksTo(1)));

    public static final DeferredItem<Item> CACTUS_CHEST_BOAT = ITEMS.registerItem("cactus_chest_boat",
            properties -> new CactusBoatItem(true, properties.stacksTo(1)));

    public static final DeferredItem<CactusArmorItem> CACTUS_HELMET =
            ITEMS.registerItem("cactus_helmet",
                    properties -> new CactusArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.CACTUS_ARMOR_MATERIAL, ArmorType.HELMET)
                    ));

    public static final DeferredItem<Item> PRICKLY_PEAR = ITEMS.registerItem("prickly_pear",
            properties -> new BlockItem(ModBlocks.OPUNTIA_CACTUS.get(), properties.food(ModFoods.PRICKLY_PEAR)));

    public static final DeferredItem<Item> CACTUS_SLICE = ITEMS.registerItem("cactus_slice",
            properties -> new Item(properties.food(ModFoods.CACTUS_SLICE)));

    public static final DeferredItem<Item> CACTUS_JUICE = ITEMS.registerItem("cactus_juice",
            properties -> new BottledJuiceItem(properties.food(ModFoods.CACTUS_JUICE).stacksTo(16)));

    public static final DeferredItem<CactusArmorItem> CACTUS_CHESTPLATE =
            ITEMS.registerItem("cactus_chestplate",
                    properties -> new CactusArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.CACTUS_ARMOR_MATERIAL, ArmorType.CHESTPLATE)
                    ));

    public static final DeferredItem<CactusArmorItem> CACTUS_LEGGINGS =
            ITEMS.registerItem("cactus_leggings",
                    properties -> new CactusArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.CACTUS_ARMOR_MATERIAL, ArmorType.LEGGINGS)
                    ));

    public static final DeferredItem<CactusArmorItem> CACTUS_BOOTS =
            ITEMS.registerItem("cactus_boots",
                    properties -> new CactusArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.CACTUS_ARMOR_MATERIAL, ArmorType.BOOTS)
                    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}



