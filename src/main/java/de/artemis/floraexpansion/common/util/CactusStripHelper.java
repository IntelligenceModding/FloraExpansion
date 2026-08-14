package de.artemis.floraexpansion.common.util;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class CactusStripHelper {
    private static final float BASE_THORN_DROP_CHANCE_ON_STRIP = 0.20F;
    private static final float FORTUNE_BONUS_PER_LEVEL = 0.10F;

    private CactusStripHelper() {
    }

    public static InteractionResult stripAndDropThorn(Level level, BlockPos pos, Player player,
                                                          InteractionHand hand, ItemStack tool,
                                                          BlockState strippedState) {
        if (!level.isClientSide()) {
            level.setBlock(pos, strippedState, Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

            Holder<Enchantment> fortune =
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
            int fortuneLevel = tool.getEnchantmentLevel(fortune);
            float thornDropChance = BASE_THORN_DROP_CHANCE_ON_STRIP + (fortuneLevel * FORTUNE_BONUS_PER_LEVEL);

            if (level.random.nextFloat() < thornDropChance) {
                Block.popResource(level, pos, new ItemStack(ModBlocks.CACTUS_THORN.get()));
            }

            tool.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        return ((level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER));
    }
}

