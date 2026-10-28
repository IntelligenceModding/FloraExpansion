package de.artemis.floraexpansion.common.registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

/** Consumption behavior kept separate from nutrition, following vanilla Foods/Consumables. */
public final class ModConsumables {
    public static final Consumable JAM = Consumables.defaultDrink().sound(SoundEvents.HONEY_DRINK).build();
    public static final Consumable BLUEBERRY_PIE = Consumables.defaultFood()
            .consumeSeconds(6.4F)
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0)))
            .build();

    private ModConsumables() {
    }
}
