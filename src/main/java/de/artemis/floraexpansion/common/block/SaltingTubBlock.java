package de.artemis.floraexpansion.common.block;

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
 @Override public com.mojang.serialization.MapCodec<SaltingTubBlock> codec() { return simpleCodec(SaltingTubBlock::new); }
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

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS, SALTED, READY);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new SaltingTubBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type,
                de.artemis.floraexpansion.common.registry.ModBlockEntities.SALTING_TUB.get(), SaltingTubBlockEntity::serverTick);
    }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected int getLightBlock(BlockState state) { return 0; }
    @Override protected boolean propagatesSkylightDown(BlockState state) { return true; }
    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int layers = state.getValue(LAYERS);
        return layers == 0 ? SHELL : Shapes.or(SHELL, Block.box(2, 2, 2, 14,
                state.getValue(READY) ? 16 : 2 + Math.ceil(13 * layers / 4.0), 14));
    }

    @Override protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                    Player player, InteractionHand hand, BlockHitResult hit) {
        boolean salt = ModRecipes.isSaltingSalt(level, stack);
        if (!salt && !ModRecipes.isSaltingFood(level, stack)) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof SaltingTubBlockEntity tub)) return InteractionResult.CONSUME;
        boolean success = salt ? tub.salt(stack) : tub.addFood(stack);
        if (success) {
            if (!player.hasInfiniteMaterials()) stack.shrink(1);
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
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                         Player player, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
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
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, net.minecraft.core.Direction direction) {
        return state.getValue(READY) ? 15 : state.getValue(SALTED) ? 14 : state.getValue(LAYERS) * 3;
    }
}
