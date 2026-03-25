package de.artemis.floraexpansion.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class ModUtils {

    private static final double SPAWN_OFFSET = 0.62D;
    private static final double OUTWARD_MOTION = 0.04D;

    private ModUtils() {
    }

    public static void spawnItemAtClickedSide(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockHitResult hitResult, @NotNull ItemStack itemStack) {
        if (itemStack.isEmpty() || level.isClientSide) {
            return;
        }

        Direction face = hitResult.getDirection();
        Vec3 center = Vec3.atCenterOf(blockPos);

        double x = center.x + face.getStepX() * SPAWN_OFFSET;
        double y = center.y + face.getStepY() * SPAWN_OFFSET;
        double z = center.z + face.getStepZ() * SPAWN_OFFSET;

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack.copy());

        itemEntity.setDeltaMovement(
                face.getStepX() * OUTWARD_MOTION,
                face == Direction.UP ? OUTWARD_MOTION : 0.0D,
                face.getStepZ() * OUTWARD_MOTION
        );

        level.addFreshEntity(itemEntity);
    }

    public static void spawnItemAtClickedSide(@NotNull Level level, @NotNull BlockPos blockPos, @NotNull Direction face, @NotNull ItemStack itemStack) {
        if (itemStack.isEmpty() || level.isClientSide) {
            return;
        }

        Vec3 center = Vec3.atCenterOf(blockPos);

        double x = center.x + face.getStepX() * SPAWN_OFFSET;
        double y = center.y + face.getStepY() * SPAWN_OFFSET;
        double z = center.z + face.getStepZ() * SPAWN_OFFSET;

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack.copy());

        itemEntity.setDeltaMovement(
                face.getStepX() * OUTWARD_MOTION,
                face == Direction.UP ? OUTWARD_MOTION : 0.0D,
                face.getStepZ() * OUTWARD_MOTION
        );

        level.addFreshEntity(itemEntity);
    }
}