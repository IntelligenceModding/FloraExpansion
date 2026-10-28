package de.artemis.floraexpansion;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.migration.LegacyContentAliases;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Fixtures use the registry inventory from the actual 0.4.1 commit, not the alias table. */
public final class LegacyMigrationTests {
    private static final BlockPos POS = new BlockPos(1, 2, 1);

    private LegacyMigrationTests() {
    }

    public static void actualOldSave(GameTestHelper helper) {
        var level = helper.getLevel();
        CompoundTag fixture;
        try (var stream = Objects.requireNonNull(LegacyMigrationTests.class.getResourceAsStream(
                "/floraexpansion/migration/0.4.1-world-data.nbt"))) {
            fixture = net.minecraft.nbt.NbtIo.readCompressed(stream, net.minecraft.nbt.NbtAccounter.unlimitedHeap());
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(exception);
        }
        check(fixture.getString("mod_version").orElseThrow().equals("0.4.1"), "Wrong fixture version");
        var states = fixture.getListOrEmpty("states");
        check(states.size() == 796, "Incomplete 0.4.1 block-state fixture");
        var chunk = net.minecraft.world.level.chunk.storage.SerializableChunkData.parse(
                level, level.palettedContainerFactory(), fixture.getCompoundOrEmpty("chunk"));
        check(chunk != null, "Legacy chunk must parse");
        var section = chunk.sectionData().stream().filter(data -> data.y() == 6).findFirst().orElseThrow().chunkSection();
        check(section != null, "Legacy block section must load");
        for (int index = 0; index < states.size(); index++) {
            var tag = states.getCompound(index).orElseThrow();
            var expected = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, tag);
            check(!expected.isAir(), "Legacy state became air: " + tag);
            var actual = section.getBlockState(index % 16, index / 256, (index / 16) % 16);
            check(actual == expected, "Legacy chunk palette/state changed: " + tag);
        }

        var ops = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
        var items = fixture.getListOrEmpty("items");
        check(items.size() == 80, "Incomplete 0.4.1 item fixture");
        for (var tag : items) {
            var old = (CompoundTag) tag;
            var stack = ItemStack.CODEC.parse(ops, tag).getOrThrow();
            var oldId = Identifier.parse(old.getString("id").orElseThrow());
            String currentName = LegacyContentAliases.ITEMS.getOrDefault(oldId.getPath(), oldId.getPath());
            check(BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(id(currentName)), "Wrong migrated item: " + oldId);
            check(stack.getCount() == old.getIntOr("count", 1), "Legacy stack count lost: " + oldId);
            check(stack.getHoverName().getString().equals("Saved in 0.4.1"), "Legacy item name lost: " + oldId);
            if (stack.isDamageableItem()) check(stack.getDamageValue() == 7, "Legacy item damage lost: " + oldId);
        }

