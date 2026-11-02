package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Finds a naturally generated cave; never places the feature or prepares an artificial fixture. */
@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class SaltCaveSmokeTest {
    private static final boolean BRINE_PREVIEW = Boolean.getBoolean("floraexpansion.brineSpringSmokeTest");
    private static final long WORLD_SEED = Long.getLong("floraexpansion.brineSpringSeed", 12345L);
    // Check the neighborhood of the located cave, not thousands of distant chunks.
    private static final int SURFACE_SEARCH_RADIUS_CHUNKS = 12;
    private static boolean creating, searching, captured;
    private static volatile boolean ready;
    private static int ticks, readyTicks;
    private static volatile float yaw;

    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        if ((!Boolean.getBoolean("floraexpansion.saltCaveSmokeTest") && !BRINE_PREVIEW) || captured) return;
        var mc = Minecraft.getInstance();
        if (!creating && mc.getOverlay() == null && mc.screen instanceof net.minecraft.client.gui.screens.AccessibilityOnboardingScreen) {
            FloraExpansion.LOGGER.info("Natural spring test dismissing initial screen: {}", mc.screen);
            mc.setScreen(new TitleScreen());
            return;
        }
        if (!creating && mc.screen instanceof net.neoforged.neoforge.client.gui.LoadingErrorScreen loading && mc.getOverlay() == null) {
            String label = net.neoforged.fml.i18n.FMLTranslations.parseMessage("fml.button.continue.launch");
            var proceed = loading.children().stream().filter(child -> child instanceof net.minecraft.client.gui.components.Button button
                    && button.active && button.getMessage().getString().equals(label)).findFirst();
            if (proceed.isEmpty()) throw new IllegalStateException("Client startup has loading errors");
            ((net.minecraft.client.gui.components.Button) proceed.get()).onPress(null);
            return;
        }
        if (!creating && mc.screen instanceof TitleScreen && mc.getOverlay() == null) {
            creating = true;
            mc.createWorldOpenFlows().createFreshLevel("salt-cave-smoke-" + System.currentTimeMillis(),
                    new net.minecraft.world.level.LevelSettings("Salt cave preview", net.minecraft.world.level.GameType.CREATIVE, false,
                            net.minecraft.world.Difficulty.NORMAL, true,
                            new net.minecraft.world.level.gamerules.GameRules(net.minecraft.world.level.WorldDataConfiguration.DEFAULT.enabledFeatures()), net.minecraft.world.level.WorldDataConfiguration.DEFAULT),
                    new net.minecraft.world.level.levelgen.WorldOptions(WORLD_SEED, true, false),
                    net.minecraft.world.level.levelgen.presets.WorldPresets::createNormalWorldDimensions, mc.screen);
        }
        if (mc.level == null || mc.player == null || ++ticks < 100) return;
        if (!searching) {
            searching = true;
            var playerId = mc.player.getUUID();
            mc.getSingleplayerServer().execute(() -> {
                var level = mc.getSingleplayerServer().overworld();
                var player = mc.getSingleplayerServer().getPlayerList().getPlayer(playerId);
                // Keep the brine comparison on the same seed-12345 cave across runs;
                // the player's spawn position includes randomness.
                var searchOrigin = BRINE_PREVIEW
                        ? (WORLD_SEED == 12345L ? new BlockPos(617, 13, -259) : BlockPos.ZERO)
                        : player.blockPosition();
                var located = level.findClosestBiome3d(biome -> biome.is(ModBiomes.SALT_CAVES), searchOrigin, 6400, 32, 64);
                if (located == null) throw new IllegalStateException("/locate cannot find the Salt Caves biome");
                try {
                    mc.getSingleplayerServer().getCommands().getDispatcher().execute("locate biome floraexpansion:salt_caves",
                            mc.getSingleplayerServer().createCommandSourceStack().withPosition(player.position()));
                } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
                    throw new IllegalStateException("Salt Caves locate command failed", exception);
                }
                FloraExpansion.LOGGER.info("Salt Caves /locate biome returned {}", located.getFirst());
                int startX = located.getFirst().getX() >> 4, startZ = located.getFirst().getZ() >> 4;
                BlockPos pool = null, camera = null;
                int inspected = 0;
                search: for (int ring = 0; ring <= (BRINE_PREVIEW ? SURFACE_SEARCH_RADIUS_CHUNKS : 10); ring++) {
                    for (int dx = -ring; dx <= ring; dx++) {
                        for (int dz = -ring; dz <= ring; dz++) {
                            if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;
                            int cx = startX + dx, cz = startZ + dz;
                            var chunk = level.getChunk(cx, cz);
                            inspected++;
                            if (BRINE_PREVIEW && inspected % 64 == 0)
                                FloraExpansion.LOGGER.info("Surface brine search: {} natural chunks inspected", inspected);
                            for (int x = 0; x < 16; x++) {
                                for (int z = 0; z < 16; z++) {
                                    int surface = BRINE_PREVIEW ? level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, cx * 16 + x, cz * 16 + z) : 100;
                                    for (int y = BRINE_PREVIEW ? surface - 4 : level.getMinY() + 12; y <= surface; y++) {
                                        var pos = new BlockPos(cx * 16 + x, y, cz * 16 + z);
                                        if (!chunk.getFluidState(pos).is(ModFluids.SALT_WATER.get())
                                                || !level.getBlockState(pos.above()).isAir()
                                                || (BRINE_PREVIEW ? !level.canSeeSky(pos.above()) : !level.getBiome(pos).is(ModBiomes.SALT_CAVES))) continue;
                                        for (var direction : Direction.Plane.HORIZONTAL) {
                                            for (int distance = 10; distance >= 6; distance--) {
                                                var candidate = pos.relative(direction, distance).above(BRINE_PREVIEW ? 5 : 3);
                                                if (!level.getBlockState(candidate).isAir()
                                                        || !level.getBlockState(candidate.above()).isAir()
                                                        || (!BRINE_PREVIEW && !level.getBiome(candidate).is(ModBiomes.SALT_CAVES))) continue;
                                                pool = pos;
                                                camera = candidate;
                                                break search;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (pool == null) {
                    FloraExpansion.LOGGER.error("No natural {} found in {} chunks", BRINE_PREVIEW ? "surface brine spring" : "salt cave", inspected);
                    mc.execute(mc::stop);
                    throw new IllegalStateException("No natural " + (BRINE_PREVIEW ? "surface brine spring" : "salt cave") + " found in " + inspected + " chunks");
                }
                if (BRINE_PREVIEW) checkSurfaceDensity(level, pool);
                int spikes = 0, salt = 0;
                for (var pos : BlockPos.betweenClosed(pool.offset(-22, -8, -22), pool.offset(22, 17, 22))) {
                    var state = level.getBlockState(pos);
                    if (state.is(ModBlocks.SALT_SPIKE.get())) spikes++;
                    if (state.is(ModBlocks.CRYSTALLINE_SALT.get())) salt++;
                }
                if ((!BRINE_PREVIEW && spikes < 10) || salt < 30) throw new IllegalStateException("Natural formation missing salt or spikes");
                FloraExpansion.LOGGER.info("Natural {} found at {} after {} chunks: salt={}, spikes={}",
                        BRINE_PREVIEW ? "surface brine spring" : "salt cave", pool, inspected, salt, spikes);
                if (BRINE_PREVIEW) {
                    boolean saltBelow = false;
                    for (int y = pool.getY() - 5; y >= Math.max(level.getMinY() + 1, pool.getY() - 184); y--)
                        if (level.getBiome(new BlockPos(pool.getX(), y, pool.getZ())).is(ModBiomes.SALT_CAVES)) saltBelow = true;
                    if (!saltBelow) throw new IllegalStateException("Surface brine spring has no Salt Caves biome beneath it");
                }
                yaw = (float) Math.toDegrees(Math.atan2(camera.getX() - pool.getX(), pool.getZ() - camera.getZ()));
                player.teleportTo(camera.getX() + .5, camera.getY(), camera.getZ() + .5);
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.setYRot(yaw);
                player.setXRot(BRINE_PREVIEW ? 30 : 18);
                if (!BRINE_PREVIEW) player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0, false, false));
                ready = true;
            });
        }
        if (!ready) return;
        mc.player.setYRot(yaw);
        mc.player.setXRot(BRINE_PREVIEW ? 30 : 18);
        mc.player.getAbilities().flying = true;
        if (++readyTicks < 100) return;
        if (!BRINE_PREVIEW && !mc.level.getBiome(mc.player.blockPosition()).is(ModBiomes.SALT_CAVES)) {
            if (readyTicks == 100) {
                var clientPos = mc.player.blockPosition();
                FloraExpansion.LOGGER.info("Waiting for cave chunk sync: pos={}, loaded={}, clientBiome={}", clientPos, mc.level.hasChunkAt(clientPos), mc.level.getBiome(clientPos));
                mc.getSingleplayerServer().execute(() -> FloraExpansion.LOGGER.info("Server cave biome at {}: {}", clientPos, mc.getSingleplayerServer().overworld().getBiome(clientPos)));
            }
            if (readyTicks < 600) return;
            throw new IllegalStateException("Client/F3 does not identify Salt Caves after chunk synchronization");
        }
        captured = true;
        net.minecraft.client.Screenshot.grab(mc.gameDirectory, BRINE_PREVIEW ? "brine-spring.png" : "salt-cave.png", mc.getMainRenderTarget(), 1,
                message -> mc.execute(() -> { FloraExpansion.LOGGER.info("Salt cave client smoke test passed: {}", message.getString()); mc.stop(); }));
    }
    private static void checkSurfaceDensity(net.minecraft.server.level.ServerLevel level, BlockPos center) {
        var water = new java.util.HashSet<BlockPos>();
        for (int x = center.getX() - 64; x < center.getX() + 64; x++) {
            for (int z = center.getZ() - 64; z < center.getZ() + 64; z++) {
                int surface = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                for (int y = surface - 4; y <= surface; y++) {
                    var pos = new BlockPos(x, y, z);
                    if (level.getFluidState(pos).is(ModFluids.SALT_WATER.get()) && level.canSeeSky(pos.above())) water.add(pos);
                }
            }
        }
        int pools = 0;
        while (!water.isEmpty()) {
            pools++;
            var queue = new java.util.ArrayDeque<BlockPos>();
            var start = water.iterator().next();
            water.remove(start);
            queue.add(start);
            while (!queue.isEmpty()) {
                var pos = queue.removeFirst();
                // Treat different depths of the same pool as one surface formation.
                for (int dx = -1; dx <= 1; dx++) for (int dz = -1; dz <= 1; dz++) for (int dy = -2; dy <= 2; dy++) {
                    var next = pos.offset(dx, dy, dz);
                    if (water.remove(next)) queue.addLast(next);
                }
            }
        }
        FloraExpansion.LOGGER.info("Natural spring density: {} pools in 128x128 blocks, seed={}, center={}", pools, WORLD_SEED, center);
        if (pools < 1 || pools > 3) throw new IllegalStateException("Expected 1-3 surface springs in a 128x128 area, got " + pools);
    }
}
