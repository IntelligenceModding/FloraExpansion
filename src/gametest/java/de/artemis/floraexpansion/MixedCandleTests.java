package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.StrawberryCakeBlock;
import de.artemis.floraexpansion.common.block.StrawberryCakeWithCandleBlock;
import de.artemis.floraexpansion.common.block.entity.StrawberryCakeBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;

import java.util.List;

final class MixedCandleTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var local = new BlockPos(1, 2, 1);
        var pos = helper.absolutePos(local);
        helper.setBlock(local.below(), Blocks.STONE);
        helper.setBlock(local, ModBlocks.STRAWBERRY_CAKE.get());
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        List<CandleBlock> colors = List.of((CandleBlock) Blocks.DYED_CANDLE.green(), (CandleBlock) Blocks.DYED_CANDLE.red(), (CandleBlock) Blocks.DYED_CANDLE.blue());
        for (int slot = 0; slot < colors.size(); slot++) {
            var stack = new ItemStack(colors.get(slot), 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            check(level.getBlockState(pos).useItemOn(stack, level, player, InteractionHand.MAIN_HAND, hit).consumesAction(), "Mixed candle rejected");
            check(stack.getCount() == 1, "Adding a candle consumed wrong amount");
            var entity = (StrawberryCakeBlockEntity) level.getBlockEntity(pos);
            check(entity.getCandles().equals(colors.subList(0, slot + 1)), "Cake lost individual candle colors");
            if (slot == 0) {
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FLINT_AND_STEEL));
                player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
            }
            check(level.getBlockState(pos).getValue(StrawberryCakeWithCandleBlock.LIT), "Adding candle extinguished cake");
        }
        var state = level.getBlockState(pos);
        check(new StrawberryCakeBlockEntity(pos, state).getCandles().equals(java.util.Collections.nCopies(3, colors.getFirst())),
                "Cake saved without color data lost its original candles");
        var entity = (StrawberryCakeBlockEntity) level.getBlockEntity(pos);
        var saved = entity.saveWithFullMetadata(level.registryAccess());
        var restored = (StrawberryCakeBlockEntity) BlockEntity.loadStatic(pos, state, saved, level.registryAccess());
        check(restored != null && restored.getCandles().equals(colors), "Mixed candles did not survive save/load");
        var packet = entity.getUpdatePacket();
        check(packet != null && packet.getTag().equals(entity.getUpdateTag(level.registryAccess())), "Candle colors missing from client update");

        var drops = Block.getDrops(state, level, pos, entity);
        check(drops.stream().mapToInt(ItemStack::getCount).sum() == 3, "Wrong candle drop count");
        for (var candle : colors) check(drops.stream().filter(s -> s.is(candle.asItem())).mapToInt(ItemStack::getCount).sum() == 1, "Dropped wrong candle color");
        var fourth = new ItemStack(Items.CANDLE);
        player.setItemInHand(InteractionHand.MAIN_HAND, fourth);
        check(state.useItemOn(fourth, level, player, InteractionHand.MAIN_HAND, hit) == InteractionResult.FAIL
                && fourth.getCount() == 1 && entity.getCandles().equals(colors), "Fourth candle changed mixed cake");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        state.useItemOn(ItemStack.EMPTY, level, player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos).add(0, 0.4, 0), Direction.UP, pos, false));
        check(!level.getBlockState(pos).getValue(StrawberryCakeWithCandleBlock.LIT)
                && entity.getCandles().equals(colors), "Extinguishing changed colors");
        player.getFoodData().setFoodLevel(0);
        level.getBlockState(pos).useWithoutItem(level, player, hit);
        check(level.getBlockState(pos).is(ModBlocks.STRAWBERRY_CAKE.get())
                && StrawberryCakeBlock.portionsEaten(level.getBlockState(pos)) == 1, "Mixed cake lost fourteen-portion behavior");
        var actual = level.getEntitiesOfClass(ItemEntity.class, new net.minecraft.world.phys.AABB(pos).inflate(2));
        for (var candle : colors) check(actual.stream().map(ItemEntity::getItem).filter(s -> s.is(candle.asItem()))
                .mapToInt(ItemStack::getCount).sum() == 1, "Eating lost a candle color");
    }

    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
}
