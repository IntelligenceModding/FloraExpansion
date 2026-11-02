package de.artemis.floraexpansion.common.registry;
import net.minecraft.world.level.block.grower.TreeGrower;
import java.util.Optional;
public final class ModTreeGrowers {
 public static final TreeGrower CHERRY_PIT = new TreeGrower("floraexpansion:cherry_pit", Optional.empty(), Optional.of(ModTreeConfiguredFeatures.PLANTED_ORCHARD_CHERRY_TREE_KEY), Optional.of(ModTreeConfiguredFeatures.PLANTED_ORCHARD_CHERRY_TREE_KEY));
 public static final TreeGrower APPLE_CORE = new TreeGrower("floraexpansion:apple_core", .15f, Optional.empty(), Optional.empty(), Optional.of(ModTreeConfiguredFeatures.PLANTED_APPLE_TREE_KEY), Optional.of(ModTreeConfiguredFeatures.PLANTED_FANCY_APPLE_TREE_KEY), Optional.of(ModTreeConfiguredFeatures.PLANTED_APPLE_TREE_KEY), Optional.of(ModTreeConfiguredFeatures.PLANTED_FANCY_APPLE_TREE_KEY));
 private ModTreeGrowers() {}
}
