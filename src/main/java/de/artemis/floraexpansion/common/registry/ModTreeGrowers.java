package de.artemis.floraexpansion.common.registry;

import org.jetbrains.annotations.NotNull;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
public final class ModTreeGrowers {
 public static final AbstractTreeGrower CHERRY_PIT=new AbstractTreeGrower(){protected ResourceKey<ConfiguredFeature<?,?>> getConfiguredFeature(@NotNull RandomSource random,boolean bees){return ModTreeConfiguredFeatures.PLANTED_ORCHARD_CHERRY_TREE_KEY;}};
 public static final AbstractTreeGrower APPLE_CORE=new AbstractTreeGrower(){protected ResourceKey<ConfiguredFeature<?,?>> getConfiguredFeature(RandomSource random,boolean bees){return random.nextFloat()<.15F?ModTreeConfiguredFeatures.PLANTED_FANCY_APPLE_TREE_KEY:ModTreeConfiguredFeatures.PLANTED_APPLE_TREE_KEY;}};
 private ModTreeGrowers(){}
}
