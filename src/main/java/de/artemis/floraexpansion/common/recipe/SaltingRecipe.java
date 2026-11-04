package de.artemis.floraexpansion.common.recipe;

import org.jetbrains.annotations.NotNull;




import de.artemis.floraexpansion.common.registry.ModRecipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record SaltingRecipe(net.minecraft.resources.ResourceLocation id, boolean showNotification, Ingredient ingredient,
                            Ingredient salt, ItemStack result, int processingTime) implements Recipe<net.minecraft.world.Container> {
    public static final int MAX_LAYERS = 4;
    public static final int DEFAULT_PROCESSING_TIME = 100;
    @Override public ItemStack result() { return result.copy(); }
    public SaltingRecipe(boolean show,Ingredient food,Ingredient salt,ItemStack result,int time){this(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("floraexpansion","generated"),show,food,salt,result,time);}
    @NotNull @Override public net.minecraft.resources.ResourceLocation getId(){return id;}
    public SaltingRecipe {
        result = result.copy();
        if (processingTime < 1 || processingTime > 72000) throw new IllegalArgumentException("Salting time must be 1..72000 ticks");
        // Item components are not bound while datapack registries are being bootstrapped.
        if (result.getCount() < 1 || result.getCount() > 99)
            throw new IllegalArgumentException("Salting output count must be 1..99");
    }
    public int layers() { return MAX_LAYERS; }
    /** Also used while loading a partial batch: every layer must belong to one possible recipe. */
    public boolean acceptsFood(java.util.List<ItemStack> food) {
        return !food.isEmpty() && food.size() <= MAX_LAYERS && food.stream().allMatch(ingredient::test);
    }
    @Override public boolean matches(@NotNull net.minecraft.world.Container container, @NotNull Level level) {
        if (!(container instanceof SaltingRecipeInput input)) return false;
        return input.food().size() == MAX_LAYERS && acceptsFood(input.food()) && salt.test(input.salt());
    }
    @NotNull @Override public ItemStack assemble(@NotNull net.minecraft.world.Container input, @NotNull net.minecraft.core.RegistryAccess registries) { return result.copy(); }
    @NotNull @Override public String getGroup() { return ""; }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @NotNull @Override public ItemStack getResultItem(@NotNull net.minecraft.core.RegistryAccess registries) { return result.copy(); }
    @NotNull @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        var inputs=net.minecraft.core.NonNullList.<Ingredient>create();
        inputs.addAll(java.util.Collections.nCopies(MAX_LAYERS, ingredient));inputs.add(salt);return inputs;
    }
    @NotNull @Override public RecipeType<SaltingRecipe> getType() { return ModRecipes.SALTING.get(); }
    @NotNull @Override public RecipeSerializer<SaltingRecipe> getSerializer() { return ModRecipes.SALTING_SERIALIZER.get(); }
}
