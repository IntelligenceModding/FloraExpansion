package de.artemis.floraexpansion.common.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.registry.ModTreeDecorators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FruitingOakLeavesDecorator extends TreeDecorator {

    public static final FruitingOakLeavesDecorator INSTANCE = new FruitingOakLeavesDecorator();
    public static final MapCodec<FruitingOakLeavesDecorator> CODEC = MapCodec.unit(INSTANCE);

    private FruitingOakLeavesDecorator() {
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return ModTreeDecorators.FRUITING_OAK_LEAVES_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        List<BlockPos> validLeaves = new ArrayList<>();

        for (BlockPos pos : context.leaves()) {
            if (!context.level().isStateAtPosition(pos, state -> state.getBlock() == Blocks.OAK_LEAVES)) {
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

        int replaceCount = Math.max(1, validLeaves.size() / 10);
        replaceCount = Math.min(replaceCount, 8);

        for (int i = 0; i < replaceCount && i < validLeaves.size(); i++) {
            BlockPos pos = validLeaves.get(i);
            context.setBlock(pos, ModBlocks.FRUITING_OAK_LEAVES.get().defaultBlockState());
        }
    }

    private boolean isExposed(Context context, BlockPos blockPos) {
        return context.isAir(blockPos.above())
                || context.isAir(blockPos.north())
                || context.isAir(blockPos.south())
                || context.isAir(blockPos.east())
                || context.isAir(blockPos.west());
    }
}

