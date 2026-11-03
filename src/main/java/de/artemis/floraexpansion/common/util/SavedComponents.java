package de.artemis.floraexpansion.common.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.RegistryOps;
import java.util.Optional;

/** Registry-aware component serialization on Minecraft's CompoundTag save API. */
public final class SavedComponents {
    public static <T> Optional<T> read(CompoundTag tag, String key, Codec<T> codec, HolderLookup.Provider registries) {
        var value=tag.get(key);
        return value==null ? Optional.empty() : codec.parse(RegistryOps.create(NbtOps.INSTANCE, registries), value).resultOrPartial(de.artemis.floraexpansion.FloraExpansion.LOGGER::error);
    }
    public static <T> void store(CompoundTag tag, String key, Codec<T> codec, T value, HolderLookup.Provider registries) {
        tag.put(key, codec.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registries), value).getOrThrow());
    }
    private SavedComponents() {}
}
