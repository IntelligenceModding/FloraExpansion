package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

public final class ModJukeboxSongs {
    public static final ResourceKey<JukeboxSong> INTO_THE_CAVES = ResourceKey.create(Registries.JUKEBOX_SONG,
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "into_the_caves"));

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        context.register(INTO_THE_CAVES, new JukeboxSong(ModSounds.MUSIC_DISC_INTO_THE_CAVES,
                Component.translatable("jukebox_song.floraexpansion.into_the_caves"), 100.310204F, 13));
    }

    private ModJukeboxSongs() {}
}
