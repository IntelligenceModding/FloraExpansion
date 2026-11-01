package de.artemis.floraexpansion.common.worldgen.feature;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

/** Broad, stratified evaporite lenses, independent of modern surface water. */
public class SaltFormationFeature implements Feature {
    public static final MapCodec<SaltFormationFeature> CODEC = MapCodec.unit(SaltFormationFeature::new);

    @Override public MapCodec<SaltFormationFeature> codec() { return CODEC; }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        int radius = 8 + random.nextInt(5);
        int halfHeight = 4 + random.nextInt(3);
        if (origin.getY() + halfHeight + 4 >= level.getHeight(
                Heightmap.Types.OCEAN_FLOOR_WG, origin.getX(), origin.getZ())) return false;
        double angle = random.nextDouble() * Math.PI * 2;
        double slopeX = Math.cos(angle) * 0.15;
        double slopeZ = Math.sin(angle) * 0.15;
        int saltThickness = 3 + random.nextInt(3);
        int period = saltThickness + 3;
        int phase = random.nextInt(period);
        var noise = new SimplexNoise(random);
        boolean placed = false;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                double horizontal = (dx * dx + dz * dz) / (double) (radius * radius);
                if (horizontal > 1) continue;
                int surface = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG,
                        origin.getX() + dx, origin.getZ() + dz);
                double bend = noise.get(dx * 0.12, dz * 0.12) * 0.6;
                double tilt = dx * slopeX + dz * slopeZ + bend;
                for (int dy = -halfHeight - 3; dy <= halfHeight + 3; dy++) {
                    double vertical = (dy - tilt) / halfHeight;
                    double edge = 0.9 + noise.get(dx * 0.2, dy * 0.2, dz * 0.2) * 0.05;
                    if (horizontal + vertical * vertical > edge) continue;
                    var pos = origin.offset(dx, dy, dz);
                    if (level.isOutsideBuildHeight(pos.getY()) || pos.getY() >= surface - 4
                            || !level.ensureCanWrite(pos)
                            || !level.getBlockState(pos).is(BlockTags.BASE_STONE_OVERWORLD)) continue;
                    int band = Math.floorMod((int) Math.floor(dy - tilt) + phase, period);
                    if (band >= saltThickness + 1) continue; // Leave native stone between beds.
                    var state = band == saltThickness ? Blocks.CALCITE.defaultBlockState()
                            : ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState();
                    level.setBlock(pos, state, 2);
                    placed = true;
                }
            }
        }
        return placed;
    }
}