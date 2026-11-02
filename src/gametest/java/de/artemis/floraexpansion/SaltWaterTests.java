package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class SaltWaterTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(3, 4, 3));
        var source = ModFluids.SALT_WATER.get();
        var flowing = ModFluids.FLOWING_SALT_WATER.get();
        check(source.isSame(flowing) && !source.isSame(Fluids.WATER) && source.getBucket() == ModItems.SALT_WATER_BUCKET.get(), "Fluid identity/bucket incorrect");
        check(source.getFluidType() == ModFluids.SALT_WATER_TYPE.get() && source.getFluidType().getIsWaterLike()
                && !source.canConvertToSource(source.defaultFluidState(), level, pos), "Fluid behavior incorrect");
        check(source.defaultFluidState().createLegacyBlock().is(ModBlocks.SALT_WATER.get()), "Source does not map to the liquid block");
        var contents = net.neoforged.neoforge.transfer.fluid.FluidUtil.getFirstStackContained(new ItemStack(ModItems.SALT_WATER_BUCKET.get()));
        check(!contents.isEmpty() && contents.getFluid() == source && contents.getAmount() == 1000, "Bucket fluid capability missing or wrong amount");
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var bucket = new ItemStack(ModItems.SALT_WATER_BUCKET.get());
        check(((BucketItem) bucket.getItem()).emptyContents(player, level, pos, null, bucket), "Salt Water bucket could not place fluid");
        check(level.getFluidState(pos).getType() == source && level.getFluidState(pos).isSource(), "Bucket placement lost fluid identity");
        var pickup = ModBlocks.SALT_WATER.get().pickupBlock(player, level, pos, level.getBlockState(pos));
        check(pickup.is(ModItems.SALT_WATER_BUCKET.get()) && level.getBlockState(pos).isAir(), "Source pickup lost Salt Water");
        level.setBlockAndUpdate(pos, ModBlocks.SALT_WATER.get().defaultBlockState().setValue(LiquidBlock.LEVEL, 2));
        check(ModBlocks.SALT_WATER.get().pickupBlock(player, level, pos, level.getBlockState(pos)).isEmpty()
                && level.getBlockState(pos).is(ModBlocks.SALT_WATER.get()), "Flowing fluid could be bucketed");
        level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
        use(level, pos, player, ModItems.SALT_WATER_BUCKET.get());
        check(player.getMainHandItem().is(Items.BUCKET) && fill(level,pos) == 3, "Bucket did not fill Salt Water cauldron");
        for (int count=1; count<=3; count++) {
            level.setBlockAndUpdate(pos, ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,count));
            use(level, pos, player, Items.GLASS_BOTTLE);
            check(player.getMainHandItem().is(Items.GLASS_BOTTLE) && fill(level,pos) == count,
                    "Glass bottle collected Salt Water or changed the cauldron");
            if (count<3) {
                use(level, pos, player, Items.BUCKET);
                check(player.getMainHandItem().is(Items.BUCKET) && fill(level,pos) == count, "Partial cauldron filled an entire bucket");
            }
            for (int rain=0; rain<100; rain++) ModBlocks.SALT_WATER_CAULDRON.get().handlePrecipitation(level.getBlockState(pos), level, pos, Biome.Precipitation.RAIN);
            check(fill(level,pos) == count, "Rain created more Salt Water");
            check(level.getBlockState(pos).getAnalogOutputSignal(level,pos,Direction.NORTH) == count, "Cauldron comparator output wrong");
        }
        use(level, pos, player, Items.BUCKET);
        check(player.getMainHandItem().is(ModItems.SALT_WATER_BUCKET.get()) && level.getBlockState(pos).is(Blocks.CAULDRON), "Full cauldron bucket extraction wrong");
        use(level,pos,player,ModItems.WOODEN_SALT_WATER_BUCKET.get());
        check(player.getMainHandItem().is(ModItems.WOODEN_BUCKET.get()) && player.getMainHandItem().getDamageValue() == 1 && fill(level,pos) == 3, "Wooden bucket pouring lost durability or fluid");
        use(level,pos,player,ModItems.WOODEN_BUCKET.get());
        check(player.getMainHandItem().is(ModItems.WOODEN_SALT_WATER_BUCKET.get()) && player.getMainHandItem().getDamageValue() == 1
                && level.getBlockState(pos).is(Blocks.CAULDRON), "Wooden bucket filling lost fluid or durability");
        var creative = helper.makeMockPlayer(GameType.CREATIVE);
        GameType.CREATIVE.updatePlayerAbilities(creative.getAbilities());
        use(level,pos,creative,ModItems.SALT_WATER_BUCKET.get());
        check(creative.getMainHandItem().is(ModItems.SALT_WATER_BUCKET.get()) && fill(level,pos) == 3, "Creative bucket was consumed");
        var drops = Block.getDrops(level.getBlockState(pos), level, pos, null);
        check(drops.size() == 1 && drops.getFirst().is(Items.CAULDRON), "Filled cauldron drop wrong");
        check(DispenserBlock.DISPENSER_REGISTRY.get(ModItems.SALT_WATER_BUCKET.get()) == DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET), "Filled bucket dispenser behavior missing");
    }

    static void flow(GameTestHelper helper) {
        var level = helper.getLevel();
        var center = helper.absolutePos(new BlockPos(4,4,4));
        for (var pos : BlockPos.betweenClosed(center.offset(-2,-1,-2), center.offset(2,-1,2))) level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
        for (var pos : BlockPos.betweenClosed(center.offset(-2,0,-2), center.offset(2,0,2))) {
            if (Math.abs(pos.getX()-center.getX()) == 2 || Math.abs(pos.getZ()-center.getZ()) == 2) level.setBlockAndUpdate(pos, Blocks.STONE.defaultBlockState());
            else level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
        level.setBlockAndUpdate(center.west(), ModBlocks.SALT_WATER.get().defaultBlockState());
        level.setBlockAndUpdate(center.east(), ModBlocks.SALT_WATER.get().defaultBlockState());
        helper.runAfterDelay(20, () -> {
            var fluid = level.getFluidState(center);
            check(fluid.getType() == ModFluids.FLOWING_SALT_WATER.get() && !fluid.isSource(), "Fluid did not flow, lost identity, or created an infinite source");
            check(level.getFluidState(center.west()).isSource() && level.getFluidState(center.east()).isSource(), "Flow consumed original sources");
            var west = ModBlocks.SALT_WATER.get().pickupBlock(null, level, center.west(), level.getBlockState(center.west()));
            check(west.is(ModItems.SALT_WATER_BUCKET.get()), "Could not drain original Salt Water source");
        });
        helper.runAfterDelay(45, () -> {
            check(!level.getFluidState(center.west()).isSource() && !level.getFluidState(center).isSource(),
                    "Drained Salt Water source regenerated from adjacent fluid");
            var east = ModBlocks.SALT_WATER.get().pickupBlock(null, level, center.east(), level.getBlockState(center.east()));
            check(east.is(ModItems.SALT_WATER_BUCKET.get()), "Remaining original Salt Water source changed");
        });
        helper.runAfterDelay(85, () -> {
            for (var pos : BlockPos.betweenClosed(center.offset(-1,0,-1),center.offset(1,0,1)))
                check(level.getFluidState(pos).isEmpty(), "Salt Water remained after all sources were removed");
            helper.succeed();
        });
    }
    private static int fill(net.minecraft.server.level.ServerLevel level, BlockPos pos) {
        var state=level.getBlockState(pos);
        check(state.is(ModBlocks.SALT_WATER_CAULDRON.get()), "Cauldron is not Salt Water");
        return state.getValue(LayeredCauldronBlock.LEVEL);
    }
    private static void use(net.minecraft.server.level.ServerLevel level, BlockPos pos, Player player, Item item) {
        var stack=new ItemStack(item);
        player.setItemInHand(InteractionHand.MAIN_HAND,stack);
        level.getBlockState(pos).useItemOn(stack,level,player,InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos),Direction.UP,pos,false));
    }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
    private SaltWaterTests() {}
}
