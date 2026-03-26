package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.entity.CactusBoatEntity;
import de.artemis.floraexpansion.common.entity.CactusChestBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CactusBoatItem extends Item {
    private final boolean chestBoat;

    public CactusBoatItem(boolean chestBoat, Properties properties) {
        super(properties);
        this.chestBoat = chestBoat;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        if (!level.isClientSide) {
            double x = hit.getLocation().x;
            double y = hit.getLocation().y;
            double z = hit.getLocation().z;

            if (chestBoat) {
                CactusChestBoatEntity boat = new CactusChestBoatEntity(level, x, y, z);
                boat.setYRot(player.getYRot());
                level.addFreshEntity(boat);
            } else {
                CactusBoatEntity boat = new CactusBoatEntity(level, x, y, z);
                boat.setYRot(player.getYRot());
                level.addFreshEntity(boat);
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}