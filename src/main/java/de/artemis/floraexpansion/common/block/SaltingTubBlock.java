package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import de.artemis.floraexpansion.common.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** A small open tub: load food one serving at a time, cap with salt, then collect. */
public class SaltingTubBlock extends BaseEntityBlock {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return useWithoutItem(state, level, pos, player, hit);
    }


    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 0, 4);
    public static final BooleanProperty SALTED = BooleanProperty.create("salted");
    public static final BooleanProperty READY = BooleanProperty.create("ready");
    private static final VoxelShape SHELL = Shapes.or(Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 2, 0, 2, 16, 16), Block.box(14, 2, 0, 16, 16, 16),
            Block.box(2, 2, 0, 14, 16, 2), Block.box(2, 2, 14, 14, 16, 16));

    public SaltingTubBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(LAYERS, 0).setValue(SALTED, false).setValue(READY, false));
    }

    @Override public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, SALTED, READY);
    }
    @Override public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) { return new SaltingTubBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, @NotNull BlockState state, @NotNull net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type,
                de.artemis.floraexpansion.common.registry.ModBlockEntities.SALTING_TUB.get(), SaltingTubBlockEntity::serverTick);
    }
    @NotNull @Override public RenderShape getRenderShape(@NotNull BlockState state) { return RenderShape.MODEL; }
    @Override public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) { return 0; }
    @Override public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) { return true; }
    @NotNull @Override public VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        int layers = state.getValue(LAYERS);
        return layers == 0 ? SHELL : Shapes.or(SHELL, Block.box(2, 2, 2, 14,
                state.getValue(READY) ? 16 : 2 + Math.ceil(13 * layers / 4.0), 14));
    }

    @NotNull public net.minecraft.world.InteractionResult useWithItem(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                                    @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        boolean salt = ModRecipes.isSaltingSalt(level, stack);
        if (!salt && !ModRecipes.isSaltingFood(level, stack)) return net.minecraft.world.InteractionResult.PASS;
        if (level.isClientSide) return net.minecraft.world.InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof SaltingTubBlockEntity tub)) return net.minecraft.world.InteractionResult.CONSUME;
        boolean success = salt ? tub.salt(stack) : tub.addFood(stack);
        if (success) {
            if (!player.getAbilities().instabuild) stack.shrink(1);
            InventoryUtils.syncPlayerInventory(player);
            level.playSound(null, pos, salt ? SoundEvents.SAND_PLACE : SoundEvents.COMPOSTER_FILL,
                    SoundSource.BLOCKS, 0.7F, 1F);
        } else {
            player.displayClientMessage(Component.translatable(state.getValue(READY)
                    ? "message.floraexpansion.salting_tub.collect"
                    : state.getValue(SALTED) ? "message.floraexpansion.salting_tub.processing"
                    : salt ? "message.floraexpansion.salting_tub.needs_food"
                    : "message.floraexpansion.salting_tub.same_food"), true);
        }
        return net.minecraft.world.InteractionResult.CONSUME;
    }

    @NotNull public InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                                         @NotNull Player player, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof SaltingTubBlockEntity tub) {
            boolean finished = tub.isReady();
            var contents = tub.take(player.isShiftKeyDown());
            if (finished && !contents.isEmpty())
                de.artemis.floraexpansion.common.advancement.FloraActionTrigger.fire(player, "salt_food");
            for (var stack : contents) InventoryUtils.giveOrDrop(player, stack);
            if (!contents.isEmpty()) InventoryUtils.syncPlayerInventory(player);
            if (!contents.isEmpty()) level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 0.7F, 1F);
            else player.displayClientMessage(Component.translatable(state.getValue(SALTED) && !state.getValue(READY)
                    ? "message.floraexpansion.salting_tub.processing" : "message.floraexpansion.salting_tub.instructions"), true);
        }
        return InteractionResult.CONSUME;
    }

    @Override public boolean hasAnalogOutputSignal(@NotNull BlockState state) { return true; }
    @Override public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState replacement, boolean moved) {
        if (!state.is(replacement.getBlock())) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof SaltingTubBlockEntity tub) tub.dropContents();
            super.onRemove(state, level, pos, replacement, moved);
        }
    }
    @Override public int getAnalogOutputSignal(BlockState state, @NotNull Level level, @NotNull BlockPos pos) {
        return state.getValue(READY) ? 15 : state.getValue(SALTED) ? 14 : state.getValue(LAYERS) * 3;
    }
}
