package de.artemis.floraexpansion.common.block;

import de.artemis.floraexpansion.common.item.ModItems;
import de.artemis.floraexpansion.common.particle.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LeafLitterBlock extends PineLitterBlock {
    private static final Map<UUID, Long> LAST_LEAF_PUFF = new HashMap<>();

    public LeafLitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack itemStack, @NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {

        if (player.getItemInHand(interactionHand).isEmpty()) {
            level.addFreshEntity(new ItemEntity(level,
                    blockPos.getX() + 0.5,
                    blockPos.getY() + 0.5,
                    blockPos.getZ() + 0.5,
                    new ItemStack(ModItems.TWIG.get(), level.random.nextInt(2 * blockState.getValue(LeafLitterBlock.AMOUNT)))));

            level.destroyBlock(blockPos, false);
            level.playSound(null, blockPos, SoundEvents.MOSS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (level.isClientSide) {
                System.out.println("2");

                System.out.println("3");

                level.addParticle(
                        ParticleTypes.ANGRY_VILLAGER,
                        player.getX(),
                        blockPos.getY() - 0.95,
                        player.getZ(),
                        1,
                        1, 1
                );
            }

            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.awardStat(Stats.BLOCK_MINED.get(this));
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);

        for (Player player : level.players()) {
            if (player.onGround() && player.blockPosition().equals(pos)) {
                double dx = player.getX() - player.xOld;
                double dz = player.getZ() - player.zOld;
                double speedSq = dx * dx + dz * dz;

                if (speedSq > 0.0003 && random.nextFloat() < 0.65F) {
                    int count = 2 + random.nextInt(4);

                    for (int i = 0; i < count; i++) {
                        double x = pos.getX() + 0.1 + random.nextDouble() * 0.8;
                        double z = pos.getZ() + 0.1 + random.nextDouble() * 0.8;
                        double y = pos.getY() + 0.05 + random.nextDouble() * 0.1;

                        double angle = random.nextDouble() * Math.PI * 2;
                        double speed = 0.025 + random.nextDouble() * 0.015;
                        double vx = Math.cos(angle) * speed;
                        double vz = Math.sin(angle) * speed;
                        double vy = 0.005 + random.nextDouble() * 0.01;

                        level.addParticle(ModParticles.LEAF_FLUFF_PARTICLES.get(), x, y, z, vx, vy, vz);
                    }

                    level.playLocalSound(
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            SoundEvents.CHERRY_LEAVES_STEP,
                            SoundSource.BLOCKS,
                            0.8F + random.nextFloat() * 0.2F,
                            0.9F + random.nextFloat() * 0.3F,
                            false
                    );
                }
            }
        }
    }
}
