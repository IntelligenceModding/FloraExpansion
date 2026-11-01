package de.artemis.floraexpansion.compat.jei;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class GrindingRecipeCategory implements IRecipeCategory<RecipeHolder<GrindingRecipe>> {
    public static final IRecipeHolderType<GrindingRecipe> TYPE = IRecipeHolderType.create(
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "grinding"));
    private final IDrawable icon;
    public GrindingRecipeCategory(IGuiHelper gui) { icon = gui.createDrawableItemStack(new ItemStack(ModBlocks.HAND_MILL.get())); }
    @Override public IRecipeHolderType<GrindingRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("recipe.floraexpansion.grinding"); }
    @Override public int getWidth() { return 132; }
    @Override public int getHeight() { return 46; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<GrindingRecipe> holder, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 4).add(holder.value().input()).setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 4).add(holder.value().output().create()).setStandardSlotBackground();
    }
    @Override public void draw(RecipeHolder<GrindingRecipe> holder, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        graphics.fill(52, 10, 74, 14, 0xFF8B8B8B);
        for (int i = 0; i < 7; i++) graphics.fill(74 + i, 5 + i, 75 + i, 19 - i, 0xFF8B8B8B);
        graphics.text(Minecraft.getInstance().font, Component.translatable("jei.floraexpansion.grinding.turns", holder.value().turns()), 8, 31, 0xFF444444, false);
    }
}