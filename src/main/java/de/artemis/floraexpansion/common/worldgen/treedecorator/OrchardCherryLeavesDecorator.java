package de.artemis.floraexpansion.common.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModTreeDecorators;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

public class OrchardCherryLeavesDecorator extends FruitLeavesDecorator {
    public static final OrchardCherryLeavesDecorator INSTANCE = new OrchardCherryLeavesDecorator();
    public static final MapCodec<OrchardCherryLeavesDecorator> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.<OrchardCherryLeavesDecorator>mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.intRange(1, 256).optionalFieldOf("replacement_divisor", 2).forGetter(OrchardCherryLeavesDecorator::replacementDivisor),
            com.mojang.serialization.Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_replacements", 30).forGetter(OrchardCherryLeavesDecorator::maxReplacements),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("exposed_only", true).forGetter(OrchardCherryLeavesDecorator::exposedOnly)
    ).apply(instance, OrchardCherryLeavesDecorator::new));

    private OrchardCherryLeavesDecorator() {
        this(2, 30);
    }

    public OrchardCherryLeavesDecorator(int divisor, int maximum) {
        this(divisor, maximum, true);
    }

    public OrchardCherryLeavesDecorator(int divisor, int maximum, boolean exposedOnly) {
        super(Blocks.CHERRY_LEAVES, () -> ModBlocks.ORCHARD_CHERRY_LEAVES.get(), divisor, maximum, exposedOnly);
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return ModTreeDecorators.ORCHARD_CHERRY_LEAVES_DECORATOR.get();
    }
}
