package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.util.InventoryUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/** A consumable whose container is retained, including stacked food consumed by non-player entities. */
public class ContainerFoodItem extends Item {
    private final Supplier<? extends ItemLike> container;

    public ContainerFoodItem(Properties properties, Supplier<? extends ItemLike> container) {
        super(properties);
        this.container = container;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity consumer) {
        ItemStack result = super.finishUsingItem(stack, level, consumer);
        if (consumer instanceof Player player && player.getAbilities().instabuild) {
            return result;
        }
        ItemStack remainder = new ItemStack(container.get());
        if (result.isEmpty()) {
            return remainder;
        }
        InventoryUtils.giveOrDrop(consumer, remainder);
        return result;
    }
}
