package de.artemis.floraexpansion.common.entity;

import de.artemis.floraexpansion.common.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class CactusChestBoatEntity extends ChestBoat {
    public CactusChestBoatEntity(EntityType<? extends Boat> type, Level level) {
        super(type, level);
        this.setVariant(Type.OAK);
    }

    public CactusChestBoatEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.CACTUS_CHEST_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setVariant(Type.OAK);
    }

    @Override
    public Item getDropItem() {
        return ModItems.CACTUS_CHEST_BOAT.get();
    }
}