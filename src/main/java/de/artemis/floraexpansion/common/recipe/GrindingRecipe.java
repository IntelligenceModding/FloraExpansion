package de.artemis.floraexpansion.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;

public class GrindingRecipe extends SingleItemRecipe {
    public static final int DEFAULT_TURNS = 4;
    public static final int MAX_TURNS = 32767;
    private final int turns;
    public static final MapCodec<GrindingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(recipe -> recipe.commonInfo),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(GrindingRecipe::input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(GrindingRecipe::result),
            Codec.intRange(1, MAX_TURNS).optionalFieldOf("turns", DEFAULT_TURNS).forGetter(GrindingRecipe::turns)
    ).apply(instance, GrindingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GrindingRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, recipe -> recipe.commonInfo,
            Ingredient.CONTENTS_STREAM_CODEC, GrindingRecipe::input,
            ItemStackTemplate.STREAM_CODEC, GrindingRecipe::result,
            ByteBufCodecs.VAR_INT, GrindingRecipe::turns, GrindingRecipe::new);

    public GrindingRecipe(Recipe.CommonInfo info, Ingredient ingredient, ItemStackTemplate result, int turns) {
        super(info, ingredient, result);
        if (turns < 1 || turns > MAX_TURNS) throw new IllegalArgumentException("Grinding turns must be 1.." + MAX_TURNS);
        if (result.count() < 1 || result.count() > 99) {
            throw new IllegalArgumentException("Grinding result count must be 1..99");
        }
        this.turns = turns;
    }
    public int turns() { return turns; }
    public ItemStackTemplate output() { return result(); }
    @Override public RecipeType<GrindingRecipe> getType() { return ModRecipes.GRINDING.get(); }
    @Override public RecipeSerializer<GrindingRecipe> getSerializer() { return ModRecipes.GRINDING_SERIALIZER.get(); }
    @Override public RecipeBookCategory recipeBookCategory() { return ModRecipes.GRINDING_BOOK_CATEGORY.get(); }
    @Override public String group() { return ""; }
}
