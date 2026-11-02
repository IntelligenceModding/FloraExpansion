package de.artemis.floraexpansion;

import de.artemis.floraexpansion.client.screen.HandMillScreen;
import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class HandMillRenderSmokeTest {
    private static boolean creating, prepared, requestedWorld, capturedWorld, requestedGui;
    private static boolean rotationRequested, interpolated, crankInterpolated, millingParticlesSeen;
    private static boolean requestedAnimatedWorld, capturedAnimatedWorld;
    private static int ticks;
    private static BlockPos pos;
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        if (!Boolean.getBoolean("floraexpansion.handMillRenderSmokeTest")) return;
        var mc = Minecraft.getInstance();
        if (!creating && mc.gui.screen() instanceof TitleScreen && mc.gui.overlay() == null) {
            creating = true;
            mc.createWorldOpenFlows().createFreshLevel("mill-smoke-" + System.currentTimeMillis(),
                    new net.minecraft.world.level.LevelSettings("Hand Mill preview", net.minecraft.world.level.GameType.CREATIVE,
                            net.minecraft.world.level.LevelSettings.DifficultySettings.DEFAULT, true,
                            net.minecraft.world.level.WorldDataConfiguration.DEFAULT),
                    new net.minecraft.world.level.levelgen.WorldOptions(12345L, true, false),
                    net.minecraft.world.level.levelgen.presets.WorldPresets::createNormalWorldDimensions, mc.gui.screen());
        }
        if (mc.level == null || mc.player == null || ++ticks < 100) return;
        if (!prepared) {
            prepared = true;
            pos = new BlockPos(mc.player.getBlockX(), 250, mc.player.getBlockZ());
            var id = mc.player.getUUID();
            mc.getSingleplayerServer().execute(() -> {
                var server = mc.getSingleplayerServer();
                var level = server.overworld();
                for (var floor : BlockPos.betweenClosed(pos.offset(-4, -2, -4), pos.offset(4, -2, 5))) {
                    level.setBlockAndUpdate(floor, Blocks.SMOOTH_STONE.defaultBlockState());
                }
                level.setBlockAndUpdate(pos.below(), Blocks.SMOOTH_STONE.defaultBlockState());
                level.setBlockAndUpdate(pos, ModBlocks.HAND_MILL.get().defaultBlockState());
                level.setBlockAndUpdate(pos.above(), ModBlocks.HAND_CRANK.get().defaultBlockState().setValue(HandCrankBlock.TURN, 1));
                var player = server.getPlayerList().getPlayer(id);
                var counterPos = pos.offset(2, 0, 0);
                level.setBlockAndUpdate(counterPos.below(), Blocks.SMOOTH_STONE.defaultBlockState());
                level.setBlockAndUpdate(counterPos, ModBlocks.HAND_CRANK.get().defaultBlockState().setValue(HandCrankBlock.STRENGTH_MODE, true));
                for (int turn = 0; turn < 12; turn++) {
                    level.getBlockState(counterPos).useWithoutItem(level, player,
                            new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(counterPos),
                                    net.minecraft.core.Direction.UP, counterPos, false));
                }
                ((de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity) level.getBlockEntity(counterPos))
                        .setNextDecay(level.getGameTime() + 1000);
                var mill = (HandMillBlockEntity) level.getBlockEntity(pos);
                mill.setItem(0, new ItemStack(ModItems.RAW_SALT.get(), 12));
                mill.setItem(1, new ItemStack(ModItems.SALT.get(), 3));
                mill.crank(player);
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.setYRot(145);
                player.setXRot(6);
                player.teleportTo(pos.getX() + 3, 249.35, pos.getZ() + 4);
            });
            ticks = 100;
            return;
        }
        if (mc.gui.screen() == null) {
            mc.player.setYRot(145);
            mc.player.setXRot(6);
        }
        if (ticks < 180) return;
        if (!rotationRequested) {
            if (!(mc.level.getBlockEntity(pos) instanceof HandMillBlockEntity)) return;
            rotationRequested = true;
            mc.options.particles().set(net.minecraft.server.level.ParticleStatus.ALL);
            mc.particleEngine.clearParticles();
            var id = mc.player.getUUID();
            mc.getSingleplayerServer().execute(() -> {
                var server = mc.getSingleplayerServer();
                var level = server.overworld();
                var player = server.getPlayerList().getPlayer(id);
                level.getBlockState(pos.above()).useWithoutItem(level, player,
                        new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos.above()),
                                net.minecraft.core.Direction.UP, pos.above(), false));
            });
            ticks = 180;
            return;
        }
        if (mc.level.getBlockEntity(pos) instanceof HandMillBlockEntity mill) {
            float stoneStep = mill.getMillstoneAngle(1) - mill.getMillstoneAngle(0);
            float crankStep = mill.getCrankAngle(1) - mill.getCrankAngle(0);
            if (stoneStep > 0.01F) {
                interpolated = true;
                if (crankStep > 0.01F && Math.abs(stoneStep - crankStep) < 0.01F) crankInterpolated = true;
                var crank = (de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity) mc.level.getBlockEntity(pos.above());
                float partial = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
                if (Math.abs(crank.getShapeAngle() - crank.getAngle(partial)) > 0.001F)
                    throw new IllegalStateException("Client hitbox animation differs from rendered crank timing");
                if (mc.level.getBlockState(pos).getShape(mc.level, pos)
                        != crank.getMillShape())
                    throw new IllegalStateException("Mill outline does not follow the interpolated runner");
                if (mc.level.getBlockState(pos.above()).getShape(mc.level, pos.above())
                        != crank.getCrankShape())
                    throw new IllegalStateException("Crank outline does not follow the interpolated handle");
                float angle = (crank.getAngle(partial) % 90 + 90) % 90;
                if (!requestedAnimatedWorld && angle > 20 && angle < 70) {
                    requestedAnimatedWorld = true;
                    net.minecraft.client.Screenshot.grab(mc.gameDirectory, "hand-mill-rotating.png", mc.gameRenderer.mainRenderTarget(), 1,
                            message -> mc.execute(() -> capturedAnimatedWorld = true));
                }
            }
        }
        String particleCounts = mc.particleEngine.countParticles();
        if (Integer.parseInt(particleCounts.substring(particleCounts.lastIndexOf("T ") + 2)) > 0) millingParticlesSeen = true;
        if (ticks < 205) return;
        if (!millingParticlesSeen) throw new IllegalStateException("Successful milling did not create particles on the client");
        if (!crankInterpolated) throw new IllegalStateException("Crank did not interpolate together with the millstone");
        if (!interpolated) throw new IllegalStateException("Millstone rotation did not synchronize/interpolate on the client");
        if (!capturedAnimatedWorld) throw new IllegalStateException("Animated outline preview was not captured");
        if (!requestedWorld) {
            var mill = (HandMillBlockEntity) mc.level.getBlockEntity(pos);
            var renderer = mc.getBlockEntityRenderDispatcher().<HandMillBlockEntity, de.artemis.floraexpansion.client.renderer.state.HandMillRenderState>getRenderer(mill);
            if (!(renderer instanceof de.artemis.floraexpansion.client.renderer.HandMillBlockEntityRenderer millRenderer)) {
                throw new IllegalStateException("Millstone renderer missing");
            }
            var crank = (de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity) mc.level.getBlockEntity(pos.above());
            var crankRenderer = mc.getBlockEntityRenderDispatcher()
                    .<de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity, de.artemis.floraexpansion.client.renderer.state.HandCrankRenderState>getRenderer(crank);
            if (!(crankRenderer instanceof de.artemis.floraexpansion.client.renderer.HandCrankBlockEntityRenderer))
                throw new IllegalStateException("Standalone crank renderer missing");
            var attachedState = crankRenderer.createRenderState();
            crankRenderer.extractRenderState(crank, attachedState, 0, mc.player.position(), null);
            if (attachedState.hasBase) throw new IllegalStateException("Mill-mounted crank rendered a standalone base");
            var counter = (de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity) mc.level.getBlockEntity(pos.offset(2, 0, 0));
            var counterRenderer = mc.getBlockEntityRenderDispatcher()
                    .<de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity, de.artemis.floraexpansion.client.renderer.state.HandCrankRenderState>getRenderer(counter);
            var counterState = counterRenderer.createRenderState();
            counterRenderer.extractRenderState(counter, counterState, 0, mc.player.position(), null);
            if (!counterState.strengthMode || counterState.signal != 12 || !counterState.hasBase)
                throw new IllegalStateException("Crank number display did not synchronize mode and signal strength");
            var renderState = millRenderer.createRenderState();
            millRenderer.extractRenderState(mill, renderState, 0, mc.player.position(), null);

            if (net.minecraft.util.LightCoordsUtil.sky(renderState.lightCoords) < 14) {
                throw new IllegalStateException("Daylight millstone rendered with cave lighting");
            }
            if (!mc.level.getBlockState(pos.above()).is(ModBlocks.HAND_CRANK.get())) return;
            var models = mc.getModelManager().getBlockStateModelSet();
            for (var block : java.util.List.of(ModBlocks.HAND_MILL.get(), ModBlocks.HAND_CRANK.get(),
                    ModBlocks.CRYSTALLINE_SALT_STAIRS.get(), ModBlocks.CRYSTALLINE_SALT_SLAB.get(), ModBlocks.CRYSTALLINE_SALT_WALL.get())) {
                for (var state : block.getStateDefinition().getPossibleStates()) {
                    if (models.get(state) == models.missingModel()) throw new IllegalStateException("Missing mill/crank model");
                }
            }
            requestedWorld = true;
            net.minecraft.client.Screenshot.grab(mc.gameDirectory, "hand-mill-blocks.png", mc.gameRenderer.mainRenderTarget(), 1,
                    message -> mc.execute(() -> capturedWorld = true));
            return;
        }
        if (!capturedWorld) return;
        if (!(mc.gui.screen() instanceof HandMillScreen)) {
            if (ticks % 20 == 0) {
                var id = mc.player.getUUID();
                mc.getSingleplayerServer().execute(() -> {
                    var player = mc.getSingleplayerServer().getPlayerList().getPlayer(id);
                    player.openMenu((HandMillBlockEntity) mc.getSingleplayerServer().overworld().getBlockEntity(pos));
                });
            }
            return;
        }
        if (ticks < 220 || requestedGui) return;
        var screen = (HandMillScreen) mc.gui.screen();
        if (screen.getMenu().getProgress() != 2 || screen.getMenu().slots.get(0).getItem().getCount() != 12
                || screen.getMenu().slots.get(1).getItem().getCount() != 3) throw new IllegalStateException("GUI inventory/progress did not synchronize");
        if (screen.getMenu().getRequiredTurns() != 4
                || !screen.getMenu().slots.get(0).mayPlace(new ItemStack(net.minecraft.world.item.Items.OAK_LOG))
                || de.artemis.floraexpansion.client.recipe.GrindingClientRecipes.get().size() < 2) {
            throw new IllegalStateException("Grinding recipes, accepted inputs or turn count did not synchronize");
        }
        requestedGui = true;
        net.minecraft.client.Screenshot.grab(mc.gameDirectory, "hand-mill-inventory.png", mc.gameRenderer.mainRenderTarget(), 1,
                message -> mc.execute(() -> {
                    FloraExpansion.LOGGER.info("Hand Mill client preview passed: standalone crank signal display, smooth shared rotation, particles, daylight and inventory");
                    mc.stop();
                }));
    }
}
