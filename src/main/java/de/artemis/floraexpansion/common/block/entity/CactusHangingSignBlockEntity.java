package de.artemis.floraexpansion.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.state.BlockState;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;

public class CactusHangingSignBlockEntity extends HangingSignBlockEntity {
    public CactusHangingSignBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.CACTUS_HANGING_SIGN.get();
    }

    @Override
    public Holder<BlockEntityType<?>> typeHolder() {
        return getType().builtInRegistryHolder();
    }
}
