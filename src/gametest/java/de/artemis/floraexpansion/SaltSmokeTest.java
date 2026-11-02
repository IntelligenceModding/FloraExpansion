package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

final class SaltSmokeTest {
    private static BlockPos origin;
    private static int startTick;
    private static boolean requested;
    private static boolean complete;

    static boolean check(Minecraft minecraft, int ticks) {
        if (complete) return true;
        if (origin == null) {
            verifyItems(minecraft);
            origin = new BlockPos(minecraft.player.getBlockX() + 24, 250, minecraft.player.getBlockZ());
            startTick = ticks;
            var playerId = minecraft.player.getUUID();
            minecraft.getSingleplayerServer().execute(() -> {
                var level = minecraft.getSingleplayerServer().overworld();
                for (var pos : BlockPos.betweenClosed(origin.offset(-2, -1, -5), origin.offset(15, -1, 12))) {
                    level.setBlockAndUpdate(pos, Blocks.SMOOTH_QUARTZ.defaultBlockState());
                }
                for (int layers = 1; layers <= 4; layers++) {
                    level.setBlockAndUpdate(origin.offset((layers - 1) * 3, 0, -3),
                            ModBlocks.SALT_DEPOSIT.get().defaultBlockState().setValue(SaltDepositBlock.LAYERS, layers));
                }
                level.setBlockAndUpdate(origin.offset(12, 0, -3), ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
                var player = minecraft.getSingleplayerServer().getPlayerList().getPlayer(playerId);
                var items = java.util.List.of(ModItems.SALT.get(), ModItems.RAW_SALT.get(), ModItems.SALTED_SPRUCE_NUTS.get(),
                        ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(), ModItems.SALTED_RAW_MEAT.get(), ModItems.SALTED_RAW_FISH.get(),
                        ModItems.CURED_MEAT.get(), ModItems.CURED_FISH.get());
                for (int slot = 0; slot < items.size(); slot++) player.getInventory().setItem(slot, new ItemStack(items.get(slot)));
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
                player.setYRot(180);
                player.setXRot(32);
                player.teleportTo(origin.getX() + 6.5, 255, origin.getZ() + 10);
            });
            return false;
        }
        minecraft.player.setYRot(180);
        minecraft.player.setXRot(32);
        minecraft.player.getAbilities().flying = true;
        if (ticks - startTick < 40) return false;
        if (requested) return false;
        requested = true;
        net.minecraft.client.Screenshot.grab(minecraft.gameDirectory, "salt-content.png", minecraft.getMainRenderTarget(), 1,
                message -> minecraft.execute(() -> {
                    FloraExpansion.LOGGER.info("Salt preview: {}", message.getString());
                    complete = true;
                }));
        return false;
    }

    private static void verifyItems(Minecraft minecraft) {
        for (var item : java.util.List.of(ModItems.SALT.get(), ModItems.RAW_SALT.get(), ModItems.SALTED_SPRUCE_NUTS.get(),
                ModItems.SALTED_TOASTED_SPRUCE_NUTS.get(), ModItems.SALTED_RAW_MEAT.get(), ModItems.SALTED_RAW_FISH.get(),
                ModItems.CURED_MEAT.get(), ModItems.CURED_FISH.get())) {
            String key = item.getDescriptionId();
            if (net.minecraft.network.chat.Component.translatable(key).getString().equals(key)) {
                throw new IllegalStateException("Missing salt item translation: " + key);
            }
            var state = new net.minecraft.client.renderer.item.ItemStackRenderState();
            minecraft.getItemModelResolver().updateForTopItem(state, new ItemStack(item),
                    net.minecraft.world.item.ItemDisplayContext.GUI, minecraft.level, minecraft.player, 0);
            var material = state.pickParticleMaterial(net.minecraft.util.RandomSource.create(0));
            var expected = net.minecraft.resources.Identifier.fromNamespaceAndPath(FloraExpansion.MODID,
                    "item/" + item.builtInRegistryHolder().key().identifier().getPath());
            if (material == null || !material.sprite().contents().name().equals(expected)
                    || material.sprite().contents().width() != 16 || material.sprite().contents().height() != 16) {
                throw new IllegalStateException("Missing or scaled salt sprite: " + expected);
            }
        }
        FloraExpansion.LOGGER.info("Salt item names, models and native 16-pixel sprites verified");
    }
}
