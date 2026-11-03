package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
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
                        output.accept(ModBlocks.NEEDLE_LITTER);
                        output.accept(ModItems.TWIG);
                        output.accept(ModItems.SPRUCE_CONE);
                        output.accept(ModItems.PEBBLES);
 output.accept(ModBlocks.SCATTERED_PEBBLES);

                        output.accept(ModItems.FLAX_SEEDS);
                        output.accept(ModItems.FLAX_FLOWER);
                        output.accept(ModItems.FLAX_FIBER);
                        output.accept(ModBlocks.FLAX_BALE);
                        output.accept(ModItems.LINEN_THREAD);
                        output.accept(ModItems.LINEN_FABRIC);
                        output.accept(ModBlocks.LINEN_BALE);
                        output.accept(ModBlocks.LINEN_CARPET);

                        output.accept(ModBlocks.LARGE_BLUEBERRY_BUSH);
                        output.accept(ModItems.BLUEBERRIES);
                        output.accept(ModItems.BLUEBERRY_JAM);
                        output.accept(ModItems.BLUEBERRY_JUICE);
                        output.accept(ModItems.BLUEBERRY_COOKIE);
                        output.accept(ModItems.BLUEBERRY_PIE);
                        output.accept(ModItems.SLICE_OF_BLUEBERRY_PIE);

                        output.accept(ModItems.STRAWBERRY);
                        output.accept(ModItems.STRAWBERRY_JAM);
                        output.accept(ModItems.STRAWBERRY_JUICE);
                        output.accept(ModBlocks.STRAWBERRY_CAKE);

                        output.accept(ModBlocks.CHERRY_PIT);
                        output.accept(ModBlocks.ORCHARD_CHERRY_LEAVES);
                        output.accept(ModItems.CHERRIES);
                        output.accept(ModItems.CHERRY_JAM);
                        output.accept(ModItems.CHERRY_JUICE);

                        output.accept(ModBlocks.APPLE_CORE);
                        output.accept(ModBlocks.APPLE_LEAVES);
                        output.accept(ModItems.APPLE_JAM);
                        output.accept(ModItems.APPLE_JUICE);

                        output.accept(ModItems.SPRUCE_NUTS);
                        output.accept(ModItems.TOASTED_SPRUCE_NUTS);
                        output.accept(ModItems.MIXED_BERRIES);
                        output.accept(ModItems.TRAIL_MIX);
                        output.accept(ModItems.SALT);
                        output.accept(ModItems.SALT_WATER_BUCKET);
                        output.accept(ModItems.RAW_SALT);
                        output.accept(ModItems.MUSIC_DISC_INTO_THE_CAVES);
                        output.accept(ModBlocks.HAND_MILL);
                        output.accept(ModBlocks.SALTING_TUB);
                        output.accept(ModBlocks.HAND_CRANK);

                        output.accept(ModBlocks.CRYSTALLINE_SALT);
                        ModBlocks.CALCITE_ORES.forEach(output::accept);
                        output.accept(ModBlocks.SALT_SPIKE);
                        output.accept(ModBlocks.GLASSWORT);
                        output.accept(ModItems.SALTED_GLASSWORT);
                        output.accept(ModBlocks.SMOOTH_SALT);
                        output.accept(ModBlocks.CUT_SALT);
                        output.accept(ModBlocks.CHISELED_SALT);
                        output.accept(ModBlocks.CRYSTALLINE_SALT_STAIRS);
                        output.accept(ModBlocks.CRYSTALLINE_SALT_SLAB);
                        output.accept(ModBlocks.CRYSTALLINE_SALT_WALL);
                        output.accept(ModItems.SALTED_SPRUCE_NUTS);
                        output.accept(ModItems.SALTED_TOASTED_SPRUCE_NUTS);
                        output.accept(ModItems.SALTED_RAW_MEAT);
                        output.accept(ModItems.SALTED_RAW_FISH);
                        output.accept(ModItems.CURED_MEAT);
                        output.accept(ModItems.CURED_FISH);

                        output.accept(ModBlocks.GIANT_CACTUS_SHOOT);
                        output.accept(ModBlocks.GIANT_CACTUS_STEM);
                        output.accept(ModBlocks.SMALL_CACTUS);
                        output.accept(ModBlocks.DESERT_MOSS);
                        output.accept(ModBlocks.FLOWERING_DESERT_MOSS);
                        output.accept(ModItems.PRICKLY_PEAR);
                        output.accept(ModItems.CACTUS_SLICE);
                        output.accept(ModItems.CACTUS_JUICE);

                        output.accept(ModBlocks.TWIG_LADDER);
                        output.accept(ModBlocks.CACTUS_LOG);
                        output.accept(ModBlocks.CACTUS_WOOD);
                        output.accept(ModBlocks.STRIPPED_CACTUS_LOG);
                        output.accept(ModBlocks.STRIPPED_CACTUS_WOOD);
                        output.accept(ModBlocks.CACTUS_PLANKS);
                        output.accept(ModBlocks.CACTUS_MOSAIC);
                        output.accept(ModBlocks.CACTUS_MOSAIC_STAIRS);
                        output.accept(ModBlocks.CACTUS_MOSAIC_SLAB);
                        output.accept(ModBlocks.CACTUS_STAIRS);
                        output.accept(ModBlocks.CACTUS_SLAB);
                        output.accept(ModBlocks.CACTUS_FENCE);
                        output.accept(ModBlocks.CACTUS_FENCE_GATE);
                        output.accept(ModBlocks.CACTUS_DOOR);
                        output.accept(ModBlocks.CACTUS_TRAPDOOR);
                        output.accept(ModBlocks.CACTUS_PRESSURE_PLATE);
                        output.accept(ModBlocks.CACTUS_BUTTON);
                        output.accept(ModItems.CACTUS_SIGN);
                        output.accept(ModItems.CACTUS_HANGING_SIGN);
                        output.accept(ModItems.CACTUS_BOAT);
                        output.accept(ModItems.CACTUS_CHEST_BOAT);

                        output.accept(ModBlocks.CRATE);
                        output.accept(ModItems.BASKET);
                        output.accept(ModItems.GLASS_JAR);
                        output.accept(ModItems.WOODEN_BUCKET);
                        output.accept(ModItems.WOODEN_WATER_BUCKET);
                        output.accept(ModItems.WOODEN_SALT_WATER_BUCKET);
                        output.accept(ModItems.WOODEN_LAVA_BUCKET);
                        output.accept(ModItems.WOODEN_POWDER_SNOW_BUCKET);
                        output.accept(ModItems.WOODEN_MILK_BUCKET);
                        output.accept(ModItems.COD_WOODEN_BUCKET);
                        output.accept(ModItems.SALMON_WOODEN_BUCKET);
                        output.accept(ModItems.PUFFERFISH_WOODEN_BUCKET);
                        output.accept(ModItems.TROPICAL_FISH_WOODEN_BUCKET);
                        output.accept(ModItems.AXOLOTL_WOODEN_BUCKET);
                        output.accept(ModItems.TADPOLE_WOODEN_BUCKET);

                        output.accept(ModBlocks.CACTUS_SPINE);
                        output.accept(ModItems.SPIKED_LEATHER_HELMET);
                        output.accept(ModItems.SPIKED_LEATHER_CHESTPLATE);
                        output.accept(ModItems.SPIKED_LEATHER_LEGGINGS);
                        output.accept(ModItems.SPIKED_LEATHER_BOOTS);
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
