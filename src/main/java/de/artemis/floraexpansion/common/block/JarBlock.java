package de.artemis.floraexpansion.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import de.artemis.floraexpansion.common.inventory.JarInputContainer;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class JarBlock extends Block implements WorldlyContainerHolder {
    private static final VoxelShape SHAPE = Shapes.or(
            Block.box(4, 0, 4, 12, 8, 12),
            Block.box(5, 8, 5, 11, 10, 11),
            Block.box(4, 10, 4, 12, 12, 12));

    public JarBlock(Properties properties) {
        super(properties);
    }

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
        int portions = state.hasProperty(JamJarBlock.PORTIONS) ? state.getValue(JamJarBlock.PORTIONS) : 0;
        level.setBlock(pos, jamForFruit(fruit).defaultBlockState().setValue(JamJarBlock.PORTIONS, portions + 1), Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.6F, 1.0F);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        return true;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                           InteractionHand hand, BlockHitResult hit) {
        if (jamForFruit(stack) == null) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (player.isSpectator() || !canAddFruit(state, stack)) return InteractionResult.FAIL;
        if (!level.isClientSide() && addFruit(level, pos, stack)) stack.consume(1, player);
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        return new JarInputContainer(level, pos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return Block.canSupportCenter(level, pos.below(), Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        return direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
    }
}
