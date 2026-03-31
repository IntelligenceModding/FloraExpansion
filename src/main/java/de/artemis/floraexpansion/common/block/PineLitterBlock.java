package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.particle.ModParticles;
import de.artemis.floraexpansion.common.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

@SuppressWarnings({"unused", "deprecation"})
public class PineLitterBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<BushBlock> CODEC = simpleCodec(PineLitterBlock::new);

    public static final int MIN_SEGMENTS = 1;
    public static final int MAX_SEGMENTS = 4;

    public static final EnumProperty<@NotNull Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty AMOUNT = ModBlockStateProperties.SEGMENT_AMOUNT;

    private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES = Util.memoize(
            (Direction direction, Integer amount) -> {
                VoxelShape[] shapes = new VoxelShape[]{
                        Block.box(8.0, 0.0, 8.0, 16.0, 3.0, 16.0),
                        Block.box(8.0, 0.0, 0.0, 16.0, 3.0, 8.0),
                        Block.box(0.0, 0.0, 0.0, 8.0, 3.0, 8.0),
                        Block.box(0.0, 0.0, 8.0, 8.0, 3.0, 16.0)
                };

                VoxelShape shape = Shapes.empty();

                for (int i = 0; i < amount; ++i) {
                    int index = Math.floorMod(i - direction.get2DDataValue(), 4);
                    shape = Shapes.or(shape, shapes[index]);
                }

                return shape.singleEncompassing();
            }
    );

    public PineLitterBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(AMOUNT, 1));
    }

    @Override
    public @NotNull MapCodec<BushBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
        return !context.isSecondaryUseActive()
                && context.getItemInHand().is(this.asItem())
                && state.getValue(AMOUNT) < 4
                || super.canBeReplaced(state, context);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE_BY_PROPERTIES.apply(state.getValue(FACING), state.getValue(AMOUNT));
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());

        if (existing.is(this)) {
            return existing.setValue(AMOUNT, Math.min(4, existing.getValue(AMOUNT) + 1));
        }

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, @NotNull BlockState> builder) {
        builder.add(FACING, AMOUNT);
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random,
                                     @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random,
                                @NotNull BlockPos pos, @NotNull BlockState state) {
        int amount = state.getValue(AMOUNT);

        if (amount < 4) {
            level.setBlock(pos, state.setValue(AMOUNT, amount + 1), 2);
        } else {
            popResource(level, pos, new ItemStack(this));
        }
    }

    @Override
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state,
                                                   @NotNull Level level, @NotNull BlockPos pos,
                                                   @NotNull Player player, @NotNull InteractionHand hand,
                                                   @NotNull BlockHitResult hitResult) {
        if (player.getItemInHand(hand).isEmpty() && hand == InteractionHand.MAIN_HAND) {
            level.addFreshEntity(new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    new ItemStack(ModItems.PINE_CONE.get(), state.getValue(AMOUNT))
            ));

            level.addFreshEntity(new ItemEntity(
                    level,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    new ItemStack(ModItems.TWIG.get(), level.random.nextInt(2 * state.getValue(AMOUNT)))
            ));

            level.destroyBlock(pos, false);
            level.playSound(null, pos, SoundEvents.MOSS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.BLOCK_MINED.get(this));
            }

            return InteractionResult.SUCCESS.withoutItem();
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                            @NotNull RandomSource random) {
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

                        double angle = random.nextDouble() * Math.PI * 2.0;
                        double speed = 0.025 + random.nextDouble() * 0.015;
                        double vx = Math.cos(angle) * speed;
                        double vz = Math.sin(angle) * speed;
                        double vy = 0.005 + random.nextDouble() * 0.01;

                        level.addParticle(ModParticles.PINE_LEAF_FLUFF_PARTICLES.get(), x, y, z, vx, vy, vz);
                    }

                    if (random.nextFloat() < 0.2F) {
                        double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
                        double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
                        double y = pos.getY() + 0.05 + random.nextDouble() * 0.1;

                        double angle = random.nextDouble() * Math.PI * 2.0;
                        double speed = 0.012 + random.nextDouble() * 0.008;
                        double vx = Math.cos(angle) * speed;
                        double vz = Math.sin(angle) * speed;
                        double vy = 0.025 + random.nextDouble() * 0.01;

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