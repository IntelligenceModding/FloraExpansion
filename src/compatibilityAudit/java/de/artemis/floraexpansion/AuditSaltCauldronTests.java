package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class AuditSaltCauldronTests {
    static void conversion(CompatibilityTestHelper helper) {
        var dispatcher = de.artemis.floraexpansion.common.event.SaltWaterInteractions.CAULDRON;
        var encoded = net.minecraft.core.cauldron.CauldronInteraction.CODEC.encodeStart(com.mojang.serialization.JsonOps.INSTANCE, dispatcher).getOrThrow();
        check(encoded.getAsString().equals("floraexpansion:salt_water"), "Salt cauldron dispatcher has an unstable save ID");
        check(net.minecraft.core.cauldron.CauldronInteraction.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, encoded).getOrThrow() == dispatcher,
                "Salt cauldron dispatcher did not survive a save round trip");
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(3, 3, 3));
        for (var mode : new GameType[]{GameType.SURVIVAL, GameType.CREATIVE}) {
            var player = helper.makeMockPlayer(mode);
            mode.updatePlayerAbilities(player.getAbilities());
            for (int fill = 1; fill <= 3; fill++) {
                var water = Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, fill);
                level.setBlockAndUpdate(pos, water);
                var salt = new ItemStack(ModItems.SALT.get(), 2);
                use(level, pos, player, salt);
                var converted = level.getBlockState(pos);
                check(converted.is(ModBlocks.SALT_WATER_CAULDRON.get()) && converted.getValue(LayeredCauldronBlock.LEVEL) == fill,
                        "Salting cauldron changed the water amount");
                check(salt.getCount() == (mode == GameType.CREATIVE ? 2 : 1), "Salting cauldron consumed the wrong salt amount");
                int count = salt.getCount();
                use(level, pos, player, salt);
                check(salt.getCount() == count, "Already salty cauldron consumed more salt");
            }
            for (var invalid : new net.minecraft.world.level.block.Block[]{Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.POWDER_SNOW_CAULDRON}) {
                level.setBlockAndUpdate(pos, invalid.defaultBlockState());
                var salt = new ItemStack(ModItems.SALT.get(), 2);
                use(level, pos, player, salt);
                check(level.getBlockState(pos).is(invalid) && salt.getCount() == 2, "Salt converted a non-water cauldron");
            }
        }
    }

    static void drips(CompatibilityTestHelper helper) {
        var level = helper.getLevel();
        var base = helper.absolutePos(new BlockPos(20, 12, 20));
        var forced = new java.util.ArrayList<net.minecraft.world.level.ChunkPos>();
        for (int x = base.getX() >> 4; x <= (base.getX() + 12) >> 4; x++) {
            for (int z = base.getZ() >> 4; z <= (base.getZ() + 12) >> 4; z++)
                if (level.setChunkForced(x, z, true)) forced.add(new net.minecraft.world.level.ChunkPos(x, z));
        }
        var spike = ModBlocks.SALT_SPIKE.get().defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, Direction.DOWN);
        check(ModFluids.SALT_WATER_TYPE.get().getDripInfo().chance() == PointedDripstoneBlock.LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK,
                "Salt Water uses a different cauldron transfer rate than lava");
        for (int test = 0; test < 9; test++) {
            var root = base.offset(test % 3 * 5, 0, test / 3 * 5);
            var cauldron = root.below(4);
            level.setBlockAndUpdate(root.above(), ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
            for (var face : Direction.Plane.HORIZONTAL) level.setBlockAndUpdate(root.above(2).relative(face), Blocks.GLASS.defaultBlockState());
            var fluid = test == 4 ? Fluids.WATER : test == 5 ? Fluids.EMPTY
                    : test == 6 ? ModFluids.FLOWING_SALT_WATER.get() : ModFluids.SALT_WATER.get();
            level.setBlockAndUpdate(root.above(2), fluid.defaultFluidState().createLegacyBlock());
            level.setBlockAndUpdate(root, spike);
            var container = test == 1 || test == 2 ? ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, test)
                    : test == 7 ? Blocks.WATER_CAULDRON.defaultBlockState() : test == 8 ? Blocks.LAVA_CAULDRON.defaultBlockState()
                    : Blocks.CAULDRON.defaultBlockState();
            level.setBlockAndUpdate(cauldron, container);
            if (test == 3) level.setBlockAndUpdate(root.below(2), Blocks.STONE.defaultBlockState());
            transfer(level, root);
            check(level.getBlockTicks().hasScheduledTick(cauldron, container.getBlock()) == (test < 3), "Wrong source/path accepted for cauldron dripping: " + test);
            if (test < 3) check(PointedDripstoneBlock.getCauldronFillFluidType(level, root) == ModFluids.SALT_WATER.get(), "Delayed drip resolved the wrong fluid");
        }
        helper.runAfterDelay(10, () -> check(level.getBlockState(base.below(4)).is(Blocks.CAULDRON), "Brine filled before vanilla's travel delay"));
        helper.runAfterDelay(60, () -> {
            for (int test = 0; test < 9; test++) {
                var state = level.getBlockState(base.offset(test % 3 * 5, 0, test / 3 * 5).below(4));
                if (test < 3) check(state.is(ModBlocks.SALT_WATER_CAULDRON.get()) && state.getValue(LayeredCauldronBlock.LEVEL) == 3, "Successful brine drip did not fill a bucket, case " + test + ": " + state + ", tip=" + PointedDripstoneBlock.findStalactiteTipAboveCauldron(level, base.offset(test % 3 * 5, 0, test / 3 * 5).below(4)) + ", ticking=" + level.shouldTickBlocksAt(new net.minecraft.world.level.ChunkPos(base.getX() >> 4, base.getZ() >> 4).toLong()));
                else check(state.is(test == 7 ? Blocks.WATER_CAULDRON : test == 8 ? Blocks.LAVA_CAULDRON : Blocks.CAULDRON), "Drip replaced an incompatible cauldron");
            }
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            use(level, base.below(4), player, new ItemStack(Items.BUCKET));
            check(player.getMainHandItem().is(ModItems.SALT_WATER_BUCKET.get()), "Drip-filled cauldron returned fresh water");
            check(level.getFluidState(base.above(2)).isSource(), "Cauldron farm consumed its source unlike lava");
            transfer(level, base);
        });
        helper.runAfterDelay(120, () -> {
            var state = level.getBlockState(base.below(4));
            check(state.is(ModBlocks.SALT_WATER_CAULDRON.get()) && state.getValue(LayeredCauldronBlock.LEVEL) == 3, "Cauldron did not refill after bucket collection");
            for (var pos : BlockPos.betweenClosed(base.offset(-2, -5, -2), base.offset(12, 3, 12))) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            forced.forEach(chunk -> level.setChunkForced(chunk.x, chunk.z, false));
            helper.succeed();
        });
    }

    private static void transfer(net.minecraft.server.level.ServerLevel level, BlockPos root) {
        level.getBlockState(root).randomTick(level, root, new LegacyRandomSource(1) {
            private int calls;
            @Override public float nextFloat() { return calls++ == 0 ? 0F : 1F; }
        });
    }
    private static void use(net.minecraft.server.level.ServerLevel level, BlockPos pos, Player player, ItemStack stack) {
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        level.getBlockState(pos).useItemOn(stack, level, player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
}
