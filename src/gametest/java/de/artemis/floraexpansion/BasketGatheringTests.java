package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.function.Consumer;

public final class BasketGatheringTests {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    private BasketGatheringTests() {}

    public static void gatherPlants(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (Block block : new Block[]{Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM,
                Blocks.CRIMSON_FUNGUS, Blocks.WARPED_FUNGUS, Blocks.CRIMSON_ROOTS,
                Blocks.WARPED_ROOTS, Blocks.DANDELION, Blocks.POPPY, Blocks.OAK_SAPLING,
                Blocks.AZALEA, Blocks.SUNFLOWER, Blocks.ROSE_BUSH, Blocks.PUMPKIN, Blocks.MELON}) {
            var state = block.defaultBlockState();
            boolean tall = state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
            for (int half = 0; half < (tall ? 2 : 1); half++) {
                helper.setBlock(POS, Blocks.AIR);
                helper.setBlock(POS.above(), Blocks.AIR);
                helper.setBlock(POS.below(), Blocks.MYCELIUM);
                helper.setBlock(POS, state);
                if (tall) helper.setBlock(POS.above(), state.setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
                var basket = new ItemStack(ModItems.BASKET.get());
                player.setItemInHand(InteractionHand.MAIN_HAND, basket);
                check(basket.useOn(context(helper, player, half == 1 ? POS.above() : POS)).consumesAction(), "Cannot gather " + block);
                check(helper.getBlockState(POS).isAir(), "Gathered block remained: " + block);
                if (tall) check(helper.getBlockState(POS.above()).isAir(), "Upper half remained: " + block);
                Item expected = block == Blocks.MELON ? Items.MELON_SLICE : block.asItem();
                int count = count(basket, expected);
                check(block == Blocks.MELON ? count >= 3 && count <= 7 : count == 1,
                        "Wrong gathering drops for " + block + ": " + count);
                check(droppedCount(helper, expected) == 0, "Gathering duplicated or spilled collected drops: " + block);
            }
        }
    }

    public static void overflow(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var basket = new ItemStack(ModItems.BASKET.get());
        check(BasketItem.insertItem(basket, new ItemStack(Items.STONE, BasketItem.SLOT_COUNT * 64)) == BasketItem.SLOT_COUNT * 64,
                "Cannot prepare full basket");
        player.setItemInHand(InteractionHand.MAIN_HAND, basket);
        helper.setBlock(POS.below(), Blocks.MYCELIUM);
        helper.setBlock(POS, Blocks.BROWN_MUSHROOM);
        var context = context(helper, player, POS);
        check(basket.useOn(context).consumesAction() && helper.getBlockState(POS).isAir(), "Full basket cannot gather");
        check(count(basket, Items.STONE) == BasketItem.SLOT_COUNT * 64, "Gathering overwrote basket contents");
        check(droppedCount(helper, Items.BROWN_MUSHROOM) == 1, "Overflow lost or duplicated mushroom");
        check(!basket.useOn(context).consumesAction() && droppedCount(helper, Items.BROWN_MUSHROOM) == 1,
                "Repeated gathering duplicated the removed plant");
    }

    public static void restrictions(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var basket = new ItemStack(ModItems.BASKET.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, basket);
        helper.setBlock(POS.below(), Blocks.MYCELIUM);
        helper.setBlock(POS, Blocks.BROWN_MUSHROOM);
        Consumer<BreakBlockEvent> cancel = event -> {
            if (event.getPlayer() == player) event.setCanceled(true);
        };
        NeoForge.EVENT_BUS.addListener(cancel);
        try {
            check(!basket.useOn(context(helper, player, POS)).consumesAction(), "Canceled block break still gathered");
            check(helper.getBlockState(POS).is(Blocks.BROWN_MUSHROOM) && count(basket, Items.BROWN_MUSHROOM) == 0,
                    "Canceled gathering changed plant or inventory");
        } finally {
            NeoForge.EVENT_BUS.unregister(cancel);
        }
        player.getAbilities().mayBuild = false;
        check(!basket.useOn(context(helper, player, POS)).consumesAction(), "Gathering bypassed adventure permissions");
        player.getAbilities().mayBuild = true;
        for (var block : new Block[]{Blocks.STONE, ModBlocks.APPLE_LEAVES.get(), ModBlocks.ORCHARD_CHERRY_LEAVES.get()}) {
            helper.setBlock(POS, block);
            check(!basket.useOn(context(helper, player, POS)).consumesAction(), "Basket broke an unrelated block or unripe fruit plant");
            check(helper.getBlockState(POS).is(block), "Failed gathering removed block");
        }
    }

    private static UseOnContext context(GameTestHelper helper, Player player, BlockPos relative) {
        var pos = helper.absolutePos(relative);
        return new UseOnContext(player, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
    }

    private static int count(ItemStack basket, Item item) {
        return BasketItem.getContents(basket).stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
    }

    private static int droppedCount(GameTestHelper helper, Item item) {
        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(helper.absolutePos(POS)).inflate(2),
                        entity -> entity.getItem().is(item)).stream().mapToInt(entity -> entity.getItem().getCount()).sum();
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
