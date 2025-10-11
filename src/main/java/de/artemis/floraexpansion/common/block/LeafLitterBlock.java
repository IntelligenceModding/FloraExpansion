package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class LeafLitterBlock extends PineLitterBlock {

    public LeafLitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack itemStack, @NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {

        if (player.getItemInHand(interactionHand).isEmpty()) {
            level.addFreshEntity(new ItemEntity(level,
                    blockPos.getX() + 0.5,
                    blockPos.getY() + 0.5,
                    blockPos.getZ() + 0.5,
                    new ItemStack(ModItems.TWIG.get(), level.random.nextInt(2 * blockState.getValue(LeafLitterBlock.AMOUNT)))));

            level.destroyBlock(blockPos, false);
            level.playSound(null, blockPos, SoundEvents.MOSS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.BLOCK_MINED.get(this));
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
