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
    public void registerCategories(mezz.jei.api.registration.IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new GrindingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new SaltingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(mezz.jei.api.registration.IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(GrindingRecipeCategory.TYPE, ModBlocks.HAND_MILL.get(), ModBlocks.HAND_CRANK.get());
        registration.addCraftingStation(SaltingRecipeCategory.TYPE, ModBlocks.SALTING_TUB.get());
    }

    @Override
    public void registerGuiHandlers(mezz.jei.api.registration.IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(de.artemis.floraexpansion.client.screen.HandMillScreen.class, 76, 35, 24, 16, GrindingRecipeCategory.TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var grinding = de.artemis.floraexpansion.client.recipe.GrindingClientRecipes.get();
        registration.addRecipes(GrindingRecipeCategory.TYPE, grinding);
        var salting = de.artemis.floraexpansion.client.recipe.SaltingClientRecipes.get();
        registration.addRecipes(SaltingRecipeCategory.TYPE, salting);
        FloraExpansion.LOGGER.info("JEI: registered {} salting recipe(s).", salting.size());
        FloraExpansion.LOGGER.info("JEI: registered {} grinding recipe(s).", grinding.size());
        registration.addIngredientInfo(ModBlocks.HAND_MILL.get().asItem(), translatedLines("jei.floraexpansion.hand_mill.info"));
        registration.addIngredientInfo(ModBlocks.SALTING_TUB.get().asItem(), translatedLines("jei.floraexpansion.salting_tub.info"));
        registration.addIngredientInfo(ModBlocks.HAND_CRANK.get().asItem(), translatedLines("jei.floraexpansion.hand_crank.info"));
        registration.addIngredientInfo(ModBlocks.SALT_SPIKE.get().asItem(), translatedLines("jei.floraexpansion.salt_spike.info"));
        registration.addIngredientInfo(ModItems.SALT.get(), translatedLines("jei.floraexpansion.salt.info.1"));
        for (var bucket : java.util.List.of(ModItems.SALT_WATER_BUCKET.get(), ModItems.WOODEN_SALT_WATER_BUCKET.get()))
            registration.addIngredientInfo(bucket, translatedLines("jei.floraexpansion.salt_water.info"));
        registration.addIngredientInfo(ModBlocks.NEEDLE_LITTER.get(), translatedLines("jei.floraexpansion.needle_litter.info.1"));
        registration.addIngredientInfo(ModItems.SPRUCE_CONE.get(), translatedLines("jei.floraexpansion.spruce_cone.info.1"));
        registration.addIngredientInfo(ModItems.FLAX_SEEDS.get(), translatedLines("jei.floraexpansion.flax_seeds.info.1"));
        registration.addIngredientInfo(ModBlocks.CRATE.get(), translatedLines("jei.floraexpansion.crate.info.1"));
        registration.addIngredientInfo(ModItems.BASKET.get(), translatedLines("jei.floraexpansion.basket.info.1"));
        registration.addIngredientInfo(ModItems.LINEN_THREAD.get(), translatedLines("jei.floraexpansion.linen_thread.info.1"));
        for (var block : java.util.List.of(ModBlocks.CACTUS_LOG.get(), ModBlocks.CACTUS_WOOD.get()))
            registration.addIngredientInfo(block, translatedLines("jei.floraexpansion.cactus_log.info.1"));
        registration.addIngredientInfo(ModBlocks.GIANT_CACTUS_STEM.get(), translatedLines("jei.floraexpansion.giant_cactus_stem.info.1"));
        registration.addIngredientInfo(ModBlocks.GIANT_CACTUS_SHOOT.get(), translatedLines("jei.floraexpansion.giant_cactus_shoot.info.1"));
        registration.addIngredientInfo(ModBlocks.SMALL_CACTUS.get(), translatedLines("jei.floraexpansion.small_cactus.info.1"));
        registration.addIngredientInfo(ModBlocks.LARGE_BLUEBERRY_BUSH.get(), translatedLines("jei.floraexpansion.large_blueberry_bush.info.1"));
        for (var armor : java.util.List.of(ModItems.SPIKED_LEATHER_HELMET.get(), ModItems.SPIKED_LEATHER_CHESTPLATE.get(),
                ModItems.SPIKED_LEATHER_LEGGINGS.get(), ModItems.SPIKED_LEATHER_BOOTS.get()))
            registration.addIngredientInfo(armor, translatedLines("jei.floraexpansion.spiked_leather_armor.info.1"));
        for (var jar : java.util.List.of(ModItems.GLASS_JAR.get(), ModItems.BLUEBERRY_JAM.get(), ModItems.STRAWBERRY_JAM.get(),
                ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get()))
            registration.addIngredientInfo(jar, translatedLines("jei.floraexpansion.placeable_jar.info.1"));
        registration.addIngredientInfo(ModItems.WOODEN_BUCKET.get(), translatedLines("jei.floraexpansion.wooden_bucket.info.1"));
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
