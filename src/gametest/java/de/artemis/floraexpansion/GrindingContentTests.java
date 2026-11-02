package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.GameType;

final class GrindingContentTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var pos = helper.absolutePos(new BlockPos(2, 3, 2));
        level.setBlockAndUpdate(pos, ModBlocks.HAND_MILL.get().defaultBlockState());
        level.setBlockAndUpdate(pos.above(), ModBlocks.HAND_CRANK.get().defaultBlockState());
        var mill = (HandMillBlockEntity) level.getBlockEntity(pos);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var seen = new java.util.HashSet<net.minecraft.world.item.Item>();
        int count = 0;
        for (var holder : level.recipeAccess().recipeMap().byType(ModRecipes.GRINDING.get())) {
            var id = holder.id().identifier();
            if (!id.getNamespace().equals(FloraExpansion.MODID) || id.getPath().startsWith("test_")) continue;
            var recipe = holder.value();
            var inputs = recipe.input().items().toList();
            check(inputs.size() == 1, "Default grinding recipe has ambiguous ingredients: " + id);
            var item = inputs.getFirst().value();
            check(seen.add(item), "Multiple default grinding recipes compete for input: " + item);
            var input = new ItemStack(item);
            check(mill.canPlaceItem(0, input), "Grinding recipe is not accepted by mill: " + id);
            mill.setItem(0, input);
            mill.setItem(1, ItemStack.EMPTY);
            var expected = recipe.assemble(new SingleRecipeInput(input));
            for (int turn = 1; turn <= recipe.turns(); turn++) {
                check(mill.crank(player), "Mill could not process recipe: " + id);
                if (turn < recipe.turns()) check(mill.getItem(1).isEmpty(), "Recipe completed too soon: " + id);
            }
            check(mill.getItem(0).isEmpty(), "Grinding did not consume exactly one input: " + id);
            check(ItemStack.isSameItemSameComponents(mill.getItem(1), expected)
                    && mill.getItem(1).getCount() == expected.getCount(), "Wrong grinding output: " + id);
            count++;
        }
        check(count == 60, "Expected 60 default grinding recipes; loaded " + count);
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
}
