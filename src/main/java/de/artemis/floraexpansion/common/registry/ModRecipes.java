package de.artemis.floraexpansion.common.registry;

import org.jetbrains.annotations.NotNull;



import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public final class ModRecipes {
    private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, FloraExpansion.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FloraExpansion.MODID);
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "grinding");
    public static final RegistryObject<RecipeType<GrindingRecipe>> GRINDING = TYPES.register("grinding", () -> RecipeType.simple(ID));
    public static final RegistryObject<RecipeSerializer<GrindingRecipe>> GRINDING_SERIALIZER = SERIALIZERS.register("grinding",
            () -> new RecipeSerializer<GrindingRecipe>() {
                @NotNull @Override public GrindingRecipe fromJson(@NotNull ResourceLocation id, @NotNull com.google.gson.JsonObject json) { return new GrindingRecipe(id, net.minecraft.util.GsonHelper.getAsBoolean(json,"show_notification",true), Ingredient.fromJson(json.get("ingredient")), net.minecraft.world.item.crafting.ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result")), net.minecraft.util.GsonHelper.getAsInt(json,"turns",4)); }
 @Override public GrindingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull net.minecraft.network.FriendlyByteBuf b){return new GrindingRecipe(id,b.readBoolean(),Ingredient.fromNetwork(b),b.readItem(),b.readVarInt());}
 @Override public void toNetwork(@NotNull net.minecraft.network.FriendlyByteBuf b, @NotNull GrindingRecipe r){b.writeBoolean(r.showNotification());r.input().toNetwork(b);b.writeItem(r.result());b.writeVarInt(r.turns());}
            });
    public static final ResourceLocation GRINDING_INPUTS = ID;
    public static final ResourceLocation SALTING_ID = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "salting");
    public static final RegistryObject<RecipeType<SaltingRecipe>> SALTING = TYPES.register("salting", () -> RecipeType.simple(SALTING_ID));
    public static final RegistryObject<RecipeSerializer<SaltingRecipe>> SALTING_SERIALIZER = SERIALIZERS.register("salting",
            () -> new RecipeSerializer<SaltingRecipe>() {
                @NotNull @Override public SaltingRecipe fromJson(@NotNull ResourceLocation id, @NotNull com.google.gson.JsonObject json) { return new SaltingRecipe(id, net.minecraft.util.GsonHelper.getAsBoolean(json,"show_notification",true), Ingredient.fromJson(json.get("ingredient")), Ingredient.fromJson(json.get("salt")), net.minecraft.world.item.crafting.ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result")), net.minecraft.util.GsonHelper.getAsInt(json,"processing_time",100)); }
 @Override public SaltingRecipe fromNetwork(@NotNull ResourceLocation id, @NotNull net.minecraft.network.FriendlyByteBuf b){return new SaltingRecipe(id,b.readBoolean(),Ingredient.fromNetwork(b),Ingredient.fromNetwork(b),b.readItem(),b.readVarInt());}
 @Override public void toNetwork(@NotNull net.minecraft.network.FriendlyByteBuf b, @NotNull SaltingRecipe r){b.writeBoolean(r.showNotification());r.ingredient().toNetwork(b);r.salt().toNetwork(b);b.writeItem(r.result());b.writeVarInt(r.processingTime());}
            });
    public static final ResourceLocation SALTING_INPUTS = SALTING_ID;
    public static final ResourceLocation SALTING_SALTS = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "salting_salts");

    public static boolean isSaltingFood(Level level, ItemStack stack) {
        return level != null && test(level, stack, SALTING_INPUTS);
    }
    public static boolean isSaltingSalt(Level level, ItemStack stack) {
        return level != null && test(level, stack, SALTING_SALTS);
    }

    public static boolean isGrindingInput(Level level, ItemStack stack) {
        return level != null && test(level, stack, GRINDING_INPUTS);
    }
    public static void register(IEventBus bus) {
        TYPES.register(bus);
        SERIALIZERS.register(bus);


    }


    private static boolean test(Level level, ItemStack stack, ResourceLocation key) {
        if (key == GRINDING_INPUTS) return level.getRecipeManager().getAllRecipesFor(GRINDING.get()).stream().anyMatch(r -> r.input().test(stack));
        return level.getRecipeManager().getAllRecipesFor(SALTING.get()).stream().anyMatch(r -> (key == SALTING_INPUTS ? r.ingredient() : r.salt()).test(stack));
    }
    private ModRecipes() {}
}
