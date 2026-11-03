package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.AppleLeavesBlock;
import de.artemis.floraexpansion.common.block.SmallBlueberryBushBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.config.ServerConfig;
import de.artemis.floraexpansion.common.item.BasketItem;
import de.artemis.floraexpansion.common.item.WoodenBucketUtil;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import java.util.function.Consumer;

public final class CustomizationTests {
    private static final BlockPos POS = new BlockPos(1, 2, 1);
    private CustomizationTests() {}

    public static void basketSettings(GameTestHelper helper) {
        boolean crops = ServerConfig.basketCrops.get(), gathering = ServerConfig.basketGathering.get(), deposits = ServerConfig.basketDeposits.get();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var basket = new ItemStack(ModItems.BASKET.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, basket);
        var pos = helper.absolutePos(POS);
        var context = new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
        helper.setBlock(POS.below(), Blocks.MYCELIUM);
        try {
            ServerConfig.basketCrops.set(false);
            helper.setBlock(POS, ModBlocks.APPLE_LEAVES.get().defaultBlockState().setValue(AppleLeavesBlock.AGE, 2));
            check(!basket.useOn(context).consumesAction(), "Server-disabled crop harvesting succeeded");
            check(helper.getBlockState(POS).getValue(AppleLeavesBlock.AGE) == 2, "Disabled harvesting changed maturity");
            helper.setBlock(POS, Blocks.BROWN_MUSHROOM);
            check(basket.useOn(context).consumesAction(), "Gathering was tied to crop-harvest switch");
            ServerConfig.basketCrops.set(true);
            ServerConfig.basketGathering.set(false);
            helper.setBlock(POS, Blocks.RED_MUSHROOM);
            check(!basket.useOn(context).consumesAction(), "Server-disabled wild gathering succeeded");
            helper.setBlock(POS, ModBlocks.APPLE_LEAVES.get().defaultBlockState().setValue(AppleLeavesBlock.AGE, 2));
            check(basket.useOn(context).consumesAction(), "Crop harvesting was tied to gathering switch");
            helper.setBlock(POS, ModBlocks.CRATE.get());
            ServerConfig.basketDeposits.set(false);
            check(!BasketItem.tryDepositIntoContainer(helper.getLevel(), pos, player, basket), "Disabled basket deposit succeeded");
            check(BasketItem.getContents(basket).stream().anyMatch(stack -> stack.is(Items.APPLE)), "Disabled action deleted stored items");
        } finally {
            ServerConfig.basketCrops.set(crops); ServerConfig.basketGathering.set(gathering); ServerConfig.basketDeposits.set(deposits);
        }
    }

    public static void growthHooks(GameTestHelper helper) {
        double previous = ServerConfig.berryGrowth.get();
        var level = helper.getLevel();
        var pos = helper.absolutePos(POS);
        helper.setBlock(POS.below(), Blocks.DIRT);
        var initial = ModBlocks.BLUEBERRY_BUSH.get().defaultBlockState();
        helper.setBlock(POS, initial);
        Consumer<CropGrowEvent.Pre> cancel = event -> {
            if (event.getLevel() == level && event.getPos().equals(pos)) event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
        };
        int[] posts = {0};
        Consumer<CropGrowEvent.Post> post = event -> {
            if (event.getLevel() == level && event.getPos().equals(pos)) {
                check(event.getOriginalState().getValue(SmallBlueberryBushBlock.AGE) == 0, "Wrong original growth state");
                check(event.getState().getValue(SmallBlueberryBushBlock.AGE) == 1, "Wrong new growth state");
                posts[0]++;
            }
        };
        try {
            ServerConfig.berryGrowth.set(0.0);
            ModBlocks.BLUEBERRY_BUSH.get().randomTick(initial, level, pos, level.getRandom());
            check(level.getBlockState(pos) == initial, "Zero growth multiplier still grew");
            ServerConfig.berryGrowth.set(16.0);
            NeoForge.EVENT_BUS.addListener(cancel);
            ModBlocks.BLUEBERRY_BUSH.get().randomTick(initial, level, pos, level.getRandom());
            check(level.getBlockState(pos) == initial, "Growth ignored canceled NeoForge event");
            NeoForge.EVENT_BUS.unregister(cancel);
            NeoForge.EVENT_BUS.addListener(post);
            ModBlocks.BLUEBERRY_BUSH.get().randomTick(initial, level, pos, level.getRandom());
            check(level.getBlockState(pos).getValue(SmallBlueberryBushBlock.AGE) == 1 && posts[0] == 1, "Growth multiplier or post event failed");
        } finally {
            NeoForge.EVENT_BUS.unregister(cancel); NeoForge.EVENT_BUS.unregister(post); ServerConfig.berryGrowth.set(previous);
        }
    }

