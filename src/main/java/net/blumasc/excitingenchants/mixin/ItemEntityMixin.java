package net.blumasc.excitingenchants.mixin;

import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.entity.ModEntities;
import net.blumasc.excitingenchants.entity.custom.BalloonEntity;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin{

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void getBallon(CallbackInfo ci){
        ItemEntity me = ((ItemEntity) (Object) this);
        if (me.level().isClientSide()) return;
        if (me.getPersistentData().getBoolean("HasBalloon")) return;
        if(me.onGround() && me.getItem()!=null){
            Holder<Enchantment> dashHolder = me.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.HELIUM);
            int level = me.getItem().getEnchantmentLevel(dashHolder);
            if(level>0) {
                BalloonEntity be = new BalloonEntity(ModEntities.BALLOON.get(), me.level());
                be.moveTo(me.position());
                be.init(me);
                me.level().addFreshEntity(be);
                me.getPersistentData().putBoolean("HasBalloon", true);
            }
            Holder<Enchantment> chorusHolder = me.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.CHORUS);
            level = me.getItem().getEnchantmentLevel(chorusHolder);
            if(level>0 && me.getRandom().nextFloat()<0.01) {
                for (int i = 0; i < 16; ++i) {
                    double d0 = me.getX() + (me.getRandom().nextDouble() - (double) 0.5F) * (double) 5.0F;
                    double d1 = Mth.clamp(me.getY() + (double) (me.getRandom().nextInt(10) - 5), (double) me.level().getMinBuildHeight(), (double) (me.level().getMinBuildHeight() + ((ServerLevel) me.level()).getLogicalHeight() - 1));
                    double d2 = me.getZ() + (me.getRandom().nextDouble() - (double) 0.5F) * (double) 5.0F;
                    if (me.isPassenger()) {
                        me.stopRiding();
                    }

                    Vec3 vec3 = me.position();
                    EntityTeleportEvent.TeleportCommand chorusEvent = new EntityTeleportEvent.TeleportCommand(me, d0, d1, d2);
                    if (randomTeleport(me, chorusEvent.getTargetX(), chorusEvent.getTargetY(), chorusEvent.getTargetZ(), true)) {
                        me.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(me));
                        SoundSource soundsource;
                        SoundEvent soundevent;
                        soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                        soundsource = SoundSource.PLAYERS;

                        me.level().playSound((Player) null, me.getX(), me.getY(), me.getZ(), soundevent, soundsource);
                        me.resetFallDistance();
                        break;
                    }
                }
            }
            Holder<Enchantment> stabilityHolder = me.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.NEEDED_STABILITY);
            level = me.getItem().getEnchantmentLevel(stabilityHolder);
            if(level>0 && me.getRandom().nextFloat()<0.002 && me.getItem().getItem() instanceof BlockItem bi){
                BlockPos pos = me.blockPosition();
                BlockState stateToPlace = bi.getBlock().defaultBlockState();
                ServerLevel serverLevel = (ServerLevel) me.level();

                if (me.level().getBlockState(pos).canBeReplaced()
                        && stateToPlace.canSurvive(me.level(), pos)
                        && me.level().isUnobstructed(null, stateToPlace.getCollisionShape(me.level(), pos).move(pos.getX(), pos.getY(), pos.getZ()))) {

                    ItemStack stack = me.getItem();
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        me.discard();
                    } else {
                        me.setItem(stack);
                    }
                }
            }
        }
        ItemStack stack = me.getItem();
        if (stack.is(ModItems.SUPERHEATED_SPEAR.get()) && me.isInWater()) {
            Level level = me.level();
                me.setItem(new ItemStack(ModItems.MELLEABLE_SPEAR.get()));
                level.playSound(null, me.getX(), me.getY(), me.getZ(),
                        SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.0f);
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CLOUD,
                            me.getX(), me.getY(), me.getZ(),
                            12, 0.3, 0.1, 0.3, 0.02);
                }
        }
        if (stack.is(ModItems.REPAIRED_SPEAR.get()) && me.isInPowderSnow) {
            Level level = me.level();
            me.setItem(new ItemStack(ModItems.SPEAR_BLANK.get()));
            level.playSound(null, me.getX(), me.getY(), me.getZ(),
                    SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.0f);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        me.getX(), me.getY(), me.getZ(),
                        12, 0.3, 0.1, 0.3, 0.02);
            }
        }
    }

    public boolean randomTeleport(Entity e,double x, double y, double z, boolean broadcastTeleport) {
        double d0 = e.getX();
        double d1 = e.getY();
        double d2 = e.getZ();
        double d3 = y;
        boolean flag = false;
        BlockPos blockpos = BlockPos.containing(x, y, z);
        Level level = e.level();
        if (level.hasChunkAt(blockpos)) {
            boolean flag1 = false;

            while(!flag1 && blockpos.getY() > level.getMinBuildHeight()) {
                BlockPos blockpos1 = blockpos.below();
                BlockState blockstate = level.getBlockState(blockpos1);
                if (blockstate.blocksMotion()) {
                    flag1 = true;
                } else {
                    --d3;
                    blockpos = blockpos1;
                }
            }

            if (flag1) {
                e.teleportTo(x, d3, z);
                if (level.noCollision(e) && !level.containsAnyLiquid(e.getBoundingBox())) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            e.teleportTo(d0, d1, d2);
            return false;
        } else {
            if (broadcastTeleport) {
                level.broadcastEntityEvent(e, (byte)46);
            }

            return true;
        }
    }
}
