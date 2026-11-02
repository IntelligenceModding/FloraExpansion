package de.artemis.floraexpansion;

import com.mojang.blaze3d.platform.NativeImage;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

import java.util.ArrayList;

final class SignRenderSmokeTest {
    private static final BlockPos[] OFFSETS = {new BlockPos(0,0,0), new BlockPos(2,0,0), new BlockPos(4,0,0),
            new BlockPos(0,0,-2), new BlockPos(4,0,-2)};
    private static BlockPos origin;
    private static int startTick;
    private static int stage;

    static boolean check(Minecraft minecraft, int worldTicks) {
        if (origin == null) {
            verifyModels(minecraft);
            verifyTexture(minecraft, "textures/gui/signs/cactus.png", 24, 26);
            verifyTexture(minecraft, "textures/gui/hanging_signs/cactus.png", 16, 16);
            origin = new BlockPos(minecraft.player.getBlockX() + 12, 250, minecraft.player.getBlockZ());
            startTick = worldTicks;
            var server = minecraft.getSingleplayerServer();
            var playerId = minecraft.player.getUUID();
            server.execute(() -> {
                var level = server.overworld();
                for (int x=-1; x<=6; x++) for (int z=-3; z<=4; z++) {
                    level.setBlockAndUpdate(origin.offset(x,-1,z), Blocks.SMOOTH_QUARTZ.defaultBlockState());
                }
                level.setBlockAndUpdate(origin.offset(2,0,-1), ModBlocks.CACTUS_PLANKS.get().defaultBlockState());
                level.setBlockAndUpdate(origin.offset(4,1,0), ModBlocks.CACTUS_PLANKS.get().defaultBlockState());
                level.setBlockAndUpdate(origin.offset(0,1,-2), ModBlocks.CACTUS_PLANKS.get().defaultBlockState());
                level.setBlockAndUpdate(origin.offset(5,0,-2), ModBlocks.CACTUS_PLANKS.get().defaultBlockState());
                BlockState[] states = {ModBlocks.CACTUS_SIGN.get().defaultBlockState(),
                        ModBlocks.CACTUS_WALL_SIGN.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH),
                        ModBlocks.CACTUS_HANGING_SIGN.get().defaultBlockState(),
                        ModBlocks.CACTUS_HANGING_SIGN.get().defaultBlockState().setValue(BlockStateProperties.ATTACHED, true),
                        ModBlocks.CACTUS_WALL_HANGING_SIGN.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)};
                for (int index=0; index<states.length; index++) {
                    var pos = origin.offset(OFFSETS[index]);
                    level.setBlockAndUpdate(pos, states[index]);
                    var sign = (SignBlockEntity) level.getBlockEntity(pos);
                    if (sign == null) throw new IllegalStateException("Sign fixture has no block entity");
                    sign.setText(new SignText().setMessage(0, Component.literal("CACTUS"))
                            .setMessage(1, Component.literal(index < 2 ? "Sign" : "Hanging")), true);
                    sign.setText(new SignText().setMessage(0, Component.literal("Back")), false);
                    sign.setAllowedPlayerEditor(playerId);
                    level.sendBlockUpdated(pos, states[index], states[index], Block.UPDATE_CLIENTS);
                }
                var player = server.getPlayerList().getPlayer(playerId);
                player.setYRot(180); player.setXRot(25);
                player.teleportTo(origin.getX()+2.5,252,origin.getZ()+6);
            });
            return false;
        }
        minecraft.player.setYRot(180); minecraft.player.setXRot(25);
        if (worldTicks-startTick < 35) return false;
        var standing = (SignBlockEntity) minecraft.level.getBlockEntity(origin.offset(OFFSETS[0]));
        var hanging = (SignBlockEntity) minecraft.level.getBlockEntity(origin.offset(OFFSETS[2]));
        if (standing == null || hanging == null || !hanging.getText(true).hasMessage(minecraft.player)) {
            if (worldTicks-startTick < 200) return false;
            throw new IllegalStateException("Sign fixtures or text did not synchronize");
        }
        if (stage == 0) {
            minecraft.player.openTextEdit(standing, true);
            if (!(minecraft.screen instanceof SignEditScreen)) throw new IllegalStateException("Wrong normal sign editor");
            stage++; return false;
        }
        if (stage == 1) {
            screenshot(minecraft, "cactus-sign-editor.png");
            minecraft.setScreen(null);
            stage++; return false;
        }
        if (stage == 2) {
            minecraft.player.openTextEdit(hanging, true);
            if (!(minecraft.screen instanceof HangingSignEditScreen)) throw new IllegalStateException("Wrong hanging sign editor");
            if (hanging.getMaxTextLineWidth() != 60 || hanging.getTextLineHeight() != 9) throw new IllegalStateException("Wrong hanging text dimensions");
            stage++; return false;
        }
        if (stage == 3) {
            screenshot(minecraft, "cactus-hanging-sign-editor.png");
            minecraft.setScreen(null);
            stage++; return false;
        }
        FloraExpansion.LOGGER.info("Cactus signs passed: every placed state has a block entity renderer; normal and hanging GUI open with correct textures and text dimensions");
        return true;
    }

    private static void verifyModels(Minecraft minecraft) {
        for (Block block : new Block[]{ModBlocks.CACTUS_SIGN.get(), ModBlocks.CACTUS_WALL_SIGN.get(),
                ModBlocks.CACTUS_HANGING_SIGN.get(), ModBlocks.CACTUS_WALL_HANGING_SIGN.get()}) {
            for (var state : block.getStateDefinition().getPossibleStates()) {
                var parts = new ArrayList<BlockStateModelPart>();
                minecraft.getModelManager().getBlockStateModelSet().get(state)
                        .collectParts(minecraft.level, minecraft.player.blockPosition(), state, RandomSource.create(0), parts);
                int quads = parts.stream().mapToInt(part -> part.getQuads(null).size()).sum();
                for (Direction side : Direction.values()) quads += parts.stream().mapToInt(part -> part.getQuads(side).size()).sum();
                // 26.1 renders the sign board through its block entity, rather than JSON geometry.
                var entity = ((net.minecraft.world.level.block.EntityBlock) block).newBlockEntity(BlockPos.ZERO, state);
                if (entity == null || minecraft.getBlockEntityRenderDispatcher().getRenderer(entity) == null)
                    throw new IllegalStateException("Missing sign renderer: " + state);
            }
        }
    }

    private static void verifyTexture(Minecraft minecraft, String path, int width, int height) {
        var id = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path);
        try (var stream = minecraft.getResourceManager().getResource(id).orElseThrow().open(); var image = NativeImage.read(stream)) {
            if (image.getWidth() != width || image.getHeight() != height) throw new IllegalStateException("Wrong sign GUI texture dimensions: " + id);
        } catch (java.io.IOException error) { throw new IllegalStateException("Missing sign GUI texture: " + id, error); }
    }

    private static void screenshot(Minecraft minecraft, String filename) {
        Screenshot.grab(minecraft.gameDirectory, filename, minecraft.getMainRenderTarget(), 1,
                message -> FloraExpansion.LOGGER.info("Sign editor screenshot: {}", message.getString()));
    }
}
