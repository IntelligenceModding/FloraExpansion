package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.worldgen.feature.GiantCactusGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.registry.ModBlocks;

public class GiantCactusBlossomBlock extends BushBlock implements BonemealableBlock {
    public static final int VARIANT_COUNT = 3;
    public static final MapCodec<BushBlock> CODEC = simpleCodec(GiantCactusBlossomBlock::new);
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, VARIANT_COUNT - 1);
    private static final VoxelShape SHAPE = Block.column(14.0D, 0.0D, 12.0D);
    private static final float BONEMEAL_GROW_CHANCE = 0.45F;

    public GiantCactusBlossomBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, 0));
    }

    @Override
    public @NotNull MapCodec<BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.getBlock() instanceof CactusBlock
                || state.getBlock() instanceof FarmlandBlock
                || state.is(ModBlocks.GIANT_CACTUS_STEM.get())
                || state.isFaceSturdy(level, pos, Direction.UP, SupportType.CENTER);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return withVariantFromPosition(this.defaultBlockState(), context.getClickedPos());
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockState below = level.getBlockState(pos.below());

        if (!isValidGiantCactusGrowthGround(below)) {
            return false;
        }

        return random.nextFloat() < BONEMEAL_GROW_CHANCE;
    }

    @Override
    public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource random, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockState below = level.getBlockState(pos.below());

        if (!isValidGiantCactusGrowthGround(below)) {
            return;
        }

        long seed = level.getSeed() ^ pos.asLong();

        RandomSource validationRandom = RandomSource.create(seed);
        if (!GiantCactusGenerator.canGenerate(level, pos, validationRandom)) {
            return;
        }

        level.removeBlock(pos, false);

        RandomSource generationRandom = RandomSource.create(seed);
        GiantCactusGenerator.generate(level, pos, generationRandom, false);
    }

    private boolean isValidGiantCactusGrowthGround(BlockState below) {
        return below.is(BlockTags.SAND)
                || below.is(Blocks.RED_SAND)
                || below.is(Blocks.TERRACOTTA)
                || below.is(Blocks.DYED_TERRACOTTA.white())
                || below.is(Blocks.DYED_TERRACOTTA.orange())
                || below.is(Blocks.DYED_TERRACOTTA.yellow());
    }

    @Override
    protected boolean isRandomlyTicking(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        BlockState below = level.getBlockState(pos.below());

        if (!isValidGiantCactusGrowthGround(below)) {
            return;
        }

        if (random.nextFloat() >= 0.08F) {
            return;
        }

        long seed = level.getSeed() ^ pos.asLong();

        RandomSource validationRandom = RandomSource.create(seed);
        if (!GiantCactusGenerator.canGenerate(level, pos, validationRandom)) {
            return;
        }

        level.removeBlock(pos, false);

        RandomSource generationRandom = RandomSource.create(seed);
        GiantCactusGenerator.generate(level, pos, generationRandom, false);
    }

    public static @NotNull BlockState withRandomVariant(@NotNull BlockState state, @NotNull RandomSource random) {
        return state.setValue(VARIANT, random.nextInt(VARIANT_COUNT));
    }

    public static @NotNull BlockState withVariantFromPosition(@NotNull BlockState state, @NotNull BlockPos pos) {
        return state.setValue(VARIANT, Math.floorMod((int) Mth.getSeed(pos), VARIANT_COUNT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, @NotNull BlockState> builder) {
        builder.add(VARIANT);
    }
}

