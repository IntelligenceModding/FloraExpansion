package de.artemis.floraexpansion.common.util;

import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Y-rotated cuboids with exact edges, ray intersections and swept entity collision.
 * A small axis-aligned backing grid is a conservative broad phase for vanilla
 * voxel boolean queries, never the outline or narrow-phase movement collision.
 */
public final class RotatingVoxelShape extends VoxelShape {
    private static final double EPSILON = 1e-7;
    private final List<Part> parts;
    private final VoxelShape fixed, moving;
    private final double cos, sin;
    private final Grid grid;
    private final Vec3 offset;
    private final DoubleList xs, ys, zs;
    BitSetDiscreteVoxelShape discreteGrid() { return grid.cells; }

    public RotatingVoxelShape(VoxelShape fixed, VoxelShape moving, double angle) {
        this(fixed, moving, Math.cos(Math.toRadians(angle)), Math.sin(Math.toRadians(angle)));
    }
    /** Keep original solid parts when a voxel union would split them into smaller boxes. */
    public RotatingVoxelShape(VoxelShape fixed, VoxelShape moving, double angle, List<AABB> movingBoxes) {
        this(fixed, moving, Math.cos(Math.toRadians(angle)), Math.sin(Math.toRadians(angle)),
                parts(fixed, movingBoxes, Math.cos(Math.toRadians(angle)), Math.sin(Math.toRadians(angle))), Vec3.ZERO, null);
    }
    private RotatingVoxelShape(VoxelShape fixed, VoxelShape moving, double cos, double sin) {
        this(fixed, moving, cos, sin, parts(fixed, moving, cos, sin), Vec3.ZERO, null);
    }
    private RotatingVoxelShape(VoxelShape fixed, VoxelShape moving, double cos, double sin,
            List<Part> parts, Vec3 offset, @Nullable Grid existingGrid) {
        super((existingGrid == null ? existingGrid = grid(parts) : existingGrid).cells);
        this.fixed = fixed; this.moving = moving; this.cos = cos; this.sin = sin;
        this.parts = parts; this.offset = offset; this.grid = existingGrid;
        xs = shifted(grid.x, offset.x); ys = shifted(grid.y, offset.y); zs = shifted(grid.z, offset.z);
    }

    private record Part(AABB box, double cos, double sin, AABB bounds, VoxelShape localShape) {
        Part(AABB box, double cos, double sin) { this(box, cos, sin, rotatedBounds(box, cos, sin), Shapes.create(box)); }
        Vec3 local(Vec3 point) {
            double x = point.x - 0.5, z = point.z - 0.5;
            return new Vec3(0.5 + x * cos + z * sin, point.y, 0.5 - x * sin + z * cos);
        }
    }
    private static List<Part> parts(VoxelShape fixed, VoxelShape moving, double cos, double sin) {
        return parts(fixed, moving.toAabbs(), cos, sin);
    }
    private static List<Part> parts(VoxelShape fixed, List<AABB> movingBoxes, double cos, double sin) {
        var result = new ArrayList<Part>();
        fixed.forAllBoxes((x0, y0, z0, x1, y1, z1) -> result.add(new Part(new AABB(x0, y0, z0, x1, y1, z1), 1, 0)));
        for (var box : movingBoxes) result.add(new Part(box, cos, sin));
        return List.copyOf(result);
    }
    private static AABB rotatedBounds(AABB box, double cos, double sin) {
        double cx = (box.minX + box.maxX) / 2 - 0.5, cz = (box.minZ + box.maxZ) / 2 - 0.5;
        double hx = (box.maxX - box.minX) / 2, hz = (box.maxZ - box.minZ) / 2;
        double x = 0.5 + cx * cos - cz * sin, z = 0.5 + cx * sin + cz * cos;
        double rx = Math.abs(cos) * hx + Math.abs(sin) * hz, rz = Math.abs(sin) * hx + Math.abs(cos) * hz;
        return new AABB(x - rx, box.minY, z - rz, x + rx, box.maxY, z + rz);
    }

    private record Grid(BitSetDiscreteVoxelShape cells, DoubleList x, DoubleList y, DoubleList z) {}
    private static Grid grid(List<Part> parts) {
        double[] x = coordinates(parts, Direction.Axis.X), y = coordinates(parts, Direction.Axis.Y), z = coordinates(parts, Direction.Axis.Z);
        var cells = new BitSetDiscreteVoxelShape(x.length - 1, y.length - 1, z.length - 1);
        for (var part : parts) {
            var b = part.bounds;
            int x0 = Arrays.binarySearch(x, b.minX), x1 = Arrays.binarySearch(x, b.maxX);
            int y0 = Arrays.binarySearch(y, b.minY), y1 = Arrays.binarySearch(y, b.maxY);
            int z0 = Arrays.binarySearch(z, b.minZ), z1 = Arrays.binarySearch(z, b.maxZ);
            for (int i = x0; i < x1; i++) for (int j = y0; j < y1; j++) for (int k = z0; k < z1; k++) cells.fill(i, j, k);
        }
        return new Grid(cells, DoubleArrayList.wrap(x), DoubleArrayList.wrap(y), DoubleArrayList.wrap(z));
    }
    private static double[] coordinates(List<Part> parts, Direction.Axis axis) {
        return parts.stream().flatMapToDouble(part -> java.util.stream.DoubleStream.of(part.bounds.min(axis), part.bounds.max(axis)))
                .distinct().sorted().toArray();
    }
    @NotNull @Override public DoubleList getCoords(Direction.Axis axis) {
        return switch (axis) { case X -> xs; case Y -> ys; case Z -> zs; };
    }
    private static DoubleList shifted(DoubleList coords, double shift) {
        if (shift == 0) return coords;
        double[] shifted = coords.toDoubleArray();
        for (int i = 0; i < shifted.length; i++) shifted[i] += shift;
        return DoubleArrayList.wrap(shifted);
    }

