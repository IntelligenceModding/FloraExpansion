package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.util.CactusStripHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.registry.ModBlocks;

public class CactusLogBlock extends RotatedPillarBlock {

    public CactusLogBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected @NotNull net.minecraft.world.ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!de.artemis.floraexpansion.common.util.CactusStripHelper.canStrip(stack)) {
            return net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockState strippedState = ModBlocks.STRIPPED_CACTUS_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        return CactusStripHelper.stripAndDropSpine(level, pos, player, hand, stack, strippedState);
    }
}
