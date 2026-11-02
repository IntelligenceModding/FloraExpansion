package de.artemis.floraexpansion;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.loot.LootTable;

/** Validate effective pack resources against the running 26.1.2 server, including unlock references. */
final class RecipeDataAuditTests {
    static void verify(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        var resources = server.getResourceManager();
        var ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());
        int recipes = 0, unlocks = 0, loot = 0;
        for (var entry : resources.listResources("recipe", id -> id.getNamespace().equals(FloraExpansion.MODID) && id.getPath().endsWith(".json")).entrySet()) {
            var file = entry.getKey();
            var id = Identifier.fromNamespaceAndPath(file.getNamespace(), file.getPath().substring("recipe/".length(), file.getPath().length()-5));
            var key = ResourceKey.create(Registries.RECIPE, id);
            try (var reader = entry.getValue().openAsReader()) {
                var json = JsonParser.parseReader(reader);
                var decoded = Recipe.CODEC.parse(ops, json).getOrThrow();
                var loaded = server.getRecipeManager().byKey(key).orElseThrow(() -> new IllegalStateException("Defined recipe did not load: " + id)).value();
                check(loaded.getSerializer() == decoded.getSerializer(), "Wrong recipe serializer: " + id);
                Recipe.CODEC.parse(ops, Recipe.CODEC.encodeStart(ops, decoded).getOrThrow()).getOrThrow();
                for (var ingredient : loaded.placementInfo().ingredients())
                    check(ingredient.items().findAny().isPresent(), "Recipe has an empty ingredient/tag: " + id);
                recipes++;
            } catch (java.io.IOException exception) { throw new IllegalStateException(file.toString(), exception); }
        }
        // Include minecraft-namespaced resources: an incorrectly emitted mod unlock must also fail this audit.
        for (var entry : resources.listResources("advancement", id -> id.getPath().startsWith("advancement/recipes/") && id.getPath().endsWith(".json")).entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                var json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.toString().contains("floraexpansion:")) continue;
                if (json.has("rewards") && json.getAsJsonObject("rewards").has("recipes"))
                    for (var reward : json.getAsJsonObject("rewards").getAsJsonArray("recipes")) {
                        var id = Identifier.parse(reward.getAsString());
                        check(server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent(),
                                "Recipe unlock points to a missing recipe: " + entry.getKey() + " -> " + id);
                        unlocks++;
                    }
                for (var criterion : json.getAsJsonObject("criteria").entrySet()) {
                    var value = criterion.getValue().getAsJsonObject();
                    if (value.has("conditions") && value.getAsJsonObject("conditions").has("recipe")) {
                        var id = Identifier.parse(value.getAsJsonObject("conditions").get("recipe").getAsString());
                        check(server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent(),
                                "Recipe criterion points to a missing recipe: " + entry.getKey() + " -> " + id);
                    }
                }
            } catch (java.io.IOException exception) { throw new IllegalStateException(entry.getKey().toString(), exception); }
        }
        int parity = 0;
        try (var reader = new java.io.InputStreamReader(java.util.Objects.requireNonNull(RecipeDataAuditTests.class.getResourceAsStream(
                "/floraexpansion/recipe-audit/expected-0.5-recipes.json")), java.nio.charset.StandardCharsets.UTF_8)) {
            var expected = JsonParser.parseReader(reader).getAsJsonObject().getAsJsonObject("recipes");
            for (var entry : expected.entrySet()) {
                var key = ResourceKey.create(Registries.RECIPE, Identifier.parse(entry.getKey()));
                var loaded = server.getRecipeManager().byKey(key).orElseThrow().value();
                var upstream = Recipe.CODEC.parse(ops, entry.getValue()).getOrThrow();
                check(Recipe.CODEC.encodeStart(ops, loaded).getOrThrow().equals(Recipe.CODEC.encodeStart(ops, upstream).getOrThrow()),
                        "Recipe behavior differs from upstream 0.5: " + key.identifier());
                parity++;
            }
        } catch (java.io.IOException exception) { throw new IllegalStateException(exception); }
        var lootOps = RegistryOps.create(JsonOps.INSTANCE, server.reloadableRegistries().lookup());
        for (var entry : resources.listResources("loot_table", id -> id.getNamespace().equals(FloraExpansion.MODID) && id.getPath().endsWith(".json")).entrySet()) {
            try (var reader = entry.getValue().openAsReader()) {
                LootTable.DIRECT_CODEC.parse(lootOps, JsonParser.parseReader(reader)).getOrThrow();
                loot++;
            } catch (java.io.IOException exception) { throw new IllegalStateException(entry.getKey().toString(), exception); }
        }
        check(recipes > 0 && unlocks > 0 && loot > 0, "Data audit did not inspect all resource categories");
        FloraExpansion.LOGGER.info("26.1.2 data audit passed: {} recipe resources, {} recipe unlocks, {} loot tables; all {} release recipes match upstream 0.5 including cooking times", recipes, unlocks, loot, parity);
    }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
}
