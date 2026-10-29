package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumables;
import de.artemis.floraexpansion.common.projectile.PebblesProjectile;
import de.artemis.floraexpansion.common.projectile.SpruceConeProjectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import de.artemis.floraexpansion.common.item.*;
import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FloraExpansion.MODID);

    public static final DeferredItem<ThrowableResourceItem> SPRUCE_CONE = ITEMS.registerItem("spruce_cone",
            properties -> new ThrowableResourceItem(properties, SoundEvents.SCAFFOLDING_BREAK,
                    SpruceConeProjectile::new, SpruceConeProjectile::new));

    public static final DeferredItem<ThrowableResourceItem> PEBBLES = ITEMS.registerItem("pebbles",
            properties -> new ThrowableResourceItem(properties, SoundEvents.POINTED_DRIPSTONE_BREAK,
                    PebblesProjectile::new, PebblesProjectile::new));

    public static final DeferredItem<Item> SPRUCE_NUTS = ITEMS.registerItem("spruce_nuts",
            properties -> new Item(properties.food(ModFoods.SPRUCE_NUTS)));

    public static final DeferredItem<Item> TOASTED_SPRUCE_NUTS = ITEMS.registerItem("toasted_spruce_nuts",
            properties -> new Item(properties.food(ModFoods.TOASTED_SPRUCE_NUTS)));

    public static final DeferredItem<Item> TWIG = ITEMS.registerSimpleItem("twig");

    public static final DeferredItem<Item> TRAIL_MIX = ITEMS.registerItem("trail_mix",
            properties -> new Item(properties.food(ModFoods.TRAIL_MIX)));

    public static final DeferredItem<Item> MIXED_BERRIES = ITEMS.registerItem("mixed_berries",
            properties -> new Item(properties.food(ModFoods.MIXED_BERRIES)));

    public static final DeferredItem<Item> BLUEBERRIES = ITEMS.registerItem("blueberries",
            properties -> new BlockItem(ModBlocks.BLUEBERRY_BUSH.get(), properties.food(ModFoods.BLUEBERRIES)));

    public static final DeferredItem<Item> BLUEBERRY_COOKIE = ITEMS.registerItem("blueberry_cookie",
            properties -> new Item(properties.food(ModFoods.BLUEBERRY_COOKIE)));

    public static final DeferredItem<Item> BLUEBERRY_PIE = ITEMS.registerItem("blueberry_pie",
            properties -> new Item(properties.food(ModFoods.BLUEBERRY_PIE, ModConsumables.BLUEBERRY_PIE).stacksTo(1)));

    public static final DeferredItem<Item> SLICE_OF_BLUEBERRY_PIE = ITEMS.registerItem("slice_of_blueberry_pie",
            properties -> new Item(properties.food(ModFoods.SLICE_OF_BLUEBERRY_PIE)));

    public static final DeferredItem<Item> BLUEBERRY_JUICE = registerJuice("blueberry_juice", ModFoods.BLUEBERRY_JUICE);

    public static final DeferredItem<Item> BASKET = ITEMS.registerItem("basket",
            properties -> new BasketItem(properties.stacksTo(1)));

    public static final DeferredItem<Item> GLASS_JAR = ITEMS.registerItem("glass_jar",
            properties -> new BlockItem(ModBlocks.GLASS_JAR.get(), properties.stacksTo(16)));

    public static final DeferredItem<Item> BLUEBERRY_JAM = registerJam("blueberry_jam", () -> ModBlocks.BLUEBERRY_JAM.get());

    public static final DeferredItem<Item> STRAWBERRY = ITEMS.registerItem("strawberry",
            properties -> new BlockItem(ModBlocks.STRAWBERRY_PLANT.get(), properties.food(ModFoods.STRAWBERRY)));

    public static final DeferredItem<Item> STRAWBERRY_JUICE = registerJuice("strawberry_juice", ModFoods.STRAWBERRY_JUICE);

    public static final DeferredItem<Item> STRAWBERRY_JAM = registerJam("strawberry_jam", () -> ModBlocks.STRAWBERRY_JAM.get());
    public static final DeferredItem<Item> CHERRY_JAM = registerJam("cherry_jam", () -> ModBlocks.CHERRY_JAM.get());
    public static final DeferredItem<Item> APPLE_JAM = registerJam("apple_jam", () -> ModBlocks.APPLE_JAM.get());

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
            properties -> new WoodenMobBucketItem(EntityTypes.COD, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> SALMON_WOODEN_BUCKET = ITEMS.registerItem("salmon_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityTypes.SALMON, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> PUFFERFISH_WOODEN_BUCKET = ITEMS.registerItem("pufferfish_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityTypes.PUFFERFISH, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> TROPICAL_FISH_WOODEN_BUCKET = ITEMS.registerItem("tropical_fish_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityTypes.TROPICAL_FISH, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> AXOLOTL_WOODEN_BUCKET = ITEMS.registerItem("axolotl_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityTypes.AXOLOTL, SoundEvents.BUCKET_EMPTY_AXOLOTL, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> TADPOLE_WOODEN_BUCKET = ITEMS.registerItem("tadpole_wooden_bucket",
            properties -> new WoodenMobBucketItem(EntityTypes.TADPOLE, SoundEvents.BUCKET_EMPTY_TADPOLE, properties.stacksTo(1).durability(10)));

    public static final DeferredItem<Item> FLAX_SEEDS = ITEMS.registerItem("flax_seeds",
            properties -> new BlockItem(ModBlocks.FLAX.get(), properties));

    public static final DeferredItem<Item> FLAX_FIBER = ITEMS.registerSimpleItem("flax_fiber");

    public static final DeferredItem<Item> FLAX_FLOWER = ITEMS.registerSimpleItem("flax_flower");

    public static final DeferredItem<Item> LINEN_THREAD = ITEMS.registerSimpleItem("linen_thread");

    public static final DeferredItem<Item> LINEN_FABRIC = ITEMS.registerSimpleItem("linen_fabric");

    public static final DeferredItem<Item> CHERRIES = ITEMS.registerItem("cherries",
            properties -> new CherriesItem(properties.food(ModFoods.CHERRIES)));

    public static final DeferredItem<Item> CHERRY_JUICE = registerJuice("cherry_juice", ModFoods.CHERRY_JUICE);

    public static final DeferredItem<Item> APPLE_JUICE = registerJuice("apple_juice", ModFoods.APPLE_JUICE);

    public static final DeferredItem<Item> CACTUS_SIGN = ITEMS.registerItem("cactus_sign",
            properties -> new net.minecraft.world.item.StandingAndWallBlockItem(
                    ModBlocks.CACTUS_SIGN.get(),
                    ModBlocks.CACTUS_WALL_SIGN.get(),
                    net.minecraft.core.Direction.DOWN,
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

    public static final DeferredItem<SpikedLeatherArmorItem> SPIKED_LEATHER_HELMET =
            ITEMS.registerItem("spiked_leather_helmet",
                    properties -> new SpikedLeatherArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.SPIKED_LEATHER_ARMOR_MATERIAL, ArmorType.HELMET)
                    ));

    public static final DeferredItem<Item> PRICKLY_PEAR = ITEMS.registerItem("prickly_pear",
            properties -> new BlockItem(ModBlocks.PRICKLY_PEAR_CACTUS.get(), properties.food(ModFoods.PRICKLY_PEAR)));

    public static final DeferredItem<Item> CACTUS_SLICE = ITEMS.registerItem("cactus_slice",
            properties -> new Item(properties.food(ModFoods.CACTUS_SLICE)));

    public static final DeferredItem<Item> CACTUS_JUICE = registerJuice("cactus_juice", ModFoods.CACTUS_JUICE);

    public static final DeferredItem<SpikedLeatherArmorItem> SPIKED_LEATHER_CHESTPLATE =
            ITEMS.registerItem("spiked_leather_chestplate",
                    properties -> new SpikedLeatherArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.SPIKED_LEATHER_ARMOR_MATERIAL, ArmorType.CHESTPLATE)
                    ));

    public static final DeferredItem<SpikedLeatherArmorItem> SPIKED_LEATHER_LEGGINGS =
            ITEMS.registerItem("spiked_leather_leggings",
                    properties -> new SpikedLeatherArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.SPIKED_LEATHER_ARMOR_MATERIAL, ArmorType.LEGGINGS)
                    ));

    public static final DeferredItem<SpikedLeatherArmorItem> SPIKED_LEATHER_BOOTS =
            ITEMS.registerItem("spiked_leather_boots",
                    properties -> new SpikedLeatherArmorItem(
                            properties.humanoidArmor(ModArmorMaterials.SPIKED_LEATHER_ARMOR_MATERIAL, ArmorType.BOOTS)
                    ));

    private static DeferredItem<Item> registerJuice(String name, FoodProperties food) {
        return ITEMS.registerItem(name, properties -> new ContainerFoodItem(
                properties.food(food, Consumables.DEFAULT_DRINK).stacksTo(16), () -> Items.GLASS_BOTTLE));
    }

    private static DeferredItem<Item> registerJam(String name, Supplier<? extends Block> block) {
        return ITEMS.registerItem(name, properties -> new ContainerFoodBlockItem(
                block.get(),
                properties.food(ModFoods.JAM, ModConsumables.JAM).stacksTo(16), GLASS_JAR::get));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
