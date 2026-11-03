package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBiomes;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModConfiguredFeatures;
import de.artemis.floraexpansion.common.registry.ModFluids;
import de.artemis.floraexpansion.common.registry.ModPlacedFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.RootSystemFeature;

final class BrineSpringTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var features = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
        var placements = level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);
        var spring = features.getValueOrThrow(ModConfiguredFeatures.BRINE_SPRING_KEY);
        var root = features.getValueOrThrow(ModConfiguredFeatures.ROOTED_BRINE_SPRING_KEY);
        verifySeparation(helper, spring);
        var placed = placements.getValueOrThrow(ModPlacedFeatures.ROOTED_BRINE_SPRING_PLACED_KEY);
        check(((net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration)root.config()).rootColumnMaxHeight >= 384
                && ((net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration)root.config()).requiredVerticalSpaceForTree == 3, "Brine still has restrictive height/plateau checks");
        var biomes = level.registryAccess().lookupOrThrow(Registries.BIOME);
        check(biomes.getValueOrThrow(ModBiomes.SALT_CAVES).getGenerationSettings().hasFeature(placed), "Salt Caves has no rooted spring feature");
        check(!biomes.getValueOrThrow(Biomes.PLAINS).getGenerationSettings().hasFeature(placed), "Brine springs added to an unrelated biome");

        var surface = helper.absolutePos(new BlockPos(2048, 150, 1024));
        var blocked = surface.east(48);
        prepare(level, surface);
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                if (x * x + z * z > 4) level.setBlock(surface.offset(x, 0, z), Blocks.SHORT_GRASS.defaultBlockState(), 2);
            }
        }
        var cave = surface.below(30);
        check(!spring.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), cave), "Surface marker generated in an internal cave");
        root.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), cave);
        int brine = brine(level, surface), crust = 0, trail = 0;
        for (var pos : BlockPos.betweenClosed(surface.offset(-7, -2, -7), surface.offset(7, 2, 7)))
            if (level.getBlockState(pos).is(ModBlocks.SALT_DEPOSIT.get())) crust++;
        for (var pos : BlockPos.betweenClosed(cave.offset(-3, 1, -3), surface.offset(3, -5, 3)))
            if (level.getBlockState(pos).is(ModBlocks.CRYSTALLINE_SALT.get())) trail++;
        check(brine > 5 && crust > 5 && trail > 50, "Missing surface brine, salt crust, or mineral trail from the cave");
        check(level.getBlockState(cave).is(ModBlocks.SALT_SPIKE.get()), "Missing native hanging-root Salt Spike");
        check(level.getBlockState(cave).canSurvive(level, cave), "Unsupported spring ceiling spike");

        prepare(level, blocked);
        level.setBlock(blocked.below(2), Blocks.CHEST.defaultBlockState(), 2);
        check(!spring.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), blocked), "Spring erased a protected block");
        check(level.getBlockState(blocked.below(2)).is(Blocks.CHEST)
                && level.getBlockState(blocked.below()).is(Blocks.GRASS_BLOCK), "Rejected spring partially changed the terrain");
        level.setBlock(blocked.below(2), Blocks.STONE.defaultBlockState(), 2);
        level.setBlock(blocked.below(), Blocks.WATER.defaultBlockState(), 2);
        check(!spring.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), blocked), "Spring converted an existing water pool");
        check(level.getBlockState(blocked.below()).is(Blocks.WATER), "Existing water was replaced");
        level.setBlock(blocked.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 2);
        level.setBlock(blocked.below(4), Blocks.AIR.defaultBlockState(), 2);
        check(!spring.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), blocked), "Spring generated over a missing basin floor");
        FloraExpansion.LOGGER.info("Rooted brine spring fixture: brine={}, crust={}, mineral trail={}", brine, crust, trail);
        helper.runAfterDelay(65, () -> {
            check(brine(level, surface) == brine, "Surface brine leaked or created new sources");
            clear(level, surface);
            clear(level, blocked);
            helper.succeed();
        });
    }

    private static void verifySeparation(GameTestHelper helper, net.minecraft.world.level.levelgen.feature.ConfiguredFeature<?, ?> spring) {
        var level = helper.getLevel();
        var anchor = helper.absolutePos(new BlockPos(2304, 150, 256));
        anchor = new BlockPos((anchor.getX() >> 4) << 4, anchor.getY(), (anchor.getZ() >> 4) << 4);
        for (var p : BlockPos.betweenClosed(anchor.offset(-8,-6,-8), anchor.offset(40,6,40)))
            level.setBlock(p, p.getY() < anchor.getY() ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
        for (int x = (anchor.getX()-8)>>4; x <= (anchor.getX()+40)>>4; x++)
            for (int z = (anchor.getZ()-8)>>4; z <= (anchor.getZ()+40)>>4; z++)
                Heightmap.primeHeightmaps(level.getChunk(x,z), java.util.EnumSet.of(Heightmap.Types.WORLD_SURFACE, Heightmap.Types.WORLD_SURFACE_WG));
        var first = anchor.offset(2,0,2);
        check(spring.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), first), "Spring limit fixture failed to generate its first pool");
        int before = brine(level, first);
        check(!spring.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), anchor.offset(13,0,13)), "A second spring crowded an existing surface pool");
        check(brine(level, first) == before, "Rejecting a second spring modified the first pool");
        var distant = anchor.offset(128,0,0);
        prepare(level, distant);
        check(spring.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), distant), "Spring separation rejected a distant suitable basin");
    }

    private static void prepare(ServerLevel level, BlockPos surface) {
        for (var pos : BlockPos.betweenClosed(surface.offset(-12, -40, -12), surface.offset(12, 6, 12)))
            level.setBlock(pos, pos.getY() >= surface.getY() ? Blocks.AIR.defaultBlockState()
                    : pos.getY() == surface.getY() - 1 ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
        for (var pos : BlockPos.betweenClosed(surface.offset(-2, -32, -2), surface.offset(2, -30, 2)))
            level.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 2);
        for (int x = (surface.getX() - 12) >> 4; x <= (surface.getX() + 12) >> 4; x++) {
            for (int z = (surface.getZ() - 12) >> 4; z <= (surface.getZ() + 12) >> 4; z++)
                Heightmap.primeHeightmaps(level.getChunk(x, z), java.util.EnumSet.of(Heightmap.Types.WORLD_SURFACE,
                        Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Heightmap.Types.OCEAN_FLOOR_WG));
        }
    }

    private static int brine(ServerLevel level, BlockPos surface) {
        int count = 0;
        for (var pos : BlockPos.betweenClosed(surface.offset(-7, -4, -7), surface.offset(7, 1, 7))) {
            var fluid = level.getFluidState(pos);
            if (!fluid.is(ModFluids.SALT_WATER.get()) && !fluid.is(ModFluids.FLOWING_SALT_WATER.get())) continue;
            check(fluid.isSource(), "Flowing brine escaped the spring");
            for (var direction : Direction.values()) {
                if (direction == Direction.UP) continue;
                var neighbor = pos.relative(direction);
                check(level.getFluidState(neighbor).is(ModFluids.SALT_WATER.get())
                        || level.getBlockState(neighbor).isFaceSturdy(level, neighbor, direction.getOpposite()), "Unsealed surface spring");
            }
            count++;
        }
        return count;
    }

    private static void clear(ServerLevel level, BlockPos surface) {
        for (var pos : BlockPos.betweenClosed(surface.offset(-12, -40, -12), surface.offset(12, 6, 12)))
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
