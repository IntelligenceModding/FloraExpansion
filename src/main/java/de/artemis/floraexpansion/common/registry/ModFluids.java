package de.artemis.floraexpansion.common.registry;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModFluids {
    public static final int SALT_WATER_COLOR = 0xFF79B8CA;
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, FloraExpansion.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, FloraExpansion.MODID);
    public static final RegistryObject<FluidType> SALT_WATER_TYPE = TYPES.register("salt_water", () -> new FluidType(
            FluidType.Properties.create().descriptionId("fluid_type.floraexpansion.salt_water")
                    .canSwim(true).canDrown(true).canExtinguish(true).supportsBoating(true)
                    .fallDistanceModifier(0F).canHydrate(false).canConvertToSource(false).density(1025).viscosity(1000)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)) {
        @Override public void initializeClient(java.util.function.Consumer<net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions> consumer) { de.artemis.floraexpansion.FloraExpansionClient.registerFluidExtensions(consumer); }
        @Override public boolean isVaporizedOnPlacement(@NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos,
                                                       @NotNull net.minecraftforge.fluids.FluidStack stack) {
            return level.dimensionType().ultraWarm();
        }
        @Override public void onVaporize(@Nullable net.minecraft.world.entity.player.Player entity,
                                        @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos,
                                        @NotNull net.minecraftforge.fluids.FluidStack stack) {
            super.onVaporize(entity,level,pos,stack);
            de.artemis.floraexpansion.common.worldgen.SaltWaterEvaporation.leaveResidue(level,pos);
        }
    });
    public static final RegistryObject<ForgeFlowingFluid.Source> SALT_WATER = FLUIDS.register("salt_water", () -> new ForgeFlowingFluid.Source(properties()));
    public static final RegistryObject<ForgeFlowingFluid.Flowing> FLOWING_SALT_WATER = FLUIDS.register("flowing_salt_water", () -> new ForgeFlowingFluid.Flowing(properties()));

    private static ForgeFlowingFluid.Properties properties() {
        return new ForgeFlowingFluid.Properties(SALT_WATER_TYPE, SALT_WATER, FLOWING_SALT_WATER)
                .bucket(ModItems.SALT_WATER_BUCKET).block(ModBlocks.SALT_WATER)
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5).explosionResistance(100F);
    }
    public static void register(IEventBus bus) { TYPES.register(bus); FLUIDS.register(bus); }
        public static DripstoneDripInfo getDripInfo() {
            // Resolve registered blocks/particles lazily, after the fluid-type registry is built.
            return new DripstoneDripInfo(.05859375F,
                    ModParticles.DRIPPING_SALT_WATER.get(), ModBlocks.SALT_WATER_CAULDRON.get());
        }
        public static boolean handleCauldronDrip(@NotNull Fluid fluid, @NotNull net.minecraft.world.level.Level level, @NotNull net.minecraft.core.BlockPos pos) {
            if (fluid != SALT_WATER.get()) return false;
            var state = ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState()
                    .setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3);
            level.setBlock(pos, state, 3);
            level.gameEvent(net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE, pos,
                    net.minecraft.world.level.gameevent.GameEvent.Context.of(state));
            level.playSound(null, pos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER,
                    net.minecraft.sounds.SoundSource.BLOCKS, 2F, .9F + level.getRandom().nextFloat() * .1F);
            return true;
        }

    public record DripstoneDripInfo(float chance, net.minecraft.core.particles.ParticleOptions dripParticle, net.minecraft.world.level.block.Block cauldron) {}
    public static DripstoneDripInfo getDripInfo(Fluid fluid) {
        if (fluid == SALT_WATER.get()) return getDripInfo();
        if (fluid == net.minecraft.world.level.material.Fluids.WATER) return new DripstoneDripInfo(.17578125F, net.minecraft.core.particles.ParticleTypes.DRIPPING_DRIPSTONE_WATER, net.minecraft.world.level.block.Blocks.WATER_CAULDRON);
        if (fluid == net.minecraft.world.level.material.Fluids.LAVA) return new DripstoneDripInfo(.05859375F, net.minecraft.core.particles.ParticleTypes.DRIPPING_DRIPSTONE_LAVA, net.minecraft.world.level.block.Blocks.LAVA_CAULDRON);
        return null;
    }
    private ModFluids() {}
}
