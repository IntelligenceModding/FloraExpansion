package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModParticles;
import de.artemis.floraexpansion.common.registry.ModBlockStateProperties;
import de.artemis.floraexpansion.common.util.ModUtils;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

public class PineLitterBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<PineLitterBlock> CODEC = simpleCodec(PineLitterBlock::new);
    public static final int MIN_SEGMENTS = 1;
    public static final int MAX_SEGMENTS = 4;
    public static final EnumProperty<Direction> FACING;
    public static final IntegerProperty AMOUNT;
    private static final BiFunction<Direction, Integer, VoxelShape> SHAPE_BY_PROPERTIES;

    @SuppressWarnings("unchecked")
    public @NotNull MapCodec<BushBlock> codec() {
        return (MapCodec<BushBlock>) (MapCodec<?>) CODEC;
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
    protected @NotNull InteractionResult useItemOn(@NotNull ItemStack itemStack, @NotNull BlockState blockState, @NotNull Level level, BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (player.getItemInHand(interactionHand).isEmpty() && interactionHand == InteractionHand.MAIN_HAND) {
            harvestLitter(level, blockPos, player, blockState);
            return ((level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER));
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
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
                    spawnFootstepParticles(level, pos, random);
                    spawnExtraFootstepParticles(level, pos, random);
                    playFootstepSound(level, pos, random);
                }
            }
        }
    }

    protected List<ItemStack> getHarvestDrops(Level level, BlockState state) {
        return List.of(
                new ItemStack(ModItems.PINE_CONE.get(), state.getValue(AMOUNT)),
                new ItemStack(ModItems.TWIG.get(), level.random.nextInt(2 * state.getValue(AMOUNT)))
        );
    }

    protected void harvestLitter(Level level, BlockPos pos, Player player, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        for (ItemStack drop : getHarvestDrops(level, state)) {
            ModUtils.spawnCenteredItem(level, pos, drop);
        }

        level.destroyBlock(pos, false);
        level.playSound(null, pos, SoundEvents.MOSS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        ModUtils.awardBlockMinedStat(player, this);
    }

    protected ParticleOptions getFootstepFluffParticle() {
        return ModParticles.PINE_LEAF_FLUFF_PARTICLES.get();
    }

    protected SoundEvent getFootstepSound() {
        return SoundEvents.HANGING_ROOTS_STEP;
    }

    protected void spawnExtraFootstepParticles(Level level, BlockPos pos, RandomSource random) {
        if (random.nextFloat() >= 0.2F) {
            return;
        }

        double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
        double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
        double y = pos.getY() + 0.05 + random.nextDouble() * 0.1;
        double angle = random.nextDouble() * Math.PI * 2;
        double speed = 0.012 + random.nextDouble() * 0.008;
        double vx = Math.cos(angle) * speed;
        double vz = Math.sin(angle) * speed;
        double vy = 0.025 + random.nextDouble() * 0.01;

        level.addParticle(ModParticles.PINE_PARTICLES.get(), x, y, z, vx, vy, vz);
    }

    private void spawnFootstepParticles(Level level, BlockPos pos, RandomSource random) {
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

            level.addParticle(getFootstepFluffParticle(), x, y, z, vx, vy, vz);
        }
    }

    private void playFootstepSound(Level level, BlockPos pos, RandomSource random) {
        level.playLocalSound(
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                getFootstepSound(),
                SoundSource.BLOCKS,
                0.8F + random.nextFloat() * 0.2F,
                0.9F + random.nextFloat() * 0.3F,
                false
        );
    }
}

