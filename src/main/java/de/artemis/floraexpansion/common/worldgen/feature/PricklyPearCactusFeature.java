package de.artemis.floraexpansion.common.worldgen.feature;

import de.artemis.floraexpansion.common.util.PlantSoils;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;

public class PricklyPearCactusFeature extends Feature<net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration> {
    public PricklyPearCactusFeature() { super(net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration.CODEC); }


    @Override
    public boolean place(net.minecraft.world.level.levelgen.feature.FeaturePlaceContext<net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration> context) {
        var level = context.level();
        var random = context.random();
        var featureOrigin = context.origin();
        BlockPos origin = level.getHeightmapPos((level instanceof net.minecraft.server.level.WorldGenRegion ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.WORLD_SURFACE), featureOrigin);

        int targetCount = random.nextFloat() < 0.72F ? 1 : 2;
        int placed = 0;

        for (int i = 0; i < 6 && placed < targetCount; i++) {
            int dx = random.nextInt(5) - 2;
            int dz = random.nextInt(5) - 2;

            BlockPos sample = origin.offset(dx, 0, dz);
            BlockPos pos = level.getHeightmapPos((level instanceof net.minecraft.server.level.WorldGenRegion ? Heightmap.Types.WORLD_SURFACE_WG : Heightmap.Types.WORLD_SURFACE), sample);

            if (!PlantSoils.canPlaceCactus(level, pos)) {
                continue;
            }

            BlockState state = ModBlocks.PRICKLY_PEAR_CACTUS.get().defaultBlockState();
            if (!state.canSurvive(level, pos)) {
                continue;
            }

            level.setBlock(pos, state, 2);
            placed++;
        }

        return placed > 0;
    }

}
