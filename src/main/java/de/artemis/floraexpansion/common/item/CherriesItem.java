package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CherriesItem extends Item {

    public CherriesItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (!level.isClientSide()) {
            RandomSource random = level.getRandom();

            if (random.nextFloat() < 0.5f) {
                ItemStack cherryPit = new ItemStack(ModBlocks.CHERRY_PIT.get());

                if (livingEntity instanceof Player player) {
                    // Same general behavior as bowl-returning foods:
                    // try inventory first, drop if it doesn't fit.
                    if (!player.getInventory().add(cherryPit)) {
                        player.drop(cherryPit, false);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                } else if (level instanceof ServerLevel serverLevel) {
                    livingEntity.spawnAtLocation(serverLevel, cherryPit);
                }
            }
        }

        return result;
    }
}
