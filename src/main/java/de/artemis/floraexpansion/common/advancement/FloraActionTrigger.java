package de.artemis.floraexpansion.common.advancement;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.artemis.floraexpansion.FloraExpansion;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.Optional;
import java.util.function.Supplier;

/** Successful server actions, exposed as a normal, reloadable datapack criterion. */
public final class FloraActionTrigger extends SimpleCriterionTrigger<FloraActionTrigger.Instance> {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, FloraExpansion.MODID);
    private static final Supplier<FloraActionTrigger> ACTION = TRIGGERS.register("action", FloraActionTrigger::new);

    public static void register(IEventBus bus) { TRIGGERS.register(bus); }
    public static void fire(Player player, String action) {
        if (player instanceof ServerPlayer server) ACTION.get().trigger(server, instance -> instance.action().equals(action));
    }
    public static Criterion<Instance> criterion(String action) {
        return ACTION.get().createCriterion(new Instance(Optional.empty(), action));
    }
    @NotNull @Override public Codec<Instance> codec() { return Instance.CODEC; }

    public record Instance(Optional<net.minecraft.advancements.critereon.ContextAwarePredicate> player, String action) implements SimpleInstance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                net.minecraft.advancements.critereon.EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                Codec.STRING.fieldOf("action").forGetter(Instance::action)
        ).apply(instance, Instance::new));
    }
}
