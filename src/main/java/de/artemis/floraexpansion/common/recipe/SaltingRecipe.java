package de.artemis.floraexpansion.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record SaltingRecipe(Recipe.CommonInfo commonInfo, Ingredient ingredient,
                            Ingredient salt, ItemStackTemplate result, int processingTime) implements Recipe<SaltingRecipeInput> {
    public static final int MAX_LAYERS = 4;
    public static final int DEFAULT_PROCESSING_TIME = 100;
    public static final MapCodec<SaltingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(SaltingRecipe::commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(SaltingRecipe::ingredient),
            Ingredient.CODEC.fieldOf("salt").forGetter(SaltingRecipe::salt),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(SaltingRecipe::result),
            Codec.intRange(1, 72000).optionalFieldOf("processing_time", DEFAULT_PROCESSING_TIME).forGetter(SaltingRecipe::processingTime)
    ).apply(instance, SaltingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SaltingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, SaltingRecipe::commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC, SaltingRecipe::ingredient,
            Ingredient.CONTENTS_STREAM_CODEC, SaltingRecipe::salt,
            ItemStackTemplate.STREAM_CODEC, SaltingRecipe::result,
            ByteBufCodecs.VAR_INT, SaltingRecipe::processingTime, SaltingRecipe::new);

    public SaltingRecipe {
        if (processingTime < 1 || processingTime > 72000) throw new IllegalArgumentException("Salting time must be 1..72000 ticks");
        // Item components are not bound while datapack registries are being bootstrapped.
        if (result.count() < 1 || result.count() > 99)
            throw new IllegalArgumentException("Salting output count must be 1..99");
    }
    public int layers() { return MAX_LAYERS; }
    /** Also used while loading a partial batch: every layer must belong to one possible recipe. */
    public boolean acceptsFood(java.util.List<ItemStack> food) {
        return !food.isEmpty() && food.size() <= MAX_LAYERS && food.stream().allMatch(ingredient::test);
    }
    @Override public boolean matches(SaltingRecipeInput input, Level level) {
        return input.food().size() == MAX_LAYERS && acceptsFood(input.food()) && salt.test(input.salt());
    }
    @Override public ItemStack assemble(SaltingRecipeInput input) { return result.create(); }
    @Override public boolean showNotification() { return commonInfo.showNotification(); }
    @Override public String group() { return ""; }
    @Override public PlacementInfo placementInfo() {
        var inputs = new java.util.ArrayList<>(java.util.Collections.nCopies(MAX_LAYERS, ingredient));
        inputs.add(salt);
        return PlacementInfo.create(inputs);
    }
    @Override public RecipeType<SaltingRecipe> getType() { return ModRecipes.SALTING.get(); }
    @Override public RecipeSerializer<SaltingRecipe> getSerializer() { return ModRecipes.SALTING_SERIALIZER.get(); }
    @Override public RecipeBookCategory recipeBookCategory() { return ModRecipes.SALTING_BOOK_CATEGORY.get(); }
}
