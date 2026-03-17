package net.blumasc.excitingenchants.effect.custom;

import net.blumasc.blubasics.damage.BaseModDamageTypes;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class PinnedEffect extends MobEffect {
    public PinnedEffect() {
        super(MobEffectCategory.HARMFUL, 0x7b1010);
    }
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity instanceof Player p)
        {
            if(p.isCrouching())
            {
                p.removeEffect(ModEffects.PINNED);
                if (amplifier > 0) {
                    float damageAmount = 1.0F * amplifier;
                    p.hurt(BaseModDamageTypes.spikeDamage(entity.level()), damageAmount);
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}