    public static void lavaAndRemainders(GameTestHelper helper) {
        int min = ServerConfig.lavaMinTicks.get(), max = ServerConfig.lavaMaxTicks.get();
        double chance = ServerConfig.cherryPitChance.get();
        try {
            ServerConfig.lavaMinTicks.set(400); ServerConfig.lavaMaxTicks.set(200);
            var lava = new ItemStack(ModItems.WOODEN_LAVA_BUCKET.get());
            WoodenBucketUtil.initializeLavaTimeout(lava, 1000, helper.getLevel().getRandom());
            check(!WoodenBucketUtil.hasExpiredLavaTimeout(lava, 1399) && WoodenBucketUtil.hasExpiredLavaTimeout(lava, 1400), "Invalid lava range was not clamped safely");
            ServerConfig.lavaMinTicks.set(800); ServerConfig.lavaMaxTicks.set(900);
            check(WoodenBucketUtil.hasExpiredLavaTimeout(lava, 1400), "Settings retroactively changed an existing bucket deadline");
            var player = helper.makeMockPlayer(GameType.SURVIVAL);
            ServerConfig.cherryPitChance.set(0.0);
            ModItems.CHERRIES.get().finishUsingItem(new ItemStack(ModItems.CHERRIES.get()), helper.getLevel(), player);
            check(count(player, ModBlocks.CHERRY_PIT.get().asItem()) == 0, "Zero pit chance created a reward");
            ServerConfig.cherryPitChance.set(1.0);
            ModItems.CHERRIES.get().finishUsingItem(new ItemStack(ModItems.CHERRIES.get()), helper.getLevel(), player);
            check(count(player, ModBlocks.CHERRY_PIT.get().asItem()) == 1, "Guaranteed pit chance failed");
        } finally {
            ServerConfig.lavaMinTicks.set(min); ServerConfig.lavaMaxTicks.set(max); ServerConfig.cherryPitChance.set(chance);
        }
    }

    public static void serverPacketAuthority(GameTestHelper helper) {
        boolean enabled = ServerConfig.crateExtraction.get(), bulk = ServerConfig.crateBulk.get();
        var player = helper.makeMockServerPlayerInLevel();
        var pos = helper.absolutePos(POS);
        player.setPos(Vec3.atCenterOf(pos).add(1, 0, 0));
        helper.setBlock(POS, ModBlocks.CRATE.get());
        var crate = (CrateBlockEntity) helper.getLevel().getBlockEntity(pos);
        crate.insertItem(new ItemStack(Items.WHEAT_SEEDS, 12));
        var payload = new de.artemis.floraexpansion.common.network.payload.CrateExtractPayload(pos, true);
        var context = (net.neoforged.neoforge.network.handling.IPayloadContext) java.lang.reflect.Proxy.newProxyInstance(
                CustomizationTests.class.getClassLoader(), new Class[]{net.neoforged.neoforge.network.handling.IPayloadContext.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "player" -> player;
                    case "enqueueWork" -> {
                        if (args[0] instanceof Runnable work) work.run(); else ((java.util.function.Supplier<?>) args[0]).get();
                        yield java.util.concurrent.CompletableFuture.completedFuture(null);
                    }
                    default -> throw new UnsupportedOperationException(method.getName());
                });
        try {
            ServerConfig.crateBulk.set(false);
            crate.recordInsertClick(player, new ItemStack(Items.WHEAT_SEEDS), 100);
            check(!crate.shouldBulkInsert(player, new ItemStack(Items.WHEAT_SEEDS), 100), "Disabled bulk deposit succeeded");
            var handler = de.artemis.floraexpansion.common.network.ModPayloads.class.getDeclaredMethod("handleCrateExtract", payload.getClass(), net.neoforged.neoforge.network.handling.IPayloadContext.class);
            handler.setAccessible(true);
            ServerConfig.crateExtraction.set(false);
            handler.invoke(null, payload, context);
            check(crate.getTotalItemCount() == 12 && count(player, Items.WHEAT_SEEDS) == 0, "Client request bypassed disabled server extraction");
            ServerConfig.crateExtraction.set(true);
            handler.invoke(null, payload, context);
            check(crate.isEmpty() && count(player, Items.WHEAT_SEEDS) == 12, "Enabled extraction did not use server inventory");
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        } finally {
            ServerConfig.crateExtraction.set(enabled); ServerConfig.crateBulk.set(bulk); player.discard();
        }
    }

