package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.HandMillShapes;
import de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class HandMillShapeTests {
    public static void animatedShapes(GameTestHelper helper) {
        orientedGeometry();
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(1, 2, 1));
        level.setBlockAndUpdate(pos, ModBlocks.HAND_MILL.get().defaultBlockState());
        level.setBlockAndUpdate(pos.above(), ModBlocks.HAND_CRANK.get().defaultBlockState());
        var state = level.getBlockState(pos);
        var initial = state.getCollisionShape(level, pos);
        check(contains(initial, 0.02, 0.1, 0.02), "Mill base lost collision");
        check(!contains(initial, 0.02, 0.5, 0.02), "Mill still has full-block collision above the base");
        check(contains(initial, 0.5, 0.9, 0.5), "Mill shaft lost collision");
        check(contains(initial, 0.75, 0.5, 0.5), "Runner lost collision");
        check(!contains(initial, 0.9, 0.5, 0.5) && contains(HandMillShapes.mill(45), 0.9, 0.5, 0.5),
                "Runner collision does not follow its rotated corners");
        check(initial.clip(new Vec3(pos.getX() + 0.02, pos.getY() + 0.5, pos.getZ() - 1),
                new Vec3(pos.getX() + 0.02, pos.getY() + 0.5, pos.getZ() + 2), pos) == null,
                "Selection still hits empty space beside the runner");
        var crankPos = pos.above();
        var crankState = level.getBlockState(crankPos);
        check(crankState.canSurvive(level, crankPos), "Crank cannot attach to shaped mill");
        // An empty mill does not turn, so exercise the same turn notification after a state advance.
        var turned = crankState.setValue(de.artemis.floraexpansion.common.block.HandCrankBlock.TURN, 1);
        level.setBlockAndUpdate(crankPos, turned);
        var crank = (HandCrankBlockEntity) level.getBlockEntity(crankPos);
        crank.turn();
        check(Math.abs(crank.getShapeAngle()) < 0.01, "Server crank collision snapped to its final rotation");
        helper.runAfterDelay(2, () -> {
            float angle = crank.getShapeAngle();
            check(angle > 0 && angle < 90, "Server crank collision did not animate");
            double radians = Math.toRadians(angle);
            double x = 0.5 + 5.0 / 16 * Math.cos(radians), z = 0.5 + 5.0 / 16 * Math.sin(radians);
            var shape = turned.getCollisionShape(level, crankPos);
            check(contains(shape, x, 0.6, z), "Animated grip collision does not follow the handle");
            check(!contains(shape, 0.8125, 0.6, 0.5), "Animated grip left stale collision at its starting point");
            check(shape == turned.getShape(level, crankPos), "Crank selection and collision differ");
            check(state.getCollisionShape(level, pos) == state.getShape(level, pos), "Mill selection and collision differ");
        });
        helper.runAfterDelay(10, () -> {
            check(Math.abs(crank.getShapeAngle() - 90) < 0.01, "Crank collision did not finish its turn");
            check(contains(turned.getCollisionShape(level, crankPos), 0.5, 0.6, 0.8125), "Final grip collision is incorrect");
            helper.succeed();
        });
    }

    private static void orientedGeometry() {
        var shaftRayStart = new Vec3(0.58, 2, 0.5);
        var shaftRayEnd = new Vec3(0.58, 0.8, 0.5);
        check(HandMillShapes.mill(0).clip(shaftRayStart, shaftRayEnd, BlockPos.ZERO) == null
                        && HandMillShapes.mill(45).clip(shaftRayStart, shaftRayEnd, BlockPos.ZERO) != null,
                "Mill connecting shaft does not rotate with the crank");
        var cube = net.minecraft.world.phys.shapes.Shapes.box(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
        var shape = new de.artemis.floraexpansion.common.util.RotatingVoxelShape(net.minecraft.world.phys.shapes.Shapes.empty(), cube, 45);
        int[] edges = {0};
        shape.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            edges[0]++;
            double length = new Vec3(x0, y0, z0).distanceTo(new Vec3(x1, y1, z1));
            check(Math.abs(length - 0.5) < 1e-9, "Rotated outline edge changed length or became a staircase");
        });
        check(edges[0] == 12, "A rotated cube must retain exactly twelve straight edges");
        check(shape.clip(new Vec3(0.8, 2, 0.8), new Vec3(0.8, -1, 0.8), BlockPos.ZERO) == null,
                "Ray hit the empty corner of a rotated box's bounds");
        var hit = shape.clip(new Vec3(0.5, 2, 0.5), new Vec3(0.5, -1, 0.5), BlockPos.ZERO);
        check(hit != null && Math.abs(hit.getLocation().y - 0.75) < 1e-9, "Ray missed the rotated box's real surface");
        var emptyCorner = new net.minecraft.world.phys.AABB(0.79, 1, 0.79, 0.81, 1.1, 0.81);
        check(shape.collide(net.minecraft.core.Direction.Axis.Y, emptyCorner, -2) == -2,
                "Movement collided with empty space inside the broad-phase bounds");
        var above = new net.minecraft.world.phys.AABB(0.49, 1, 0.49, 0.51, 1.1, 0.51);
        check(Math.abs(shape.collide(net.minecraft.core.Direction.Axis.Y, above, -2) + 0.25) < 1e-9,
                "Downward collision did not stop on the actual surface");
        double expected = 0.6 - Math.sqrt(0.125);
        var left = new net.minecraft.world.phys.AABB(-0.2, 0.4, 0.49, -0.1, 0.6, 0.51);
        var right = new net.minecraft.world.phys.AABB(1.1, 0.4, 0.49, 1.2, 0.6, 0.51);
        check(Math.abs(shape.collide(net.minecraft.core.Direction.Axis.X, left, 1) - expected) < 1e-9,
                "Swept collision missed the rotated corner");
        check(Math.abs(shape.collide(net.minecraft.core.Direction.Axis.X, right, -1) + expected) < 1e-9,
                "Negative movement collision is incorrect");
        var moved = shape.move(100, 20, -30);
        check(Math.abs(moved.collide(net.minecraft.core.Direction.Axis.X, left.move(100, 20, -30), 1) - expected) < 1e-9,
                "World translation lost the custom collision behavior");
        check(moved.clip(new Vec3(100.5, 22, -29.5), new Vec3(100.5, 19, -29.5), BlockPos.ZERO) != null,
                "World translation lost exact ray intersection");
        var first = new double[1]; var second = new double[1];
        new de.artemis.floraexpansion.common.util.RotatingVoxelShape(net.minecraft.world.phys.shapes.Shapes.empty(), cube, 10.001)
                .forAllEdges((x0, y0, z0, x1, y1, z1) -> first[0] = x0);
        new de.artemis.floraexpansion.common.util.RotatingVoxelShape(net.minecraft.world.phys.shapes.Shapes.empty(), cube, 10.002)
                .forAllEdges((x0, y0, z0, x1, y1, z1) -> second[0] = x0);
        check(first[0] != second[0], "Shape angle is still quantized");
        int maxEdges = 0, maxBoxes = 0;
        for (int angle = 0; angle < 90; angle++) {
            var standalone = HandMillShapes.crank(angle, true);
            check(standalone.clip(new Vec3(0.26, 0.2, 0.26), new Vec3(0.26, 0.01, 0.26), BlockPos.ZERO) != null,
                    "Standalone stone base rotated with the crank");
            for (var animated : java.util.List.of(HandMillShapes.mill(angle), HandMillShapes.crank(angle), standalone)) {
                int count = animated.toAabbs().size();
                check(count <= 6, "Machine generated a dense collision mesh");
                int[] lines = {0};
                animated.forAllEdges((x0, y0, z0, x1, y1, z1) -> lines[0]++);
                check(lines[0] <= 72, "Machine generated excessive outline edges");
                maxEdges = Math.max(maxEdges, lines[0]); maxBoxes = Math.max(maxBoxes, count);
            }
        }
        FloraExpansion.LOGGER.info("Rotating shape geometry verified across 90 angles: at most {} broad-phase boxes and {} straight outline edges per machine", maxBoxes, maxEdges);
    }

    private static boolean contains(VoxelShape shape, double x, double y, double z) {
        return shape.toAabbs().stream().anyMatch(box -> box.contains(x, y, z));
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
    private HandMillShapeTests() {}
}
