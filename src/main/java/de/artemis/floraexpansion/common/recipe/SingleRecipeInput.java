package de.artemis.floraexpansion.common.recipe;
public class SingleRecipeInput extends net.minecraft.world.SimpleContainer {
 public SingleRecipeInput(net.minecraft.world.item.ItemStack stack){super(stack);}
 public net.minecraft.world.item.ItemStack item(){return getItem(0);}
}