    public static void strippingRewards(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(POS);
        var state = ModBlocks.CACTUS_LOG.get().defaultBlockState();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var axe = new ItemStack(Items.IRON_AXE);
        axe.enchant(level.registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                .getOrThrow(net.minecraft.world.item.enchantment.Enchantments.FORTUNE), 255);
        var loot = de.artemis.floraexpansion.common.loot.HarvestLoot.drops(level, pos, state, player, axe,
                de.artemis.floraexpansion.common.loot.HarvestLoot.CACTUS_STRIPPING);
        check(loot.stream().filter(s -> s.is(ModBlocks.CACTUS_SPINE.get().asItem())).mapToInt(ItemStack::getCount).sum() == 1,
                "Stripping table lost Fortune bonus");
    }

    public static void cactusGrowthHooks(GameTestHelper helper) {
        double previous = ServerConfig.cactusGrowth.get();
        var level = helper.getLevel();
        var pos = helper.absolutePos(POS);
        helper.setBlock(POS.below(), ModBlocks.CACTUS_LOG.get());
        var initial = ModBlocks.GIANT_CACTUS_STEM.get().defaultBlockState();
        helper.setBlock(POS, initial);
        helper.setBlock(POS.above(), Blocks.AIR);
        Consumer<CropGrowEvent.Pre> cancel = event -> {
            if (event.getLevel() == level && event.getPos().equals(pos)) event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
        };
        int[] posts = {0};
        Consumer<CropGrowEvent.Post> post = event -> { if (event.getLevel() == level && event.getPos().equals(pos)) posts[0]++; };
        try {
            ServerConfig.cactusGrowth.set(0.0);
            initial.randomTick(level, pos, level.getRandom());
            check(level.getBlockState(pos.above()).isAir(), "Zero cactus multiplier allowed stem growth");
            ServerConfig.cactusGrowth.set(1.0);
            NeoForge.EVENT_BUS.addListener(cancel);
            initial.randomTick(level, pos, level.getRandom());
            check(level.getBlockState(pos.above()).isAir(), "Stem growth ignored cancellation");
            NeoForge.EVENT_BUS.unregister(cancel);
            NeoForge.EVENT_BUS.addListener(post);
            initial.randomTick(level, pos, level.getRandom());
            check(!level.getBlockState(pos.above()).isAir() && posts[0] == 1, "Stem growth/post event failed");
        } finally {
            NeoForge.EVENT_BUS.unregister(cancel); NeoForge.EVENT_BUS.unregister(post); ServerConfig.cactusGrowth.set(previous);
        }
    }

