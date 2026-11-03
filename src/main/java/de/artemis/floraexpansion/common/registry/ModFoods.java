package de.artemis.floraexpansion.common.registry;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties GLASSWORT = net.minecraft.world.food.Foods.DRIED_KELP;
    public static final FoodProperties SALTED_GLASSWORT = new FoodProperties.Builder().nutrition(2).saturationModifier(0.3F).fast().build();
    public static final FoodProperties SALTED_SPRUCE_NUTS = new FoodProperties.Builder().nutrition(2).saturationModifier(0.5F).build();
    public static final FoodProperties SALTED_TOASTED_SPRUCE_NUTS = new FoodProperties.Builder().nutrition(3).saturationModifier(0.6F).build();
    // Reward salting plus campfire drying: steak-level hunger with better saturation,
    // and fish that improves on cooked salmon, without adding food or status-effect hazards.
    public static final FoodProperties CURED_MEAT = new FoodProperties.Builder().nutrition(8).saturationModifier(0.9F).build();
    public static final FoodProperties CURED_FISH = new FoodProperties.Builder().nutrition(7).saturationModifier(0.8F).build();
    public static final FoodProperties JAM = (new FoodProperties.Builder()).nutrition(8).saturationModifier(0.4F).build();

    public static final FoodProperties BLUEBERRIES = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).build();
    public static final FoodProperties BLUEBERRY_COOKIE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.25F).build();
    public static final FoodProperties BLUEBERRY_PIE = withDuration((new FoodProperties.Builder()).nutrition(8).saturationModifier(0.3F).effect(() -> new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, 100, 0), 1F).build(), 6.4F);

    private static FoodProperties withDuration(FoodProperties food, float seconds) {
        return new FoodProperties(food.nutrition(), food.saturation(), food.canAlwaysEat(), seconds, food.usingConvertsTo(), food.effects());
    }
    public static final FoodProperties SLICE_OF_BLUEBERRY_PIE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.4F).build();
    public static final FoodProperties BLUEBERRY_JUICE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.45F).build();
    public static final FoodProperties BLUEBERRY_JAM = JAM;
    public static final FoodProperties STRAWBERRY = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).build();
    public static final FoodProperties STRAWBERRY_JUICE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.45F).build();
    public static final FoodProperties STRAWBERRY_JAM = JAM;
    public static final FoodProperties SPRUCE_NUTS = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).build();
    public static final FoodProperties CHERRIES = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).build();
    public static final FoodProperties TOASTED_SPRUCE_NUTS = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).build();
    public static final FoodProperties TRAIL_MIX = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.6F).build();
    public static final FoodProperties MIXED_BERRIES = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.4F).build();
    public static final FoodProperties CHERRY_JUICE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.45F).build();
    public static final FoodProperties APPLE_JUICE = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.45F).build();
    public static final FoodProperties CACTUS_SLICE = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).build();
    public static final FoodProperties CACTUS_JUICE = (new FoodProperties.Builder()).nutrition(4).saturationModifier(0.45F).build();
    public static final FoodProperties PRICKLY_PEAR = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).build();
}
