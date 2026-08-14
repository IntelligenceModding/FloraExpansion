package de.artemis.floraexpansion.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;

public class CactusHangingSignBlockEntity extends SignBlockEntity {
    public CactusHangingSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CACTUS_HANGING_SIGN.get(), pos, state);
    }
}
