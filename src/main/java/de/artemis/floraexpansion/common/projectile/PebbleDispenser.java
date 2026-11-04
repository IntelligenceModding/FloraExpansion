package de.artemis.floraexpansion.common.projectile;

import org.jetbrains.annotations.NotNull;
public final class PebbleDispenser {
 public static void register(){
  net.minecraft.world.level.block.DispenserBlock.registerBehavior(de.artemis.floraexpansion.common.registry.ModItems.PEBBLES.get(),new net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior(){
   @NotNull protected net.minecraft.world.entity.projectile.Projectile getProjectile(@NotNull net.minecraft.world.level.Level level,@NotNull net.minecraft.core.Position pos,@NotNull net.minecraft.world.item.ItemStack stack){return new PebblesProjectile(level,pos.x(),pos.y(),pos.z(),stack);}
  });
 }
 private PebbleDispenser(){}
}
