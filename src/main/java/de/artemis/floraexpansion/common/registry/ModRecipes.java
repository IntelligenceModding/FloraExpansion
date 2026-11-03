package de.artemis.floraexpansion.common.registry;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, FloraExpansion.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FloraExpansion.MODID);
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "grinding");
    public static final DeferredHolder<RecipeType<?>, RecipeType<GrindingRecipe>> GRINDING = TYPES.register("grinding", () -> RecipeType.simple(ID));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrindingRecipe>> GRINDING_SERIALIZER = SERIALIZERS.register("grinding",
            () -> new RecipeSerializer<GrindingRecipe>() {
                @NotNull @Override public com.mojang.serialization.MapCodec<GrindingRecipe> codec() { return GrindingRecipe.MAP_CODEC; }
                @NotNull @Override public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GrindingRecipe> streamCodec() { return GrindingRecipe.STREAM_CODEC; }
            });
    public static final ResourceLocation GRINDING_INPUTS = ID;
    public static final ResourceLocation SALTING_ID = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "salting");
    public static final DeferredHolder<RecipeType<?>, RecipeType<SaltingRecipe>> SALTING = TYPES.register("salting", () -> RecipeType.simple(SALTING_ID));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SaltingRecipe>> SALTING_SERIALIZER = SERIALIZERS.register("salting",
            () -> new RecipeSerializer<SaltingRecipe>() {
                @NotNull @Override public com.mojang.serialization.MapCodec<SaltingRecipe> codec() { return SaltingRecipe.MAP_CODEC; }
                @NotNull @Override public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, SaltingRecipe> streamCodec() { return SaltingRecipe.STREAM_CODEC; }
            });
    public static final ResourceLocation SALTING_INPUTS = SALTING_ID;
    public static final ResourceLocation SALTING_SALTS = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "salting_salts");

    public static boolean isSaltingFood(Level level, ItemStack stack) {
        return level != null && test(level, stack, SALTING_INPUTS);
    }
    public static boolean isSaltingSalt(Level level, ItemStack stack) {
        return level != null && test(level, stack, SALTING_SALTS);
    }

    public static boolean isGrindingInput(Level level, ItemStack stack) {
        return level != null && test(level, stack, GRINDING_INPUTS);
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus);
        SERIALIZERS.register(bus);


    }


    private static boolean test(Level level, ItemStack stack, ResourceLocation key) {
        if (key == GRINDING_INPUTS) return level.getRecipeManager().getAllRecipesFor(GRINDING.get()).stream().anyMatch(r -> r.value().input().test(stack));
        return level.getRecipeManager().getAllRecipesFor(SALTING.get()).stream().anyMatch(r -> (key == SALTING_INPUTS ? r.value().ingredient() : r.value().salt()).test(stack));
    }
    private ModRecipes() {}
}
