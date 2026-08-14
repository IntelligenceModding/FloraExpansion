package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class VillagerTradeEvents {
    private VillagerTradeEvents() {
    }

    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.FARMER) {
            addFarmerTrades(event);
            return;
        }

        if (event.getType() == VillagerProfession.FLETCHER) {
            addFletcherTrades(event);
            return;
        }

        if (event.getType() == VillagerProfession.SHEPHERD) {
            addShepherdTrades(event);
        }
    }

    private static void addFarmerTrades(VillagerTradesEvent event) {
        addTrade(event, 1, new VillagerTrades.EmeraldForItems(ModItems.FLAX_SEED.get(), 24, 16, 2));
        addTrade(event, 1, new VillagerTrades.EmeraldForItems(ModItems.BLUEBERRIES.get(), 16, 16, 2));
        addTrade(event, 1, new VillagerTrades.EmeraldForItems(ModItems.STRAWBERRY.get(), 20, 16, 2));
        addTrade(event, 2, new VillagerTrades.EmeraldForItems(ModItems.FLAX_FIBER.get(), 18, 12, 10));
        addTrade(event, 2, new VillagerTrades.ItemsForEmeralds(ModItems.BLUEBERRIES.get(), 1, 1, 12, 5));
        addTrade(event, 2, new VillagerTrades.ItemsForEmeralds(ModItems.STRAWBERRY.get(), 1, 3, 12, 5));
        addTrade(event, 3, new VillagerTrades.ItemsForEmeralds(ModItems.BLUEBERRY_PIE_SLICE.get(), 2, 1, 12, 10));
        addTrade(event, 3, new VillagerTrades.ItemsForEmeralds(ModItems.STRAWBERRY_JAM.get(), 3, 1, 12, 10));
    }

    private static void addFletcherTrades(VillagerTradesEvent event) {
        addTrade(event, 3, new VillagerTrades.EmeraldForItems(ModItems.LINEN_THREAD.get(), 16, 16, 20));
    }

    private static void addShepherdTrades(VillagerTradesEvent event) {
        addTrade(event, 2, new VillagerTrades.EmeraldForItems(ModItems.LINEN_CLOTH.get(), 8, 12, 10));
        addTrade(event, 2, new VillagerTrades.ItemsForEmeralds(ModBlocks.LINEN_BLOCK.get(), 1, 1, 16, 5));
        addTrade(event, 2, new VillagerTrades.ItemsForEmeralds(ModBlocks.LINEN_CARPET.get(), 1, 4, 16, 5));
    }

    private static void addTrade(VillagerTradesEvent event, int level, VillagerTrades.ItemListing trade) {
        event.getTrades().get(level).add(trade);
    }

    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event) {
        event.getGenericTrades().add(new BasicItemListing(1, new ItemStack(ModItems.BLUEBERRIES.get(), 1), 6, 1, 0.05F));
        event.getGenericTrades().add(new BasicItemListing(1, new ItemStack(ModItems.STRAWBERRY.get(), 3), 6, 1, 0.05F));
        event.getRareTrades().add(new BasicItemListing(3, new ItemStack(ModItems.BLUEBERRY_JAM.get(), 1), 2, 1, 0.05F));
        event.getRareTrades().add(new BasicItemListing(3, new ItemStack(ModItems.STRAWBERRY_JAM.get(), 1), 2, 1, 0.05F));
    }
}

