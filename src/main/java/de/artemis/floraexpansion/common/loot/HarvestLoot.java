package de.artemis.floraexpansion.common.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public final class HarvestLoot {
    public static final ResourceKey<net.minecraft.world.level.storage.loot.LootTable> CACTUS_STRIPPING = ResourceKey.create(Registries.LOOT_TABLE,
            Identifier.fromNamespaceAndPath("floraexpansion", "harvest/cactus_stripping"));
    private HarvestLoot() {}

    public static List<ItemStack> drops(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
        var block = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        var key = ResourceKey.create(Registries.LOOT_TABLE,
                Identifier.fromNamespaceAndPath(block.getNamespace(), "harvest/" + block.getPath()));
        return drops(level, pos, state, player, tool, key);
    }

    public static List<ItemStack> drops(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack tool,
                                        ResourceKey<net.minecraft.world.level.storage.loot.LootTable> key) {
        var params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.BLOCK_STATE, state)
                .withParameter(LootContextParams.TOOL, tool)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.BLOCK);
        return level.getServer().reloadableRegistries().getLootTable(key).getRandomItems(params, level.getRandom());
    }
}
