package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FloraExpansion.MODID);

    @SuppressWarnings("unused")
    public static final Supplier<CreativeModeTab> FLORA_EXPANSION_CREATIVE_TAB = CREATIVE_MODE_TAB.register("flora_expansion_creative_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FLAX_FLOWER.get()))
                    .title(Component.translatable("floraexpansion.creative_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.LEAF_LITTER);
                        output.accept(ModBlocks.PINE_LITTER);
                        output.accept(ModItems.PINE_CONE);
                        output.accept(ModItems.PINE_NUTS);
                        output.accept(ModItems.TOASTED_PINE_NUTS);
                        output.accept(ModItems.TWIG);
                        output.accept(ModBlocks.TWIG_LADDER);
                        output.accept(ModBlocks.CRATE);
                        output.accept(ModItems.BASKET);
                        output.accept(ModItems.FOREST_SNACK);
                        output.accept(ModItems.SWEET_BERRY_MIX);
                        output.accept(ModItems.BLUEBERRIES);
                        output.accept(ModBlocks.LARGE_BLUEBERRY_BUSH);
                        output.accept(ModItems.BLUEBERRY_COOKIE);
                        output.accept(ModItems.BLUEBERRY_PIE);
                        output.accept(ModItems.BLUEBERRY_PIE_SLICE);
                        output.accept(ModItems.BLUEBERRY_JUICE);
                        output.accept(ModItems.EMPTY_JAR);
                        output.accept(ModItems.BLUEBERRY_JAM);
                        output.accept(ModItems.STRAWBERRY);
                        output.accept(ModItems.STRAWBERRY_JAM);
                        output.accept(ModBlocks.STRAWBERRY_CAKE);
                        output.accept(ModItems.WOODEN_BUCKET);
                        output.accept(ModItems.WOODEN_WATER_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.WOODEN_LAVA_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.WOODEN_POWDER_SNOW_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.WOODEN_MILK_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.COD_WOODEN_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.SALMON_WOODEN_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.PUFFERFISH_WOODEN_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.TROPICAL_FISH_WOODEN_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.AXOLOTL_WOODEN_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.TADPOLE_WOODEN_BUCKET, TabVisibility.SEARCH_TAB_ONLY);
                        output.accept(ModItems.FLAX_SEED);
                        output.accept(ModItems.FLAX_FIBER);
                        output.accept(ModBlocks.FLAX_BALE);
                        output.accept(ModItems.FLAX_FLOWER);
                        output.accept(ModItems.LINEN_THREAD);
                        output.accept(ModItems.LINEN_CLOTH);
                        output.accept(ModBlocks.LINEN_BLOCK);
                        output.accept(ModBlocks.LINEN_CARPET);
                        output.accept(ModBlocks.PEBBLE_BLOCK);
                        output.accept(ModBlocks.PEBBLE_PATCH);
                        output.accept(ModItems.PEBBLES);
                        output.accept(ModBlocks.FRUITING_CHERRY_LEAVES);
                        output.accept(ModItems.CHERRIES);
                        output.accept(ModBlocks.CHERRY_PIT);
                        output.accept(ModItems.CHERRY_JUICE);
                        output.accept(ModBlocks.FRUITING_OAK_LEAVES);
                        output.accept(ModBlocks.APPLE_CORE);
                        output.accept(ModItems.APPLE_JUICE);
                        output.accept(ModBlocks.GIANT_CACTUS_BASE);
                        output.accept(ModBlocks.GIANT_CACTUS_WOOD);
                        output.accept(ModBlocks.STRIPPED_GIANT_CACTUS_BASE);
                        output.accept(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD);
                        output.accept(ModBlocks.GIANT_CACTUS_STEM);
                        output.accept(ModBlocks.CACTUS_THORN);
                        output.accept(ModBlocks.CACTUS_FLOWER);
                        output.accept(ModBlocks.CACTUS_PLANKS);
                        output.accept(ModBlocks.CACTUS_MOSAIC);
                        output.accept(ModBlocks.CACTUS_STAIRS);
                        output.accept(ModBlocks.CACTUS_SLAB);
                        output.accept(ModBlocks.CACTUS_FENCE);
                        output.accept(ModBlocks.CACTUS_FENCE_GATE);
                        output.accept(ModBlocks.CACTUS_BUTTON);
                        output.accept(ModBlocks.CACTUS_PRESSURE_PLATE);
                        output.accept(ModBlocks.CACTUS_DOOR);
                        output.accept(ModBlocks.CACTUS_TRAPDOOR);
                        output.accept(ModItems.CACTUS_SIGN);
                        output.accept(ModItems.CACTUS_HANGING_SIGN);
                        output.accept(ModItems.CACTUS_BOAT);
                        output.accept(ModItems.CACTUS_CHEST_BOAT);
                        output.accept(ModItems.CACTUS_HELMET);
                        output.accept(ModItems.CACTUS_CHESTPLATE);
                        output.accept(ModItems.CACTUS_LEGGINGS);
                        output.accept(ModItems.CACTUS_BOOTS);
                        output.accept(ModBlocks.DESERT_MOSS);
                        output.accept(ModItems.CACTUS_SLICE);
                        output.accept(ModBlocks.CACTUS_CLUSTER);
                        output.accept(ModItems.CACTUS_JUICE);
                        output.accept(ModBlocks.OPUNTIA_CACTUS);
                        output.accept(ModItems.PRICKLY_PEAR);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}


