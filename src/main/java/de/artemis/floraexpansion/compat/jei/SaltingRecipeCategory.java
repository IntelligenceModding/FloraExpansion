package de.artemis.floraexpansion.compat.jei;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** Each food slot is one layer; the separate salt slot is the single covering layer. */
public class SaltingRecipeCategory implements IRecipeCategory<RecipeHolder<SaltingRecipe>> {
    public static final IRecipeHolderType<SaltingRecipe> TYPE = IRecipeHolderType.create(
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salting"));
    private final IDrawable icon;
    public SaltingRecipeCategory(IGuiHelper gui) { icon = gui.createDrawableItemStack(new ItemStack(ModBlocks.SALTING_TUB.get())); }
    @Override public IRecipeHolderType<SaltingRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("recipe.floraexpansion.salting"); }
    @Override public int getWidth() { return 176; }
    @Override public int getHeight() { return 160; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SaltingRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        for (int layer = 0; layer < recipe.layers(); layer++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 6 + layer * 19, 16)
                    .add(recipe.ingredient()).setStandardSlotBackground();
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 94, 16).add(recipe.salt()).setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 151, 16).add(recipe.result().create()).setStandardSlotBackground();
    }
    @Override public void draw(RecipeHolder<SaltingRecipe> holder, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        var recipe = holder.value();
        float time = (net.minecraft.util.Util.getMillis() % 12000L) / 1000F;
        var food = slots.getSlotViews().stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.INPUT)
                .findFirst().flatMap(slot -> slot.getDisplayedItemStack()).orElse(ItemStack.EMPTY);
        var salt = slots.getSlotViews().stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.INPUT).skip(recipe.layers())
                .findFirst().flatMap(slot -> slot.getDisplayedItemStack()).orElse(ItemStack.EMPTY);
        graphics.submitPictureInPictureRenderState(new de.artemis.floraexpansion.client.renderer.SaltingRecipeRenderer.State(
                food.copy(), salt.copy(), recipe.result().create(), time,
                8, 36, 168, 156, 61, new org.joml.Matrix3x2f(graphics.pose()), graphics.peekScissorStack()));
        graphics.fill(119, 23, 137, 26, 0xFF8B8B8B);
        for (int i = 0; i < 5; i++) graphics.fill(137+i, 19+i, 138+i, 30-i, 0xFF8B8B8B);
    }
}



