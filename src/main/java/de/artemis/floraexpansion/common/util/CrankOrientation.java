package de.artemis.floraexpansion.common.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

/** Exact right-angle mounting transforms around the block center. */
public enum CrankOrientation {
    UP(Direction.EAST, Direction.UP, Direction.SOUTH),
    DOWN(Direction.EAST, Direction.DOWN, Direction.NORTH),
    NORTH(Direction.WEST, Direction.NORTH, Direction.DOWN),
    SOUTH(Direction.EAST, Direction.SOUTH, Direction.DOWN),
    EAST(Direction.NORTH, Direction.EAST, Direction.DOWN),
    WEST(Direction.SOUTH, Direction.WEST, Direction.DOWN);

    private final Direction[] basis;
    private final Direction.Axis[] localAxes = new Direction.Axis[3];
    private final int[] signs = new int[3];

    CrankOrientation(Direction right, Direction up, Direction forward) {
        basis = new Direction[]{right, up, forward};
        for (int local = 0; local < 3; local++) {
            var direction = basis[local];
            int world = direction.getAxis().ordinal();
            localAxes[world] = Direction.Axis.values()[local];
            signs[world] = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;
        }
    }
    public static CrankOrientation of(Direction facing) { return valueOf(facing.name()); }
    /** Handle rotation that points down on walls; preserves the default on horizontal mounts. */
    public float restingAngle() {
        return basis[1].getAxis().isHorizontal() ? 90F : 0F;
    }
    /** Roll after aligning the shaft with the mounting normal, keeping local Z down on walls. */
    public float mountingRoll() {
        return switch (this) {
            case NORTH -> 180F;
            case EAST -> 90F;
            case WEST -> -90F;
            default -> 0F;
        };
    }
    public Direction.Axis localAxis(Direction.Axis world) { return localAxes[world.ordinal()]; }
    public int sign(Direction.Axis world) { return signs[world.ordinal()]; }
    public Vec3 toWorld(Vec3 point) {
        double x = point.x - 0.5, y = point.y - 0.5, z = point.z - 0.5;
        return new Vec3(0.5 + x * basis[0].getStepX() + y * basis[1].getStepX() + z * basis[2].getStepX(),
                0.5 + x * basis[0].getStepY() + y * basis[1].getStepY() + z * basis[2].getStepY(),
                0.5 + x * basis[0].getStepZ() + y * basis[1].getStepZ() + z * basis[2].getStepZ());
    }
    public Vec3 toLocal(Vec3 point) {
        double x = point.x - 0.5, y = point.y - 0.5, z = point.z - 0.5;
        return new Vec3(0.5 + x * basis[0].getStepX() + y * basis[0].getStepY() + z * basis[0].getStepZ(),
                0.5 + x * basis[1].getStepX() + y * basis[1].getStepY() + z * basis[1].getStepZ(),
                0.5 + x * basis[2].getStepX() + y * basis[2].getStepY() + z * basis[2].getStepZ());
    }
    public Direction toWorld(Direction normal) {
        var axis = basis[normal.getAxis().ordinal()];
        return normal.getAxisDirection() == Direction.AxisDirection.POSITIVE ? axis : axis.getOpposite();
    }
}
