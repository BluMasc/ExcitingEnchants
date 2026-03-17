package net.blumasc.excitingenchants.mixin;

import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Arrow.class)
public abstract class ArrowMixin extends AbstractArrow{

    protected ArrowMixin(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    abstract PotionContents getPotionContents();



    @Inject(method = "tick", at = @At("TAIL"))
    private void selectivepowers$tick(CallbackInfo ci) {
        Arrow arrow = (Arrow)(Object)this;

        int poisonLevel = selectivepowers$getLevel(ModEnchantments.TRAILING);

        PotionContents pc = (PotionContents)this.getPickupItemStackOrigin().getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);


        if (poisonLevel > 0 && !inGround && pc.hasEffects()) {
            AreaEffectCloud cloud = new AreaEffectCloud(
                    arrow.level(),
                    arrow.getX(),
                    arrow.getY(),
                    arrow.getZ()
            );

            cloud.setRadius(1.0F);
            cloud.setDuration(100);
            for (MobEffectInstance i : pc.getAllEffects()) {
                cloud.addEffect(
                        new MobEffectInstance(
                                i.getEffect(),
                                (i.getDuration()/5)*poisonLevel,
                                i.getAmplifier()
                        )
                );
            }

            arrow.level().addFreshEntity(cloud);
        }
    }
    private int selectivepowers$getLevel(ResourceKey<Enchantment> enchantment) {
        ItemStack weapon = getWeaponItem();
        if(weapon == null){
            return 0;
        }
        Holder<Enchantment> livingwoodHolder = this.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(enchantment);
        return weapon.getEnchantmentLevel(livingwoodHolder);
    }
}
