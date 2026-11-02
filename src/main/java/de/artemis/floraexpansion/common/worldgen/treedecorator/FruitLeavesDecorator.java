package de.artemis.floraexpansion.common.worldgen.treedecorator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class FruitLeavesDecorator extends TreeDecorator {
    private final Block originalLeaves;
    private final Supplier<? extends Block> fruitLeaves;
    private final int replacementDivisor;
    private final int maxReplacements;
    private final boolean exposedOnly;

    protected FruitLeavesDecorator(Block originalLeaves, Supplier<? extends Block> fruitLeaves, int replacementDivisor, int maxReplacements, boolean exposedOnly) {
        this.originalLeaves = originalLeaves;
        this.fruitLeaves = fruitLeaves;
        this.replacementDivisor = replacementDivisor;
        this.maxReplacements = maxReplacements;
        this.exposedOnly = exposedOnly;
    }

    public int replacementDivisor() { return replacementDivisor; }
    public int maxReplacements() { return maxReplacements; }
    public boolean exposedOnly() { return exposedOnly; }

    @Override
    public void place(Context context) {
        List<BlockPos> validLeaves = new ArrayList<>();
        for (BlockPos pos : context.leaves()) {
            if (context.level().isStateAtPosition(pos, state -> state.is(originalLeaves)) && (!exposedOnly || isExposed(context, pos))) {
                validLeaves.add(pos.immutable());
            }
        }
        if (validLeaves.isEmpty()) {
            return;
        }
        // Keep the original shuffle and random consumption so existing seeds generate the same trees.
        Collections.shuffle(validLeaves, new java.util.Random(context.random().nextLong()));
        int count = Math.min(Math.max(1, validLeaves.size() / replacementDivisor), maxReplacements);
        for (int i = 0; i < count && i < validLeaves.size(); i++) {
            context.setBlock(validLeaves.get(i), fruitLeaves.get().defaultBlockState());
        }
    }

    private static boolean isExposed(Context context, BlockPos pos) {
        return context.isAir(pos.above()) || context.isAir(pos.north()) || context.isAir(pos.south())
                || context.isAir(pos.east()) || context.isAir(pos.west());
    }
}
