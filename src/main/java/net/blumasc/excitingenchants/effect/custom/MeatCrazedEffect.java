package net.blumasc.excitingenchants.effect.custom;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MeatCrazedEffect extends MobEffect {

    public MeatCrazedEffect() {
        super(MobEffectCategory.HARMFUL, 0xce2121);
    }
}
