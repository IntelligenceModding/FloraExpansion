package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModArmorMaterials;
import de.artemis.floraexpansion.common.registry.ModFoods;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluids;
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

    public static final DeferredItem<Item> BLUEBERRIES = ITEMS.register("blueberries",
            () -> new ItemNameBlockItem(ModBlocks.BLUEBERRY_BUSH.get(), new Item.Properties().food(ModFoods.BLUEBERRIES)));

    public static final DeferredItem<Item> BLUEBERRY_COOKIE = ITEMS.register("blueberry_cookie",
            () -> new Item(new Item.Properties().food(ModFoods.BLUEBERRY_COOKIE)));

    public static final DeferredItem<Item> BLUEBERRY_PIE = ITEMS.register("blueberry_pie",
            () -> new BlueberryPieItem(new Item.Properties().food(ModFoods.BLUEBERRY_PIE).stacksTo(1)));

    public static final DeferredItem<Item> BLUEBERRY_PIE_SLICE = ITEMS.register("blueberry_pie_slice",
            () -> new Item(new Item.Properties().food(ModFoods.BLUEBERRY_PIE_SLICE)));

    public static final DeferredItem<Item> BLUEBERRY_JUICE = ITEMS.register("blueberry_juice",
            () -> new BottledJuiceItem(new Item.Properties().food(ModFoods.BLUEBERRY_JUICE).stacksTo(16)));

    public static final DeferredItem<Item> BASKET = ITEMS.register("basket",
            () -> new BasketItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> EMPTY_JAR = ITEMS.register("empty_jar",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> BLUEBERRY_JAM = ITEMS.register("blueberry_jam",
            () -> new JarFoodItem(new Item.Properties().food(ModFoods.BLUEBERRY_JAM).stacksTo(16)));

    public static final DeferredItem<Item> STRAWBERRY = ITEMS.register("strawberry",
            () -> new ItemNameBlockItem(ModBlocks.STRAWBERRY_PLANT.get(), new Item.Properties().food(ModFoods.STRAWBERRY)));

    public static final DeferredItem<Item> STRAWBERRY_JAM = ITEMS.register("strawberry_jam",
            () -> new JarFoodItem(new Item.Properties().food(ModFoods.STRAWBERRY_JAM).stacksTo(16)));

    public static final DeferredItem<Item> WOODEN_BUCKET = ITEMS.register("wooden_bucket",
            () -> new WoodenBucketItem(Fluids.EMPTY, new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> WOODEN_WATER_BUCKET = ITEMS.register("wooden_water_bucket",
            () -> new WoodenBucketItem(Fluids.WATER, new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> WOODEN_LAVA_BUCKET = ITEMS.register("wooden_lava_bucket",
            () -> new WoodenLavaBucketItem(new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> WOODEN_POWDER_SNOW_BUCKET = ITEMS.register("wooden_powder_snow_bucket",
            () -> new WoodenPowderSnowBucketItem(new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> WOODEN_MILK_BUCKET = ITEMS.register("wooden_milk_bucket",
            () -> new WoodenMilkBucketItem(new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> COD_WOODEN_BUCKET = ITEMS.register("cod_wooden_bucket",
            () -> new WoodenMobBucketItem(EntityType.COD, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> SALMON_WOODEN_BUCKET = ITEMS.register("salmon_wooden_bucket",
            () -> new WoodenMobBucketItem(EntityType.SALMON, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> PUFFERFISH_WOODEN_BUCKET = ITEMS.register("pufferfish_wooden_bucket",
            () -> new WoodenMobBucketItem(EntityType.PUFFERFISH, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> TROPICAL_FISH_WOODEN_BUCKET = ITEMS.register("tropical_fish_wooden_bucket",
            () -> new WoodenMobBucketItem(EntityType.TROPICAL_FISH, SoundEvents.BUCKET_EMPTY_FISH, new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> AXOLOTL_WOODEN_BUCKET = ITEMS.register("axolotl_wooden_bucket",
            () -> new WoodenMobBucketItem(EntityType.AXOLOTL, SoundEvents.BUCKET_EMPTY_AXOLOTL, new Item.Properties().stacksTo(1).durability(10)));

    public static final DeferredItem<Item> TADPOLE_WOODEN_BUCKET = ITEMS.register("tadpole_wooden_bucket",
            () -> new WoodenMobBucketItem(EntityType.TADPOLE, SoundEvents.BUCKET_EMPTY_TADPOLE, new Item.Properties().stacksTo(1).durability(10)));

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



