package de.artemis.floraexpansion.client.recipe;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import java.util.List;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class GrindingClientRecipes {
    private static List<RecipeHolder<GrindingRecipe>> recipes = List.of();
    public static List<RecipeHolder<GrindingRecipe>> get() { return recipes; }
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void received(RecipesReceivedEvent event) {
        if (event.getRecipeTypes().contains(ModRecipes.GRINDING.get())) {
            recipes = List.copyOf(event.getRecipeMap().byType(ModRecipes.GRINDING.get()));
        }
    }
    @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event) { recipes = List.of(); }
    private GrindingClientRecipes() {}
}