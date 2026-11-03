package de.artemis.floraexpansion.common.item;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.util.InventoryUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import de.artemis.floraexpansion.common.block.JamJarBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import net.minecraft.ChatFormatting;

import java.util.function.Supplier;

public class ContainerFoodBlockItem extends BlockItem {
    @NotNull @Override public String getDescriptionId() { return net.minecraft.Util.makeDescriptionId("item", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this)); }
    private final Supplier<? extends ItemLike> container;

    public ContainerFoodBlockItem(Block block, Properties properties, Supplier<? extends ItemLike> container) {
        super(block, properties);
        this.container = container;
    }

    @NotNull @Override public net.minecraft.world.item.UseAnim getUseAnimation(@NotNull ItemStack stack) { return net.minecraft.world.item.UseAnim.DRINK; }
    @NotNull @Override public net.minecraft.sounds.SoundEvent getDrinkingSound() { return net.minecraft.sounds.SoundEvents.HONEY_DRINK; }
    @Override
    @NotNull public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity consumer) {
        if (getBlock() instanceof JamJarBlock) {
            int portions = JamJarBlock.itemPortions(stack);
            ItemStack bite = stack.copyWithCount(1);
            bite.set(DataComponents.FOOD, JamJarBlock.foodForBite(portions));
            super.finishUsingItem(bite, level, consumer);
            if (consumer instanceof Player player && player.getAbilities().instabuild) return stack;

            if (portions == 1) {
                stack.shrink(1);
                return InventoryUtils.finishWithContainer(stack, consumer, container.get());
            }

            ItemStack unopened = stack.copyWithCount(stack.getCount() - 1);
            stack.setCount(1);
            stack.set(DataComponents.BLOCK_STATE, stack.getOrDefault(DataComponents.BLOCK_STATE,
                    BlockItemStateProperties.EMPTY).with(JamJarBlock.PORTIONS, portions - 1));
            stack.set(DataComponents.FOOD, JamJarBlock.foodForPortions(portions - 1));
            InventoryUtils.giveOrDrop(consumer, unopened);
            return stack;
        }
        return InventoryUtils.finishWithContainer(super.finishUsingItem(stack, level, consumer), consumer, container.get());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull java.util.List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (getBlock() instanceof JamJarBlock) tooltip.add(Component.translatable("tooltip.floraexpansion.jar_portions",
                JamJarBlock.itemPortions(stack), JamJarBlock.MAX_PORTIONS).withStyle(ChatFormatting.GRAY));
    }
}
