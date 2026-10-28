package de.artemis.floraexpansion.common.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModTreeDecorators;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

public class OrchardCherryLeavesDecorator extends FruitLeavesDecorator {
    public static final OrchardCherryLeavesDecorator INSTANCE = new OrchardCherryLeavesDecorator();
    public static final MapCodec<OrchardCherryLeavesDecorator> CODEC = MapCodec.unit(INSTANCE);

    private OrchardCherryLeavesDecorator() {
        super(Blocks.CHERRY_LEAVES, () -> ModBlocks.ORCHARD_CHERRY_LEAVES.get(), 2, 30);
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return ModTreeDecorators.ORCHARD_CHERRY_LEAVES_DECORATOR.get();
    }
}