        var restored = (CrateBlockEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                helper.absolutePos(POS), ModBlocks.CRATE.get().defaultBlockState(),
                fixture.getCompoundOrEmpty("crate"), level.registryAccess());
        check(restored != null && restored.getTotalItemCount() == 37, "Actual legacy placed crate contents lost");
        check(restored.getStoredTypeCopy().is(ModBlocks.APPLE_LEAVES.get().asItem()), "Actual legacy crate item lost");
        var basket = ItemStack.CODEC.parse(ops, fixture.getCompoundOrEmpty("basket")).getOrThrow();
        var contents = BasketItem.getContents(basket);
        check(contents.get(0).is(ModItems.SPRUCE_CONE.get()) && contents.get(0).getCount() == 13, "Actual legacy basket contents lost");
        var packed = contents.get(1);
        check(CrateBlock.isPackedItem(packed), "Actual legacy crate packing lost");
        var unpacked = new CrateBlockEntity(helper.absolutePos(POS), ModBlocks.CRATE.get().defaultBlockState());
        packed.get(DataComponents.BLOCK_ENTITY_DATA).loadInto(unpacked, level.registryAccess());
        unpacked.applyComponentsFromItemStack(packed);
        check(unpacked.getTotalItemCount() == 37
                && unpacked.getStoredTypeCopy().is(ModBlocks.APPLE_LEAVES.get().asItem()), "Actual legacy packed crate contents lost");
        check(unpacked.getStoredTypeCopy().getHoverName().getString().equals("Saved leaves in 0.4.1"), "Actual nested legacy item name lost");
        FloraExpansion.LOGGER.info("Actual 0.4.1 save fixture passed: 796 chunk block states, 80 item stacks, placed and nested packed crates");
    }

    public static void registriesAndBlockStates(GameTestHelper helper) {
        JsonObject old = manifest();
        verifyRegistry(old, "blocks", BuiltInRegistries.BLOCK);
        verifyRegistry(old, "items", BuiltInRegistries.ITEM);
        verifyRegistry(old, "feature_types", BuiltInRegistries.FEATURE_TYPE);
        verifyRegistry(old, "tree_decorator_types", BuiltInRegistries.TREE_DECORATOR_TYPE);
        verifyRegistry(old, "particle_types", BuiltInRegistries.PARTICLE_TYPE);
        verifyRegistry(old, "block_entity_types", BuiltInRegistries.BLOCK_ENTITY_TYPE);
        verifyRegistry(old, "entity_types", BuiltInRegistries.ENTITY_TYPE);

        int states = 0;
        for (var rename : LegacyContentAliases.BLOCKS.entrySet()) {
            var block = BuiltInRegistries.BLOCK.get(id(rename.getValue())).orElseThrow().value();
            check(!BuiltInRegistries.BLOCK.containsKey(id(rename.getKey())), "Alias must not add a duplicate block");
            check(BuiltInRegistries.BLOCK.get(id(rename.getKey())).orElseThrow().value() == block, "Wrong block alias");
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                var encoded = NbtUtils.writeBlockState(state);
                encoded.putString("id", id(rename.getKey()).toString());
                var decoded = NbtUtils.readBlockState(BuiltInRegistries.BLOCK, encoded);
                check(decoded == state, "Legacy block properties changed: " + encoded);
                var saved = NbtUtils.writeBlockState(decoded);
                check(saved.getString("id").orElseThrow().equals(id(rename.getValue()).toString()), "Block must save canonical name");
                states++;
            }
        }
        FloraExpansion.LOGGER.info("Migration checked all 61 legacy blocks, 80 legacy items and {} renamed block states", states);
    }

    public static void itemData(GameTestHelper helper) {
        var ops = RegistryOps.create(NbtOps.INSTANCE, helper.getLevel().registryAccess());
        for (var entry : manifest().getAsJsonArray("items")) {
            String oldName = entry.getAsString();
            String currentName = LegacyContentAliases.ITEMS.getOrDefault(oldName, oldName);
            Item item = BuiltInRegistries.ITEM.get(id(currentName)).orElseThrow().value();
            ItemStack sample = namedStack(item, Math.min(17, item.getDefaultMaxStackSize()));
            if (sample.isDamageableItem()) sample.setDamageValue(3);
            var encoded = (CompoundTag) ItemStack.CODEC.encodeStart(ops, sample).getOrThrow();
            encoded.putString("id", id(oldName).toString());
            var decoded = ItemStack.CODEC.parse(ops, encoded).getOrThrow();
            check(decoded.getCount() == sample.getCount() && ItemStack.isSameItemSameComponents(sample, decoded),
                    "Legacy item count/components changed: " + oldName);
            var saved = (CompoundTag) ItemStack.CODEC.encodeStart(ops, decoded).getOrThrow();
            check(saved.getString("id").orElseThrow().equals(id(currentName).toString()), "Item must save canonical name");
        }

        // Explicit equippable patches keep their resource key; the compatibility asset
        // provides the same appearance without discarding the rest of that component.
        var armor = namedStack(ModItems.SPIKED_LEATHER_HELMET.get(), 1);
        armor.setDamageValue(7);
        var encoded = (CompoundTag) ItemStack.CODEC.encodeStart(ops, armor).getOrThrow();
        encoded.putString("id", id("cactus_helmet").toString());
        var equippable = (CompoundTag) Equippable.CODEC.encodeStart(ops, armor.get(DataComponents.EQUIPPABLE)).getOrThrow();
        equippable.putString("asset_id", id("cactus").toString());
        encoded.getCompoundOrEmpty("components").put("minecraft:equippable", equippable);
        var decoded = ItemStack.CODEC.parse(ops, encoded).getOrThrow();
        check(decoded.is(ModItems.SPIKED_LEATHER_HELMET.get()) && decoded.getDamageValue() == 7, "Legacy armor lost");
        check(decoded.get(DataComponents.EQUIPPABLE).assetId().orElseThrow().identifier().equals(id("cactus")), "Explicit armor asset patch lost");
        try (var asset = LegacyMigrationTests.class.getResourceAsStream("/assets/floraexpansion/equipment/cactus.json")) {
            check(asset != null, "Legacy armor appearance asset missing");
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    public static void nestedContainers(GameTestHelper helper) {
        var level = helper.getLevel();
        var ops = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
        var leaves = namedStack(ModBlocks.APPLE_LEAVES.get().asItem(), 37);

        helper.setBlock(POS.below(), Blocks.STONE);
        helper.setBlock(POS, ModBlocks.CRATE.get());
        var crate = (CrateBlockEntity) level.getBlockEntity(helper.absolutePos(POS));
        check(crate != null && crate.insertItem(leaves.copy()) == 37, "Prepare packed crate fixture");
        var packed = new ItemStack(ModBlocks.CRATE.get());
        crate.saveToItem(packed, level.registryAccess());
        var currentCrate = new CrateBlockEntity(helper.absolutePos(POS), ModBlocks.CRATE.get().defaultBlockState());
        currentCrate.applyComponentsFromItemStack(packed);
        check(currentCrate.getTotalItemCount() == 37, "New packed crates must use the container component");
        // Reproduce the actual 0.4.1 saveToItem format, independently of the new writer.
        packed.remove(DataComponents.CONTAINER);
        var legacyOutput = net.minecraft.world.level.storage.TagValueOutput.createWithContext(
                net.minecraft.util.ProblemReporter.DISCARDING, level.registryAccess());
        crate.saveCustomOnly(legacyOutput);
        net.minecraft.world.item.BlockItem.setBlockEntityData(packed,
                de.artemis.floraexpansion.common.registry.ModBlockEntities.CRATE.get(), legacyOutput);
        CrateBlock.setPackedItem(packed, true);
        helper.setBlock(POS, Blocks.AIR);

        var contents = NonNullList.withSize(BasketItem.SLOT_COUNT, ItemStack.EMPTY);
        contents.set(0, namedStack(ModItems.SPRUCE_CONE.get(), 13));
        contents.set(1, packed);
        contents.set(2, namedStack(ModItems.SPIKED_LEATHER_BOOTS.get(), 1));
        contents.get(2).setDamageValue(4);
        var basket = namedStack(ModItems.BASKET.get(), 1);
        BasketItem.saveContents(basket, contents);

        var oldData = (CompoundTag) ItemStack.CODEC.encodeStart(ops, basket).getOrThrow();
        writeOldItemNames(oldData);
        check(oldData.toString().contains("pine_cone") && oldData.toString().contains("fruiting_oak_leaves"),
                "Fixture must really contain legacy nested IDs");
        var loaded = ItemStack.CODEC.parse(ops, oldData).getOrThrow();
        var loadedContents = BasketItem.getContents(loaded);
        check(loadedContents.get(0).getCount() == 13 && loadedContents.get(0).is(ModItems.SPRUCE_CONE.get()), "Nested basket item lost");
        check(loadedContents.get(2).is(ModItems.SPIKED_LEATHER_BOOTS.get()) && loadedContents.get(2).getDamageValue() == 4, "Nested armor lost");
        check(CrateBlock.isPackedItem(loadedContents.get(1)), "Packed crate state lost");

        // Placement loads legacy block-entity NBT before applying modern components.
        var restored = new CrateBlockEntity(helper.absolutePos(POS), ModBlocks.CRATE.get().defaultBlockState());
        loadedContents.get(1).get(DataComponents.BLOCK_ENTITY_DATA).loadInto(restored, level.registryAccess());
        check(restored.getTotalItemCount() == 37, "Legacy crate NBT must load its renamed contents");
        restored.applyComponentsFromItemStack(loadedContents.get(1));
        check(restored.getTotalItemCount() == 37,
                "Applying absent container component erased legacy contents: " + restored.getTotalItemCount());
        var explicitlyEmpty = loadedContents.get(1).copy();
        explicitlyEmpty.set(DataComponents.CONTAINER, net.minecraft.world.item.component.ItemContainerContents.EMPTY);
        restored.applyComponentsFromItemStack(explicitlyEmpty);
        check(restored.getTotalItemCount() == 0, "Explicit modern empty container must take precedence over legacy data");

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, loadedContents.get(1));
        var support = helper.absolutePos(POS.below());
        var hit = new BlockHitResult(Vec3.atCenterOf(support).add(0, 0.5, 0), Direction.UP, support, false);
        check(player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit)).consumesAction(), "Legacy packed crate placement");
        var placed = (CrateBlockEntity) level.getBlockEntity(helper.absolutePos(POS));
        check(placed != null && placed.getTotalItemCount() == 37,
                "Packed crate count lost: placed=" + (placed == null ? "null" : placed.getTotalItemCount())
                        + ", saved=" + oldData);
        check(ItemStack.isSameItemSameComponents(placed.getStoredTypeCopy(), leaves), "Packed crate legacy contents/components lost");

        // A block entity's Items list uses the same ItemStack codec as player and chest inventories.
        var items = NonNullList.withSize(3, ItemStack.EMPTY);
        items.set(0, namedStack(ModItems.GLASS_JAR.get(), 11));
        items.set(1, loaded.copy());
        var output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, level.registryAccess());
        net.minecraft.world.ContainerHelper.saveAllItems(output, items);
        var inventoryData = output.buildResult();
        writeOldItemNames(inventoryData);
        var read = NonNullList.withSize(3, ItemStack.EMPTY);
        net.minecraft.world.ContainerHelper.loadAllItems(
                net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, level.registryAccess(), inventoryData), read);
        check(read.get(0).is(ModItems.GLASS_JAR.get()) && read.get(0).getCount() == 11, "Legacy inventory item lost");
        check(read.get(1).is(ModItems.BASKET.get()) && BasketItem.getContents(read.get(1)).get(0).getCount() == 13, "Inventory-nested basket lost");
    }

    private static JsonObject manifest() {
        var stream = Objects.requireNonNull(LegacyMigrationTests.class.getResourceAsStream("/floraexpansion/migration/0.4.1-registries.json"));
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (java.io.IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static void verifyRegistry(JsonObject manifest, String kind, Registry<?> registry) {
        for (var entry : manifest.getAsJsonArray(kind)) {
            check(registry.get(id(entry.getAsString())).isPresent(), "Missing legacy " + kind + ": " + entry.getAsString());
        }
    }

    private static ItemStack namedStack(Item item, int count) {
        var stack = new ItemStack(item, count);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Migration keeps this name"));
        var data = new CompoundTag();
        data.putInt("migration_marker", 42);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        return stack;
    }

    private static void writeOldItemNames(Tag tag) {
        Map<String, String> reverse = LegacyContentAliases.ITEMS.entrySet().stream()
                .collect(Collectors.toMap(entry -> id(entry.getValue()).toString(), entry -> id(entry.getKey()).toString()));
        writeOldItemNames(tag, reverse);
    }

    private static void writeOldItemNames(Tag tag, Map<String, String> reverse) {
        if (tag instanceof CompoundTag compound) {
            compound.getString("id").map(reverse::get).ifPresent(old -> compound.putString("id", old));
            for (String key : compound.keySet()) writeOldItemNames(compound.get(key), reverse);
        } else if (tag instanceof ListTag list) {
            for (Tag element : list) writeOldItemNames(element, reverse);
        }
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
