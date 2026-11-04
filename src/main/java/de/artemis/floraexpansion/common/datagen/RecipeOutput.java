package de.artemis.floraexpansion.common.datagen;

import org.jetbrains.annotations.NotNull;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import de.artemis.floraexpansion.common.recipe.*;
import com.google.gson.*;
import java.util.function.Consumer;
/** Bridges the shared recipe builders to 1.20.1's FinishedRecipe output. */
final class RecipeOutput implements Consumer<FinishedRecipe> {
 private final Consumer<FinishedRecipe> delegate;
 RecipeOutput(Consumer<FinishedRecipe> delegate){this.delegate=delegate;}
 public void accept(FinishedRecipe recipe){delegate.accept(recipe);}
 public void accept(ResourceLocation id, Recipe<?> recipe, Object advancement){
  delegate.accept(new FinishedRecipe(){
   public void serializeRecipeData(@NotNull JsonObject json){
    net.minecraft.world.item.ItemStack result;
    if(recipe instanceof GrindingRecipe r){json.add("ingredient",r.input().toJson());json.addProperty("turns",r.turns());result=r.result();}
    else if(recipe instanceof SaltingRecipe r){json.add("ingredient",r.ingredient().toJson());json.add("salt",r.salt().toJson());json.addProperty("processing_time",r.processingTime());result=r.result();}
    else throw new IllegalArgumentException("Unsupported custom recipe "+recipe);
    JsonObject output=new JsonObject();output.addProperty("item",net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(result.getItem()).toString());output.addProperty("count",result.getCount());json.add("result",output);
   }
   @NotNull public ResourceLocation getId(){return id;}
   @NotNull public RecipeSerializer<?> getType(){return recipe.getSerializer();}
   public JsonObject serializeAdvancement(){return null;}
   public ResourceLocation getAdvancementId(){return ResourceLocation.fromNamespaceAndPath(id.getNamespace(),"recipes/"+id.getPath());}
  });
 }
}
