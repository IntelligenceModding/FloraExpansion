package de.artemis.floraexpansion.common.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/** Reusable JSON geometry and texture templates for generated block models. */
final class ModelJsonTemplates {
    private ModelJsonTemplates() {
    }

    static JsonObject createJarModelJson(String jam, int portions) {
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:block/block");
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", "minecraft:block/glass");
        textures.add("glass", translucentMaterial("minecraft:block/glass"));
        textures.add("body", translucentMaterial("floraexpansion:block/glass_jar"));
        textures.addProperty("cork", "minecraft:block/stripped_oak_log_top");
        if (jam != null) textures.add("jam", translucentMaterial("floraexpansion:block/" + jam + "_fill"));
        model.add("textures", textures);
        JsonArray elements = new JsonArray();
        elements.add(jarElement(4, 0, 4, 12, 8, 12, "body", false));
        addJarGlass(elements, 5, 8, 5, 11, 10, 11, true);
        elements.add(jarElement(4, 10, 4, 12, 12, 12, "cork", false));
        if (jam != null) elements.add(jarElement(5, 1, 5, 11, 1 + (6 * portions + 3) / 4, 11, "jam", false));
        model.add("elements", elements);
        return model;
    }

    private static JsonObject translucentMaterial(String sprite) {
        JsonObject material = new JsonObject();
        material.addProperty("sprite", sprite);
        material.addProperty("force_translucent", true);
        return material;
    }

    private static void addJarGlass(JsonArray elements, int x1, int y1, int z1, int x2, int y2, int z2, boolean sidesOnly) {
        for (String direction : new String[]{"down", "up", "north", "south", "east", "west"}) {
            boolean horizontal = direction.equals("down") || direction.equals("up");
            if (horizontal && sidesOnly) continue;
            int halfWidth = (direction.equals("east") || direction.equals("west") ? z2 - z1 : x2 - x1) / 2;
            int halfHeight = (horizontal ? z2 - z1 : y2 - y1) / 2;
            for (int row = 0; row < 2; row++) for (int column = 0; column < 2; column++) {
                int u1 = column * halfWidth, u2 = u1 + halfWidth;
                int v1 = row * halfHeight, v2 = v1 + halfHeight;
                int[] bounds = switch (direction) {
                    case "north" -> new int[]{x2-u2, y2-v2, z1, x2-u1, y2-v1, z1};
                    case "south" -> new int[]{x1+u1, y2-v2, z2, x1+u2, y2-v1, z2};
                    case "west" -> new int[]{x1, y2-v2, z1+u1, x1, y2-v1, z1+u2};
                    case "east" -> new int[]{x2, y2-v2, z2-u2, x2, y2-v1, z2-u1};
                    case "up" -> new int[]{x1+u1, y2, z1+v1, x1+u2, y2, z1+v2};
                    case "down" -> new int[]{x1+u1, y1, z2-v2, x1+u2, y1, z2-v1};
                    default -> throw new IllegalArgumentException(direction);
                };
                JsonObject element = new JsonObject();
                element.add("from", createNumberArray(bounds[0], bounds[1], bounds[2]));
                element.add("to", createNumberArray(bounds[3], bounds[4], bounds[5]));
                int textureU = column == 0 ? 0 : 16 - halfWidth;
                int textureV = row == 0 ? 0 : 16 - halfHeight;
                JsonObject faces = new JsonObject();
                faces.add(direction, createFace("#glass", textureU, textureV,
                        textureU + halfWidth, textureV + halfHeight, false, null));
                element.add("faces", faces);
                elements.add(element);
            }
        }
    }

    private static JsonObject jarElement(int x1, int y1, int z1, int x2, int y2, int z2, String material, boolean sidesOnly) {
        JsonObject element = new JsonObject();
        element.add("from", createNumberArray(x1, y1, z1));
        element.add("to", createNumberArray(x2, y2, z2));
        JsonObject faces = new JsonObject();
        for (String direction : new String[]{"down", "up", "north", "south", "east", "west"}) {
            boolean horizontal = direction.equals("down") || direction.equals("up");
            if (horizontal && sidesOnly) continue;
            int width = direction.equals("east") || direction.equals("west") ? z2 - z1 : x2 - x1;
            int height = horizontal ? z2 - z1 : y2 - y1;
            JsonObject face = material.equals("jam")
                    ? createFace("#jam", 5, horizontal ? 6 : 12 - height, 11, 12, false, null)
                    : material.equals("cork") && horizontal
                    ? createFace("#cork", x1, z1, x2, z2, false, null)
                    : createFace("#" + material, 0, 16 - height, width, 16, false, null);
            if (material.equals("jam")) face.addProperty("tintindex", 0);
            faces.add(direction, face);
        }
        element.add("faces", faces);
        return element;
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

    static JsonObject createHalfSliceCakeModelJson(int completedSlices) {
        JsonObject model = createCakeModelJson("minecraft:block/block");
        JsonArray elements = new JsonArray();
        int x = 1 + completedSlices * 2;
        if (x + 2 < 15) {
            elements.add(createCakePiece(x + 2, 1, 15, 15, true, false));
        }
        elements.add(createCakePiece(x, 8, x + 2, 15, completedSlices > 0, true));
        model.add("elements", elements);
        return model;
    }

    private static JsonObject createCakePiece(int x1, int z1, int x2, int z2, boolean cutWest, boolean cutNorth) {
        JsonObject element = new JsonObject();
        element.add("from", createNumberArray(x1, 0, z1));
        element.add("to", createNumberArray(x2, 8, z2));
        JsonObject faces = new JsonObject();
        for (String direction : new String[]{"down", "up", "north", "south", "west", "east"}) {
            JsonObject face = new JsonObject();
            String texture = switch (direction) {
                case "down" -> "bottom";
                case "up" -> "top";
                case "west" -> cutWest ? "inside" : "side";
                case "north" -> cutNorth ? "inside" : "side";
                default -> "side";
            };
            face.addProperty("texture", "#" + texture);
            if (direction.equals("down")) face.addProperty("cullface", "down");
            faces.add(direction, face);
        }
        element.add("faces", faces);
        return element;
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

    static JsonObject createFloweringDesertMossModelJson(String texture, int variant) {
        JsonObject model = createGroundPlaneModelJson(texture);
        model.getAsJsonObject("textures").addProperty("flowers", "minecraft:block/pink_petals");
        int[][] flowers = switch (variant) {
            case 0 -> new int[][]{{3, 3}, {10, 3}, {9, 10}};
            case 1 -> new int[][]{{11, 1}, {4, 5}, {8, 8}};
            case 2 -> new int[][]{{3, 2}, {11, 5}, {5, 10}};
            default -> new int[][]{{3, 3}, {11, 4}, {3, 11}};
        };
        for (int[] flower : flowers) {
            JsonObject element = new JsonObject();
            element.add("from", createNumberArray(flower[0], 0.2, flower[1]));
            element.add("to", createNumberArray(flower[0] + 3, 0.2, flower[1] + 3));
            JsonObject faces = new JsonObject();
            faces.add("up", createFace("#flowers", 3, 0, 6, 3, false, null));
            element.add("faces", faces);
            model.getAsJsonArray("elements").add(element);
        }
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
