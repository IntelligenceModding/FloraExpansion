package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.DesertMossBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

final class FloweringMossSmokeTest {
    private static BlockPos origin;
    private static int startTick;
    private static boolean complete;
    private static boolean screenshotRequested;

    static boolean check(Minecraft minecraft, int ticks) {
        if (complete) return true;
        if (origin == null) {
            verifyItem(minecraft);
            origin = new BlockPos(minecraft.player.getBlockX() + 24, 250, minecraft.player.getBlockZ());
            startTick = ticks;
            var playerId = minecraft.player.getUUID();
            minecraft.getSingleplayerServer().execute(() -> {
                var level = minecraft.getSingleplayerServer().overworld();
                for (var pos : BlockPos.betweenClosed(origin.offset(-1, -1, -4), origin.offset(8, -1, 6))) {
                    level.setBlockAndUpdate(pos.below(), Blocks.SMOOTH_QUARTZ.defaultBlockState());
                    level.setBlockAndUpdate(pos, Blocks.SAND.defaultBlockState());
                }
                for (int variant = 0; variant < 4; variant++) {
                    level.setBlockAndUpdate(origin.offset(variant * 2, 0, 0),
                            ModBlocks.DESERT_MOSS.get().defaultBlockState().setValue(DesertMossBlock.VARIANT, variant));
                    level.setBlockAndUpdate(origin.offset(variant * 2, 0, -2),
                            ModBlocks.FLOWERING_DESERT_MOSS.get().defaultBlockState().setValue(DesertMossBlock.VARIANT, variant));
                }
                var player = minecraft.getSingleplayerServer().getPlayerList().getPlayer(playerId);
                player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
                        new net.minecraft.world.item.ItemStack(ModBlocks.FLOWERING_DESERT_MOSS.get()));
                player.getInventory().setItem(1, new net.minecraft.world.item.ItemStack(ModBlocks.DESERT_MOSS.get()));
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.setYRot(180);
                player.setXRot(38);
                player.teleportTo(origin.getX() + 3.5, 253, origin.getZ() + 5);
            });
            return false;
        }
        minecraft.player.setYRot(180);
        minecraft.player.setXRot(38);
        minecraft.player.getAbilities().flying = true;
        if (ticks - startTick < 40) return false;
        for (int variant = 0; variant < 4; variant++) {
            var state = minecraft.level.getBlockState(origin.offset(variant * 2, 0, -2));
            if (!state.is(ModBlocks.FLOWERING_DESERT_MOSS.get())) {
                if (ticks - startTick < 200) return false;
                throw new IllegalStateException("Flowering moss did not synchronize");
            }
            var models = minecraft.getModelManager().getBlockModelShaper();
            if (models.getBlockModel(state) == models.getModelManager().getMissingBlockStateModel()) throw new IllegalStateException("Missing flowering moss model");
        }
        if (screenshotRequested) return false;
        screenshotRequested = true;
        net.minecraft.client.Screenshot.grab(minecraft.gameDirectory, "flowering-desert-moss.png",
                minecraft.getMainRenderTarget(), 1,
                message -> minecraft.execute(() -> {
                    FloraExpansion.LOGGER.info("Flowering moss preview: {}", message.getString());
                    complete = true;
                }));
        return false;
    }

    private static void verifyItem(Minecraft minecraft) {
        if (!net.minecraft.network.chat.Component.translatable("item.floraexpansion.flowering_desert_moss")
                .getString().equals("Flowering Desert Moss")) {
            throw new IllegalStateException("Missing flowering moss item translation");
        }
        var base = itemSprite(minecraft, ModBlocks.DESERT_MOSS.get());
        var icon = itemSprite(minecraft, ModBlocks.FLOWERING_DESERT_MOSS.get());
        if (!icon.contents().name().equals(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(
                FloraExpansion.MODID, "item/flowering_desert_moss"))) {
            throw new IllegalStateException("Flowering moss does not use its flat item sprite");
        }
        var texture = net.minecraft.resources.ResourceLocation.withDefaultNamespace("textures/block/pink_petals.png");
        try (var stream = minecraft.getResourceManager().getResourceOrThrow(texture).open();
             var petals = com.mojang.blaze3d.platform.NativeImage.read(stream)) {
            int changed = 0;
            for (int y = 0; y < 16; y++) for (int x = 0; x < 16; x++) {
                int expected = base.contents().getOriginalImage().getPixel( x, y);
                for (int[] flower : new int[][]{{3, 2}, {11, 5}, {5, 10}}) {
                    if (x >= flower[0] && x < flower[0] + 3 && y >= flower[1] && y < flower[1] + 3) {
                        int color = petals.getPixel(3 + x - flower[0], y - flower[1]);
                        if ((color >>> 24) != 0) expected = color;
                    }
                }
                int actual = icon.contents().getOriginalImage().getPixel( x, y);
                if ((actual >>> 24) != (expected >>> 24) || (expected >>> 24) != 0 && actual != expected) {
                    throw new IllegalStateException("Flowering moss icon pixel differs at " + x + "," + y
                            + ": actual=" + Integer.toHexString(actual) + ", expected=" + Integer.toHexString(expected));
                }
                if (expected != base.contents().getOriginalImage().getPixel( x, y)) changed++;
            }
            if (changed != 15) throw new IllegalStateException("Flowering moss must add three five-pixel flowers");
        } catch (java.io.IOException error) {
            throw new IllegalStateException(error);
        }
        FloraExpansion.LOGGER.info("Flowering moss item passed: translated name, flat model, original moss pixels and three native-size flowers");
    }

    private static net.minecraft.client.renderer.texture.TextureAtlasSprite itemSprite(Minecraft minecraft,
                                                                                     net.minecraft.world.level.block.Block block) {
        var state = new net.minecraft.client.renderer.item.ItemStackRenderState();
        minecraft.getItemModelResolver().updateForTopItem(state, new net.minecraft.world.item.ItemStack(block),
                net.minecraft.world.item.ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);
        var material = state.pickParticleIcon(net.minecraft.util.RandomSource.create(0));
        if (material == null) throw new IllegalStateException("Moss item has no sprite");
        return material;
    }
}
