package de.artemis.floraexpansion.common.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/** Reusable JSON geometry and texture templates for generated block models. */
final class ModelJsonTemplates {
    private ModelJsonTemplates() {
    }

    static JsonObject createCutoutCrossModelJson(String texture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:block/cross");
        model.addProperty("render_type", "cutout");
        textures.addProperty("cross", texture);
        textures.addProperty("particle", texture);
        model.add("textures", textures);
        return model;
    }

    static JsonObject createCutoutPottedPlantModelJson(String plantTexture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:block/flower_pot_cross");
        model.addProperty("render_type", "cutout");
        textures.addProperty("plant", plantTexture);
        model.add("textures", textures);
        return model;
    }

    static JsonObject createDoorModelJson(String parent, String bottomTexture, String topTexture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", parent);
        model.addProperty("render_type", "cutout");
        textures.addProperty("bottom", bottomTexture);
        textures.addProperty("top", topTexture);
        model.add("textures", textures);
        return model;
    }

    static JsonObject createTrapdoorModelJson(String parent) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", parent);
        model.addProperty("render_type", "cutout");
        textures.addProperty("texture", "floraexpansion:block/cactus_trapdoor");
        model.add("textures", textures);
        return model;
    }

    static JsonObject createGeneratedItemModelJson(String texture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:item/generated");
        textures.addProperty("layer0", texture);
        model.add("textures", textures);
        return model;
    }

    static JsonObject createComposterCrateModelJson(String bottomTexture, String topTexture, String sideTexture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:block/composter");
        textures.addProperty("particle", sideTexture);
        textures.addProperty("bottom", bottomTexture);
        textures.addProperty("top", topTexture);
        textures.addProperty("side", sideTexture);
        textures.addProperty("inside", bottomTexture);
        model.add("textures", textures);
        return model;
    }

    static JsonObject createCakeModelJson(String parent) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", parent);
        textures.addProperty("particle", "floraexpansion:block/strawberry_cake_side");
        textures.addProperty("bottom", "floraexpansion:block/strawberry_cake_bottom");
        textures.addProperty("top", "floraexpansion:block/strawberry_cake_top");
        textures.addProperty("side", "floraexpansion:block/strawberry_cake_side");
        textures.addProperty("inside", "floraexpansion:block/strawberry_cake_inner");
        model.add("textures", textures);
        return model;
    }

    static JsonObject createCandleCakeModelJson(String candleTexture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:block/template_cake_with_candle");
        textures.addProperty("particle", "floraexpansion:block/strawberry_cake_side");
        textures.addProperty("bottom", "floraexpansion:block/strawberry_cake_bottom");
        textures.addProperty("top", "floraexpansion:block/strawberry_cake_top");
        textures.addProperty("side", "floraexpansion:block/strawberry_cake_side");
        textures.addProperty("candle", candleTexture);
        model.add("textures", textures);
        return model;
    }

    static JsonObject createLeavesModelJson(String texture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:block/leaves");
        textures.addProperty("all", texture);
        model.add("textures", textures);
        return model;
    }

    static JsonObject createOakLeavesModelJson() {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();
        JsonArray elements = new JsonArray();

        model.addProperty("render_type", "minecraft:cutout_mipped");
        textures.addProperty("leaves", "minecraft:block/oak_leaves");
        textures.addProperty("particle", "minecraft:block/oak_leaves");
        model.add("textures", textures);
        elements.add(createOakLeavesBaseElement());
        model.add("elements", elements);
        return model;
    }

    static JsonObject createOakLeavesWithSideOverlayModelJson(String overlayTexture) {
        JsonObject model = createOakLeavesModelJson();
        JsonObject textures = model.getAsJsonObject("textures");

        textures.addProperty("overlay", overlayTexture);
        model.getAsJsonArray("elements").add(createOakLeavesSideOverlayElement());
        return model;
    }

    static JsonObject createOakLeavesBaseElement() {
        JsonObject element = new JsonObject();
        JsonObject faces = new JsonObject();

        element.add("from", createNumberArray(0, 0, 0));
        element.add("to", createNumberArray(16, 16, 16));
        faces.add("down", createTextureFace("#leaves", true));
        faces.add("east", createTextureFace("#leaves", true));
        faces.add("north", createTextureFace("#leaves", true));
        faces.add("south", createTextureFace("#leaves", true));
        faces.add("up", createTextureFace("#leaves", true));
        faces.add("west", createTextureFace("#leaves", true));
        element.add("faces", faces);
        return element;
    }

    static JsonObject createOakLeavesSideOverlayElement() {
        JsonObject element = new JsonObject();
        JsonObject faces = new JsonObject();

        element.add("from", createNumberArray(-0.01F, -0.01F, -0.01F));
        element.add("to", createNumberArray(16.01F, 16.01F, 16.01F));
        faces.add("east", createTextureFace("#overlay", false, true));
        faces.add("north", createTextureFace("#overlay", false, true));
        faces.add("south", createTextureFace("#overlay", false, true));
        faces.add("west", createTextureFace("#overlay", false, true));
        element.add("faces", faces);
        return element;
    }

    static JsonObject createTextureFace(String texture, boolean tint) {
        return createTextureFace(texture, tint, false);
    }

    static JsonObject createTextureFace(String texture, boolean tint, boolean uv) {
        JsonObject face = new JsonObject();

        face.addProperty("texture", texture);
        if (uv) {
            face.add("uv", createNumberArray(0.0F, 0.0F, 16.0F, 16.0F));
        }
        if (tint) {
            face.addProperty("tintindex", 0);
        }
        return face;
    }

    static JsonArray createNumberArray(Number... values) {
        JsonArray array = new JsonArray();

        for (Number value : values) {
            array.add(value);
        }
        return array;
    }

    static JsonObject createGroundPlaneModelJson(String texture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();
        JsonArray elements = new JsonArray();
        JsonObject element = new JsonObject();
        JsonObject faces = new JsonObject();

        model.addProperty("parent", "minecraft:block/block");
        model.addProperty("render_type", "cutout");

        textures.addProperty("particle", texture);
        textures.addProperty("texture", texture);
        model.add("textures", textures);

        element.add("from", createNumberArray(0, 0, 0));

        element.add("to", createNumberArray(16, 0.1, 16));

        faces.add("up", createFace("#texture", false, null));
        element.add("faces", faces);
        elements.add(element);
        model.add("elements", elements);
        return model;
    }

    static JsonObject createInsetCactusModelJson(String bottomTexture, String topTexture, String sideTexture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();
        JsonArray elements = new JsonArray();

        model.addProperty("parent", "minecraft:block/block");
        model.addProperty("render_type", "cutout");
        textures.addProperty("particle", sideTexture);
        textures.addProperty("bottom", bottomTexture);
        textures.addProperty("top", topTexture);
        textures.addProperty("side", sideTexture);
        model.add("textures", textures);

        elements.add(createCactusCoreElement());
        elements.add(createCactusNorthSouthElement());
        elements.add(createCactusWestEastElement());
        model.add("elements", elements);
        return model;
    }

    static JsonObject createCactusCoreElement() {
        JsonObject element = new JsonObject();
        JsonObject faces = new JsonObject();

        element.add("from", createNumberArray(1, 0, 1));

        element.add("to", createNumberArray(15, 16, 15));

        faces.add("down", createFace("#bottom", 1, 1, 15, 15, false, "down"));
        faces.add("up", createFace("#top", 1, 1, 15, 15, false, "up"));
        element.add("faces", faces);
        return element;
    }

    static JsonObject createCactusNorthSouthElement() {
        JsonObject element = new JsonObject();
        JsonObject faces = new JsonObject();

        element.add("from", createNumberArray(0, 0, 1));

        element.add("to", createNumberArray(16, 16, 15));

        faces.add("north", createFace("#side", 0, 0, 16, 16, false, null));
        faces.add("south", createFace("#side", 0, 0, 16, 16, false, null));
        element.add("faces", faces);
        return element;
    }

    static JsonObject createCactusWestEastElement() {
        JsonObject element = new JsonObject();
        JsonObject faces = new JsonObject();

        element.add("from", createNumberArray(1, 0, 0));

        element.add("to", createNumberArray(15, 16, 16));

        faces.add("west", createFace("#side", 0, 0, 16, 16, false, null));
        faces.add("east", createFace("#side", 0, 0, 16, 16, false, null));
        element.add("faces", faces);
        return element;
    }

    static JsonObject createPottedCactusModelJson(String sideTexture, String topTexture) {
        JsonObject model = new JsonObject();
        JsonObject textures = new JsonObject();

        model.addProperty("parent", "minecraft:block/potted_cactus");
        model.addProperty("render_type", "cutout");
        textures.addProperty("cactus", sideTexture);
        textures.addProperty("cactus_top", topTexture);
        textures.addProperty("flowerpot", "minecraft:block/flower_pot");
        model.add("textures", textures);
        return model;
    }

    static JsonObject createFace(String textureSlot, boolean tint, String cullface) {
        return createFace(textureSlot, 0, 0, 16, 16, tint, cullface);
    }

    static JsonObject createFace(String textureSlot,
                                         int u1,
                                         int v1,
                                         int u2,
                                         int v2,
                                         boolean tint,
                                         String cullface) {
        JsonObject face = new JsonObject();
        face.add("uv", createNumberArray(u1, v1, u2, v2));
        face.addProperty("texture", textureSlot);

        if (cullface != null) {
            face.addProperty("cullface", cullface);
        }

        if (tint) {
            face.addProperty("tintindex", 0);
        }

        return face;
    }
}
