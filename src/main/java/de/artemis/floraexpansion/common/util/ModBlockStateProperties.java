package de.artemis.floraexpansion.common.util;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockStateProperties {

    public static final IntegerProperty SEGMENT_AMOUNT;
    public static final IntegerProperty PEBBLE_AMOUNT;

    static {
        SEGMENT_AMOUNT = IntegerProperty.create("segment_amount", 1, 4);
        PEBBLE_AMOUNT = IntegerProperty.create("pebble_amount", 1, 5);
    }
}