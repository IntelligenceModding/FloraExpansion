package de.artemis.floraexpansion.common.registry;

import net.minecraft.world.level.block.grower.TreeGrower;

import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;

public class ModTreeGrowers {
    public static final TreeGrower CHERRY_PIT = new TreeGrower(
            "floraexpansion:cherry_pit",
            WeightedList.of(ModTreeConfiguredFeatures.FRUITING_CHERRY_TREE_KEY),
            WeightedList.of(),
            WeightedList.of(ModTreeConfiguredFeatures.FRUITING_CHERRY_TREE_KEY),
            ModTreeConfiguredFeatures.FRUITING_CHERRY_TREE_KEY
    );

    public static final TreeGrower APPLE_CORE = new TreeGrower(
            "floraexpansion:apple_core",
            WeightedList.of(new Weighted<>(ModTreeConfiguredFeatures.FRUITING_OAK_TREE_KEY, 85),
                    new Weighted<>(ModTreeConfiguredFeatures.FANCY_FRUITING_OAK_TREE_KEY, 15)),
            WeightedList.of(),
            WeightedList.of(new Weighted<>(ModTreeConfiguredFeatures.FRUITING_OAK_TREE_KEY, 85),
                    new Weighted<>(ModTreeConfiguredFeatures.FANCY_FRUITING_OAK_TREE_KEY, 15)),
            ModTreeConfiguredFeatures.FRUITING_OAK_TREE_KEY
    );

    private ModTreeGrowers() {
    }
}
