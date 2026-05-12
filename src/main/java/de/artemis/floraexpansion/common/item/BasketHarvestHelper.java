package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.block.LargeBlueberryBushBlock;
import de.artemis.floraexpansion.common.block.FruitingCherryLeavesBlock;
import de.artemis.floraexpansion.common.block.FruitingOakLeavesBlock;
import de.artemis.floraexpansion.common.block.FlaxCropBlock;
import de.artemis.floraexpansion.common.block.SmallBlueberryBushBlock;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class BasketHarvestHelper {
    private BasketHarvestHelper() {
    }

    public static boolean tryHarvest(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        ItemStack basket = context.getItemInHand();

        if (tryHarvestFlax(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        if (tryHarvestCrop(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        if (tryHarvestNetherWart(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        if (tryHarvestCocoa(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        if (tryHarvestBlueberryBush(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        if (tryHarvestSweetBerryBush(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        if (tryHarvestGlowBerries(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        if (tryHarvestFruitingLeaves(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        if (tryHarvestInteractivePlant(context, state, basket)) {
            BasketItem.syncPlayerInventory(player);
            return true;
        }

        return false;
    }

    private static boolean tryHarvestFlax(UseOnContext context, BlockState state, ItemStack basket) {
        if (!(state.getBlock() instanceof FlaxCropBlock) || context.getLevel().isClientSide) {
            return false;
        }

        BlockPos lowerPos = state.getValue(FlaxCropBlock.HALF) == DoubleBlockHalf.UPPER
                ? context.getClickedPos().below()
                : context.getClickedPos();
        BlockState lowerState = context.getLevel().getBlockState(lowerPos);
        if (!(lowerState.getBlock() instanceof FlaxCropBlock)
                || lowerState.getValue(FlaxCropBlock.HALF) != DoubleBlockHalf.LOWER) {
            return false;
        }

        int age = lowerState.getValue(FlaxCropBlock.AGE);
        if (age < 2) {
            return false;
        }

        List<ItemStack> drops = Block.getDrops(lowerState, (net.minecraft.server.level.ServerLevel) context.getLevel(), lowerPos, null, context.getPlayer(), ItemStack.EMPTY);
        ((FlaxCropBlock) lowerState.getBlock()).growCropBy(context.getLevel(), lowerPos, lowerState, -age);
        context.getLevel().levelEvent(2001, lowerPos, Block.getId(lowerState));
        context.getLevel().levelEvent(2001, lowerPos.above(), Block.getId(state));
        depositOrDrop(context.getLevel(), lowerPos, basket, drops);
        return true;
    }

    private static boolean tryHarvestCrop(UseOnContext context, BlockState state, ItemStack basket) {
        if (!(state.getBlock() instanceof CropBlock cropBlock) || !cropBlock.isMaxAge(state)) {
            return false;
        }

        List<ItemStack> drops = Block.getDrops(state, (net.minecraft.server.level.ServerLevel) context.getLevel(), context.getClickedPos(), null, context.getPlayer(), ItemStack.EMPTY);
        ItemStack replantStack = findCropReplantStack(state, drops);
        if (replantStack.isEmpty() || !consumeOne(drops, replantStack)) {
            return false;
        }

        BlockState replanted = resetAgeState(state);
        if (replanted == state) {
            return false;
        }

        playCropHarvestEffects(context, context.getClickedPos(), state, replanted);
        depositOrDrop(context.getLevel(), context.getClickedPos(), basket, drops);
        return true;
    }

    private static boolean tryHarvestNetherWart(UseOnContext context, BlockState state, ItemStack basket) {
        if (!(state.getBlock() instanceof NetherWartBlock) || state.getValue(NetherWartBlock.AGE) < 3) {
            return false;
        }

        List<ItemStack> drops = Block.getDrops(state, (net.minecraft.server.level.ServerLevel) context.getLevel(), context.getClickedPos(), null, context.getPlayer(), ItemStack.EMPTY);
        ItemStack replantStack = new ItemStack(Items.NETHER_WART);
        if (!consumeOne(drops, replantStack)) {
            return false;
        }

        playCropHarvestEffects(context, context.getClickedPos(), state, state.setValue(NetherWartBlock.AGE, 0));
        depositOrDrop(context.getLevel(), context.getClickedPos(), basket, drops);
        return true;
    }

    private static boolean tryHarvestCocoa(UseOnContext context, BlockState state, ItemStack basket) {
        if (!(state.getBlock() instanceof CocoaBlock) || context.getLevel().isClientSide) {
            return false;
        }

        if (state.getValue(CocoaBlock.AGE) < 2) {
            return false;
        }

        List<ItemStack> drops = Block.getDrops(state, (net.minecraft.server.level.ServerLevel) context.getLevel(), context.getClickedPos(), null, context.getPlayer(), ItemStack.EMPTY);
        if (!consumeOne(drops, new ItemStack(Items.COCOA_BEANS))) {
            return false;
        }

        playCropHarvestEffects(context, context.getClickedPos(), state, state.setValue(CocoaBlock.AGE, 0));
        depositOrDrop(context.getLevel(), context.getClickedPos(), basket, drops);
        return true;
    }

    private static boolean tryHarvestBlueberryBush(UseOnContext context, BlockState state, ItemStack basket) {
        if (state.getBlock() instanceof SmallBlueberryBushBlock) {
            int age = state.getValue(SmallBlueberryBushBlock.AGE);
            if (age < 3) {
                return false;
            }

            int amount = 1 + context.getLevel().random.nextInt(2);
            depositOrDrop(context.getLevel(), context.getClickedPos(), basket, List.of(new ItemStack(ModItems.BLUEBERRIES.get(), amount)));
            playBerryHarvestEffects(context, state.setValue(SmallBlueberryBushBlock.AGE, 2));
            return true;
        }

        if (state.getBlock() instanceof LargeBlueberryBushBlock) {
            int age = state.getValue(LargeBlueberryBushBlock.AGE);
            if (age < 2) {
                return false;
            }

            int amount = 1 + context.getLevel().random.nextInt(2);
            if (age == LargeBlueberryBushBlock.MAX_AGE) {
                amount *= 2;
            }

            depositOrDrop(context.getLevel(), context.getClickedPos(), basket, List.of(new ItemStack(ModItems.BLUEBERRIES.get(), amount)));
            playBerryHarvestEffects(context, state.setValue(LargeBlueberryBushBlock.AGE, 1));
            return true;
        }

        return false;
    }

    private static boolean tryHarvestSweetBerryBush(UseOnContext context, BlockState state, ItemStack basket) {
        if (!(state.getBlock() instanceof net.minecraft.world.level.block.SweetBerryBushBlock)
                || state.getBlock() instanceof SmallBlueberryBushBlock) {
            return false;
        }

        int age = state.getValue(net.minecraft.world.level.block.SweetBerryBushBlock.AGE);
        if (age <= 1) {
            return false;
        }

        int amount = 1 + context.getLevel().random.nextInt(2);
        if (age == 3) {
            amount++;
        }

        depositOrDrop(context.getLevel(), context.getClickedPos(), basket, List.of(new ItemStack(Items.SWEET_BERRIES, amount)));
        playBerryHarvestEffects(context, state.setValue(net.minecraft.world.level.block.SweetBerryBushBlock.AGE, 1));
        return true;
    }

    private static boolean tryHarvestGlowBerries(UseOnContext context, BlockState state, ItemStack basket) {
        if ((! (state.getBlock() instanceof net.minecraft.world.level.block.CaveVinesBlock))
                && (!(state.getBlock() instanceof net.minecraft.world.level.block.CaveVinesPlantBlock))) {
            return false;
        }

        if (!state.hasProperty(BlockStateProperties.BERRIES) || !state.getValue(BlockStateProperties.BERRIES)) {
            return false;
        }

        depositOrDrop(context.getLevel(), context.getClickedPos(), basket, List.of(new ItemStack(Items.GLOW_BERRIES)));
        BlockState resetState = state.setValue(BlockStateProperties.BERRIES, false);
        context.getLevel().setBlock(context.getClickedPos(), resetState, 3);
        context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + context.getLevel().random.nextFloat() * 0.4F);
        context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(), GameEvent.Context.of(context.getPlayer(), resetState));
        return true;
    }

    private static boolean tryHarvestFruitingLeaves(UseOnContext context, BlockState state, ItemStack basket) {
        if (state.getBlock() instanceof FruitingCherryLeavesBlock) {
            int age = state.getValue(FruitingCherryLeavesBlock.AGE);
            if (age <= 0) {
                return false;
            }

            int amount = switch (age) {
                case 1 -> 1;
                case 2 -> 2 + context.getLevel().random.nextInt(2);
                case 3 -> 3 + context.getLevel().random.nextInt(2);
                default -> 0;
            };

            if (amount <= 0) {
                return false;
            }

            depositOrDrop(context.getLevel(), context.getClickedPos(), basket, List.of(new ItemStack(ModItems.CHERRIES.get(), amount)));
            context.getLevel().setBlock(context.getClickedPos(), state.setValue(FruitingCherryLeavesBlock.AGE, 0), 3);
            context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(), GameEvent.Context.of(context.getPlayer(), state.setValue(FruitingCherryLeavesBlock.AGE, 0)));
            return true;
        }

        if (state.getBlock() instanceof FruitingOakLeavesBlock) {
            if (state.getValue(FruitingOakLeavesBlock.AGE) < FruitingOakLeavesBlock.MAX_AGE) {
                return false;
            }

            depositOrDrop(context.getLevel(), context.getClickedPos(), basket, List.of(new ItemStack(Items.APPLE)));
            context.getLevel().setBlock(context.getClickedPos(), state.setValue(FruitingOakLeavesBlock.AGE, 0), 3);
            context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(), GameEvent.Context.of(context.getPlayer(), state.setValue(FruitingOakLeavesBlock.AGE, 0)));
            return true;
        }

        return false;
    }

    private static boolean tryHarvestInteractivePlant(UseOnContext context, BlockState state, ItemStack basket) {
        if (!isLikelyInteractivePlant(state)) {
            return false;
        }

        Player player = context.getPlayer();
        if (player == null) {
            return false;
        }

        BlockPos pos = context.getClickedPos();
        BlockHitResult hitResult = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), pos, false);
        List<ItemStack> inventoryBefore = snapshotInventory(player.getInventory());
        Set<Integer> nearbyItemsBefore = snapshotNearbyItemIds(context.getLevel(), pos);

        InteractionResult result = state.useWithoutItem(context.getLevel(), player, hitResult);
        if (!result.consumesAction()) {
            return false;
        }

        moveNewInventoryItemsIntoBasket(player.getInventory(), inventoryBefore, basket);
        absorbNewNearbyItemEntities(context, nearbyItemsBefore, basket);
        return true;
    }

    private static void playBerryHarvestEffects(UseOnContext context, BlockState resetState) {
        context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + context.getLevel().random.nextFloat() * 0.4F);
        context.getLevel().setBlock(context.getClickedPos(), resetState, 3);
        context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(), GameEvent.Context.of(context.getPlayer(), resetState));
    }

    private static void playCropHarvestEffects(UseOnContext context, BlockPos pos, BlockState harvestedState, BlockState resetState) {
        context.getLevel().setBlock(pos, resetState, 3);
        context.getLevel().levelEvent(2001, pos, Block.getId(harvestedState));
        context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(context.getPlayer(), resetState));
    }

    private static boolean isLikelyInteractivePlant(BlockState state) {
        return state.getBlock() instanceof BushBlock
                || state.getBlock() instanceof GrowingPlantHeadBlock
                || state.getBlock() instanceof GrowingPlantBodyBlock;
    }

    private static ItemStack findCropReplantStack(BlockState state, List<ItemStack> drops) {
        for (ItemStack stack : drops) {
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == state.getBlock()) {
                return stack.copyWithCount(1);
            }
        }

        return ItemStack.EMPTY;
    }

    private static boolean consumeOne(List<ItemStack> drops, ItemStack target) {
        for (ItemStack drop : drops) {
            if (!drop.isEmpty() && ItemStack.isSameItemSameComponents(drop, target)) {
                drop.shrink(1);
                return true;
            }
        }

        return false;
    }

    private static void depositOrDrop(net.minecraft.world.level.Level level, BlockPos pos, ItemStack basket, List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            if (drop.isEmpty()) {
                continue;
            }

            BasketItem.insertItem(basket, drop);
            if (!drop.isEmpty()) {
                Containers.dropItemStack(level, pos.getX() + 0.5D, pos.getY() + 0.8D, pos.getZ() + 0.5D, drop);
            }
        }
    }

    private static List<ItemStack> snapshotInventory(Inventory inventory) {
        List<ItemStack> snapshot = new ArrayList<>(inventory.getContainerSize());
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            snapshot.add(inventory.getItem(i).copy());
        }

        return snapshot;
    }

    private static void moveNewInventoryItemsIntoBasket(Inventory inventory, List<ItemStack> before, ItemStack basket) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack current = inventory.getItem(i);
            if (current.isEmpty()) {
                continue;
            }

            ItemStack old = before.get(i);
            int gained = getGainedCount(old, current);
            if (gained <= 0) {
                continue;
            }

            ItemStack extracted = current.copyWithCount(gained);
            int inserted = BasketItem.insertItem(basket, extracted);
            if (inserted <= 0) {
                continue;
            }

            current.shrink(inserted);
            if (current.isEmpty()) {
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }

    private static int getGainedCount(ItemStack before, ItemStack after) {
        if (after.isEmpty()) {
            return 0;
        }

        if (before.isEmpty()) {
            return after.getCount();
        }

        if (ItemStack.isSameItemSameComponents(before, after)) {
            return Math.max(0, after.getCount() - before.getCount());
        }

        return after.getCount();
    }

    private static Set<Integer> snapshotNearbyItemIds(net.minecraft.world.level.Level level, BlockPos pos) {
        Set<Integer> ids = new HashSet<>();
        for (ItemEntity itemEntity : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1.5D))) {
            ids.add(itemEntity.getId());
        }

        return ids;
    }

    private static void absorbNewNearbyItemEntities(UseOnContext context, Set<Integer> beforeIds, ItemStack basket) {
        Direction face = context.getClickedFace();
        AABB area = new AABB(context.getClickedPos().relative(face)).inflate(1.75D);

        for (ItemEntity itemEntity : context.getLevel().getEntitiesOfClass(ItemEntity.class, area)) {
            if (beforeIds.contains(itemEntity.getId())) {
                continue;
            }

            ItemStack entityStack = itemEntity.getItem();
            BasketItem.insertItem(basket, entityStack);
            if (entityStack.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.setItem(entityStack);
            }
        }
    }

    private static BlockState resetAgeState(BlockState state) {
        for (var property : state.getProperties()) {
            if (property instanceof IntegerProperty integerProperty && integerProperty.getName().equals("age")) {
                return state.setValue(integerProperty, integerProperty.getPossibleValues().stream().min(Integer::compareTo).orElse(0));
            }
        }

        return state;
    }
}
