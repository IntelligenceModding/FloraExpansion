package de.artemis.floraexpansion.common.network;

import de.artemis.floraexpansion.common.util.InventoryUtils;
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
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.NetworkEvent;
import de.artemis.floraexpansion.common.config.ServerConfig;

public final class ModPayloads {
    private ModPayloads() {
    }

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("floraexpansion","main"), () -> "1", "1"::equals, "1"::equals);
    public static void register() {
        CHANNEL.messageBuilder(CrateExtractPayload.class, 0, net.minecraftforge.network.NetworkDirection.PLAY_TO_SERVER)
            .encoder((packet, buffer) -> { buffer.writeBlockPos(packet.pos()); buffer.writeBoolean(packet.fullStack()); })
            .decoder(buffer -> new CrateExtractPayload(buffer.readBlockPos(), buffer.readBoolean()))
            .consumerNetworkThread((payload, supplier) -> { handleCrateExtract(payload, supplier.get()); }).add();
    }

    private static void handleCrateExtract(CrateExtractPayload payload, NetworkEvent.Context context) {
        context.setPacketHandled(true);
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.isSpectator() || !ServerConfig.crateExtraction.get()) {
                return;
            }

            Level level = player.level();
            BlockPos pos = payload.pos();
            if (!level.isLoaded(pos) || !level.mayInteract(player, pos) || player.distanceToSqr(Vec3.atCenterOf(pos)) > 36.0D) {
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
            InventoryUtils.syncPlayerInventory(player);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.6F, payload.fullStack() ? 0.95F + level.getRandom().nextFloat() * 0.2F : 1.05F + level.getRandom().nextFloat() * 0.2F);
        });
    }

    private static void giveExtractedItem(Player player, ItemStack stack) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.isEmpty()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            return;
        }

        if (ItemStack.isSameItemSameTags(mainHandItem, stack) && mainHandItem.getCount() + stack.getCount() <= mainHandItem.getMaxStackSize()) {
            mainHandItem.grow(stack.getCount());
            return;
        }

        InventoryUtils.giveOrDrop(player, stack);
    }

}
