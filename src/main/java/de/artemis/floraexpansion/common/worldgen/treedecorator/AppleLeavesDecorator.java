package de.artemis.floraexpansion.common.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModTreeDecorators;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

public class AppleLeavesDecorator extends FruitLeavesDecorator {
    public static final AppleLeavesDecorator INSTANCE = new AppleLeavesDecorator();
    public static final MapCodec<AppleLeavesDecorator> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.<AppleLeavesDecorator>mapCodec(instance -> instance.group(
            com.mojang.serialization.Codec.intRange(1, 256).optionalFieldOf("replacement_divisor", 10).forGetter(AppleLeavesDecorator::replacementDivisor),
            com.mojang.serialization.Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_replacements", 8).forGetter(AppleLeavesDecorator::maxReplacements),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("exposed_only", true).forGetter(AppleLeavesDecorator::exposedOnly)
    ).apply(instance, AppleLeavesDecorator::new));

    private AppleLeavesDecorator() {
        this(10, 8);
    }

    public AppleLeavesDecorator(int divisor, int maximum) {
        this(divisor, maximum, true);
    }

    public AppleLeavesDecorator(int divisor, int maximum, boolean exposedOnly) {
        super(Blocks.OAK_LEAVES, () -> ModBlocks.APPLE_LEAVES.get(), divisor, maximum, exposedOnly);
    }

    @Override
    protected @NotNull TreeDecoratorType<?> type() {
        return ModTreeDecorators.APPLE_LEAVES_DECORATOR.get();
    }
}
