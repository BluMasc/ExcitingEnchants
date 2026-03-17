package net.blumasc.excitingenchants.mixin;

import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin extends Item {
    public TridentItemMixin(Properties properties) {
        super(properties);
    }

    @Shadow
    public abstract int getUseDuration(ItemStack stack, LivingEntity entity);
    private static boolean isTooDamagedToUseExtra(ItemStack stack) {
        return stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"), cancellable = true)
    public void releaseDevilUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft, CallbackInfo ci) {
        if (entityLiving instanceof Player player) {
            Holder<Enchantment> stingerHolder = level.registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.DEVILS_TOOL);

            int enchLevel = stack.getEnchantmentLevel(stingerHolder);
            if(enchLevel<=0)return;
            int i = this.getUseDuration(stack, entityLiving) - timeLeft;
            if (i >= 10) {
                float f = EnchantmentHelper.getTridentSpinAttackStrength(stack, player);
                if ((!(f > 0.0F) || (player.isOnFire() || player.level().dimension()==Level.NETHER )) && !isTooDamagedToUseExtra(stack)) {
                    Holder<SoundEvent> holder = (Holder)EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND).orElse(SoundEvents.TRIDENT_THROW);
                    if (!level.isClientSide) {
                        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
                        if (f == 0.0F) {
                            ThrownTrident throwntrident = new ThrownTrident(level, player, stack);
                            throwntrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
                            if (player.hasInfiniteMaterials()) {
                                throwntrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            level.addFreshEntity(throwntrident);
                            level.playSound((Player)null, throwntrident, (SoundEvent)holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!player.hasInfiniteMaterials()) {
                                player.getInventory().removeItem(stack);
                            }
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (f > 0.0F) {
                        float f7 = player.getYRot();
                        float f1 = player.getXRot();
                        float f2 = -Mth.sin(f7 * ((float)Math.PI / 180F)) * Mth.cos(f1 * ((float)Math.PI / 180F));
                        float f3 = -Mth.sin(f1 * ((float)Math.PI / 180F));
                        float f4 = Mth.cos(f7 * ((float)Math.PI / 180F)) * Mth.cos(f1 * ((float)Math.PI / 180F));
                        float f5 = Mth.sqrt(f2 * f2 + f3 * f3 + f4 * f4);
                        f2 *= f / f5;
                        f3 *= f / f5;
                        f4 *= f / f5;
                        player.push((double)f2, (double)f3, (double)f4);
                        player.startAutoSpinAttack(20, 8.0F, stack);
                        if (player.onGround()) {
                            float f6 = 1.1999999F;
                            player.move(MoverType.SELF, new Vec3((double)0.0F, (double)1.1999999F, (double)0.0F));
                        }

                        level.playSound((Player)null, player, (SoundEvent)holder.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
            ci.cancel();
        }

    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void devilsUse(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (isTooDamagedToUseExtra(itemstack)) {
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));
            return;
        }
        Holder<Enchantment> stingerHolder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.DEVILS_TOOL);

        int enchLevel = itemstack.getEnchantmentLevel(stingerHolder);
        float spinAttackStrength = EnchantmentHelper.getTridentSpinAttackStrength(itemstack, player);
        boolean isSpinable = (enchLevel<=0 && player.isInWaterOrRain()) || (enchLevel>0 && (player.isOnFire() || player.level().dimension()==Level.NETHER));
        if ( spinAttackStrength> 0.0F && !isSpinable) {
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));
        } else {
            player.startUsingItem(hand);
            cir.setReturnValue(InteractionResultHolder.consume(itemstack));
        }
    }
}
