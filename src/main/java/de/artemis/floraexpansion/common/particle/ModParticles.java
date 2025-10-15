package de.artemis.floraexpansion.common.particle;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, FloraExpansion.MODID);

    public static final Supplier<SimpleParticleType> LEAF_FLUFF_PARTICLES =
            PARTICLE_TYPES.register("leaf_fluff_particles", () -> new SimpleParticleType(true));

    public static final Supplier<SimpleParticleType> PINE_LEAF_FLUFF_PARTICLES =
            PARTICLE_TYPES.register("pine_leaf_fluff_particles", () -> new SimpleParticleType(true));

    public static final Supplier<SimpleParticleType> PINE_PARTICLES =
            PARTICLE_TYPES.register("pine_particles", () -> new SimpleParticleType(true));

    public static final Supplier<SimpleParticleType> FLAX_FLOWER =
            PARTICLE_TYPES.register("flax_flower_particles", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }
}