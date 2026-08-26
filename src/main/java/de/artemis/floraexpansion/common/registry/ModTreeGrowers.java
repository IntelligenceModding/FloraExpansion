package de.artemis.floraexpansion.common.registry;

import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower CHERRY_PIT = new TreeGrower(
            "floraexpansion:cherry_pit",
            Optional.empty(),
            Optional.of(ModTreeConfiguredFeatures.FRUITING_CHERRY_TREE_KEY),
            Optional.of(ModTreeConfiguredFeatures.FRUITING_CHERRY_TREE_KEY)
    );

    public static final TreeGrower APPLE_CORE = new TreeGrower(
            "floraexpansion:apple_core",
            0.15F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(ModTreeConfiguredFeatures.FRUITING_OAK_TREE_KEY),
            Optional.of(ModTreeConfiguredFeatures.FANCY_FRUITING_OAK_TREE_KEY),
            Optional.of(ModTreeConfiguredFeatures.FRUITING_OAK_TREE_KEY),
            Optional.of(ModTreeConfiguredFeatures.FANCY_FRUITING_OAK_TREE_KEY)
    );

    private ModTreeGrowers() {
    }
}
