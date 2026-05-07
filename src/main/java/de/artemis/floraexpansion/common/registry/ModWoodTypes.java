package de.artemis.floraexpansion.common.registry;

import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public final class ModWoodTypes {
    private ModWoodTypes() {}

    public static final String CACTUS_NAME = FloraExpansion.MODID + ":cactus";

    public static final BlockSetType CACTUS_SET_TYPE = BlockSetType.register(
            new BlockSetType(CACTUS_NAME)
    );

    public static final WoodType CACTUS_WOOD_TYPE = WoodType.register(
            new WoodType(
                    CACTUS_NAME,
                    CACTUS_SET_TYPE,
                    SoundType.WOOD,
                    SoundType.HANGING_SIGN,
                    SoundEvents.FENCE_GATE_CLOSE,
                    SoundEvents.FENCE_GATE_OPEN
            )
    );
}
