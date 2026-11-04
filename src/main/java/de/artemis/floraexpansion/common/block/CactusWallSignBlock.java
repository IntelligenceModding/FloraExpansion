package de.artemis.floraexpansion.common.block;


import de.artemis.floraexpansion.common.block.entity.CactusSignBlockEntity;
import de.artemis.floraexpansion.common.registry.ModWoodTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;


public class CactusWallSignBlock extends WallSignBlock {


    public CactusWallSignBlock(BlockBehaviour.Properties properties) {
        super(properties, ModWoodTypes.CACTUS_WOOD_TYPE);
    }



    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new CactusSignBlockEntity(pos, state);
    }
}
