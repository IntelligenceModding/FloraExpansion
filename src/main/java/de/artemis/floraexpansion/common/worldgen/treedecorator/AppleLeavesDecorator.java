package de.artemis.floraexpansion.common.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModTreeDecorators;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

public class AppleLeavesDecorator extends FruitLeavesDecorator {
    public static final AppleLeavesDecorator INSTANCE = new AppleLeavesDecorator();
    public static final MapCodec<AppleLeavesDecorator> CODEC = MapCodec.unit(INSTANCE);

    private AppleLeavesDecorator() {
        super(Blocks.OAK_LEAVES, () -> ModBlocks.APPLE_LEAVES.get(), 10, 8);
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return ModTreeDecorators.APPLE_LEAVES_DECORATOR.get();
    }
}
