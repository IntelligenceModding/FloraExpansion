package de.artemis.floraexpansion.common.network;

import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.block.entity.CrateBlockEntity;
import de.artemis.floraexpansion.common.network.payload.CrateExtractPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ModPayloads {
    private ModPayloads() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(CrateExtractPayload.TYPE, CrateExtractPayload.STREAM_CODEC, ModPayloads::handleCrateExtract);
    }

    private static void handleCrateExtract(CrateExtractPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            Level level = player.level();
            BlockPos pos = payload.pos();
            if (!level.isLoaded(pos) || player.distanceToSqr(Vec3.atCenterOf(pos)) > 36.0D) {
                return;
            }

            BlockState state = level.getBlockState(pos);
            if (!(state.getBlock() instanceof CrateBlock) || state.getValue(CrateBlock.POWERED) || state.getValue(CrateBlock.PACKED)) {
                return;
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof CrateBlockEntity crate) || crate.isEmpty()) {
                return;
            }

            ItemStack extracted = payload.fullStack() ? crate.extractItem() : crate.extractSingleItem();
            if (extracted.isEmpty()) {
                return;
            }

            giveExtractedItem(player, extracted);
            syncPlayerInventory(player);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.6F, payload.fullStack() ? 0.95F + level.getRandom().nextFloat() * 0.2F : 1.05F + level.getRandom().nextFloat() * 0.2F);
        });
    }

    private static void giveExtractedItem(Player player, ItemStack stack) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            return;
        }

        if (ItemStack.isSameItemSameComponents(mainHandItem, stack) && mainHandItem.getCount() + stack.getCount() <= mainHandItem.getMaxStackSize()) {
            mainHandItem.grow(stack.getCount());
            return;
        }

        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static void syncPlayerInventory(Player player) {
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
    }
}
