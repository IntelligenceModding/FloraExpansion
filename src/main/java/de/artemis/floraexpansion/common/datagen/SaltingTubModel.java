package de.artemis.floraexpansion.common.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/** Open wooden tub with iron straps; textures come from the existing vanilla palette. */
final class SaltingTubModel {
    static JsonObject create() {
        var model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        var textures = new JsonObject();
        textures.addProperty("particle", "minecraft:block/composter_side");
        textures.addProperty("side", "minecraft:block/composter_side");
        textures.addProperty("top", "minecraft:block/composter_top");
        textures.addProperty("bottom", "minecraft:block/composter_bottom");
        textures.addProperty("metal", "minecraft:block/iron_block");
        model.add("textures", textures);
        var elements = new JsonArray();
        var floor = box(elements, 0, 0, 0, 16, 2, 16, false);
        // The composter top is a rim texture; the interior floor needs solid wood.
        floor.getAsJsonObject("faces").getAsJsonObject("up").addProperty("texture", "#bottom");
        box(elements, 0, 2, 0, 2, 16, 16, false);
        box(elements, 14, 2, 0, 16, 16, 16, false);
        box(elements, 2, 2, 0, 14, 16, 2, false);
        box(elements, 2, 2, 14, 14, 16, 16, false);
        // Narrow metal bands make the salting tub distinct from the composter.
        for (int y : new int[]{3, 12}) {
            box(elements, -0.05, y, -0.05, 16.05, y + 1, 0.05, true);
            box(elements, -0.05, y, 15.95, 16.05, y + 1, 16.05, true);
            box(elements, -0.05, y, 0.05, 0.05, y + 1, 15.95, true);
            box(elements, 15.95, y, 0.05, 16.05, y + 1, 15.95, true);
        }
        model.add("elements", elements);
        return model;
    }

    private static JsonObject box(JsonArray elements, double x0, double y0, double z0, double x1, double y1, double z1, boolean metal) {
        var element = new JsonObject();
        element.add("from", vector(x0, y0, z0));
        element.add("to", vector(x1, y1, z1));
        var faces = new JsonObject();
        for (String direction : new String[]{"up", "down", "north", "south", "east", "west"}) {
            var face = new JsonObject();
            face.addProperty("texture", metal ? "#metal" : direction.equals("up") ? "#top" : direction.equals("down") ? "#bottom" : "#side");
            faces.add(direction, face);
        }
        element.add("faces", faces);
        elements.add(element);
        return element;
    }
    private static JsonArray vector(double x, double y, double z) {
        var array = new JsonArray();
        array.add(x); array.add(y); array.add(z);
        return array;
    }
    private SaltingTubModel() {}
}
