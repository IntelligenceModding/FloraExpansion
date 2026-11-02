package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.artemis.floraexpansion.common.block.ScatteredPebblesBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

/** Loose shoreline stones and occasional rocky scree beds; never clears plants or drains water. */
public final class PebbleClusterFeature extends Feature<PebbleClusterFeature.Config> {
    public PebbleClusterFeature() {
        super(Config.CODEC);
    }

    @Override public boolean place(net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<PebbleClusterFeature.Config> context) {
        var level = context.level();
        var generator = context.chunkGenerator();
        var random = context.random();
        var origin = context.origin();
        int radius = context.config().minRadius() + random.nextInt(context.config().maxRadius() - context.config().minRadius() + 1);
        var noise = new SimplexNoise(RandomSource.create(level.getSeed() ^ 0x504542424c45L));
        boolean placed = false;
        int originY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ());
        for (int x = -radius - 1; x <= radius + 1; x++) for (int z = -radius - 1; z <= radius + 1; z++) {
            int wx = origin.getX() + x, wz = origin.getZ() + z;
            double edge = (x * x + z * z * .7) / (double)(radius * radius)
                    + noise.getValue(wx * .4, wz * .4) * .65;
            if (edge > 1.05 || random.nextFloat() > .75 - Math.max(0, edge) * .3) continue;
            var pos = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, new BlockPos(wx, origin.getY(), wz));
            var ground = pos.below();
            if (Math.abs(pos.getY() - originY) > 4 || !level.ensureCanWrite(pos) || !level.ensureCanWrite(ground)
                    || !level.getBiome(pos).is(ModTags.Biomes.HAS_PEBBLE_CLUSTERS)
                    || !level.getBlockState(ground).is(ModTags.Blocks.PEBBLE_GROUND)) continue;
            var old = level.getBlockState(pos);
            var fluid = old.getFluidState();
            boolean wet = fluid.is(FluidTags.WATER) || fluid.is(ModTags.Fluids.SALT_WATER);
            if (wet && !fluid.isSource()) continue;
            if (!old.isAir() && !(wet && old.getBlock() instanceof net.minecraft.world.level.block.LiquidBlock)) continue;
            if (wet && level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, wx, wz) - pos.getY() > 2) continue;
            var biome = level.getBiome(pos);
            boolean rocky = biome.is(Biomes.WINDSWEPT_HILLS) || biome.is(Biomes.WINDSWEPT_GRAVELLY_HILLS)
                    || biome.is(Biomes.WINDSWEPT_FOREST) || biome.is(Biomes.STONY_PEAKS) || biome.is(Biomes.STONY_SHORE);
            if (!rocky && !wet && !nearWater(level, ground)) continue;
            var state = ModBlocks.SCATTERED_PEBBLES.get().defaultBlockState()
                    .setValue(ScatteredPebblesBlock.AMOUNT, edge < .35 ? 3 + random.nextInt(3) : 1 + random.nextInt(3))
                    .setValue(ScatteredPebblesBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(random))
                    .setValue(ScatteredPebblesBlock.WATERLOGGED, wet)
                    .setValue(ScatteredPebblesBlock.BRINE, fluid.is(ModTags.Fluids.SALT_WATER));
            if (!state.canSurvive(level, pos)) continue;
            if (rocky && !wet && edge < .45 && random.nextInt(3) == 0) {
                level.setBlock(ground, Blocks.GRAVEL.defaultBlockState(), 2);
            }
            level.setBlock(pos, state, 2);
            placed = true;
        }
        return placed;
    }

    private static boolean nearWater(WorldGenLevel level, BlockPos ground) {
        for (int x = -3; x <= 3; x++) for (int z = -3; z <= 3; z++) {
            if (Math.abs(x) + Math.abs(z) > 3) continue;
            for (int y = -1; y <= 1; y++) {
                var pos = ground.offset(x, y, z);
                if (!level.ensureCanWrite(pos)) continue;
                var fluid = level.getFluidState(pos);
                if (fluid.is(FluidTags.WATER) || fluid.is(ModTags.Fluids.SALT_WATER)) return true;
            }
        }
        return false;
    }
    public record Config(int minRadius, int maxRadius) implements net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration {
        public static final com.mojang.serialization.Codec<Config> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.<Config>create(i -> i.group(
            com.mojang.serialization.Codec.intRange(1, 8).optionalFieldOf("min_radius", 1).forGetter(Config::minRadius),
            com.mojang.serialization.Codec.intRange(1, 8).optionalFieldOf("max_radius", 3).forGetter(Config::maxRadius)
        ).apply(i, Config::new)).validate(c -> c.maxRadius >= c.minRadius ? com.mojang.serialization.DataResult.success(c) : com.mojang.serialization.DataResult.error(() -> "max_radius must be at least min_radius"));
    }

}
