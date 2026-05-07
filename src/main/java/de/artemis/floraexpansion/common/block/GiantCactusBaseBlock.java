package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.util.CactusStripHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.registry.ModBlocks;

public class GiantCactusBaseBlock extends RotatedPillarBlock {
    public static final MapCodec<GiantCactusBaseBlock> CODEC = simpleCodec(GiantCactusBaseBlock::new);

    public GiantCactusBaseBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!stack.canPerformAction(ItemAbilities.AXE_STRIP)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockState strippedState = ModBlocks.STRIPPED_GIANT_CACTUS_BASE.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
        return CactusStripHelper.stripAndDropThorn(level, pos, player, hand, stack, strippedState);
    }
}

