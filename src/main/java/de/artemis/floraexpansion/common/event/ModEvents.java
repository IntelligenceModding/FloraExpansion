package de.artemis.floraexpansion.common.event;


import de.artemis.floraexpansion.common.block.ModBlocks;
import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        ItemStack itemStack = event.getItemStack();

        //Blocks
        if (itemStack.is(ModBlocks.LEAF_LITTER.get().asItem())) {
            event.setBurnTime(100);
        }

        if (itemStack.is(ModBlocks.PINE_LITTER.get().asItem())) {
            event.setBurnTime(100);
        }
        
        //Items
        if (itemStack.is(ModItems.PINE_CONE.get())) {
            event.setBurnTime(100);
        }

        if (itemStack.is(ModItems.TWIG.get())) {
            event.setBurnTime(200);
        }
    }
}
