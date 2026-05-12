package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.item.BasketHarvestHelper;
import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class BasketInteractionEvents {
    private BasketInteractionEvents() {
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (!stack.is(ModItems.BASKET.get()) || player.level().isClientSide) {
            return;
        }

        if (player.isShiftKeyDown()) {
            BasketItem.openBasket(player, event.getHand());
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
            return;
        }

        UseOnContext context = new UseOnContext(player, event.getHand(), event.getHitVec());
        if (BasketHarvestHelper.tryHarvest(context)
                || BasketItem.tryDepositIntoContainer(player.level(), event.getPos(), player, stack)) {
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
        }
    }
}
