package de.artemis.floraexpansion.common.util;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.*;
import net.minecraft.world.item.ItemStack;
import java.util.Optional;
public final class SavedComponents {
 public static final Codec<ItemStack> STACK_CODEC=CompoundTag.CODEC.xmap(ItemStack::of,s->s.save(new CompoundTag()));
 public static <T> void store(CompoundTag tag,String key,Codec<T> codec,T value){codec.encodeStart(NbtOps.INSTANCE,value).result().ifPresent(nbt->tag.put(key,nbt));}
 public static <T> Optional<T> read(CompoundTag tag,String key,Codec<T> codec){return tag.contains(key)?codec.parse(NbtOps.INSTANCE,tag.get(key)).result():Optional.empty();}
 private SavedComponents(){}
}
