package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.registry.ModBiomes;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.worldgen.CalciteOreVariants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.SpeleothemBlock;
import net.minecraft.world.level.block.state.properties.SpeleothemThickness;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

import java.util.ArrayList;
import java.util.HashSet;

/** Dresses the biome's noise caves and carver tunnels, without carving additional chambers. */
public final class SaltCaveSurfaceFeature extends Feature<net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration> {
    public SaltCaveSurfaceFeature() { super(net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.CODEC); }

    @Override
    public boolean place(net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration> context) {
        var level = context.level();
        var generator = context.chunkGenerator();
        var random = context.random();
        var origin = context.origin();
        int startX = origin.getX() & ~15, startZ = origin.getZ() & ~15;
        var noise = new SimplexNoise(RandomSource.create(level.getSeed()));
        int phase = (int) Math.floorMod(level.getSeed(), 9);
        var surfaces = new ArrayList<BlockPos>();
        var airFaces = new HashSet<BlockPos>();
        // Collect first, then replace, so the palette does not depend on iteration order.
        for (int x = startX; x < startX + 16; x++) {
            for (int z = startZ; z < startZ + 16; z++) {
                int top = Math.min(level.getMaxY() - 12, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 10);
                for (int y = level.getMinY() + 12; y < top; y++) {
                    var pos = new BlockPos(x, y, z);
                    var state = level.getBlockState(pos);
                    if (!rock(state) && CalciteOreVariants.replacement(state) == null) continue;
                    boolean exposed = false;
                    for (var direction : Direction.values()) {
                        var neighbor = pos.relative(direction);
                        var neighborState = level.getBlockState(neighbor);
                        if (!neighborState.isAir() && neighborState.getFluidState().isEmpty()) continue;
                        if (!exposed && !level.getBiome(pos).is(ModBiomes.SALT_CAVES)) break;
                        exposed = true;
                        airFaces.add(neighbor);
                    }
                    if (exposed) surfaces.add(pos);
                }
            }
        }
        for (var surface : surfaces) {
            for (var direction : Direction.values()) {
                var pos = surface.relative(direction);
                if (level.ensureCanWrite(pos)) lineSurface(level, noise, phase, pos);
            }
            lineSurface(level, noise, phase, surface);
        }
        var floors = new ArrayList<BlockPos>();
        for (var pos : airFaces) {
            if (pos.getX() < startX || pos.getX() >= startX + 16 || pos.getZ() < startZ || pos.getZ() >= startZ + 16) continue;
            if (level.getBlockState(pos).isAir() && saltStone(level.getBlockState(pos.below()))) floors.add(pos);
        }
        // Sort collections before consuming randomness for repeatable generation across JVM sessions.
        floors.sort(BlockPos::compareTo);
        if (!floors.isEmpty()) {
            var candidates = new ArrayList<>(floors);
            for (int i = candidates.size() - 1; i > 0; i--)
                java.util.Collections.swap(candidates, i, random.nextInt(i + 1));
            int pools = 0;
            for (int attempt = 0; attempt < Math.min(128, candidates.size()) && pools < 2; attempt++)
                if (basin(level, candidates.get(attempt).below(), 3)
                        || basin(level, candidates.get(attempt).below(), 2)) pools++;
        }
        for (var bottom : floors) {
            if (!level.getBlockState(bottom).isAir() || !saltStone(level.getBlockState(bottom.below()))) continue;
            int gap = 0;
            while (gap < 40 && level.getBlockState(bottom.above(gap)).isAir()) gap++;
            if (gap < 3 || gap >= 40) continue;
            var ceiling = bottom.above(gap);
            if (saltStone(level.getBlockState(ceiling)) && random.nextInt(7) == 0) {
                int length = 1 + random.nextInt(Math.min(6, gap - 2));
                level.setBlock(ceiling, ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState(), 2);
                reservoir(level, ceiling);
                spike(level, ceiling.below(), Direction.DOWN, length);
                if (random.nextBoolean()) spike(level, bottom, Direction.UP,
                        1 + random.nextInt(Math.min(4, gap - length - 1)));
            } else if (random.nextInt(14) == 0) {
                spike(level, bottom, Direction.UP, 1 + random.nextInt(Math.min(4, gap - 1)));
            } else if (random.nextInt(22) == 0) {
                level.setBlock(bottom, ModBlocks.SALT_DEPOSIT.get().defaultBlockState()
                        .setValue(SaltDepositBlock.NATURAL, true)
                        .setValue(SaltDepositBlock.LAYERS, 1 + random.nextInt(2)), 2);
            }
        }
        var sortedAir = new ArrayList<>(airFaces);
        sortedAir.sort(BlockPos::compareTo);
        for (var pos : sortedAir) {
            if (random.nextInt(40) != 0 || !level.ensureCanWrite(pos) || !level.getBlockState(pos).isAir()) continue;
            for (var direction : Direction.values()) {
                if (!saltStone(level.getBlockState(pos.relative(direction)))) continue;
                var lichen = Blocks.GLOW_LICHEN.defaultBlockState().setValue(MultifaceBlock.getFaceProperty(direction), true);
                if (lichen.canSurvive(level, pos)) level.setBlock(pos, lichen, 2);
                break;
            }
        }
        for (var pos : floors) GlasswortPatch.tryPlace(level, random, pos);
        return !surfaces.isEmpty();
    }

