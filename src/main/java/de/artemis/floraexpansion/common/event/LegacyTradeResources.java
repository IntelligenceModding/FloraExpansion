package de.artemis.floraexpansion.common.event;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import org.apache.commons.lang3.tuple.Pair;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import java.util.*;

/** Reads the 0.5 trade resources on Minecraft versions that predate the vanilla trade registry. */
@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class LegacyTradeResources {
    @SubscribeEvent public static void listeners(AddServerReloadListenersEvent event) {
        event.addListener(Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "trades"), (ResourceManagerReloadListener) LegacyTradeResources::reload);
    }

    public record Trade(Identifier id, JsonObject definition) implements VillagerTrades.ItemListing {
        @Override public MerchantOffer getOffer(net.minecraft.server.level.ServerLevel level, Entity trader, RandomSource random) {
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
        Map<Identifier, Trade> trades = new HashMap<>();
        for (var entry : resources.listResources("villager_trade", id -> id.getPath().endsWith(".json")).entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                var id = Identifier.fromNamespaceAndPath(entry.getKey().getNamespace(), entry.getKey().getPath().substring(15, entry.getKey().getPath().length()-5));
                trades.put(id, new Trade(id, JsonParser.parseReader(reader).getAsJsonObject()));
            } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load trade " + entry.getKey(), error); }
        }
        // Only remove our own entries, preserving vanilla and other mods across /reload.
        VillagerTrades.TRADES.values().forEach(levels -> levels.replaceAll((level, listings) -> withoutOwn(listings)));
        var wandering = new ArrayList<Pair<VillagerTrades.ItemListing[], Integer>>();
        for (var pool : VillagerTrades.WANDERING_TRADER_TRADES) wandering.add(Pair.of(withoutOwn(pool.getLeft()), pool.getRight()));
        for (var file : resources.listResources("tags/villager_trade", id -> id.getNamespace().equals("minecraft") && id.getPath().endsWith(".json")).keySet()) {
            Set<Identifier> members = readPool(resources, file, new HashSet<>());
            var additions = members.stream().map(trades::get).filter(Objects::nonNull).toList();
            if (additions.isEmpty()) continue;
            String pool = file.getPath().substring("tags/villager_trade/".length(), file.getPath().length()-5);
            if (pool.startsWith("wandering_trader/")) {
                int index = pool.endsWith("/common") ? 2 : pool.endsWith("/uncommon") ? 1 : 0;
                var existing = wandering.get(index);
                var entries = new ArrayList<VillagerTrades.ItemListing>(Arrays.asList(existing.getLeft())); entries.addAll(additions);
                wandering.set(index, Pair.of(entries.toArray(VillagerTrades.ItemListing[]::new), existing.getRight()));
            } else {
                var parts = pool.split("/level_"); if (parts.length != 2) continue;
                var profession = ResourceKey.create(Registries.VILLAGER_PROFESSION, Identifier.withDefaultNamespace(parts[0]));
                var levels = VillagerTrades.TRADES.get(profession); if (levels == null) throw new IllegalStateException("Unknown trade profession " + pool);
                int level = Integer.parseInt(parts[1]);
                var entries = new ArrayList<VillagerTrades.ItemListing>(Arrays.asList(levels.getOrDefault(level, new VillagerTrades.ItemListing[0]))); entries.addAll(additions);
                levels.put(level, entries.toArray(VillagerTrades.ItemListing[]::new));
            }
        }
        VillagerTrades.WANDERING_TRADER_TRADES = List.copyOf(wandering);
        FloraExpansion.LOGGER.info("Loaded {} Flora Expansion trade definitions for 1.21.11", trades.size());
    }
    private static Set<Identifier> readPool(ResourceManager resources, Identifier file, Set<Identifier> visiting) {
        if (!visiting.add(file)) throw new IllegalStateException("Cyclic trade pool tag: " + file);
        Set<Identifier> members = new LinkedHashSet<>();
        for (var resource : resources.getResourceStack(file)) {
            try (var reader = resource.openAsReader()) {
                var json = JsonParser.parseReader(reader).getAsJsonObject();
                if (json.has("replace") && json.get("replace").getAsBoolean()) members.clear();
                for (var value : json.getAsJsonArray("values")) {
                    String name = value.isJsonObject() ? value.getAsJsonObject().get("id").getAsString() : value.getAsString();
                    if (name.startsWith("#")) {
                        var nested = Identifier.parse(name.substring(1));
                        members.addAll(readPool(resources, nested.withPath("tags/villager_trade/" + nested.getPath() + ".json"), visiting));
                    } else members.add(Identifier.parse(name));
                }
            } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load trade pool " + file, error); }
        }
        visiting.remove(file);
        return members;
    }
    private LegacyTradeResources() {}
}
