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

public class GrindingRecipe extends SingleItemRecipe {
    public static final int DEFAULT_TURNS = 4;
    public static final int MAX_TURNS = 32767;
    private final int turns;
    private final boolean showNotification;
    @Override public boolean showNotification() { return showNotification; }
    public static final MapCodec<GrindingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(GrindingRecipe::showNotification),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(GrindingRecipe::input),
            ItemStack.CODEC.fieldOf("result").forGetter(GrindingRecipe::result),
            Codec.intRange(1, MAX_TURNS).optionalFieldOf("turns", DEFAULT_TURNS).forGetter(GrindingRecipe::turns)
    ).apply(instance, GrindingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, GrindingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, GrindingRecipe::showNotification,
            Ingredient.CONTENTS_STREAM_CODEC, GrindingRecipe::input,
            ItemStack.STREAM_CODEC, GrindingRecipe::result,
            ByteBufCodecs.VAR_INT, GrindingRecipe::turns, GrindingRecipe::new);

    public GrindingRecipe(boolean showNotification, Ingredient ingredient, ItemStack result, int turns) {
        super(ModRecipes.GRINDING.get(), ModRecipes.GRINDING_SERIALIZER.get(), "", ingredient, result.copy());
        this.showNotification = showNotification;
        if (turns < 1 || turns > MAX_TURNS) throw new IllegalArgumentException("Grinding turns must be 1.." + MAX_TURNS);
        if (result.getCount() < 1 || result.getCount() > 99) {
            throw new IllegalArgumentException("Grinding result count must be 1..99");
        }
        this.turns = turns;
    }
    public Ingredient input() { return ingredient; }
    public ItemStack result() { return result.copy(); }
    @Override public boolean matches(SingleRecipeInput input, @NotNull net.minecraft.world.level.Level level) { return ingredient.test(input.item()); }
    public int turns() { return turns; }
    public ItemStack output() { return result.copy(); }
    @NotNull @Override public RecipeType<GrindingRecipe> getType() { return ModRecipes.GRINDING.get(); }
    @NotNull @Override public RecipeSerializer<GrindingRecipe> getSerializer() { return ModRecipes.GRINDING_SERIALIZER.get(); }
    @NotNull @Override public String getGroup() { return ""; }
}
