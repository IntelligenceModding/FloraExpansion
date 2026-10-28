package de.artemis.floraexpansion.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Harvest rules shared by a plant's hand interaction and the harvesting basket. */
public interface HarvestableBlock {
    boolean canHarvest(BlockState state);

    ItemStack getHarvest(BlockState state, RandomSource random);

    BlockState getHarvestedState(BlockState state);

    /** Standard bush interaction; plants with different effects can use the harvest rules directly. */
    default InteractionResult harvest(BlockState state, Level level, BlockPos pos, Player player) {
        if (!canHarvest(state)) {
            return InteractionResult.PASS;
        }
        ItemStack harvest = getHarvest(state, level.getRandom());
        if (!level.isClientSide()) {
            Block.popResource(level, pos, harvest);
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS,
                    1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
            BlockState resetState = getHarvestedState(state);
            level.setBlock(pos, resetState, 2);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, resetState));
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }
}
