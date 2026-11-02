package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModJukeboxSongs;
import de.artemis.floraexpansion.common.registry.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.function.Consumer;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class IntoTheCavesDiscTest {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "into_the_caves_disc");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION = ResourceKey.create(Registries.TEST_FUNCTION, ID);
    @SubscribeEvent public static void function(RegisterEvent event) { event.register(Registries.TEST_FUNCTION, ID, () -> IntoTheCavesDiscTest::run); }
    @SubscribeEvent public static void tests(RegisterGameTestsEvent event) {
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;
        event.registerTest(ID, new FunctionGameTestInstance(FUNCTION, new TestData<>(event.registerEnvironment(ID), Identifier.withDefaultNamespace("empty"), 100, 0, true)));
    }

    private static void run(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(3, 4, 3));
        level.setBlockAndUpdate(pos, Blocks.JUKEBOX.defaultBlockState());
        var box = (JukeboxBlockEntity) level.getBlockEntity(pos);
        var disc = new ItemStack(ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
        check(disc.getMaxStackSize() == 1 && disc.get(DataComponents.RARITY) == Rarity.UNCOMMON, "Disc differs from vanilla rarity/stack rules");
        check(disc.is(ItemTags.CREEPER_DROP_MUSIC_DISCS) && disc.is(Tags.Items.MUSIC_DISCS), "Disc is absent from standard disc tags");
        var holder = JukeboxSong.fromStack(level.registryAccess(), disc).orElseThrow();
        check(holder.is(ModJukeboxSongs.INTO_THE_CAVES), "Disc points to wrong jukebox song");
        var song = holder.value();
        check(song.soundEvent().value() == ModSounds.MUSIC_DISC_INTO_THE_CAVES.get(), "Song points to wrong sound");
        check(Math.abs(song.lengthInSeconds() - 100.310204) < .001 && song.comparatorOutput() == 13, "Wrong song duration/comparator level");
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, disc);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        level.getBlockState(pos).useItemOn(disc, level, player, InteractionHand.MAIN_HAND, hit);
        check(disc.isEmpty() && box.getTheItem().is(ModItems.MUSIC_DISC_INTO_THE_CAVES.get()), "Right-click did not consume/insert the disc");
        check(box.getSongPlayer().isPlaying() && level.getBlockState(pos).getValue(JukeboxBlock.HAS_RECORD), "Vanilla playback did not start");
        check(box.getComparatorOutput() == 13, "Jukebox comparator output wrong");
        var second = new ItemStack(ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
        check(!box.canPlaceItem(0, second), "Occupied jukebox accepts another disc");
        for (int tick = 0; tick < 40; tick++) JukeboxBlockEntity.tick(level, pos, level.getBlockState(pos), box);
        var saved = box.saveWithFullMetadata(level.registryAccess());
        var restored = (JukeboxBlockEntity) BlockEntity.loadStatic(pos, box.getBlockState(), saved, level.registryAccess());
        restored.setLevel(level);
        check(restored.getTheItem().is(ModItems.MUSIC_DISC_INTO_THE_CAVES.get()) && restored.getSongPlayer().isPlaying()
                && restored.getSongPlayer().getTicksSinceSongStarted() == 40 && restored.getComparatorOutput() == 13, "Save/reload lost song progress or disc");
        for (int tick = 0; tick < song.lengthInTicks() + 21; tick++) JukeboxBlockEntity.tick(level, pos, level.getBlockState(pos), box);
        check(!box.getSongPlayer().isPlaying() && !box.getTheItem().isEmpty() && box.getComparatorOutput() == 13,
                "Playback completion differs from vanilla");
        level.getBlockState(pos).useWithoutItem(level, player, hit);
        check(box.getTheItem().isEmpty() && !level.getBlockState(pos).getValue(JukeboxBlock.HAS_RECORD)
                && box.getComparatorOutput() == 0, "Right-click did not eject/stop the disc");
        var remainder = HopperBlockEntity.addItem(null, box, new ItemStack(ModItems.MUSIC_DISC_INTO_THE_CAVES.get()), Direction.DOWN);
        check(remainder.isEmpty() && box.getSongPlayer().isPlaying(), "Hopper insertion does not start playback");
        box.getSongPlayer().stop(level, level.getBlockState(pos));
        level.setBlockAndUpdate(pos.below(), Blocks.HOPPER.defaultBlockState().setValue(HopperBlock.ENABLED, false));
        var hopper = (HopperBlockEntity) level.getBlockEntity(pos.below());
        check(HopperBlockEntity.suckInItems(level, hopper) && hopper.getItem(0).is(ModItems.MUSIC_DISC_INTO_THE_CAVES.get())
                && box.getTheItem().isEmpty(), "Hopper extraction lost or failed to extract the disc");
        FloraExpansion.LOGGER.info("Into the Caves disc passed: native item components, disc/creeper tags, jukebox insertion, playback, completion, save/reload, comparator, ejection and hopper automation");
        helper.succeed();
    }
    private static void check(boolean pass, String message) { if (!pass) throw new IllegalStateException(message); }
}
