package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.util.InventoryUtils;
import net.minecraft.world.entity.LivingEntity;
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
        return InventoryUtils.finishWithContainer(super.finishUsingItem(stack, level, consumer), consumer, container.get());
    }
}
