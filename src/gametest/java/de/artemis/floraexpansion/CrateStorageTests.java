package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.inventory.CrateMenu;
import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.GameType;

public final class CrateStorageTests {
    private static final BlockPos POS = new BlockPos(1, 2, 1);
    private static final int HOTBAR_START = CrateMenu.VISIBLE_SLOT_COUNT + 27;

    private CrateStorageTests() {}

    public static void restrictions(GameTestHelper helper) {
        var crate = crate(helper);
        for (Item item : new Item[]{Items.WHEAT_SEEDS, Items.OAK_SAPLING, Items.SWEET_BERRIES,
                Items.GLOW_BERRIES, Items.BONE_MEAL, Items.WHEAT, Items.CARROT, Items.POTATO,
                Items.COCOA_BEANS, Items.NETHER_WART, Items.SUGAR_CANE, Items.CACTUS,
                Items.BAMBOO, Items.BROWN_MUSHROOM, Items.DANDELION, Items.EGG,
                Items.HONEYCOMB, Items.MILK_BUCKET, Items.SHEARS, Items.IRON_HOE,
                ModItems.FLAX_SEEDS.get(), ModItems.FLAX_FIBER.get(), ModItems.FLAX_FLOWER.get(),
                ModItems.BLUEBERRIES.get(), ModItems.STRAWBERRY.get(), ModItems.CHERRIES.get(),
                ModItems.PRICKLY_PEAR.get(), ModBlocks.CHERRY_PIT.get().asItem(),
                ModBlocks.APPLE_LEAVES.get().asItem(), ModBlocks.ORCHARD_CHERRY_LEAVES.get().asItem(),
                ModBlocks.APPLE_CORE.get().asItem()}) {
            crate.clearContent();
            var stack = new ItemStack(item);
            check(crate.canPlaceItem(0, stack), "Farming item rejected: " + item);
            check(crate.insertItem(stack) == 1, "Direct farming deposit failed: " + item);
        }

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = new CrateMenu(1, player.getInventory(), crate);
        var clientMenu = new CrateMenu(2, player.getInventory());
        for (Item item : new Item[]{Items.BIRCH_LOG, Items.DIAMOND, Items.STONE, Items.IRON_INGOT, Items.STICK,
                Items.IRON_SWORD, ModItems.PEBBLES.get(), ModItems.CACTUS_BOAT.get()}) {
            crate.clearContent();
            var stack = new ItemStack(item, 1);
            var result = helper.getBlockState(POS).useItemOn(stack, helper.getLevel(), player,
                    net.minecraft.world.InteractionHand.MAIN_HAND,
                    new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.atCenterOf(helper.absolutePos(POS)),
                            net.minecraft.core.Direction.UP, helper.absolutePos(POS), false));
            check(result == net.minecraft.world.InteractionResult.FAIL,
                    "Rejected item did not stop interaction without a hand swing: " + item);
            check(stack.getCount() == 1 && crate.isEmpty(), "Rejected interaction changed inventory");
            check(!crate.canPlaceItem(0, stack), "Unrelated item accepted: " + item);
            check(!menu.slots.get(0).mayPlace(stack), "Server slot bypass: " + item);
            check(!clientMenu.slots.get(0).mayPlace(stack), "Client slot bypass: " + item);
            check(crate.insertItem(stack) == 0, "Direct deposit bypass: " + item);
            crate.setItem(0, stack.copy());
            crate.setItem(1, stack.copy(), true);
            check(crate.isEmpty(), "Slot setter bypass: " + item);
            player.getInventory().setItem(0, stack.copy());
            check(menu.quickMoveStack(player, HOTBAR_START).isEmpty(), "Shift-click bypass: " + item);
            check(crate.isEmpty() && player.getInventory().getItem(0).getCount() == 1, "Rejected transfer consumed an item");
        }
        player.getInventory().setItem(0, new ItemStack(Items.BONE_MEAL, 12));
        check(!menu.quickMoveStack(player, HOTBAR_START).isEmpty() && crate.getTotalItemCount() == 12,
                "Allowed shift-click failed");
        check(crate.insertItem(new ItemStack(Items.WHEAT_SEEDS)) == 0, "Single-item restriction must remain");

