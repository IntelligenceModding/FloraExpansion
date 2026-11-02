package de.artemis.floraexpansion.client.recipe;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipePropertySet;
public final class ClientRecipeInputs {
 public static boolean test(ItemStack stack, ResourceKey<RecipePropertySet> key) {
  if(key == ModRecipes.GRINDING_INPUTS) return GrindingClientRecipes.get().stream().anyMatch(r -> r.value().input().test(stack));
  return SaltingClientRecipes.get().stream().anyMatch(r -> (key == ModRecipes.SALTING_INPUTS ? r.value().ingredient() : r.value().salt()).test(stack));
 }
 private ClientRecipeInputs() {}
}
