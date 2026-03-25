package de.artemis.floraexpansion.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import org.jetbrains.annotations.NotNull;

public class GiantCactusWoodBlock extends RotatedPillarBlock {
    public static final MapCodec<GiantCactusWoodBlock> CODEC = simpleCodec(GiantCactusWoodBlock::new);

    public static final BooleanProperty GENERATED = BooleanProperty.create("generated");

    private static final float BASE_THORN_DROP_CHANCE_ON_STRIP = 0.20F;
    private static final float FORTUNE_BONUS_PER_LEVEL = 0.10F;

    public GiantCactusWoodBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(AXIS, Direction.Axis.Y)
                .setValue(GENERATED, false));
    }

    @Override
    public @NotNull MapCodec<? extends RotatedPillarBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, GENERATED);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack,
                                                       @NotNull BlockState state,
                                                       @NotNull Level level,
                                                       @NotNull BlockPos pos,
                                                       @NotNull Player player,
                                                       @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hitResult) {
        if (!stack.canPerformAction(ItemAbilities.AXE_STRIP)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        // Already stripped: do nothing special
        if (state.is(ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get())) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        BlockState strippedState = ModBlocks.STRIPPED_GIANT_CACTUS_WOOD.get().defaultBlockState()
                .setValue(AXIS, state.getValue(AXIS))
                .setValue(GENERATED, state.getValue(GENERATED));

        if (!level.isClientSide) {
            level.setBlock(pos, strippedState, Block.UPDATE_ALL_IMMEDIATE);
            level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);

            Holder<net.minecraft.world.item.enchantment.Enchantment> fortune =
                    level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);

            int fortuneLevel = stack.getEnchantmentLevel(fortune);
            float thornDropChance = BASE_THORN_DROP_CHANCE_ON_STRIP + (fortuneLevel * FORTUNE_BONUS_PER_LEVEL);

            if (level.random.nextFloat() < thornDropChance) {
                Block.popResource(level, pos, new ItemStack(ModBlocks.CACTUS_THORN.get()));
            }

            stack.hurtAndBreak(1, player,
                    hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}