        crate.clearContent();
        var basket = new ItemStack(ModItems.BASKET.get());
        var contents = NonNullList.withSize(BasketItem.SLOT_COUNT, ItemStack.EMPTY);
        contents.set(0, new ItemStack(Items.DIAMOND, 3));
        contents.set(1, new ItemStack(Items.WHEAT_SEEDS, 8));
        BasketItem.saveContents(basket, contents);
        check(BasketItem.tryDepositIntoContainer(helper.getLevel(), helper.absolutePos(POS), player, basket), "Basket deposit failed");
        check(crate.getTotalItemCount() == 8 && crate.getStoredTypeCopy().is(Items.WHEAT_SEEDS), "Basket bypassed tag");
        check(BasketItem.getContents(basket).get(0).getCount() == 3, "Rejected basket items lost");
        crate.clearContent();
        var rejected = net.minecraft.world.level.block.entity.HopperBlockEntity.addItem(
                null, crate, new ItemStack(Items.DIAMOND, 3), net.minecraft.core.Direction.UP);
        check(rejected.getCount() == 3 && crate.isEmpty(), "Hopper bypassed tag or consumed rejected items");
        var accepted = net.minecraft.world.level.block.entity.HopperBlockEntity.addItem(
                null, crate, new ItemStack(Items.BONE_MEAL, 6), net.minecraft.core.Direction.UP);
        check(accepted.isEmpty() && crate.getTotalItemCount() == 6, "Hopper rejected farming supply");
    }

    public static void existingContents(GameTestHelper helper) {
        var crate = crate(helper);
        var items = NonNullList.withSize(CrateBlockEntity.CONTAINER_SIZE, ItemStack.EMPTY);
        items.set(0, new ItemStack(Items.DIAMOND, 37));
        var packed = new ItemStack(ModBlocks.CRATE.get());
        packed.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
        crate.applyComponentsFromItemStack(packed);
        check(crate.getTotalItemCount() == 37, "Existing disallowed contents lost on load");
        var saved = crate.saveWithFullMetadata(helper.getLevel().registryAccess());
        var restored = (CrateBlockEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                helper.absolutePos(POS), ModBlocks.CRATE.get().defaultBlockState(), saved, helper.getLevel().registryAccess());
        check(restored != null && restored.getTotalItemCount() == 37, "Existing disallowed contents lost after save/load");
        check(crate.insertItem(new ItemStack(Items.DIAMOND)) == 0, "Existing stack allowed forbidden refill");
        var rejected = net.minecraft.world.level.block.entity.HopperBlockEntity.addItem(
                null, crate, new ItemStack(Items.DIAMOND, 3), net.minecraft.core.Direction.UP);
        check(rejected.getCount() == 3 && crate.getTotalItemCount() == 37, "Hopper merged into old forbidden contents");

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = new CrateMenu(1, player.getInventory(), crate);
        player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 5));
        check(menu.quickMoveStack(player, HOTBAR_START).isEmpty() && crate.getTotalItemCount() == 37,
                "Shift-click merged into an existing forbidden stack");
        menu.setCarried(new ItemStack(Items.DIAMOND, 2));
        menu.clicked(0, 0, ContainerInput.PICKUP, player);
        check(crate.getTotalItemCount() == 37 && menu.getCarried().getCount() == 2, "Normal click bypassed tag");
        menu.setCarried(ItemStack.EMPTY);

        crate.setItem(0, new ItemStack(Items.DIAMOND, 38), true);
        check(crate.getTotalItemCount() == 37, "Transactional setter refilled forbidden items");
        crate.setItem(0, new ItemStack(Items.DIAMOND, 36), true);
        check(crate.getTotalItemCount() == 36, "Automation cannot retain remainder after extraction");
        check(crate.extractSingleItem().is(Items.DIAMOND) && crate.getTotalItemCount() == 35,
                "Existing forbidden item cannot be withdrawn");
        check(!menu.quickMoveStack(player, 0).isEmpty() && crate.isEmpty(), "Existing forbidden items cannot be shift-clicked out");
        check(crate.insertItem(new ItemStack(Items.WHEAT_SEEDS, 4)) == 4, "Empty old crate cannot accept farming items");
    }

    private static CrateBlockEntity crate(GameTestHelper helper) {
        helper.setBlock(POS, ModBlocks.CRATE.get());
        return (CrateBlockEntity) helper.getLevel().getBlockEntity(helper.absolutePos(POS));
    }

    private static void check(boolean value, String message) {
        if (!value) throw new IllegalStateException(message);
    }
}
