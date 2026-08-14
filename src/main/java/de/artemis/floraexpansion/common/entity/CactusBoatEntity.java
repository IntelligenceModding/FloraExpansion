package de.artemis.floraexpansion.common.entity;

import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;
import de.artemis.floraexpansion.common.registry.ModEntityTypes;

public class CactusBoatEntity extends Boat {
    public CactusBoatEntity(EntityType<? extends CactusBoatEntity> type, Level level) {
        super(type, level, ModItems.CACTUS_BOAT::get);
    }

    public CactusBoatEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.CACTUS_BOAT.get(), level, ModItems.CACTUS_BOAT::get);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }
}

