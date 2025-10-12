package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.particle.ModParticles;
import de.artemis.floraexpansion.common.util.ModBlockStateProperties;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public class PineLitterBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<PineLitterBlock> CODEC = simpleCodec(PineLitterBlock::new);
    public static final int MIN_SEGMENTS = 1;
    public static final int MAX_SEGMENTS = 4;
    public static final DirectionProperty FACING;
    public static final IntegerProperty AMOUNT;
    private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES;

    public @NotNull MapCodec<PineLitterBlock> codec() {
        return CODEC;
    }

    public PineLitterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState) ((BlockState) ((BlockState) this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(AMOUNT, 1));
    }

    public @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return (BlockState) blockState.setValue(FACING, rotation.rotate((Direction) blockState.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation((Direction) blockState.getValue(FACING)));
    }

    public boolean canBeReplaced(@NotNull BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return !blockPlaceContext.isSecondaryUseActive() && blockPlaceContext.getItemInHand().is(this.asItem()) && (Integer) blockState.getValue(AMOUNT) < 4 ? true : super.canBeReplaced(blockState, blockPlaceContext);
    }

    public @NotNull VoxelShape getShape(BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext context) {
        return (VoxelShape) SHAPE_BY_PROPERTIES.apply((Direction) blockState.getValue(FACING), (Integer) blockState.getValue(AMOUNT));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos());
        return blockstate.is(this) ? (BlockState) blockstate.setValue(AMOUNT, Math.min(4, (Integer) blockstate.getValue(AMOUNT) + 1)) : (BlockState) this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> blockBlockStateBuilder) {
        blockBlockStateBuilder.add(new Property[]{FACING, AMOUNT});
    }

    public boolean isValidBonemealTarget(@NotNull LevelReader levelReader, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return true;
    }

    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource randomSource, @NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return true;
    }

    public void performBonemeal(@NotNull ServerLevel serverLevel, @NotNull RandomSource randomSource, @NotNull BlockPos blockPos, BlockState blockState) {
        int i = (Integer) blockState.getValue(AMOUNT);
        if (i < 4) {
            serverLevel.setBlock(blockPos, (BlockState) blockState.setValue(AMOUNT, i + 1), 2);
        } else {
            popResource(serverLevel, blockPos, new ItemStack(this));
        }

    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack itemStack, @NotNull BlockState blockState, @NotNull Level level, BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, BlockHitResult blockHitResult) {

        if (player.getItemInHand(interactionHand).isEmpty()) {
            level.addFreshEntity(new ItemEntity(level,
                    blockPos.getX() + 0.5,
                    blockPos.getY() + 0.5,
                    blockPos.getZ() + 0.5,
                    new ItemStack(ModItems.PINE_CONE.get(), blockState.getValue(LeafLitterBlock.AMOUNT))));
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

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        AMOUNT = ModBlockStateProperties.SEGMENT_AMOUNT;
        SHAPE_BY_PROPERTIES = Util.memoize((p_296142_, p_294775_) -> {
            VoxelShape[] avoxelshape = new VoxelShape[]{Block.box((double) 8.0F, (double) 0.0F, (double) 8.0F, (double) 16.0F, (double) 3.0F, (double) 16.0F), Block.box((double) 8.0F, (double) 0.0F, (double) 0.0F, (double) 16.0F, (double) 3.0F, (double) 8.0F), Block.box((double) 0.0F, (double) 0.0F, (double) 0.0F, (double) 8.0F, (double) 3.0F, (double) 8.0F), Block.box((double) 0.0F, (double) 0.0F, (double) 8.0F, (double) 8.0F, (double) 3.0F, (double) 16.0F)};
            VoxelShape voxelshape = Shapes.empty();

            for (int i = 0; i < p_294775_; ++i) {
                int j = Math.floorMod(i - p_296142_.get2DDataValue(), 4);
                voxelshape = Shapes.or(voxelshape, avoxelshape[j]);
            }

            return voxelshape.singleEncompassing();
        });
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        for (Player player : level.players()) {
            if (player.onGround() && player.blockPosition().equals(pos)) {
                double dx = player.getX() - player.xOld;
                double dz = player.getZ() - player.zOld;
                double speedSq = dx * dx + dz * dz;

                if (speedSq > 0.0003 && random.nextFloat() < 0.65F) {
                    int count = 2 + random.nextInt(4);

                    for (int i = 0; i < count; i++) {
                        double x = pos.getX() + 0.1 + random.nextDouble() * 0.8;
                        double z = pos.getZ() + 0.1 + random.nextDouble() * 0.8;
                        double y = pos.getY() + 0.05 + random.nextDouble() * 0.1;

                        double angle = random.nextDouble() * Math.PI * 2;
                        double speed = 0.025 + random.nextDouble() * 0.015;
                        double vx = Math.cos(angle) * speed;
                        double vz = Math.sin(angle) * speed;
                        double vy = 0.005 + random.nextDouble() * 0.01;

                        level.addParticle(ModParticles.PINE_LEAF_FLUFF_PARTICLES.get(), x, y, z, vx, vy, vz);
                    }

                    if (random.nextFloat() < 0.2F) {
                        double x = pos.getX() + 0.3 + random.nextDouble() * 0.4; // centered within block
                        double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
                        double y = pos.getY() + 0.05 + random.nextDouble() * 0.1;
                        double angle = random.nextDouble() * Math.PI * 2;
                        double speed = 0.012 + random.nextDouble() * 0.008; // gentle drift
                        double vx = Math.cos(angle) * speed;
                        double vz = Math.sin(angle) * speed;
                        double vy = 0.025 + random.nextDouble() * 0.01; // short upward pop

                        level.addParticle(ModParticles.PINE_PARTICLES.get(), x, y, z, vx, vy, vz);

                    }

                    level.playLocalSound(
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            SoundEvents.HANGING_ROOTS_STEP,
                            SoundSource.BLOCKS,
                            0.8F + random.nextFloat() * 0.2F,
                            0.9F + random.nextFloat() * 0.3F,
                            false
                    );
                }
            }
        }
    }
}