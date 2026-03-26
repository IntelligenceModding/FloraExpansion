package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import de.artemis.floraexpansion.common.block.entity.CactusSignBlockEntity;
import de.artemis.floraexpansion.common.util.ModWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class CactusStandingSignBlock extends StandingSignBlock {
    public static final MapCodec<StandingSignBlock> CODEC =
            (MapCodec<StandingSignBlock>) (MapCodec<?>) simpleCodec(CactusStandingSignBlock::new);

    public CactusStandingSignBlock(BlockBehaviour.Properties properties) {
        super(ModWoodTypes.CACTUS_WOOD_TYPE, properties);
    }

    @Override
    public @NotNull MapCodec<StandingSignBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CactusSignBlockEntity(pos, state);
    }
}