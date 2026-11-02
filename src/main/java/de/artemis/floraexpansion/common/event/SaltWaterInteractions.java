package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.item.WoodenBucketUtil;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCauldronInteractionEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class SaltWaterInteractions {
    public static final CauldronInteraction.Dispatcher CAULDRON = new CauldronInteraction.Dispatcher();
    private static final Identifier ID = Identifier.fromNamespaceAndPath(FloraExpansion.MODID, "salt_water");
    @SubscribeEvent public static void dispatchers(RegisterCauldronInteractionEvent.Dispatcher event) { event.register(ID, CAULDRON); }
    @SubscribeEvent public static void interactions(RegisterCauldronInteractionEvent.Interaction event) {
        CauldronInteractions.WATER.put(ModItems.SALT.get(), SaltWaterInteractions::saltWater);
        event.registerToAll(ModItems.SALT_WATER_BUCKET.get(), SaltWaterInteractions::pourBucket);
        event.registerToAll(ModItems.WOODEN_SALT_WATER_BUCKET.get(), SaltWaterInteractions::pourBucket);
        CAULDRON.put(Items.BUCKET, (state, level, pos, player, hand, stack) -> CauldronInteractions.fillBucket(
                state, level, pos, player, hand, stack, new ItemStack(ModItems.SALT_WATER_BUCKET.get()),
                s -> s.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
        CAULDRON.put(ModItems.WOODEN_BUCKET.get(), SaltWaterInteractions::fillWoodenBucket);
        CAULDRON.put(net.minecraft.tags.ItemTags.CAULDRON_CAN_REMOVE_DYE, CauldronInteractions::dyedItemIteration);
        Items.BANNER.forEach(banner -> CAULDRON.put(banner, CauldronInteractions::bannerInteraction));
        Items.DYED_SHULKER_BOX.forEach(box -> CAULDRON.put(box, CauldronInteractions::shulkerBoxInteraction));
        // Preserve vanilla replacement behavior when another filled bucket is poured in.
        CauldronInteractions.addDefaultInteractions(CAULDRON);
    }
    private static InteractionResult saltWater(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!state.is(Blocks.WATER_CAULDRON)) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (!level.isClientSide()) {
            level.setBlock(pos, ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState()
                    .setValue(LayeredCauldronBlock.LEVEL, state.getValue(LayeredCauldronBlock.LEVEL)), 3);
            if (!player.hasInfiniteMaterials()) stack.shrink(1);
            de.artemis.floraexpansion.common.util.InventoryUtils.syncPlayerInventory(player);
            changed(level, pos, player, ModItems.SALT.get(), true, SoundEvents.SAND_PLACE);
        }
        return InteractionResult.SUCCESS;
    }
    @SubscribeEvent public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> DispenserBlock.registerBehavior(ModItems.SALT_WATER_BUCKET.get(),
                DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET)));
    }
    private static InteractionResult pourBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        var full = ModBlocks.SALT_WATER_CAULDRON.get().defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3);
        if (stack.is(ModItems.SALT_WATER_BUCKET.get()))
            return CauldronInteractions.emptyBucket(level, pos, player, hand, stack, full, SoundEvents.BUCKET_EMPTY);
        return WoodenBucketCauldronInteractions.pour(level,pos,player,hand,stack,full,SoundEvents.BUCKET_EMPTY);
    }
    private static InteractionResult fillWoodenBucket(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (state.getValue(LayeredCauldronBlock.LEVEL) != 3) return InteractionResult.TRY_WITH_EMPTY_HAND;
        return WoodenBucketCauldronInteractions.fill(level,pos,player,hand,stack,
                ModItems.WOODEN_SALT_WATER_BUCKET.get(),SoundEvents.BUCKET_FILL);
    }
    private static void changed(Level level, BlockPos pos, Player player, net.minecraft.world.item.Item used, boolean pour, net.minecraft.sounds.SoundEvent sound) {
        player.awardStat(Stats.USE_CAULDRON);
        player.awardStat(Stats.ITEM_USED.get(used));
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1F, 1F);
        level.gameEvent(player, pour ? GameEvent.FLUID_PLACE : GameEvent.FLUID_PICKUP, pos);
    }
    private SaltWaterInteractions() {}
}
