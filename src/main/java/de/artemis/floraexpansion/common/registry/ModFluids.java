package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModFluids {
    public static final int SALT_WATER_COLOR = 0xFF79B8CA;
    private static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, FloraExpansion.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, FloraExpansion.MODID);
    public static final DeferredHolder<FluidType, FluidType> SALT_WATER_TYPE = TYPES.register("salt_water", () -> new FluidType(
            FluidType.Properties.create().descriptionId("fluid_type.floraexpansion.salt_water")
                    .isWaterLike(true).canSwim(true).canDrown(true).canExtinguish(true).supportsBoating(true)
                    .fallDistanceModifier(0F).canHydrate(false).canConvertToSource(false).density(1025).viscosity(1000)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)) {
        @Override public DripstoneDripInfo getDripInfo() {
            // Resolve registered blocks/particles lazily, after the fluid-type registry is built.
            return new DripstoneDripInfo(net.minecraft.world.level.block.PointedDripstoneBlock.LAVA_TRANSFER_PROBABILITY_PER_RANDOM_TICK,
                    ModParticles.DRIPPING_SALT_WATER.get(), ModBlocks.SALT_WATER_CAULDRON.get());
        }
        @Override public boolean handleCauldronDrip(Fluid fluid, net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos) {
            if (fluid != SALT_WATER.get()) return false;
            var state = ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState()
                    .setValue(net.minecraft.world.level.block.LayeredCauldronBlock.LEVEL, 3);
            level.setBlockAndUpdate(pos, state);
            level.gameEvent(net.minecraft.world.level.gameevent.GameEvent.BLOCK_CHANGE, pos,
                    net.minecraft.world.level.gameevent.GameEvent.Context.of(state));
            level.playSound(null, pos, SoundEvents.POINTED_DRIPSTONE_DRIP_WATER,
                    net.minecraft.sounds.SoundSource.BLOCKS, 2F, .9F + level.getRandom().nextFloat() * .1F);
            return true;
        }
        @Override public boolean isVaporizedOnPlacement(net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                                                       net.neoforged.neoforge.fluids.FluidStack stack) {
            return level.environmentAttributes().getValue(net.minecraft.world.attribute.EnvironmentAttributes.WATER_EVAPORATES, pos);
        }
        @Override public void onVaporize(@Nullable LivingEntity entity,
                                        net.minecraft.world.level.Level level, net.minecraft.core.BlockPos pos,
                                        net.neoforged.neoforge.fluids.FluidStack stack) {
            super.onVaporize(entity,level,pos,stack);
            de.artemis.floraexpansion.common.worldgen.SaltWaterEvaporation.leaveResidue(level,pos);
        }
    });
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SALT_WATER = FLUIDS.register("salt_water", () -> new BaseFlowingFluid.Source(properties()));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_SALT_WATER = FLUIDS.register("flowing_salt_water", () -> new BaseFlowingFluid.Flowing(properties()));

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(SALT_WATER_TYPE, SALT_WATER, FLOWING_SALT_WATER)
                .bucket(ModItems.SALT_WATER_BUCKET).block(ModBlocks.SALT_WATER)
                .slopeFindDistance(4).levelDecreasePerBlock(1).tickRate(5).explosionResistance(100F);
    }
    public static void register(IEventBus bus) { TYPES.register(bus); FLUIDS.register(bus); }
    private ModFluids() {}
}
