package de.artemis.floraexpansion.common.worldgen;

import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower CHERRY_PIT = new TreeGrower(
            "floraexpansion:cherry_pit",
            Optional.empty(),
            Optional.of(ModTreeConfiguredFeatures.CHERRY_STONE_TEST_TREE_KEY),
            Optional.of(ModTreeConfiguredFeatures.CHERRY_STONE_TEST_TREE_KEY)
    );

    private ModTreeGrowers() {
    }
}