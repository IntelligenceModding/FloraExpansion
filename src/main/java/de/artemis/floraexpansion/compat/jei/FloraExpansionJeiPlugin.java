package de.artemis.floraexpansion.compat.jei;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.item.SpikedLeatherArmorItem;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
@JeiPlugin
public final class FloraExpansionJeiPlugin implements IModPlugin {
    private static final Identifier UID =
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "jei_plugin");

    @Override
    public @NotNull Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                ModBlocks.NEEDLE_LITTER.get(),
                translatedLines(
                        "jei.floraexpansion.needle_litter.info.1",
                        "jei.floraexpansion.needle_litter.info.2",
                        "jei.floraexpansion.needle_litter.info.3",
                        "jei.floraexpansion.needle_litter.info.4"
                )
        );

        registration.addIngredientInfo(
                ModItems.SPRUCE_CONE.get(),
                translatedLines(
                        "jei.floraexpansion.spruce_cone.info.1",
                        "jei.floraexpansion.spruce_cone.info.2",
                        "jei.floraexpansion.spruce_cone.info.3",
                        "jei.floraexpansion.spruce_cone.info.4"
                )
        );

        registration.addIngredientInfo(
                ModItems.FLAX_SEEDS.get(),
                translatedLines(
                        "jei.floraexpansion.flax_seeds.info.1",
                        "jei.floraexpansion.flax_seeds.info.2",
                        "jei.floraexpansion.flax_seeds.info.3",
                        "jei.floraexpansion.flax_seeds.info.4"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.SCATTERED_PEBBLES.get(),
                translatedLines(
                        "jei.floraexpansion.scattered_pebbles.info.1",
                        "jei.floraexpansion.scattered_pebbles.info.2",
                        "jei.floraexpansion.scattered_pebbles.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.PEBBLES.get(),
                translatedLines(
                        "jei.floraexpansion.pebbles.info.1",
                        "jei.floraexpansion.pebbles.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.CRATE.get(),
                translatedLines(
                        "jei.floraexpansion.crate.info.1",
                        "jei.floraexpansion.crate.info.2",
                        "jei.floraexpansion.crate.info.3",
                        "jei.floraexpansion.crate.info.4",
                        "jei.floraexpansion.crate.info.5"
                )
        );

        registration.addIngredientInfo(
                ModItems.BASKET.get(),
                translatedLines(
                        "jei.floraexpansion.basket.info.1",
                        "jei.floraexpansion.basket.info.2",
                        "jei.floraexpansion.basket.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.LINEN_THREAD.get(),
                translatedLines(
                        "jei.floraexpansion.linen_thread.info.1",
                        "jei.floraexpansion.linen_thread.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.ORCHARD_CHERRY_LEAVES.get(),
                translatedLines(
                        "jei.floraexpansion.orchard_cherry_leaves.info.1",
                        "jei.floraexpansion.orchard_cherry_leaves.info.2",
                        "jei.floraexpansion.orchard_cherry_leaves.info.3",
                        "jei.floraexpansion.orchard_cherry_leaves.info.4"
                )
        );

        registration.addIngredientInfo(
                ModItems.CHERRIES.get(),
                translatedLines(
                        "jei.floraexpansion.cherries.info.1",
                        "jei.floraexpansion.cherries.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.CHERRY_PIT.get(),
                translatedLines(
                        "jei.floraexpansion.cherry_pit.info.1",
                        "jei.floraexpansion.cherry_pit.info.2"
                )
        );

        registration.addIngredientInfo(
                ModItems.CHERRY_JUICE.get(),
                translatedLines(
                        "jei.floraexpansion.bottled_juice.info.1",
                        "jei.floraexpansion.bottled_juice.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.APPLE_LEAVES.get(),
                translatedLines(
                        "jei.floraexpansion.apple_leaves.info.1",
                        "jei.floraexpansion.apple_leaves.info.2",
                        "jei.floraexpansion.apple_leaves.info.3"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.APPLE_CORE.get(),
                translatedLines(
                        "jei.floraexpansion.apple_core.info.1",
                        "jei.floraexpansion.apple_core.info.2"
                )
        );

        registration.addIngredientInfo(
                ModItems.APPLE_JUICE.get(),
                translatedLines(
                        "jei.floraexpansion.bottled_juice.info.1",
                        "jei.floraexpansion.bottled_juice.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.CACTUS_LOG.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_log.info.1",
                        "jei.floraexpansion.cactus_log.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.CACTUS_WOOD.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_wood.info.1",
                        "jei.floraexpansion.cactus_wood.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.GIANT_CACTUS_STEM.get(),
                translatedLines(
                        "jei.floraexpansion.giant_cactus_stem.info.1",
                        "jei.floraexpansion.giant_cactus_stem.info.2",
                        "jei.floraexpansion.giant_cactus_stem.info.3"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.CACTUS_SPINE.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_spine.info.1",
                        "jei.floraexpansion.cactus_spine.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.GIANT_CACTUS_SHOOT.get(),
                translatedLines(
                        "jei.floraexpansion.giant_cactus_shoot.info.1",
                        "jei.floraexpansion.giant_cactus_shoot.info.2",
                        "jei.floraexpansion.giant_cactus_shoot.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.SPIKED_LEATHER_HELMET.get(),
                translatedLines(
                        "jei.floraexpansion.spiked_leather_armor.info.1",
                        "jei.floraexpansion.spiked_leather_armor.info.2",
                        "jei.floraexpansion.spiked_leather_armor.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.SPIKED_LEATHER_CHESTPLATE.get(),
                translatedLines(
                        "jei.floraexpansion.spiked_leather_armor.info.1",
                        "jei.floraexpansion.spiked_leather_armor.info.2",
                        "jei.floraexpansion.spiked_leather_armor.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.SPIKED_LEATHER_LEGGINGS.get(),
                translatedLines(
                        "jei.floraexpansion.spiked_leather_armor.info.1",
                        "jei.floraexpansion.spiked_leather_armor.info.2",
                        "jei.floraexpansion.spiked_leather_armor.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.SPIKED_LEATHER_BOOTS.get(),
                translatedLines(
                        "jei.floraexpansion.spiked_leather_armor.info.1",
                        "jei.floraexpansion.spiked_leather_armor.info.2",
                        "jei.floraexpansion.spiked_leather_armor.info.3"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.SMALL_CACTUS.get(),
                translatedLines(
                        "jei.floraexpansion.small_cactus.info.1",
                        "jei.floraexpansion.small_cactus.info.2",
                        "jei.floraexpansion.small_cactus.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.CACTUS_JUICE.get(),
                translatedLines(
                        "jei.floraexpansion.bottled_juice.info.1",
                        "jei.floraexpansion.bottled_juice.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.DESERT_MOSS.get(), translatedLines("jei.floraexpansion.desert_moss.info.1"));
        registration.addIngredientInfo(ModBlocks.FLOWERING_DESERT_MOSS.get(), translatedLines(
                "jei.floraexpansion.flowering_desert_moss.info.1", "jei.floraexpansion.flowering_desert_moss.info.2"));
        registration.addIngredientInfo(
                ModBlocks.PRICKLY_PEAR_CACTUS.get(),
                translatedLines(
                        "jei.floraexpansion.prickly_pear_cactus.info.1",
                        "jei.floraexpansion.prickly_pear_cactus.info.2",
                        "jei.floraexpansion.prickly_pear_cactus.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.PRICKLY_PEAR.get(),
                translatedLines(
                        "jei.floraexpansion.prickly_pear.info.1",
                        "jei.floraexpansion.prickly_pear.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.BLUEBERRY_BUSH.get(),
                translatedLines(
                        "jei.floraexpansion.small_blueberry_bush.info.1",
                        "jei.floraexpansion.small_blueberry_bush.info.2",
                        "jei.floraexpansion.small_blueberry_bush.info.3",
                        "jei.floraexpansion.small_blueberry_bush.info.4"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.LARGE_BLUEBERRY_BUSH.get(),
                translatedLines(
                        "jei.floraexpansion.large_blueberry_bush.info.1",
                        "jei.floraexpansion.large_blueberry_bush.info.2",
                        "jei.floraexpansion.large_blueberry_bush.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.BLUEBERRY_PIE.get(),
                translatedLines(
                        "jei.floraexpansion.blueberry_pie.info.1",
                        "jei.floraexpansion.blueberry_pie.info.2",
                        "jei.floraexpansion.blueberry_pie.info.3"
                )
        );

        for (var juice : java.util.List.of(ModItems.BLUEBERRY_JUICE.get(), ModItems.STRAWBERRY_JUICE.get(),
                ModItems.CHERRY_JUICE.get(), ModItems.APPLE_JUICE.get(), ModItems.CACTUS_JUICE.get())) {
            registration.addIngredientInfo(juice, translatedLines("jei.floraexpansion.bottled_juice.info.1",
                    "jei.floraexpansion.bottled_juice.info.2"));
        }
        registration.addIngredientInfo(ModItems.STRAWBERRY_JUICE.get(), translatedLines("jei.floraexpansion.strawberry_juice.info"));
        registration.addIngredientInfo(ModItems.MIXED_BERRIES.get(), translatedLines("jei.floraexpansion.mixed_berries.info"));
        registration.addIngredientInfo(ModBlocks.STRAWBERRY_CAKE.get().asItem(), translatedLines(
                "jei.floraexpansion.strawberry_cake.info.1", "jei.floraexpansion.strawberry_cake.info.2",
                "jei.floraexpansion.strawberry_cake.info.3"));
        for (var mosaic : java.util.List.of(ModBlocks.CACTUS_MOSAIC.get(), ModBlocks.CACTUS_MOSAIC_STAIRS.get(), ModBlocks.CACTUS_MOSAIC_SLAB.get())) {
            registration.addIngredientInfo(mosaic.asItem(), translatedLines("jei.floraexpansion.cactus_mosaic.info"));
        }

        registration.addIngredientInfo(
                ModItems.BLUEBERRY_JAM.get(),
                translatedLines(
                        "jei.floraexpansion.blueberry_jam.info.1",
                        "jei.floraexpansion.blueberry_jam.info.2"
                )
        );

        registration.addIngredientInfo(
                ModItems.STRAWBERRY.get(),
                translatedLines(
                        "jei.floraexpansion.strawberry.info.1",
                        "jei.floraexpansion.strawberry.info.2"
                )
        );

        registration.addIngredientInfo(
                ModItems.STRAWBERRY_JAM.get(),
                translatedLines(
                        "jei.floraexpansion.strawberry_jam.info.1",
                        "jei.floraexpansion.strawberry_jam.info.2"
                )
        );

        registration.addIngredientInfo(ModItems.CHERRY_JAM.get(), translatedLines(
                "jei.floraexpansion.cherry_jam.info.1", "jei.floraexpansion.glass_jar.info.2"));
        registration.addIngredientInfo(ModItems.APPLE_JAM.get(), translatedLines(
                "jei.floraexpansion.apple_jam.info.1", "jei.floraexpansion.glass_jar.info.2"));
        for (var jar : java.util.List.of(ModItems.GLASS_JAR.get(), ModItems.BLUEBERRY_JAM.get(),
                ModItems.STRAWBERRY_JAM.get(), ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get())) {
            registration.addIngredientInfo(jar, translatedLines("jei.floraexpansion.placeable_jar.info.1"));
        }
        for (var jam : java.util.List.of(ModItems.BLUEBERRY_JAM.get(), ModItems.STRAWBERRY_JAM.get(),
                ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get())) {
            registration.addIngredientInfo(jam, translatedLines("jei.floraexpansion.placeable_jar.info.2"));
            registration.addIngredientInfo(jam, translatedLines("jei.floraexpansion.jam_portions.info.1",
                    "jei.floraexpansion.jam_portions.info.2", "jei.floraexpansion.jam_portions.info.3",
                    "jei.floraexpansion.jam_trades.info"));
        }

        registration.addIngredientInfo(
                ModItems.GLASS_JAR.get(),
                translatedLines(
                        "jei.floraexpansion.glass_jar.info.1",
                        "jei.floraexpansion.glass_jar.info.2"
                )
        );

        registration.addIngredientInfo(
                ModItems.WOODEN_BUCKET.get(),
                translatedLines(
                        "jei.floraexpansion.wooden_bucket.info.1",
                        "jei.floraexpansion.wooden_bucket.info.2",
                        "jei.floraexpansion.wooden_bucket.info.3"
                )
        );
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        IRecipeManager recipeManager = jeiRuntime.getRecipeManager();

        List<IJeiAnvilRecipe> toHide = recipeManager.createRecipeLookup(RecipeTypes.ANVIL)
                .get()
                .filter(FloraExpansionJeiPlugin::shouldHideAnvilRecipe)
                .toList();

        if (!toHide.isEmpty()) {
            recipeManager.hideRecipes(RecipeTypes.ANVIL, toHide);
            FloraExpansion.LOGGER.info("JEI: hid {} spiked leather armor + thorns anvil recipe(s).", toHide.size());
        }
    }

    private static boolean shouldHideAnvilRecipe(IJeiAnvilRecipe recipe) {
        return recipe.getLeftInputs().stream().anyMatch(SpikedLeatherArmorItem::isSpikedLeatherArmor)
                && recipe.getRightInputs().stream().anyMatch(FloraExpansionJeiPlugin::hasThorns);
    }

    private static boolean hasThorns(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Holder.Reference<Enchantment> thorns = getThornsHolder();
        if (thorns == null) {
            return false;
        }

        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored != null && stored.getLevel(thorns) > 0) {
            return true;
        }

        ItemEnchantments direct = stack.get(DataComponents.ENCHANTMENTS);
        return direct != null && direct.getLevel(thorns) > 0;
    }

    private static Holder.Reference<Enchantment> getThornsHolder() {
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }

        HolderLookup.RegistryLookup<Enchantment> lookup =
                minecraft.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        return lookup.getOrThrow(Enchantments.THORNS);
    }

    private static Component[] translatedLines(String... keys) {
        Component[] lines = new Component[keys.length];
        for (int i = 0; i < keys.length; i++) {
            lines[i] = Component.translatable(keys[i]);
        }
        return lines;
    }
}
