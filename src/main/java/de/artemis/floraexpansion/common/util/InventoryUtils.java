package de.artemis.floraexpansion.common.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Shared delivery and synchronization for inventory-changing interactions. */
public final class InventoryUtils {
    private InventoryUtils() {
    }

    public static void giveOrDrop(LivingEntity entity, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (entity instanceof Player player) {
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false, Prediction.SERVER_ONLY);
            }
        } else if (entity.level() instanceof ServerLevel level) {
            entity.spawnAtLocation(level, stack);
        }
    }

    public static void syncPlayerInventory(Player player) {
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
    }
}
