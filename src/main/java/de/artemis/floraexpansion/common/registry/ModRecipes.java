package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, FloraExpansion.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FloraExpansion.MODID);
    private static final DeferredRegister<RecipeBookCategory> BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, FloraExpansion.MODID);
    public static final Identifier ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "grinding");
    public static final DeferredHolder<RecipeType<?>, RecipeType<GrindingRecipe>> GRINDING = TYPES.register("grinding", () -> RecipeType.simple(ID));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrindingRecipe>> GRINDING_SERIALIZER = SERIALIZERS.register("grinding",
            () -> new RecipeSerializer<GrindingRecipe>() {
                @Override public com.mojang.serialization.MapCodec<GrindingRecipe> codec() { return GrindingRecipe.MAP_CODEC; }
                @Override public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, GrindingRecipe> streamCodec() { return GrindingRecipe.STREAM_CODEC; }
            });
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> GRINDING_BOOK_CATEGORY = BOOK_CATEGORIES.register("grinding", RecipeBookCategory::new);
    public static final ResourceKey<RecipePropertySet> GRINDING_INPUTS = ResourceKey.create(RecipePropertySet.TYPE_KEY, ID);
    public static final Identifier SALTING_ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salting");
    public static final DeferredHolder<RecipeType<?>, RecipeType<SaltingRecipe>> SALTING = TYPES.register("salting", () -> RecipeType.simple(SALTING_ID));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SaltingRecipe>> SALTING_SERIALIZER = SERIALIZERS.register("salting",
            () -> new RecipeSerializer<SaltingRecipe>() {
                @Override public com.mojang.serialization.MapCodec<SaltingRecipe> codec() { return SaltingRecipe.MAP_CODEC; }
                @Override public net.minecraft.network.codec.StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, SaltingRecipe> streamCodec() { return SaltingRecipe.STREAM_CODEC; }
            });
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> SALTING_BOOK_CATEGORY = BOOK_CATEGORIES.register("salting", RecipeBookCategory::new);
    public static final ResourceKey<RecipePropertySet> SALTING_INPUTS = ResourceKey.create(RecipePropertySet.TYPE_KEY, SALTING_ID);
    public static final ResourceKey<RecipePropertySet> SALTING_SALTS = ResourceKey.create(RecipePropertySet.TYPE_KEY,
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salting_salts"));

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
        BOOK_CATEGORIES.register(bus);

        NeoForge.EVENT_BUS.addListener(ModRecipes::sync);
    }

    private static void sync(OnDatapackSyncEvent event) { event.sendRecipes(GRINDING.get(), SALTING.get()); }
    private static boolean test(Level level, ItemStack stack, ResourceKey<RecipePropertySet> key) {
        if (level instanceof net.minecraft.server.level.ServerLevel server) {
            if (key == GRINDING_INPUTS) return server.recipeAccess().recipeMap().byType(GRINDING.get()).stream().anyMatch(r -> r.value().input().test(stack));
            return server.recipeAccess().recipeMap().byType(SALTING.get()).stream().anyMatch(r -> (key == SALTING_INPUTS ? r.value().ingredient() : r.value().salt()).test(stack));
        }
        return de.artemis.floraexpansion.client.recipe.ClientRecipeInputs.test(stack, key);
    }
    private ModRecipes() {}
}
