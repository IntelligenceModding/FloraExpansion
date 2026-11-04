package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FloraExpansion.MODID);

    @SuppressWarnings("unused")
    public static final Supplier<CreativeModeTab> FLORA_EXPANSION_CREATIVE_TAB = CREATIVE_MODE_TAB.register("flora_expansion_creative_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.FLAX_FLOWER.get()))
                    .title(Component.translatable("floraexpansion.creative_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.NEEDLE_LITTER.get());
                        output.accept(ModItems.TWIG.get());
                        output.accept(ModItems.SPRUCE_CONE.get());
                        output.accept(ModItems.PEBBLES.get());
 output.accept(ModBlocks.SCATTERED_PEBBLES.get());

                        output.accept(ModItems.FLAX_SEEDS.get());
                        output.accept(ModItems.FLAX_FLOWER.get());
                        output.accept(ModItems.FLAX_FIBER.get());
                        output.accept(ModBlocks.FLAX_BALE.get());
                        output.accept(ModItems.LINEN_THREAD.get());
                        output.accept(ModItems.LINEN_FABRIC.get());
                        output.accept(ModBlocks.LINEN_BALE.get());
                        output.accept(ModBlocks.LINEN_CARPET.get());

                        output.accept(ModBlocks.LARGE_BLUEBERRY_BUSH.get());
                        output.accept(ModItems.BLUEBERRIES.get());
                        output.accept(ModItems.BLUEBERRY_JAM.get());
                        output.accept(ModItems.BLUEBERRY_JUICE.get());
                        output.accept(ModItems.BLUEBERRY_COOKIE.get());
                        output.accept(ModItems.BLUEBERRY_PIE.get());
                        output.accept(ModItems.SLICE_OF_BLUEBERRY_PIE.get());

                        output.accept(ModItems.STRAWBERRY.get());
                        output.accept(ModItems.STRAWBERRY_JAM.get());
                        output.accept(ModItems.STRAWBERRY_JUICE.get());
                        output.accept(ModBlocks.STRAWBERRY_CAKE.get());

                        output.accept(ModBlocks.CHERRY_PIT.get());
                        output.accept(ModBlocks.ORCHARD_CHERRY_LEAVES.get());
                        output.accept(ModItems.CHERRIES.get());
                        output.accept(ModItems.CHERRY_JAM.get());
                        output.accept(ModItems.CHERRY_JUICE.get());

                        output.accept(ModBlocks.APPLE_CORE.get());
                        output.accept(ModBlocks.APPLE_LEAVES.get());
                        output.accept(ModItems.APPLE_JAM.get());
                        output.accept(ModItems.APPLE_JUICE.get());

                        output.accept(ModItems.SPRUCE_NUTS.get());
                        output.accept(ModItems.TOASTED_SPRUCE_NUTS.get());
                        output.accept(ModItems.MIXED_BERRIES.get());
                        output.accept(ModItems.TRAIL_MIX.get());
                        output.accept(ModItems.SALT.get());
                        output.accept(ModItems.SALT_WATER_BUCKET.get());
                        output.accept(ModItems.RAW_SALT.get());
                        output.accept(ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
                        output.accept(ModBlocks.HAND_MILL.get());
                        output.accept(ModBlocks.SALTING_TUB.get());
                        output.accept(ModBlocks.HAND_CRANK.get());

                        output.accept(ModBlocks.CRYSTALLINE_SALT.get());
                        ModBlocks.CALCITE_ORES.forEach(ore -> output.accept(ore.get()));
                        output.accept(ModBlocks.SALT_SPIKE.get());
                        output.accept(ModBlocks.GLASSWORT.get());
                        output.accept(ModItems.SALTED_GLASSWORT.get());
                        output.accept(ModBlocks.SMOOTH_SALT.get());
                        output.accept(ModBlocks.CUT_SALT.get());
                        output.accept(ModBlocks.CHISELED_SALT.get());
                        output.accept(ModBlocks.CRYSTALLINE_SALT_STAIRS.get());
                        output.accept(ModBlocks.CRYSTALLINE_SALT_SLAB.get());
                        output.accept(ModBlocks.CRYSTALLINE_SALT_WALL.get());
                        output.accept(ModItems.SALTED_SPRUCE_NUTS.get());
                        output.accept(ModItems.SALTED_TOASTED_SPRUCE_NUTS.get());
                        output.accept(ModItems.SALTED_RAW_MEAT.get());
                        output.accept(ModItems.SALTED_RAW_FISH.get());
                        output.accept(ModItems.CURED_MEAT.get());
                        output.accept(ModItems.CURED_FISH.get());

                        output.accept(ModBlocks.GIANT_CACTUS_SHOOT.get());
                        output.accept(ModBlocks.GIANT_CACTUS_STEM.get());
                        output.accept(ModBlocks.SMALL_CACTUS.get());
                        output.accept(ModBlocks.DESERT_MOSS.get());
                        output.accept(ModBlocks.FLOWERING_DESERT_MOSS.get());
                        output.accept(ModItems.PRICKLY_PEAR.get());
                        output.accept(ModItems.CACTUS_SLICE.get());
                        output.accept(ModItems.CACTUS_JUICE.get());

                        output.accept(ModBlocks.TWIG_LADDER.get());
                        output.accept(ModBlocks.CACTUS_LOG.get());
                        output.accept(ModBlocks.CACTUS_WOOD.get());
                        output.accept(ModBlocks.STRIPPED_CACTUS_LOG.get());
                        output.accept(ModBlocks.STRIPPED_CACTUS_WOOD.get());
                        output.accept(ModBlocks.CACTUS_PLANKS.get());
                        output.accept(ModBlocks.CACTUS_MOSAIC.get());
                        output.accept(ModBlocks.CACTUS_MOSAIC_STAIRS.get());
                        output.accept(ModBlocks.CACTUS_MOSAIC_SLAB.get());
                        output.accept(ModBlocks.CACTUS_STAIRS.get());
                        output.accept(ModBlocks.CACTUS_SLAB.get());
                        output.accept(ModBlocks.CACTUS_FENCE.get());
                        output.accept(ModBlocks.CACTUS_FENCE_GATE.get());
                        output.accept(ModBlocks.CACTUS_DOOR.get());
                        output.accept(ModBlocks.CACTUS_TRAPDOOR.get());
                        output.accept(ModBlocks.CACTUS_PRESSURE_PLATE.get());
                        output.accept(ModBlocks.CACTUS_BUTTON.get());
                        output.accept(ModItems.CACTUS_SIGN.get());
                        output.accept(ModItems.CACTUS_HANGING_SIGN.get());
                        output.accept(ModItems.CACTUS_BOAT.get());
                        output.accept(ModItems.CACTUS_CHEST_BOAT.get());

                        output.accept(ModBlocks.CRATE.get());
                        output.accept(ModItems.BASKET.get());
                        output.accept(ModItems.GLASS_JAR.get());
                        output.accept(ModItems.WOODEN_BUCKET.get());
                        output.accept(ModItems.WOODEN_WATER_BUCKET.get());
                        output.accept(ModItems.WOODEN_SALT_WATER_BUCKET.get());
                        output.accept(ModItems.WOODEN_LAVA_BUCKET.get());
                        output.accept(ModItems.WOODEN_POWDER_SNOW_BUCKET.get());
                        output.accept(ModItems.WOODEN_MILK_BUCKET.get());
                        output.accept(ModItems.COD_WOODEN_BUCKET.get());
                        output.accept(ModItems.SALMON_WOODEN_BUCKET.get());
                        output.accept(ModItems.PUFFERFISH_WOODEN_BUCKET.get());
                        output.accept(ModItems.TROPICAL_FISH_WOODEN_BUCKET.get());
                        output.accept(ModItems.AXOLOTL_WOODEN_BUCKET.get());
                        output.accept(ModItems.TADPOLE_WOODEN_BUCKET.get());

                        output.accept(ModBlocks.CACTUS_SPINE.get());
                        output.accept(ModItems.SPIKED_LEATHER_HELMET.get());
                        output.accept(ModItems.SPIKED_LEATHER_CHESTPLATE.get());
                        output.accept(ModItems.SPIKED_LEATHER_LEGGINGS.get());
                        output.accept(ModItems.SPIKED_LEATHER_BOOTS.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
