package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.item.WoodenBucketUtil;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class WoodenBucketEvents {
    private WoodenBucketEvents() {
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (!stack.is(ModItems.WOODEN_BUCKET.get())) {
            return;
        }

        if (event.getTarget() instanceof LivingEntity livingEntity && livingEntity instanceof Bucketable bucketable && livingEntity.isAlive()) {
            Item woodenMobBucket = getWoodenMobBucket(livingEntity);
            if (woodenMobBucket != null) {
                if (!player.level().isClientSide) {
                    livingEntity.playSound(bucketable.getPickupSound(), 1.0F, 1.0F);

                    ItemStack result = WoodenBucketUtil.transformInto(stack, player, woodenMobBucket, bucketable::saveToBucketTag);
                    player.setItemInHand(event.getHand(), result);
                    player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(ModItems.WOODEN_BUCKET.get()));
                    if (player instanceof ServerPlayer serverPlayer && !result.isEmpty()) {
                        CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, result);
                    }

                    livingEntity.discard();
                }

                event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
                event.setCanceled(true);
                return;
            }
        }

        if (!(event.getTarget() instanceof Cow cow) || cow.isBaby()) {
            return;
        }

        if (!player.level().isClientSide) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack milkBucket = WoodenBucketUtil.transformInto(stack, player, ModItems.WOODEN_MILK_BUCKET.get());
            player.setItemInHand(event.getHand(), milkBucket);
        }

        event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
        event.setCanceled(true);
    }

    private static Item getWoodenMobBucket(LivingEntity entity) {
        if (entity instanceof Cod) {
            return ModItems.COD_WOODEN_BUCKET.get();
        }

        if (entity instanceof Salmon) {
            return ModItems.SALMON_WOODEN_BUCKET.get();
        }

        if (entity instanceof Pufferfish) {
            return ModItems.PUFFERFISH_WOODEN_BUCKET.get();
        }

        if (entity instanceof TropicalFish) {
            return ModItems.TROPICAL_FISH_WOODEN_BUCKET.get();
        }

        if (entity instanceof Axolotl) {
            return ModItems.AXOLOTL_WOODEN_BUCKET.get();
        }

        if (entity instanceof Tadpole) {
            return ModItems.TADPOLE_WOODEN_BUCKET.get();
        }

        return null;
    }
}
