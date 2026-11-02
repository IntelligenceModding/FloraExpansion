package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.event.SaltDepositBrushing;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class SaltDepositBrushingTest {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salt_deposit_brushing");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION = ResourceKey.create(Registries.TEST_FUNCTION, ID);
    private static BlockPos protectedPos;
    private static int denied;
    @SubscribeEvent public static void function(RegisterEvent event) { event.register(Registries.TEST_FUNCTION, ID, () -> SaltDepositBrushingTest::run); }
    @SubscribeEvent public static void tests(RegisterGameTestsEvent event) {
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;
        var rules = net.minecraft.world.level.gamerules.GameRuleMap.of();
        rules.set(net.minecraft.world.level.gamerules.GameRules.RANDOM_TICK_SPEED, 0);
        var environment = event.registerEnvironment(ID, new TestEnvironmentDefinition.SetGameRules(rules));
        event.registerTest(ID, new FunctionGameTestInstance(FUNCTION, new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 220, 0, true)));
    }
    @SubscribeEvent public static void protect(BreakBlockEvent event) {
        if (event.getPos().equals(protectedPos)) { denied++; event.setCanceled(true); }
    }

    private static void run(GameTestHelper helper) {
        var level = helper.getLevel();
        var fixtures = new ArrayList<Fixture>();
        for (int i = 0; i < 7; i++) {
            var pos = helper.absolutePos(new BlockPos(32 + i * 6, 6, 32));
            level.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, true);
            for (var p : BlockPos.betweenClosed(pos.offset(-2, -1, -2), pos.offset(2, 4, 2)))
                level.setBlock(p, p.getY() < pos.getY() ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
            boolean wet = i == 2 || i == 3;
            if (wet) level.setBlock(pos.above(3), Blocks.STONE.defaultBlockState(), 2);
            if (wet) for (var direction : Direction.Plane.HORIZONTAL) level.setBlock(pos.relative(direction), Blocks.GLASS.defaultBlockState(), 2);
            var state = ModBlocks.SALT_DEPOSIT.get().defaultBlockState().setValue(SaltDepositBlock.LAYERS, i < 2 ? 4 : 1)
                    .setValue(SaltDepositBlock.NATURAL, i == 0).setValue(SaltDepositBlock.WATERLOGGED, wet).setValue(SaltDepositBlock.BRINE, i == 3);
            level.setBlock(pos, state, 3);
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            var brush = new ItemStack(Items.BRUSH);
            var hand = i == 4 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            player.setItemInHand(hand, brush);
            var fixture = new Fixture(pos, player, brush, hand);
            aim(fixture);
            if (i == 1) verifyReplacementCycles(helper, fixture);
            fixtures.add(fixture);
        }
        protectedPos = fixtures.get(5).pos; denied = 0;
        verifyLoot(helper, fixtures.getFirst());
        helper.runAtTickTime(20, () -> {
            for (var f : fixtures) {
                f.brush.getItem().useOn(new UseOnContext(f.player, f.hand, new BlockHitResult(Vec3.atCenterOf(f.pos), Direction.UP, f.pos, false)));
                check(f.player.isUsingItem(), "Vanilla brush did not start on salt");
            }
        });
        for (int tick = 1; tick <= 165; tick++) {
            final int current = tick;
            helper.runAtTickTime(tick + 20, () -> {
                for (var fixture : fixtures) {
                    aim(fixture);
                    fixture.player.tick();
                }
                if (current == 15) fixtures.get(6).player.releaseUsingItem();
                if (current == 20) {
                    var f = fixtures.get(6);
                    f.brush.getItem().useOn(new UseOnContext(f.player, f.hand, new BlockHitResult(Vec3.atCenterOf(f.pos), Direction.UP, f.pos, false)));
                }
                if (current == 34) check(level.getBlockState(fixtures.getFirst().pos).getValue(SaltDepositBlock.LAYERS) == 4, "Brush removed salt before four strokes");
                if (current == 36) check(level.getBlockState(fixtures.getFirst().pos).getValue(SaltDepositBlock.LAYERS) == 3, "Four brush strokes did not clear exactly one layer");
                if (current == 45) check(level.getBlockState(fixtures.get(6).pos).is(ModBlocks.SALT_DEPOSIT.get()), "Stopping the brush retained partial progress");
                if (current == 165) {
                    for (int i = 0; i < fixtures.size(); i++) {
                        var f = fixtures.get(i);
                        if (i == 5) {
                            check(denied > 0 && level.getBlockState(f.pos).is(ModBlocks.SALT_DEPOSIT.get()) && f.brush.getDamageValue() == 0, "Brush bypassed protection");
                            continue;
                        }
                        check(!level.getBlockState(f.pos).is(ModBlocks.SALT_DEPOSIT.get()), "Brush left salt behind in fixture " + i);
                        int expected = i < 2 ? 4 : 1;
                        check(f.brush.getDamageValue() == expected, "Wrong brush durability in fixture " + i + ": " + f.brush.getDamageValue());
                        check(count(f, ModItems.RAW_SALT.get(), helper) == expected, "Lost or duplicated raw salt in fixture " + i
                                + ": count=" + count(f, ModItems.RAW_SALT.get(), helper) + "; nearby="
                                + level.getEntitiesOfClass(ItemEntity.class, new AABB(f.pos).inflate(8)).stream().map(e -> e.getItem() + "@" + e.position().subtract(Vec3.atCenterOf(f.pos))).toList());
                        if (i != 0) {
                            int items = level.getEntitiesOfClass(ItemEntity.class, new AABB(f.pos).inflate(2)).stream().mapToInt(e -> e.getItem().getCount()).sum();
                            for (int slot = 0; slot < f.player.getInventory().getContainerSize(); slot++) {
                                var stack = f.player.getInventory().getItem(slot);
                                if (!stack.is(Items.BRUSH)) items += stack.getCount();
                            }
                            check(items == expected, "Manufactured salt yielded buried treasure");
                        }
                    }
                    check(level.getFluidState(fixtures.get(2).pos).isSource() && level.getFluidState(fixtures.get(2).pos).is(net.minecraft.tags.FluidTags.WATER), "Brushing destroyed fresh water");
                    check(level.getFluidState(fixtures.get(3).pos).isSource() && level.getFluidState(fixtures.get(3).pos).is(ModFluids.SALT_WATER.get()), "Brushing destroyed brine");
                    protectedPos = null;
                    for (var f : fixtures) level.setChunkForced(f.pos.getX() >> 4, f.pos.getZ() >> 4, false);
                    FloraExpansion.LOGGER.info("Salt brushing passed: actual vanilla held brush ticks, 4 strokes/layer, all layers, offhand, interruption, durability, fluids, protection and eight weighted finds including Into the Caves");
                    helper.succeed();
                }
            });
        }
    }

    private static void verifyLoot(GameTestHelper helper, Fixture fixture) {
        var level = helper.getLevel();
        var table = level.getServer().reloadableRegistries().getLootTable(SaltDepositBrushing.FINDS);
        var params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(fixture.pos))
                .withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(fixture.pos))
                .withParameter(LootContextParams.TOOL, fixture.brush).withOptionalParameter(LootContextParams.THIS_ENTITY, fixture.player)
                .create(LootContextParamSets.BLOCK);
        var expected = Set.of(ModItems.PEBBLES.get(), Items.FLINT, Items.BONE, Items.IRON_NUGGET, Items.AMETHYST_SHARD, Items.ANGLER_POTTERY_SHERD, Items.SHELTER_POTTERY_SHERD, ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
        var seen = new HashSet<Item>(); var random = RandomSource.create(783412);
        int nonempty = 0;
        for (int sample = 0; sample < 2000; sample++) {
            var finds = table.getRandomItems(params, random);
            check(finds.size() <= 1, "One salt layer yields multiple finds");
            if (!finds.isEmpty()) nonempty++;
            for (var find : finds) { check(expected.contains(find.getItem()), "Unexpected salt find"); seen.add(find.getItem()); }
        }
        check(seen.equals(expected) && nonempty > 450 && nonempty < 750, "Salt find weights or loot table did not load: " + nonempty);
        var manufactured = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(fixture.pos))
                .withParameter(LootContextParams.BLOCK_STATE, level.getBlockState(fixture.pos).setValue(SaltDepositBlock.NATURAL, false))
                .withParameter(LootContextParams.TOOL, fixture.brush).withOptionalParameter(LootContextParams.THIS_ENTITY, fixture.player)
                .create(LootContextParamSets.BLOCK);
        for (int sample = 0; sample < 2000; sample++)
            check(table.getRandomItems(manufactured, random).isEmpty(), "Loot table permits finds from manufactured salt");
        check(!ModBlocks.SALT_DEPOSIT.get().defaultBlockState().getValue(SaltDepositBlock.NATURAL), "Unmarked salt defaults to buried finds");
    }

    private static void verifyReplacementCycles(GameTestHelper helper, Fixture fixture) {
        var level = helper.getLevel();
        // Begin with a real natural pile, harvest its ordinary block drops, and repeatedly place those items again.
        level.setBlock(fixture.pos, level.getBlockState(fixture.pos).setValue(SaltDepositBlock.NATURAL, true), 3);
        for (int cycle = 0; cycle < 32; cycle++) {
            var state = level.getBlockState(fixture.pos);
            var drops = net.minecraft.world.level.block.Block.getDrops(state, level, fixture.pos, null);
            check(drops.size() == 1 && drops.getFirst().is(ModItems.RAW_SALT.get()) && drops.getFirst().getCount() == 4,
                    "Mining salt retained treasure data or lost layers");
            level.setBlock(fixture.pos, Blocks.AIR.defaultBlockState(), 3);
            var salt = drops.getFirst(); fixture.player.setItemInHand(InteractionHand.MAIN_HAND, salt);
            for (int layer = 1; layer <= 4; layer++) {
                var clicked = layer == 1 ? fixture.pos.below() : fixture.pos;
                salt.getItem().useOn(new UseOnContext(fixture.player, InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(clicked), Direction.UP, clicked, false)));
                var placed = level.getBlockState(fixture.pos);
                check(placed.is(ModBlocks.SALT_DEPOSIT.get()) && placed.getValue(SaltDepositBlock.LAYERS) == layer
                        && !placed.getValue(SaltDepositBlock.NATURAL), "Replace/stack cycle manufactured finds: " + cycle + "/" + layer);
            }
            check(salt.isEmpty(), "Replacing salt did not consume its items");
        }
        level.setBlock(fixture.pos, ModBlocks.SALT_DEPOSIT.get().defaultBlockState()
                .setValue(SaltDepositBlock.LAYERS, 2).setValue(SaltDepositBlock.NATURAL, true), 3);
        var addedSalt = new ItemStack(ModItems.RAW_SALT.get(), 2);
        fixture.player.setItemInHand(InteractionHand.MAIN_HAND, addedSalt);
        for (int layer = 3; layer <= 4; layer++) {
            addedSalt.getItem().useOn(new UseOnContext(fixture.player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(fixture.pos), Direction.UP, fixture.pos, false)));
            check(level.getBlockState(fixture.pos).getValue(SaltDepositBlock.LAYERS) == layer
                    && !level.getBlockState(fixture.pos).getValue(SaltDepositBlock.NATURAL), "Topping up a natural pile preserved buried finds");
        }
        fixture.player.setItemInHand(fixture.hand, fixture.brush);
        FloraExpansion.LOGGER.info("Salt anti-farming passed: 32 harvest/replacement cycles, 130 real item placements, topping up natural piles; every placed layer has natural=false");
    }
    private static int count(Fixture fixture, Item item, GameTestHelper helper) {
        int count = helper.getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(fixture.pos).inflate(2)).stream()
                .filter(e -> e.getItem().is(item)).mapToInt(e -> e.getItem().getCount()).sum();
        for (int slot = 0; slot < fixture.player.getInventory().getContainerSize(); slot++) {
            var stack = fixture.player.getInventory().getItem(slot); if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }
    private static void aim(Fixture fixture) {
        fixture.player.setNoGravity(true);
        fixture.player.setPos(fixture.pos.getX() + .5, fixture.pos.getY() + 1, fixture.pos.getZ() + .5);
        fixture.player.setXRot(90); fixture.player.xRotO = 90;
        fixture.player.setYRot(0); fixture.player.yRotO = 0;
    }
    private record Fixture(BlockPos pos, Player player, ItemStack brush, InteractionHand hand) {}
    private static void check(boolean pass, String message) { if (!pass) throw new IllegalStateException(message); }
}
