package de.artemis.floraexpansion.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class ModUtils {

    private static final double SPAWN_OFFSET = 0.62D;
    private static final double OUTWARD_MOTION = 0.04D;

    private ModUtils() {
    }

    public static void spawnItemAtClickedSide(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockHitResult hitResult, @NotNull ItemStack itemStack) {
        spawnItemAtClickedSide(level, blockPos, hitResult.getDirection(), itemStack);
    }

    public static void spawnItemAtClickedSide(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction face, @NotNull ItemStack itemStack) {
        if (itemStack.isEmpty() || level.isClientSide) {
            return;
        }

        Vec3 center = Vec3.atCenterOf(blockPos);

        double x = center.x + face.getStepX() * SPAWN_OFFSET;
        double y = center.y + face.getStepY() * SPAWN_OFFSET;
        double z = center.z + face.getStepZ() * SPAWN_OFFSET;

        spawnItem(level, x, y, z, itemStack,
                face.getStepX() * OUTWARD_MOTION,
                face == Direction.UP ? OUTWARD_MOTION : 0.0D,
                face.getStepZ() * OUTWARD_MOTION
        );
    }

    public static void spawnCenteredItem(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull ItemStack itemStack) {
        spawnCenteredItem(level, blockPos, itemStack, 0.0D, 0.0D, 0.0D);
    }

    public static void spawnCenteredItem(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull ItemStack itemStack,
                                         double motionX, double motionY, double motionZ) {
        if (itemStack.isEmpty() || level.isClientSide) {
            return;
        }

        Vec3 center = Vec3.atCenterOf(blockPos);
        spawnItem(level, center.x, center.y, center.z, itemStack, motionX, motionY, motionZ);
    }

    public static void awardBlockMinedStat(@NotNull Player player, @NotNull Block block) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.awardStat(Stats.BLOCK_MINED.get(block));
        }
    }

    private static void spawnItem(@NotNull Level level, double x, double y, double z, @NotNull ItemStack itemStack,
                                  double motionX, double motionY, double motionZ) {
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack.copy());
        itemEntity.setDeltaMovement(motionX, motionY, motionZ);
        level.addFreshEntity(itemEntity);
    }
}