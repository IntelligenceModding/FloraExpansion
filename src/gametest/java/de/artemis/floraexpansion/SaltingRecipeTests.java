package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.SaltingTubBlockEntity;
import de.artemis.floraexpansion.common.recipe.SaltingRecipe;
import de.artemis.floraexpansion.common.recipe.SaltingRecipeInput;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import de.artemis.floraexpansion.common.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.List;

final class SaltingRecipeTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var recipes = level.recipeAccess().recipeMap().byType(ModRecipes.SALTING.get());
        check(recipes.stream().filter(holder -> !holder.id().identifier().getPath().startsWith("test_")).count() == 5,
                "Expected five default salting recipes");
        var recipe = (SaltingRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "test_salting_potatoes"))).orElseThrow().value();
        var input = new SaltingRecipeInput(java.util.stream.IntStream.range(0, 4).mapToObj(i -> new ItemStack(Items.POTATO)).toList(), new ItemStack(ModItems.RAW_SALT.get()));
        check(recipe.getType() == ModRecipes.SALTING.get() && recipe.getSerializer() == ModRecipes.SALTING_SERIALIZER.get()
                && recipe.matches(input, level) && recipe.assemble(input).getCount() == 5, "Salting recipe registration/matching failed");
        check(!recipe.matches(new SaltingRecipeInput(input.food(), new ItemStack(ModItems.SALT.get())), level)
                && !recipe.matches(new SaltingRecipeInput(List.of(input.food().getFirst()), input.salt()), level),
                "Wrong salt or layer count accepted");
        check(ModRecipes.isSaltingFood(level, new ItemStack(Items.POTATO)) && ModRecipes.isSaltingSalt(level, input.salt()),
                "Recipe ingredient property sets are not synchronized");
        var ops = level.registryAccess().createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE);
        var json = net.minecraft.world.item.crafting.Recipe.CODEC.encodeStart(ops, recipe).getOrThrow();
        var decoded = (SaltingRecipe) net.minecraft.world.item.crafting.Recipe.CODEC.parse(ops, json).getOrThrow();
        check(decoded.matches(input, level) && decoded.layers() == 4 && decoded.processingTime() == 40 && decoded.result().count() == 5, "JSON lost salting fields");
        for (int badTime : new int[]{0, 72001}) {
            var bad = json.getAsJsonObject().deepCopy(); bad.addProperty("processing_time", badTime);
            check(net.minecraft.world.item.crafting.Recipe.CODEC.parse(ops, bad).error().isPresent(), "Invalid salting processing time accepted");
        }
        var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), level.registryAccess());
        try {
            Recipe.STREAM_CODEC.encode(buffer, recipe);
            var network = (SaltingRecipe) Recipe.STREAM_CODEC.decode(buffer);
            check(network.matches(input, level) && network.layers() == 4 && network.processingTime() == 40 && network.result().count() == 5,
                    "Network synchronization lost salting fields");
        } finally { buffer.release(); }
        var pos = helper.absolutePos(new BlockPos(2, 3, 2));
        level.setBlockAndUpdate(pos, ModBlocks.SALTING_TUB.get().defaultBlockState());
        var tub = (SaltingTubBlockEntity) level.getBlockEntity(pos);
        check(tub.addFood(new ItemStack(Items.POTATO)) && !tub.salt(input.salt()), "Partial custom batch accepted salt");
        for (int layer=1; layer<4; layer++) check(tub.addFood(new ItemStack(Items.POTATO)), "Custom ingredient rejected");
        check(!tub.addFood(new ItemStack(Items.POTATO)), "Four-layer limit ignored");
        check(!tub.salt(new ItemStack(ModItems.SALT.get())) && tub.salt(input.salt()), "Custom salt ingredient ignored");
        SaltingTubTests.advance(tub, 13);
        var saved = tub.saveWithFullMetadata(level.registryAccess());
        var restored = (SaltingTubBlockEntity) BlockEntity.loadStatic(pos, level.getBlockState(pos), saved, level.registryAccess());
        level.setBlockEntity(restored);
        check(restored.remainingTicks() == 27 && restored.take(false).isEmpty(), "Custom countdown did not persist");
        SaltingTubTests.advance(restored, 27);
        var result = restored.take(false);
        check(result.size() == 1 && result.getFirst().is(Items.BAKED_POTATO) && result.getFirst().getCount() == 5
                && restored.layers() == 0, "Saved variable output was reduced to the layer count");
    }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
    private SaltingRecipeTests() {}
}
