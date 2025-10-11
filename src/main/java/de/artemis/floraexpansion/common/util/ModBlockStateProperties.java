package de.artemis.floraexpansion.common.util;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class ModBlockStateProperties {

    public static final IntegerProperty SEGMENT_AMOUNT;

    static {
        SEGMENT_AMOUNT = IntegerProperty.create("segment_amount", 1, 4);
    }
}