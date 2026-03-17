package net.blumasc.excitingenchants.mixin;

import net.blumasc.excitingenchants.effect.ModEffects;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin {
    @Inject(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;pullEntity(Lnet/minecraft/world/entity/Entity;)V"
            ),
            cancellable = true
    )
    private void excitingenchants$yoinking(ItemStack rod, CallbackInfoReturnable<Integer> cir) {
        FishingHook hook = (FishingHook)(Object)this;

        Entity owner = hook.getOwner();
        if (!(owner instanceof Player player)) return;

        Entity hooked = hook.getHookedIn();
        if (!(hooked instanceof LivingEntity target)) return;

        if (target.isShiftKeyDown()) {this.pullEntity(hooked);return;}

        Holder<Enchantment> yoinkHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.YOINKING);

        int level = rod.getEnchantmentLevel(yoinkHolder);
        if (level <= 0) return;

        double chance = 0.10D * level;
        if (player.getRandom().nextDouble() > chance){this.pullEntity(hooked); return;}

        ItemStack main = target.getMainHandItem();
        ItemStack off = target.getOffhandItem();

        InteractionHand chosenHand = null;

        if (!main.isEmpty() && !off.isEmpty()) {
            chosenHand = player.getRandom().nextBoolean()
                    ? InteractionHand.MAIN_HAND
                    : InteractionHand.OFF_HAND;
        } else if (!main.isEmpty()) {
            chosenHand = InteractionHand.MAIN_HAND;
        } else if (!off.isEmpty()) {
            chosenHand = InteractionHand.OFF_HAND;
        }

        if (chosenHand == null) {
            this.pullEntity(hooked);
            return;
        }
        ItemStack stolen = target.getItemInHand(chosenHand);

        if (stolen.isEmpty()) return;

        target.setItemInHand(chosenHand, ItemStack.EMPTY);

        ItemEntity drop = new ItemEntity(
                player.level(),
                target.getX(),
                target.getEyeY(),
                target.getZ(),
                stolen
        );

        Vec3 motion = player.position().subtract(drop.position()).normalize().scale(0.3);
        drop.setDeltaMovement(motion);

        player.level().addFreshEntity(drop);
    }

    @Shadow
    protected abstract void pullEntity(Entity entity);

    @Shadow
    abstract void setHookedEntity(@Nullable Entity hookedEntity);

    @Redirect(
            method = "retrieve",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/FishingHook;pullEntity(Lnet/minecraft/world/entity/Entity;)V"
            )
    )
    private void excitingenchants$disablePull(FishingHook hook, Entity entity) {
        Entity owner = hook.getOwner();
        if (!(owner instanceof Player player)) {
            this.pullEntity(entity);
            return;
        }

        Holder<Enchantment> yoink = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.YOINKING);

        if (hasEnchantment(yoink, player)) {
            return;
        }

        Holder<Enchantment> freezeHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.FLASH_FREEZE);

        if (hasEnchantment(freezeHolder, player)) return;

        this.pullEntity(entity);
    }

        @Inject(
                method = "onHitEntity",
                at = @At("TAIL")
        )
        private void excitingenchants$flashFreeze(EntityHitResult hit, CallbackInfo ci) {
            FishingHook hook = (FishingHook)(Object)this;

            if (!(hook.getOwner() instanceof Player player)) return;
            if (player.level().isClientSide()) return;

            Entity entity = hit.getEntity();
            if (!(entity instanceof LivingEntity target)) return;


            Holder<Enchantment> freezeHolder = player.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.FLASH_FREEZE);

            if (!hasEnchantment(freezeHolder, player)) return;

            Level level = player.level();
            BlockPos base = target.blockPosition();

            BlockPos placePos = null;
            if (level.getBlockState(base).canBeReplaced()) {
                placePos = base;
            } else if (level.getBlockState(base.above()).canBeReplaced()) {
                placePos = base.above();
            }

            if (placePos != null) {
                level.setBlock(
                        placePos,
                        Blocks.POWDER_SNOW.defaultBlockState(),
                        Block.UPDATE_ALL
                );
            }
        }
    @Inject(
            method = "onHitBlock",
            at = @At("TAIL")
    )
    private void excitingenchants$flashFreezeBlock(BlockHitResult hit, CallbackInfo ci) {
        FishingHook hook = (FishingHook)(Object)this;

        if (!(hook.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        Holder<Enchantment> freezeHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.FLASH_FREEZE);

        if (!hasEnchantment(freezeHolder, player)) return;

        Level level = player.level();
        BlockPos hitPos = hit.getBlockPos();
        var hitState = level.getBlockState(hitPos);

        if (hitState.is(Blocks.WATER)) {
            level.setBlock(hitPos, Blocks.ICE.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }

        BlockPos placePos = hitPos.relative(hit.getDirection());
        if (level.getBlockState(placePos).canBeReplaced()) {
            if(level.getBlockState(placePos).is(Blocks.WATER)){
                level.setBlock(placePos, Blocks.ICE.defaultBlockState(), Block.UPDATE_ALL);
            }else {
                level.setBlock(placePos, Blocks.POWDER_SNOW.defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }
    @Inject(
            method = "onHitEntity",
            at = @At("TAIL")
    )
    private void excitingenchants$badBait(EntityHitResult hit, CallbackInfo ci) {
        FishingHook hook = (FishingHook)(Object)this;

        if (!(hook.getOwner() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        Entity entity = hit.getEntity();
        if (!(entity instanceof LivingEntity target)) return;


        Holder<Enchantment> freezeHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.BAD_BAIT);

        if (!hasEnchantment(freezeHolder, player)) return;

        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION,60));
    }
    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void excitingenchants$barbedHook(CallbackInfo ci) {
        FishingHook hook = (FishingHook)(Object)this;

        if (!(hook.getOwner() instanceof Player owner)) return;
        if (owner.level().isClientSide()) return;

        Level level = owner.level();
        BlockPos hitPos = BlockPos.containing(((FishingHook)(Object)this).position());
        var hitState = level.getBlockState(hitPos);

        Holder<Enchantment> barbedHolder = owner.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.BARBED_HOOK);

        Holder<Enchantment> badBait = owner.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.BAD_BAIT);

        Holder<Enchantment> flashFreeze = owner.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.FLASH_FREEZE);

        if (hitState.is(Blocks.WATER) && hasEnchantment(flashFreeze, owner)) {
            level.setBlock(hitPos, Blocks.ICE.defaultBlockState(), Block.UPDATE_ALL);
            hook.setPos(hook.getX(), Math.ceil(hook.getY()), hook.getZ());
        }

        if (hitState.is(Blocks.ICE) && hasEnchantment(flashFreeze, owner)) {
            hook.setPos(hook.getX(), Math.ceil(hook.getY()), hook.getZ());
        }

        if(hasEnchantment(badBait, owner)){
            int color = MobEffects.CONFUSION.value().getColor();
            double r = ((color >> 16) & 0xFF) / 255.0;
            double g = ((color >> 8)  & 0xFF) / 255.0;
            double b = ((color)       & 0xFF) / 255.0;

            ((ServerLevel) (owner.level())).sendParticles(
                    ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, (float) r, (float) g, (float) b),
                    hook.getX(), hook.getY(), hook.getZ(),
                    3,
                    0.3, 0.3, 0.3,
                    0.0
            );
        }

        if (!hook.onGround()) return;

        if (!hasEnchantment(barbedHolder, owner)) return;

        if (hook.getHookedIn() != null) return;

        AABB trapBox = hook.getBoundingBox().inflate(0.35D);

        List<LivingEntity> entities = owner.level().getEntitiesOfClass(
                LivingEntity.class,
                trapBox,
                e -> e.isAlive() && e.getEffect(ModEffects.PINNED) == null
        );

        for (LivingEntity target : entities) {
            target.addEffect(
                    new MobEffectInstance(
                            ModEffects.PINNED,
                            100,
                            1,
                            false,
                            true
                    )
            );
            if(hasEnchantment(badBait, owner)){
                target.addEffect(new MobEffectInstance(MobEffects.CONFUSION,60));
            }
            if(target != owner){
                this.setHookedEntity(target);
            }
            break;
        }
    }
    private boolean hasEnchantment(Holder<Enchantment> enchantment, Player p){
        boolean hasEnch = false;
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack rod = p.getItemInHand(hand);

            if (rod.getEnchantmentLevel(enchantment) > 0) {
                hasEnch = true;
                break;
            }else if( rod.getItem() instanceof FishingRodItem){
                return false;
            }
        }
        return hasEnch;
    }

}
