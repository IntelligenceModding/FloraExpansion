package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.advancement.FloraActionTrigger;
import de.artemis.floraexpansion.common.block.entity.*;
import de.artemis.floraexpansion.common.datagen.ModAdvancementProvider;
import de.artemis.floraexpansion.common.projectile.PebblesProjectile;
import de.artemis.floraexpansion.common.registry.*;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class AdvancementProgressionTest {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "advancement_progression");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION = ResourceKey.create(Registries.TEST_FUNCTION, ID);
    @SubscribeEvent public static void function(RegisterEvent event) { event.register(Registries.TEST_FUNCTION, ID, () -> AdvancementProgressionTest::run); }
    @SubscribeEvent public static void tests(RegisterGameTestsEvent event) {
        if (Boolean.getBoolean("floraexpansion.customizationOverrides")) return;
        event.registerTest(ID, new FunctionGameTestInstance(FUNCTION, new TestData<>(event.registerEnvironment(ID), Identifier.withDefaultNamespace("empty"), 200, 0, true)));
    }

    private static AdvancementHolder advancement(ServerPlayer player, String name) {
        return java.util.Objects.requireNonNull(player.level().getServer().getAdvancements().get(
                Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "nature/" + name)), "Missing advancement " + name);
    }
    private static boolean done(ServerPlayer player, String name) { return player.getAdvancements().getOrStartProgress(advancement(player, name)).isDone(); }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
    private static void obtain(ServerPlayer player, Item item) {
        var stack = new ItemStack(item);
        player.getInventory().setItem(0, stack);
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), stack);
        player.getInventory().setItem(0, ItemStack.EMPTY);
    }

    private static void run(GameTestHelper helper) {
        var level = helper.getLevel();
        var player = helper.makeMockServerPlayerInLevel();
        var manager = level.getServer().getAdvancements();
        player.getAdvancements().reload(manager);
        for (var milestone : ModAdvancementProvider.MILESTONES) {
            var node = advancement(player, milestone.id()).value();
            check(node.display().isPresent() && !node.criteria().isEmpty(), "Invalid visible milestone " + milestone.id());
            check(node.parent().isEmpty() == milestone.parent().isEmpty(), "Incorrect root/parent for " + milestone.id());
            if (!milestone.parent().isEmpty()) check(node.parent().get().equals(advancement(player, milestone.parent()).id()), "Incorrect parent for " + milestone.id());
        }
        check(manager.getAllAdvancements().stream().filter(a -> a.id().getNamespace().equals(FloraExpansion.MODID)
                && a.id().getPath().startsWith("nature/")).count() == ModAdvancementProvider.MILESTONES.size(), "Missing or stale tree nodes");
        obtain(player, Items.DIRT);
        check(!done(player, "root"), "Vanilla dirt unlocked mod tab");
        obtain(player, ModItems.TWIG.get());
        check(done(player, "root") && done(player, "foraging"), "Gathering did not unlock root and foraging");
        obtain(player, ModItems.BLUEBERRY_JAM.get());
        check(done(player, "jam") && !done(player, "all_jams"), "Jam OR/AND conditions incorrect");
        for (var jam : new Item[]{ModItems.STRAWBERRY_JAM.get(), ModItems.CHERRY_JAM.get(), ModItems.APPLE_JAM.get()}) obtain(player, jam);
        check(done(player, "all_jams"), "Obtaining jams at different times did not accumulate progress");
        obtain(player, ModItems.CURED_MEAT.get());
        check(!done(player, "cured_food"), "Holding food counted as eating it");
        CriteriaTriggers.CONSUME_ITEM.trigger(player, new ItemStack(ModItems.CURED_MEAT.get()));
        check(!done(player, "cured_food"), "Only one cured food completed both-food challenge");
        CriteriaTriggers.CONSUME_ITEM.trigger(player, new ItemStack(ModItems.CURED_FISH.get()));
        check(done(player, "cured_food"), "Both cured foods failed to complete challenge");
        int xp = player.totalExperience;
        CriteriaTriggers.CONSUME_ITEM.trigger(player, new ItemStack(ModItems.CURED_FISH.get()));
        check(player.totalExperience == xp, "Completed challenge paid XP twice");
        for (var juice : new Item[]{ModItems.BLUEBERRY_JUICE.get(), ModItems.STRAWBERRY_JUICE.get(), ModItems.CHERRY_JUICE.get(), ModItems.APPLE_JUICE.get()})
            CriteriaTriggers.CONSUME_ITEM.trigger(player, new ItemStack(juice));
        check(!done(player, "all_juices"), "Four juices completed five-juice challenge");
        CriteriaTriggers.CONSUME_ITEM.trigger(player, new ItemStack(ModItems.CACTUS_JUICE.get()));
        check(done(player, "all_juices"), "Cactus juice did not complete juice challenge");
        obtain(player, ModItems.WOODEN_SALT_WATER_BUCKET.get());
        check(done(player, "brine"), "Wooden brine bucket excluded");
        obtain(player, ModItems.AXOLOTL_WOODEN_BUCKET.get());
        check(done(player, "wooden_passenger"), "Wooden passenger bucket excluded");
        FloraActionTrigger.fire(player, "unknown_action");
        check(!done(player, "salt_food") && !done(player, "basket_harvest"), "Unrelated action awarded advancements");

        var pos = helper.absolutePos(new BlockPos(4, 6, 4));
        level.setBlockAndUpdate(pos, ModBlocks.HAND_MILL.get().defaultBlockState());
        level.setBlockAndUpdate(pos.above(), ModBlocks.HAND_CRANK.get().defaultBlockState());
        var mill = (HandMillBlockEntity)level.getBlockEntity(pos);
        check(!mill.crank(player) && !done(player, "grind_salt"), "Empty mill awarded grinding");
        mill.setItem(0, new ItemStack(ModItems.RAW_SALT.get()));
        int turns = mill.getRequiredTurns();
        for (int i = 1; i < turns; i++) mill.crank(player);
        check(!done(player, "grind_salt"), "Partial grinding awarded completion");
        mill.crank(player);
        check(done(player, "grind_salt"), "Finished salt grinding did not award advancement");

        level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
        var tub = (SaltingTubBlockEntity)level.getBlockEntity(pos);
        tub.addFood(new ItemStack(Items.COD));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setShiftKeyDown(true);
        var hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        level.getBlockState(pos).useWithoutItem(level, player, hit);
        check(!done(player, "salt_food"), "Undoing unsalted food awarded salting");
        player.setShiftKeyDown(false);
        for (int i = 0; i < 4; i++) tub.addFood(new ItemStack(Items.COD));
        check(tub.salt(), "Could not salt test batch");
        for (int i = 0; i < 100; i++) SaltingTubBlockEntity.serverTick(level, pos, level.getBlockState(pos), tub);
        check(tub.isReady() && !done(player, "salt_food"), "Salting award granted before collection");
        level.getBlockState(pos).useWithoutItem(level, player, hit);
        check(done(player, "salt_food"), "Finished batch collection did not award salting");

        level.setBlockAndUpdate(pos, ModBlocks.CRATE.get().defaultBlockState());
        var crate = (CrateBlockEntity)level.getBlockEntity(pos);
        var thread = new ItemStack(ModItems.LINEN_THREAD.get(), 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, thread);
        level.getBlockState(pos).useItemOn(thread, level, player, InteractionHand.MAIN_HAND, hit);
        check(!done(player, "tie_crate"), "Empty crate awarded packing");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        level.getBlockState(pos).useWithoutItem(level, player, hit);
        crate.insertItem(new ItemStack(Items.WHEAT, 4));
        player.setItemInHand(InteractionHand.MAIN_HAND, thread);
        level.getBlockState(pos).useItemOn(thread, level, player, InteractionHand.MAIN_HAND, hit);
        check(done(player, "tie_crate"), "Tying filled crate did not award packing");

        level.setBlockAndUpdate(pos.below(), Blocks.FARMLAND.defaultBlockState());
        level.setBlockAndUpdate(pos, Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7));
        var basket = new ItemStack(ModItems.BASKET.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, basket);
        basket.getItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
        check(done(player, "basket_harvest"), "Successful basket harvest did not award gathering");

        var armor = new Item[]{ModItems.SPIKED_LEATHER_HELMET.get(), ModItems.SPIKED_LEATHER_CHESTPLATE.get(), ModItems.SPIKED_LEATHER_LEGGINGS.get(), ModItems.SPIKED_LEATHER_BOOTS.get()};
        var slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int i = 0; i < 3; i++) player.setItemSlot(slots[i], new ItemStack(armor[i]));
        CriteriaTriggers.LOCATION.trigger(player);
        check(!done(player, "spiked_armor"), "Partial armor set completed armor goal");
        player.setItemSlot(slots[3], new ItemStack(armor[3]));
        CriteriaTriggers.LOCATION.trigger(player);
        check(done(player, "spiked_armor"), "Full worn armor set did not complete goal");

        // Go through the real server interaction path so the vanilla criterion fires too.
        level.setBlockAndUpdate(pos, Blocks.JUKEBOX.defaultBlockState());
        var otherDisc = new ItemStack(Items.MUSIC_DISC_CAT);
        player.setItemInHand(InteractionHand.MAIN_HAND, otherDisc);
        player.setPos(Vec3.atCenterOf(pos).add(1, 0, 0));
        player.gameMode.useItemOn(player, level, otherDisc, InteractionHand.MAIN_HAND, hit);
        check(!done(player, "cave_music"), "Unrelated disc awarded mod music goal");
        var disc = new ItemStack(ModItems.MUSIC_DISC_INTO_THE_CAVES.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, disc);
        player.gameMode.useItemOn(player, level, disc, InteractionHand.MAIN_HAND, hit);
        check(!done(player, "cave_music"), "Ejecting a different record while holding mod disc awarded playback");
        player.gameMode.useItemOn(player, level, disc, InteractionHand.MAIN_HAND, hit);
        check(done(player, "cave_music"), "Real jukebox insertion did not award music goal");

        for (var sapling : new Block[]{ModBlocks.CHERRY_PIT.get(), ModBlocks.APPLE_CORE.get()}) {
            level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState());
            level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
            var stack = new ItemStack(sapling.asItem());
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            player.gameMode.useItemOn(player, level, stack, InteractionHand.MAIN_HAND, hit);
        }
        check(done(player, "orchard"), "Planting both saplings did not award orchard advancement");

        check(!done(player, "salt_caves"), "Unrelated biome awarded salt cave exploration");
        // Biome lookup samples neighboring quart cells with positional jitter.
        var minBiome = pos.offset(-8, -8, -8);
        var maxBiome = pos.offset(8, 8, 8);
        String coordinates = minBiome.getX() + " " + minBiome.getY() + " " + minBiome.getZ()
                + " " + maxBiome.getX() + " " + maxBiome.getY() + " " + maxBiome.getZ();
        level.getServer().getCommands().performPrefixedCommand(level.getServer().createCommandSourceStack(),
                "fillbiome " + coordinates + " floraexpansion:salt_caves");
        player.setPos(Vec3.atCenterOf(pos));
        check(level.getBiome(pos).is(ModBiomes.SALT_CAVES), "Biome test fixture did not place player inside Salt Caves");
        CriteriaTriggers.LOCATION.trigger(player);
        check(done(player, "salt_caves"), "Salt Caves biome did not award exploration");

        var waterPos = helper.absolutePos(new BlockPos(64, 20, 32));
        for (var p : BlockPos.betweenClosed(waterPos.offset(-2,-3,-2), waterPos.offset(82,3,2))) {
            int y = p.getY() - waterPos.getY();
            level.setBlock(p, y == -3 ? Blocks.STONE.defaultBlockState() : y < 0 ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
        }
        var stone = new PebblesProjectile(level, player, new ItemStack(ModItems.PEBBLES.get()));
        stone.setPos(waterPos.getX(), waterPos.getY() + .15, waterPos.getZ() + .5);
        stone.setDeltaMovement(1.2, -.08, 0);
        for (int i = 0; i < 200 && stone.isAlive(); i++) stone.tick();
        check(done(player, "skipping_stones"), "Actual three-skip trajectory did not award challenge");
        // Custom actions remain independent, revocable, and usable after a datapack reload.
        for (String action : new String[]{"brush_salt", "salt_find"}) {
            check(!done(player, action), "Find criterion granted without brushing");
            FloraActionTrigger.fire(player, action);
            check(done(player, action), "Action criterion did not grant " + action);
            player.getAdvancements().revoke(advancement(player, action), "performed");
            check(!done(player, action), "Revocation failed for " + action);
            FloraActionTrigger.fire(player, action);
            check(done(player, action), "Revoked criterion failed to listen again");
        }
        FloraExpansion.LOGGER.info("Advancement progression passed: {} connected nodes, accumulated collections, food challenges, wooden buckets, real mill/tub/crate/basket actions, armor, three water skips, action isolation and revocation", ModAdvancementProvider.MILESTONES.size());
        helper.succeed();
    }
}
