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
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return super.use(state, level, pos, player, hand, hit);
    }


    public CactusLogBlock(Properties properties) {
        super(properties);
    }


    public @NotNull net.minecraft.world.InteractionResult useWithItem(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!de.artemis.floraexpansion.common.util.CactusStripHelper.canStrip(stack)) {
            return net.minecraft.world.InteractionResult.PASS;
        }

        BlockState strippedState = ModBlocks.STRIPPED_CACTUS_LOG.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        return CactusStripHelper.stripAndDropSpine(level, pos, player, hand, stack, strippedState);
    }
}
