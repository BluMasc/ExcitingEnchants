package net.blumasc.excitingenchants.mixin;

import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {

    protected AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow public abstract ItemStack getWeaponItem();

    @Shadow protected boolean inGround;

    @Unique
    private double selectivepowers$startY;
    @Unique
    private boolean selectivepowers$startYSet;
    @Unique private boolean selectivepowers$hasSplit;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void selectivepowers$init(CallbackInfo ci) {
        AbstractArrow self = (AbstractArrow)(Object)this;
        this.selectivepowers$hasSplit = false;
        this.selectivepowers$startYSet = false;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void selectivepowers$tick(CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;

        if (arrow.level().isClientSide) return;
        if(!selectivepowers$startYSet)
        {
            this.selectivepowers$startY = arrow.getY();
            this.selectivepowers$startYSet = true;
        }
        int rainLevel = selectivepowers$getLevel(ModEnchantments.VOLLEY);

        if (
            rainLevel > 0 &&
            !this.selectivepowers$hasSplit &&
            arrow.getY() - this.selectivepowers$startY >= 20.0
        ) {
            this.selectivepowers$hasSplit = true;

            for (int i = 0; i < rainLevel; i++) {
                AbstractArrow copy = (AbstractArrow) arrow.getType().create(arrow.level());
                if (copy == null) continue;

                copy.setPos(
                    arrow.getX(),
                    arrow.getY(),
                    arrow.getZ()
                );

                copy.setOwner(arrow.getOwner());

                copy.setDeltaMovement(
                    (arrow.level().random.nextDouble() - 0.5) * 0.2,
                    -1.0,
                    (arrow.level().random.nextDouble() - 0.5) * 0.2
                );
                copy.pickup = AbstractArrow.Pickup.DISALLOWED;

                arrow.level().addFreshEntity(copy);
            }

            // make original fall too
            arrow.setDeltaMovement(
                (arrow.level().random.nextDouble() - 0.5) * 0.2,
                -1.0,
                (arrow.level().random.nextDouble() - 0.5) * 0.2
            );
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
