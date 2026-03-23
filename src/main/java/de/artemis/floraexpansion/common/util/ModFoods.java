package de.artemis.floraexpansion.common.util;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {

    public static final FoodProperties PINE_NUTS = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).fast().build();
    public static final FoodProperties CHERRIES = (new FoodProperties.Builder()).nutrition(1).saturationModifier(0.3F).fast().build();
    public static final FoodProperties TOASTED_PINE_NUTS = (new FoodProperties.Builder()).nutrition(2).saturationModifier(0.3F).fast().build();
    public static final FoodProperties FOREST_SNACK = (new FoodProperties.Builder()).nutrition(6).saturationModifier(0.6F).build();
    public static final FoodProperties SWEET_BERRY_MIX = (new FoodProperties.Builder()).nutrition(5).saturationModifier(0.4F).build();
}
