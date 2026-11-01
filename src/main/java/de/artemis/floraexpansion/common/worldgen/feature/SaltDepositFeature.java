package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public class SaltDepositFeature implements Feature {
    public static final MapCodec<SaltDepositFeature> CODEC = MapCodec.unit(SaltDepositFeature::new);

    @Override public MapCodec<SaltDepositFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        boolean ocean = level.getBiome(origin).is(BiomeTags.IS_OCEAN);
        boolean placed = ocean && placeOceanDeposit(level, random, origin);
        for (int attempt = 0; attempt < 8; attempt++) {
            var sample = attempt == 0 ? origin : origin.offset(random.nextInt(9) - 4, 0, random.nextInt(9) - 4);
            var pos = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, sample).below();
            var ground = level.getBlockState(pos);
            if (!level.getFluidState(pos.above()).is(FluidTags.WATER)) continue;
            if (!ocean && isSediment(ground)) {
                level.setBlock(pos, ModBlocks.SALT_DEPOSIT.get().defaultBlockState().setValue(SaltDepositBlock.LAYERS, 4).setValue(SaltDepositBlock.NATURAL, true), 2);
                placed = true;
            }
        }
        return placed;
    }

    private static boolean placeOceanDeposit(WorldGenLevel level, RandomSource random, BlockPos origin) {
        // Three size classes, with small deposits more common than large ones.
        int roll = random.nextInt(6);
        int size = roll < 3 ? 0 : roll < 5 ? 1 : 2;
        double length = 3.0 + size * 2.0;
        double width = 2.0 + size;
        double angle = random.nextDouble() * Math.PI * 2.0;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        int radius = (int) Math.ceil(length + width);
        boolean placed = false;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double u = (dx * cos + dz * sin) / length;
                // Bend the ellipse into a kidney/bean outline, with a concave inner edge.
                double v = (-dx * sin + dz * cos) / width - 0.9 * u * u + 0.3;
                double distance = u * u + v * v;
                if (distance > 1.0) continue;
                var bed = level.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, origin.offset(dx, 0, dz)).below();
                if (!level.getBiome(bed).is(BiomeTags.IS_OCEAN)
                        || !level.getFluidState(bed.above()).is(FluidTags.WATER)) continue;
                int thickness = 1 + (int) ((size + 1) * (1.0 - distance));
                for (int depth = 0; depth < thickness; depth++) {
                    var pos = bed.below(depth);
                    if (!isSediment(level.getBlockState(pos))) break;
                    level.setBlock(pos, ModBlocks.SALT_DEPOSIT.get().defaultBlockState().setValue(SaltDepositBlock.LAYERS, 4).setValue(SaltDepositBlock.NATURAL, true), 2);
                    placed = true;
                }
            }
        }
        return placed;
    }

    private static boolean isSediment(net.minecraft.world.level.block.state.BlockState state) {
        return state.is(BlockTags.SAND) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) || state.is(Blocks.DIRT);
    }
}
