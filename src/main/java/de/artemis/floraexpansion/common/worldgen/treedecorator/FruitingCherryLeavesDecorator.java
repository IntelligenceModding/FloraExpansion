package de.artemis.floraexpansion.common.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FruitingCherryLeavesDecorator extends TreeDecorator {

    public static final FruitingCherryLeavesDecorator INSTANCE = new FruitingCherryLeavesDecorator();
    public static final MapCodec<FruitingCherryLeavesDecorator> CODEC = MapCodec.unit(INSTANCE);

    private FruitingCherryLeavesDecorator() {
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return ModTreeDecorators.FRUITING_CHERRY_LEAVES_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        List<BlockPos> validLeaves = new ArrayList<>();

        for (BlockPos pos : context.leaves()) {
            if (!context.level().isStateAtPosition(pos, state -> state.getBlock() == Blocks.CHERRY_LEAVES)) {
                continue;
            }

            if (!isExposed(context, pos)) {
                continue;
            }

            validLeaves.add(pos.immutable());
        }

        if (validLeaves.isEmpty()) {
            return;
        }

        RandomSource random = context.random();
        Collections.shuffle(validLeaves, new java.util.Random(random.nextLong()));

        int replaceCount = Math.max(1, validLeaves.size() / 2);
        replaceCount = Math.min(replaceCount, 30);

        for (int i = 0; i < replaceCount && i < validLeaves.size(); i++) {
            BlockPos pos = validLeaves.get(i);
            context.setBlock(pos, ModBlocks.FRUITING_CHERRY_LEAVES.get().defaultBlockState());
        }
    }

    private boolean isExposed(Context context, BlockPos pos) {
        return context.isAir(pos.above())
                || context.isAir(pos.north())
                || context.isAir(pos.south())
                || context.isAir(pos.east())
                || context.isAir(pos.west());
    }
}