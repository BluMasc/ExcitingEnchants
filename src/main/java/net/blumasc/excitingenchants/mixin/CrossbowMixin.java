package net.blumasc.excitingenchants.mixin;

import net.blumasc.excitingenchants.damage.ModDamageTypes;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.entity.custom.GrapeShotProjectile;
import net.blumasc.excitingenchants.entity.custom.LightningRodArrowEntity;
import net.blumasc.excitingenchants.entity.custom.WardenBeamProjectile;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowMixin {
    @Inject(
            method = "createProjectile",
            at = @At("HEAD"),
            cancellable = true
    )
    private void enchanment$replaceProjectile(
            Level level,
            LivingEntity shooter,
            ItemStack weapon,
            ItemStack ammo,
            boolean isCrit,
            CallbackInfoReturnable<Projectile> cir
    ) {
        if (ammo.is(Items.ECHO_SHARD)) {
            cir.setReturnValue(new WardenBeamProjectile(level, shooter));
        }
        if (ammo.is(Items.LIGHTNING_ROD)) {
            Holder<Enchantment> galvanizingHolder = shooter.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.GALVANIZING);

            int enchantLevel = weapon.getEnchantmentLevel(galvanizingHolder);
            cir.setReturnValue(new LightningRodArrowEntity(level, shooter, 1+enchantLevel, weapon));
        }
        if (ammo.is(ModTags.Items.GRAPESHOT_AMMUNITION)) {
            Holder<Enchantment> galvanizingHolder = shooter.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.GRAPE_SHOT);

            int enchantLevel = weapon.getEnchantmentLevel(galvanizingHolder);
            cir.setReturnValue(new GrapeShotProjectile(level, ammo, enchantLevel, shooter ));
        }
    }

    @Inject(
            method = "tryLoadProjectiles",
            at = @At("RETURN")
    )
    private static void onLoadProjectiles(
            LivingEntity shooter,
            ItemStack crossbowStack,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValue()) return;
        ChargedProjectiles charged = crossbowStack.get(DataComponents.CHARGED_PROJECTILES);
        if (charged == null) return;
        for (ItemStack projectile : charged.getItems()) {
            if (projectile.is(ModItems.BLOOD_ORB)) {
                shooter.hurt(ModDamageTypes.sanguineDamage(shooter.level()), 2);

                break;
            }
        }
    }
}
