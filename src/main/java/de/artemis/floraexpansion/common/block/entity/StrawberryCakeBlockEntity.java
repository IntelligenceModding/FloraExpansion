package de.artemis.floraexpansion.common.block.entity;

import de.artemis.floraexpansion.common.block.StrawberryCakeWithCandleBlock;
import de.artemis.floraexpansion.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

public class StrawberryCakeBlockEntity extends BlockEntity {
    private List<CandleBlock> candles = List.of();

    public StrawberryCakeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.STRAWBERRY_CAKE.get(), pos, state);
    }

    public List<CandleBlock> getCandles() {
        var state = getBlockState();
        var cake = (StrawberryCakeWithCandleBlock) state.getBlock();
        int count = state.getValue(StrawberryCakeWithCandleBlock.CANDLES);
        var result = new ArrayList<CandleBlock>(count);
        for (int slot = 0; slot < count; slot++) {
            result.add(slot < candles.size() ? candles.get(slot) : cake.getCandle());
        }
        return List.copyOf(result);
    }

    public void setCandles(List<CandleBlock> candles) {
        if (candles.isEmpty() || candles.size() > StrawberryCakeWithCandleBlock.MAX_CANDLES) {
            throw new IllegalArgumentException("A cake must have one to three candles");
        }
        this.candles = List.copyOf(candles);
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        candles = input.read("candles", BuiltInRegistries.BLOCK.byNameCodec().listOf()).orElse(List.of()).stream()
                .filter(CandleBlock.class::isInstance).map(CandleBlock.class::cast)
                .limit(StrawberryCakeWithCandleBlock.MAX_CANDLES).toList();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("candles", BuiltInRegistries.BLOCK.byNameCodec().listOf(), new ArrayList<Block>(getCandles()));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
