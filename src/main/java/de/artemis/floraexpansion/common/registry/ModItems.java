package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.food.FoodProperties;

import de.artemis.floraexpansion.common.projectile.SpruceConeProjectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.item.*;
import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(net.minecraft.core.registries.Registries.ITEM, FloraExpansion.MODID);
    public static final RegistryObject<Item> MUSIC_DISC_INTO_THE_CAVES = registerItem("music_disc_into_the_caves",
            properties -> new Item(properties.stacksTo(1).rarity(net.minecraft.world.item.Rarity.UNCOMMON)
                    .jukeboxPlayable(ModJukeboxSongs.INTO_THE_CAVES)));
    public static final RegistryObject<BlockItem> RAW_SALT = registerItem("raw_salt",
            properties -> new net.minecraft.world.item.ItemNameBlockItem(ModBlocks.SALT_DEPOSIT.get(), properties));
    public static final RegistryObject<Item> SALT = ITEMS.register("salt", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SALTED_GLASSWORT = registerItem("salted_glasswort",
            properties -> new Item(properties.food(ModFoods.SALTED_GLASSWORT)));
    public static final RegistryObject<net.minecraft.world.item.BucketItem> SALT_WATER_BUCKET = registerItem("salt_water_bucket",
            properties -> new net.minecraft.world.item.BucketItem(ModFluids.SALT_WATER.get(), properties.stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final RegistryObject<Item> WOODEN_SALT_WATER_BUCKET = registerItem("wooden_salt_water_bucket",
            properties -> new WoodenBucketItem(ModFluids.SALT_WATER.get(), properties.stacksTo(1).durability(10)));
    public static final RegistryObject<Item> SALTED_SPRUCE_NUTS = registerItem("salted_spruce_nuts",
            properties -> new Item(properties.food(ModFoods.SALTED_SPRUCE_NUTS)));
    public static final RegistryObject<Item> SALTED_TOASTED_SPRUCE_NUTS = registerItem("salted_toasted_spruce_nuts",
            properties -> new Item(properties.food(ModFoods.SALTED_TOASTED_SPRUCE_NUTS)));
    public static final RegistryObject<Item> SALTED_RAW_MEAT = ITEMS.register("salted_raw_meat", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SALTED_RAW_FISH = ITEMS.register("salted_raw_fish", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CURED_MEAT = registerItem("cured_meat",
            properties -> new Item(properties.food(ModFoods.CURED_MEAT)));
    public static final RegistryObject<Item> CURED_FISH = registerItem("cured_fish",
            properties -> new Item(properties.food(ModFoods.CURED_FISH)));

    public static final RegistryObject<ThrowableResourceItem> SPRUCE_CONE = registerItem("spruce_cone",
            properties -> new ThrowableResourceItem(properties, SoundEvents.SCAFFOLDING_BREAK,
                    SpruceConeProjectile::new, SpruceConeProjectile::new));

    public static final RegistryObject<PebbleItem> PEBBLES = registerItem("pebbles", PebbleItem::new);

    public static final RegistryObject<Item> SPRUCE_NUTS = registerItem("spruce_nuts",
            properties -> new Item(properties.food(ModFoods.SPRUCE_NUTS)));

    public static final RegistryObject<Item> TOASTED_SPRUCE_NUTS = registerItem("toasted_spruce_nuts",
            properties -> new Item(properties.food(ModFoods.TOASTED_SPRUCE_NUTS)));

    public static final RegistryObject<Item> TWIG = ITEMS.register("twig", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TRAIL_MIX = registerItem("trail_mix",
            properties -> new Item(properties.food(ModFoods.TRAIL_MIX)));

    public static final RegistryObject<Item> MIXED_BERRIES = registerItem("mixed_berries",
            properties -> new Item(properties.food(ModFoods.MIXED_BERRIES)));

    public static final RegistryObject<Item> BLUEBERRIES = registerItem("blueberries",
            properties -> new net.minecraft.world.item.ItemNameBlockItem(ModBlocks.BLUEBERRY_BUSH.get(), properties.food(ModFoods.BLUEBERRIES)));

    public static final RegistryObject<Item> BLUEBERRY_COOKIE = registerItem("blueberry_cookie",
            properties -> new Item(properties.food(ModFoods.BLUEBERRY_COOKIE)));

    public static final RegistryObject<Item> BLUEBERRY_PIE = registerItem("blueberry_pie",
            properties -> new Item(properties.food(ModFoods.BLUEBERRY_PIE).stacksTo(1)));

    public static final RegistryObject<Item> SLICE_OF_BLUEBERRY_PIE = registerItem("slice_of_blueberry_pie",
            properties -> new Item(properties.food(ModFoods.SLICE_OF_BLUEBERRY_PIE)));

    public static final RegistryObject<Item> BLUEBERRY_JUICE = registerJuice("blueberry_juice", ModFoods.BLUEBERRY_JUICE);

    public static final RegistryObject<Item> BASKET = registerItem("basket",
            properties -> new BasketItem(properties.stacksTo(1)));

    public static final RegistryObject<Item> GLASS_JAR = registerItem("glass_jar",
            properties -> new net.minecraft.world.item.ItemNameBlockItem(ModBlocks.GLASS_JAR.get(), properties.stacksTo(16)));

    public static final RegistryObject<Item> BLUEBERRY_JAM = registerJam("blueberry_jam", () -> ModBlocks.BLUEBERRY_JAM.get());

    public static final RegistryObject<Item> STRAWBERRY = registerItem("strawberry",
            properties -> new net.minecraft.world.item.ItemNameBlockItem(ModBlocks.STRAWBERRY_PLANT.get(), properties.food(ModFoods.STRAWBERRY)));

    public static final RegistryObject<Item> STRAWBERRY_JUICE = registerJuice("strawberry_juice", ModFoods.STRAWBERRY_JUICE);

    public static final RegistryObject<Item> STRAWBERRY_JAM = registerJam("strawberry_jam", () -> ModBlocks.STRAWBERRY_JAM.get());
    public static final RegistryObject<Item> CHERRY_JAM = registerJam("cherry_jam", () -> ModBlocks.CHERRY_JAM.get());
    public static final RegistryObject<Item> APPLE_JAM = registerJam("apple_jam", () -> ModBlocks.APPLE_JAM.get());

    public static final RegistryObject<Item> WOODEN_BUCKET = registerItem("wooden_bucket",
            properties -> new WoodenBucketItem(Fluids.EMPTY, properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> WOODEN_WATER_BUCKET = registerItem("wooden_water_bucket",
            properties -> new WoodenBucketItem(Fluids.WATER, properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> WOODEN_LAVA_BUCKET = registerItem("wooden_lava_bucket",
            properties -> new WoodenLavaBucketItem(properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> WOODEN_POWDER_SNOW_BUCKET = registerItem("wooden_powder_snow_bucket",
            properties -> new WoodenPowderSnowBucketItem(properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> WOODEN_MILK_BUCKET = registerItem("wooden_milk_bucket",
            properties -> new WoodenMilkBucketItem(properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> COD_WOODEN_BUCKET = registerItem("cod_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.COD, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> SALMON_WOODEN_BUCKET = registerItem("salmon_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.SALMON, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> PUFFERFISH_WOODEN_BUCKET = registerItem("pufferfish_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.PUFFERFISH, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> TROPICAL_FISH_WOODEN_BUCKET = registerItem("tropical_fish_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.TROPICAL_FISH, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> AXOLOTL_WOODEN_BUCKET = registerItem("axolotl_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.AXOLOTL, SoundEvents.BUCKET_EMPTY_AXOLOTL, properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> TADPOLE_WOODEN_BUCKET = registerItem("tadpole_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityType.TADPOLE, SoundEvents.BUCKET_EMPTY_TADPOLE, properties.stacksTo(1).durability(10)));

    public static final RegistryObject<Item> FLAX_SEEDS = registerItem("flax_seeds",
            properties -> new net.minecraft.world.item.ItemNameBlockItem(ModBlocks.FLAX.get(), properties));

    public static final RegistryObject<Item> FLAX_FIBER = ITEMS.register("flax_fiber", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FLAX_FLOWER = ITEMS.register("flax_flower", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LINEN_THREAD = ITEMS.register("linen_thread", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LINEN_FABRIC = ITEMS.register("linen_fabric", () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CHERRIES = registerItem("cherries",
            properties -> new CherriesItem(properties.food(ModFoods.CHERRIES)));

    public static final RegistryObject<Item> CHERRY_JUICE = registerJuice("cherry_juice", ModFoods.CHERRY_JUICE);

    public static final RegistryObject<Item> APPLE_JUICE = registerJuice("apple_juice", ModFoods.APPLE_JUICE);

    public static final RegistryObject<Item> CACTUS_SIGN = registerItem("cactus_sign",
            properties -> new net.minecraft.world.item.StandingAndWallBlockItem(
                    ModBlocks.CACTUS_SIGN.get(),
                    ModBlocks.CACTUS_WALL_SIGN.get(),
                    properties.stacksTo(16),
                    net.minecraft.core.Direction.DOWN));

    public static final RegistryObject<Item> CACTUS_HANGING_SIGN = registerItem("cactus_hanging_sign",
            properties -> new net.minecraft.world.item.HangingSignItem(
                    ModBlocks.CACTUS_HANGING_SIGN.get(),
                    ModBlocks.CACTUS_WALL_HANGING_SIGN.get(),
                    properties.stacksTo(16)));

    public static final RegistryObject<Item> CACTUS_BOAT = registerItem("cactus_boat",
            properties -> new CactusBoatItem(false, properties.stacksTo(1)));

    public static final RegistryObject<Item> CACTUS_CHEST_BOAT = registerItem("cactus_chest_boat",
            properties -> new CactusBoatItem(true, properties.stacksTo(1)));

    public static final RegistryObject<SpikedLeatherArmorItem> SPIKED_LEATHER_HELMET =
            registerItem("spiked_leather_helmet",
                    properties -> new SpikedLeatherArmorItem(ArmorItem.Type.HELMET, properties
                    ));

    public static final RegistryObject<Item> PRICKLY_PEAR = registerItem("prickly_pear",
            properties -> new net.minecraft.world.item.ItemNameBlockItem(ModBlocks.PRICKLY_PEAR_CACTUS.get(), properties.food(ModFoods.PRICKLY_PEAR)));

    public static final RegistryObject<Item> CACTUS_SLICE = registerItem("cactus_slice",
            properties -> new Item(properties.food(ModFoods.CACTUS_SLICE)));

    public static final RegistryObject<Item> CACTUS_JUICE = registerJuice("cactus_juice", ModFoods.CACTUS_JUICE);

    public static final RegistryObject<SpikedLeatherArmorItem> SPIKED_LEATHER_CHESTPLATE =
            registerItem("spiked_leather_chestplate",
                    properties -> new SpikedLeatherArmorItem(ArmorItem.Type.CHESTPLATE, properties
                    ));

    public static final RegistryObject<SpikedLeatherArmorItem> SPIKED_LEATHER_LEGGINGS =
            registerItem("spiked_leather_leggings",
                    properties -> new SpikedLeatherArmorItem(ArmorItem.Type.LEGGINGS, properties
                    ));

    public static final RegistryObject<SpikedLeatherArmorItem> SPIKED_LEATHER_BOOTS =
            registerItem("spiked_leather_boots",
                    properties -> new SpikedLeatherArmorItem(ArmorItem.Type.BOOTS, properties
                    ));

    private static RegistryObject<Item> registerJuice(String name, FoodProperties food) {
        return registerItem(name, properties -> new ContainerFoodItem(
                properties.food(food).stacksTo(16), () -> Items.GLASS_BOTTLE));
    }

    private static RegistryObject<Item> registerJam(String name, Supplier<? extends Block> block) {
        return registerItem(name, properties -> new ContainerFoodBlockItem(
                block.get(),
                properties.food(ModFoods.JAM).stacksTo(16), GLASS_JAR::get));
    }

    private static <T extends Item> RegistryObject<T> registerItem(String name, java.util.function.Function<Item.Properties, T> factory) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties()));
    }
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
