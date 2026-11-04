package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class MusicDiscCreativeTabEvents {
    @SubscribeEvent public static void addDisc(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.getEntries().putAfter(new ItemStack(Items.MUSIC_DISC_5), new ItemStack(ModItems.MUSIC_DISC_INTO_THE_CAVES.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
    private MusicDiscCreativeTabEvents() {}
}
