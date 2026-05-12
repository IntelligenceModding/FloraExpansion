package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.CrateBlock;
import de.artemis.floraexpansion.common.network.payload.CrateExtractPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = FloraExpansion.MODID, value = Dist.CLIENT)
public final class CrateInteractionEvents {
    private static boolean wasAttackDown;

    private CrateInteractionEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            wasAttackDown = false;
            return;
        }

        boolean attackDown = minecraft.options.keyAttack.isDown();
        if (!attackDown || wasAttackDown || minecraft.player.isCreative() || !(minecraft.hitResult instanceof BlockHitResult blockHitResult)) {
            wasAttackDown = attackDown;
            return;
        }

        Level level = minecraft.level;
        BlockPos pos = blockHitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CrateBlock) || state.getValue(CrateBlock.POWERED) || state.getValue(CrateBlock.PACKED)) {
            wasAttackDown = attackDown;
            return;
        }

        wasAttackDown = attackDown;
        PacketDistributor.sendToServer(new CrateExtractPayload(pos.immutable(), minecraft.player.isShiftKeyDown()));
    }
}
