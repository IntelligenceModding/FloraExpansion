package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, FloraExpansion.MODID);
    public static final DeferredHolder<SoundEvent, SoundEvent> SALT_CAVE_ADDITIONS = register("ambient.salt_caves.additions");
    public static final DeferredHolder<SoundEvent, SoundEvent> SALT_CAVE_MOOD = register("ambient.salt_caves.mood");
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_INTO_THE_CAVES = register("music_disc.into_the_caves");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, name)));
    }
    public static void register(IEventBus bus) { SOUNDS.register(bus); }
    private ModSounds() {}
}
