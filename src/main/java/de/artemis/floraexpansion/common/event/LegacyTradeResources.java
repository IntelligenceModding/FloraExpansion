package de.artemis.floraexpansion.common.event;

import org.jetbrains.annotations.NotNull;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import java.util.*;

/** Reads the 0.5 trade resources on Minecraft versions that predate the vanilla trade registry. */
@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class LegacyTradeResources {
    @SubscribeEvent public static void listeners(AddReloadListenerEvent event) {
        event.addListener((ResourceManagerReloadListener) LegacyTradeResources::reload);
    }

    public record Trade(ResourceLocation id, JsonObject definition) implements VillagerTrades.ItemListing {
        @Override public MerchantOffer getOffer(Entity trader, @NotNull RandomSource random) {
            var ops = RegistryOps.create(JsonOps.INSTANCE, trader.registryAccess());
            var cost = ItemCost.CODEC.parse(ops, definition.get("wants")).getOrThrow();
            var result = ItemStack.CODEC.parse(ops, definition.get("gives")).getOrThrow();
            return new MerchantOffer(cost, result, number(definition, "max_uses", 12).intValue(), number(definition, "xp", 1).intValue(), number(definition, "reputation_discount", .05F).floatValue());
        }
    }
    private static Number number(JsonObject json, String key, Number fallback) { return json.has(key) ? json.get(key).getAsNumber() : fallback; }
    private static VillagerTrades.ItemListing[] withoutOwn(VillagerTrades.ItemListing[] listings) {
        return Arrays.stream(listings).filter(entry -> !(entry instanceof Trade)).toArray(VillagerTrades.ItemListing[]::new);
    }
    private static void reload(ResourceManager resources) {
        Map<ResourceLocation, Trade> trades = new HashMap<>();
        for (var entry : resources.listResources("villager_trade", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                var id = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(), entry.getKey().getPath().substring(15, entry.getKey().getPath().length()-5));
                trades.put(id, new Trade(id, JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load trade " + entry.getKey(), error); }
        }
        // Only remove our own entries, preserving vanilla and other mods across /reload.
        VillagerTrades.TRADES.values().forEach(levels -> levels.replaceAll((level, listings) -> withoutOwn(listings)));
        VillagerTrades.WANDERING_TRADER_TRADES.replaceAll((level, listings) -> withoutOwn(listings));
        for (var file : resources.listResources("tags/villager_trade", id -> id.getNamespace().equals("minecraft") && id.getPath().endsWith(".json")).keySet()) {
            Set<ResourceLocation> members = readPool(resources, file, new HashSet<>());
            var additions = members.stream().map(trades::get).filter(Objects::nonNull).toList();
            if (additions.isEmpty()) continue;
            String pool = file.getPath().substring("tags/villager_trade/".length(), file.getPath().length()-5);
            if (pool.startsWith("wandering_trader/")) {
                // 1.21.1 has common and rare pools; fold the later uncommon pool into common.
                int index = pool.endsWith("/rare") ? 2 : 1;
                var entries = new ArrayList<VillagerTrades.ItemListing>(Arrays.asList(VillagerTrades.WANDERING_TRADER_TRADES.get(index)));
                entries.addAll(additions);
                VillagerTrades.WANDERING_TRADER_TRADES.put(index, entries.toArray(VillagerTrades.ItemListing[]::new));
            } else {
                var parts = pool.split("/level_"); if (parts.length != 2) continue;
                var profession = net.minecraft.core.registries.BuiltInRegistries.VILLAGER_PROFESSION.get(ResourceLocation.withDefaultNamespace(parts[0]));
                var levels = VillagerTrades.TRADES.get(profession); if (levels == null) throw new IllegalStateException("Unknown trade profession " + pool);
                int level = Integer.parseInt(parts[1]);
                var entries = new ArrayList<VillagerTrades.ItemListing>(Arrays.asList(levels.getOrDefault(level, new VillagerTrades.ItemListing[0]))); entries.addAll(additions);
                levels.put(level, entries.toArray(VillagerTrades.ItemListing[]::new));
            }
        }
        FloraExpansion.LOGGER.info("Loaded {} Flora Expansion trade definitions for 1.21.1", trades.size());
    }
    private static Set<ResourceLocation> readPool(ResourceManager resources, ResourceLocation file, Set<ResourceLocation> visiting) {
        if (!visiting.add(file)) throw new IllegalStateException("Cyclic trade pool tag: " + file);
        Set<ResourceLocation> members = new LinkedHashSet<>();
        for (var resource : resources.getResourceStack(file)) {
            try (var reader = resource.openAsReader()) {
                var json = JsonParser.parseReader(reader).getAsJsonObject();
                if (json.has("replace") && json.get("replace").getAsBoolean()) members.clear();
                for (var value : json.getAsJsonArray("values")) {
                    String name = value.isJsonObject() ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();
                    if (name.startsWith("#")) {
                        var nested = ResourceLocation.parse(name.substring(1));
                        members.addAll(readPool(resources, nested.withPath("tags/villager_trade/" + nested.getPath() + ".json"), visiting));
                    } else members.add(ResourceLocation.parse(name));
                }
            } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load trade pool " + file, error); }
        }
        visiting.remove(file);
        return members;
    }
    private LegacyTradeResources() {}
}
