package de.artemis.floraexpansion.client.recipe;
import java.util.List;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.registry.ModRecipes;
public final class GrindingClientRecipes {
    public static List<GrindingRecipe> get() {
        var level=net.minecraft.client.Minecraft.getInstance().level;
        return level==null ? List.of() : level.getRecipeManager().getAllRecipesFor(ModRecipes.GRINDING.get());
    }
    private GrindingClientRecipes() {}
}