    @Override public void forAllEdges(@NotNull Shapes.DoubleLineConsumer consumer) {
        fixed.forAllEdges((x0, y0, z0, x1, y1, z1) -> consumer.consume(
                x0 + offset.x, y0 + offset.y, z0 + offset.z, x1 + offset.x, y1 + offset.y, z1 + offset.z));
        moving.forAllEdges((x0, y0, z0, x1, y1, z1) -> consumer.consume(
                rotateX(x0, z0) + offset.x, y0 + offset.y, rotateZ(x0, z0) + offset.z,
                rotateX(x1, z1) + offset.x, y1 + offset.y, rotateZ(x1, z1) + offset.z));
    }
    private double rotateX(double x, double z) { return 0.5 + (x - 0.5) * cos - (z - 0.5) * sin; }
    private double rotateZ(double x, double z) { return 0.5 + (x - 0.5) * sin + (z - 0.5) * cos; }
    @Override public void forAllBoxes(@NotNull Shapes.DoubleLineConsumer consumer) {
        for (var part : parts) {
            var b = part.bounds;
            consumer.consume(b.minX + offset.x, b.minY + offset.y, b.minZ + offset.z,
                    b.maxX + offset.x, b.maxY + offset.y, b.maxZ + offset.z);
        }
    }
    @NotNull @Override public VoxelShape move(double x, double y, double z) {
        return new RotatingVoxelShape(fixed, moving, cos, sin, parts, offset.add(x, y, z), grid);
    }
    @Override public @Nullable BlockHitResult clip(@NotNull Vec3 from, @NotNull Vec3 to, @NotNull BlockPos pos) {
        Vec3 origin = Vec3.atLowerCornerOf(pos).add(offset);
        BlockHitResult closest = null;
        double distance = Double.POSITIVE_INFINITY;
        for (var part : parts) {
            var hit = part.localShape.clip(part.local(from.subtract(origin)), part.local(to.subtract(origin)), BlockPos.ZERO);
            if (hit == null) continue;
            Vec3 point = hit.getLocation();
            double x = point.x - 0.5, z = point.z - 0.5;
            Vec3 worldPoint = new Vec3(0.5 + x * part.cos - z * part.sin, point.y, 0.5 + x * part.sin + z * part.cos).add(origin);
            double current = worldPoint.distanceToSqr(from);
            if (current >= distance) continue;
            var normal = hit.getDirection();
            var direction = Direction.getNearest(normal.getStepX() * part.cos - normal.getStepZ() * part.sin,
                    normal.getStepY(), normal.getStepX() * part.sin + normal.getStepZ() * part.cos);
            closest = new BlockHitResult(worldPoint, direction, pos, hit.isInside());
            distance = current;
        }
        return closest;
    }
    @Override public double collide(@NotNull Direction.Axis axis, @NotNull AABB movingBox, double distance) {
        if (Math.abs(distance) < EPSILON) return 0;
        AABB local = movingBox.move(-offset.x, -offset.y, -offset.z);
        for (var part : parts) distance = sweep(part, local, axis, distance);
        return distance;
    }

    /** Continuous separating-axis test of a moving AABB against a Y-rotated box. */
    private static double sweep(Part part, AABB movingBox, Direction.Axis axis, double distance) {
        Vec3 center = movingBox.getCenter();
        double hx = movingBox.getXsize() / 2, hy = movingBox.getYsize() / 2, hz = movingBox.getZsize() / 2;
        Vec3 obstacle = part.box.getCenter();
        double bx = part.box.getXsize() / 2, by = part.box.getYsize() / 2, bz = part.box.getZsize() / 2;
        double ox = 0.5 + (obstacle.x - 0.5) * part.cos - (obstacle.z - 0.5) * part.sin;
        double oz = 0.5 + (obstacle.x - 0.5) * part.sin + (obstacle.z - 0.5) * part.cos;
        double dx = center.x - ox, dy = center.y - obstacle.y, dz = center.z - oz;
        double enter = Double.NEGATIVE_INFINITY, exit = Double.POSITIVE_INFINITY;
        for (int test = 0; test < 5; test++) {
            double nx = switch (test) { case 0 -> 1; case 2 -> part.cos; case 3 -> -part.sin; default -> 0; };
            double nz = switch (test) { case 1 -> 1; case 2 -> part.sin; case 3 -> part.cos; default -> 0; };
            double ny = test == 4 ? 1 : 0;
            double projected = dx * nx + dy * ny + dz * nz;
            double radius = hx * Math.abs(nx) + hy * ny + hz * Math.abs(nz)
                    + bx * Math.abs(nx * part.cos + nz * part.sin) + by * ny + bz * Math.abs(-nx * part.sin + nz * part.cos);
            double velocity = axis.choose(nx, ny, nz) * distance;
            if (Math.abs(velocity) < EPSILON) {
                if (Math.abs(projected) >= radius - EPSILON) return distance;
            } else {
                double a = (-radius - projected) / velocity, b = (radius - projected) / velocity;
                enter = Math.max(enter, Math.min(a, b)); exit = Math.min(exit, Math.max(a, b));
                if (enter > exit) return distance;
            }
        }
        // Permit escape from existing overlap instead of trapping entities.
        return enter >= -EPSILON && enter <= 1 && exit > EPSILON ? distance * Math.max(0, enter) : distance;
    }
}
