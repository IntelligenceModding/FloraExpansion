package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.SaltingTubBlock;
import de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

final class AuditSaltingTubTests {
    static void verify(CompatibilityTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(2, 3, 2));
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        Item[][] foods = {{Items.COD, ModItems.SALTED_RAW_FISH.get()}, {Items.BEEF, ModItems.SALTED_RAW_MEAT.get()},
                {ModItems.SPRUCE_NUTS.get(), ModItems.SALTED_SPRUCE_NUTS.get()},
                {ModItems.TOASTED_SPRUCE_NUTS.get(), ModItems.SALTED_TOASTED_SPRUCE_NUTS.get()}};
        for (var pair : foods) for (int size : new int[]{4}) {
            level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
            var tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
            var salt = new ItemStack(ModItems.SALT.get(), 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, salt);
            level.getBlockState(pos).useItemOn(salt, level, player, InteractionHand.MAIN_HAND, hit);
            check(salt.getCount() == 2 && !tub.isSalted(), "Empty tub consumed salt");
            var input = new ItemStack(pair[0], 5);
            player.setItemInHand(InteractionHand.MAIN_HAND, input);
            for (int i = 1; i <= size; i++) {
                level.getBlockState(pos).useItemOn(input, level, player, InteractionHand.MAIN_HAND, hit);
                check(tub.layers() == i && input.getCount() == 5-i
                        && level.getBlockState(pos).getValue(SaltingTubBlock.LAYERS) == i, "Food layers/consumption incorrect");
                if (i < 4) check(!tub.salt(), "Salted a batch below the minimum");
            }
            if (size == 4) check(!tub.addFood(input), "Overfilled tub");
            check(!tub.addFood(new ItemStack(Items.DIAMOND)), "Nonfood accepted");
            check(!tub.addFood(new ItemStack(pair[0] == Items.COD ? Items.BEEF : Items.COD)), "Mixed categories accepted");
            check(tub.take(false).isEmpty() && tub.layers() == size, "Unfinished food was collected without undo");
            player.setItemInHand(InteractionHand.MAIN_HAND, salt);
            level.getBlockState(pos).useItemOn(salt, level, player, InteractionHand.MAIN_HAND, hit);
            check(tub.isSalted() && salt.getCount() == 1 && tub.layers() == size, "Salting did not consume exactly one salt");
            level.getBlockState(pos).useItemOn(salt, level, player, InteractionHand.MAIN_HAND, hit);
            check(salt.getCount() == 1 && !tub.addFood(input), "Finished batch accepted additional input");
            check(!tub.isReady() && tub.take(false).isEmpty() && tub.take(true).isEmpty(), "Processing food could be collected early");
            advance(tub, 37);
            var saved = tub.saveWithFullMetadata(level.registryAccess());
            var restored = (SaltingTubBlockEntity) BlockEntity.loadStatic(pos, level.getBlockState(pos), saved, level.registryAccess());
            check(restored != null && restored.layers() == size && restored.isSalted()
                    && restored.displayFood().is(pair[1]), "Saved salted batch lost state or output");
            check(!restored.isReady() && restored.remainingTicks() == 63, "Reload lost processing countdown");
            level.setBlockEntity(restored);
            tub = restored;
            advance(tub, 62);
            check(!tub.isReady() && tub.remainingTicks() == 1 && tub.take(false).isEmpty(), "Batch finished too early");
            advance(tub, 1);
            check(tub.isReady() && level.getBlockState(pos).getValue(SaltingTubBlock.READY), "Batch never became ready");
            int before = player.getInventory().countItem(pair[1]);
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            level.getBlockState(pos).useWithoutItem(level, player, hit);
            check(player.getInventory().countItem(pair[1]) == before + size && tub.layers() == 0 && !tub.isSalted(),
                    "Collection produced wrong output count or did not reset");
            check(tub.take(false).isEmpty(), "Batch could be collected twice");
            level.removeBlock(pos, false);
        }
        level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
        var tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
        check(tub.addFood(new ItemStack(Items.COD)) && tub.addFood(new ItemStack(Items.SALMON)), "Compatible fish rejected");
        check(tub.take(true).getFirst().is(Items.SALMON) && tub.layers() == 1, "Undo did not preserve original fish");
        var saved = tub.saveWithFullMetadata(level.registryAccess());
        var restored = (SaltingTubBlockEntity) BlockEntity.loadStatic(pos, level.getBlockState(pos), saved, level.registryAccess());
        check(restored != null && restored.layers() == 1 && restored.displayFood().is(Items.COD) && !restored.isSalted(),
                "Unfinished saved batch lost its original food");
        level.destroyBlock(pos, true);
        var drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1));
        check(drops.stream().map(ItemEntity::getItem).filter(s -> s.is(Items.COD)).mapToInt(ItemStack::getCount).sum() == 1,
                "Breaking tub lost or duplicated unfinished food");
        check(drops.stream().map(ItemEntity::getItem).filter(s -> s.is(ModBlocks.SALTING_TUB.get().asItem())).mapToInt(ItemStack::getCount).sum() == 1,
                "Tub did not drop itself");
        drops.forEach(ItemEntity::discard);
        level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
        tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
        for (int layer=0; layer<4; layer++) check(tub.addFood(new ItemStack(Items.COD)), "Could not fill final batch");
        check(tub.salt(), "Could not salt final batch");
        level.destroyBlock(pos, true);
        drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1));
        check(drops.stream().map(ItemEntity::getItem).filter(s -> s.is(Items.COD)).mapToInt(ItemStack::getCount).sum() == 4
                        && drops.stream().map(ItemEntity::getItem).filter(s -> s.is(ModItems.SALT.get())).mapToInt(ItemStack::getCount).sum() == 1
                        && drops.stream().noneMatch(e -> e.getItem().is(ModItems.SALTED_RAW_FISH.get())),
                "Breaking processing tub bypassed the wait or lost ingredients");
        drops.forEach(ItemEntity::discard);
        level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
        tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
        for (int layer=0; layer<4; layer++) tub.addFood(new ItemStack(Items.COD));
        check(tub.salt(), "Could not start ready batch");
        advance(tub, 100);
        level.destroyBlock(pos, true);
        drops = level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1));
        check(drops.stream().map(ItemEntity::getItem).filter(s -> s.is(ModItems.SALTED_RAW_FISH.get())).mapToInt(ItemStack::getCount).sum() == 4
                        && drops.stream().noneMatch(e -> e.getItem().is(Items.COD)), "Breaking finished tub lost or duplicated converted food");
        drops.forEach(ItemEntity::discard);
        level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
        tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
        var creative = helper.makeMockPlayer(GameType.CREATIVE);
        GameType.CREATIVE.updatePlayerAbilities(creative.getAbilities());
        var fish = new ItemStack(Items.COD);
        creative.setItemInHand(InteractionHand.MAIN_HAND, fish);
        for (int layer=0; layer<4; layer++) level.getBlockState(pos).useItemOn(fish, level, creative, InteractionHand.MAIN_HAND, hit);
        var salt = new ItemStack(ModItems.SALT.get());
        creative.setItemInHand(InteractionHand.MAIN_HAND, salt);
        level.getBlockState(pos).useItemOn(salt, level, creative, InteractionHand.MAIN_HAND, hit);
        check(fish.getCount() == 1 && salt.getCount() == 1 && tub.isSalted() && tub.layers() == 4,
                "Creative interaction consumed food or salt");
    }
    static void advance(SaltingTubBlockEntity tub, int ticks) {
        for (int tick=0; tick<ticks; tick++) SaltingTubBlockEntity.serverTick(tub.getLevel(), tub.getBlockPos(), tub.getBlockState(), tub);
    }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
    private AuditSaltingTubTests() {}
}
