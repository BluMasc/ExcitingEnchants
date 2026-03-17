package net.blumasc.excitingenchants.entity.custom;

import net.blumasc.excitingenchants.entity.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WardenBeamProjectile extends Projectile {

    private static final double SPEED = 3.0; // blocks per tick, adjust as needed
    private static final double DAMAGE_RADIUS = 1.5;
    private static final float DAMAGE = 8.0f; // damage per tick
    private static final double MAX_DISTANCE = 10.0;

    private Vec3 startPos;

    public WardenBeamProjectile(Level level, LivingEntity shooter) {
        super(ModEntities.WARDEN_BEAM.get(), level);
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.startPos = this.position();
    }

    public WardenBeamProjectile(EntityType<WardenBeamProjectile> wardenBeamProjectileEntityType, Level level) {
        super(wardenBeamProjectileEntityType, level);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {

        super.shoot(x, y, z, velocity, inaccuracy);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        if(startPos==null){
            startPos = this.position();
        }
        super.tick();

        Vec3 motion = this.getDeltaMovement().scale(SPEED * 0.05);
        this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);

        if (level().isClientSide) {
            level().addParticle(ParticleTypes.SONIC_BOOM,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0, 0);
        }

        if (!level().isClientSide) {
            AABB area = new AABB(this.getX() - DAMAGE_RADIUS, this.getY() - DAMAGE_RADIUS, this.getZ() - DAMAGE_RADIUS,
                    this.getX() + DAMAGE_RADIUS, this.getY() + DAMAGE_RADIUS, this.getZ() + DAMAGE_RADIUS);
            for (Entity entity : level().getEntities(this, area)) {
                if (entity instanceof LivingEntity target && entity != this.getOwner()) {
                    target.hurt(target.level().damageSources().sonicBoom(getOwner()), DAMAGE);
                }
            }
            if(tickCount%10==0){
            ((ServerLevel) level()).playSound(null, this.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f);
        }}

        if (this.position().distanceTo(startPos) >= MAX_DISTANCE) {
            this.discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.nbt.CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.nbt.CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
    }
}
