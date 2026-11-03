package de.artemis.floraexpansion.common.event;

import de.artemis.floraexpansion.FloraExpansion;
import de.artemis.floraexpansion.common.block.SaltDepositBlock;
import de.artemis.floraexpansion.common.loot.HarvestLoot;
import de.artemis.floraexpansion.common.registry.ModBlocks;
import de.artemis.floraexpansion.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.level.BlockEvent.BreakEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Map;
import java.util.WeakHashMap;

/** Runs after vanilla brush strokes so their animation, hit particles and brushing sound stay intact. */
@EventBusSubscriber(modid = FloraExpansion.MODID)
public final class SaltDepositBrushing {
    public static final ResourceKey<LootTable> FINDS = ResourceKey.create(Registries.LOOT_TABLE,
            ResourceLocation.fromNamespaceAndPath(FloraExpansion.MODID, "archaeology/salt_deposit"));
    private static final int STROKES_PER_LAYER = 4;
    private static final Map<Player, Progress> PROGRESS = new WeakHashMap<>();

    @SubscribeEvent public static void start(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) PROGRESS.remove(player);
    }

    @SubscribeEvent public static void tick(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (!(player.level() instanceof ServerLevel level)) return;
        var tool = player.getUseItem();
        if (!player.isUsingItem() || player.isSpectator() || !tool.canPerformAction(ItemAbilities.BRUSH_BRUSH)) {
            PROGRESS.remove(player);
            return;
        }
        int elapsed = tool.getUseDuration(player) - player.getUseItemRemainingTicks();
        var hit = ProjectileUtil.getHitResultOnViewVector(player, entity -> !entity.isSpectator() && entity.isPickable(), player.blockInteractionRange());
        if (!(hit instanceof BlockHitResult blockHit) || hit.getType() != HitResult.Type.BLOCK) {
            PROGRESS.remove(player);
            return;
        }
        var pos = blockHit.getBlockPos();
        var state = level.getBlockState(pos);
        if (!state.is(ModBlocks.SALT_DEPOSIT.get()) || !level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, blockHit.getDirection(), tool)
                || !player.mayBuild() && !tool.canBreakBlockInAdventureMode(new BlockInWorld(level, pos, false))) {
            PROGRESS.remove(player);
            return;
        }
        var previous = PROGRESS.get(player);
        if (previous != null && (!previous.pos.equals(pos) || previous.state != state
                || previous.dimension != level.dimension())) {
            PROGRESS.remove(player);
            previous = null;
        }
        // Vanilla makes contact on the fifth tick of each ten-tick brush animation.
        if (elapsed <= 0 || elapsed % 10 != 5) return;
        long now = level.getGameTime();
        if (previous != null && now - previous.lastStroke < 10) return;
        int strokes = previous != null && now - previous.lastStroke <= 20 ? previous.strokes + 1 : 1;
        if (strokes < STROKES_PER_LAYER) {
            PROGRESS.put(player, new Progress(pos.immutable(), level.dimension(), state, now, strokes));
            return;
        }
        PROGRESS.remove(player);
        if (NeoForge.EVENT_BUS.post(new BreakEvent(level, pos, state, player)).isCanceled()) return;
        int layers = state.getValue(SaltDepositBlock.LAYERS);
        var remaining = layers == 1 ? state.getFluidState().createLegacyBlock() : state.setValue(SaltDepositBlock.LAYERS, layers - 1);
        if (!level.setBlock(pos, remaining, 3)) return;
        double surface = pos.getY() + layers / 4.0;
        drop(level, pos, surface, new ItemStack(ModItems.RAW_SALT.get()));
        if (state.getValue(SaltDepositBlock.NATURAL)) {
            de.artemis.floraexpansion.common.advancement.FloraActionTrigger.fire(player, "brush_salt");
            for (var find : HarvestLoot.drops(level, pos, state, player, tool, FINDS)) {
                drop(level, pos, surface, find);
                if (!find.isEmpty()) de.artemis.floraexpansion.common.advancement.FloraActionTrigger.fire(player, "salt_find");
            }
        }
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                pos.getX() + .5, surface, pos.getZ() + .5, 8, .25, .03, .25, .025);
        level.playSound(null, pos, SoundEvents.BRUSH_SAND_COMPLETED, SoundSource.BLOCKS, .6F, 1.1F);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, remaining));
        tool.hurtAndBreak(1, player, player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND
                ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
    }

    private static void drop(ServerLevel level, BlockPos pos, double surface, ItemStack stack) {
        if (stack.isEmpty()) return;
        var item = new ItemEntity(level, pos.getX() + .5, surface + .1, pos.getZ() + .5, stack);
        item.setDeltaMovement(0, .07, 0);
        item.setDefaultPickUpDelay();
        level.addFreshEntity(item);
    }

    private record Progress(BlockPos pos, ResourceKey<Level> dimension, BlockState state, long lastStroke, int strokes) {}
    private SaltDepositBrushing() {}
}
