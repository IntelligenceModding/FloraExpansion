package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.util.InventoryUtils;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import de.artemis.floraexpansion.common.config.ServerConfig;

public class CherriesItem extends Item {

    public CherriesItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (!level.isClientSide()) {
            RandomSource random = level.getRandom();

            if (random.nextFloat() < ServerConfig.cherryPitChance.get()) {
                ItemStack cherryPit = new ItemStack(ModBlocks.CHERRY_PIT.get());

                InventoryUtils.giveOrDrop(livingEntity, cherryPit);
                if (livingEntity instanceof Player player) {
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }

        return result;
    }
}
