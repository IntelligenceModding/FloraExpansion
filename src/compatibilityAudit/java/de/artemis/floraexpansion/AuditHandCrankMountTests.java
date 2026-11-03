package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.block.HandMillShapes;
import de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity;
import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.util.CrankOrientation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class AuditHandCrankMountTests {
    static void verify(CompatibilityTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        int index = 0;
        for (var face : Direction.values()) {
            var target = helper.absolutePos(new BlockPos(2 + 3 * index++, 5, 7));
            var support = target.relative(face.getOpposite());
            level.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(support, Blocks.STONE.defaultBlockState());
            var stack = new ItemStack(ModBlocks.HAND_CRANK.get());
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            var hit = new BlockHitResult(Vec3.atCenterOf(support).add(face.getStepX() * 0.5, face.getStepY() * 0.5, face.getStepZ() * 0.5),
                    face, support, false);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
            var state = level.getBlockState(target);
            check(state.is(ModBlocks.HAND_CRANK.get()) && state.getValue(HandCrankBlock.FACING) == face && stack.isEmpty(), "Placement failed on " + face);
            var crank = (HandCrankBlockEntity) level.getBlockEntity(target);
            check(!crank.isOnMill() && state.canSurvive(level, target), "Standalone mount or support incorrect on " + face);
            state.useWithoutItem(level, player, new BlockHitResult(Vec3.atCenterOf(target), face, target, false));
            state = level.getBlockState(target);
            for (var direction : Direction.values()) {
                check(state.getDirectSignal(level, target, direction) == (direction == face ? 15 : 0), "Strong output direction incorrect on " + face);
            }
            check(level.getDirectSignalTo(support) == 15, "Crank did not strongly power its support on " + face);
            player.setShiftKeyDown(true);
            state.useWithoutItem(level, player, new BlockHitResult(Vec3.atCenterOf(target), face, target, false));
            player.setShiftKeyDown(false);
            check(level.getDirectSignalTo(support) == 0 && level.getBlockState(target).getValue(HandCrankBlock.STRENGTH_MODE), "Mode switching failed on " + face);
            var saved = crank.saveWithFullMetadata(level.registryAccess());
            var restored = (HandCrankBlockEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(target, level.getBlockState(target), saved, level.registryAccess());
            check(restored != null && restored.getBlockState().getValue(HandCrankBlock.FACING) == face, "Mount direction did not survive save/load");
            level.removeBlock(support, false);
            check(level.getBlockState(target).isAir(), "Unsupported crank remained floating on " + face);
            geometry(face);
        }
        // A wall crank directly above a mill is an independent redstone crank, not a mill attachment.
        var millPos = helper.absolutePos(new BlockPos(2, 3, 2));
        level.setBlockAndUpdate(millPos, ModBlocks.HAND_MILL.get().defaultBlockState());
        level.setBlockAndUpdate(millPos.above().south(), Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(millPos.above(), ModBlocks.HAND_CRANK.get().defaultBlockState().setValue(HandCrankBlock.FACING, Direction.NORTH));
        var mill = (HandMillBlockEntity) level.getBlockEntity(millPos);
        mill.setItem(0, new ItemStack(ModItems.RAW_SALT.get()));
        check(!mill.crank(player) && mill.getMillstoneAngle(0) == 0, "Wall-mounted crank drove the mill below it");
    }

    private static void geometry(Direction face) {
        var orientation = CrankOrientation.of(face);
        for (float angle : new float[]{0, 23.125F, 45, 83.75F}) {
            var local = HandMillShapes.crank(angle + orientation.restingAngle(), true);
            var mounted = HandMillShapes.crank(angle, true, face);
            var a = orientation.toWorld(new Vec3(0.26, 0.25, 0.26));
            var b = orientation.toWorld(new Vec3(0.26, -0.1, 0.26));
            var hit = mounted.clip(a, b, BlockPos.ZERO);
            check(hit != null && hit.getDirection() == face, "Mounted base ray intersection incorrect on " + face);
            var localBox = new AABB(0.28, 0.2, 0.28, 0.32, 0.3, 0.32);
            var worldBox = new AABB(orientation.toWorld(new Vec3(localBox.minX, localBox.minY, localBox.minZ)),
                    orientation.toWorld(new Vec3(localBox.maxX, localBox.maxY, localBox.maxZ)));
            double expected = local.collide(Direction.Axis.Y, localBox, -0.5);
            int sign = orientation.sign(face.getAxis());
            check(Math.abs(mounted.collide(face.getAxis(), worldBox, -0.5 * sign) * sign - expected) < 1e-9,
                    "Mounted collision does not match the floor crank on " + face);
            check(Math.abs(mounted.move(100, 20, -30).collide(face.getAxis(), worldBox.move(100, 20, -30), -0.5 * sign) * sign - expected) < 1e-9,
                    "World translation broke mounted collision on " + face);
            var expectedEdges = new java.util.ArrayList<Vec3>();
            local.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
                expectedEdges.add(orientation.toWorld(new Vec3(x0, y0, z0)));
                expectedEdges.add(orientation.toWorld(new Vec3(x1, y1, z1)));
            });
            int[] vertex = {0};
            mounted.forAllEdges((x0, y0, z0, x1, y1, z1) -> {
                check(expectedEdges.get(vertex[0]++).distanceToSqr(new Vec3(x0, y0, z0)) < 1e-18
                        && expectedEdges.get(vertex[0]++).distanceToSqr(new Vec3(x1, y1, z1)) < 1e-18,
                        "Mounted outline differs from its model transform on " + face);
            });
            check(vertex[0] == expectedEdges.size(), "Mounted outline lost edges");
        }
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
    private AuditHandCrankMountTests() {}
}
