package net.blumasc.excitingenchants.mixin;

import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.entity.custom.BloodShardProjectile;
import net.blumasc.excitingenchants.entity.custom.NerfArrow;
import net.blumasc.excitingenchants.entity.custom.WoodArrow;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileWeaponItem.class)
public abstract class ProjectileWeaponMixin {

    @Shadow
    public abstract  AbstractArrow customArrow(
            AbstractArrow arrow,
            ItemStack projectileStack,
            ItemStack weaponStack
    );
    @Inject(
            method = "createProjectile",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void enchanment$woodArrow(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit, CallbackInfoReturnable<Projectile> cir) {
        if(ammo.is(Items.STICK))
        {
            AbstractArrow arrow = new WoodArrow(level, ammo, shooter, weapon);
            if (isCrit) {
                arrow.setCritArrow(true);
            }
            cir.setReturnValue(arrow);
            cir.cancel();
        }
        if(ammo.is(ModItems.BLOOD_ORB))
        {
            AbstractArrow arrow = new BloodShardProjectile(shooter,level, ammo, weapon);
            if (isCrit) {
                arrow.setCritArrow(true);
            }
            cir.setReturnValue(arrow);
            cir.cancel();
        }
        Holder<Enchantment> nerfHolder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.NERF);
        if (ammo.is(Items.ARROW) && weapon.getEnchantmentLevel(nerfHolder) > 0) {
            AbstractArrow arrow = new NerfArrow(level, ammo, shooter, weapon);
            cir.setReturnValue(arrow);
            cir.cancel();
        }
    }
}
