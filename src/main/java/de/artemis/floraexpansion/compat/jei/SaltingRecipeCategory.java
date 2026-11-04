package de.artemis.floraexpansion.compat.jei;

import org.jetbrains.annotations.NotNull;


import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeType;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import net.minecraft.world.item.ItemStack;
/** Each food slot is one layer; the separate salt slot is the single covering layer. */
public class SaltingRecipeCategory implements IRecipeCategory<SaltingRecipe> {
    public static final RecipeType<SaltingRecipe> TYPE = new RecipeType<>(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("floraexpansion", "salting"), SaltingRecipe.class);
    private final IDrawable icon;
    public SaltingRecipeCategory(IGuiHelper gui) { icon = gui.createDrawableItemStack(new ItemStack(ModBlocks.SALTING_TUB.get())); }
    @NotNull @Override public RecipeType<SaltingRecipe> getRecipeType() { return TYPE; }
    @NotNull @Override public Component getTitle() { return Component.translatable("recipe.floraexpansion.salting"); }
    @Override public int getWidth() { return 176; }
    @Override public int getHeight() { return 160; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull SaltingRecipe holder, @NotNull IFocusGroup focuses) {
        var recipe = holder;
        for (int layer = 0; layer < recipe.layers(); layer++) {
            builder.addSlot(RecipeIngredientRole.INPUT, 6 + layer * 19, 16)
                    .addIngredients(recipe.ingredient()).setStandardSlotBackground();
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 94, 16).addIngredients(recipe.salt()).setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 151, 16).addItemStack(recipe.result().copy()).setStandardSlotBackground();
    }
    @Override public void draw(@NotNull SaltingRecipe holder, IRecipeSlotsView slots, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        var recipe = holder;
        float time = (net.minecraft.Util.getMillis() % 12000L) / 1000F;
        var food = slots.getSlotViews().stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.INPUT)
                .findFirst().flatMap(slot -> slot.getDisplayedItemStack()).orElse(ItemStack.EMPTY);
        var salt = slots.getSlotViews().stream()
                .filter(slot -> slot.getRole() == RecipeIngredientRole.INPUT).skip(recipe.layers())
                .findFirst().flatMap(slot -> slot.getDisplayedItemStack()).orElse(ItemStack.EMPTY);
        de.artemis.floraexpansion.client.renderer.SaltingRecipeRenderer.draw(graphics,
                new de.artemis.floraexpansion.client.renderer.SaltingRecipeRenderer.State(food.copy(), salt.copy(), recipe.result().copy(), time));
        graphics.fill(119, 23, 137, 26, 0xFF8B8B8B);
        for (int i = 0; i < 5; i++) graphics.fill(137+i, 19+i, 138+i, 30-i, 0xFF8B8B8B);
    }
}
