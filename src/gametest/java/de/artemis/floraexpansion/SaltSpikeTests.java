package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class SaltSpikeTests {
    static void brineSeepage(GameTestHelper helper) {
        var level = helper.getLevel();
        var block = ModBlocks.SALT_SPIKE.get();
        var root = helper.absolutePos(new BlockPos(5, 24, 5));
        var down = block.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN);
        for (var pos : BlockPos.betweenClosed(root.offset(-2, -8, -2), root.offset(2, 4, 2))) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(root.above(), ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState(), 2);
        for (int i = 0; i < 3; i++) level.setBlock(root.below(i), down, 2);
        var cauldron = root.below(6);
        level.setBlock(cauldron, Blocks.CAULDRON.defaultBlockState(), 2);
        for (var fluid : java.util.List.of(net.minecraft.world.level.material.Fluids.WATER, net.minecraft.world.level.material.Fluids.LAVA,
                net.minecraft.world.level.material.Fluids.EMPTY,
                de.artemis.floraexpansion.common.registry.ModFluids.FLOWING_SALT_WATER.get(),
                de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get())) {
            level.setBlock(root.above(2), fluid.defaultFluidState().createLegacyBlock(), 2);
            boolean brine = fluid == de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get();
            check(block.getDripFluid(level, root) == (brine ? fluid : net.minecraft.world.level.material.Fluids.EMPTY), "Salt spike dripped the wrong root fluid");
            check(block.getDripFluid(level, root.below(2)) == (brine ? fluid : net.minecraft.world.level.material.Fluids.EMPTY), "Long spike lost Salt Water identity");
            level.getBlockState(root).randomTick(level, root, new net.minecraft.world.level.levelgen.LegacyRandomSource(1) {
                private int calls;
                @Override public float nextFloat() { return calls++ == 0 ? 0F : 1F; }
            });
            check(level.getBlockTicks().hasScheduledTick(cauldron, Blocks.CAULDRON) == brine, "Salt spike scheduled the wrong cauldron fluid");
            check(level.getBlockState(cauldron).is(Blocks.CAULDRON), "Drip particle created a fluid source");
        }
        level.setBlock(root.above(2), de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get().defaultFluidState().createLegacyBlock(), 2);
        level.setBlock(root.below(2), down.setValue(PointedDripstoneBlock.WATERLOGGED, true), 2);
        check(block.getDripFluid(level, root.below(2)) == net.minecraft.world.level.material.Fluids.EMPTY, "Submerged tip dripped brine");
        level.setBlock(root.below(2), block.defaultBlockState(), 2);
        check(block.getDripFluid(level, root.below(2)) == net.minecraft.world.level.material.Fluids.EMPTY, "Standing spike dripped brine");
        level.setBlock(root.above(2), Blocks.AIR.defaultBlockState(), 2);
    }

    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var block = ModBlocks.SALT_SPIKE.get();
        var pos = helper.absolutePos(new BlockPos(3, 12, 3));
        check(block.defaultBlockState().isRandomlyTicking(), "Spike tag or random ticking missing");
        for (var state : block.getStateDefinition().getPossibleStates()) {
            var vanilla = Blocks.POINTED_DRIPSTONE.defaultBlockState()
                    .setValue(PointedDripstoneBlock.TIP_DIRECTION, state.getValue(PointedDripstoneBlock.TIP_DIRECTION))
                    .setValue(PointedDripstoneBlock.THICKNESS, state.getValue(PointedDripstoneBlock.THICKNESS))
                    .setValue(PointedDripstoneBlock.WATERLOGGED, state.getValue(PointedDripstoneBlock.WATERLOGGED));
            check(state.getShape(level, pos).bounds().equals(vanilla.getShape(level, pos).bounds()), "Salt spike shape differs from vanilla");
            check(state.getFluidState().equals(vanilla.getFluidState()), "Waterlogging differs from vanilla");
        }
        var drops = Block.getDrops(block.defaultBlockState(), level, pos, null);
        check(drops.size() == 1 && drops.getFirst().is(block.asItem()), "Spike must drop itself");
        var down = block.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN);
        for (int scenario=0; scenario<8; scenario++) {
            for (int y=-9; y<=3; y++) level.setBlockAndUpdate(pos.offset(0,y,0), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.above(), scenario == 2 ? Blocks.DRIPSTONE_BLOCK.defaultBlockState() : ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
            if (scenario != 0) level.setBlockAndUpdate(pos.above(2), switch (scenario) {
                case 1 -> ModBlocks.SALT_WATER.get().defaultBlockState().setValue(LiquidBlock.LEVEL, 1);
                case 5 -> Blocks.WATER.defaultBlockState();
                case 6 -> Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 1);
                case 7 -> Blocks.LAVA.defaultBlockState();
                default -> ModBlocks.SALT_WATER.get().defaultBlockState();
            });
            level.setBlockAndUpdate(pos, scenario == 3 ? down.setValue(PointedDripstoneBlock.WATERLOGGED, true) : down);
            if (scenario == 4) level.setBlockAndUpdate(pos.below(), Blocks.STONE.defaultBlockState());
            for (int attempt=0; attempt<50; attempt++) block.growStalactiteOrStalagmiteIfPossible(level.getBlockState(pos), level, pos, RandomSource.create(attempt));
            check(!level.getBlockState(pos.below()).is(block), "Salt grew without source Salt Water, on the wrong mineral, underwater, or through an obstruction: " + scenario);
        }
        level.setBlockAndUpdate(pos.above(2), ModBlocks.SALT_WATER.get().defaultBlockState());
        level.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(pos, down);
        for (int attempt=0; attempt<50 && !level.getBlockState(pos.below()).is(block); attempt++)
            block.growStalactiteOrStalagmiteIfPossible(level.getBlockState(pos), level, pos, RandomSource.create(attempt));
        check(level.getBlockState(pos.below()).is(block), "Salt Water setup did not grow a stalactite");
        check(level.getBlockState(pos.above()).is(ModBlocks.CRYSTALLINE_SALT.get()), "Growth unexpectedly consumed the salt support");

        var floorRoot = pos.offset(5,0,0);
        for (int y=-7; y<=3; y++) level.setBlockAndUpdate(floorRoot.offset(0,y,0), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(floorRoot.above(), ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
        level.setBlockAndUpdate(floorRoot.above(2), ModBlocks.SALT_WATER.get().defaultBlockState());
        level.setBlockAndUpdate(floorRoot, down);
        level.setBlockAndUpdate(floorRoot.below(6), Blocks.STONE.defaultBlockState());
        var growthRandom = RandomSource.create(0);
        for (int attempt=0; attempt<50; attempt++)
            block.growStalactiteOrStalagmiteIfPossible(level.getBlockState(floorRoot), level, floorRoot, growthRandom);
        check(level.getBlockState(floorRoot.below(5)).is(block)
                && level.getBlockState(floorRoot.below(5)).getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.UP,
                "Salt-bearing seepage did not create a stalagmite below the hanging spike");

        // Place a standing tip against a hanging tip, including sneak-to-avoid-merging behavior.
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setXRot(60);
        for (boolean sneak : new boolean[]{false, true}) {
            level.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(pos.below(2), Blocks.STONE.defaultBlockState());
            level.setBlockAndUpdate(pos, down);
            player.setShiftKeyDown(sneak);
            var stack = new ItemStack(block);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            var hit = new BlockHitResult(Vec3.atCenterOf(pos.below(2)).add(0,0.5,0), Direction.UP, pos.below(2), false);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
            check(stack.isEmpty() && level.getBlockState(pos.below()).is(block), "Standing salt spike placement failed");
            check((level.getBlockState(pos).getValue(PointedDripstoneBlock.THICKNESS) == DripstoneThickness.TIP_MERGE) == !sneak,
                    "Hanging tip merge/sneak behavior incorrect");
            check((level.getBlockState(pos.below()).getValue(PointedDripstoneBlock.THICKNESS) == DripstoneThickness.TIP_MERGE) == !sneak,
                    "Standing tip merge/sneak behavior incorrect");
        }
        level.setBlockAndUpdate(pos.above(2), Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(floorRoot.above(2), Blocks.AIR.defaultBlockState());
    }

    static void supportLoss(GameTestHelper helper) {
        var level = helper.getLevel();
        var block = ModBlocks.SALT_SPIKE.get();
        var downPos = helper.absolutePos(new BlockPos(2, 8, 2));
        var upPos = helper.absolutePos(new BlockPos(7, 4, 2));
        level.setBlockAndUpdate(downPos.above(), Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(downPos, block.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN));
        level.setBlockAndUpdate(upPos.below(), Blocks.STONE.defaultBlockState());
        level.setBlockAndUpdate(upPos, block.defaultBlockState());
        level.removeBlock(downPos.above(), false);
        level.removeBlock(upPos.below(), false);
        helper.runAfterDelay(4, () -> {
            check(level.getBlockState(upPos).isAir(), "Unsupported stalagmite remained floating");
            check(level.getEntitiesOfClass(ItemEntity.class, new AABB(upPos).inflate(1)).stream()
                    .anyMatch(item -> item.getItem().is(block.asItem())), "Unsupported stalagmite did not drop its item");
            check(level.getBlockState(downPos).isAir() && level.getEntitiesOfClass(FallingBlockEntity.class,
                    new AABB(downPos).inflate(1, 4, 1)).stream().anyMatch(entity -> entity.getBlockState().is(block)),
                    "Unsupported stalactite did not become falling salt");
            helper.succeed();
        });
    }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
    private SaltSpikeTests() {}
}

