package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.LinkedHashMap;
import java.util.Map;

/** Surface end of a vanilla root system: a shallow, salt-crusted brine spring. */
public final class BrineSpringFeature extends Feature<net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration> {
    public BrineSpringFeature() { super(net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration> context) {
        var level = context.level();
        var generator = context.chunkGenerator();
        var random = context.random();
        var origin = context.origin();
        // The root system can pass through several caves. Only its open-air destination gets a spring.
        if (origin.getY() != surfaceHeight(level, origin.getX(), origin.getZ())
                || !clearable(level.getBlockState(origin))) return false;
        if (hasNearbySurfaceSpring(level, origin)) return false;
        int radiusX = 3 + random.nextInt(2), radiusZ = 2 + random.nextInt(2);
        var noise = new SimplexNoise(random);
        Map<BlockPos, BlockState> plan = new LinkedHashMap<>();
        for (int x = -radiusX - 1; x <= radiusX + 1; x++) {
            for (int z = -radiusZ - 1; z <= radiusZ + 1; z++) {
                double shape = x * x / (double) (radiusX * radiusX) + z * z / (double) (radiusZ * radiusZ)
                        + noise.getValue(x * .35, z * .35) * .1;
                // Leave a full bank around even the narrowest noisy basin.
                if (shape > 1.5) continue;
                var column = origin.offset(x, 0, z);
                int surface = surfaceHeight(level, column.getX(), column.getZ());
                if (Math.abs(surface - origin.getY()) > 2) return false;
                if (shape > .95) {
                    var ground = new BlockPos(column.getX(), surface - 1, column.getZ());
                    int bankTop = Math.max(surface - 1, origin.getY() - 1);
                    var crust = new BlockPos(column.getX(), bankTop + 1, column.getZ());
                    if (!ground(level.getBlockState(ground)) || !clearable(level.getBlockState(crust))) return false;
                    // Raise the downhill bank to the waterline, rather than rejecting
                    // an otherwise sound basin because its salt crust cannot hold water.
                    for (int y = ground.getY(); y <= bankTop; y++) {
                        var bank = new BlockPos(column.getX(), y, column.getZ());
                        if (y > ground.getY() && !clearable(level.getBlockState(bank))) return false;
                        plan.put(bank, ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
                    }
                    plan.put(crust, ModBlocks.SALT_DEPOSIT.get().defaultBlockState()
                            .setValue(SaltDepositBlock.NATURAL, true)
                            .setValue(SaltDepositBlock.LAYERS, 1 + random.nextInt(2)));
                    continue;
                }
                // Require an intact natural bed. Validate before placing anything, including on slopes.
                for (int y = -4; y <= -1; y++) {
                    var pos = column.offset(0, y, 0);
                    var old = level.getBlockState(pos);
                    if (!ground(old) && !(pos.getY() >= surface && old.isAir())) return false;
                    boolean water = shape < .5 && (y == -1 || (y == -2 && shape < .22));
                    plan.put(pos, water ? ModBlocks.SALT_WATER.get().defaultBlockState()
                            : y < -2 || shape < .5 ? ModBlocks.SMOOTH_SALT.get().defaultBlockState()
                            : ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState());
                }
                for (int y = origin.getY(); y <= Math.max(origin.getY(), surface); y++) {
                    var pos = new BlockPos(column.getX(), y, column.getZ());
                    var state = level.getBlockState(pos);
                    if (!state.getFluidState().isEmpty() || (!ground(state) && !clearable(state))) return false;
                    plan.put(pos, Blocks.AIR.defaultBlockState());
                }
                if (shape >= .5 && random.nextInt(3) == 0)
                    plan.put(column, ModBlocks.SALT_DEPOSIT.get().defaultBlockState().setValue(SaltDepositBlock.NATURAL, true));
            }
        }
        for (var pos : plan.keySet()) if (!level.ensureCanWrite(pos) || level.isOutsideBuildHeight(pos.getY())) return false;
        for (var entry : plan.entrySet()) {
            if (!entry.getValue().is(ModBlocks.SALT_WATER.get())) continue;
            for (var direction : net.minecraft.core.Direction.values()) {
                if (direction == net.minecraft.core.Direction.UP) continue;
                var neighbor = entry.getKey().relative(direction);
                var state = plan.getOrDefault(neighbor, level.getBlockState(neighbor));
                if (!state.is(ModBlocks.SALT_WATER.get())
                        && !state.isFaceSturdy(level, neighbor, direction.getOpposite())) return false;
            }
        }
        plan.forEach((pos, state) -> level.setBlock(pos, state, 2));
        for (var pos : plan.keySet()) GlasswortPatch.tryPlace(level, random, pos.above());
        return true;
    }

    private static boolean hasNearbySurfaceSpring(WorldGenLevel level, BlockPos origin) {
        final int separation = 32;
        // A pool has a contiguous surface at least three blocks across. Sampling every
        // three blocks finds it without visiting every column of the surrounding area.
        for (int dx = -separation; dx <= separation; dx += 3) {
            for (int dz = -separation; dz <= separation; dz += 3) {
                if (dx * dx + dz * dz > separation * separation) continue;
                int x = origin.getX() + dx, z = origin.getZ() + dz;
                // Only inspect the region already available to decoration; never generate chunks here.
                if (!level.hasChunk(x >> 4, z >> 4)) continue;
                if (level instanceof net.minecraft.server.level.WorldGenRegion region
                        && !region.isWithinWriteZone(new BlockPos(x, origin.getY(), z))) continue;
                var top = new BlockPos(x, surfaceHeight(level, x, z) - 1, z);
                if (level.getBlockState(top).is(ModBlocks.SALT_WATER.get())) return true;
            }
        }
        return false;
    }

    private static int surfaceHeight(WorldGenLevel level, int x, int z) {
        // Use the heightmap maintained during decoration, then ignore replaceable surface foliage.
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        for (int i = 0; i < 16 && y > level.getMinY(); i++) {
            if (!clearable(level.getBlockState(new BlockPos(x, y - 1, z)))) break;
            y--;
        }
        return y;
    }

    private static boolean clearable(BlockState state) {
        return state.getFluidState().isEmpty() && (state.isAir() || state.canBeReplaced() || state.is(BlockTags.REPLACEABLE_BY_TREES));
    }

    private static boolean ground(BlockState state) {
        return state.getFluidState().isEmpty() && (state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.DIRT) || state.is(BlockTags.SAND)
                || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.PODZOL) || state.is(Blocks.MYCELIUM)
                || state.is(Blocks.MOSS_BLOCK) || state.is(Blocks.MUD)
                || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) || state.is(Blocks.CALCITE)
                || state.is(ModBlocks.CRYSTALLINE_SALT.get()) || state.is(ModBlocks.SMOOTH_SALT.get()));
    }
}
