package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.HandCrankBlock;
import de.artemis.floraexpansion.common.block.entity.HandCrankBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class AuditHandCrankRedstoneTests {
    static void timing(CompatibilityTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(3, 4, 3));
        var block = ModBlocks.HAND_CRANK.get();
        level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(pos.east().below(), Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(pos.east(), Blocks.REDSTONE_WIRE.defaultBlockState());
        level.setBlockAndUpdate(pos, block.defaultBlockState());
        check(hasBaseCollision(level, pos), "Standalone crank is missing its base collision");
        level.setBlockAndUpdate(pos.below(), ModBlocks.HAND_MILL.get().defaultBlockState());
        check(!hasBaseCollision(level, pos), "Mill-mounted crank kept the standalone base collision");
        level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
        check(hasBaseCollision(level, pos), "Changing the crank support did not refresh its base");
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setShiftKeyDown(true);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COBBLESTONE));
        var interaction = new net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock(player,
                net.minecraft.world.InteractionHand.MAIN_HAND, pos, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(interaction);
        check(interaction.getUseBlock() == net.neoforged.neoforge.common.util.TriState.TRUE && interaction.getUseItem() == net.neoforged.neoforge.common.util.TriState.FALSE,
                "Sneak mode-switch bypassed crank while holding an item");
        player.setShiftKeyDown(false);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, net.minecraft.world.item.ItemStack.EMPTY);
        check(!level.getBlockState(pos).getValue(HandCrankBlock.STRENGTH_MODE), "Crank does not default to pulse mode");
        check(block.defaultBlockState().canSurvive(level, pos), "Crank cannot mount on ordinary stone");
        use(level, pos, player);
        check(power(level, pos) == 15 && level.getBlockState(pos.east()).getValue(HandCrankBlock.POWER) == 15,
                "Manual crank did not power adjacent redstone");
        check(level.getBlockState(pos).getDirectSignal(level, pos, Direction.UP) == 15, "Crank did not strongly power its support");
        helper.runAfterDelay(1, () -> check(power(level, pos) == 15, "Pulse ended too soon"));
        helper.runAfterDelay(3, () -> {
            check(power(level, pos) == 0 && level.getBlockState(pos.east()).getValue(HandCrankBlock.POWER) == 0, "Pulse did not expire");
            player.setShiftKeyDown(true); use(level, pos, player); player.setShiftKeyDown(false);
            check(level.getBlockState(pos).getValue(HandCrankBlock.STRENGTH_MODE) && power(level, pos) == 0, "Mode toggle emitted power");
            for (int i = 0; i < 3; i++) use(level, pos, player);
            check(power(level, pos) == 3 && level.getBlockState(pos.east()).getValue(HandCrankBlock.POWER) == 3, "Analog signal did not rise per turn");
            var entity = (HandCrankBlockEntity) level.getBlockEntity(pos);
            var saved = entity.saveWithFullMetadata(level.registryAccess());
            var restored = (HandCrankBlockEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                    pos, level.getBlockState(pos), saved, level.registryAccess());
            check(restored != null && restored.getNextDecay() == entity.getNextDecay(), "Decay timing did not survive save/load");
            level.setBlockEntity(restored);
        });
        helper.runAfterDelay(22, () -> check(power(level, pos) == 3, "Strength decayed before one second"));
        helper.runAfterDelay(24, () -> {
            check(power(level, pos) == 2 && level.getBlockState(pos.east()).getValue(HandCrankBlock.POWER) == 2, "Strength did not decay by one");
            use(level, pos, player);
            check(power(level, pos) == 3, "Cranking did not restore strength");
        });
        helper.runAfterDelay(43, () -> check(power(level, pos) == 3, "Cranking did not reset the decay delay"));
        helper.runAfterDelay(45, () -> {
            check(power(level, pos) == 2, "Rescheduled decay failed");
            for (int i = 0; i < 20; i++) use(level, pos, player);
            check(power(level, pos) == 15, "Strength did not cap at 15");
            player.setShiftKeyDown(true); use(level, pos, player); player.setShiftKeyDown(false);
            check(power(level, pos) == 0 && !level.getBlockState(pos).getValue(HandCrankBlock.STRENGTH_MODE), "Switching to pulse left stale power");
            use(level, pos, player);
            check(power(level, pos) == 15, "Pulse failed after mode switch");
            level.removeBlock(pos, false);
            check(level.getBlockState(pos.east()).getValue(HandCrankBlock.POWER) == 0, "Removing powered crank left redstone powered");
            helper.succeed();
        });
    }
    private static int power(net.minecraft.server.level.ServerLevel level, BlockPos pos) { return level.getBlockState(pos).getValue(HandCrankBlock.POWER); }
    private static boolean hasBaseCollision(net.minecraft.server.level.ServerLevel level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).clip(
                new Vec3(pos.getX() + 0.3, pos.getY() + 0.2, pos.getZ() + 0.3),
                new Vec3(pos.getX() + 0.3, pos.getY() + 0.01, pos.getZ() + 0.3), pos) != null;
    }
    private static void use(net.minecraft.server.level.ServerLevel level, BlockPos pos, Player player) {
        level.getBlockState(pos).useWithoutItem(level, player, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
}
