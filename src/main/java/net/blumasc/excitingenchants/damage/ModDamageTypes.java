package net.blumasc.excitingenchants.damage;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> GRAPE_SHOT_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "grape_shot"));

    public static final ResourceKey<DamageType> SANGUINE_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "enchantment_bleed"));

    public static final ResourceKey<DamageType> SMOKE_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "enchantment_smoke"));

    public static DamageSource grapeShotDamage(Level level, @Nullable Entity cause) {
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(GRAPE_SHOT_DAMAGE), cause);
    }

    public static DamageSource sanguineDamage(Level level) {
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SANGUINE_DAMAGE));
    }

    public static DamageSource smokeDamage(Level level) {
        return new DamageSource(
                level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(SMOKE_DAMAGE));
    }
}
