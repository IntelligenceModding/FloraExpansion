package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biomes;
import java.util.List;
import java.util.ArrayList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MobSpawnSettings;

public final class ModBiomes {
    public static final ResourceKey<Biome> SALT_CAVES = ResourceKey.create(Registries.BIOME,
            Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salt_caves"));

    // Split Dripstone's existing cave region evenly along the signed weirdness noise.
    // This keeps both cave types comparable without expanding into unrelated underground climates.
    public static final Climate.ParameterPoint SALT_CAVES_CLIMATE = Climate.parameters(
            Climate.Parameter.span(-1F, 1F), Climate.Parameter.span(-1F, 1F),
            Climate.Parameter.span(.8F, 1F), Climate.Parameter.span(-1F, 1F),
            Climate.Parameter.span(.2F, .9F), Climate.Parameter.span(0F, 1F), 0F);

    /** Partition existing Dripstone rectangles instead of adding a competing underground rectangle. */
    public static List<Pair<Climate.ParameterPoint, Holder<Biome>>>
    withSaltCaves(List<Pair<Climate.ParameterPoint, Holder<Biome>>> original,
                  Holder<Biome> salt) {
        var result = new ArrayList<Pair<Climate.ParameterPoint, Holder<Biome>>>();
        var target = axes(SALT_CAVES_CLIMATE);
        for (var entry : original) {
            if (!entry.getSecond().is(Biomes.DRIPSTONE_CAVES)) { result.add(entry); continue; }
            var remaining = axes(entry.getFirst());
            var intersection = remaining.clone();
            boolean intersects = true;
            for (int i = 0; i < 6; i++) {
                long min = Math.max(remaining[i].min(), target[i].min()), max = Math.min(remaining[i].max(), target[i].max());
                if (min > max) { intersects = false; break; }
                intersection[i] = new Climate.Parameter(min, max);
            }
            if (!intersects) { result.add(entry); continue; }
            for (int i = 0; i < 6; i++) {
                if (remaining[i].min() < intersection[i].min()) {
                    var outside = remaining.clone();
                    outside[i] = new Climate.Parameter(remaining[i].min(), intersection[i].min() - 1);
                    result.add(Pair.of(point(outside, entry.getFirst().offset()), entry.getSecond()));
                }
                if (remaining[i].max() > intersection[i].max()) {
                    var outside = remaining.clone();
                    outside[i] = new Climate.Parameter(intersection[i].max() + 1, remaining[i].max());
                    result.add(Pair.of(point(outside, entry.getFirst().offset()), entry.getSecond()));
                }
                remaining[i] = intersection[i];
            }
            result.add(Pair.of(point(intersection, entry.getFirst().offset()), salt));
        }
        return result;
    }

    private static Climate.Parameter[] axes(Climate.ParameterPoint p) {
        return new Climate.Parameter[]{p.temperature(), p.humidity(), p.continentalness(), p.erosion(), p.depth(), p.weirdness()};
    }

    private static Climate.ParameterPoint point(Climate.Parameter[] p, long offset) {
        return new Climate.ParameterPoint(p[0], p[1], p[2], p[3], p[4], p[5], offset);
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        var generation = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE),
                context.lookup(Registries.CONFIGURED_CARVER));
        BiomeDefaultFeatures.addDefaultCarversAndLakes(generation);
        generation.addFeature(net.minecraft.world.level.levelgen.GenerationStep.Decoration.LAKES,
                ModPlacedFeatures.ROOTED_BRINE_SPRING_PLACED_KEY);
        BiomeDefaultFeatures.addDefaultCrystalFormations(generation);
        BiomeDefaultFeatures.addDefaultMonsterRoom(generation);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(generation);
        BiomeDefaultFeatures.addDefaultSprings(generation);
        BiomeDefaultFeatures.addSurfaceFreezing(generation);
        BiomeDefaultFeatures.addDefaultOres(generation, true);
        BiomeDefaultFeatures.addDefaultSoftDisks(generation);
        var mobs = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.dripstoneCavesSpawns(mobs);
        context.register(SALT_CAVES, new Biome.BiomeBuilder()
                .hasPrecipitation(true).temperature(.8F).downfall(.2F)
                .setAttribute(EnvironmentAttributes.SKY_COLOR, OverworldBiomes.calculateSkyColor(.8F))
                .setAttribute(EnvironmentAttributes.FOG_COLOR, 0xB8C9CD)
                .setAttribute(EnvironmentAttributes.WATER_FOG_COLOR, 0x436F79)
                .setAttribute(EnvironmentAttributes.AMBIENT_PARTICLES, AmbientParticle.of(ParticleTypes.WHITE_ASH, .0025F))
                .setAttribute(EnvironmentAttributes.AMBIENT_SOUNDS, new AmbientSounds(
                        java.util.Optional.empty(),
                        java.util.Optional.of(new net.minecraft.world.attribute.AmbientMoodSettings(
                                ModSounds.SALT_CAVE_MOOD, 6000, 8, 2.0)),
                        List.of(new net.minecraft.world.attribute.AmbientAdditionsSettings(
                                ModSounds.SALT_CAVE_ADDITIONS, .003D))))
                .setAttribute(EnvironmentAttributes.BACKGROUND_MUSIC, new BackgroundMusic(SoundEvents.MUSIC_BIOME_DRIPSTONE_CAVES))
                .specialEffects(new BiomeSpecialEffects.Builder().waterColor(4159204).build())
                .mobSpawnSettings(mobs.build()).generationSettings(generation.build()).build());
    }
}
