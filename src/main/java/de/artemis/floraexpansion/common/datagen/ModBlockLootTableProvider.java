package de.artemis.floraexpansion.common.datagen;

import de.artemis.floraexpansion.common.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {

    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        ArrayList<Block> registeredBlocks = new ArrayList<Block>();

        return registeredBlocks;
    }
}
