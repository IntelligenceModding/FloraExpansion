package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModParticles;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
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
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected int getGrowthChance() {
        return 8;
    }

    @Override
    protected boolean shouldSpawnFruitParticle(int age) {
        return age != 2;
    }

    @Override
    protected SimpleParticleType getFruitParticle() {
        return ModParticles.FALLING_CHERRY.get();
    }

    @Override
    public boolean canHarvest(BlockState state) {
        return state.getValue(AGE) > 0;
    }

    @Override
    public ItemStack getHarvest(BlockState state, RandomSource random) {
        int amount = switch (state.getValue(AGE)) {
            case 1 -> 1;
            case 2 -> 2 + random.nextInt(2);
            case 3 -> 3 + random.nextInt(2);
            default -> 0;
        };
        return new ItemStack(ModItems.CHERRIES.get(), amount);
    }
}
