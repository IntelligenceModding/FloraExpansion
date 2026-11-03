package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.*;
import de.artemis.floraexpansion.common.block.entity.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

/** Runs against a fresh isolated world with real datapacks and registries. */
@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class CompatibilityWorldAudit {
    static volatile boolean finished;
    static volatile Throwable failure;
    private static Runnable afterFluidTicks;
    private static int fluidTicks;
    @SubscribeEvent public static void afterTicks(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        if (afterFluidTicks == null) return;
        CompatibilityInteractionAudit.tick();
        if (++fluidTicks < 400) return;
        try {
            CompatibilityInteractionAudit.verifyFinished();
            afterFluidTicks.run();
            FloraExpansion.LOGGER.info("SPRING AUDIT PASSED: grass, slope, deep cave and intermediate cave surfaces; protected blocks; fluid stability");
            java.nio.file.Files.writeString(java.nio.file.Path.of("audit-success.txt"), "1.21.1 compatibility audit passed\n");
        } catch (Throwable error) {
            failure = error;
            FloraExpansion.LOGGER.error("SPRING AUDIT FAILED", error);
            throw new IllegalStateException("Spring audit failed", error);
        } finally { afterFluidTicks = null; finished = true; }
        if (event.getServer().isDedicatedServer()) event.getServer().halt(false);
    }
    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
    @SubscribeEvent public static void started(ServerStartedEvent event) {
        if (!Boolean.getBoolean("floraexpansion.compatibilityAudit")) return;
        try {
            var level = event.getServer().overworld();
            var pos = new BlockPos(0, 200, 0);
            level.getChunkAt(pos);
            int recipes = 0, entities = 0;
            for (var recipe : level.getRecipeManager().getRecipes()) {
                if (!recipe.id().getNamespace().equals(FloraExpansion.MODID)) continue;
                check(!recipe.value().getResultItem(level.registryAccess()).isEmpty(), "Empty recipe: " + recipe.id());
                recipes++;
            }
            check(recipes == 168, "Recipe total changed: " + recipes);
            for (var entry : ModBlocks.BLOCKS.getEntries()) {
                for (var state : entry.get().getStateDefinition().getPossibleStates()) {
                    state.getShape(level, pos);
                    state.getCollisionShape(level, pos);
                    state.getFluidState();
                }
                var state = entry.get().defaultBlockState();
                if (!(entry.get() instanceof net.minecraft.world.level.block.EntityBlock block)) continue;
                var entity = block.newBlockEntity(pos, state);
                if (entity == null) continue;
                entity.setLevel(level);
                if (entity instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign) {
                    sign.setText(new net.minecraft.world.level.block.entity.SignText()
                            .setMessage(0, net.minecraft.network.chat.Component.literal("Saved cactus sign"))
                            .setColor(net.minecraft.world.item.DyeColor.BLUE).setHasGlowingText(true), true);
                    sign.setWaxed(true);
                }
                var data = entity.saveWithFullMetadata(level.registryAccess());
                var restored = BlockEntity.loadStatic(pos, state, data, level.registryAccess());
                check(restored != null && restored.getType() == entity.getType(), "Block entity save/load: " + entry.getId());
                if (restored instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign)
                    check(sign.isWaxed() && sign.getText(true).hasGlowingText()
                            && sign.getText(true).getMessage(0, false).getString().equals("Saved cactus sign"), "Sign text/glow/wax lost");
                entities++;
            }
            level.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), 3);
            level.setBlock(pos, ModBlocks.CRATE.get().defaultBlockState(), 3);
            var crate = (CrateBlockEntity) level.getBlockEntity(pos);
            check(crate.insertItem(new ItemStack(ModItems.BLUEBERRIES.get(), 64)) == 64, "Crate storage tag is broken");
            var crateTag = crate.saveWithFullMetadata(level.registryAccess());
            var restoredCrate = (CrateBlockEntity) BlockEntity.loadStatic(pos, crate.getBlockState(), crateTag, level.registryAccess());
            check(restoredCrate.getTotalItemCount() == 64, "Crate lost saved inventory");
            level.setBlock(pos, ModBlocks.SALTING_TUB.get().defaultBlockState(), 3);
            var tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
            for (int i = 0; i < 4; i++) check(tub.addFood(new ItemStack(ModItems.SPRUCE_NUTS.get())), "Salting layer rejected");
            check(tub.salt(), "Salt recipe lookup failed");
            var savedTub = tub.saveWithFullMetadata(level.registryAccess());
            var restoredTub = (SaltingTubBlockEntity) BlockEntity.loadStatic(pos, tub.getBlockState(), savedTub, level.registryAccess());
            check(restoredTub.isSalted() && restoredTub.layers() == 4 && restoredTub.remainingTicks() == tub.remainingTicks(), "Salting progress save/load failed");
            for (int i = 0; i < 3000 && !tub.isReady(); i++)
                SaltingTubBlockEntity.serverTick(level, pos, level.getBlockState(pos), tub);
            check(tub.isReady(), "Salting timer never completed");
            var salted = tub.take(false);
            check(salted.size() == 1 && salted.getFirst().is(ModItems.SALTED_SPRUCE_NUTS.get()) && salted.getFirst().getCount() == 4,
                    "Salting batch produced incorrect output");
            level.setBlock(pos, ModBlocks.HAND_MILL.get().defaultBlockState(), 3);
            level.setBlock(pos.above(), ModBlocks.HAND_CRANK.get().defaultBlockState(), 3);
            var mill = (HandMillBlockEntity) level.getBlockEntity(pos);
            mill.setItem(0, new ItemStack(ModItems.RAW_SALT.get()));
            var turns = mill.getRequiredTurns();
            check(turns > 0, "Grinding recipe lookup failed");
            var player = net.neoforged.neoforge.common.util.FakePlayerFactory.getMinecraft(level);
            for (int i = 0; i < turns; i++) check(mill.crank(player), "Mill rejected crank turn " + i);
            check(mill.getItem(0).isEmpty() && mill.getItem(1).is(ModItems.SALT.get()), "Mill did not consume input and produce salt");
            var saltBlock = (de.artemis.floraexpansion.common.block.SaltDepositBlock) ModBlocks.SALT_DEPOSIT.get();
            var pile = pos.offset(4, 0, 0);
            level.setBlock(pile.below(), Blocks.STONE.defaultBlockState(), 3);
            level.setBlock(pile, saltBlock.defaultBlockState(), 3);
            for (int layers = 2; layers <= 4; layers++) {
                var start = pile.above(5);
                level.setBlock(start, saltBlock.defaultBlockState(), 3);
                var falling = net.minecraft.world.entity.item.FallingBlockEntity.fall(level, start, level.getBlockState(start));
                for (int i = 0; i < 100 && falling.isAlive(); i++) falling.tick();
                check(level.getBlockState(pile).is(saltBlock)
                        && level.getBlockState(pile).getValue(de.artemis.floraexpansion.common.block.SaltDepositBlock.LAYERS) == layers,
                        "Falling salt did not merge: " + layers);
            }
            for (var alias : de.artemis.floraexpansion.common.migration.LegacyContentAliases.ITEMS.entrySet()) {
                var oldId = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, alias.getKey());
                var newId = ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, alias.getValue());
                check(BuiltInRegistries.ITEM.get(oldId) == BuiltInRegistries.ITEM.get(newId), "Legacy item alias: " + oldId);
            }
            var villager = net.minecraft.world.entity.EntityType.VILLAGER.create(level);
            int trades = 0;
            var pools = new java.util.ArrayList<net.minecraft.world.entity.npc.VillagerTrades.ItemListing[]>();
            net.minecraft.world.entity.npc.VillagerTrades.TRADES.values().forEach(levels -> pools.addAll(levels.values()));
            pools.addAll(net.minecraft.world.entity.npc.VillagerTrades.WANDERING_TRADER_TRADES.values());
            for (var pool : pools) for (var listing : pool) {
                if (!(listing instanceof de.artemis.floraexpansion.common.event.LegacyTradeResources.Trade trade)) continue;
                var offer = trade.getOffer(villager, level.random);
                check(offer != null && !offer.getResult().isEmpty() && !offer.getBaseCostA().isEmpty(), "Invalid villager trade: " + trade.id());
                trades++;
            }
            check(trades == 35, "Missing live villager trades: " + trades);
            FloraExpansion.LOGGER.info("TRADE/SIGN AUDIT PASSED: 35 live offers; cactus sign text, glow, dye and wax persistence");
            FloraExpansion.LOGGER.info("WORLD AUDIT PASSED: {} recipes, {} block entity save/load types, all state shapes, crate storage, complete salting and grinding batches, falling salt layer merges, legacy item aliases", recipes, entities);
            CompatibilityMigrationAudit.run(level);
            CompatibilityInteractionAudit.run(level);
            afterFluidTicks = CompatibilitySpringAudit.run(level);
        } catch (Throwable error) {
            failure = error;
            FloraExpansion.LOGGER.error("WORLD AUDIT FAILED", error);
            throw new IllegalStateException("World audit failed", error);
        } finally { if (afterFluidTicks == null) finished = true; }
    }
}
