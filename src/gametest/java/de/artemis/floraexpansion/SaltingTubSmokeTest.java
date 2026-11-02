package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class SaltingTubSmokeTest {
    private static boolean creating, prepared, captured, worldCaptured, readyCaptured;
    private static int jeiOpenedTick, grindingOpenedTick;
    private static boolean saltingJeiCaptured;
    private static int ticks, preparedTick;
    private static BlockPos origin;
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        if (!Boolean.getBoolean("floraexpansion.saltingTubSmokeTest") || captured) return;
        var mc = Minecraft.getInstance();
        if (!creating && ClientSmokeStartup.atTitle(mc)) {
            creating = true;
            mc.createWorldOpenFlows().createFreshLevel("salting-tub-smoke-" + System.currentTimeMillis(),
                    new net.minecraft.world.level.LevelSettings("Salting Tub preview", net.minecraft.world.level.GameType.CREATIVE,
                            net.minecraft.world.level.LevelSettings.DifficultySettings.DEFAULT, true,
                            net.minecraft.world.level.WorldDataConfiguration.DEFAULT),
                    new net.minecraft.world.level.levelgen.WorldOptions(12345L, true, false),
                    net.minecraft.world.level.levelgen.presets.WorldPresets::createNormalWorldDimensions, mc.gui.screen());
        }
        if (mc.level == null || mc.player == null || ++ticks < 80) return;
        if (!prepared) {
            prepared = true;
            preparedTick = ticks;
            origin = new BlockPos(mc.player.getBlockX(), 250, mc.player.getBlockZ());
            var id = mc.player.getUUID();
            mc.getSingleplayerServer().execute(() -> {
                var level = mc.getSingleplayerServer().overworld();
                for (var pos : BlockPos.betweenClosed(origin.offset(-2,-1,-3), origin.offset(13,-1,8)))
                    level.setBlockAndUpdate(pos, Blocks.SMOOTH_STONE.defaultBlockState());
                for (int i = 0; i < 6; i++) {
                    var pos = origin.offset(i*2, 0, 0);
                    level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
                    var tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
                    int layers = i < 4 ? i+1 : 4;
                    for (int layer = 0; layer < layers; layer++) tub.addFood(new ItemStack(Items.COD));
                    if (i >= 4) tub.salt();
                    if (i == 5) SaltingTubTests.advance(tub, 100);
                }
                var player = mc.getSingleplayerServer().getPlayerList().getPlayer(id);
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.teleportTo(origin.getX()+5.5, 254, origin.getZ()+8.5);
                player.setYRot(180); player.setXRot(28);
            });
            return;
        }
        mc.player.setYRot(180); mc.player.setXRot(28);
        mc.player.getAbilities().flying = true;
        if (ticks-preparedTick < 60) return;
        for (int i=0; i<6; i++) {
            var tub = mc.level.getBlockEntity(origin.offset(i*2,0,0));
            if (!(tub instanceof SaltingTubBlockEntity entity) || entity.layers() != (i<4 ? i+1 : 4)
                    || entity.isSalted() != (i>=4)) throw new IllegalStateException("Salting tub client sync failed: " + i);
        }
        var models = mc.getModelManager().getBlockStateModelSet();
        for (var state : ModBlocks.SALTING_TUB.get().getStateDefinition().getPossibleStates())
            if (models.get(state) == models.missingModel()) throw new IllegalStateException("Missing salting tub model");
        if (!worldCaptured) {
            if (((SaltingTubBlockEntity) mc.level.getBlockEntity(origin.offset(8,0,0))).isReady())
                throw new IllegalStateException("Processing tub became ready before its delay");
            if (!((SaltingTubBlockEntity) mc.level.getBlockEntity(origin.offset(10,0,0))).isReady())
                throw new IllegalStateException("Ready tub state was not synchronized");
            worldCaptured = true;
            net.minecraft.client.Screenshot.grab(mc.gameDirectory, "salting-tub.png", mc.gameRenderer.mainRenderTarget(), 1,
                    message -> FloraExpansion.LOGGER.info("Salting tub client smoke test passed: {}", message.getString()));
            return;
        }
        if (!readyCaptured) {
            if (!((SaltingTubBlockEntity) mc.level.getBlockEntity(origin.offset(8,0,0))).isReady()) {
                if (ticks - preparedTick > 200) throw new IllegalStateException("Automatic salting ticker did not finish");
                return;
            }
            readyCaptured = true;
            net.minecraft.client.Screenshot.grab(mc.gameDirectory, "salting-tub-ready.png", mc.gameRenderer.mainRenderTarget(), 1,
                    message -> FloraExpansion.LOGGER.info("Salting tub automatic completion passed: {}", message.getString()));
            return;
        }
        if (jeiOpenedTick == 0) {
            var runtime = SaltingJeiSmokePlugin.runtime;
            if (runtime == null) throw new IllegalStateException("JEI runtime unavailable");
            var recipes = runtime.getRecipeManager().createRecipeLookup(de.artemis.floraexpansion.compat.jei.SaltingRecipeCategory.TYPE).get().toList();
            var synchronizedRecipes = de.artemis.floraexpansion.client.recipe.SaltingClientRecipes.get();
            var grinding = runtime.getRecipeManager().createRecipeLookup(de.artemis.floraexpansion.compat.jei.GrindingRecipeCategory.TYPE).get().toList();
            var synchronizedGrinding = de.artemis.floraexpansion.client.recipe.GrindingClientRecipes.get();
            if (grinding.stream().filter(SaltingTubSmokeTest::releaseRecipe).count() != 60 || synchronizedGrinding.stream().filter(SaltingTubSmokeTest::releaseRecipe).count() != 60
                    || !grinding.stream().map(r -> r.id()).collect(java.util.stream.Collectors.toSet()).equals(synchronizedGrinding.stream().map(r -> r.id()).collect(java.util.stream.Collectors.toSet())))
                throw new IllegalStateException("Missing synchronized grinding recipes in JEI");
            if (recipes.stream().filter(SaltingTubSmokeTest::releaseRecipe).count() != 5 || synchronizedRecipes.stream().filter(SaltingTubSmokeTest::releaseRecipe).count() != 5
                    || !recipes.stream().map(r -> r.id()).collect(java.util.stream.Collectors.toSet()).equals(synchronizedRecipes.stream().map(r -> r.id()).collect(java.util.stream.Collectors.toSet())))
                throw new IllegalStateException("Missing synchronized salting recipes in JEI: " + recipes.size() + "/" + synchronizedRecipes.size());
            if (!de.artemis.floraexpansion.common.registry.ModRecipes.isSaltingFood(mc.level, new ItemStack(Items.COD))
                    || !de.artemis.floraexpansion.common.registry.ModRecipes.isSaltingSalt(mc.level,
                            new ItemStack(de.artemis.floraexpansion.common.registry.ModItems.SALT.get())))
                throw new IllegalStateException("Salting ingredient property sets missing on client");
            runtime.getRecipesGui().showTypes(java.util.List.of(de.artemis.floraexpansion.compat.jei.SaltingRecipeCategory.TYPE));
            jeiOpenedTick = ticks;
            return;
        }
        if (ticks - jeiOpenedTick < 12) return;
        if (mc.gui.screen() == null) throw new IllegalStateException("JEI salting recipe screen did not open");
        if (!saltingJeiCaptured) {
            saltingJeiCaptured = true;
            net.minecraft.client.Screenshot.grab(mc.gameDirectory, "salting-recipes-jei.png", mc.gameRenderer.mainRenderTarget(), 1,
                    message -> FloraExpansion.LOGGER.info("Salting JEI smoke test passed: {}", message.getString()));
            SaltingJeiSmokePlugin.runtime.getRecipesGui().showTypes(java.util.List.of(de.artemis.floraexpansion.compat.jei.GrindingRecipeCategory.TYPE));
            grindingOpenedTick = ticks;
            return;
        }
        if (ticks - grindingOpenedTick < 12) return;
        if (mc.gui.screen() == null) throw new IllegalStateException("JEI grinding recipe screen did not open");
        captured = true;
        net.minecraft.client.Screenshot.grab(mc.gameDirectory, "grinding-recipes-jei.png", mc.gameRenderer.mainRenderTarget(), 1,
                message -> mc.execute(() -> { FloraExpansion.LOGGER.info("Grinding JEI smoke test passed: {}", message.getString()); mc.stop(); }));
    }
    private static boolean releaseRecipe(net.minecraft.world.item.crafting.RecipeHolder<?> recipe) {
        return recipe.id().identifier().getNamespace().equals(FloraExpansion.MODID)
                && !recipe.id().identifier().getPath().startsWith("test_");
    }
}
