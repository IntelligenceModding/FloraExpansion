package de.artemis.floraexpansion.common.recipe;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record SaltingRecipe(boolean showNotification, Ingredient ingredient,
                            Ingredient salt, ItemStack result, int processingTime) implements Recipe<SaltingRecipeInput> {
    public static final int MAX_LAYERS = 4;
    public static final int DEFAULT_PROCESSING_TIME = 100;
    public static final MapCodec<SaltingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(SaltingRecipe::showNotification),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SaltingRecipe::ingredient),
            Ingredient.CODEC.fieldOf("salt").forGetter(SaltingRecipe::salt),
            ItemStack.CODEC.fieldOf("result").forGetter(SaltingRecipe::result),
            Codec.intRange(1, 72000).optionalFieldOf("processing_time", DEFAULT_PROCESSING_TIME).forGetter(SaltingRecipe::processingTime)
    ).apply(instance, SaltingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SaltingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SaltingRecipe::showNotification,
            Ingredient.CONTENTS_STREAM_CODEC, SaltingRecipe::ingredient,
            Ingredient.CONTENTS_STREAM_CODEC, SaltingRecipe::salt,
            ItemStack.STREAM_CODEC, SaltingRecipe::result,
            ByteBufCodecs.VAR_INT, SaltingRecipe::processingTime, SaltingRecipe::new);

    @Override public ItemStack result() { return result.copy(); }
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
    @Override public boolean matches(SaltingRecipeInput input, @NotNull Level level) {
        return input.food().size() == MAX_LAYERS && acceptsFood(input.food()) && salt.test(input.salt());
    }
    @NotNull @Override public ItemStack assemble(@NotNull SaltingRecipeInput input, @NotNull net.minecraft.core.HolderLookup.Provider registries) { return result.copy(); }
    @NotNull @Override public String getGroup() { return ""; }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @NotNull @Override public ItemStack getResultItem(@NotNull net.minecraft.core.HolderLookup.Provider registries) { return result.copy(); }
    @NotNull @Override public net.minecraft.core.NonNullList<Ingredient> getIngredients() {
        var inputs=net.minecraft.core.NonNullList.<Ingredient>create();
        inputs.addAll(java.util.Collections.nCopies(MAX_LAYERS, ingredient));inputs.add(salt);return inputs;
    }
    @NotNull @Override public RecipeType<SaltingRecipe> getType() { return ModRecipes.SALTING.get(); }
    @NotNull @Override public RecipeSerializer<SaltingRecipe> getSerializer() { return ModRecipes.SALTING_SERIALIZER.get(); }
}
