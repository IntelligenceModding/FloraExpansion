package de.artemis.floraexpansion.common.item;

import de.artemis.floraexpansion.common.entity.CactusBoatEntity;
import de.artemis.floraexpansion.common.entity.CactusChestBoatEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class CactusBoatItem extends Item {
    private final boolean chestBoat;

    public CactusBoatItem(boolean chestBoat, Properties properties) {
        super(properties);
        this.chestBoat = chestBoat;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);

        if (hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResult.PASS;
        }

        ItemStack resultStack = stack;

        if (!player.getAbilities().instabuild) {
            resultStack = stack.copy();
            resultStack.shrink(1);
        }

        if (!level.isClientSide()) {
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

            player.awardStat(Stats.ITEM_USED.get(this));
        }

        return InteractionResult.SUCCESS.heldItemTransformedTo(resultStack);
    }
}