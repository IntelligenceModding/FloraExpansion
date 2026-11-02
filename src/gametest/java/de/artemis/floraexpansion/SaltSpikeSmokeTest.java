package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class SaltSpikeSmokeTest {
    private static boolean creating, prepared, captured, dripsVerified;
    private static int ticks, preparedTick;
    private static BlockPos origin;
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        if (!Boolean.getBoolean("floraexpansion.saltSpikeSmokeTest") || captured) return;
        var mc = Minecraft.getInstance();
        if (!creating && mc.gui.screen() instanceof TitleScreen && mc.gui.overlay() == null) {
            creating = true;
            mc.createWorldOpenFlows().createFreshLevel("salt-spike-smoke-" + System.currentTimeMillis(),
                    new net.minecraft.world.level.LevelSettings("Salt Spike preview", net.minecraft.world.level.GameType.CREATIVE,
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
                for (var pos : BlockPos.betweenClosed(origin.offset(-2,-1,-2), origin.offset(15,-1,8)))
                    level.setBlockAndUpdate(pos, Blocks.SMOOTH_STONE.defaultBlockState());
                column(level, origin, Blocks.POINTED_DRIPSTONE, 4, Direction.UP);
                column(level, origin.offset(2,0,0), ModBlocks.SALT_SPIKE.get(), 1, Direction.UP);
                column(level, origin.offset(4,0,0), ModBlocks.SALT_SPIKE.get(), 3, Direction.UP);
                column(level, origin.offset(6,0,0), ModBlocks.SALT_SPIKE.get(), 5, Direction.UP);
                var ceiling = origin.offset(9,5,0);
                level.setBlockAndUpdate(ceiling, ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
                for (var side : Direction.Plane.HORIZONTAL) level.setBlockAndUpdate(ceiling.above().relative(side), Blocks.GLASS.defaultBlockState());
                level.setBlockAndUpdate(ceiling.above(), ModBlocks.SALT_WATER.get().defaultBlockState());
                column(level, ceiling.below(), ModBlocks.SALT_SPIKE.get(), 3, Direction.DOWN);
                column(level, origin.offset(9,0,0), ModBlocks.SALT_SPIKE.get(), 1, Direction.UP);
                var mergeBase = origin.offset(12,0,0);
                level.setBlockAndUpdate(mergeBase.above(5), ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
                column(level, mergeBase, ModBlocks.SALT_SPIKE.get(), 2, Direction.UP);
                column(level, mergeBase.above(4), ModBlocks.SALT_SPIKE.get(), 3, Direction.DOWN);
                level.setBlockAndUpdate(mergeBase.above(), ModBlocks.SALT_SPIKE.get().defaultBlockState()
                        .setValue(SpeleothemBlock.THICKNESS, SpeleothemThickness.TIP_MERGE));
                level.setBlockAndUpdate(mergeBase.above(2), ModBlocks.SALT_SPIKE.get().defaultBlockState()
                        .setValue(SpeleothemBlock.TIP_DIRECTION, Direction.DOWN).setValue(SpeleothemBlock.THICKNESS, SpeleothemThickness.TIP_MERGE));
                var player = mc.getSingleplayerServer().getPlayerList().getPlayer(playerId);
                player.getInventory().setItem(0, new ItemStack(ModBlocks.SALT_SPIKE.get()));
                player.getAbilities().flying = true; player.onUpdateAbilities();
                player.teleportTo(origin.getX()+5.5, 253, origin.getZ()+11);
                player.setYRot(180); player.setXRot(12);
            });
            return;
        }
        mc.player.setYRot(180); mc.player.setXRot(12); mc.player.getAbilities().flying = true;
        if (ticks - preparedTick < 60) return;
        var models = mc.getModelManager().getBlockStateModelSet();
        java.util.Map<Block, String> crystallineFamily = java.util.Map.of(
                ModBlocks.CRYSTALLINE_SALT.get(), "Crystalline Salt",
                ModBlocks.CRYSTALLINE_SALT_STAIRS.get(), "Crystalline Salt Stairs",
                ModBlocks.CRYSTALLINE_SALT_SLAB.get(), "Crystalline Salt Slab",
                ModBlocks.CRYSTALLINE_SALT_WALL.get(), "Crystalline Salt Wall");
        var crystallineTexture = Identifier.fromNamespaceAndPath("floraexpansion", "block/crystalline_salt");
        for (var entry : crystallineFamily.entrySet()) {
            var stack = new ItemStack(entry.getKey());
            if (!stack.getHoverName().getString().equals(entry.getValue()))
                throw new IllegalStateException("Crystalline Salt name mismatch: " + stack.getHoverName().getString());
            for (var state : entry.getKey().getStateDefinition().getPossibleStates())
                if (!models.get(state).particleMaterial().sprite().contents().name().equals(crystallineTexture))
                    throw new IllegalStateException("Crystalline Salt block texture missing: " + state);
            var itemState = new net.minecraft.client.renderer.item.ItemStackRenderState();
            mc.getItemModelResolver().updateForTopItem(itemState, stack, net.minecraft.world.item.ItemDisplayContext.GUI,
                    mc.level, mc.player, 0);
            var material = itemState.pickParticleMaterial(net.minecraft.util.RandomSource.create(0));
            if (material == null || !material.sprite().contents().name().equals(crystallineTexture))
                throw new IllegalStateException("Crystalline Salt inventory model missing: " + entry.getValue());
        }
        FloraExpansion.LOGGER.info("Crystalline Salt client names, block states, item models and renamed texture verified");
        for (var state : ModBlocks.SALT_SPIKE.get().getStateDefinition().getPossibleStates())
            if (models.get(state) == models.missingModel()) throw new IllegalStateException("Missing salt spike model: " + state);
        var atlas = mc.getAtlasManager().getAtlasOrThrow(net.minecraft.data.AtlasIds.BLOCKS);
        for (var state : ModBlocks.SALT_SPIKE.get().getStateDefinition().getPossibleStates()) {
            String suffix = state.getValue(SpeleothemBlock.TIP_DIRECTION).getSerializedName() + "_"
                    + state.getValue(SpeleothemBlock.THICKNESS).getSerializedName();
            var expected = atlas.getSprite(Identifier.fromNamespaceAndPath("floraexpansion", "block/salt_spike_" + suffix));
            if (models.get(state).particleMaterial().sprite() != expected)
                throw new IllegalStateException("Salt spike block model used the wrong atlas: " + suffix);
        }
        var itemAtlas = mc.getAtlasManager().getAtlasOrThrow(net.minecraft.data.AtlasIds.ITEMS);
        if (itemAtlas.getSprite(Identifier.fromNamespaceAndPath("floraexpansion", "item/salt_spike")) == itemAtlas.missingSprite())
            throw new IllegalStateException("Missing salt spike inventory sprite");
        for (var direction : new String[]{"up", "down"}) for (var thickness : SpeleothemThickness.values()) {
            String suffix = direction + "_" + thickness.getSerializedName();
            var salt = atlas.getSprite(Identifier.fromNamespaceAndPath("floraexpansion", "block/salt_spike_" + suffix));
            var vanilla = atlas.getSprite(Identifier.withDefaultNamespace("block/pointed_dripstone_" + suffix));
            if (salt == atlas.missingSprite()) throw new IllegalStateException("Missing salt spike sprite: " + suffix);
            int different = 0, opaque = 0, min = 255, max = 0;
            var colors = new java.util.HashSet<Integer>();
            for (int y=0; y<16; y++) for (int x=0; x<16; x++) {
                int pixel = salt.contents().getOriginalImage().getPixel(x,y);
                if ((pixel >>> 24) != (vanilla.contents().getOriginalImage().getPixel(x,y) >>> 24)) different++;
                if ((pixel >>> 24) == 0) continue;
                opaque++;
                colors.add(pixel);
                int brightness = ((pixel >> 16 & 255) + (pixel >> 8 & 255) + (pixel & 255)) / 3;
                min = Math.min(min, brightness); max = Math.max(max, brightness);
            }
            if (different == 0 || opaque < 20 || colors.size() < 20 || max - min < 70)
                throw new IllegalStateException("Salt spike lacks its crystal silhouette or facet contrast: " + suffix);
        }
        if (!dripsVerified) {
            for (var type : java.util.List.of(de.artemis.floraexpansion.common.registry.ModParticles.DRIPPING_SALT_WATER.get(),
                    de.artemis.floraexpansion.common.registry.ModParticles.FALLING_SALT_WATER.get(),
                    de.artemis.floraexpansion.common.registry.ModParticles.LANDING_SALT_WATER.get())) {
                var drip = mc.particleEngine.createParticle(type, origin.getX() + 9.5, origin.getY() + 2.2, origin.getZ() + .5, 0, 0, 0);
                if (!(drip instanceof de.artemis.floraexpansion.common.particle.SaltWaterDripParticle saltDrip)
                        || saltDrip.getType() != de.artemis.floraexpansion.common.registry.ModFluids.SALT_WATER.get())
                    throw new IllegalStateException("Salt Water drip provider lost its fluid identity");
            }
            var particleAtlas = mc.getAtlasManager().getAtlasOrThrow(net.minecraft.data.AtlasIds.PARTICLES);
            for (var texture : new String[]{"drip_hang", "drip_fall", "drip_land"})
                if (particleAtlas.getSprite(Identifier.withDefaultNamespace(texture)) == particleAtlas.missingSprite())
                    throw new IllegalStateException("Missing Salt Water drip texture: " + texture);
            dripsVerified = true;
            FloraExpansion.LOGGER.info("Salt Water hanging, falling and landing particle providers verified with actual Salt Water fluid type");
            return;
        }
        captured = true;
        net.minecraft.client.Screenshot.grab(mc.gameDirectory, "salt-spikes.png", mc.gameRenderer.mainRenderTarget(), 1,
                message -> mc.execute(() -> { FloraExpansion.LOGGER.info("Salt spike client smoke test passed: {}", message.getString()); mc.stop(); }));
    }
    private static void column(net.minecraft.server.level.ServerLevel level, BlockPos base, Block block, int length, Direction direction) {
        for (int i=0; i<length; i++) level.setBlockAndUpdate(base.relative(direction, i), block.defaultBlockState()
                .setValue(SpeleothemBlock.TIP_DIRECTION, direction));
    }
}
