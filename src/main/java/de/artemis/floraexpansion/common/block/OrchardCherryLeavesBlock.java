package de.artemis.floraexpansion.common.block;



import de.artemis.floraexpansion.common.registry.ModParticles;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class OrchardCherryLeavesBlock extends FruitLeavesBlock {

    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    public static final int MAX_AGE = 3;

    public OrchardCherryLeavesBlock(Properties properties) {
        super(0.0F, properties);
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    public int getGrowthChance() {
        return 8;
    }

    @Override
    public boolean shouldSpawnFruitParticle(int age) {
        return age != 2;
    }

    @Override
    public SimpleParticleType getFruitParticle() {
        return ModParticles.FALLING_CHERRY.get();
    }

    @Override
    public boolean canHarvest(BlockState state) {
        return state.getValue(AGE) > 0;
    }

}
