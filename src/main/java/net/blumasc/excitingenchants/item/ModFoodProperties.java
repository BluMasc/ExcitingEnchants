package net.blumasc.excitingenchants.item;

import net.blumasc.excitingenchants.effect.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {
    public static final FoodProperties SUSPICIOUS_MEAT = new FoodProperties.Builder().nutrition(3).saturationModifier(0.8f).build();
    public static final FoodProperties COOKED_SUSPICIOUS_MEAT = new FoodProperties.Builder().nutrition(8).saturationModifier(1.51f).build();
    public static final FoodProperties GOLDEN_MEAT = new FoodProperties.Builder().nutrition(3).saturationModifier(0.8f).build();
    public static final FoodProperties COOKED_GOLDEN_MEAT = new FoodProperties.Builder().nutrition(8).saturationModifier(1.51f).build();
    public static final FoodProperties HOT_DOG = new FoodProperties.Builder().nutrition(5).saturationModifier(1.2f).build();
    public static final FoodProperties RAW_CARP = new FoodProperties.Builder().nutrition(2).saturationModifier(0.5f).effect(new MobEffectInstance(MobEffects.CONFUSION, 200), 1.0f).build();
    public static final FoodProperties COOKED_CARP = new FoodProperties.Builder().nutrition(5).saturationModifier(1.2f).effect(new MobEffectInstance(MobEffects.CONFUSION, 100), 1.0f).build();
    public static final FoodProperties RAW_RAINBOW_FISH = new FoodProperties.Builder().nutrition(2).saturationModifier(0.5f).effect(new MobEffectInstance(ModEffects.RAINBOW, 200), 1.0f).build();
    public static final FoodProperties COOKED_RAINBOW_FISH = new FoodProperties.Builder().nutrition(5).saturationModifier(1.2f).effect(new MobEffectInstance(ModEffects.RAINBOW, 100), 1.0f).build();
    public static final FoodProperties GASSTATION_SUSHI = new FoodProperties.Builder().nutrition(5).saturationModifier(1.2f).effect(new MobEffectInstance(MobEffects.WITHER, 200), 1.0f).effect(new MobEffectInstance(MobEffects.POISON, 200), 1.0f).effect(new MobEffectInstance(MobEffects.CONFUSION, 200), 1.0f).effect(new MobEffectInstance(MobEffects.UNLUCK, 200), 1.0f).build();
}
