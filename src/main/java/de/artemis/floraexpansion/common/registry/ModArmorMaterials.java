package de.artemis.floraexpansion.common.registry;

import org.jetbrains.annotations.NotNull;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.sounds.*;
public final class ModArmorMaterials {
 public static final ArmorMaterial SPIKED_LEATHER_ARMOR_MATERIAL=new ArmorMaterial(){
  public int getDurabilityForType(ArmorItem.Type type){return switch(type){case BOOTS->13*7;case LEGGINGS->15*7;case CHESTPLATE->16*7;case HELMET->11*7;};}
  public int getDefenseForType(ArmorItem.Type type){return switch(type){case BOOTS,HELMET->1;case LEGGINGS->3;case CHESTPLATE->4;};}
  public int getEnchantmentValue(){return 18;}
  @NotNull public SoundEvent getEquipSound(){return SoundEvents.ARMOR_EQUIP_LEATHER;}
  @NotNull public Ingredient getRepairIngredient(){return Ingredient.of(ModTags.Items.SPIKED_LEATHER_ARMOR_REPAIRABLE);}
  @NotNull public String getName(){return "floraexpansion:spiked_leather";}
  public float getToughness(){return 0;}
  public float getKnockbackResistance(){return 0;}
 };
 public static void register(net.minecraftforge.eventbus.api.IEventBus bus){}
 private ModArmorMaterials(){}
}
