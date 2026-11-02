package de.artemis.floraexpansion.client.renderer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FaceInfo;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

/** Reads the ordinary JSON asset directly; no item display transforms or generated textures. */
public final class SaltingSceneModel implements ResourceManagerReloadListener {
    public static final SaltingSceneModel INSTANCE = new SaltingSceneModel();
    private static final Identifier MODEL = Identifier.fromNamespaceAndPath("floraexpansion", "models/block/salting_tub_cutaway.json");
    private List<Face> faces = List.of();
    private record Face(Vector3f min, Vector3f max, Direction direction, Identifier texture, float[] uv) {}

    @Override public void onResourceManagerReload(ResourceManager resources) {
        var loaded = new ArrayList<Face>();
        try (var reader = resources.getResourceOrThrow(MODEL).openAsReader()) {
            var model = JsonParser.parseReader(reader).getAsJsonObject();
            var textures = model.getAsJsonObject("textures");
            for (var entry : model.getAsJsonArray("elements")) {
                var element = entry.getAsJsonObject();
                var min = vector(element, "from");
                var max = vector(element, "to");
                for (var faceEntry : element.getAsJsonObject("faces").entrySet()) {
                    var face = faceEntry.getValue().getAsJsonObject();
                    String texture = face.get("texture").getAsString();
                    while (texture.startsWith("#")) texture = textures.get(texture.substring(1)).getAsString();
                    var id = Identifier.parse(texture);
                    var uv = face.getAsJsonArray("uv");
                    loaded.add(new Face(min, max, Direction.byName(faceEntry.getKey()),
                            id.withPath(path -> "textures/" + path + ".png"),
                            new float[]{uv.get(0).getAsFloat()/16, uv.get(1).getAsFloat()/16,
                                    uv.get(2).getAsFloat()/16, uv.get(3).getAsFloat()/16}));
                }
            }
        } catch (java.io.IOException | RuntimeException error) {
            throw new IllegalStateException("Cannot load the salting JEI model " + MODEL, error);
        }
        faces = List.copyOf(loaded);
    }
    private static Vector3f vector(JsonObject element, String field) {
        var a = element.getAsJsonArray(field);
        return new Vector3f(a.get(0).getAsFloat()/16, a.get(1).getAsFloat()/16, a.get(2).getAsFloat()/16);
    }
    void submit(PoseStack poses, SubmitNodeCollector collector) {
        if (faces.isEmpty()) onResourceManagerReload(Minecraft.getInstance().getResourceManager());
        for (var face : faces) {
            collector.submitCustomGeometry(poses, RenderTypes.entityCutout(face.texture), (pose, out) -> {
                var info = FaceInfo.fromFacing(face.direction);
                for (int i = 0; i < 4; i++) {
                    var vertex = info.getVertexInfo(i).select(face.min, face.max);
                    out.addVertex(pose, vertex.x, vertex.y, vertex.z).setColor(-1)
                            .setUv(i < 2 ? face.uv[0] : face.uv[2], i == 0 || i == 3 ? face.uv[1] : face.uv[3])
                            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880)
                            .setNormal(pose, face.direction.getStepX(), face.direction.getStepY(), face.direction.getStepZ());
                }
            });
        }
    }
}
