package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModFoods;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class JamJarBlock extends JarBlock {
    public static final int MAX_PORTIONS = 4;
    public static final IntegerProperty PORTIONS = IntegerProperty.create("portions", 1, MAX_PORTIONS);

    public JamJarBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(PORTIONS, MAX_PORTIONS));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PORTIONS);
    }

    public static int itemPortions(ItemStack stack) {
        Integer portions = stack.getOrDefault(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY).get(PORTIONS);
        return portions == null ? MAX_PORTIONS : portions;
    }

    public static FoodProperties foodForPortions(int portions) {
        int nutrition = (ModFoods.JAM.nutrition() * portions + MAX_PORTIONS - 1) / MAX_PORTIONS;
        return new FoodProperties(nutrition, ModFoods.JAM.saturation() * nutrition / ModFoods.JAM.nutrition(), ModFoods.JAM.canAlwaysEat());
    }

    public static FoodProperties foodForBite(int portions) {
        FoodProperties before = foodForPortions(portions), after = foodForPortions(portions - 1);
        return new FoodProperties(before.nutrition() - after.nutrition(), before.saturation() - after.saturation(), false);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isSpectator() || !player.canEat(false)) return InteractionResult.PASS;
        if (!level.isClientSide()) {
            int portions = state.getValue(PORTIONS);
            player.getFoodData().eat(foodForBite(portions));
            player.awardStat(Stats.ITEM_USED.get(asItem()));
            level.setBlockAndUpdate(pos, portions == 1 ? ModBlocks.GLASS_JAR.get().defaultBlockState()
                    : state.setValue(PORTIONS, portions - 1));
            level.playSound(null, pos, SoundEvents.GENERIC_EAT.value(), SoundSource.PLAYERS, 0.7F, 1.0F);
            level.gameEvent(player, GameEvent.EAT, pos);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        var drops = super.getDrops(state, params);
        int portions = state.getValue(PORTIONS);
        if (portions < MAX_PORTIONS) for (ItemStack stack : drops) {
            if (stack.is(asItem())) {
                stack.set(DataComponents.BLOCK_STATE, stack.getOrDefault(DataComponents.BLOCK_STATE,
                        BlockItemStateProperties.EMPTY).with(PORTIONS, portions));
                stack.set(DataComponents.FOOD, foodForPortions(portions));
            }
        }
        return drops;
    }
}
