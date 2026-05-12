package de.artemis.floraexpansion.common.registry;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockStateProperties {

    public static final IntegerProperty SEGMENT_AMOUNT;
    public static final IntegerProperty PEBBLE_AMOUNT;
    public static final BooleanProperty WILD_FLAX;
    public static final BooleanProperty WILD_STRAWBERRY;

    static {
        SEGMENT_AMOUNT = IntegerProperty.create("segment_amount", 1, 4);
        PEBBLE_AMOUNT = IntegerProperty.create("pebble_amount", 1, 5);
        WILD_FLAX = BooleanProperty.create("wild_flax");
        WILD_STRAWBERRY = BooleanProperty.create("wild_strawberry");
    }
}
