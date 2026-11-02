package de.artemis.floraexpansion;

import de.artemis.floraexpansion.common.block.entity.HandMillBlockEntity;
import de.artemis.floraexpansion.common.inventory.HandMillMenu;
import de.artemis.floraexpansion.common.recipe.GrindingRecipe;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.GameType;

final class GrindingRecipeTests {
    static void verify(GameTestHelper helper) {
        var level = helper.getLevel();
        var salt = (GrindingRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salt_from_grinding"))).orElseThrow().value();
        check(salt.getType() == ModRecipes.GRINDING.get() && salt.getSerializer() == ModRecipes.GRINDING_SERIALIZER.get(), "Grinding type/serializer not registered");
        check(salt.turns() == 4 && salt.matches(new SingleRecipeInput(new ItemStack(ModItems.RAW_SALT.get())), level)
                && salt.assemble(new SingleRecipeInput(new ItemStack(ModItems.RAW_SALT.get())), level.registryAccess()).is(ModItems.SALT.get()), "Salt JSON recipe incorrect");
        var tagged = (GrindingRecipe) level.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE,
                Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "test_grinding_logs"))).orElseThrow().value();
        check(tagged.matches(new SingleRecipeInput(new ItemStack(Items.OAK_LOG)), level)
                && tagged.matches(new SingleRecipeInput(new ItemStack(Items.BIRCH_LOG)), level), "Tag ingredient failed");
        check(ModRecipes.isGrindingInput(level, new ItemStack(Items.OAK_LOG)), "Synchronized input property set ignored datapack recipe");
        var ops = level.registryAccess().createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE);
        var json = net.minecraft.world.item.crafting.Recipe.CODEC.encodeStart(ops, tagged).getOrThrow();
        var decoded = (GrindingRecipe) net.minecraft.world.item.crafting.Recipe.CODEC.parse(ops, json).getOrThrow();
        check(decoded.turns() == 2 && decoded.output().getCount() == 3, "JSON round-trip lost recipe data");
        var bad = json.getAsJsonObject().deepCopy();
        bad.addProperty("turns", 0);
        check(net.minecraft.world.item.crafting.Recipe.CODEC.parse(ops, bad).error().isPresent(), "Zero-turn recipe accepted");
        var buffer = new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(), level.registryAccess());
        try {
            Recipe.STREAM_CODEC.encode(buffer, tagged);
            var network = (GrindingRecipe) Recipe.STREAM_CODEC.decode(buffer);
            check(network.turns() == 2 && network.output().getCount() == 3 && network.matches(new SingleRecipeInput(new ItemStack(Items.BIRCH_LOG)), level),
                    "Network recipe synchronization lost ingredient/output/turns");
        } finally { buffer.release(); }
        var pos = helper.absolutePos(new BlockPos(2, 3, 2));
        level.setBlockAndUpdate(pos, ModBlocks.HAND_MILL.get().defaultBlockState());
        level.setBlockAndUpdate(pos.above(), ModBlocks.HAND_CRANK.get().defaultBlockState());
        var mill = (HandMillBlockEntity) level.getBlockEntity(pos);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var menu = (HandMillMenu) mill.createMenu(1, player.getInventory(), player);
        check(menu.slots.get(0).mayPlace(new ItemStack(Items.OAK_LOG)), "Menu still hardcodes raw salt");
        mill.setItem(0, new ItemStack(Items.OAK_LOG, 4));
        check(mill.crank(player) && menu.getRequiredTurns() == 2 && menu.getArrowWidth() == 12, "Arrow ignored recipe turn count");
        mill.setItem(1, new ItemStack(Items.STICK, 63));
        check(!mill.crank(player) && mill.getProgress() == 1 && mill.getItem(0).getCount() == 4 && mill.getItem(1).getCount() == 63,
                "Multi-item output overflow consumed input/progress");
        mill.removeItem(1, 63);
        check(mill.crank(player) && mill.getItem(0).getCount() == 3 && mill.getItem(1).is(Items.STICK)
                && mill.getItem(1).getCount() == 3 && menu.getArrowWidth() == 0, "Mill ignored datapack output/count");
        check(mill.crank(player), "Next batch did not start");
        var saved = mill.saveWithFullMetadata(level.registryAccess());
        var restored = (HandMillBlockEntity) net.minecraft.world.level.block.entity.BlockEntity.loadStatic(pos, level.getBlockState(pos), saved, level.registryAccess());
        level.setBlockEntity(restored);
        check(restored.crank(player) && restored.getItem(0).getCount() == 2 && restored.getItem(1).getCount() == 6,
                "Reload lost progress or variable output count");
    }
    private static void check(boolean condition, String message) { if (!condition) throw new IllegalStateException(message); }
}
