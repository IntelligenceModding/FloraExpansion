package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBiomes;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModConfiguredFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.levelgen.Heightmap;

final class SaltBiomeTests {
    static void climate(GameTestHelper helper) {
        var registry = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        var salt = registry.getOrThrow(ModBiomes.SALT_CAVES);
        check(salt.value().getGenerationSettings().getCarvers().iterator().hasNext(), "Salt biome has no carvers");
        var overworld = new MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD, registry);
        var source = MultiNoiseBiomeSource.createFromPreset(Holder.direct(overworld));
        check(source.possibleBiomes().contains(salt), "Salt Caves is absent from /locate biome's candidate biomes");
        check(source.getNoiseBiome(Climate.target(.6F, -.65F, .95F, 0F, .55F, .35F)).is(ModBiomes.SALT_CAVES), "Salt underground climate cannot be selected");
        var vanilla = MultiNoiseBiomeSourceParameterList.knownPresets().get(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD);
        var baseline = MultiNoiseBiomeSource.createFromList(new Climate.ParameterList<>(vanilla.values().stream()
                .map(pair -> pair.mapSecond(key -> (Holder<net.minecraft.world.level.biome.Biome>) registry.getOrThrow(key))).toList()));
        var samples = RandomSource.create(48219);
        int saltSamples = 0, dripstoneSamples = 0;
        for (int i = 0; i < 30000; i++) {
            var sample = Climate.target(samples.nextFloat() * 2 - 1, samples.nextFloat() * 2 - 1,
                    samples.nextFloat() * 2 - 1, samples.nextFloat() * 2 - 1,
                    .2F + samples.nextFloat() * .7F, samples.nextFloat() * 2 - 1);
            var selected = source.getNoiseBiome(sample);
            var original = baseline.getNoiseBiome(sample);
            if (selected.is(Biomes.DRIPSTONE_CAVES)) dripstoneSamples++;
            if (selected.is(ModBiomes.SALT_CAVES)) {
                saltSamples++;
                check(original.is(Biomes.DRIPSTONE_CAVES), "Salt expanded outside the original Dripstone climate region");
            } else if (!selected.equals(original)) {
                // Splitting rectangles may change R-tree ordering of equally close vanilla biomes.
                long chosenFitness = vanilla.values().stream().filter(pair -> selected.is(pair.getSecond()))
                        .mapToLong(pair -> fitness(pair.getFirst(), sample)).min().orElseThrow();
                long originalFitness = vanilla.values().stream().filter(pair -> original.is(pair.getSecond()))
                        .mapToLong(pair -> fitness(pair.getFirst(), sample)).min().orElseThrow();
                check(chosenFitness == originalFitness, "Salt changed an unrelated biome's climate distance");
            }
        }
        check(saltSamples > 0 && Math.abs(saltSamples - dripstoneSamples) < (saltSamples + dripstoneSamples) / 10,
                "Salt and Dripstone climate coverage are unbalanced: " + saltSamples + "/" + dripstoneSamples);
        FloraExpansion.LOGGER.info("Salt climate coverage: {}/30000 underground samples", saltSamples);
        check(!source.getNoiseBiome(Climate.target(.6F, -.65F, .55F, 0F, 0F, 0F)).is(ModBiomes.SALT_CAVES), "Salt replaced a surface biome");
        check(source.getNoiseBiome(Climate.target(.6F, -.65F, .95F, 0F, .55F, -.35F)).is(Biomes.DRIPSTONE_CAVES), "Salt displaced Dripstone Caves");
        check(source.getNoiseBiome(Climate.target(0F, .95F, 0F, 0F, .55F, 0F)).is(Biomes.LUSH_CAVES), "Salt displaced Lush Caves");
        int worldSalt = 0, worldDripstone = 0;
        var access = helper.getLevel().registryAccess();
        var settings = access.lookupOrThrow(Registries.NOISE_SETTINGS)
                .getValueOrThrow(net.minecraft.world.level.levelgen.NoiseGeneratorSettings.OVERWORLD);
        for (long seed : new long[]{1, 42, 12345, 8675309, -314159}) {
            var randomState = net.minecraft.world.level.levelgen.RandomState.create(settings, access.lookupOrThrow(Registries.NOISE), seed);

            int seedSalt = 0, seedDripstone = 0;
            for (int x = -4096; x <= 4096; x += 256) {
                for (int z = -4096; z <= 4096; z += 256) {
                    for (int y : new int[]{-32, 0, 32}) {
                        var selected = source.getNoiseBiome(x >> 2, y >> 2, z >> 2, randomState.sampler());
                        if (selected.is(ModBiomes.SALT_CAVES)) seedSalt++;
                        if (selected.is(Biomes.DRIPSTONE_CAVES)) seedDripstone++;
                    }
                }
            }
            worldSalt += seedSalt;
            worldDripstone += seedDripstone;
            FloraExpansion.LOGGER.info("Cave distribution seed {}: salt={}, dripstone={}", seed, seedSalt, seedDripstone);
        }
        check(worldSalt > 0 && worldDripstone > 0 && worldSalt > worldDripstone * .65 && worldSalt < worldDripstone * 1.55,
                "Five-seed cave distribution is unbalanced: " + worldSalt + "/" + worldDripstone);
        var nether = new MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset.NETHER, registry);
        check(nether.parameters().values().stream().noneMatch(pair -> pair.getSecond().is(ModBiomes.SALT_CAVES)), "Salt added to the Nether");
    }

    static void surfaces(GameTestHelper helper) {
        var level = helper.getLevel();        var removed = net.minecraft.resources.Identifier.fromNamespaceAndPath(FloraExpansion.MODID,"salt_cave");
        check(level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE)
                .get(net.minecraft.resources.ResourceKey.create(Registries.CONFIGURED_FEATURE,removed)).isEmpty(), "Old chamber feature still registered");
        check(level.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE)
                .get(net.minecraft.resources.ResourceKey.create(Registries.PLACED_FEATURE,
                        removed.withPath("salt_cave_placed"))).isEmpty(), "Old chamber placement still registered");
        var raw = helper.absolutePos(new BlockPos(512, 120, 256));
        var origin = new BlockPos(raw.getX() & ~15, raw.getY(), raw.getZ() & ~15);
        for (var pos : BlockPos.betweenClosed(origin.offset(-3, -8, -3), origin.offset(18, 24, 18)))
            level.setBlock(pos, Blocks.STONE.defaultBlockState(), 2);
        for (var pos : BlockPos.betweenClosed(origin.offset(1, 0, 1), origin.offset(14, 8, 14)))
            level.setBlock(pos, Blocks.CAVE_AIR.defaultBlockState(), 2);
        var salt = level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(ModBiomes.SALT_CAVES);
        FillBiomeCommand.fill(level, origin.offset(-4, -12, -4), origin.offset(20, 28, 20), salt);
        for (int x = (origin.getX() - 3) >> 4; x <= (origin.getX() + 18) >> 4; x++) {
            for (int z = (origin.getZ() - 3) >> 4; z <= (origin.getZ() + 18) >> 4; z++)
                Heightmap.primeHeightmaps(level.getChunk(x, z), java.util.EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG));
        }
        var ore = origin.offset(0, 3, 8);
        var chest = origin.offset(2, 0, 2);
        var water = origin.offset(3, 1, 3);
        level.setBlock(ore, Blocks.DIAMOND_ORE.defaultBlockState(), 2);
        level.setBlock(chest, Blocks.CHEST.defaultBlockState(), 2);
        level.setBlock(water, Blocks.WATER.defaultBlockState(), 2);
        Block[] oreTypes = {Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.COPPER_ORE, Blocks.GOLD_ORE,
                Blocks.REDSTONE_ORE, Blocks.EMERALD_ORE, Blocks.LAPIS_ORE, Blocks.DIAMOND_ORE,
                Blocks.DEEPSLATE_COAL_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.DEEPSLATE_GOLD_ORE,
                Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.DEEPSLATE_DIAMOND_ORE};
        for (int i = 0; i < oreTypes.length; i++)
            level.setBlock(origin.offset(0, 1 + i / 8, 1 + i % 8), oreTypes[i].defaultBlockState(), 2);
        var buriedOre = origin.offset(-2, 3, 8);
        level.setBlock(buriedOre, Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState(), 2);
        var feature = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).getValueOrThrow(ModConfiguredFeatures.SALT_CAVE_SURFACE_KEY);
        check(feature.place(level, level.getChunkSource().getGenerator(), RandomSource.create(1), origin), "Salt biome surfaces were not decorated");
        int saltBlocks = 0, spikes = 0, brine = 0, plants = 0, groupedPlants = 0;
        for (var pos : BlockPos.betweenClosed(origin.offset(-2, -4, -2), origin.offset(17, 12, 17))) {
            var state = level.getBlockState(pos);
            if (state.is(ModBlocks.SALT_DEPOSIT.get()))
                check(state.getValue(de.artemis.floraexpansion.common.block.SaltDepositBlock.NATURAL), "Cave-generated salt lost buried finds");
            if (state.is(ModBlocks.GLASSWORT.get())) {
                plants++;
                check(state.canSurvive(level, pos), "Glasswort patch escaped the brine shore");
                for (var direction : net.minecraft.core.Direction.Plane.HORIZONTAL) {
                    if (level.getBlockState(pos.relative(direction)).is(ModBlocks.GLASSWORT.get())) {
                        groupedPlants++;
                        break;
                    }
                }
            }
            if (state.is(ModBlocks.SALT_WATER.get())) {
                brine++;
                check(state.getFluidState().isSource(), "Cave pool did not contain collectable sources");
                for (var direction : net.minecraft.core.Direction.values()) {
                    if (direction == net.minecraft.core.Direction.UP) continue;
                    var neighbor = pos.relative(direction);
                    check(level.getBlockState(neighbor).is(ModBlocks.SALT_WATER.get())
                            || level.getBlockState(neighbor).isFaceSturdy(level, neighbor, direction.getOpposite()), "Unsealed natural cave brine pool");
                }
            }
            if (state.is(ModBlocks.CRYSTALLINE_SALT.get()) || state.is(ModBlocks.SMOOTH_SALT.get())) saltBlocks++;
            if (state.is(ModBlocks.SALT_SPIKE.get())) {
                spikes++;
                check(state.canSurvive(level, pos), "Unsupported natural-cave spike at " + pos);
                check(!state.getValue(PointedDripstoneBlock.WATERLOGGED), "Salt spike introduced fresh water");
            }
        }
        check(saltBlocks > 400 && spikes > 20, "Missing salt lining or spikes across the cave network");
        check(brine >= 26, "Suitable cave floors did not receive a brine pool");
        check(plants > 0 && groupedPlants > 0, "Brine shores did not receive grouped Glasswort");
        check(level.getBlockState(ore).is(ModBlocks.CALCITE_DIAMOND_ORE.get()), "Salt lining lost the exposed diamond ore");
        for (int i = 0; i < oreTypes.length; i++)
            check(level.getBlockState(origin.offset(0, 1 + i / 8, 1 + i % 8)).is(ModBlocks.CALCITE_ORES.get(i % 8).get()),
                    "Ore did not receive a calcite host: " + oreTypes[i]);
        check(level.getBlockState(buriedOre).is(Blocks.DEEPSLATE_DIAMOND_ORE), "Salt lining reached buried ore beyond its lining");
        for (var pos : BlockPos.betweenClosed(origin.offset(1, 0, 1), origin.offset(14, 8, 14))) {
            if (!level.getBlockState(pos).isAir()) continue;
            for (var direction : net.minecraft.core.Direction.values()) {
                var neighbor = level.getBlockState(pos.relative(direction));
                check(!neighbor.is(Blocks.STONE) && !neighbor.is(Blocks.DEEPSLATE), "Grey cave wall survived salt lining");
                for (var vanillaOre : oreTypes) check(!neighbor.is(vanillaOre), "Grey ore host survived salt lining");
            }
        }
        check(level.getBlockState(chest).is(Blocks.CHEST), "Salt decoration erased a protected block");
        check(level.getBlockState(water).is(Blocks.WATER), "Salt decoration converted an existing aquifer");
        FillBiomeCommand.fill(level, origin.offset(-4, -12, -4), origin.offset(20, 28, 20),
                level.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS));
        check(!feature.place(level, level.getChunkSource().getGenerator(), RandomSource.create(2), origin), "Salt surfaces ran outside the Salt Caves biome");
        FloraExpansion.LOGGER.info("Salt biome surface fixture: salt={}, spikes={}, brine={}, glasswort={}, grouped={}", saltBlocks, spikes, brine, plants, groupedPlants);
        for (var pos : BlockPos.betweenClosed(origin.offset(-3, -8, -3), origin.offset(18, 24, 18)))
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
 private static long fitness(Climate.ParameterPoint p, Climate.TargetPoint t) { return sq(p.temperature().distance(t.temperature())) + sq(p.humidity().distance(t.humidity())) + sq(p.continentalness().distance(t.continentalness())) + sq(p.erosion().distance(t.erosion())) + sq(p.depth().distance(t.depth())) + sq(p.weirdness().distance(t.weirdness())) + sq(p.offset()); }
 private static long sq(long n) { return n*n; }
}
