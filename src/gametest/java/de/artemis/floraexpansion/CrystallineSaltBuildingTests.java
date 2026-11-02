package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.WallSide;

import java.util.List;

final class CrystallineSaltBuildingTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(2, 3, 2));
        var blocks = List.of(ModBlocks.CRYSTALLINE_SALT_STAIRS.get(), ModBlocks.CRYSTALLINE_SALT_SLAB.get(), ModBlocks.CRYSTALLINE_SALT_WALL.get());
        for (var block : blocks) {
            var state = block.defaultBlockState();
            check(state.is(BlockTags.MINEABLE_WITH_PICKAXE) && state.requiresCorrectToolForDrops(), "Salt variant lacks pickaxe mining rules");
            check(state.setValue(BlockStateProperties.WATERLOGGED, true).getFluidState().is(FluidTags.WATER), "Salt variant cannot hold water");
            var drops = Block.getDrops(state, level, pos, null);
            check(drops.size() == 1 && drops.getFirst().is(block.asItem()) && drops.getFirst().getCount() == 1, "Salt variant does not drop itself");
        }
        check(ModBlocks.CRYSTALLINE_SALT_STAIRS.get().defaultBlockState().is(BlockTags.STAIRS)
                && ModBlocks.CRYSTALLINE_SALT_SLAB.get().defaultBlockState().is(BlockTags.SLABS)
                && ModBlocks.CRYSTALLINE_SALT_WALL.get().defaultBlockState().is(BlockTags.WALLS), "Salt building tags missing");
        var doubleSlab = ModBlocks.CRYSTALLINE_SALT_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        check(Block.getDrops(doubleSlab, level, pos, null).stream().filter(stack -> stack.is(ModBlocks.CRYSTALLINE_SALT_SLAB.get().asItem()))
                .mapToInt(ItemStack::getCount).sum() == 2, "Double salt slab lost a slab");
        level.setBlockAndUpdate(pos, ModBlocks.CRYSTALLINE_SALT_WALL.get().defaultBlockState());
        level.setBlockAndUpdate(pos.east(), Blocks.STONE.defaultBlockState());
        check(level.getBlockState(pos).getValue(BlockStateProperties.EAST_WALL) != WallSide.NONE, "Salt wall does not connect");

        var salt = new ItemStack(ModBlocks.CRYSTALLINE_SALT.get());
        List<CraftingInput> inputs = List.of(
                CraftingInput.of(3, 3, List.of(salt.copy(), ItemStack.EMPTY, ItemStack.EMPTY,
                        salt.copy(), salt.copy(), ItemStack.EMPTY, salt.copy(), salt.copy(), salt.copy())),
                CraftingInput.of(3, 1, List.of(salt.copy(), salt.copy(), salt.copy())),
                CraftingInput.of(3, 2, List.of(salt.copy(), salt.copy(), salt.copy(), salt.copy(), salt.copy(), salt.copy())));
        int[] craftingCounts = {4, 6, 6}, cuttingCounts = {1, 2, 1};
        String[] names = {"crystalline_salt_stairs", "crystalline_salt_slab", "crystalline_salt_wall"};
        for (int i = 0; i < blocks.size(); i++) {
            var crafting = (CraftingRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE,
                    Identifier.fromNamespaceAndPath(FloraExpansion.MODID, names[i]))).orElseThrow().value();
            check(crafting.matches(inputs.get(i), level) && crafting.assemble(inputs.get(i), level.registryAccess()).is(blocks.get(i).asItem())
                    && crafting.assemble(inputs.get(i), level.registryAccess()).getCount() == craftingCounts[i], "Salt crafting recipe incorrect: " + names[i]);
            var cutting = (SingleItemRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE,
                    Identifier.fromNamespaceAndPath(FloraExpansion.MODID, names[i] + "_from_crystalline_salt_stonecutting"))).orElseThrow().value();
            var input = new SingleRecipeInput(salt.copy());
            check(cutting.matches(input, level) && cutting.assemble(input, level.registryAccess()).is(blocks.get(i).asItem())
                    && cutting.assemble(input, level.registryAccess()).getCount() == cuttingCounts[i], "Salt stonecutting recipe incorrect: " + names[i]);
        }
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
    private CrystallineSaltBuildingTests() {}
}
