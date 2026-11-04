package de.artemis.floraexpansion.common.worldgen;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/** Rehosts existing vanilla ore deposits without changing their distribution or replacing modded ores. */
public final class CalciteOreVariants {
    private static final Map<Block, RegistryObject<? extends Block>> VARIANTS = Map.ofEntries(
            Map.entry(Blocks.COAL_ORE, ModBlocks.CALCITE_COAL_ORE), Map.entry(Blocks.DEEPSLATE_COAL_ORE, ModBlocks.CALCITE_COAL_ORE),
            Map.entry(Blocks.IRON_ORE, ModBlocks.CALCITE_IRON_ORE), Map.entry(Blocks.DEEPSLATE_IRON_ORE, ModBlocks.CALCITE_IRON_ORE),
            Map.entry(Blocks.COPPER_ORE, ModBlocks.CALCITE_COPPER_ORE), Map.entry(Blocks.DEEPSLATE_COPPER_ORE, ModBlocks.CALCITE_COPPER_ORE),
            Map.entry(Blocks.GOLD_ORE, ModBlocks.CALCITE_GOLD_ORE), Map.entry(Blocks.DEEPSLATE_GOLD_ORE, ModBlocks.CALCITE_GOLD_ORE),
            Map.entry(Blocks.REDSTONE_ORE, ModBlocks.CALCITE_REDSTONE_ORE), Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, ModBlocks.CALCITE_REDSTONE_ORE),
            Map.entry(Blocks.EMERALD_ORE, ModBlocks.CALCITE_EMERALD_ORE), Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, ModBlocks.CALCITE_EMERALD_ORE),
            Map.entry(Blocks.LAPIS_ORE, ModBlocks.CALCITE_LAPIS_ORE), Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, ModBlocks.CALCITE_LAPIS_ORE),
            Map.entry(Blocks.DIAMOND_ORE, ModBlocks.CALCITE_DIAMOND_ORE), Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, ModBlocks.CALCITE_DIAMOND_ORE));

    public static @Nullable BlockState replacement(BlockState original) {
        var variant = VARIANTS.get(original.getBlock());
        if (variant == null) return null;
        var result = variant.get().defaultBlockState();
        return original.hasProperty(BlockStateProperties.LIT)
                ? result.setValue(BlockStateProperties.LIT, original.getValue(BlockStateProperties.LIT)) : result;
    }

    private CalciteOreVariants() {}
}
