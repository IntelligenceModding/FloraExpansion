package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/** Captures the real chunk/BER render path rather than only sampling angle calculations. */
@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
final class CompatibilityMachineVisualAudit {
    private static volatile BlockPos origin;
    private static volatile boolean active;
    private static int serverTicks;
    private static final StringBuilder frames = new StringBuilder("tick,partial,angle,shapeAngle,turn,delta,elapsed\n");
    private static double lastTime = Double.NaN;
    private static float lastAngle;
    private static int jumps;
    @SubscribeEvent public static void frame(net.neoforged.neoforge.client.event.RenderLevelStageEvent event) {
        if (!active || event.getStage() != net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        var mc = Minecraft.getInstance();
        if (!(mc.level.getBlockEntity(origin.above()) instanceof de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity crank)) return;
        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        double now = de.artemis.floraexpansion.client.ClientAnimationClock.time(partial);
        float angle = crank.getAngle(partial);
        float delta = net.minecraft.util.Mth.wrapDegrees(angle - lastAngle);
        double elapsed = Double.isNaN(lastTime) ? 0 : now - lastTime;
        if (!Double.isNaN(lastTime) && (delta < -.1F || delta > Math.max(0, elapsed) * 34 + 1)) jumps++;
        frames.append(mc.level.getGameTime()).append(',').append(partial).append(',').append(angle).append(',')
                .append(crank.getShapeAngle()).append(',').append(crank.getBlockState().getValue(HandCrankBlock.TURN))
                .append(',').append(delta).append(',').append(elapsed).append('\n');
        lastTime = now; lastAngle = angle;
    }
    static void start(Minecraft mc) {
        mc.setScreen(null);
        mc.options.hideGui = true;
        var server = mc.getSingleplayerServer();
        var playerId = mc.player.getUUID();
        var base = mc.player.blockPosition().above(4);
        server.execute(() -> {
            var level = server.overworld();
            for (var pos : BlockPos.betweenClosed(base.offset(-5, -1, -3), base.offset(4, 3, 3)))
                level.setBlock(pos, pos.getY() == base.getY() - 1 ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
            for (var pos : BlockPos.betweenClosed(base.offset(-5, 0, -2), base.offset(4, 4, -2)))
                level.setBlock(pos, Blocks.SMOOTH_QUARTZ.defaultBlockState(), 3);
            level.setBlock(base, ModBlocks.HAND_MILL.get().defaultBlockState(), 3);
            level.setBlock(base.above(), ModBlocks.HAND_CRANK.get().defaultBlockState(), 3);
            ((de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity) level.getBlockEntity(base))
                    .setItem(0, new ItemStack(ModItems.RAW_SALT.get(), 64));
            level.setBlock(base.east(2), ModBlocks.HAND_CRANK.get().defaultBlockState(), 3);
            for (int age = 0; age < 3; age++) level.setBlock(base.west(4).south(age - 1),
                    ModBlocks.APPLE_LEAVES.get().defaultBlockState()
                            .setValue(de.artemis.floraexpansion.common.block.AppleLeavesBlock.AGE, age), 3);
            var player = server.getPlayerList().getPlayer(playerId);
            player.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);
            var root = server.getAdvancements().get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "nature/root"));
            root.value().criteria().keySet().forEach(criterion -> player.getAdvancements().award(root, criterion));
            player.connection.teleport(base.getX() + 3.7, base.getY() + 2.1, base.getZ() + 5.3, 145, 18);
            level.setDayTime(6000);
            origin = base;
            active = true;
        });
    }
    @SubscribeEvent public static void tickServer(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        if (!active || ++serverTicks % 10 != 0) return;
        var level = event.getServer().overworld();
        var player = net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);
        for (var pos : new BlockPos[]{origin.above(), origin.east(2)})
            level.getBlockState(pos).useWithoutItem(level, player,
                    new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }
    static void tick(Minecraft mc, int tick) {
        if (tick == 40 || tick == 42 || tick == 44 || tick == 60) {
            net.minecraft.client.Screenshot.grab(mc.gameDirectory, "machines-world-" + tick + ".png", mc.getMainRenderTarget(),
                    message -> FloraExpansion.LOGGER.info("Machine world screenshot: {}", message.getString()));
        }
        if (tick == 120) {
            active = false; mc.options.hideGui = false;
            try { java.nio.file.Files.writeString(mc.gameDirectory.toPath().resolve("machine-frame-angles.csv"), frames); }
            catch (java.io.IOException error) { throw new IllegalStateException(error); }
            FloraExpansion.LOGGER.info("Machine world frame audit: {} discontinuities", jumps);
            if (jumps != 0) throw new IllegalStateException("Real rendered machine frames jumped: " + jumps);
        }
    }
}
