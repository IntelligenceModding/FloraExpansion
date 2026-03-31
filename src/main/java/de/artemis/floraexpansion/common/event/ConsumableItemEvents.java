package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public class ConsumableItemEvents {

    private static final float APPLE_CORE_CHANCE = 0.5F;

    @SubscribeEvent
    public static void onItemFinished(LivingEntityUseItemEvent.Finish event) {
        ItemStack consumedStack = event.getItem();
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide()) {
            return;
        }

        handleAppleConsumption(consumedStack, entity);
    }

    private static void handleAppleConsumption(ItemStack consumedStack, LivingEntity entity) {
        if (!consumedStack.is(Items.APPLE)) {
            return;
        }

        if (entity.getRandom().nextFloat() >= APPLE_CORE_CHANCE) {
            return;
        }

        giveOrDrop(entity, new ItemStack(ModBlocks.APPLE_CORE.get()));
    }

    private static void giveOrDrop(@NotNull LivingEntity entity, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        if (entity instanceof Player player) {
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        } else {
            Containers.dropItemStack(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack);
        }
    }
}