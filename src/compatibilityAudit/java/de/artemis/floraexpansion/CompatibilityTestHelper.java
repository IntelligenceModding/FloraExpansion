package de.artemis.floraexpansion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import java.util.ArrayList;
import java.util.List;

/** Runs the original interaction assertions against real 1.21.1 blocks and ticking hoppers. */
final class CompatibilityTestHelper {
    private final ServerLevel level;
    private final BlockPos origin;
    private final String name;
    private final List<Scheduled> scheduled = new ArrayList<>();
    private int tick;
    private boolean asynchronous;
    private boolean complete;
    private record Scheduled(int tick, Runnable action) {}
    CompatibilityTestHelper(ServerLevel level, BlockPos origin, String name) {
        this.level = level; this.origin = origin; this.name = name;
        for (int x = (origin.getX() - 16) >> 4; x <= (origin.getX() + 16) >> 4; x++)
            for (int z = (origin.getZ() - 16) >> 4; z <= (origin.getZ() + 16) >> 4; z++) level.setChunkForced(x, z, true);
        clearDrops();
        for (var pos : BlockPos.betweenClosed(origin.offset(0, -1, 0), origin.offset(24, 24, 24)))
            level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
    }
    void clearDrops() {
        level.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                new net.minecraft.world.phys.AABB(origin).expandTowards(28, 28, 28).inflate(2))
                .forEach(net.minecraft.world.entity.item.ItemEntity::discard);
    }
    ServerLevel getLevel() { return level; }
    BlockPos absolutePos(BlockPos relative) { return origin.offset(relative); }
    void setBlock(BlockPos relative, Block block) { setBlock(relative, block.defaultBlockState()); }
    void setBlock(BlockPos relative, BlockState state) { level.setBlockAndUpdate(absolutePos(relative), state); }
    FakePlayer makeMockPlayer(GameType mode) {
        var player = FakePlayerFactory.get(level, new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "AuditPlayer"));
        mode.updatePlayerAbilities(player.getAbilities());
        player.setPos(origin.getX() + .5, origin.getY() + 1, origin.getZ() + .5);
        return player;
    }
    void runAfterDelay(int delay, Runnable action) { asynchronous = true; scheduled.add(new Scheduled(tick + delay, action)); }
    void succeed() { complete = true; }
    void tick() {
        tick++;
        var due = scheduled.stream().filter(s -> s.tick <= tick).toList();
        scheduled.removeAll(due);
        for (var task : due) try { task.action.run(); } catch (Throwable error) { throw new IllegalStateException(name + " at tick " + tick, error); }
    }
    void verifyFinished() {
        if (!scheduled.isEmpty() || asynchronous && !complete) throw new IllegalStateException("Interaction test timed out: " + name);
    }
}
