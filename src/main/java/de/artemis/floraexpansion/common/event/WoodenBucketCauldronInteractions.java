package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.common.config.ServerConfig;
import de.artemis.floraexpansion.common.item.WoodenBucketUtil;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


/** Vanilla cauldron rules, with wooden-bucket transformations instead of iron buckets. */
public final class WoodenBucketCauldronInteractions {
    @SubscribeEvent
    public static void register(net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(WoodenBucketCauldronInteractions::registerInteractions);
    }
    private static void registerInteractions() {
        CauldronInteraction.WATER.put(ModItems.WOODEN_BUCKET.get(), (state, level, pos, player, hand, stack) ->
                isFull(state) ? fill(level,pos,player,hand,stack,ModItems.WOODEN_WATER_BUCKET.get(),SoundEvents.BUCKET_FILL)
                        : InteractionResult.PASS);
        CauldronInteraction.POWDER_SNOW.put(ModItems.WOODEN_BUCKET.get(), (state, level, pos, player, hand, stack) ->
                isFull(state) ? fill(level,pos,player,hand,stack,ModItems.WOODEN_POWDER_SNOW_BUCKET.get(),SoundEvents.BUCKET_FILL_POWDER_SNOW)
                        : InteractionResult.PASS);
        CauldronInteraction.LAVA.put(ModItems.WOODEN_BUCKET.get(), (state, level, pos, player, hand, stack) ->
                ServerConfig.lavaEnabled.get() ? fill(level,pos,player,hand,stack,ModItems.WOODEN_LAVA_BUCKET.get(),SoundEvents.BUCKET_FILL_LAVA)
                        : InteractionResult.PASS);

        // Includes custom cauldrons, just as vanilla's default filled-bucket interactions do.
        SaltWaterInteractions.registerToAll(ModItems.WOODEN_WATER_BUCKET.get(), (state, level, pos, player, hand, stack) ->
                pour(level,pos,player,hand,stack,Blocks.WATER_CAULDRON.defaultBlockState()
                        .setValue(LayeredCauldronBlock.LEVEL,3),SoundEvents.BUCKET_EMPTY));
        SaltWaterInteractions.registerToAll(ModItems.WOODEN_LAVA_BUCKET.get(), (state, level, pos, player, hand, stack) ->
                isUnderWater(level,pos) ? InteractionResult.CONSUME :
                        pour(level,pos,player,hand,stack,Blocks.LAVA_CAULDRON.defaultBlockState(),SoundEvents.BUCKET_EMPTY_LAVA));
        SaltWaterInteractions.registerToAll(ModItems.WOODEN_POWDER_SNOW_BUCKET.get(), (state, level, pos, player, hand, stack) ->
                isUnderWater(level,pos) ? InteractionResult.CONSUME :
                        pour(level,pos,player,hand,stack,Blocks.POWDER_SNOW_CAULDRON.defaultBlockState()
                                .setValue(LayeredCauldronBlock.LEVEL,3),SoundEvents.BUCKET_EMPTY_POWDER_SNOW));
    }
    public static InteractionResult fill(Level level, BlockPos pos, Player player, InteractionHand hand,
                                         ItemStack stack, Item filledItem, SoundEvent sound) {
        if (!level.isClientSide) {
            var filled = WoodenBucketUtil.transformInto(stack,player,filledItem, result -> {
                if (filledItem == ModItems.WOODEN_LAVA_BUCKET.get())
                    WoodenBucketUtil.initializeLavaTimeout(result,level.getGameTime(),level.getRandom());
            });
            player.setItemInHand(hand,filled);
            level.setBlock(pos,Blocks.CAULDRON.defaultBlockState(), 3);
            changed(level,pos,player,stack.getItem(),false,sound);
        }
        return InteractionResult.SUCCESS;
    }
    public static InteractionResult pour(Level level, BlockPos pos, Player player, InteractionHand hand,
                                         ItemStack stack, BlockState target, SoundEvent sound) {
        if (!level.isClientSide) {
            player.setItemInHand(hand,WoodenBucketUtil.damageInto(stack,player,ModItems.WOODEN_BUCKET.get()));
            level.setBlock(pos,target, 3);
            changed(level,pos,player,stack.getItem(),true,sound);
        }
        return InteractionResult.SUCCESS;
    }
    private static void changed(Level level, BlockPos pos, Player player, Item used, boolean pour, SoundEvent sound) {
        player.awardStat(pour ? Stats.FILL_CAULDRON : Stats.USE_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(used));
        level.playSound(null,pos,sound,SoundSource.BLOCKS,1F,1F);
        level.gameEvent(null,pour ? GameEvent.FLUID_PLACE : GameEvent.FLUID_PICKUP,pos);
    }
    private static boolean isFull(BlockState state) { return state.getValue(LayeredCauldronBlock.LEVEL) == 3; }
    private static boolean isUnderWater(Level level, BlockPos pos) { return level.getFluidState(pos.above()).is(FluidTags.WATER); }
    private WoodenBucketCauldronInteractions() {}
}
