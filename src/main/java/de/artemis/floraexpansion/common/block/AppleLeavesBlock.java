package de.artemis.floraexpansion.common.block;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.registry.ModParticles;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class AppleLeavesBlock extends FruitLeavesBlock {
 @NotNull @Override public com.mojang.serialization.MapCodec<AppleLeavesBlock> codec() { return simpleCodec(AppleLeavesBlock::new); }
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    public static final int MAX_AGE = 2;

    public AppleLeavesBlock(Properties properties) {
        super(0.01F, properties);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected int getGrowthChance() {
        return 24;
    }

    @Override
    protected boolean shouldSpawnFruitParticle(int age) {
        return age > 0;
    }

    @Override
    protected SimpleParticleType getFruitParticle() {
        return ModParticles.FALLING_APPLE.get();
    }

    @Override
    public boolean canHarvest(BlockState state) {
        return state.getValue(AGE) >= MAX_AGE;
    }

}
