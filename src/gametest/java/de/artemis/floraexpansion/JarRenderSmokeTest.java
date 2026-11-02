package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.block.JamJarBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

final class JarRenderSmokeTest {
    private static BlockPos origin;
    private static int startTick;

    static boolean check(Minecraft minecraft, int worldTicks) {
        Block[] jars = {ModBlocks.GLASS_JAR.get(), ModBlocks.BLUEBERRY_JAM.get(), ModBlocks.STRAWBERRY_JAM.get(),
                ModBlocks.CHERRY_JAM.get(), ModBlocks.APPLE_JAM.get()};
        if (origin == null) {
            origin = new BlockPos(minecraft.player.getBlockX() + 12, 250, minecraft.player.getBlockZ());
            startTick = worldTicks;
            var server = minecraft.getSingleplayerServer();
            var playerId = minecraft.player.getUUID();
            server.execute(() -> {
                var level = server.overworld();
                for (int x = -1; x <= 5; x++) for (int z = -7; z <= 4; z++) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
                }
                for (int x = 0; x < jars.length; x++) {
                    level.setBlockAndUpdate(origin.offset(x, 0, -7), (x % 2 == 0 ? Blocks.WHITE_CONCRETE : Blocks.BLACK_CONCRETE).defaultBlockState());
                    for (int row = 0; row < 4; row++) {
                        var state = jars[x].defaultBlockState();
                        if (state.hasProperty(JamJarBlock.PORTIONS)) state = state.setValue(JamJarBlock.PORTIONS, 4 - row);
                        level.setBlockAndUpdate(origin.offset(x, 0, -row * 2), state);
                    }
                }
                var player = server.getPlayerList().getPlayer(playerId);
                player.setYRot(180);
                player.setXRot(28);
                player.teleportTo(origin.getX() + 2.5, 252, origin.getZ() + 4);
            });
            return false;
        }
        minecraft.player.setYRot(180);
        minecraft.player.setXRot(28);
        if (worldTicks - startTick < 35) return false;
        for (int row = 0; row < 4; row++) for (int index = 0; index < jars.length; index++) {
            var pos = origin.offset(index, 0, -row * 2);
            var state = minecraft.level.getBlockState(pos);
            if (!state.is(jars[index])) {
                if (worldTicks - startTick < 200) return false;
                throw new IllegalStateException("Placed jar did not synchronize");
            }
            var model = minecraft.getModelManager().getBlockModelShaper().getBlockModel(state);
            var parts = new ArrayList<BlockModelPart>();
            model.collectParts(minecraft.level, pos, state, RandomSource.create(0), parts);
            var fill = parts.stream().flatMap(part -> part.getQuads(null).stream())
                    .filter(quad -> quad.tintIndex() == 0).toList();
            if (index == 0) {
                if (!fill.isEmpty()) throw new IllegalStateException("Empty jar contains jam");
            } else {
                if (state.getValue(JamJarBlock.PORTIONS) != 4 - row) throw new IllegalStateException("Jar fill count did not synchronize");
                int tint = minecraft.getBlockColors().getColor(state, minecraft.level, pos, 0);
                if ((tint >>> 24) != 160 || fill.size() != 6 || parts.stream().filter(part -> part.getQuads(null).stream().anyMatch(quad -> quad.tintIndex() == 0)).anyMatch(part -> part.getRenderType(state) != net.minecraft.client.renderer.chunk.ChunkSectionLayer.TRANSLUCENT)) {
                    throw new IllegalStateException("Jam fill lost alpha, geometry, or translucent rendering: " + jars[index]
                            + ", alpha=" + (tint >>> 24) + ", quads=" + fill.size()
                            + ", layers=" + parts.stream().map(part -> part.getRenderType(state)).toList());
                }
                double top = fill.stream().mapToDouble(quad -> Math.max(Math.max(quad.position0().y(), quad.position1().y()),
                        Math.max(quad.position2().y(), quad.position3().y()))).max().orElseThrow();
                double expected = (1 + (6 * (4 - row) + 3) / 4) / 16.0;
                if (Math.abs(top - expected) > 0.00001) throw new IllegalStateException("Jar fill height ignored portions: " + top + " vs " + expected);
            }
        }
        FloraExpansion.LOGGER.info("Jar render passed: four fill levels for every jam, translucent alpha 160, native-pixel geometry");
        return true;
    }
}
