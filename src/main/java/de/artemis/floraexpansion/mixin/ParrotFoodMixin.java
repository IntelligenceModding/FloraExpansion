package de.artemis.floraexpansion.mixin;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(Parrot.class)
public abstract class ParrotFoodMixin {
 @Redirect(method="mobInteract",at=@At(value="INVOKE",target="Ljava/util/Set;contains(Ljava/lang/Object;)Z",remap=false))
 private boolean floraexpansion$taming(java.util.Set<?> foods,Object item){return foods.contains(item)||item==ModItems.SPRUCE_NUTS.get()||item==ModItems.FLAX_SEEDS.get();}
 @Redirect(method="mobInteract",at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"))
 private boolean floraexpansion$poison(ItemStack stack,Item item){return stack.is(item)||item==Items.COOKIE&&stack.is(ModItems.TOASTED_SPRUCE_NUTS.get());}
}
