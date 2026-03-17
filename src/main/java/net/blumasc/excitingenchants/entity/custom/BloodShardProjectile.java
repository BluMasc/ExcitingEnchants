package net.blumasc.excitingenchants.entity.custom;

import net.blumasc.excitingenchants.entity.ModEntities;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.awt.*;

public class BloodShardProjectile extends AbstractArrow {

    public BloodShardProjectile(LivingEntity owner, Level level, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(ModEntities.BLOOD_SHARD.get(), owner, level, pickupItemStack, firedFromWeapon);
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    public BloodShardProjectile(Level level, double x, double y, double z, ItemStack pickUpItemStack, ItemStack weapon){
        super(ModEntities.BLOOD_SHARD.get(), x, y, z, level, pickUpItemStack, weapon);
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    public BloodShardProjectile(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    public BloodShardProjectile(EntityType<? extends AbstractArrow> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level, ItemStack.EMPTY, null);
        this.pickup = Pickup.CREATIVE_ONLY;

    }

    public BloodShardProjectile(EntityType<? extends AbstractArrow> entityType, LivingEntity owner, Level level) {
        super(entityType, owner, level, ItemStack.EMPTY, null);
        this.pickup = Pickup.CREATIVE_ONLY;
    }

    @Override
    public void tick() {

        super.tick();

        if (!this.level().isClientSide) {
        }else if(!this.inGround){
            spawnDustParticles();
        }
    }

    private void spawnDustParticles() {
        RandomSource random = level().random;

        for (int i = 0; i < 2; i++) { // spawn multiple per tick
            double offsetX = (random.nextDouble() - 0.5) * 0.2;
            double offsetY = (random.nextDouble() - 0.5) * 0.2;
            double offsetZ = (random.nextDouble() - 0.5) * 0.2;

            double px = this.getX() + offsetX;
            double py = this.getY() + offsetY;
            double pz = this.getZ() + offsetZ;

            int color = new Color(0x5a1f16).getRGB();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;

            DustParticleOptions dust = new DustParticleOptions(new Vector3f(r, g, b), 0.5f);
            level().addParticle(dust, px, py, pz, 0, -0.15, 0);
        }
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return SoundEvents.DRIPSTONE_BLOCK_BREAK;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ModItems.BLOOD_ORB.toStack();
    }
}
