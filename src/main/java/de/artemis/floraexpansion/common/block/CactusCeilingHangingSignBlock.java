package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.block.entity.CactusHangingSignBlockEntity;
import de.artemis.floraexpansion.common.registry.ModWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class CactusCeilingHangingSignBlock extends CeilingHangingSignBlock {
    public static final MapCodec<CeilingHangingSignBlock> CODEC =
            (MapCodec<CeilingHangingSignBlock>) (MapCodec<?>) simpleCodec(CactusCeilingHangingSignBlock::new);

    public CactusCeilingHangingSignBlock(BlockBehaviour.Properties properties) {
        super(ModWoodTypes.CACTUS_WOOD_TYPE, properties);
    }

    @Override
    public @NotNull MapCodec<CeilingHangingSignBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CactusHangingSignBlockEntity(pos, state);
    }
}
