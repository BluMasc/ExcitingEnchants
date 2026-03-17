package net.blumasc.excitingenchants.entity.custom;

import net.blumasc.excitingenchants.damage.ModDamageTypes;
import net.blumasc.excitingenchants.entity.ModEntities;
import net.blumasc.excitingenchants.sound.ModSounds;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GrapeShotProjectile extends Projectile {
    private static final EntityDataAccessor<ItemStack> PARTICLE_ITEM = SynchedEntityData.defineId(GrapeShotProjectile.class, EntityDataSerializers.ITEM_STACK);
    private int damage;

    public GrapeShotProjectile(Level level, ItemStack stack, int damage, Entity shooter) {
        super(ModEntities.GRAPE_SHOT.get(), level);
        this.entityData.set(PARTICLE_ITEM, stack.copy());
        this.setOwner(shooter);
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.damage = damage;
        this.setPos(this.position());
    }

    public GrapeShotProjectile(EntityType<GrapeShotProjectile> grapeShotProjectileEntityType, Level level) {

        super(grapeShotProjectileEntityType, level);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 dir = new Vec3(x, y, z).normalize().scale(velocity);
        this.setDeltaMovement(dir);
        super.shoot(x, y, z, velocity, inaccuracy);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
            builder.define(PARTICLE_ITEM, Items.COBBLESTONE.getDefaultInstance());
    }

    @Override
    public void tick() {
        super.tick();

        if((level() instanceof ServerLevel sl) && tickCount==1)
        {
            sl.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.GRAPE_SHOT.get(), SoundSource.PLAYERS);
        }

        Vec3 delta = getDeltaMovement();
        if (delta.lengthSqr() == 0) return;

        Vec3 pos = this.position();

        spawnAndDamage(pos, 0, 1, delta);
        spawnAndDamage(pos.add(delta), 1.5, 2, delta);

        if (!level().isClientSide && tickCount >= 2) {
            this.discard();
        }
    }

    private void spawnAndDamage(Vec3 center, double sizeXY, int height, Vec3 move) {
        AABB area = new AABB(
                center.x - sizeXY, center.y - height/2.0, center.z - sizeXY,
                center.x + sizeXY, center.y + height/2.0, center.z + sizeXY
        );

        for (Entity entity : level().getEntities(this, area)) {
            if (entity instanceof LivingEntity target && entity != this.getOwner()) {
                target.hurt(ModDamageTypes.grapeShotDamage(level(), getOwner()), damage);
            }
        }

        if (level().isClientSide) {
            ItemStack stack = this.entityData.get(PARTICLE_ITEM);
            for (int i = 0; i < (sizeXY*2+1) * (sizeXY*2+1) * height*10; i++) {
                double px = center.x -(move.x/2) + (level().random.nextDouble() - 0.5) * sizeXY*2;
                double py = center.y -(move.y/2)+ (level().random.nextDouble() - 0.5) * height;
                double pz = center.z -(move.z/2)+ (level().random.nextDouble() - 0.5) * sizeXY*2;
                level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack), px, py, pz, move.x, move.y, move.z);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.nbt.CompoundTag nbt) {
        this.damage = nbt.getInt("damage");
        this.entityData.set(PARTICLE_ITEM,Items.COBBLESTONE.getDefaultInstance());
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.nbt.CompoundTag nbt) {
        nbt.putInt("damage",this.damage);
    }
}
