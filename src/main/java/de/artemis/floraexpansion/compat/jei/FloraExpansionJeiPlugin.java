package de.artemis.floraexpansion.compat.jei;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.item.CactusArmorItem;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
@JeiPlugin
public final class FloraExpansionJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                ModBlocks.LEAF_LITTER.get(),
                translatedLines(
                        "jei.floraexpansion.leaf_litter.info.1",
                        "jei.floraexpansion.leaf_litter.info.2",
                        "jei.floraexpansion.leaf_litter.info.3",
                        "jei.floraexpansion.leaf_litter.info.4"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.PINE_LITTER.get(),
                translatedLines(
                        "jei.floraexpansion.pine_litter.info.1",
                        "jei.floraexpansion.pine_litter.info.2",
                        "jei.floraexpansion.pine_litter.info.3",
                        "jei.floraexpansion.pine_litter.info.4"
                )
        );

        registration.addIngredientInfo(
                ModItems.PINE_CONE.get(),
                translatedLines(
                        "jei.floraexpansion.pine_cone.info.1",
                        "jei.floraexpansion.pine_cone.info.2",
                        "jei.floraexpansion.pine_cone.info.3",
                        "jei.floraexpansion.pine_cone.info.4"
                )
        );

        registration.addIngredientInfo(
                ModItems.FLAX_SEED.get(),
                translatedLines(
                        "jei.floraexpansion.flax_seed.info.1",
                        "jei.floraexpansion.flax_seed.info.2",
                        "jei.floraexpansion.flax_seed.info.3",
                        "jei.floraexpansion.flax_seed.info.4"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.PEBBLE_PATCH.get(),
                translatedLines(
                        "jei.floraexpansion.pebble_patch.info.1",
                        "jei.floraexpansion.pebble_patch.info.2",
                        "jei.floraexpansion.pebble_patch.info.3"
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
                ModBlocks.FRUITING_CHERRY_LEAVES.get(),
                translatedLines(
                        "jei.floraexpansion.fruiting_cherry_leaves.info.1",
                        "jei.floraexpansion.fruiting_cherry_leaves.info.2",
                        "jei.floraexpansion.fruiting_cherry_leaves.info.3",
                        "jei.floraexpansion.fruiting_cherry_leaves.info.4"
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
                ModBlocks.FRUITING_OAK_LEAVES.get(),
                translatedLines(
                        "jei.floraexpansion.fruiting_oak_leaves.info.1",
                        "jei.floraexpansion.fruiting_oak_leaves.info.2",
                        "jei.floraexpansion.fruiting_oak_leaves.info.3"
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
                ModBlocks.GIANT_CACTUS_BASE.get(),
                translatedLines(
                        "jei.floraexpansion.giant_cactus_base.info.1",
                        "jei.floraexpansion.giant_cactus_base.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.GIANT_CACTUS_WOOD.get(),
                translatedLines(
                        "jei.floraexpansion.giant_cactus_wood.info.1",
                        "jei.floraexpansion.giant_cactus_wood.info.2"
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
                ModBlocks.CACTUS_THORN.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_thorn.info.1",
                        "jei.floraexpansion.cactus_thorn.info.2"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.CACTUS_FLOWER.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_flower.info.1",
                        "jei.floraexpansion.cactus_flower.info.2",
                        "jei.floraexpansion.cactus_flower.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.CACTUS_HELMET.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_armor.info.1",
                        "jei.floraexpansion.cactus_armor.info.2",
                        "jei.floraexpansion.cactus_armor.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.CACTUS_CHESTPLATE.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_armor.info.1",
                        "jei.floraexpansion.cactus_armor.info.2",
                        "jei.floraexpansion.cactus_armor.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.CACTUS_LEGGINGS.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_armor.info.1",
                        "jei.floraexpansion.cactus_armor.info.2",
                        "jei.floraexpansion.cactus_armor.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.CACTUS_BOOTS.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_armor.info.1",
                        "jei.floraexpansion.cactus_armor.info.2",
                        "jei.floraexpansion.cactus_armor.info.3"
                )
        );

        registration.addIngredientInfo(
                ModBlocks.CACTUS_CLUSTER.get(),
                translatedLines(
                        "jei.floraexpansion.cactus_cluster.info.1",
                        "jei.floraexpansion.cactus_cluster.info.2",
                        "jei.floraexpansion.cactus_cluster.info.3"
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
                ModBlocks.OPUNTIA_CACTUS.get(),
                translatedLines(
                        "jei.floraexpansion.opuntia_cactus.info.1",
                        "jei.floraexpansion.opuntia_cactus.info.2",
                        "jei.floraexpansion.opuntia_cactus.info.3"
                )
        );

        registration.addIngredientInfo(
                ModItems.PRICKLY_PEAR.get(),
                translatedLines(
                        "jei.floraexpansion.prickly_pear.info.1",
                        "jei.floraexpansion.prickly_pear.info.2"
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
            FloraExpansion.LOGGER.info("JEI: hid {} cactus armor + thorns anvil recipe(s).", toHide.size());
        }
    }

    private static boolean shouldHideAnvilRecipe(Object recipe) {
        List<ItemStack> leftStacks = new ArrayList<>();
        List<ItemStack> rightStacks = new ArrayList<>();

        extractInto(leftStacks, invokeNoArg(recipe, "getLeftInputs"));
        extractInto(leftStacks, invokeNoArg(recipe, "getLeftInput"));
        extractInto(leftStacks, invokeNoArg(recipe, "getBaseInputs"));
        extractInto(leftStacks, invokeNoArg(recipe, "getBaseInput"));

        extractInto(rightStacks, invokeNoArg(recipe, "getRightInputs"));
        extractInto(rightStacks, invokeNoArg(recipe, "getRightInput"));
        extractInto(rightStacks, invokeNoArg(recipe, "getExtraInputs"));
        extractInto(rightStacks, invokeNoArg(recipe, "getAdditionInputs"));
        extractInto(rightStacks, invokeNoArg(recipe, "getAdditionInput"));

        extractInto(leftStacks, readField(recipe, "leftInputs"));
        extractInto(leftStacks, readField(recipe, "leftInput"));
        extractInto(leftStacks, readField(recipe, "baseInputs"));
        extractInto(leftStacks, readField(recipe, "baseInput"));

        extractInto(rightStacks, readField(recipe, "rightInputs"));
        extractInto(rightStacks, readField(recipe, "rightInput"));
        extractInto(rightStacks, readField(recipe, "extraInputs"));
        extractInto(rightStacks, readField(recipe, "additionInputs"));
        extractInto(rightStacks, readField(recipe, "additionInput"));

        if (leftStacks.isEmpty() || rightStacks.isEmpty()) {
            return false;
        }

        boolean leftHasCactusArmor = leftStacks.stream().anyMatch(CactusArmorItem::isCactusArmor);
        if (!leftHasCactusArmor) {
            return false;
        }

        return rightStacks.stream().anyMatch(FloraExpansionJeiPlugin::hasThorns);
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

    private static Object invokeNoArg(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            method.setAccessible(true);
            return method.invoke(target);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static Object readField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static void extractInto(List<ItemStack> out, Object value) {
        if (value == null) {
            return;
        }

        switch (value) {
            case ItemStack stack -> {
                if (!stack.isEmpty()) {
                    out.add(stack);
                }
            }
            case Optional<?> optional -> optional.ifPresent(v -> extractInto(out, v));
            case Collection<?> collection -> {
                for (Object element : collection) {
                    extractInto(out, element);
                }
            }
            default -> {
                if (value.getClass().isArray()) {
                    int len = Array.getLength(value);
                    for (int i = 0; i < len; i++) {
                        extractInto(out, Array.get(value, i));
                    }
                }
            }
        }
    }

    private static Component[] translatedLines(String... keys) {
        Component[] lines = new Component[keys.length];
        for (int i = 0; i < keys.length; i++) {
            lines[i] = Component.translatable(keys[i]);
        }
        return lines;
    }
}
