package net.blumasc.excitingenchants.entity.custom;

import net.blumasc.excitingenchants.sound.ModSounds;
import net.blumasc.excitingenchants.state.PlayerEnchantmentState;
import net.blumasc.excitingenchants.state.PlayerEnchantmentStateHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BalloonEntity extends LivingEntity {
    private static final EntityDataAccessor<Optional<UUID>> BOUND_UUID =
            SynchedEntityData.defineId(BalloonEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> COLOR =
            SynchedEntityData.defineId(BalloonEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> HEIGHT =
            SynchedEntityData.defineId(BalloonEntity.class, EntityDataSerializers.FLOAT);

    public BalloonEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }


    public int getColor() {
        return this.entityData.get(COLOR);
    }

    public Entity getBoundEntity() {
        return getEntityFromUUID();
    }

    public void init(Entity boundEntity) {
        this.entityData.set(BOUND_UUID, Optional.of(boundEntity.getUUID()));
        int r =random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        this.entityData.set(COLOR, (r << 16) | (g << 8) | b);
        this.entityData.set(HEIGHT, random.nextFloat()*0.5f+0.1f);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if(source.is(DamageTypes.CRAMMING) || source.is(DamageTypes.IN_WALL)){
            return false;
        }
        this.pop();
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BOUND_UUID, Optional.empty());
        builder.define(COLOR, 0xFFFFFF);
        builder.define(HEIGHT, 0.3f);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if(compoundTag.contains("BoundUUID")) {
            this.entityData.set(BOUND_UUID, Optional.of(compoundTag.getUUID("BoundUUID")));
        }
        if(compoundTag.contains("color")) {
            this.entityData.set(COLOR, compoundTag.getInt("color"));
        }
        if(compoundTag.contains("height")) {
            this.entityData.set(HEIGHT, compoundTag.getFloat("height"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("color", this.entityData.get(COLOR));
        compoundTag.putFloat("height", this.entityData.get(HEIGHT));
        if(this.entityData.get(BOUND_UUID)!=null) {
            this.entityData.get(BOUND_UUID).ifPresent(uuid -> compoundTag.putUUID("BoundUUID", uuid));
        }
    }

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        Entity e = getEntityFromUUID();
        if(e == null){
            this.pop();
            return;
        }
        if(this.getY()>500){
            if(e instanceof ItemEntity){
                e.discard();
            }
            this.pop();
            return;
        }
        if(e instanceof ItemEntity){
            e.setDeltaMovement(e.getDeltaMovement().add(new Vec3(0,0.043,0)));
        }
        if(e instanceof LivingEntity le){
            PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(le);
            int level = pes.getBalloonCount()/2;
            le.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, level, true, false, false));
        }
        Vec3 entityTop = e.position().add(0, e.getBbHeight(), 0);
        Vec3 targetPosition = entityTop.add(0, 1+this.entityData.get(HEIGHT), 0);
        Vec3 testPosition = targetPosition.add(0, 1.0, 0);
        while(!this.level().getBlockState(BlockPos.containing(testPosition)).getCollisionShape(this.level(), BlockPos.containing(testPosition)).isEmpty()){
            testPosition = testPosition.add(0,-0.1, 0);
            if(testPosition.y<entityTop.y+0.6){
                this.pop();
                return;
            }
        }

        targetPosition = testPosition.add(0,-1.0,0);

        Vec3 currentPos = this.position();

        double dx = targetPosition.x - currentPos.x;
        double dz = targetPosition.z - currentPos.z;
        double horizontalDist = Math.sqrt(dx * dx + dz * dz);

        if(horizontalDist>4){
            this.setPos(targetPosition);
            return;
        }

        double lerpSpeed = Math.min(horizontalDist * 0.4, 1.0);

        double newX = currentPos.x + dx * lerpSpeed;
        double newZ = currentPos.z + dz * lerpSpeed;

        this.setPos(newX, targetPosition.y, newZ);
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    public void pop() {
        if (!this.level().isClientSide()) {
            int color = this.entityData.get(COLOR);
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8)  & 0xFF) / 255.0f;
            float b = ((color)       & 0xFF) / 255.0f;
            ((ServerLevel) this.level()).sendParticles(
                    new DustParticleOptions(new Vector3f(r, g, b), 1.5f),
                    this.getX(), this.getY(), this.getZ(),
                    20,
                    0.3, 0.3, 0.3,
                    0.1
            );

            this.level().playSound(
                    null,
                    this.blockPosition(),
                    ModSounds.BALLOON_POP.get(),
                    SoundSource.NEUTRAL,
                    1.0f, 1.8f
            );

            if(getEntityFromUUID() instanceof LivingEntity e){
                PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(e);
                pes.setBalloonCount(pes.getBalloonCount()-1);
                PlayerEnchantmentStateHandler.saveState(e, pes);
            }
            if (getEntityFromUUID() instanceof ItemEntity e) {
                e.getPersistentData().putBoolean("HasBalloon", false);
            }

            this.discard();
        }
    }


    private Entity getEntityFromUUID() {
        Optional<UUID> uuid = this.entityData.get(BOUND_UUID);
        if (uuid.isEmpty()) return null;

        if (this.level() instanceof ServerLevel serverLevel) {
            return serverLevel.getEntity(uuid.get());
        }

        return this.level().getEntities(
                EntityTypeTest.forClass(Entity.class),
                this.getBoundingBox().inflate(64),
                e -> e.getUUID().equals(uuid.get())
        ).stream().findFirst().orElse(null);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return List.of();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {

    }
}
