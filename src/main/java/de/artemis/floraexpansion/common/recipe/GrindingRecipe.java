package de.artemis.floraexpansion.common.recipe;

import org.jetbrains.annotations.NotNull;




import de.artemis.floraexpansion.common.registry.ModRecipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

public class GrindingRecipe extends SingleItemRecipe {
    public static final int DEFAULT_TURNS = 4;
    public static final int MAX_TURNS = 32767;
    private final int turns;
    private final boolean showNotification;
    @Override public boolean showNotification() { return showNotification; }
    public GrindingRecipe(boolean show, Ingredient ingredient, ItemStack result, int turns) { this(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("floraexpansion", "generated"),show,ingredient,result,turns); }
    public GrindingRecipe(net.minecraft.resources.ResourceLocation id, boolean showNotification, Ingredient ingredient, ItemStack result, int turns) {
        super(ModRecipes.GRINDING.get(), ModRecipes.GRINDING_SERIALIZER.get(), id, "", ingredient, result.copy());
        this.showNotification = showNotification;
        if (turns < 1 || turns > MAX_TURNS) throw new IllegalArgumentException("Grinding turns must be 1.." + MAX_TURNS);
        if (result.getCount() < 1 || result.getCount() > 99) {
            throw new IllegalArgumentException("Grinding result count must be 1..99");
        }
        this.turns = turns;
    }
    public Ingredient input() { return ingredient; }
    public ItemStack result() { return result.copy(); }
    @Override public boolean matches(net.minecraft.world.Container input, @NotNull net.minecraft.world.level.Level level) { return ingredient.test(input.getItem(0)); }
    public int turns() { return turns; }
    public ItemStack output() { return result.copy(); }
    @NotNull @Override public RecipeType<GrindingRecipe> getType() { return ModRecipes.GRINDING.get(); }
    @NotNull @Override public RecipeSerializer<GrindingRecipe> getSerializer() { return ModRecipes.GRINDING_SERIALIZER.get(); }
    @NotNull @Override public String getGroup() { return ""; }
}
