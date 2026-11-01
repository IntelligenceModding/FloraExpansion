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
import net.neoforged.neoforge.event.RegisterRecipePropertiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, FloraExpansion.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FloraExpansion.MODID);
    private static final DeferredRegister<RecipeBookCategory> BOOK_CATEGORIES = DeferredRegister.create(Registries.RECIPE_BOOK_CATEGORY, FloraExpansion.MODID);
    public static final Identifier ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "grinding");
    public static final DeferredHolder<RecipeType<?>, RecipeType<GrindingRecipe>> GRINDING = TYPES.register("grinding", () -> RecipeType.simple(ID));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<GrindingRecipe>> GRINDING_SERIALIZER = SERIALIZERS.register("grinding",
            () -> new RecipeSerializer<>(GrindingRecipe.MAP_CODEC, GrindingRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> GRINDING_BOOK_CATEGORY = BOOK_CATEGORIES.register("grinding", RecipeBookCategory::new);
    public static final ResourceKey<RecipePropertySet> GRINDING_INPUTS = ResourceKey.create(RecipePropertySet.TYPE_KEY, ID);
    public static final Identifier SALTING_ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salting");
    public static final DeferredHolder<RecipeType<?>, RecipeType<SaltingRecipe>> SALTING = TYPES.register("salting", () -> RecipeType.simple(SALTING_ID));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SaltingRecipe>> SALTING_SERIALIZER = SERIALIZERS.register("salting",
            () -> new RecipeSerializer<>(SaltingRecipe.MAP_CODEC, SaltingRecipe.STREAM_CODEC));
    public static final DeferredHolder<RecipeBookCategory, RecipeBookCategory> SALTING_BOOK_CATEGORY = BOOK_CATEGORIES.register("salting", RecipeBookCategory::new);
    public static final ResourceKey<RecipePropertySet> SALTING_INPUTS = ResourceKey.create(RecipePropertySet.TYPE_KEY, SALTING_ID);
    public static final ResourceKey<RecipePropertySet> SALTING_SALTS = ResourceKey.create(RecipePropertySet.TYPE_KEY,
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salting_salts"));

    public static boolean isSaltingFood(Level level, ItemStack stack) {
        return level != null && level.recipeAccess().propertySet(SALTING_INPUTS).test(stack);
    }
    public static boolean isSaltingSalt(Level level, ItemStack stack) {
        return level != null && level.recipeAccess().propertySet(SALTING_SALTS).test(stack);
    }

    public static boolean isGrindingInput(Level level, ItemStack stack) {
        return level != null && level.recipeAccess().propertySet(GRINDING_INPUTS).test(stack);
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus);
        SERIALIZERS.register(bus);
        BOOK_CATEGORIES.register(bus);
        bus.addListener(ModRecipes::properties);
        NeoForge.EVENT_BUS.addListener(ModRecipes::sync);
    }
    private static void properties(RegisterRecipePropertiesEvent event) {
        event.register(SALTING_INPUTS, recipe -> recipe instanceof SaltingRecipe salting
                ? java.util.Optional.of(salting.ingredient()) : java.util.Optional.empty());
        event.register(SALTING_SALTS, recipe -> recipe instanceof SaltingRecipe salting
                ? java.util.Optional.of(salting.salt()) : java.util.Optional.empty());
        event.register(GRINDING_INPUTS, recipe -> recipe instanceof GrindingRecipe grinding
                ? java.util.Optional.of(grinding.input()) : java.util.Optional.empty());
    }
    private static void sync(OnDatapackSyncEvent event) { event.sendRecipes(GRINDING.get(), SALTING.get()); }
    private ModRecipes() {}
}
