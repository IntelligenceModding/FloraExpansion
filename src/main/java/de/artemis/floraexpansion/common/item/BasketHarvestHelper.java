package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.util.InventoryUtils;
import de.artemis.floraexpansion.common.block.HarvestableBlock;
import de.artemis.floraexpansion.common.block.FlaxBlock;
import de.artemis.floraexpansion.common.block.SmallBlueberryBushBlock;
import de.artemis.floraexpansion.common.registry.ModTags;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
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
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import de.artemis.floraexpansion.common.config.ServerConfig;

public final class BasketHarvestHelper {
    private BasketHarvestHelper() {
    }

    public static boolean tryHarvest(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || context.getLevel().isClientSide()) {
            return false;
        }

        BlockPos pos = context.getClickedPos();
        BlockState state = context.getLevel().getBlockState(pos);
        ItemStack basket = context.getItemInHand();

        boolean harvested = ServerConfig.basketCrops.get() && (tryHarvestFlax(context, state, basket)
                || tryHarvestCrop(context, state, basket)
                || tryHarvestNetherWart(context, state, basket)
                || tryHarvestCocoa(context, state, basket)
                || tryHarvestPlant(context, state, basket)
                || tryHarvestSweetBerryBush(context, state, basket)
                || tryHarvestGlowBerries(context, state, basket)
                || tryHarvestInteractivePlant(context, state, basket))
                || ServerConfig.basketGathering.get() && tryGatherPlant(context, state, basket);
        if (harvested) {
            InventoryUtils.syncPlayerInventory(player);
        }
        return harvested;
    }

    private static boolean tryHarvestFlax(UseOnContext context, BlockState state, ItemStack basket) {
        if (!(state.getBlock() instanceof FlaxBlock) || context.getLevel().isClientSide()) {
            return false;
        }

        BlockPos lowerPos = state.getValue(FlaxBlock.HALF) == DoubleBlockHalf.UPPER
                ? context.getClickedPos().below()
                : context.getClickedPos();
        BlockState lowerState = context.getLevel().getBlockState(lowerPos);
        if (!(lowerState.getBlock() instanceof FlaxBlock)
                || lowerState.getValue(FlaxBlock.HALF) != DoubleBlockHalf.LOWER) {
            return false;
        }

        int age = lowerState.getValue(FlaxBlock.AGE);
        if (age < 2) {
            return false;
        }

        List<ItemStack> drops = Block.getDrops(lowerState, (net.minecraft.server.level.ServerLevel) context.getLevel(), lowerPos, null, context.getPlayer(), ItemStack.EMPTY);
        ((FlaxBlock) lowerState.getBlock()).growCropBy(context.getLevel(), lowerPos, lowerState, -age);
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
        if (!(state.getBlock() instanceof CocoaBlock) || context.getLevel().isClientSide()) {
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

    private static boolean tryHarvestPlant(UseOnContext context, BlockState state, ItemStack basket) {
        if (!(state.getBlock() instanceof HarvestableBlock plant) || !plant.canHarvest(state)) {
            return false;
        }
        var drops = plant.getHarvestDrops(state, (net.minecraft.server.level.ServerLevel) context.getLevel(), context.getClickedPos(), context.getPlayer());
        depositOrDrop(context.getLevel(), context.getClickedPos(), basket, drops);
        playBerryHarvestEffects(context, plant.getHarvestedState(state));
        return true;
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

        int amount = 1 + context.getLevel().getRandom().nextInt(2);
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
        context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.CAVE_VINES_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + context.getLevel().getRandom().nextFloat() * 0.4F);
        context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(), GameEvent.Context.of(context.getPlayer(), resetState));
        return true;
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

    public static boolean canGather(BlockState state) {
        return state.is(ModTags.Blocks.BASKET_GATHERABLE) && !state.hasBlockEntity()
                && !(state.getBlock() instanceof HarvestableBlock)
                && !(state.getBlock() instanceof CropBlock)
                && !(state.getBlock() instanceof FlaxBlock)
                && !(state.getBlock() instanceof NetherWartBlock)
                && !(state.getBlock() instanceof CocoaBlock)
                && !(state.getBlock() instanceof net.minecraft.world.level.block.SweetBerryBushBlock);
    }

    private static boolean tryGatherPlant(UseOnContext context, BlockState state, ItemStack basket) {
        if (!canGather(state)) return false;
        var level = (net.minecraft.server.level.ServerLevel) context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) {
            pos = pos.below();
            BlockState lower = level.getBlockState(pos);
            if (!lower.is(state.getBlock()) || !canGather(lower)) return false;
            state = lower;
        }
        if (!canBreakForGathering(context, pos, state)) return false;
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            var upper = level.getBlockState(pos.above());
            if (upper.is(state.getBlock()) && !canBreakForGathering(context, pos.above(), upper)) return false;
        }

        var drops = Block.getDrops(state, level, pos, null, context.getPlayer(), basket);
        if (!level.destroyBlock(pos, false, context.getPlayer())) return false;
        depositOrDrop(level, pos, basket, drops);
        return true;
    }

    private static boolean canBreakForGathering(UseOnContext context, BlockPos pos, BlockState state) {
        Player player = context.getPlayer();
        var level = context.getLevel();
        return player != null && !player.isSpectator() && level.mayInteract(player, pos)
                && (player.mayBuild() || context.getItemInHand().canBreakBlockInAdventureMode(new BlockInWorld(level, pos, false)))
                && !NeoForge.EVENT_BUS.post(new BreakBlockEvent(level, pos, state, player)).isCanceled();
    }

    private static void playBerryHarvestEffects(UseOnContext context, BlockState resetState) {
        context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + context.getLevel().getRandom().nextFloat() * 0.4F);
        context.getLevel().setBlock(context.getClickedPos(), resetState, 3);
        context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, context.getClickedPos(), GameEvent.Context.of(context.getPlayer(), resetState));
    }

    private static void playCropHarvestEffects(UseOnContext context, BlockPos pos, BlockState harvestedState, BlockState resetState) {
        context.getLevel().setBlock(pos, resetState, 3);
        context.getLevel().levelEvent(2001, pos, Block.getId(harvestedState));
        context.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(context.getPlayer(), resetState));
    }

    private static boolean isLikelyInteractivePlant(BlockState state) {
        return canGather(state)
                || state.getBlock() instanceof BushBlock
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
