package de.artemis.floraexpansion.common.util;

import de.artemis.floraexpansion.common.loot.HarvestLoot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class CactusStripHelper {

    private CactusStripHelper() {
    }

    public static boolean canStrip(ItemStack stack) {
        var transformer = stack.get(net.minecraft.core.component.DataComponents.BLOCK_TRANSFORMER);
        return transformer != null && transformer.is(net.minecraft.world.item.component.BlockTransformers.AXE);
    }

    public static InteractionResult stripAndDropSpine(Level level, BlockPos pos, Player player,
                                                          InteractionHand hand, ItemStack tool,
                                                          BlockState strippedState) {
        if (!level.isClientSide()) {
            BlockState originalState = level.getBlockState(pos);
            level.setBlock(pos, strippedState, Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.AXE_STRIP.value(), SoundSource.BLOCKS, 1.0F, 1.0F);

            for (var drop : HarvestLoot.drops((ServerLevel) level, pos, originalState, player, tool, HarvestLoot.CACTUS_STRIPPING))
                Block.popResource(level, pos, drop);

            tool.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        return ((level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER));
    }
}

