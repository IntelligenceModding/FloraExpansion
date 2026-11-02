package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

final class SaltTests {
    static void waterlogging(GameTestHelper helper) {
        var level = helper.getLevel();
        var block = (SaltDepositBlock) ModBlocks.SALT_DEPOSIT.get();
        var pos = helper.absolutePos(new BlockPos(3, 20, 3));
        level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, new ItemStack(ModItems.RAW_SALT.get(), 8));
        for (var fluid : java.util.List.of(net.minecraft.world.level.material.Fluids.WATER,
                de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get())) {
            for (int layers = 1; layers <= 4; layers++) {
                var dry = block.defaultBlockState().setValue(SaltDepositBlock.LAYERS, layers);
                level.setBlockAndUpdate(pos, dry);
                check(block.canPlaceLiquid(player, level, pos, dry, fluid) == (layers < 4), "Wrong full-pile fluid acceptance");
                check(block.placeLiquid(level, pos, dry, fluid.defaultFluidState()) == (layers < 4), "Bucket failed to waterlog salt layers");
                var wet = level.getBlockState(pos);
                if (layers == 4) { check(wet.getFluidState().isEmpty(), "Full salt pile retained water"); continue; }
                check(wet.getFluidState().is(fluid) && wet.getFluidState().isSource(), "Salt changed the contained fluid type");
                var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos),
                        net.minecraft.core.Direction.UP, pos, false);
                var context = new net.minecraft.world.item.context.BlockPlaceContext(new net.minecraft.world.item.context.UseOnContext(
                        player, net.minecraft.world.InteractionHand.MAIN_HAND, hit));
                var stacked = block.getStateForPlacement(context);
                check(stacked.getValue(SaltDepositBlock.LAYERS) == layers + 1, "Wet salt did not stack");
                check(!stacked.getValue(SaltDepositBlock.NATURAL), "Adding manufactured salt retained buried finds");
                check(layers == 3 ? stacked.getFluidState().isEmpty() : stacked.getFluidState().is(fluid), "Stacking changed fluid identity");
                var bucket = block.pickupBlock(player, level, pos, wet);
                check(bucket.is(fluid.getBucket()) && level.getBlockState(pos).getFluidState().isEmpty(), "Pickup returned wrong bucket or left a source behind");
            }
            level.setBlockAndUpdate(pos, fluid.defaultFluidState().createLegacyBlock());
            var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(pos.below()),
                    net.minecraft.core.Direction.UP, pos.below(), false);
            var placement = block.getStateForPlacement(new net.minecraft.world.item.context.BlockPlaceContext(
                    new net.minecraft.world.item.context.UseOnContext(player, net.minecraft.world.InteractionHand.MAIN_HAND, hit)));
            check(placement != null && placement.getFluidState().is(fluid), "Underwater salt placement displaced its fluid");
            check(!placement.getValue(SaltDepositBlock.NATURAL), "Placed salt received buried finds");
            // Falling leaves the original source behind and picks up the destination's fluid.
            var start = pos.above(6);
            for (int y = 1; y <= 7; y++) level.setBlockAndUpdate(pos.above(y), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(start.below(), Blocks.STONE.defaultBlockState());
            level.setBlockAndUpdate(start, block.defaultBlockState());
            block.placeLiquid(level, start, level.getBlockState(start), fluid.defaultFluidState());
            var fallingState = level.getBlockState(start);
            level.removeBlock(start.below(), false);
            fallingState.tick(level, start, level.getRandom());
            check(level.getFluidState(start).is(fluid), "Falling salt removed its original fluid source");
            var falling = level.getEntitiesOfClass(FallingBlockEntity.class, new AABB(start).inflate(.5)).getFirst();
            for (int i = 0; i < 80 && falling.isAlive(); i++) falling.tick();
            check(level.getBlockState(pos).is(block) && level.getFluidState(pos).is(fluid), "Falling salt failed to waterlog at its destination");
            for (int total = 2; total <= 4; total++) {
                level.setBlockAndUpdate(start, block.defaultBlockState());
                level.getBlockState(start).tick(level, start, level.getRandom());
                falling = level.getEntitiesOfClass(FallingBlockEntity.class, new AABB(start).inflate(.5)).getFirst();
                for (int i = 0; i < 80 && falling.isAlive(); i++) falling.tick();
                check(level.getBlockState(pos).getValue(SaltDepositBlock.LAYERS) == total, "Falling salt did not merge into a wet pile");
                check(total == 4 ? level.getFluidState(pos).isEmpty() : level.getFluidState(pos).is(fluid), "Merging salt lost or duplicated its fluid");
            }
            level.setBlockAndUpdate(start, Blocks.AIR.defaultBlockState());
        }
        level.setBlockAndUpdate(pos, block.defaultBlockState());
        check(!block.placeLiquid(level, pos, level.getBlockState(pos), net.minecraft.world.level.material.Fluids.LAVA.defaultFluidState()), "Lava waterlogged salt");
        check(!block.placeLiquid(level, pos, level.getBlockState(pos), de.artemis.floraexpansion.common.registry.ModFluids.FLOWING_SALT_WATER.get().defaultFluidState()), "Flowing brine created a source");
    }

    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var base = helper.absolutePos(new BlockPos(1, 2, 4));
        level.setBlockAndUpdate(base, Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(base.above(), Blocks.AIR.defaultBlockState());
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        var stack = new ItemStack(ModItems.RAW_SALT.get(), 4);
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stack);
        for (int layer = 1; layer <= 4; layer++) {
            var clicked = layer == 1 ? base : base.above();
            var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(clicked),
                    net.minecraft.core.Direction.UP, clicked, false);
            stack.useOn(new net.minecraft.world.item.context.UseOnContext(player,
                    net.minecraft.world.InteractionHand.MAIN_HAND, hit));
            var placed = level.getBlockState(base.above());
            check(placed.is(ModBlocks.SALT_DEPOSIT.get()) && placed.getValue(SaltDepositBlock.LAYERS) == layer,
                    "Raw salt did not place/increase the Salt Deposit: " + layer);
        }
        check(stack.isEmpty(), "Four layers did not consume four raw salt");
        level.removeBlock(base.above(), false);
        for (int layers = 1; layers <= 4; layers++) {
            var floor = helper.absolutePos(new BlockPos(layers * 3, 2, 1));
            var start = floor.above(5);
            for (int y = 1; y <= 6; y++) level.setBlockAndUpdate(floor.above(y), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(floor, Blocks.STONE.defaultBlockState());
            level.setBlockAndUpdate(start.below(), Blocks.STONE.defaultBlockState());
            var state = ModBlocks.SALT_DEPOSIT.get().defaultBlockState().setValue(SaltDepositBlock.LAYERS, layers);
            level.setBlockAndUpdate(start, state);
            state.tick(level, start, level.getRandom());
            check(level.getBlockState(start).equals(state), "Supported salt fell");
            level.removeBlock(start.below(), false);
            state.tick(level, start, level.getRandom());
            var entities = level.getEntitiesOfClass(FallingBlockEntity.class, new AABB(start).inflate(0.5));
            check(entities.size() == 1, "Unsupported salt did not start falling: " + layers);
            var falling = entities.getFirst();
            check(falling.getBlockState().equals(state), "Falling salt lost layers");
            for (int tick = 0; tick < 80 && falling.isAlive(); tick++) falling.tick();
            check(!falling.isAlive() && level.getBlockState(floor.above()).equals(state), "Salt did not land with its layers: " + layers);
            var drops = Block.getDrops(state, level, floor.above(), null);
            check(drops.stream().filter(s -> s.is(ModItems.RAW_SALT.get())).mapToInt(ItemStack::getCount).sum() == layers,
                    "Landed pile lost raw salt");
            level.removeBlock(floor.above(), false);
            // A torch breaks a falling pile; vanilla's single-item fallback must not lose layers.
            level.setBlockAndUpdate(floor.above(), Blocks.TORCH.defaultBlockState());
            level.setBlockAndUpdate(start, state);
            state.tick(level, start, level.getRandom());
            falling = level.getEntitiesOfClass(FallingBlockEntity.class, new AABB(start).inflate(0.5)).getFirst();
            for (int tick = 0; tick < 80 && falling.isAlive(); tick++) falling.tick();
            var items = level.getEntitiesOfClass(ItemEntity.class, new AABB(floor).inflate(2));
            check(items.stream().filter(e -> e.getItem().is(ModItems.RAW_SALT.get())).mapToInt(e -> e.getItem().getCount()).sum() == layers,
                    "Broken falling pile lost raw salt: " + layers);
            items.forEach(ItemEntity::discard);
        }
    }

    static void merging(GameTestHelper helper) {
        var level = helper.getLevel();
        var block = ModBlocks.SALT_DEPOSIT.get();
        for (int lower = 1; lower <= 4; lower++) {
            for (int upper = 1; upper <= 4; upper++) {
                var floor = helper.absolutePos(new BlockPos(lower * 3, 2, upper * 3));
                var start = floor.above(6);
                for (int y = 1; y <= 7; y++) level.setBlockAndUpdate(floor.above(y), Blocks.AIR.defaultBlockState());
                level.setBlockAndUpdate(floor, Blocks.STONE.defaultBlockState());
                level.setBlockAndUpdate(floor.above(), block.defaultBlockState().setValue(SaltDepositBlock.LAYERS, lower));
                var fallingState = block.defaultBlockState().setValue(SaltDepositBlock.LAYERS, upper);
                level.setBlockAndUpdate(start, fallingState);
                fallingState.tick(level, start, level.getRandom());
                var falling = level.getEntitiesOfClass(FallingBlockEntity.class, new AABB(start).inflate(0.5)).getFirst();
                for (int tick = 0; tick < 80 && falling.isAlive(); tick++) falling.tick();
                int total = lower + upper;
                var bottom = level.getBlockState(floor.above());
                check(bottom.is(block) && bottom.getValue(SaltDepositBlock.LAYERS) == Math.min(4, total),
                        "Falling layers failed to merge: " + lower + " + " + upper);
                var top = level.getBlockState(floor.above(2));
                check(total <= 4 ? top.isAir() : top.is(block) && top.getValue(SaltDepositBlock.LAYERS) == total - 4,
                        "Overflow layers lost: " + lower + " + " + upper);
                check(level.getEntitiesOfClass(ItemEntity.class, new AABB(floor).inflate(2)).isEmpty(),
                        "Merging salt dropped items: " + lower + " + " + upper);
            }
        }
    }
    static void support(GameTestHelper helper) {
        var level = helper.getLevel();
        var block = ModBlocks.SALT_DEPOSIT.get();
        var anchor = helper.absolutePos(new BlockPos(3, 5, 3));
        for (var pos : BlockPos.betweenClosed(anchor.offset(-2, -3, -2), anchor.offset(2, 2, 2))) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        level.setBlockAndUpdate(anchor, Blocks.STONE.defaultBlockState());
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        for (var face : net.minecraft.core.Direction.values()) {
            if (face == net.minecraft.core.Direction.UP) continue;
            var stack = new ItemStack(ModItems.RAW_SALT.get(), 4);
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, stack);
            var hit = new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(anchor), face, anchor, false);
            stack.useOn(new net.minecraft.world.item.context.UseOnContext(player, net.minecraft.world.InteractionHand.MAIN_HAND, hit));
            check(level.getBlockState(anchor.relative(face)).isAir() && stack.getCount() == 4,
                    "Salt could be placed without support against " + face);
        }
        for (int layers = 1; layers <= 4; layers++) {
            var state = block.defaultBlockState().setValue(SaltDepositBlock.LAYERS, layers);
            check(state.canSurvive(level, anchor.above()), "Solid block cannot support salt");
            level.setBlockAndUpdate(anchor.above(), state);
            check(state.canSurvive(level, anchor.above(2)) == (layers == 4), "Partial pile supported a floating block");
        }
        level.setBlockAndUpdate(anchor.above(), Blocks.OAK_SLAB.defaultBlockState());
        check(!block.defaultBlockState().canSurvive(level, anchor.above(2)), "Bottom slab supported floating salt");
        var salt = block.defaultBlockState().setValue(SaltDepositBlock.LAYERS, 3);
        level.setBlockAndUpdate(anchor.above(2), salt);
        salt.tick(level, anchor.above(2), level.getRandom());
        check(level.getBlockState(anchor.above(2)).isAir(), "Salt remained floating over a bottom slab");
        level.getEntitiesOfClass(FallingBlockEntity.class, new AABB(anchor.above(2)).inflate(1)).forEach(FallingBlockEntity::discard);
        level.setBlockAndUpdate(anchor.above(), Blocks.OAK_FENCE.defaultBlockState());
        check(!block.defaultBlockState().canSurvive(level, anchor.above(2)), "Fence supported floating salt");
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
