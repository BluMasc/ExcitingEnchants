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

public class CastleModeEffect extends MobEffect {

    private static final ResourceLocation CASTLE_ARMOR_ID =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "castle_armor");

    public CastleModeEffect() {
        super(MobEffectCategory.NEUTRAL, 0x414141);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        int armorBonus = amplifier + 1;

        AttributeInstance armorAttr = entity.getAttribute(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(CASTLE_ARMOR_ID);
            armorAttr.addTransientModifier(new AttributeModifier(
                    CASTLE_ARMOR_ID,
                    armorBonus,
                    AttributeModifier.Operation.ADD_VALUE
            ));
        }
        return true;
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributeMap) {
        super.removeAttributeModifiers(attributeMap);
        AttributeInstance armorAttr = attributeMap.getInstance(Attributes.ARMOR);
        if (armorAttr != null) {
            armorAttr.removeModifier(CASTLE_ARMOR_ID);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
