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

/** Mounts an animated shape on another face without voxelizing its rotated geometry. */
public final class MountedVoxelShape extends VoxelShape {
    private final RotatingVoxelShape source;
    private final CrankOrientation orientation;
    private final Vec3 offset;
    private final Grid grid;
    private final DoubleList xs, ys, zs;
    private record Grid(BitSetDiscreteVoxelShape cells, DoubleList x, DoubleList y, DoubleList z) {}

    public MountedVoxelShape(RotatingVoxelShape source, CrankOrientation orientation) {
        this(source, orientation, Vec3.ZERO, grid(source, orientation));
    }
    private MountedVoxelShape(RotatingVoxelShape source, CrankOrientation orientation, Vec3 offset, Grid grid) {
        super(grid.cells);
        this.source = source; this.orientation = orientation; this.offset = offset; this.grid = grid;
        xs = shifted(grid.x, offset.x); ys = shifted(grid.y, offset.y); zs = shifted(grid.z, offset.z);
    }
    private static Grid grid(RotatingVoxelShape source, CrankOrientation orientation) {
        DoubleList x = coords(source, orientation, Direction.Axis.X);
        DoubleList y = coords(source, orientation, Direction.Axis.Y);
        DoubleList z = coords(source, orientation, Direction.Axis.Z);
        var cells = new BitSetDiscreteVoxelShape(x.size() - 1, y.size() - 1, z.size() - 1);
        var original = source.discreteGrid();
        for (int i = 0; i < original.getXSize(); i++) for (int j = 0; j < original.getYSize(); j++) for (int k = 0; k < original.getZSize(); k++) {
            if (!original.isFull(i, j, k)) continue;
            cells.fill(index(orientation, Direction.Axis.X, i, j, k, x.size() - 1),
                    index(orientation, Direction.Axis.Y, i, j, k, y.size() - 1),
                    index(orientation, Direction.Axis.Z, i, j, k, z.size() - 1));
        }
        return new Grid(cells, x, y, z);
    }
    private static int index(CrankOrientation orientation, Direction.Axis world, int x, int y, int z, int size) {
        int local = orientation.localAxis(world).choose(x, y, z);
        return orientation.sign(world) > 0 ? local : size - 1 - local;
    }
    private static DoubleList coords(RotatingVoxelShape source, CrankOrientation orientation, Direction.Axis world) {
        var original = source.getCoords(orientation.localAxis(world));
        if (orientation.sign(world) > 0) return original;
        double[] reversed = new double[original.size()];
        for (int i = 0; i < reversed.length; i++) reversed[i] = 1 - original.getDouble(reversed.length - 1 - i);
        return DoubleArrayList.wrap(reversed);
    }
    private static DoubleList shifted(DoubleList coords, double offset) {
        if (offset == 0) return coords;
        double[] result = coords.toDoubleArray();
        for (int i = 0; i < result.length; i++) result[i] += offset;
        return DoubleArrayList.wrap(result);
    }
    @NotNull @Override public DoubleList getCoords(Direction.Axis axis) { return switch (axis) { case X -> xs; case Y -> ys; case Z -> zs; }; }
    @Override public void forAllEdges(@NotNull Shapes.DoubleLineConsumer consumer) {
        source.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
            var a = orientation.toWorld(new Vec3(x0, y0, z0)).add(offset);
            var b = orientation.toWorld(new Vec3(x1, y1, z1)).add(offset);
            consumer.consume(a.x, a.y, a.z, b.x, b.y, b.z);
        });
    }
    @Override public void forAllBoxes(@NotNull Shapes.DoubleLineConsumer consumer) {
        source.forAllBoxes((x0, y0, z0, x1, y1, z1) -> {
            var a = orientation.toWorld(new Vec3(x0, y0, z0)).add(offset);
            var b = orientation.toWorld(new Vec3(x1, y1, z1)).add(offset);
            consumer.consume(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z),
                    Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
        });
    }
    @NotNull @Override public VoxelShape move(double x, double y, double z) { return new MountedVoxelShape(source, orientation, offset.add(x, y, z), grid); }
    @Override public @Nullable BlockHitResult clip(Vec3 from, Vec3 to, @NotNull BlockPos pos) {
        Vec3 origin = Vec3.atLowerCornerOf(pos).add(offset);
        var hit = source.clip(orientation.toLocal(from.subtract(origin)), orientation.toLocal(to.subtract(origin)), BlockPos.ZERO);
        return hit == null ? null : new BlockHitResult(orientation.toWorld(hit.getLocation()).add(origin),
                orientation.toWorld(hit.getDirection()), pos, hit.isInside());
    }
    @Override public double collide(@NotNull Direction.Axis axis, AABB box, double distance) {
        Vec3 a = orientation.toLocal(new Vec3(box.minX, box.minY, box.minZ).subtract(offset));
        Vec3 b = orientation.toLocal(new Vec3(box.maxX, box.maxY, box.maxZ).subtract(offset));
        return source.collide(orientation.localAxis(axis), new AABB(a, b), distance * orientation.sign(axis)) * orientation.sign(axis);
    }
}
