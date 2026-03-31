package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.worldgen.feature.GiantCactusGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CactusFlowerBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<BushBlock> CODEC = simpleCodec(CactusFlowerBlock::new);
    private static final VoxelShape SHAPE = box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
    private static final float BONEMEAL_GROW_CHANCE = 0.45F;

    public CactusFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull MapCodec<BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.getBlock() instanceof CactusBlock
                || state.getBlock() instanceof FarmBlock
                || state.is(ModBlocks.GIANT_CACTUS_STEM.get())
                || state.isFaceSturdy(level, pos, Direction.UP, SupportType.CENTER);
    }

    @Override
    protected boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        return mayPlaceOn(level.getBlockState(pos.below()), level, pos.below());
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
                || below.is(Blocks.WHITE_TERRACOTTA)
                || below.is(Blocks.ORANGE_TERRACOTTA)
                || below.is(Blocks.YELLOW_TERRACOTTA);
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
}