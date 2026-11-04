package de.artemis.floraexpansion.mixin;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Chicken.class)
public abstract class ChickenFoodMixin {
 @Inject(method="isFood",at=@At("HEAD"),cancellable=true)
 private void floraexpansion$food(ItemStack stack,CallbackInfoReturnable<Boolean> result){
  if(stack.is(ModItems.SPRUCE_NUTS.get())||stack.is(ModItems.FLAX_SEEDS.get()))result.setReturnValue(true);
 }
 @ModifyArg(method="registerGoals",at=@At(value="INVOKE",target="Lnet/minecraft/world/entity/ai/goal/TemptGoal;<init>(Lnet/minecraft/world/entity/PathfinderMob;DLnet/minecraft/world/item/crafting/Ingredient;Z)V"),index=2)
 private Ingredient floraexpansion$temptation(Ingredient original){
  return Ingredient.of(java.util.stream.Stream.concat(java.util.Arrays.stream(original.getItems()),java.util.stream.Stream.of(new ItemStack(ModItems.SPRUCE_NUTS.get()),new ItemStack(ModItems.FLAX_SEEDS.get()))));
 }
}
