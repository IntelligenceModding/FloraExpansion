package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.util.InventoryUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import de.artemis.floraexpansion.common.config.ServerConfig;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public class ConsumableItemEvents {


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

        if (entity.getRandom().nextFloat() >= ServerConfig.appleCoreChance.get()) {
            return;
        }

        InventoryUtils.giveOrDrop(entity, new ItemStack(ModBlocks.APPLE_CORE.get()));
    }
}
