package net.blumasc.excitingenchants.entity.custom;

import net.blumasc.excitingenchants.damage.ModDamageTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EchoGhostEntity extends Mob {

    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID =
        SynchedEntityData.defineId(EchoGhostEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> DATA_LIFE_TICK =
            SynchedEntityData.defineId(EchoGhostEntity.class, EntityDataSerializers.INT);

    private static final int SWING_DURATION = 10;
    @Nullable private UUID ownerUUID;
    private ItemStack echoSword = ItemStack.EMPTY;
    private static final EntityDataAccessor<Float> DATA_ATTACK_YAW =
            SynchedEntityData.defineId(EchoGhostEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> DATA_ATTACK_PITCH =
            SynchedEntityData.defineId(EchoGhostEntity.class, EntityDataSerializers.FLOAT);

    private int lifeTick = 0;
    private boolean hasAttacked = false;

    private static final int ATTACK_TICK   = 2;
    private static final int LIFETIME      = 22;
    private static final float SWORD_REACH = 3.5F;

    public EchoGhostEntity(EntityType<? extends EchoGhostEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setInvulnerable(false);
        this.setSilent(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    public LookControl getLookControl() {
        return new LookControl(this) {
            @Override
            public void tick() {
            }
        };
    }

    @Override
    public boolean isNoAi() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER_UUID, Optional.empty());
        builder.define(DATA_LIFE_TICK, 0);
        builder.define(DATA_ATTACK_YAW, 0f);
        builder.define(DATA_ATTACK_PITCH, 0f);
    }
    public float getAttackYaw() {
        return this.entityData.get(DATA_ATTACK_YAW);
    }

    public float getAttackPitch() {
        return this.entityData.get(DATA_ATTACK_PITCH);
    }
    public void init(UUID owner, ItemStack sword, float yaw, float pitch) {
        this.ownerUUID  = owner;
        this.echoSword  = sword.copy();
        this.entityData.set(DATA_ATTACK_YAW, yaw);
        this.entityData.set(DATA_ATTACK_PITCH, pitch);

        this.entityData.set(DATA_OWNER_UUID, Optional.of(owner));

        this.setYRot(yaw);
        this.yHeadRot = yaw;
        this.setXRot(pitch);

        this.setItemSlot(EquipmentSlot.MAINHAND, sword.copy());
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    public Optional<UUID> getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID);
    }

    @Override
    public void tick() {
        super.tick();
        lifeTick++;

        entityData.set(DATA_LIFE_TICK, lifeTick);

        if (level().isClientSide) return;

        ServerLevel serverLevel = (ServerLevel) level();

        if (lifeTick % 2 == 0 && lifeTick < LIFETIME - 3) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                getX(), getY() + 0.5, getZ(),
                2, 0.2, 0.6, 0.2, 0.01);
        }

        if (lifeTick == ATTACK_TICK && !hasAttacked) {
            hasAttacked = true;
            performEchoAttack(serverLevel);
        }

        if (lifeTick >= LIFETIME) {
            spawnVanishSmoke(serverLevel);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS);
            this.discard();
        }
    }

    @Override
    public float getAttackAnim(float partialTick) {
        float tick = entityData.get(DATA_LIFE_TICK) + partialTick;
        float t = (tick - ATTACK_TICK) / SWING_DURATION;
        if (t <= 0f || t >= 1f) return 0f;
        return Mth.sin(t * Mth.PI);
    }

    private void performEchoAttack(ServerLevel level) {
        level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS);
        Vec3 eyePos  = this.getEyePosition();
        Vec3 lookVec = lookAngleFromRotation(getAttackYaw(), getAttackPitch());
        Vec3 endPos  = eyePos.add(lookVec.scale(SWORD_REACH));

        AABB searchBox = new AABB(eyePos, endPos).inflate(0.5);

        List<Entity> candidates = level.getEntities(this, searchBox, e ->
            e != this
            && e instanceof LivingEntity
            && !e.getUUID().equals(ownerUUID)
            && !(e instanceof EchoGhostEntity));

        Entity hit         = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity candidate : candidates) {
            Optional<Vec3> result = candidate.getBoundingBox().inflate(0.2).clip(eyePos, endPos);
            if (result.isPresent()) {
                double dist = eyePos.distanceToSqr(result.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    hit = candidate;
                }
            }
        }

        if (!(hit instanceof LivingEntity livingHit)) return;

        float damage = computeSwordDamage();

        DamageSource source;
        @Nullable ServerPlayer owner = getOwnerPlayer(level);
        source = (owner != null)
            ? level.damageSources().playerAttack(owner)
            : level.damageSources().magic();

        livingHit.hurt(source, damage);

        double kx = -Math.sin(Math.toRadians(getAttackYaw()));
        double kz =  Math.cos(Math.toRadians(getAttackYaw()));
        livingHit.knockback(0.35F, -kx, -kz);
    }

    private float computeSwordDamage() {
        double[] dam = {1.0};
        echoSword.getAttributeModifiers().forEach(EquipmentSlot.MAINHAND, (attr, mod) -> {
            if (attr.is(Attributes.ATTACK_DAMAGE)
                && mod.operation() == AttributeModifier.Operation.ADD_VALUE) {
                dam[0] += mod.amount();
            }
        });
        return (float) dam[0];
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (level().isClientSide) return false;

        if (source.getEntity() != null && source.getEntity().getUUID().equals(ownerUUID))
            return false;

        if (source.getEntity() instanceof EchoGhostEntity)
            return false;

        ServerLevel serverLevel = (ServerLevel) level();

        spawnVanishSmoke(serverLevel);
        serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS);

        @Nullable ServerPlayer owner = getOwnerPlayer(serverLevel);
        if (owner != null && !owner.isCreative() && !owner.isSpectator()) {
            owner.hurt(ModDamageTypes.smokeDamage(level()), 3.0F);
        }

        this.discard();
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(net.minecraft.world.damagesource.DamageTypes.FALL)
            || source.is(net.minecraft.world.damagesource.DamageTypes.IN_FIRE)
            || source.is(net.minecraft.world.damagesource.DamageTypes.ON_FIRE)
            || source.is(net.minecraft.world.damagesource.DamageTypes.DROWN)
            || source.is(net.minecraft.world.damagesource.DamageTypes.IN_WALL)
            || source.is(net.minecraft.world.damagesource.DamageTypes.CRAMMING)
            || super.isInvulnerableTo(source);
    }

    private void spawnVanishSmoke(ServerLevel level) {
        for (int i = 0; i < 30; i++) {
            level.sendParticles(ParticleTypes.LARGE_SMOKE,
                getX() + (random.nextDouble() - 0.5) * 0.8,
                getY() + random.nextDouble() * 1.8,
                getZ() + (random.nextDouble() - 0.5) * 0.8,
                1, 0, 0.02, 0, 0.04);
        }
    }

    @Nullable
    private ServerPlayer getOwnerPlayer(ServerLevel level) {
        return ownerUUID != null ? (ServerPlayer) level.getPlayerByUUID(ownerUUID) : null;
    }

    public static Vec3 lookAngleFromRotation(float yawDeg, float pitchDeg) {
        float p  = pitchDeg * (float)(Math.PI / 180.0);
        float y  = -yawDeg  * (float)(Math.PI / 180.0);
        float cp = (float) Math.cos(p);
        float sp = (float) Math.sin(p);
        float cy = (float) Math.cos(y);
        float sy = (float) Math.sin(y);
        return new Vec3(sy * cp, -sp, cy * cp);
    }

    @Override
    protected void registerGoals() {}

    @Override
    public boolean shouldShowName() { return false; }

    @Override
    public boolean canBeSeenByAnyone() { return false; }

    @Override
    protected void dropEquipment() { }

    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource source) {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) { }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) { }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.DifficultyInstance difficulty,
            MobSpawnType reason,
            @Nullable SpawnGroupData data) {
        return data;
    }
}