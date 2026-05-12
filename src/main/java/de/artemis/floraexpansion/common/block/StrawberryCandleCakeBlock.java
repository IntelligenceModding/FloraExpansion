package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StrawberryCandleCakeBlock extends CandleCakeBlock {
    private static final Map<CandleBlock, StrawberryCandleCakeBlock> BY_CANDLE = new HashMap<>();
    private static final MapCodec<StrawberryCandleCakeBlock> DIRECT_CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("candle").forGetter((StrawberryCandleCakeBlock block) -> block.candleBlock),
                            propertiesCodec()
                    )
                    .apply(instance, StrawberryCandleCakeBlock::new)
    );
    @SuppressWarnings("unchecked")
    public static final MapCodec<CandleCakeBlock> CODEC = (MapCodec<CandleCakeBlock>) (MapCodec<?>) DIRECT_CODEC;

    private final CandleBlock candleBlock;

    public StrawberryCandleCakeBlock(Block candleBlock, BlockBehaviour.Properties properties) {
        super(candleBlock, properties);
        if (candleBlock instanceof CandleBlock candle) {
            this.candleBlock = candle;
            BY_CANDLE.put(candle, this);
            return;
        }

        throw new IllegalArgumentException("Expected CandleBlock but got " + candleBlock.getClass());
    }

    @Override
    public @NotNull MapCodec<CandleCakeBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        InteractionResult result = eatStrawberryCake(level, pos, de.artemis.floraexpansion.common.registry.ModBlocks.STRAWBERRY_CAKE.get().defaultBlockState(), player);
        if (result.consumesAction()) {
            dropResources(state, level, pos);
        }

        return result;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(de.artemis.floraexpansion.common.registry.ModBlocks.STRAWBERRY_CAKE.get());
    }

    public static BlockState byCandle(CandleBlock candleBlock) {
        return BY_CANDLE.get(candleBlock).defaultBlockState();
    }

    private static InteractionResult eatStrawberryCake(LevelAccessor level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }

        player.awardStat(Stats.EAT_CAKE_SLICE);
        player.getFoodData().eat(2, 0.1F);
        int bites = state.getValue(StrawberryCakeBlock.BITES);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (bites < 6) {
            level.setBlock(pos, state.setValue(StrawberryCakeBlock.BITES, bites + 1), 3);
        } else {
            level.removeBlock(pos, false);
            level.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }

        return InteractionResult.SUCCESS;
    }
}
