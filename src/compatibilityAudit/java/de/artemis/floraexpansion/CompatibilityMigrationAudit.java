package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Reads data produced by the unmodified production code at 7681d06 (0.4.1r). */
final class CompatibilityMigrationAudit {
    private static void check(boolean value, String message) {
        if (!value) throw new IllegalStateException(message);
    }
    static void run(ServerLevel level) throws java.io.IOException {
        CompoundTag fixture;
        try (var input = CompatibilityMigrationAudit.class.getResourceAsStream("/floraexpansion/0.4.1-1211-world-data.nbt")) {
            check(input != null, "Missing independent 0.4.1 fixture");
            fixture = NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
        }
        check(fixture.getString("source_commit").equals("7681d06"), "Incorrect fixture source");
        var states = fixture.getList("states", Tag.TAG_COMPOUND);
        var origin = new BlockPos(1024, 96, 1024);
        var chunk = net.minecraft.world.level.chunk.storage.ChunkSerializer.read(level, level.getPoiManager(),
                new net.minecraft.world.level.chunk.storage.RegionStorageInfo("legacy-audit", level.dimension(), "chunk"),
                new net.minecraft.world.level.ChunkPos(origin), fixture.getCompound("chunk"));
        for (int i = 0; i < states.size(); i++) {
            var data = states.getCompound(i);
            var state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), data);
            check(!state.isAir(), "Old block disappeared: " + data);
            check(chunk.getBlockState(origin.offset(i % 16, i / 256, (i / 16) % 16)).equals(state),
                    "Old chunk palette changed block state: " + data);
            var properties = NbtUtils.writeBlockState(state).getCompound("Properties");
            var oldProperties = data.getCompound("Properties");
            for (var key : oldProperties.getAllKeys())
                if (properties.contains(key)) check(properties.getString(key).equals(oldProperties.getString(key)), "Old block property changed: " + data);
        }
        var ops = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
        var items = fixture.getList("items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            var data = items.getCompound(i);
            var stack = ItemStack.CODEC.parse(ops, data).getOrThrow();
            check(!stack.isEmpty() && stack.getHoverName().getString().equals("Saved in 0.4.1"), "Old item/name disappeared: " + data);
            check(stack.getCount() == data.getInt("count"), "Old item count changed: " + data);
            if (stack.isDamageableItem()) check(stack.getDamageValue() == 7, "Old durability changed");
        }
        var crateState = ModBlocks.CRATE.get().defaultBlockState();
        var crate = (CrateBlockEntity) BlockEntity.loadStatic(BlockPos.ZERO, crateState, fixture.getCompound("crate"), level.registryAccess());
        check(crate != null && crate.getTotalItemCount() == 37 && crate.getStoredTypeCopy().is(ModBlocks.APPLE_LEAVES.get().asItem()), "Placed old crate lost renamed contents");
        var basket = ItemStack.CODEC.parse(ops, fixture.get("basket")).getOrThrow();
        var contents = BasketItem.getContents(basket);
        check(contents.get(0).getCount() == 13 && contents.get(0).is(de.artemis.floraexpansion.common.registry.ModItems.SPRUCE_CONE.get()), "Nested old item rename failed");
        var packed = contents.get(1);
        check(de.artemis.floraexpansion.common.block.CrateBlock.isPackedItem(packed), "Old packed crate lost packed flag");
        var packedCrate = new CrateBlockEntity(BlockPos.ZERO, crateState);
        packedCrate.applyComponentsFromItemStack(packed);
        check(packedCrate.getTotalItemCount() == 37 && packedCrate.getStoredTypeCopy().is(ModBlocks.APPLE_LEAVES.get().asItem()), "Nested old packed crate lost inventory");
        FloraExpansion.LOGGER.info("MIGRATION AUDIT PASSED: {} original block states and chunk palette, {} original items; durability, names, counts, placed crate and nested packed crate", states.size(), items.size());
    }
}
