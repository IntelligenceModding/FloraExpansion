package de.artemis.floraexpansion.common.entity;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;
import net.minecraft.world.level.Level;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;

public class CactusChestBoatEntity extends ChestBoat {
    public CactusChestBoatEntity(EntityType<? extends CactusChestBoatEntity> type, Level level) {
        super(type, level, ModItems.CACTUS_CHEST_BOAT::get);
    }

    public CactusChestBoatEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.CACTUS_CHEST_BOAT.get(), level, ModItems.CACTUS_CHEST_BOAT::get);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }
}

