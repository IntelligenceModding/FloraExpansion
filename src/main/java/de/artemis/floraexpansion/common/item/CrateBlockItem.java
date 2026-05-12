package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.block.CrateBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class CrateBlockItem extends BlockItem {
    public CrateBlockItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        super.verifyComponentsAfterLoad(stack);

        if (CrateBlock.isPackedItem(stack) && !stack.has(DataComponents.CUSTOM_MODEL_DATA)) {
            CrateBlock.setPackedItem(stack, true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        if (!CrateBlock.isPackedItem(stack)) {
            return;
        }

        ItemContainerContents contents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStack storedType = ItemStack.EMPTY;
        int totalCount = 0;

        for (ItemStack contentStack : contents.nonEmptyItems()) {
            if (storedType.isEmpty()) {
                storedType = contentStack.copyWithCount(1);
            }

            totalCount += contentStack.getCount();
        }

        if (!storedType.isEmpty() && totalCount > 0) {
            MutableComponent storedName = Component.empty().append(storedType.getHoverName()).withStyle(storedType.getRarity().color());
            if (storedType.has(DataComponents.CUSTOM_NAME)) {
                storedName.withStyle(ChatFormatting.ITALIC);
            }

            tooltipComponents.add(Component.literal(totalCount + " ").withStyle(ChatFormatting.GRAY)
                    .append(storedName)
                    .append(Component.translatable("tooltip.floraexpansion.crate_contents_suffix").withStyle(ChatFormatting.GRAY)));
        }
    }
}
