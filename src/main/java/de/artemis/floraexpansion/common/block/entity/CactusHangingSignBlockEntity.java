package de.artemis.floraexpansion.common.block.entity;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;

public class CactusHangingSignBlockEntity extends HangingSignBlockEntity {
    public CactusHangingSignBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    @NotNull public BlockEntityType<?> getType() {
        return ModBlockEntities.CACTUS_HANGING_SIGN.get();
    }


}
