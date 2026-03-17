package net.blumasc.excitingenchants.effect.custom;

import net.blumasc.blubasics.damage.BaseModDamageTypes;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RainbowEffect extends MobEffect {
    public RainbowEffect() {
        super(MobEffectCategory.HARMFUL, 0x000000);
    }
}