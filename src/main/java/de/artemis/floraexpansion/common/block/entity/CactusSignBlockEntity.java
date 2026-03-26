package de.artemis.floraexpansion.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CactusSignBlockEntity extends SignBlockEntity {
    public CactusSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CACTUS_SIGN.get(), pos, state);
    }
}