    private static void lineSurface(WorldGenLevel level, SimplexNoise noise, int phase, BlockPos pos) {
        var state = level.getBlockState(pos);
        var ore = CalciteOreVariants.replacement(state);
        if (ore != null) level.setBlock(pos, ore, 2);
        else if (rock(state)) level.setBlock(pos, palette(noise, phase, pos.getX(), pos.getY(), pos.getZ()), 2);
    }

    private static boolean rock(BlockState state) {
        return state.getFluidState().isEmpty() && (state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(Blocks.DIRT) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY));
    }

    private static boolean basin(WorldGenLevel level, BlockPos floor, int radius) {
        int outer = radius == 3 ? 10 : 5, inner = radius == 3 ? 4 : 1;
        // A two-block-deep pool with an intact stone rim, never a conversion of an aquifer.
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > outer) continue;
                for (int y = -2; y <= 0; y++) {
                    var pos = floor.offset(x, y, z);
                    var state = level.getBlockState(pos);
                    if (!level.ensureCanWrite(pos) || (!rock(state) && !saltStone(state)
                            && !(y > -2 && state.isAir()))) return false;
                }
                if (x * x + z * z <= inner && !level.getBlockState(floor.offset(x, 1, z)).isAir()) return false;
            }
        }
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > outer) continue;
                for (int y = -2; y <= 0; y++) {
                    level.setBlock(floor.offset(x, y, z), x * x + z * z <= inner && y > -2
                            ? ModBlocks.SALT_WATER.get().defaultBlockState()
                            : ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState(), 2);
                }
            }
        }
        return true;
    }
    private static BlockState palette(SimplexNoise noise, int phase, int x, int y, int z) {
        double vein = noise.getValue(x * .09, z * .09) * 2;
        int band = Math.floorMod((int) Math.floor(y + x * .12 + vein) + phase, 9);
        if (band == 0) return Blocks.CALCITE.defaultBlockState();
        if (band == 1 && noise.getValue(x * .18, y * .18, z * .18) > .2)
            return Blocks.DIORITE.defaultBlockState();
        return band >= 6 ? ModBlocks.SMOOTH_SALT.get().defaultBlockState()
                : ModBlocks.CRYSTALLINE_SALT.get().defaultBlockState();
    }

    private static boolean saltStone(BlockState state) {
        return state.is(ModBlocks.CRYSTALLINE_SALT.get()) || state.is(ModBlocks.SMOOTH_SALT.get())
                || state.is(Blocks.CALCITE) || state.is(Blocks.DIORITE);
    }

    private static void reservoir(WorldGenLevel level, BlockPos support) {
        var source = support.above();
        // Only replace a fully enclosed natural block; no leaking brine above an adjoining cavern.
        if (!saltStone(level.getBlockState(source))) return;
        for (var direction : Direction.values()) {
            var neighbor = source.relative(direction);
            if (!level.ensureCanWrite(neighbor) || !level.getBlockState(neighbor)
                    .isFaceSturdy(level, neighbor, direction.getOpposite())) return;
        }
        level.setBlock(source, ModBlocks.SALT_WATER.get().defaultBlockState(), 2);
    }

    private static void spike(WorldGenLevel level, BlockPos root, Direction direction, int length) {
        for (int i = 0; i < length; i++) {
            var thickness = i == length - 1 ? SpeleothemThickness.TIP
                    : i == length - 2 ? SpeleothemThickness.FRUSTUM
                    : i == 0 ? SpeleothemThickness.BASE : SpeleothemThickness.MIDDLE;
            level.setBlock(root.relative(direction, i), ModBlocks.SALT_SPIKE.get().defaultBlockState()
                    .setValue(SpeleothemBlock.TIP_DIRECTION, direction)
                    .setValue(SpeleothemBlock.THICKNESS, thickness), 2);
        }
    }

}
