package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public final class SaltWaterInteractions {
    public static final CauldronInteraction.InteractionMap CAULDRON = CauldronInteraction.newInteractionMap("floraexpansion:salt_water");
    private static void interactions() {
        // Register the custom leather armor with vanilla washing interactions.
        for (var armor : java.util.List.of(ModItems.SPIKED_LEATHER_HELMET.get(), ModItems.SPIKED_LEATHER_CHESTPLATE.get(), ModItems.SPIKED_LEATHER_LEGGINGS.get(), ModItems.SPIKED_LEATHER_BOOTS.get()))
            CauldronInteraction.WATER.map().put(armor, CauldronInteraction.WATER.map().get(Items.LEATHER_CHESTPLATE));
        // Copy washing handlers only: freshwater bottles/potions must never collect or dilute brine.
        var armorItems = java.util.Set.of(Items.LEATHER_BOOTS, Items.LEATHER_LEGGINGS, Items.LEATHER_CHESTPLATE, Items.LEATHER_HELMET, Items.LEATHER_HORSE_ARMOR, Items.WOLF_ARMOR,
                ModItems.SPIKED_LEATHER_HELMET.get(), ModItems.SPIKED_LEATHER_CHESTPLATE.get(), ModItems.SPIKED_LEATHER_LEGGINGS.get(), ModItems.SPIKED_LEATHER_BOOTS.get());
        CauldronInteraction.WATER.map().forEach((item, interaction) -> {
            if (armorItems.contains(item) || item instanceof net.minecraft.world.item.BannerItem
                    || item instanceof net.minecraft.world.item.BlockItem block && block.getBlock() instanceof net.minecraft.world.level.block.ShulkerBoxBlock)
                CAULDRON.map().put(item, interaction);
        });
        for (var filled : java.util.List.of(Items.WATER_BUCKET, Items.LAVA_BUCKET, Items.POWDER_SNOW_BUCKET))
            CAULDRON.map().put(filled, CauldronInteraction.EMPTY.map().get(filled));
        CauldronInteraction.WATER.map().put(ModItems.SALT.get(), SaltWaterInteractions::saltWater);
        SaltWaterInteractions.registerToAll(ModItems.SALT_WATER_BUCKET.get(), SaltWaterInteractions::pourBucket);
        SaltWaterInteractions.registerToAll(ModItems.WOODEN_SALT_WATER_BUCKET.get(), SaltWaterInteractions::pourBucket);
        CAULDRON.map().put(Items.BUCKET, (state, level, pos, player, hand, stack) -> CauldronInteraction.fillBucket(
                state, level, pos, player, hand, stack, new ItemStack(ModItems.SALT_WATER_BUCKET.get()),
                s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
        CAULDRON.map().put(ModItems.WOODEN_BUCKET.get(), SaltWaterInteractions::fillWoodenBucket);

    }
    private static ItemInteractionResult saltWater(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!state.is(Blocks.WATER_CAULDRON)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!level.isClientSide) {
            level.setBlock(pos, ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState()
                    .setValue(LayeredCauldronBlock.LEVEL, state.getValue(LayeredCauldronBlock.LEVEL)), 3);
            if (!player.hasInfiniteMaterials()) stack.shrink(1);
            de.artemis.floraexpansion.common.util.InventoryUtils.syncPlayerInventory(player);
            changed(level, pos, player, ModItems.SALT.get(), true, SoundEvents.SAND_PLACE);
        }
        return ItemInteractionResult.SUCCESS;
    }
    @SubscribeEvent public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            interactions();
            DispenserBlock.registerBehavior(ModItems.SALT_WATER_BUCKET.get(), DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET));
        });
    }
    private static ItemInteractionResult pourBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        var full = ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
        if (stack.is(ModItems.SALT_WATER_BUCKET.get()))
            return CauldronInteraction.emptyBucket(level, pos, player, hand, stack, full, SoundEvents.BUCKET_EMPTY);
        return WoodenBucketCauldronInteractions.pour(level,pos,player,hand,stack,full,SoundEvents.BUCKET_EMPTY);
    }
    private static ItemInteractionResult fillWoodenBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return WoodenBucketCauldronInteractions.fill(level,pos,player,hand,stack,
                ModItems.WOODEN_SALT_WATER_BUCKET.get(),SoundEvents.BUCKET_FILL);
    }
    private static void changed(Level level, BlockPos pos, Player player, net.minecraft.world.item.Item used, boolean pour, net.minecraft.sounds.SoundEvent sound) {
        player.awardStat(Stats.USE_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(used));
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1F, 1F);
        level.gameEvent(player, pour ? GameEvent.FLUID_PLACE : GameEvent.FLUID_PICKUP, pos);
    }
    // 26.1 has no registration event; use the native dispatcher registry during queued common setup.
    static void registerToAll(net.minecraft.world.item.Item item, CauldronInteraction interaction) {
        CauldronInteraction.INTERACTIONS.values().forEach(dispatcher -> dispatcher.map().put(item, interaction));
    }
    private SaltWaterInteractions() {}
}
