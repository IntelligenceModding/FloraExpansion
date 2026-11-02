package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class SaltWaterSmokeTest {
    private static boolean creating, prepared, captured;
    private static int ticks, preparedTick;
    private static BlockPos origin;
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        if (!Boolean.getBoolean("floraexpansion.saltWaterSmokeTest") || captured) return;
        var mc = Minecraft.getInstance();
        if (!creating && mc.gui.screen() instanceof TitleScreen && mc.gui.overlay() == null) {
            creating = true;
            mc.createWorldOpenFlows().createFreshLevel("salt-water-smoke-" + System.currentTimeMillis(),
                    new net.minecraft.world.level.LevelSettings("Salt Water preview", net.minecraft.world.level.GameType.CREATIVE,
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
            var playerId = mc.player.getUUID();
            mc.getSingleplayerServer().execute(() -> {
                var level = mc.getSingleplayerServer().overworld();
                for (var pos : BlockPos.betweenClosed(origin.offset(-2,-2,-2), origin.offset(13,-1,9)))
                    level.setBlockAndUpdate(pos, Blocks.SMOOTH_STONE.defaultBlockState());
                for (int x=0; x<=8; x+=5) {
                    for (var pos : BlockPos.betweenClosed(origin.offset(x,0,0), origin.offset(x+3,0,3))) {
                        boolean edge=pos.getX()==origin.getX()+x || pos.getX()==origin.getX()+x+3
                                || pos.getZ()==origin.getZ() || pos.getZ()==origin.getZ()+3;
                        level.setBlockAndUpdate(pos, edge ? Blocks.SMOOTH_STONE.defaultBlockState()
                                : x==0 ? Blocks.WATER.defaultBlockState() : ModBlocks.SALT_WATER.get().defaultBlockState());
                    }
                }
                for (int fill=1; fill<=3; fill++) level.setBlockAndUpdate(origin.offset((fill-1)*2+2,0,5),
                        ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL,fill));
                var player=mc.getSingleplayerServer().getPlayerList().getPlayer(playerId);
                player.getInventory().setItem(0,new ItemStack(ModItems.SALT_WATER_BUCKET.get()));
                player.getInventory().setItem(1,new ItemStack(ModItems.WOODEN_SALT_WATER_BUCKET.get()));
                player.getAbilities().flying=true;player.onUpdateAbilities();
                player.teleportTo(origin.getX()+4.5,254,origin.getZ()+12);
                player.setYRot(180);player.setXRot(25);
            });
            return;
        }
        mc.player.setYRot(180);mc.player.setXRot(25);mc.player.getAbilities().flying=true;
        if (ticks-preparedTick<60) return;
        var models=mc.getModelManager().getBlockStateModelSet();
        for(var state:ModBlocks.SALT_WATER_CAULDRON.get().getStateDefinition().getPossibleStates())
            if(models.get(state)==models.missingModel()) throw new IllegalStateException("Missing Salt Water cauldron model");
        var still=mc.getModelManager().getFluidStateModelSet().get(ModFluids.SALT_WATER.get().defaultFluidState());
        var flowing=mc.getModelManager().getFluidStateModelSet().get(ModFluids.FLOWING_SALT_WATER.get().defaultFluidState());
        if(!still.stillMaterial().sprite().contents().name().equals(net.minecraft.resources.Identifier.withDefaultNamespace("block/water_still"))
                || !flowing.flowingMaterial().sprite().contents().name().equals(net.minecraft.resources.Identifier.withDefaultNamespace("block/water_flow")))
            throw new IllegalStateException("Missing Salt Water fluid material");
        for(var item:java.util.List.of(ModItems.SALT_WATER_BUCKET.get(),ModItems.WOODEN_SALT_WATER_BUCKET.get())) {
            var state=new net.minecraft.client.renderer.item.ItemStackRenderState();
            var stack=new ItemStack(item);
            mc.getItemModelResolver().updateForTopItem(state,stack,net.minecraft.world.item.ItemDisplayContext.GUI,mc.level,mc.player,0);
            var material=state.pickParticleMaterial(net.minecraft.util.RandomSource.create(0));
            if(state.isEmpty() || material==null || material.sprite()==mc.getAtlasManager().getAtlasOrThrow(net.minecraft.data.AtlasIds.ITEMS).missingSprite())
                throw new IllegalStateException("Missing Salt Water item model: "+item);
            if(stack.getHoverName().getString().startsWith("item.")) throw new IllegalStateException("Salt Water translation missing");
        }
        captured=true;
        net.minecraft.client.Screenshot.grab(mc.gameDirectory,"salt-water.png",mc.gameRenderer.mainRenderTarget(),1,
                message->mc.execute(()->{FloraExpansion.LOGGER.info("Salt Water client smoke test passed: {}",message.getString());mc.stop();}));
    }
}
