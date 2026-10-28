package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.util.CactusStripHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.registry.ModBlocks;

public class GiantCactusWoodBlock extends RotatedPillarBlock {

    public static final BooleanProperty GENERATED = BooleanProperty.create("generated");

    public GiantCactusWoodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(GENERATED, false));
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, GENERATED);
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack,
                                                       @NotNull BlockState state,
                                                       @NotNull Level level,
                                                       @NotNull BlockPos pos,
                                                       @NotNull Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        if (!de.artemis.floraexpansion.common.util.CactusStripHelper.canStrip(stack)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (state.is(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get())) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        BlockState strippedState = ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get().defaultBlockState()
                .setValue(AXIS, state.getValue(AXIS))
                .setValue(GENERATED, state.getValue(GENERATED));
        return CactusStripHelper.stripAndDropThorn(level, pos, player, hand, stack, strippedState);
    }
}

