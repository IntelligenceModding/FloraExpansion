package de.artemis.floraexpansion.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import java.util.List;

/** One item per food layer, followed by the salt used to cover the batch. */
public record SaltingRecipeInput(List<ItemStack> food, ItemStack salt) implements RecipeInput {
    public SaltingRecipeInput { food = List.copyOf(food); }
    @Override public ItemStack getItem(int index) { return index == food.size() ? salt : food.get(index); }
    @Override public int size() { return food.size() + 1; }
}
