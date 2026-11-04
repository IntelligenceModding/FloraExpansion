package de.artemis.floraexpansion.common.util;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.level.block.state.properties.Property;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
/** NBT storage used by the 1.20.1 item implementations. */
public final class ItemData {
 public record Key<T>(String name) {}
 public static final class DataComponents {
  public static final Key<ItemContainerContents> CONTAINER=new Key<>("Items");
  public static final Key<CustomData> CUSTOM_DATA=new Key<>("custom");
  public static final Key<Component> CUSTOM_NAME=new Key<>("Name");
  public static final Key<CustomModelData> CUSTOM_MODEL_DATA=new Key<>("CustomModelData");
  public static final Key<BlockItemStateProperties> BLOCK_STATE=new Key<>("BlockStateTag");
 }
 @SuppressWarnings("unchecked") public static <T> T get(ItemStack stack, Key<T> key) {
  CompoundTag tag=stack.getTag(); if(tag==null) return null;
  Object value=null;
  if(key==DataComponents.CUSTOM_NAME) value=stack.hasCustomHoverName()?stack.getHoverName():null;
  else if(key==DataComponents.CUSTOM_DATA) value=new CustomData(tag.copy());
  else if(key==DataComponents.CUSTOM_MODEL_DATA && tag.contains(key.name())) value=new CustomModelData(tag.getInt(key.name()));
  else if(key==DataComponents.BLOCK_STATE && tag.contains(key.name())) value=new BlockItemStateProperties(tag.getCompound(key.name()).copy());
  else if(key==DataComponents.CONTAINER) {
   CompoundTag items=tag.contains("BlockEntityTag")?tag.getCompound("BlockEntityTag"):tag;
   if(items.contains("Items")) {var list=NonNullList.withSize(tag.contains("BlockEntityTag") ? de.artemis.floraexpansion.common.inventory.CrateMenu.CONTAINER_SLOT_COUNT : de.artemis.floraexpansion.common.item.BasketItem.SLOT_COUNT,ItemStack.EMPTY);ContainerHelper.loadAllItems(items,list);value=new ItemContainerContents(list);}
  }
  return (T)value;
 }
 public static <T> T getOrDefault(ItemStack s,Key<T> k,T fallback){T value=get(s,k);return value==null?fallback:value;}
 public static boolean has(ItemStack s,Key<?> k){return get(s,k)!=null;}
 public static void remove(ItemStack s,Key<?> k){CompoundTag tag=s.getTag();if(tag!=null)tag.remove(k.name());}
 public static <T> void set(ItemStack s,Key<T> k,T value){
  if(k==DataComponents.CUSTOM_NAME)s.setHoverName((Component)value);
  else if(value instanceof ItemContainerContents contents){var list=NonNullList.withSize(contents.items.size(),ItemStack.EMPTY);contents.copyInto(list);ContainerHelper.saveAllItems(s.getOrCreateTag(),list);}
  else if(value instanceof CustomModelData model)s.getOrCreateTag().putInt(k.name(),model.value());
  else if(value instanceof BlockItemStateProperties state)s.getOrCreateTag().put(k.name(),state.tag.copy());
 }
 public record CustomModelData(int value){}
 public record CustomData(CompoundTag tag){
  public static final CustomData EMPTY=new CustomData(new CompoundTag());
  public CompoundTag copyTag(){return tag.copy();}
  public static void update(Key<CustomData> key,ItemStack stack,Consumer<CompoundTag> action){action.accept(stack.getOrCreateTag());}
 }
 public record BlockItemStateProperties(CompoundTag tag){
  public static final BlockItemStateProperties EMPTY=new BlockItemStateProperties(new CompoundTag());
  public <T extends Comparable<T>> T get(Property<T> property){return property.getValue(tag.getString(property.getName())).orElse(null);}
  public <T extends Comparable<T>> BlockItemStateProperties with(Property<T> property,T value){var copy=tag.copy();copy.putString(property.getName(),property.getName(value));return new BlockItemStateProperties(copy);}
 }
 public static final class ItemContainerContents {
  public static final ItemContainerContents EMPTY=new ItemContainerContents(List.of());
  private final List<ItemStack> items;
  public ItemContainerContents(List<ItemStack> items){this.items=items.stream().map(ItemStack::copy).toList();}
  public static ItemContainerContents fromItems(List<ItemStack> items){return new ItemContainerContents(items);}
  public void copyInto(NonNullList<ItemStack> target){for(int i=0;i<target.size();i++)target.set(i,i<items.size()?items.get(i).copy():ItemStack.EMPTY);}
  public Stream<ItemStack> stream(){return items.stream().map(ItemStack::copy);}
  public Stream<ItemStack> nonEmptyStream(){return stream().filter(s->!s.isEmpty());}
 }
 public static void consume(ItemStack stack,int count,net.minecraft.world.entity.LivingEntity entity){if(!(entity instanceof net.minecraft.world.entity.player.Player player) || !player.getAbilities().instabuild)stack.shrink(count);}
 public static <T extends net.minecraft.world.entity.LivingEntity> void damage(ItemStack stack,int count,T entity,net.minecraft.world.entity.EquipmentSlot slot){stack.hurtAndBreak(count,entity,e->e.broadcastBreakEvent(slot));}
 private ItemData(){}
}
