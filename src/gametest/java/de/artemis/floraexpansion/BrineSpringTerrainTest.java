package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import java.util.function.Consumer;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class BrineSpringTerrainTest {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "brine_spring_terrain");
    private static final ResourceKey<Consumer<GameTestHelper>> FUNCTION = ResourceKey.create(Registries.TEST_FUNCTION, ID);
    @SubscribeEvent public static void function(RegisterEvent e) { e.register(Registries.TEST_FUNCTION, ID, () -> BrineSpringTerrainTest::run); }
    @SubscribeEvent public static void tests(RegisterGameTestsEvent e) {
        var rules = net.minecraft.world.level.gamerules.GameRuleMap.of();
        rules.set(net.minecraft.world.level.gamerules.GameRules.RANDOM_TICK_SPEED, 0);
        var environment = e.registerEnvironment(ID, new TestEnvironmentDefinition.SetGameRules(rules));
        e.registerTest(ID, new FunctionGameTestInstance(FUNCTION, new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 120, 0, true)));
    }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
    private static void run(GameTestHelper helper) {
        var level = helper.getLevel();
        var root = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getValueOrThrow(ModConfiguredFeatures.ROOTED_BRINE_SPRING_KEY);
        var spring = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getValueOrThrow(ModConfiguredFeatures.BRINE_SPRING_KEY);
        var missing = new java.util.ArrayList<Integer>();
        var basins = new java.util.LinkedHashMap<BlockPos,Integer>();
        for (int scenario = 0; scenario < 4; scenario++) {
            var surface = helper.absolutePos(new BlockPos(768 + scenario * 64, 150, 256));
            int depth = scenario == 2 ? 200 : 30;
            for (var p : BlockPos.betweenClosed(surface.offset(-12,-depth-4,-12), surface.offset(12,8,12))) {
                int slope = scenario == 1 ? Math.floorDiv(p.getX()-surface.getX(), 3) : 0;
                int top = surface.getY() + slope;
                level.setBlock(p, p.getY() >= top ? Blocks.AIR.defaultBlockState()
                        : p.getY() == top-1 ? Blocks.GRASS_BLOCK.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
            }
            var cave = surface.below(depth);
            for (var p : BlockPos.betweenClosed(cave.offset(-2,-2,-2), cave.offset(2,0,2))) level.setBlock(p, Blocks.CAVE_AIR.defaultBlockState(), 2);
            if (scenario == 3)
                for (var p : BlockPos.betweenClosed(surface.offset(-4,-20,-4), surface.offset(4,-12,4)))
                    level.setBlock(p, Blocks.CAVE_AIR.defaultBlockState(), 2);
            if (scenario == 0) level.setBlock(surface, Blocks.SHORT_GRASS.defaultBlockState(), 2);
            prime(level, surface);
            root.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), cave);
            int brine = 0;
            for (var p : BlockPos.betweenClosed(surface.offset(-7,-4,-7), surface.offset(7,2,7)))
                if (level.getBlockState(p).is(ModBlocks.SALT_WATER.get())) brine++;
            if (brine <= 5) missing.add(scenario);
            basins.put(surface, brine);
            FloraExpansion.LOGGER.info("Brine spring terrain case {}: {} source blocks, cave-to-surface distance {}", scenario, brine, depth);
            // Both a cliff and a protected block must still reject atomically.
            var rejected = surface.east(24);
            for (var p : BlockPos.betweenClosed(rejected.offset(-8,-4,-8), rejected.offset(8,6,8)))
                level.setBlock(p, p.getY() >= rejected.getY() ? Blocks.AIR.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
            level.setBlock(rejected.below(2), Blocks.CHEST.defaultBlockState(), 2);
            prime(level, rejected);
            check(!spring.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), rejected), "Spring erased a chest");
            check(level.getBlockState(rejected.below(2)).is(Blocks.CHEST), "Rejected basin changed a protected block");
        }
        check(missing.isEmpty(), "Rooted springs missing in terrain cases " + missing + " (0=grass, 1=gentle slope, 2=deep cave, 3=intermediate cave)");
        helper.runAfterDelay(65, () -> {
            basins.forEach((surface, expected) -> {
                int sources = 0;
                for (var p : BlockPos.betweenClosed(surface.offset(-7,-4,-7), surface.offset(7,2,7))) {
                    var fluid = level.getFluidState(p);
                    if (!fluid.is(ModFluids.SALT_WATER.get()) && !fluid.is(ModFluids.FLOWING_SALT_WATER.get())) continue;
                    check(fluid.isSource(), "Spring leaked flowing brine on sloped or deep terrain");
                    sources++;
                }
                check(sources == expected, "Spring lost or created sources after fluid ticks: " + sources + "/" + expected);
            });
            helper.succeed();
        });
    }
    private static void prime(ServerLevel level, BlockPos surface) {
        for (int x = (surface.getX()-12)>>4; x <= (surface.getX()+12)>>4; x++)
            for (int z = (surface.getZ()-12)>>4; z <= (surface.getZ()+12)>>4; z++)
                Heightmap.primeHeightmaps(level.getChunk(x,z), java.util.EnumSet.of(Heightmap.Types.WORLD_SURFACE, Heightmap.Types.WORLD_SURFACE_WG));
    }
}
