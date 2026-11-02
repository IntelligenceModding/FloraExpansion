package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.util.RotatingVoxelShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Model geometry, in pixels. Rotation is continuous, without slices or angle tables. */
public final class HandMillShapes {
    private static final VoxelShape BASE = Shapes.or(Block.box(0, 0, 0, 16, 4, 16),
            Block.box(2, 4, 2, 14, 6, 14));
    private static final VoxelShape RUNNER_STONE = Block.box(3, 6.25, 3, 13, 11.5, 13);
    private static final VoxelShape SHAFT = Block.box(7, 6, 7, 9, 16, 9);
    private static final VoxelShape RUNNER = Shapes.or(RUNNER_STONE, SHAFT);
    private static final java.util.List<net.minecraft.world.phys.AABB> RUNNER_BOXES = java.util.List.of(RUNNER_STONE.bounds(), SHAFT.bounds());
    private static final VoxelShape HANDLE = Shapes.or(Block.box(7, 0, 7, 9, 7, 9),
            Block.box(9, 5, 7, 14, 7, 9), Block.box(12, 7, 7, 14, 11, 9));
    private static final VoxelShape CRANK_BASE = Block.box(4, 0, 4, 12, 2, 12);
    private static final VoxelShape RESTING_MILL = mill(0);

    public static VoxelShape restingMill() { return RESTING_MILL; }
    public static VoxelShape mill(float angle) { return new RotatingVoxelShape(BASE, RUNNER, angle, RUNNER_BOXES); }
    public static VoxelShape crank(float angle) { return new RotatingVoxelShape(Shapes.empty(), HANDLE, angle); }
    public static VoxelShape crank(float angle, boolean standalone) {
        return new RotatingVoxelShape(standalone ? CRANK_BASE : Shapes.empty(), HANDLE, angle);
    }
    public static VoxelShape crank(float angle, boolean standalone, net.minecraft.core.Direction facing) {
        angle += de.artemis.floraexpansion.common.util.CrankOrientation.of(facing).restingAngle();
        var shape = new RotatingVoxelShape(standalone ? CRANK_BASE : Shapes.empty(), HANDLE, angle);
        return facing == net.minecraft.core.Direction.UP ? shape : new de.artemis.floraexpansion.common.util.MountedVoxelShape(
                shape, de.artemis.floraexpansion.common.util.CrankOrientation.of(facing));
    }
    private HandMillShapes() {}
}
