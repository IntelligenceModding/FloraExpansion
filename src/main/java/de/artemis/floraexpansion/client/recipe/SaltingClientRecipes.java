package de.artemis.floraexpansion.client.recipe;
import java.util.List;
import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import de.artemis.floraexpansion.common.registry.ModRecipes;
public final class SaltingClientRecipes {
    public static List<SaltingRecipe> get() {
        var level=net.minecraft.client.Minecraft.getInstance().level;
        return level==null ? List.of() : level.getRecipeManager().getAllRecipesFor(ModRecipes.SALTING.get());
    }
    private SaltingClientRecipes() {}
}
