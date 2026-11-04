package de.artemis.floraexpansion.common.item;

import org.jetbrains.annotations.NotNull;

import de.artemis.floraexpansion.common.block.CrateBlock;
import net.minecraft.ChatFormatting;
import de.artemis.floraexpansion.common.util.ItemData;
import de.artemis.floraexpansion.common.util.ItemData.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import de.artemis.floraexpansion.common.util.ItemData.ItemContainerContents;

import net.minecraft.world.level.block.Block;

import de.artemis.floraexpansion.common.config.ClientConfig;

public class CrateBlockItem extends BlockItem {
    public CrateBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @org.jetbrains.annotations.Nullable net.minecraft.world.level.Level context, @NotNull java.util.List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if (ClientConfig.hints()) tooltipComponents.add(Component.translatable("tooltip.floraexpansion.crate_controls").withStyle(ChatFormatting.GRAY));
        if (!CrateBlock.isPackedItem(stack)) {
            return;
        }

        ItemContainerContents contents = ItemData.getOrDefault(stack, DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStack storedType = ItemStack.EMPTY;
        int totalCount = 0;

        for (ItemStack contentStack : contents.nonEmptyStream().toList()) {
            if (storedType.isEmpty()) {
                storedType = contentStack.copyWithCount(1);
            }

            totalCount += contentStack.getCount();
        }

        if (!storedType.isEmpty() && totalCount > 0) {
            if (ClientConfig.expanded(tooltipFlag)) {
                int capacity = de.artemis.floraexpansion.common.block.entity.CrateBlockEntity.CONTAINER_SIZE * storedType.getMaxStackSize();
                tooltipComponents.add(Component.translatable("tooltip.floraexpansion.crate_capacity", totalCount, capacity).withStyle(ChatFormatting.GRAY));
            }
            MutableComponent storedName = Component.empty().append(storedType.getHoverName()).withStyle(storedType.getRarity().getStyleModifier());
            if (ItemData.has(storedType, DataComponents.CUSTOM_NAME)) {
                storedName.withStyle(ChatFormatting.ITALIC);
            }

            tooltipComponents.add(Component.literal(totalCount + " ").withStyle(ChatFormatting.GRAY)
                    .append(storedName)
                    .append(Component.translatable("tooltip.floraexpansion.crate_contents_suffix").withStyle(ChatFormatting.GRAY)));
        }
    }
}
