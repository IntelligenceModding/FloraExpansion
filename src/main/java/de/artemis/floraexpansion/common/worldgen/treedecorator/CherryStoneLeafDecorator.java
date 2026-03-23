package de.artemis.floraexpansion.common.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CherryStoneLeafDecorator extends TreeDecorator {

    public static final CherryStoneLeafDecorator INSTANCE = new CherryStoneLeafDecorator();
    public static final MapCodec<CherryStoneLeafDecorator> CODEC = MapCodec.unit(INSTANCE);

    private CherryStoneLeafDecorator() {
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecorators.CHERRY_STONE_LEAF_DECORATOR.get();
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