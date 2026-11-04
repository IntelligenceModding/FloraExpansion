package de.artemis.floraexpansion.common.recipe;
import net.minecraft.world.item.ItemStack;
import java.util.List;
public class SaltingRecipeInput extends net.minecraft.world.SimpleContainer {
 private final List<ItemStack> food; private final ItemStack salt;
 public SaltingRecipeInput(List<ItemStack> food,ItemStack salt){super(food.size()+1);this.food=List.copyOf(food);this.salt=salt;for(int i=0;i<food.size();i++)setItem(i,food.get(i));setItem(food.size(),salt);}
 public List<ItemStack> food(){return food;} public ItemStack salt(){return salt;}
}
