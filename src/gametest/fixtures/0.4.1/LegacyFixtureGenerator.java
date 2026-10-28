package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.item.BasketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;

import java.nio.file.Path;

/** Run only against unmodified 0.4.1 production sources; never part of the mod. */
@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class LegacyFixtureGenerator {
    private static final Identifier TEST = id("generate_upgrade_fixture");

    @SubscribeEvent
    public static void functions(net.neoforged.neoforge.registries.RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, registry -> registry.register(TEST, LegacyFixtureGenerator::generate));
    }

    @SubscribeEvent
    public static void tests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(TEST);
        event.registerTest(TEST, new FunctionGameTestInstance(
                net.minecraft.resources.ResourceKey.create(Registries.TEST_FUNCTION, TEST),
                new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 1, true)));
    }

    private static void generate(GameTestHelper helper) {
        try {
            var level = helper.getLevel();
            var ops = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
            var fixture = new CompoundTag();
            fixture.putString("source_commit", "5db57b6");
            fixture.putString("mod_version", "0.4.1");
            var states = new ListTag();
            var origin = new BlockPos(1024, 96, 1024);
            int index = 0;
            for (var holder : BuiltInRegistries.BLOCK.listElements().toList()) {
                if (!holder.key().identifier().getNamespace().equals(FloraExpansion.MODID)) continue;
                for (var state : holder.value().getStateDefinition().getPossibleStates()) {
                    states.add(NbtUtils.writeBlockState(state));
                    var pos = origin.offset(index % 16, index / 256, (index / 16) % 16);
                    level.getChunkAt(pos).setBlockState(pos, state, 0);
                    index++;
                }
            }
            fixture.put("states", states);
            fixture.put("chunk", SerializableChunkData.copyOf(level, level.getChunkAt(origin)).write());
            var items = new ListTag();
            for (var holder : BuiltInRegistries.ITEM.listElements().toList()) {
                if (!holder.key().identifier().getNamespace().equals(FloraExpansion.MODID)) continue;
                var stack = new ItemStack(holder.value(), Math.min(17, holder.value().getDefaultMaxStackSize()));
                stack.set(DataComponents.CUSTOM_NAME, Component.literal("Saved in 0.4.1"));
                if (stack.isDamageableItem()) stack.setDamageValue(7);
                items.add(ItemStack.CODEC.encodeStart(ops, stack).getOrThrow());
            }
            fixture.put("items", items);
            var crateBlock = BuiltInRegistries.BLOCK.get(id("crate")).orElseThrow().value();
            var crate = new CrateBlockEntity(origin, crateBlock.defaultBlockState());
            var leaves = new ItemStack(BuiltInRegistries.ITEM.get(id("fruiting_oak_leaves")).orElseThrow().value(), 37);
            leaves.set(DataComponents.CUSTOM_NAME, Component.literal("Saved leaves in 0.4.1"));
            crate.insertItem(leaves);
            fixture.put("crate", crate.saveWithFullMetadata(level.registryAccess()));
            var packed = new ItemStack(crateBlock);
            crate.saveToItem(packed, level.registryAccess());
            CrateBlock.setPackedItem(packed, true);
            var contents = NonNullList.withSize(BasketItem.SLOT_COUNT, ItemStack.EMPTY);
            contents.set(0, new ItemStack(BuiltInRegistries.ITEM.get(id("pine_cone")).orElseThrow().value(), 13));
            contents.set(1, packed);
            var basket = new ItemStack(BuiltInRegistries.ITEM.get(id("basket")).orElseThrow().value());
            BasketItem.saveContents(basket, contents);
            fixture.put("basket", ItemStack.CODEC.encodeStart(ops, basket).getOrThrow());
            NbtIo.writeCompressed(fixture, Path.of("../0.4.1-world-data.nbt"));
            FloraExpansion.LOGGER.info("Generated actual 0.4.1 save fixture: {} block states, {} items", states.size(), items.size());
            helper.succeed();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(FloraExpansion.MODID, path);
    }
}