    public static void combatSettings(GameTestHelper helper) {
        double coneDamage = ServerConfig.coneDamage.get(), pebbleDamage = ServerConfig.pebbleDamage.get();
        int slow = ServerConfig.coneSlownessTicks.get();
        double chance = ServerConfig.armorChance.get(), damage = ServerConfig.armorDamage.get(), bonus = ServerConfig.armorBonus.get();
        var level = helper.getLevel();
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        class Cone extends de.artemis.floraexpansion.common.projectile.SpruceConeProjectile {
            Cone() { super(level, player, new ItemStack(ModItems.SPRUCE_CONE.get())); }
            void hit(net.minecraft.world.entity.Entity target) { onHitEntity(new net.minecraft.world.phys.EntityHitResult(target)); }
        }
        class Pebble extends de.artemis.floraexpansion.common.projectile.PebblesProjectile {
            Pebble() { super(level, player, new ItemStack(ModItems.PEBBLES.get())); }
            void hit(net.minecraft.world.entity.Entity target) { onHitEntity(new net.minecraft.world.phys.EntityHitResult(target)); }
        }
        try {
            ServerConfig.coneDamage.set(3.0); ServerConfig.pebbleDamage.set(4.0); ServerConfig.coneSlownessTicks.set(0);
            var coneTarget = net.minecraft.world.entity.EntityType.COW.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            var pebbleTarget = net.minecraft.world.entity.EntityType.COW.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            check(coneTarget != null && pebbleTarget != null, "Cannot create combat targets");
            float before = coneTarget.getHealth();
            new Cone().hit(coneTarget);
            check(coneTarget.getHealth() == before - 3 && !coneTarget.hasEffect(net.minecraft.world.effect.MobEffects.SLOWNESS), "Cone ignored damage or disabled slowness settings");
            before = pebbleTarget.getHealth();
            new Pebble().hit(pebbleTarget);
            check(pebbleTarget.getHealth() == before - 4, "Pebble ignored server damage setting");
            ServerConfig.armorDamage.set(2.0); ServerConfig.armorBonus.set(1.0);
            for (boolean enabled : new boolean[]{false, true}) {
                ServerConfig.armorChance.set(enabled ? 1.0 : 0.0);
                var victim = net.minecraft.world.entity.EntityType.COW.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                var attacker = net.minecraft.world.entity.EntityType.COW.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                check(victim != null && attacker != null, "Cannot create armor targets");
                victim.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(ModItems.SPIKED_LEATHER_HELMET.get()));
                victim.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, new ItemStack(ModItems.SPIKED_LEATHER_CHESTPLATE.get()));
                victim.setItemSlot(net.minecraft.world.entity.EquipmentSlot.LEGS, new ItemStack(ModItems.SPIKED_LEATHER_LEGGINGS.get()));
                victim.setItemSlot(net.minecraft.world.entity.EquipmentSlot.FEET, new ItemStack(ModItems.SPIKED_LEATHER_BOOTS.get()));
                before = attacker.getHealth();
                victim.hurtServer(level, victim.damageSources().mobAttack(attacker), 1);
                check(attacker.getHealth() == before - (enabled ? 3 : 0), "Armor retaliation ignored configured chance/damage");
            }
        } finally {
            ServerConfig.coneDamage.set(coneDamage); ServerConfig.pebbleDamage.set(pebbleDamage); ServerConfig.coneSlownessTicks.set(slow);
            ServerConfig.armorChance.set(chance); ServerConfig.armorDamage.set(damage); ServerConfig.armorBonus.set(bonus);
        }
    }

    public static void featureCodecs(GameTestHelper helper) {
        var ops = com.mojang.serialization.JsonOps.INSTANCE;
        var cactusJson = com.google.gson.JsonParser.parseString("{\"min_height\":6,\"max_height\":12}");
        var cactus = de.artemis.floraexpansion.common.worldgen.feature.GiantCactusFeature.Config.CODEC.parse(ops, cactusJson).getOrThrow();
        var saved = de.artemis.floraexpansion.common.worldgen.feature.GiantCactusFeature.Config.CODEC.encodeStart(ops, cactus).getOrThrow().getAsJsonObject();
        check(saved.get("min_height").getAsInt() == 6 && saved.get("max_height").getAsInt() == 12, "Cactus dimensions did not round-trip");
        check(de.artemis.floraexpansion.common.worldgen.feature.GiantCactusFeature.Config.CODEC.parse(ops,
                com.google.gson.JsonParser.parseString("{\"min_height\":12,\"max_height\":6}")).result().isEmpty(), "Inverted cactus dimensions accepted");
        check(de.artemis.floraexpansion.common.worldgen.feature.PebbleClusterFeature.Config.CODEC.parse(ops,
                com.google.gson.JsonParser.parseString("{\"max_radius\":100}")).result().isEmpty(), "Unbounded pebble patch accepted");
        check(de.artemis.floraexpansion.common.worldgen.treedecorator.AppleLeavesDecorator.CODEC.codec().parse(ops,
                com.google.gson.JsonParser.parseString("{\"replacement_divisor\":0}")).result().isEmpty(), "Invalid fruit-leaf divisor accepted");
        check(de.artemis.floraexpansion.common.worldgen.treedecorator.AppleLeavesDecorator.CODEC.codec().parse(ops,
                com.google.gson.JsonParser.parseString("{\"max_replacements\":0}")).result().isPresent(), "Disabling fruit-leaf replacement failed");
    }

    private static int count(net.minecraft.world.entity.player.Player player, net.minecraft.world.item.Item item) {
        int count = 0;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            var stack = player.getInventory().getItem(slot);
            if (stack.is(item)) count += stack.getCount();
        }
        return count;
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
