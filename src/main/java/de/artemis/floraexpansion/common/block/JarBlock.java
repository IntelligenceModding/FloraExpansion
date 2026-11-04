package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import de.artemis.floraexpansion.common.inventory.JarInputContainer;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class JarBlock extends Block implements WorldlyContainerHolder {
    @NotNull @Override public net.minecraft.world.InteractionResult use(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.world.entity.player.Player player, @NotNull net.minecraft.world.InteractionHand hand, @NotNull net.minecraft.world.phys.BlockHitResult hit) {
        var result = useWithItem(player.getItemInHand(hand), state, level, pos, player, hand, hit);
        if (result != net.minecraft.world.InteractionResult.PASS) return result;
        return super.use(state, level, pos, player, hand, hit);
    }

    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(4, 0, 4, 12, 8, 12),
            Block.box(5, 8, 5, 11, 10, 11),
            Block.box(4, 10, 4, 12, 12, 12));

    public JarBlock(Properties properties) {
        super(properties);
    }

    @org.jetbrains.annotations.Nullable
    public static JamJarBlock jamForFruit(ItemStack fruit) {
        if (fruit.is(ModItems.BLUEBERRIES.get())) return ModBlocks.BLUEBERRY_JAM.get();
        if (fruit.is(ModItems.STRAWBERRY.get())) return ModBlocks.STRAWBERRY_JAM.get();
        if (fruit.is(ModItems.CHERRIES.get())) return ModBlocks.CHERRY_JAM.get();
        if (fruit.is(Items.APPLE)) return ModBlocks.APPLE_JAM.get();
        return null;
    }

    public static boolean canAddFruit(BlockState state, ItemStack fruit) {
        JamJarBlock jam = jamForFruit(fruit);
        return jam != null && (state.is(ModBlocks.GLASS_JAR.get())
                || state.is(jam) && state.getValue(JamJarBlock.PORTIONS) < JamJarBlock.MAX_PORTIONS);
    }

    public static boolean addFruit(LevelAccessor level, BlockPos pos, ItemStack fruit) {
        BlockState state = level.getBlockState(pos);
        if (!canAddFruit(state, fruit)) return false;
        JamJarBlock jam = jamForFruit(fruit);
        if (jam == null) return false;
        int portions = state.hasProperty(JamJarBlock.PORTIONS) ? state.getValue(JamJarBlock.PORTIONS) : 0;
        level.setBlock(pos, jam.defaultBlockState().setValue(JamJarBlock.PORTIONS, portions + 1), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.6F, 1.0F);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        return true;
    }

    @NotNull public net.minecraft.world.InteractionResult useWithItem(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player,
                                           @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (jamForFruit(stack) == null) return net.minecraft.world.InteractionResult.PASS;
        if (player.isSpectator() || !canAddFruit(state, stack)) return net.minecraft.world.InteractionResult.FAIL;
        if (!level.isClientSide && addFruit(level, pos, stack)) de.artemis.floraexpansion.common.util.ItemData.consume(stack, 1, player);
        return level.isClientSide ? net.minecraft.world.InteractionResult.SUCCESS : net.minecraft.world.InteractionResult.CONSUME;
    }

    @Override
    @NotNull public WorldlyContainer getContainer(@NotNull BlockState state, @NotNull LevelAccessor level, @NotNull BlockPos pos) {
        return new JarInputContainer(level, pos);
    }

    @Override
    @NotNull public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    @NotNull public BlockState updateShape(@NotNull net.minecraft.world.level.block.state.BlockState state, @NotNull net.minecraft.core.Direction direction, @NotNull net.minecraft.world.level.block.state.BlockState neighborState, @NotNull net.minecraft.world.level.LevelAccessor level, @NotNull net.minecraft.core.BlockPos pos, @NotNull net.minecraft.core.BlockPos neighborPos) {
        return direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}
