package de.artemis.floraexpansion.compat.jei;

import org.jetbrains.annotations.NotNull;


import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import net.minecraft.world.item.ItemStack;
public class GrindingRecipeCategory implements IRecipeCategory<GrindingRecipe> {
    public static final RecipeType<GrindingRecipe> TYPE = new RecipeType<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("floraexpansion", "grinding"), GrindingRecipe.class);
    private final IDrawable icon;
    public GrindingRecipeCategory(IGuiHelper gui) { icon = gui.createDrawableItemStack(new ItemStack(ModBlocks.HAND_MILL.get())); }
    @NotNull @Override public RecipeType<GrindingRecipe> getRecipeType() { return TYPE; }
    @NotNull @Override public Component getTitle() { return Component.translatable("recipe.floraexpansion.grinding"); }
    @Override public int getWidth() { return 132; }
    @Override public int getHeight() { return 46; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, GrindingRecipe holder, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 16, 4).addIngredients(holder.input()).setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 4).addItemStack(holder.output().copy()).setStandardSlotBackground();
    }
    @Override public void draw(@NotNull GrindingRecipe holder, @NotNull IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.fill(52, 10, 74, 14, 0xFF8B8B8B);
        for (int i = 0; i < 7; i++) graphics.fill(74 + i, 5 + i, 75 + i, 19 - i, 0xFF8B8B8B);
        graphics.drawString(Minecraft.getInstance().font, Component.translatable("jei.floraexpansion.grinding.turns", holder.turns()), 8, 31, 0xFF444444, false);
    }
}
