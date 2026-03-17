package net.blumasc.excitingenchants.event;

import com.ibm.icu.impl.Pair;
import net.blumasc.blubasics.damage.BaseModDamageTypes;
import net.blumasc.blubasics.effect.BaseModEffects;
import net.blumasc.blubasics.entity.custom.projectile.BlockProjectileEntity;
import net.blumasc.blubasics.entity.custom.projectile.PickaxeBoomerangEntity;
import net.blumasc.blubasics.entity.custom.projectile.ShardProjectileEntity;
import net.blumasc.blubasics.entity.custom.projectile.SpikeEntity;
import net.blumasc.blubasics.sound.BaseModSounds;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.block.entity.custom.BreakingBlockEntity;
import net.blumasc.excitingenchants.block.entity.custom.TrapBlockEntity;
import net.blumasc.excitingenchants.damage.ModDamageTypes;
import net.blumasc.excitingenchants.datagen.ModChestLoot;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.entity.ModEntities;
import net.blumasc.excitingenchants.entity.custom.BalloonEntity;
import net.blumasc.excitingenchants.entity.custom.RedLightning;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.mixin.AbstractArrowMixin;
import net.blumasc.excitingenchants.network.DiviningDataSyncPacket;
import net.blumasc.excitingenchants.network.SoulDataSyncPacket;
import net.blumasc.excitingenchants.particle.MagneticItemParticleOption;
import net.blumasc.excitingenchants.sound.ModSounds;
import net.blumasc.excitingenchants.state.PlayerEnchantmentState;
import net.blumasc.excitingenchants.state.PlayerEnchantmentStateHandler;
import net.blumasc.excitingenchants.state.RedstoneSignalManager;
import net.blumasc.excitingenchants.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;

@EventBusSubscriber(modid = ExcitingEnchantsMod.MODID)
public class EnchantmentEventHandlers {

    @SubscribeEvent
    public static void onDivinerPlayerTick(PlayerTickEvent.Pre event) {

        Player player = event.getEntity();
        if (player.tickCount % 10 != 0) return;

        if (player.level().isClientSide) return;

        Holder<Enchantment> shrinkHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.DIVINER);

        ItemStack boots = player.getItemBySlot(EquipmentSlot.HEAD);
        int level = boots.getEnchantmentLevel(shrinkHolder);

        if (level <= 0) return;
        double radius = 10 + (5 * level);
        AABB area = new AABB(
                player.getX() - radius, player.getY() - radius, player.getZ() - radius,
                player.getX() + radius, player.getY() + radius, player.getZ() + radius
        );

        List<Vec3> mobPositions = new ArrayList<>();
        List<Vec3> playerPositions = new ArrayList<>();

        for (LivingEntity entity : player.level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (entity == player) continue;

            Vec3 pos = entity.position();
            if (entity instanceof Player && level >= 3) {
                playerPositions.add(pos);
            } else if (entity instanceof Mob mob && mob.getTarget() == player) {
                mobPositions.add(pos);
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new DiviningDataSyncPacket(mobPositions, playerPositions)
            );
        }
    }

    @SubscribeEvent
    public static void onBalloonPlayerTick(PlayerTickEvent.Pre event) {

        Player player = event.getEntity();

        if (player.level().isClientSide) return;

        if(player.tickCount%10!=0) return;

        if(!player.onGround()) return;

        int neededBalloonCount = 0;

        Holder<Enchantment> heliumHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.HELIUM);

        Holder<Enchantment> bulletTimeHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.BULLET_TIME);

        ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offHand = player.getItemBySlot(EquipmentSlot.OFFHAND);

        neededBalloonCount += mainHand.getEnchantmentLevel(bulletTimeHolder);
        neededBalloonCount += offHand.getEnchantmentLevel(bulletTimeHolder);
        neededBalloonCount += (player.getInventory().items.stream().filter(i -> i.getEnchantmentLevel(heliumHolder)>0).count()/5);
        List<BalloonEntity> actualEntities = player.level().getNearbyEntities(BalloonEntity.class, TargetingConditions.DEFAULT, player, new AABB(player.getOnPos()).inflate(6))
                .stream()
                .filter(b -> player.getUUID().equals(b.getBoundEntity() != null ? b.getBoundEntity().getUUID() : null))
                .toList();
        while(actualEntities.size()>neededBalloonCount){
            actualEntities.get(0).pop();
            actualEntities.remove(0);
        }
        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);
        if(actualEntities.size()<neededBalloonCount){
            BalloonEntity be = new BalloonEntity(ModEntities.BALLOON.get(), player.level());
            be.moveTo(player.getEyePosition());
            be.init(player);
            player.level().addFreshEntity(be);
            pes.setBalloonCount(pes.getBalloonCount()+1);
        }
        PlayerEnchantmentStateHandler.saveState(player, pes);
    }

    @SubscribeEvent
    public static void onBulletTimeTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        if(player.onGround())return;

        ItemStack item = player.getMainHandItem();

        Holder<Enchantment> bulletTimeHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.BULLET_TIME);

        int level = item.getEnchantmentLevel(bulletTimeHolder);
        if (level <= 0) return;

        player.addEffect(new MobEffectInstance(
                MobEffects.SLOW_FALLING,
                2,
                level - 1,
                true,
                false
        ));
    }

    @SubscribeEvent
    public static void onHungryPlayerTick(PlayerTickEvent.Pre event) {

        Player player = event.getEntity();

        if (player.tickCount % 20 != 0) return;

        Holder<Enchantment> hungryHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.HUNGRY);

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        int level = helmet.getEnchantmentLevel(hungryHolder);
        if (level > 0 && player.getFoodData().needsFood()) {
            PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);
            if (!pes.isOnCooldown(ModEnchantments.HUNGRY.location(), player.level().getGameTime())) {
                ItemStack food = selectFood(player);
                if (food.isEmpty()) return;

                fakeEat(player, food);
                pes.setCooldown(ModEnchantments.HUNGRY.location(), player.level().getGameTime() + 5 * 20);
                PlayerEnchantmentStateHandler.saveState(player, pes);
            }
        }
    }

    private static void fakeEat(Player player, ItemStack foodStack) {
        FoodProperties food = foodStack.getFoodProperties(player);
        if (food == null) return;

        player.getFoodData().eat((int) (food.nutrition()*0.8), (float) (food.saturation()*0.8));

        player.level().playSound(
                null,
                player.getX(),
                player.getEyeY(),
                player.getZ(),
                SoundEvents.GENERIC_EAT,
                SoundSource.PLAYERS,
                0.8F,
                0.9F + player.level().random.nextFloat() * 0.2F
        );
        ItemParticleOption particle =
                new ItemParticleOption(ParticleTypes.ITEM, foodStack);

        if (player.level() instanceof ServerLevel sl) {

            sl.sendParticles(
                    particle,
                    player.getX(),
                    player.getEyeY() ,
                    player.getZ(),
                    20,
                    0.25, 0.25, 0.25,
                    0.05
            );
        }
        ItemStack result = foodStack.finishUsingItem(player.level(), player);
        foodStack.shrink(1);
        if (!result.isEmpty() && result != foodStack) {
            if (!player.getInventory().add(result)) {
                player.drop(result, false);
            }
        }
    }

    private static ItemStack selectFood(Player player) {
        if (isValidFood(player.getMainHandItem(), player))
            return player.getMainHandItem();

        if (isValidFood(player.getOffhandItem(), player))
            return player.getOffhandItem();

        return findBestFood(player);
    }

    private static ItemStack findBestFood(Player player) {
        int missing = 20 - player.getFoodData().getFoodLevel();

        ItemStack best = ItemStack.EMPTY;
        int bestDiff = Integer.MAX_VALUE;

        for (ItemStack stack : player.getInventory().items) {
            if (!isValidFood(stack, player)) continue;

            int foodValue = stack.getFoodProperties(player).nutrition();
            int diff = Math.abs(foodValue - missing);

            if (diff < bestDiff) {
                bestDiff = diff;
                best = stack;
            }
        }

        return best;
    }

    private static boolean isValidFood(ItemStack stack, Player player) {
        if (stack.isEmpty()) return false;
        FoodProperties food = stack.getFoodProperties(player);
        if (food == null) return false;
        return food.effects().isEmpty();
    }

    @SubscribeEvent
    public static void onBulwarkTick(PlayerTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        Holder<Enchantment> squidHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.BULWARK);

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        int level = chest.getEnchantmentLevel(squidHolder);
        if (level > 0) {

            if (player.isCrouching()) {
                player.addEffect(new MobEffectInstance(
                        ModEffects.CASTLE_MODE, 5 * 20, level-1, false, false, false
                ));
            }
        }
    }

    @SubscribeEvent
    public static void onMidAirJump(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);

        if (player.onGround()) {
            Optional<BlockPos> op_below = player.mainSupportingBlockPos;
            BlockPos below = op_below.isEmpty() ? player.getOnPos() : op_below.get();
            BlockState belowState = player.level().getBlockState(below);
            if (belowState.getBlock() != ModBlocks.CLOUD_BLOCK.get()
                    && !belowState.getCollisionShape(player.level(), below).isEmpty()) {
                pes.resetCloudStepJumps();
            }
            PlayerEnchantmentStateHandler.saveState(player, pes);
        }
    }

    @SubscribeEvent
    public static void onSquidHit(LivingDamageEvent.Post event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity.getLastDamageSource() == null) return;
        Entity causer = event.getEntity().getLastDamageSource().getEntity();
        if (causer instanceof LivingEntity livingcauser) {
            if (entity.distanceToSqr(livingcauser) > 5 * 5) return;
            Holder<Enchantment> squidHolder = entity.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.SQUID_ENCOUNTER);

            ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
            int level = chest.getEnchantmentLevel(squidHolder);
            if (level > 0) {
                PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(entity);
                if (!pes.isOnCooldown(ModEnchantments.SQUID_ENCOUNTER.location(), entity.level().getGameTime())) {
                    int inkRadius = 4;
                    List<LivingEntity> nearby = entity.level().getEntitiesOfClass(
                            LivingEntity.class,
                            entity.getBoundingBox().inflate(inkRadius),
                            e -> e != entity
                    );

                    for (LivingEntity target : nearby) {
                        target.addEffect(new MobEffectInstance(ModEffects.INKED, 15 * 20, level));
                    }
                    placeInkAround(entity, (ServerLevel) entity.level(), entity.getRandom());
                    entity.level().playSound(
                            null,
                            entity.getX(), entity.getY(), entity.getZ(),
                            SoundEvents.SQUID_HURT,
                            SoundSource.PLAYERS,
                            1.0f,
                            1.0f
                    );
                    if (livingcauser.level() instanceof ServerLevel world) {
                        double x = livingcauser.getX();
                        double y = livingcauser.getY() + livingcauser.getBbHeight() / 2.0;
                        double z = livingcauser.getZ();
                        RandomSource random = world.getRandom();

                        for (int i = 0; i < 20; i++) {
                            world.sendParticles(
                                    ParticleTypes.SQUID_INK,
                                    x + (random.nextDouble() - 0.5),
                                    y + (random.nextDouble() - 0.5),
                                    z + (random.nextDouble() - 0.5),
                                    1,
                                    0.0, 0.0, 0.0,
                                    0.05
                            );
                        }
                    }
                    pes.setCooldown(ModEnchantments.SQUID_ENCOUNTER.location(), entity.level().getGameTime() + (20 * 60 * (6 - level)));
                    PlayerEnchantmentStateHandler.saveState(entity, pes);
                }
            }
        }

    }

    private static void placeInkAround(LivingEntity target, ServerLevel world, RandomSource random) {
        BlockPos feet = BlockPos.containing(
                target.getX(),
                target.getY(),
                target.getZ()
        );

        int count = 3 + random.nextInt(3);
        int attempts = 0;

        while (count > 0 && attempts < 20) {
            attempts++;
            int dx = random.nextInt(5) - 2;
            int dz = random.nextInt(5) - 2;
            BlockPos candidate = feet.offset(dx, 0, dz);
            BlockState existing = world.getBlockState(candidate);

            if (existing.isAir() || existing.canBeReplaced()) {
                BlockState below = world.getBlockState(candidate.below());
                if (below.isFaceSturdy(world, candidate.below(), Direction.UP)) {
                    world.setBlock(candidate, ModBlocks.INK_LAYER_BLOCK.get().defaultBlockState(), 3);
                    count--;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCleaveHit(LivingShieldBlockEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;

        Entity causer = event.getDamageSource().getEntity();
        if (!(causer instanceof LivingEntity attacker)) return;

        Holder<Enchantment> cleaveHolder = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.CLEAVING);

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(cleaveHolder);
        if (level <= 0) return;

        ItemStack shield = target.getUseItem();
        if (!target.isBlocking()) return;

        int disableTicks = 20 * 20 * level;
        if (target instanceof Player player) {
            player.getCooldowns().addCooldown(shield.getItem(), disableTicks);
        }
    }
    @SubscribeEvent
    public static void onSoulRetrieverKill(LivingDeathEvent event) {
        LivingEntity deadEntity = event.getEntity();
        if (deadEntity.level().isClientSide()) return;

        DamageSource source = event.getSource();

        Entity killerEntity = source.getEntity();
        if (!(killerEntity instanceof LivingEntity attacker)) return;

        if (!(attacker instanceof Player player)) return;
        Holder<Enchantment> soulRetrieverHolder = deadEntity.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SOUL_RETRIEVER);

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(soulRetrieverHolder);
        if (level <= 0) return;
        if(deadEntity.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, deadEntity.getX(), deadEntity.getY(), deadEntity.getZ(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0f, player.getRandom().nextFloat()+0.5f);
            serverLevel.sendParticles(
                    ParticleTypes.SCULK_SOUL,
                    deadEntity.getX(), deadEntity.getY(), deadEntity.getZ(),
                    20, 0.2, 0.2, 0.2, 0.05
            );
        }

        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);
        ResourceLocation entityType = BuiltInRegistries.ENTITY_TYPE
                .getKey(event.getEntity().getType());
        pes.addSoul(entityType);
        PlayerEnchantmentStateHandler.saveState(player, pes);
    }

    @SubscribeEvent
    public static void onSoulBlock(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        Holder<Enchantment> soulRetrieverHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SOUL_RETRIEVER);

        ItemStack weapon = player.getMainHandItem();
        int level = weapon.getEnchantmentLevel(soulRetrieverHolder);
        if (level <= 0) return;

        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);
        List<ResourceLocation> souls = pes.getSouls();
        if (souls.isEmpty()) return;

        Entity attacker = event.getSource().getEntity();
        if (attacker == null) return;

        Vec3 attackDir = attacker.position()
                .subtract(player.position())
                .normalize();
        for (int i = 0; i < souls.size(); i++) {
            float angle = getSoulAngle(player, i, souls.size());
            float radius = 0.8f;
            Vec3 soulPos = new Vec3(
                    Math.cos(angle) * radius,
                    0,
                    Math.sin(angle) * radius
            ).normalize();
            double dot = soulPos.dot(attackDir);
            if (dot > 0.5) {
                event.setCanceled(true);
                pes.removeSoul(i);
                PlayerEnchantmentStateHandler.saveState(player, pes);
                if (player.level() instanceof ServerLevel serverLevel) {
                    double sx = player.getX() + Math.cos(angle) * radius;
                    double sy = player.getY() + 0.5;
                    double sz = player.getZ() + Math.sin(angle) * radius;
                    serverLevel.playSound(null, sx, sy,sz, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0f, player.getRandom().nextFloat()+0.5f);
                    serverLevel.sendParticles(
                            ParticleTypes.SCULK_SOUL,
                            sx, sy, sz,
                            20, 0.2, 0.2, 0.2, 0.05
                    );
                }
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);
        pes.getSouls().clear();
        PlayerEnchantmentStateHandler.saveState(player, pes);
    }

    private static float getSoulAngle(Player player, int soulIndex, int numSouls) {
        long gameTime = player.level().getGameTime();
        float baseAngle = (float)(soulIndex * (2 * Math.PI / numSouls));
        float orbitAngle = baseAngle + (gameTime * 0.05f);
        return orbitAngle;
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        syncSouls(player);
    }

    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getTarget() instanceof ServerPlayer tracked)) return;
        if (!(event.getEntity() instanceof ServerPlayer observer)) return;

        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(tracked);
        PacketDistributor.sendToPlayer(observer,
                new SoulDataSyncPacket(tracked.getUUID(), pes.getSouls())
        );
    }

    public static void syncSouls(Player player) {
        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new SoulDataSyncPacket(player.getUUID(), pes.getSouls())
        );
    }

    @SubscribeEvent
    public static void onReapingHit(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;

        Entity causer = event.getSource().getEntity();
        if (!(causer instanceof LivingEntity attacker)) return;

        Holder<Enchantment> reapingHolder = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.REAPING);

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(reapingHolder);
        if (level <= 0) return;

        List<MobEffectInstance> eligibleEffects = target.getActiveEffects().stream()
                .filter(MobEffectInstance::showIcon)
                .filter(e -> e.getEffect().value().getCategory() != MobEffectCategory.HARMFUL)
                .toList();

        if (eligibleEffects.isEmpty()) return;

        RandomSource random = target.level().getRandom();
        MobEffectInstance effect = eligibleEffects.get(random.nextInt(eligibleEffects.size()));

        int stolenDuration = (int) (effect.getDuration() * 0.1f * level);
        if (stolenDuration <= 0) return;

        attacker.addEffect(new MobEffectInstance(
                effect.getEffect(),
                stolenDuration,
                1,
                false,
                true
        ));
        if (target.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, random.nextFloat()+0.5f);
            int color = effect.getEffect().value().getColor();
            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            double startX = target.getX();
            double startY = target.getY() + target.getBbHeight() / 2.0;
            double startZ = target.getZ();
            double endX = attacker.getX();
            double endY = attacker.getY() + attacker.getBbHeight() / 2.0;
            double endZ = attacker.getZ();

            int particleCount = 12;
            for (int i = 0; i < particleCount; i++) {
                float t = i / (float) particleCount;
                double x = startX + (endX - startX) * t + (random.nextDouble() - 0.5) * 0.3;
                double y = startY + (endY - startY) * t + (random.nextDouble() - 0.5) * 0.3;
                double z = startZ + (endZ - startZ) * t + (random.nextDouble() - 0.5) * 0.3;

                serverLevel.sendParticles(
                        new DustParticleOptions(new Vector3f(r, g, b), 1.0f),
                        x, y, z,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }
    }



    @SubscribeEvent
    public static void onFrenzyHit(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target == null) return;
        if (target.level().isClientSide()) return;
        if (event.getSource() == null) return;

        Entity causer = event.getSource().getEntity();
        if (!(causer instanceof LivingEntity attacker)) return;

        Holder<Enchantment> frenzyHolder = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.FRENZY);

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(frenzyHolder);
        if (level <= 0) return;

        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(attacker);
        float mult = (pes.getFrenzyCount(target.getUUID())-1.0f)/2.0f;

        float multiplier = 1.0f + (0.20f * level * mult);
        event.setAmount(event.getAmount() * multiplier);

        pes.setFrenzyLastAttackedEntity(target.getUUID());
        PlayerEnchantmentStateHandler.saveState(attacker, pes);
    }

    @SubscribeEvent
    public static void onDequipingHit(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;

        Entity causer = event.getSource().getEntity();
        if (!(causer instanceof LivingEntity attacker)) return;

        Holder<Enchantment> dequipingHolder = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.DEQUIPING);

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(dequipingHolder);
        if (level <= 0) return;

        event.setAmount(1.0f);

        float chance = 0.10f + 0.05f * level;
        RandomSource random = target.level().getRandom();
        if (random.nextFloat() > chance) return;

        List<EquipmentSlot> armorSlots = List.of(
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        );

        EquipmentSlot slot = armorSlots.get(random.nextInt(armorSlots.size()));
        ItemStack armor = target.getItemBySlot(slot);
        if (armor.isEmpty()) return;

        Holder<Enchantment> bindingHolder = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.BINDING_CURSE);
        if (armor.getEnchantmentLevel(bindingHolder)>0) return;
        target.setItemSlot(slot, ItemStack.EMPTY);
        if (!target.level().isClientSide()) {
            ItemEntity dropped = new ItemEntity(
                    target.level(),
                    target.getX(), target.getY(), target.getZ(),
                    armor
            );
            target.level().addFreshEntity(dropped);
        }
    }

    @SubscribeEvent
    public static void onForkPost(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;

        Entity causer = event.getSource().getEntity();
        if (!(causer instanceof Player player)) return;

        ItemStack weapon = player.getMainHandItem();

        Holder<Enchantment> forkHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.FORK);

        int level = weapon.getEnchantmentLevel(forkHolder);
        if (level <= 0) return;

        float damage = event.getNewDamage();
        int hungerGain = Math.max(1, (int) (damage * 0.5f));
        float saturationGain = hungerGain * 0.3f;
        target.level().playSound(null, causer.getX(), causer.getY(), causer.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8f, causer.getRandom().nextFloat()+0.5f);
        player.getFoodData().eat(hungerGain, saturationGain);
    }



    @SubscribeEvent
    public static void onGravityImplosion(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;

        Entity causer = event.getSource().getEntity();
        if (!(causer instanceof LivingEntity attacker)) return;

        if (!(attacker instanceof Player player)) return;
        ItemStack weapon = player.getMainHandItem();

        if (player.fallDistance < 1.5f) return;
        if (player.onGround()) return;
        if (player.isFallFlying()) return;

        Holder<Enchantment> gravityHolder = attacker.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.GRAVITY_IMPLOSION);

        int level = weapon.getEnchantmentLevel(gravityHolder);
        if (level <= 0) return;

        double radius = 3.0 + level;

        List<LivingEntity> entities = attacker.level().getEntitiesOfClass(
                LivingEntity.class,
                new AABB(
                        attacker.getX() - radius, attacker.getY() - radius, attacker.getZ() - radius,
                        attacker.getX() + radius, attacker.getY() + radius, attacker.getZ() + radius
                ),
                e -> e != attacker && e != target
        );

        for (LivingEntity mob : entities) {
            if (mob instanceof Player p && p.isShiftKeyDown()) continue;

            double dx = attacker.getX() - mob.getX();
            double dy = attacker.getY() - mob.getY();
            double dz = attacker.getZ() - mob.getZ();

            double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
            if (distance < 0.001) continue;

            double pullStrength = 1.3;
            mob.setDeltaMovement(
                    mob.getDeltaMovement().add(
                            (dx / distance) * pullStrength,
                            (dy / distance) * pullStrength,
                            (dz / distance) * pullStrength
                    )
            );

            mob.hasImpulse = true;
        }
    }







    @SubscribeEvent
    public static void projectilePocketHit(ProjectileImpactEvent event) {
        HitResult result = event.getRayTraceResult();
        if (result.getType() == HitResult.Type.ENTITY) {
            Entity hitEntity = ((EntityHitResult) result).getEntity();
            if (hitEntity instanceof Player p) {
                Holder<Enchantment> pocketsHolder = p.level().registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(ModEnchantments.COLLECTING_POCKETS);

                ItemStack legs = p.getItemBySlot(EquipmentSlot.LEGS);
                int level = legs.getEnchantmentLevel(pocketsHolder);
                if (p.getRandom().nextFloat() < (0.1 + (0.1 * level))) {
                    ItemStack out = null;
                    Projectile pr = event.getProjectile();
                    if (pr instanceof ThrowableItemProjectile tip) {
                        out = tip.getItem();
                    }
                    if (pr instanceof AbstractArrow aa) {
                        out = aa.getPickupItemStackOrigin();
                    }
                    if (pr instanceof Fireball) {
                        out = Items.FIRE_CHARGE.getDefaultInstance();
                    }
                    if (pr instanceof AbstractWindCharge) {
                        out = Items.WIND_CHARGE.getDefaultInstance();
                    }
                    if (out != null) {
                        p.addItem(out);
                    }

                }
            }
        }
    }

    @SubscribeEvent
    public static void arrowPinningHit(ProjectileImpactEvent event) {
        Projectile pr = event.getProjectile();
        if (pr instanceof AbstractArrow aa) {
            ItemStack source = aa.getWeaponItem();
            Holder<Enchantment> pinningHolder = aa.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.PINNING);
            if (source == null) return;
            int level = source.getEnchantmentLevel(pinningHolder);
            if (level > 0) {
                if (aa.getDeltaMovement().length() >= 2.0) {
                    if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
                        Entity hit = ((EntityHitResult) event.getRayTraceResult()).getEntity();
                        if (hit instanceof LivingEntity le) {
                            le.addEffect(new MobEffectInstance(ModEffects.PINNED, 15 * level * 20));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void arrowRopedHit(ProjectileImpactEvent event) {
        Projectile pr = event.getProjectile();
        if (pr instanceof AbstractArrow aa) {
            ItemStack source = aa.getWeaponItem();
            Holder<Enchantment> ropedHolder = aa.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.ROPED);
            if (source == null) return;
            int level = source.getEnchantmentLevel(ropedHolder);
            if (level > 0) {
                if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
                    Entity hit = ((EntityHitResult) event.getRayTraceResult()).getEntity();
                    if (aa.getOwner() != null) {
                        if (aa.getOwner().level() == hit.level()) {
                            Vec3 shooterPos = aa.getOwner().position();

                            Vec3 dir = shooterPos.subtract(hit.position()).normalize();
                            dir = new Vec3(dir.x, Mth.clamp(dir.y, -0.1, 0.1), dir.z);
                            Vec3 velocity = dir.scale(0.8 * level);

                            hit.setDeltaMovement(velocity);
                            hit.hasImpulse = true;
                            hit.level().playSound(null, hit.getX(), hit.getY(), hit.getZ(), ModSounds.ROPE.get(), aa.getSoundSource());
                            aa.getOwner().level().playSound(null, aa.getOwner().getX(), aa.getOwner().getY(), aa.getOwner().getZ(), ModSounds.ROPE.get(), aa.getOwner().getSoundSource());
                            if (aa.getOwner() instanceof Player shooter
                                    && hit instanceof Mob mob
                                    && !mob.isLeashed()) {

                                double distance = shooter.distanceTo(mob);
                                if (distance <= 13.0) {
                                    for (int i = 0; i < shooter.getInventory().getContainerSize(); i++) {
                                        ItemStack stack = shooter.getInventory().getItem(i);
                                        if (stack.is(Items.LEAD)) {
                                            mob.setLeashedTo(shooter, true);
                                            if (!shooter.isCreative()) {
                                                stack.shrink(1);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void arrowSharingHit(ProjectileImpactEvent event) {
        Projectile pr = event.getProjectile();
        if (pr instanceof AbstractArrow aa) {
            ItemStack source = aa.getWeaponItem();
            Holder<Enchantment> sharingHolder = aa.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.SHARING);
            if (source == null) return;
            int level = source.getEnchantmentLevel(sharingHolder);
            if (level > 0) {
                if (event.getRayTraceResult().getType() == HitResult.Type.ENTITY) {
                    Entity hit = ((EntityHitResult) event.getRayTraceResult()).getEntity();
                    if (hit instanceof LivingEntity hitLiving) {
                        if (aa.getOwner() != null) {
                            if (aa.getOwner() instanceof LivingEntity shooterLiving) {
                                Collection<MobEffectInstance> effects = shooterLiving.getActiveEffects();
                                if (!effects.isEmpty()) {
                                    MobEffectInstance chosen = effects.stream()
                                            .filter(MobEffectInstance::showIcon)
                                            .skip(shooterLiving.level().getRandom().nextInt(effects.size()))
                                            .findFirst()
                                            .orElse(null);

                                    if (chosen != null) {
                                        if (hit.level() instanceof ServerLevel serverLevel) {
                                            serverLevel.playSound(null, hit.getX(), hit.getY(), hit.getZ(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, serverLevel.random.nextFloat()+0.5f);
                                            int color = chosen.getEffect().value().getColor();
                                            float r = ((color >> 16) & 0xFF) / 255.0f;
                                            float g = ((color >> 8) & 0xFF) / 255.0f;
                                            float b = (color & 0xFF) / 255.0f;

                                            double startX = shooterLiving.getX();
                                            double startY = shooterLiving.getY() + shooterLiving.getBbHeight() / 2.0;
                                            double startZ = shooterLiving.getZ();
                                            double endX = hit.getX();
                                            double endY = hit.getY() + hit.getBbHeight() / 2.0;
                                            double endZ = hit.getZ();

                                            int particleCount = 12;
                                            for (int i = 0; i < particleCount; i++) {
                                                float t = i / (float) particleCount;
                                                double x = startX + (endX - startX) * t + (serverLevel.random.nextDouble() - 0.5) * 0.3;
                                                double y = startY + (endY - startY) * t + (serverLevel.random.nextDouble() - 0.5) * 0.3;
                                                double z = startZ + (endZ - startZ) * t + (serverLevel.random.nextDouble() - 0.5) * 0.3;

                                                serverLevel.sendParticles(
                                                        new DustParticleOptions(new Vector3f(r, g, b), 1.0f),
                                                        x, y, z,
                                                        1,
                                                        0, 0, 0,
                                                        0
                                                );
                                            }
                                        }
                                        int duration = Math.min(20 * 5 * level, chosen.getDuration());
                                        MobEffectInstance newEffect = new MobEffectInstance(
                                                chosen.getEffect(),
                                                duration,
                                                0
                                        );

                                        hitLiving.addEffect(newEffect);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onReverberating(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        Level world = (Level) event.getLevel();
        BlockPos origin = event.getPos();
        ItemStack stack = player.getMainHandItem();

        Holder<Enchantment> reverberatingHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.REVERBERATING);

        int enchantLevel = stack.getEnchantmentLevel(reverberatingHolder);
        if (enchantLevel <= 0) return;

        BlockState originalState = world.getBlockState(origin);
        if (!stack.isCorrectToolForDrops(originalState)) return;

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos neighbor = origin.offset(dx, dy, dz);
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        int maxExtraBlocks = (int) Math.pow(2, enchantLevel + 2);
        int brokenCount = 0;

        while (!queue.isEmpty() && brokenCount < maxExtraBlocks) {
            BlockPos pos = queue.poll();
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() != originalState.getBlock()) continue;

            if (world.getBlockEntity(pos) != null) continue;

            double distance = Math.sqrt(origin.distSqr(pos));
            int totalTime = Math.max(5, (int)(distance * 10));

            world.setBlock(pos, ModBlocks.BREAKING_BLOCK.get().defaultBlockState(), 3);

            if (world.getBlockEntity(pos) instanceof BreakingBlockEntity breakingBE) {
                breakingBE.setHeldState(state);
                breakingBE.setTool(stack.copy());
                breakingBE.setTotalTime(totalTime);
                breakingBE.setPlayer(player);
            }

            brokenCount++;

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos neighbor = pos.offset(dx, dy, dz);
                        if (!visited.contains(neighbor)) {
                            queue.add(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }
        }
        if(brokenCount>0){
            world.playSound((Player) null, origin, ModSounds.RUMBLE.get(), SoundSource.BLOCKS, 1.0f, (world.random.nextFloat()*0.8F)+0.8f);
        }
    }

    @SubscribeEvent
    public static void onScorchBreak(BlockDropsEvent event) {
        Entity e = event.getBreaker();
        if (e == null) return;
        ItemStack stack = e.getWeaponItem();
        if (stack == null) return;
        if (e.level().isClientSide) return;

        Holder<Enchantment> scorchHolder = e.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SCORCH);
        int enchantLevel = stack.getEnchantmentLevel(scorchHolder);
        if (enchantLevel <= 0) return;

        List<ItemEntity> newDrops = new ArrayList<>();
        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack drop = dropEntity.getItem();
            ItemStack smelted = getSmeltingResult(e.level(), drop);

            if (smelted != null && !smelted.isEmpty()) {
                ItemEntity newEntity = new ItemEntity(
                        e.level(),
                        dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                        smelted.copy()
                );
                newDrops.add(newEntity);

                if (!e.level().isClientSide) {
                    ((ServerLevel) e.level()).sendParticles(ParticleTypes.FLAME,
                            dropEntity.getX() + 0.5, dropEntity.getY() + 0.5, dropEntity.getZ() + 0.5,
                            2, 0.25, 0.25, 0.25, 0.02);
                }
            } else {
                newDrops.add(dropEntity);
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(newDrops);
    }

    @SubscribeEvent
    public static void onScorchKill(LivingDropsEvent event) {
        DamageSource d = event.getSource();
        if (d == null) return;
        Entity e = d.getEntity();
        if (e == null) return;
        ItemStack stack = e.getWeaponItem();
        if (stack == null) return;
        if (e.level().isClientSide) return;

        Holder<Enchantment> scorchHolder = e.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SCORCH);
        int enchantLevel = stack.getEnchantmentLevel(scorchHolder);
        if (enchantLevel <= 0) return;

        List<ItemEntity> newDrops = new ArrayList<>();
        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack drop = dropEntity.getItem();
            ItemStack smelted = getSmeltingResult(e.level(), drop);

            if (smelted != null && !smelted.isEmpty()) {
                ItemEntity newEntity = new ItemEntity(
                        e.level(),
                        dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                        smelted.copy() // fresh copy
                );
                newDrops.add(newEntity);

                if (!e.level().isClientSide) {
                    ((ServerLevel) e.level()).sendParticles(ParticleTypes.FLAME,
                            dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                            2, 0.25, 0.25, 0.25, 0.02);
                }
            } else {
                newDrops.add(dropEntity);
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(newDrops);
    }

    private static @Nullable ItemStack getSmeltingResult(Level l, ItemStack input) {
        RecipeManager rm = l.getRecipeManager();
        Optional<RecipeHolder<SmeltingRecipe>> recipe =
                rm.getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), l);
        if (recipe.isEmpty()) return null;

        return recipe.get().value().getResultItem(l.registryAccess());
    }

    @SubscribeEvent
    public static void onMagneticBreak(BlockDropsEvent event) {
        Entity e = event.getBreaker();
        if (e == null) return;
        if (!(e instanceof Player p)) return;
        ItemStack stack = e.getWeaponItem();
        if (stack == null) return;
        if (e.level().isClientSide) return;

        Holder<Enchantment> magneticHolder = e.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.MAGNETIC);
        int enchantLevel = stack.getEnchantmentLevel(magneticHolder);
        if (enchantLevel <= 0) return;

        List<ItemEntity> newDrops = new ArrayList<>();
        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack stackCopy = dropEntity.getItem().copy();
            if (!p.addItem(stackCopy)) {
                newDrops.add(dropEntity);
            } else {
                spawnMagneticParticles((ServerLevel) p.level(), dropEntity.position(), p, dropEntity.getItem().copy());
                p.level().playSound((Player) null, p.getX(), p.getY(), p.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.7f, 1.0f);
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(newDrops);
    }

    @SubscribeEvent
    public static void onMagneticKill(LivingDropsEvent event) {
        DamageSource d = event.getSource();
        if (d == null) return;
        Entity e = d.getEntity();
        if (e == null) return;
        if (!(e instanceof Player p)) return;
        ItemStack stack = e.getWeaponItem();
        if (stack == null) return;
        if (e.level().isClientSide) return;

        Holder<Enchantment> magneticHolder = e.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.MAGNETIC);
        int enchantLevel = stack.getEnchantmentLevel(magneticHolder);
        if (enchantLevel <= 0) return;

        List<ItemEntity> newDrops = new ArrayList<>();
        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack stackCopy = dropEntity.getItem().copy();
            if (!p.addItem(stackCopy)) {
                newDrops.add(dropEntity);
            } else {
                spawnMagneticParticles((ServerLevel) p.level(), dropEntity.position(), p, dropEntity.getItem().copy());
                p.level().playSound((Player) null, p.getX(), p.getY(), p.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.7f, 1.0f);
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(newDrops);
    }
    private static void spawnMagneticParticles(ServerLevel level, Vec3 from, Player player, ItemStack itemStack) {
        Vec3 to = player.getEyePosition();
        Vec3 dir = to.subtract(from);

        MagneticItemParticleOption particle = new MagneticItemParticleOption(itemStack);

        for (int i = 0; i < 25; i++) {
            double vx = dir.x * 0.1 + (level.random.nextDouble() - 0.5) * 0.05;
            double vy = dir.y * 0.1 + (level.random.nextDouble() - 0.5) * 0.05;
            double vz = dir.z * 0.1 + (level.random.nextDouble() - 0.5) * 0.05;

            level.sendParticles(particle,
                    from.x, from.y, from.z,
                    0,
                    vx, vy, vz,
                    1.0);
        }
    }

    public static final ResourceLocation RUN_AWAY_MODIFIER_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "run_away_modifier");

    public static final ResourceLocation LAST_STAND_MODIFIER_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "last_stand_modifier");

    @SubscribeEvent
    public static void onAirDash(PlayerTickEvent.Post event) {
        Player player = event.getEntity();

        Holder<Enchantment> dashHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.DASH);

        ItemStack boots = player.getItemBySlot(EquipmentSlot.LEGS);
        int level = boots.getEnchantmentLevel(dashHolder);
        if (level <= 0) return;

        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);

        if (player.onGround()) {
            pes.setDashUsed(false);
            PlayerEnchantmentStateHandler.saveState(player, pes);
            return;
        }

        if (!player.isCrouching()) return;
        if (pes.isDashUsed()) return;

        Vec3 look = player.getLookAngle();
        Vec3 dash = new Vec3(look.x, 0, look.z).normalize().scale(level / 2.5d);
        player.setDeltaMovement(dash);
        player.hasImpulse = true;

        if (player.level() instanceof ServerLevel world) {
            world.playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
            );
            Vec3 back = player.getLookAngle().normalize().scale(-0.4);

            world.sendParticles(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    player.getX() + back.x,
                    player.getY() + 0.1,
                    player.getZ() + back.z,
                    3,
                    0.2, 0.05, 0.2,
                    0.02
            );
        }
        pes.setDashUsed(true);
        PlayerEnchantmentStateHandler.saveState(player, pes);
    }

    @SubscribeEvent
    public static void onLiquidWalk(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();

        if (player.isCrouching()) return;

        Holder<Enchantment> liquidHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.BUOYANT);

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        int level = boots.getEnchantmentLevel(liquidHolder);
        if (level <= 0) return;
        Vec3 motion = player.getDeltaMovement();

        Level levelWorld = player.level();

        AABB box = player.getBoundingBox();

        int minX = BlockPos.containing(box.minX, player.getY() - 0.1, box.minZ).getX();
        int maxX = BlockPos.containing(box.maxX, player.getY() - 0.1, box.maxZ).getX();
        int minZ = BlockPos.containing(box.minX, player.getY() - 0.1, box.minZ).getZ();
        int maxZ = BlockPos.containing(box.maxX, player.getY() - 0.1, box.maxZ).getZ();
        int blockY = BlockPos.containing(player.getX(), player.getY() - 0.1, player.getZ()).getY();

        double highestSurface = Double.MIN_VALUE;
        boolean foundLava = false;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos pos = new BlockPos(x, blockY, z);
                FluidState fluid = levelWorld.getFluidState(pos);

                if (fluid.isEmpty() || !(fluid.is(Fluids.LAVA) || fluid.is(Fluids.FLOWING_LAVA))) continue;

                double surface = pos.getY() + fluid.getOwnHeight();
                if (surface > highestSurface) {
                    highestSurface = surface;
                    foundLava = true;
                }
            }
        }

        if (!foundLava) return;

        player.setPos(player.getX(), Math.max(highestSurface, player.getY()), player.getZ());
        player.setDeltaMovement(motion.x, Math.max(0.0, motion.y), motion.z);
        player.setOnGround(true);
        player.fallDistance = 0.0F;
    }


    @SubscribeEvent
    public static void onReverseShardHit(LivingDamageEvent.Post event) {
        if (event.getEntity().level().isClientSide()) return;

        if (event.getEntity().getLastDamageSource() == null) return;

        if (!(event.getEntity().getLastDamageSource().getEntity() instanceof Player attacker)) return;

        Holder<Enchantment> shardHolder = attacker.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SPLATTER);

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(shardHolder);
        if (level <= 0) return;

        LivingEntity target = event.getEntity();

        RandomSource random = target.level().getRandom();
        Vec3 reverseDir = attacker.getLookAngle().normalize();

        for (int i = 0; i < level; i++) {
            float yawOffset = (random.nextFloat() - 0.5f) * 30f;
            float pitchOffset = (random.nextFloat() - 0.5f) * 20f;

            Vec3 dir = rotateYaw(reverseDir, yawOffset);
            dir = rotatePitch(dir, pitchOffset);

            double spawnX = target.getX();
            double spawnY = target.getEyeY() - 0.2;
            double spawnZ = target.getZ();

            ShardProjectileEntity projectile = new ShardProjectileEntity(
                    target,
                    target.level(),
                    BaseModDamageTypes.spikeDamage(target.level()).typeHolder(),
                    0xff0000
            );

            projectile.setPos(spawnX, spawnY, spawnZ);
            projectile.shoot(dir.x, dir.y, dir.z, 1.0F, 0.2F);
            target.level().addFreshEntity(projectile);
        }
    }

    private static Vec3 rotateYaw(Vec3 vec, float degrees) {
        double radians = Math.toRadians(degrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double x = vec.x * cos - vec.z * sin;
        double z = vec.x * sin + vec.z * cos;

        return new Vec3(x, vec.y, z).normalize();
    }

    private static Vec3 rotatePitch(Vec3 vec, float degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vec3(
                vec.x,
                vec.y * cos - vec.z * sin,
                vec.y * sin + vec.z * cos
        );
    }

    @SubscribeEvent
    public static void onPickerang(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level world = player.level();
        ItemStack stack = event.getItemStack();
        if (world.isClientSide()) return;

        Holder<Enchantment> pickerangHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.PICKERANG);

        int enchantLevel = stack.getEnchantmentLevel(pickerangHolder);
        if (enchantLevel <= 0) return;
        PickaxeBoomerangEntity boomerang = new PickaxeBoomerangEntity(world, player, stack.copy(), 10 + (5 * enchantLevel));
        boomerang.setPos(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        world.addFreshEntity(boomerang);
        stack.shrink(1);
        ServerLevel level = (ServerLevel) world;
        level.playSound(
                null,
                player.blockPosition(),
                BaseModSounds.PICKERANG.get(),
                SoundSource.PLAYERS,
                1.0F,
                player.getRandom().nextFloat() + 0.5f
        );
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onShovelThrowSand(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level world = player.level();
        if (world.isClientSide) return;

        ItemStack stack = player.getItemInHand(event.getHand());

        Holder<Enchantment> sandHolder = world.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.POCKET_SAND);

        int level = stack.getEnchantmentLevel(sandHolder);
        if (level <= 0) return;

        int damage = 30 - (5 * level);
        if (stack.getMaxDamage() - stack.getDamageValue() < damage) return;

        BlockProjectileEntity projectile = new BlockProjectileEntity(
                player,
                new Vec3(
                        -Math.sin(Math.toRadians(player.getYRot())) * Math.cos(Math.toRadians(player.getXRot())),
                        -Math.sin(Math.toRadians(player.getXRot())),
                        Math.cos(Math.toRadians(player.getYRot())) * Math.cos(Math.toRadians(player.getXRot()))
                ).scale(1.5),
                world,
                Blocks.SAND.defaultBlockState()
        );
        projectile.setPos(player.getEyePosition());
        world.addFreshEntity(projectile);
        ItemStack particleStack = stack.copy();
        stack.hurtAndBreak(damage, (ServerLevel) world, player, (p) -> {
            ServerLevel l = (ServerLevel) world;
            l.playSound(
                    null,
                    player.getOnPos(),
                    SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
            ItemParticleOption particle =
                    new ItemParticleOption(ParticleTypes.ITEM, particleStack);
            l.sendParticles(
                    particle,
                    player.getX(),
                    player.getY() + 1.0,
                    player.getZ(),
                    20,
                    0.25, 0.25, 0.25,
                    0.05
            );
        });

        world.playSound(null, player.blockPosition(), SoundEvents.SAND_PLACE,
                SoundSource.PLAYERS, 1.0F, 1.2F);

        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onShovelPlaceTrap(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level world = player.level();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (world.isClientSide) return;
        Holder<Enchantment> sandHolder = world.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.TRAP_DIGGER);
        int level = stack.getEnchantmentLevel(sandHolder);
        if (level <= 0) return;
        if (stack.getMaxDamage() - stack.getDamageValue() < 20) return;
        if (event.getFace() != Direction.UP) return;
        BlockPos placePos = event.getPos().relative(event.getFace());
        if (!world.getBlockState(placePos).isAir()) return;
        if (!world.getBlockState(event.getPos()).is(BlockTags.MINEABLE_WITH_SHOVEL)) return;
        if(!(world.getBlockState(event.getPos()).isSolidRender(world, event.getPos())))return;
        world.setBlock(placePos, ModBlocks.PITFALL_TRAP.get().defaultBlockState(), 3);
        if (world.getBlockEntity(placePos) instanceof TrapBlockEntity be) {
            be.setTimer(600 - (100 * level));
        }
        ItemStack particleStack = stack.copy();
        stack.hurtAndBreak(20, (ServerLevel) world, player, (p) -> {
            ServerLevel l = (ServerLevel) world;
            l.playSound(
                    null,
                    player.getOnPos(),
                    SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
            ItemParticleOption particle =
                    new ItemParticleOption(ParticleTypes.ITEM, particleStack);

            l.sendParticles(
                    particle,
                    player.getX(),
                    player.getY() + 1.0,
                    player.getZ(),
                    20,
                    0.25, 0.25, 0.25,
                    0.05
            );
        });
        world.playSound(null, placePos, SoundEvents.ROOTED_DIRT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level world = player.level();
        ItemStack stack = player.getItemInHand(event.getHand());
        if (world.isClientSide) return;
        if (stack.getMaxDamage() - stack.getDamageValue() < 20) return;

        Holder<Enchantment> pickerangHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.EARTHEN_SPIKE);

        int enchantLevel = stack.getEnchantmentLevel(pickerangHolder);
        if (enchantLevel <= 0) return;

        BlockPos pos = event.getPos();
        if (!(world.getBlockState(pos).is(BlockTags.MINEABLE_WITH_PICKAXE))) return;
        if(!(world.getBlockState(pos).isSolidRender(world, pos)))return;

        SpikeEntity spike = new SpikeEntity(world, pos, enchantLevel, 200 * enchantLevel, player);
        world.addFreshEntity(spike);
        ItemStack particleStack = stack.copy();
        stack.hurtAndBreak(20, (ServerLevel) world, player, (p) -> {
            ServerLevel l = (ServerLevel) world;
            l.playSound(
                    null,
                    player.getOnPos(),
                    SoundEvents.ITEM_BREAK,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
            ItemParticleOption particle =
                    new ItemParticleOption(ParticleTypes.ITEM, particleStack);

            l.sendParticles(
                    particle,
                    player.getX(),
                    player.getY() + 1.0,
                    player.getZ(),
                    20,
                    0.25, 0.25, 0.25,
                    0.05
            );
        });
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void crobbowLoading(LivingGetProjectileEvent e) {
        LivingEntity player = e.getEntity();
        ItemStack s = e.getProjectileWeaponItemStack();
        Holder<Enchantment> echoingLevel = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.ECHOING);

        int enchantLevel = s.getEnchantmentLevel(echoingLevel);
        if (enchantLevel > 0) {
            if (player instanceof Player p && !p.hasInfiniteMaterials()) {
                ItemStack echoShardStack = ItemStack.EMPTY;

                for (ItemStack stack : p.getInventory().items) {
                    if (stack.is(Items.ECHO_SHARD)) {
                        echoShardStack = stack;
                        break;
                    }
                }
                e.setProjectileItemStack(echoShardStack);
            } else {
                e.setProjectileItemStack(Items.ECHO_SHARD.getDefaultInstance().copy());
            }
        }
        Holder<Enchantment> galvanizingHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.GALVANIZING);

        enchantLevel = s.getEnchantmentLevel(galvanizingHolder);
        if (enchantLevel > 0) {
            if (player instanceof Player p && !p.hasInfiniteMaterials()) {
                ItemStack lightningRodStack = ItemStack.EMPTY;

                for (ItemStack stack : p.getInventory().items) {
                    if (stack.is(Items.LIGHTNING_ROD)) {
                        lightningRodStack = stack;
                        break;
                    }
                }
                e.setProjectileItemStack(lightningRodStack);
            } else {
                e.setProjectileItemStack(Items.LIGHTNING_ROD.getDefaultInstance().copy());
            }
        }
        Holder<Enchantment> grapeShotHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.GRAPE_SHOT);

        enchantLevel = s.getEnchantmentLevel(grapeShotHolder);
        if (enchantLevel > 0) {
            if (player instanceof Player p && !p.hasInfiniteMaterials()) {
                ItemStack lightningRodStack = ItemStack.EMPTY;

                for (ItemStack stack : p.getInventory().items) {
                    if (stack.is(ItemTags.STONE_TOOL_MATERIALS)) {
                        lightningRodStack = stack;
                        break;
                    }
                }
                e.setProjectileItemStack(lightningRodStack);
            } else {
                e.setProjectileItemStack(Items.COBBLESTONE.getDefaultInstance().copy());
            }
        }
        Holder<Enchantment> sanguineLevel = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SANGUINE);

        enchantLevel = s.getEnchantmentLevel(sanguineLevel);
        if (enchantLevel > 0) {
            e.setProjectileItemStack(ModItems.BLOOD_ORB.toStack().copy());
        }
        if(e.getProjectileItemStack().isEmpty()) {
            Holder<Enchantment> livingwoodHolder = player.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.LIVING_WOOD);

            enchantLevel = s.getEnchantmentLevel(livingwoodHolder);
            if (enchantLevel > 0) {
                e.setProjectileItemStack(Items.STICK.getDefaultInstance().copy());
            }
        }
    }

    @SubscribeEvent
    public static void onShieldDevouring(LivingShieldBlockEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack shield = player.getUseItem();;
        if (shield == null) return;

        Holder<Enchantment> bouncyHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.DEVOURING);

        int enchantLevel = shield.getEnchantmentLevel(bouncyHolder);
        if (enchantLevel <= 0) return;

        Entity attacker = event.getDamageSource().getEntity();

        if (attacker == null) return;
        if (!(attacker instanceof Player attPlayer)) return;

        attPlayer.causeFoodExhaustion((float) (event.getBlockedDamage()*(0.1*enchantLevel)));

        if(attPlayer.level() instanceof ServerLevel world) {
            world.playSound(
                    null,
                    player.getOnPos(),
                    SoundEvents.GENERIC_EAT,
                    SoundSource.PLAYERS,
                    1.0F,
                    attPlayer.getRandom().nextFloat()+0.5f
            );
        }
    }
    @SubscribeEvent
    public static void onShieldContingencyBreak(PlayerDestroyItemEvent event){
        ItemStack brokenItem = event.getOriginal();
        Player entityLiving = event.getEntity();

        Holder<Enchantment> contingencyHolder = entityLiving.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.CONTINGENCY);

        int enchLevel = brokenItem.getEnchantmentLevel(contingencyHolder);
        if (enchLevel <= 0) return;

        Level level = entityLiving.level();

        if (!level.isClientSide) {
            for(int i = 0; i < 16; ++i) {
                double d0 = entityLiving.getX() + (entityLiving.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                double d1 = Mth.clamp(entityLiving.getY() + (double)(entityLiving.getRandom().nextInt(16) - 8), (double)level.getMinBuildHeight(), (double)(level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1));
                double d2 = entityLiving.getZ() + (entityLiving.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                if (entityLiving.isPassenger()) {
                    entityLiving.stopRiding();
                }

                Vec3 vec3 = entityLiving.position();
                EntityTeleportEvent.ChorusFruit chorusEvent = EventHooks.onChorusFruitTeleport(entityLiving, d0, d1, d2);
                if (entityLiving.randomTeleport(chorusEvent.getTargetX(), chorusEvent.getTargetY(), chorusEvent.getTargetZ(), true)) {
                    level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entityLiving));
                    SoundSource soundsource;
                    SoundEvent soundevent;
                    soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    soundsource = SoundSource.PLAYERS;

                    level.playSound((Player)null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), soundevent, soundsource);
                    entityLiving.resetFallDistance();
                    break;
                }
            }
        }
    }
    @SubscribeEvent
    public static void onShieldContingencyDisable(LivingShieldBlockEvent event) {
        LivingEntity entityLiving = event.getEntity();
        if (!(entityLiving instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        ItemStack shield = entityLiving.getUseItem();

        Holder<Enchantment> contingencyHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.CONTINGENCY);

        int contongencyLevel = shield.getEnchantmentLevel(contingencyHolder);
        if (contongencyLevel <= 0) return;

        Entity sourceEntity = event.getDamageContainer().getSource().getEntity();
        if (!(sourceEntity instanceof LivingEntity attacker)) return;

        ItemStack weapon = attacker.getMainHandItem();
        if (!(weapon.getItem() instanceof AxeItem)) return;

        Level level = entityLiving.level();

        if (!level.isClientSide) {
            for(int i = 0; i < 16; ++i) {
                double d0 = entityLiving.getX() + (entityLiving.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                double d1 = Mth.clamp(entityLiving.getY() + (double)(entityLiving.getRandom().nextInt(16) - 8), (double)level.getMinBuildHeight(), (double)(level.getMinBuildHeight() + ((ServerLevel)level).getLogicalHeight() - 1));
                double d2 = entityLiving.getZ() + (entityLiving.getRandom().nextDouble() - (double)0.5F) * (double)16.0F;
                if (entityLiving.isPassenger()) {
                    entityLiving.stopRiding();
                }

                Vec3 vec3 = entityLiving.position();
                EntityTeleportEvent.ChorusFruit chorusEvent = EventHooks.onChorusFruitTeleport(entityLiving, d0, d1, d2);
                if (entityLiving.randomTeleport(chorusEvent.getTargetX(), chorusEvent.getTargetY(), chorusEvent.getTargetZ(), true)) {
                    level.gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(entityLiving));
                    SoundSource soundsource;
                    SoundEvent soundevent;
                    soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    soundsource = SoundSource.PLAYERS;

                    level.playSound((Player)null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), soundevent, soundsource);
                    entityLiving.resetFallDistance();
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onShieldImmortality(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        ItemStack shield = player.getItemBySlot(EquipmentSlot.OFFHAND);
        if (shield.isEmpty() || shield.getItem() != Items.SHIELD){
            shield = player.getItemBySlot(EquipmentSlot.MAINHAND);
        }
        if (shield.isEmpty() || shield.getItem() != Items.SHIELD)return;

        Holder<Enchantment> immortalityHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.IMMORTALITY);

        int level = shield.getEnchantmentLevel(immortalityHolder);
        if (level <= 0) return;

        event.setCanceled(true);

        shield.shrink(1);

        player.setHealth(player.getMaxHealth() / 2f);

        player.removeAllEffects();

        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

        player.level().levelEvent(1038, player.blockPosition(), 0);

        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
        );

        if (player.level() instanceof ServerLevel server) {
            server.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.0
            );
        }
    }
    @SubscribeEvent
    public static void onDevilsToolNetherImpaling(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;

        DamageSource source = event.getSource();
        Entity attackerEntity = source.getEntity();
        ItemStack weapon = null;
        if ((attackerEntity instanceof LivingEntity attacker)) {
            weapon = attacker.getWeaponItem();
        }
        if(attackerEntity instanceof ThrownTrident trident){
            weapon = trident.getWeaponItem();
        }
        if(weapon == null) return;
        if (weapon.isEmpty()) return;

        Holder<Enchantment> devilHolder = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.DEVILS_TOOL);

        int level = weapon.getEnchantmentLevel(devilHolder);
        if (level <= 0) return;
        Holder<Enchantment> impalingHolder = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(Enchantments.IMPALING);
        level = weapon.getEnchantmentLevel(impalingHolder);
        if(level<=0) return;

        if(target.getType().is(EntityTypeTags.SENSITIVE_TO_IMPALING))
        {
            event.setAmount(event.getAmount()-2.5f*level);
        }

        if(target.getType().is(ModTags.EntityTypes.SENSITIVE_TO_DEVILS_IMPALING))
        {
            event.setAmount(event.getAmount()+2.5f*level);
        }
    }
    @SubscribeEvent
    public static void onPoseidonTridentImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof ThrownTrident trident)) return;
        if (trident.level().isClientSide()) return;
        if (!(event.getRayTraceResult() instanceof BlockHitResult hit)) return;

        ItemStack stack = trident.getWeaponItem();
        if (stack == null || stack.isEmpty()) return;

        Level level = trident.level();

        Holder<Enchantment> poseidonHolder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.POSEIDON);

        if (stack.getEnchantmentLevel(poseidonHolder) <= 0) return;

        Holder<Enchantment> devilHolder = level.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.DEVILS_TOOL);

        boolean hasDevilsTool = stack.getEnchantmentLevel(devilHolder) > 0;

        BlockPos placePos = hit.getBlockPos().relative(hit.getDirection());

        if (!level.getBlockState(placePos).canBeReplaced()) return;
        if (!level.getFluidState(placePos).isEmpty()) return;

        if (hasDevilsTool) {
            BlockState fireState = BaseFireBlock.getState(level, placePos);
            if (fireState != null) {
                level.setBlock(placePos, fireState, Block.UPDATE_ALL);
            }
        } else {
            level.setBlock(placePos, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    @SubscribeEvent
    public static void onSpikedHit(LivingIncomingDamageEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide()) return;

        Entity causer = event.getSource().getEntity();
        if (!(causer instanceof LivingEntity attacker)) return;

        Holder<Enchantment> spikedHolder = target.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SPIKED_STINGER);

        ItemStack weapon = attacker.getMainHandItem();
        int level = weapon.getEnchantmentLevel(spikedHolder);
        if (level <= 0) return;

        target.addEffect(new MobEffectInstance(BaseModEffects.HALLUCINATION, 10*20*level, level));
    }

    @SubscribeEvent
    public static void onMidasBreak(BlockDropsEvent event) {
        Entity e = event.getBreaker();
        if (e == null) return;
        ItemStack stack = e.getWeaponItem();
        if (stack == null) return;
        if (e.level().isClientSide) return;

        Holder<Enchantment> goldHolder = e.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.MIDAS);
        int enchantLevel = stack.getEnchantmentLevel(goldHolder);
        if (enchantLevel <= 0) return;

        List<ItemEntity> newDrops = new ArrayList<>();
        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack drop = dropEntity.getItem();
            ItemStack golded = getGoldResult(drop);

            if (golded != null && !golded.isEmpty()) {
                ItemEntity newEntity = new ItemEntity(
                        e.level(),
                        dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                        golded.copy()
                );
                newDrops.add(newEntity);

                if (!e.level().isClientSide) {
                    ((ServerLevel) e.level()).sendParticles(ParticleTypes.END_ROD,
                            dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                            2, 0.25, 0.25, 0.25, 0.02);
                }
            } else {
                ItemEntity newEntity = new ItemEntity(
                        e.level(),
                        dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                        Items.GOLD_NUGGET.getDefaultInstance().copyWithCount(drop.getCount())
                );
                newDrops.add(newEntity);
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(newDrops);
    }

    @SubscribeEvent
    public static void onMidasKill(LivingDropsEvent event) {
        DamageSource d = event.getSource();
        if (d == null) return;
        Entity e = d.getEntity();
        if (e == null) return;
        ItemStack stack = e.getWeaponItem();
        if (stack == null) return;
        if (e.level().isClientSide) return;

        Holder<Enchantment> goldHolder = e.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.MIDAS);
        int enchantLevel = stack.getEnchantmentLevel(goldHolder);
        if (enchantLevel <= 0) return;

        List<ItemEntity> newDrops = new ArrayList<>();
        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack drop = dropEntity.getItem();
            ItemStack golded = getGoldResult(drop);

            if (golded != null && !golded.isEmpty()) {
                ItemEntity newEntity = new ItemEntity(
                        e.level(),
                        dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                        golded.copy()
                );
                newDrops.add(newEntity);

                if (!e.level().isClientSide) {
                    ((ServerLevel) e.level()).sendParticles(ParticleTypes.END_ROD,
                            dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                            2, 0.25, 0.25, 0.25, 0.02);
                }
            } else {
                ItemEntity newEntity = new ItemEntity(
                        e.level(),
                        dropEntity.getX(), dropEntity.getY(), dropEntity.getZ(),
                        Items.GOLD_NUGGET.getDefaultInstance().copyWithCount(drop.getCount())
                );
                newDrops.add(newEntity);
            }
        }

        event.getDrops().clear();
        event.getDrops().addAll(newDrops);
    }

    private static ItemStack getGoldResult(ItemStack drop) {
        if(drop.is(Items.RAW_GOLD_BLOCK)) return drop.copy();
        if(drop.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","storage_blocks")))) return new ItemStack(Items.GOLD_BLOCK, drop.getCount());
        if(drop.is(ItemTags.COALS)) return new ItemStack(Items.RAW_GOLD, drop.getCount());
        if(drop.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","raw_materials")))) return new ItemStack(Items.RAW_GOLD, drop.getCount());
        if(drop.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","gems")))) return new ItemStack(Items.GOLD_INGOT, drop.getCount());
        if(drop.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","ingots")))) return new ItemStack(Items.GOLD_INGOT, drop.getCount());
        if(drop.is(ItemTags.SWORDS)) return new ItemStack(Items.GOLDEN_SWORD, drop.getCount());
        if(drop.is(ItemTags.AXES)) return new ItemStack(Items.GOLDEN_AXE, drop.getCount());
        if(drop.is(ItemTags.SHOVELS)) return new ItemStack(Items.GOLDEN_SHOVEL, drop.getCount());
        if(drop.is(ItemTags.HOES)) return new ItemStack(Items.GOLDEN_HOE, drop.getCount());
        if(drop.is(ItemTags.PICKAXES)) return new ItemStack(Items.GOLDEN_PICKAXE, drop.getCount());
        if(drop.is(ItemTags.HEAD_ARMOR)) return new ItemStack(Items.GOLDEN_HELMET, drop.getCount());
        if(drop.is(ItemTags.CHEST_ARMOR)) return new ItemStack(Items.GOLDEN_CHESTPLATE, drop.getCount());
        if(drop.is(ItemTags.LEG_ARMOR)) return new ItemStack(Items.GOLDEN_LEGGINGS, drop.getCount());
        if(drop.is(ItemTags.FOOT_ARMOR)) return new ItemStack(Items.GOLDEN_BOOTS, drop.getCount());
        if(drop.is(Items.APPLE)) return new ItemStack(Items.GOLDEN_APPLE, drop.getCount());
        if(drop.is(Items.GOLDEN_APPLE)) return new ItemStack(Items.GOLDEN_APPLE, drop.getCount());
        if(drop.is(Items.ENCHANTED_GOLDEN_APPLE)) return new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, drop.getCount());
        if(drop.is(Items.CARROT)) return new ItemStack(Items.GOLDEN_CARROT, drop.getCount());
        if(drop.is(Items.GOLDEN_CARROT)) return new ItemStack(Items.GOLDEN_CARROT, drop.getCount());
        if(drop.is(Items.MELON_SLICE)) return new ItemStack(Items.GLISTERING_MELON_SLICE, drop.getCount());
        if(drop.is(Items.GLISTERING_MELON_SLICE)) return new ItemStack(Items.GLISTERING_MELON_SLICE, drop.getCount());
        if(drop.is(Items.LEATHER_HORSE_ARMOR) ||
                drop.is(Items.IRON_HORSE_ARMOR) ||
                drop.is(Items.DIAMOND_HORSE_ARMOR)||
                drop.is(Items.GOLDEN_HORSE_ARMOR))
            return new ItemStack(Items.GOLDEN_HORSE_ARMOR, drop.getCount());
        if(drop.is(ItemTags.LOGS)) return new ItemStack(ModBlocks.GOLDEN_LOG, drop.getCount());
        if(drop.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","foods/cooked_meat")))) return new ItemStack(ModItems.COOKED_GOLDEN_MEAT.get(), drop.getCount());
        if(drop.is(ItemTags.MEAT)) return new ItemStack(ModItems.GOLDEN_MEAT.get(), drop.getCount());
        if(drop.is(ItemTags.SAPLINGS)) return new ItemStack(ModBlocks.GOLDEN_GRASS.get(), drop.getCount());
        if(drop.is(ItemTags.FLOWERS)) return new ItemStack(ModBlocks.GOLDEN_GRASS.get(), drop.getCount());
        if(drop.is(ItemTags.PLANKS)) return new ItemStack(ModBlocks.GOLDEN_PLANKS.get(), drop.getCount());
        if(drop.is(ItemTags.FENCES)) return new ItemStack(ModBlocks.GOLDEN_FENCE.get(), drop.getCount());
        if(drop.is(ItemTags.WOODEN_SLABS)) return new ItemStack(ModBlocks.GOLDEN_PLANK_SLAB.get(), drop.getCount());
        if(drop.is(ItemTags.WOODEN_STAIRS)) return new ItemStack(ModBlocks.GOLDEN_PLANK_STAIRS.get(), drop.getCount());
        if(drop.is(ItemTags.STONE_BRICKS)) return new ItemStack(ModBlocks.GOLDEN_BRICKS.get(), drop.getCount());
        if(drop.is(ItemTags.WALLS)) return new ItemStack(ModBlocks.GOLDEN_BRICK_WALL.get(), drop.getCount());
        if(drop.is(ItemTags.SLABS)) return new ItemStack(ModBlocks.GOLDEN_BRICK_SLAB.get(), drop.getCount());
        if(drop.is(ItemTags.STAIRS)) return new ItemStack(ModBlocks.GOLDEN_BRICK_STAIRS.get(), drop.getCount());
        if(drop.getItem() instanceof BlockItem) return new ItemStack(ModBlocks.GOLDEN_BLOCK.get(), drop.getCount());
        return new ItemStack(Items.GOLD_NUGGET, drop.getCount());
    }

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        Holder<Enchantment> reaping = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.GHOULS);

        int enchLevel = helmet.getEnchantmentLevel(reaping);

        ItemStack food = event.getItem();
        FoodProperties foodProps = food.getFoodProperties(player);
        if (foodProps == null) return;
        if(food.is(ModTags.Items.GHOUL_EATABLE) && enchLevel<=0){
            player.addEffect(new MobEffectInstance(ModEffects.MEAT_CRAZED, 20*60*5));
        }
        if(!food.is(ModTags.Items.GHOUL_EATABLE) && enchLevel>0){
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20*30, 3));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20*30));
        }
    }

    @SubscribeEvent
    public static void onPlayerEat(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack stack = event.getItem();
        if (!isFood(stack, player)) return;
        if (player.hasEffect(ModEffects.MEAT_CRAZED) && !stack.is(ItemTags.MEAT)) {
            event.setCanceled(true);
        }
    }

    private static boolean isFood(ItemStack stack, Player player) {
        if (stack.isEmpty()) return false;
        FoodProperties food = stack.getFoodProperties(player);
        if (food == null) return false;
        return true;
    }

    @SubscribeEvent
    public static void onPlayerStabilityTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.getRandom().nextFloat() >= 0.002f) return;

        ServerLevel serverLevel = (ServerLevel) player.level();

        Holder<Enchantment> stabilityHolder = serverLevel.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.NEEDED_STABILITY);

        List<ItemStack> candidates = new ArrayList<>();
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof BlockItem)) continue;
            if (stack.getEnchantmentLevel(stabilityHolder) <= 0) continue;
            candidates.add(stack);
        }

        if (candidates.isEmpty()) return;
        ItemStack chosen = candidates.get(player.getRandom().nextInt(candidates.size()));
        BlockItem bi = (BlockItem) chosen.getItem();
        BlockState stateToPlace = bi.getBlock().defaultBlockState();
        for (int attempt = 0; attempt < 16; attempt++) {
            int dx = player.getRandom().nextIntBetweenInclusive(-3, 3);
            int dy = player.getRandom().nextIntBetweenInclusive(-2, 2);
            int dz = player.getRandom().nextIntBetweenInclusive(-3, 3);
            BlockPos pos = player.blockPosition().offset(dx, dy, dz);

            if (!serverLevel.getBlockState(pos).isAir()) continue;
            if (!stateToPlace.canSurvive(serverLevel, pos)) continue;

            serverLevel.setBlock(pos, stateToPlace, 3);
            chosen.shrink(1);
            break;
        }
    }
    @SubscribeEvent
    public static void onPlayerChorusTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(player instanceof net.minecraft.server.level.ServerPlayer sp)) return;
        if (player.getPersistentData().getInt("InventoryOpen")>0) return;

        ServerLevel serverLevel = (ServerLevel) player.level();

        Holder<Enchantment> chorusHolder = serverLevel.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.CHORUS);
        List<Integer> eligibleIndices = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            if (i == player.getInventory().selected) continue;
            ItemStack stack = player.getInventory().items.get(i);
            if (stack.getEnchantmentLevel(chorusHolder) > 0 && player.getRandom().nextFloat() < 0.01f) {
                eligibleIndices.add(i);
            }
        }

        if (eligibleIndices.isEmpty()) return;
        int fromIndex = eligibleIndices.get(player.getRandom().nextInt(eligibleIndices.size()));
        List<Integer> swapTargets = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            if (i == fromIndex) continue;
            if (i == player.getInventory().selected) continue;
            swapTargets.add(i);
        }

        if (swapTargets.isEmpty()) return;

        int toIndex = swapTargets.get(player.getRandom().nextInt(swapTargets.size()));

        ItemStack a = player.getInventory().items.get(fromIndex);
        ItemStack b = player.getInventory().items.get(toIndex);

        player.getInventory().items.set(fromIndex, b);
        player.getInventory().items.set(toIndex, a);

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.0f);

        sp.inventoryMenu.sendAllDataToRemote();
    }

    @SubscribeEvent
    public static void onPlayerInventoryLogin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getPersistentData().putInt("InventoryOpen", 0);
    }

    @SubscribeEvent
    public static void onArmorTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) player.level();

        Holder<Enchantment> enchantHolder = serverLevel.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SPARKING);

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {

            ItemStack piece = player.getItemBySlot(slot);
            if (piece.isEmpty()) continue;
            if (piece.getEnchantmentLevel(enchantHolder) <= 0) continue;
            if (player.getRandom().nextFloat() >= 0.005f) continue;
            BlockPos target = findTaggedBlockNearby(serverLevel, player.blockPosition(), 5, ModTags.Blocks.REDSTONE_TARGET);
            if (target == null) {
                target = player.blockPosition().offset(
                        player.getRandom().nextIntBetweenInclusive(-5, 5),
                        player.getRandom().nextIntBetweenInclusive(-2, 2),
                        player.getRandom().nextIntBetweenInclusive(-5, 5));
            }
            RedLightning rl = new RedLightning(serverLevel, player.position().add(0.0, player.getBbHeight()*player.getRandom().nextFloat(), 0.0), target.getCenter());
            serverLevel.addFreshEntity(rl);
            applyRedstoneSignal(serverLevel, target, 15);
        }
    }

    private static BlockPos findTaggedBlockNearby(ServerLevel level, BlockPos origin,
                                                  int radius, TagKey<Block> tag) {
        List<BlockPos> found = new ArrayList<>();
        BlockPos.betweenClosedStream(
                        origin.offset(-radius, -radius, -radius),
                        origin.offset(radius, radius, radius))
                .forEach(pos -> {
                    if (level.getBlockState(pos).is(tag)) {
                        found.add(pos.immutable());
                    }
                });

        if (found.isEmpty()) return null;
        return found.get(level.getRandom().nextInt(found.size())).below();
    }

    private static void applyRedstoneSignal(ServerLevel level, BlockPos pos, int strength) {
        RedstoneSignalManager.poweredPositions.put(pos.immutable(), level.getGameTime() + 2L);
        level.updateNeighborsAt(pos, level.getBlockState(pos).getBlock());
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        long time = event.getServer().overworld().getGameTime();
        RedstoneSignalManager.poweredPositions.entrySet().removeIf(entry -> {
            if (time >= entry.getValue()) {
                ServerLevel level = event.getServer().overworld();
                BlockPos pos = entry.getKey();
                BlockState state = level.getBlockState(pos);

                level.neighborChanged(pos, state.getBlock(), pos);
                level.updateNeighborsAt(pos, state.getBlock());

                return true;
            }
            return false;
        });
    }
    @SubscribeEvent
    public static void onBlackThumbTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) player.level();

        Holder<Enchantment> blackThumbHolder = serverLevel.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.BLACK_THUMB);

        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {

            ItemStack piece = player.getItemBySlot(slot);
            if (piece.isEmpty()) continue;
            if (piece.getEnchantmentLevel(blackThumbHolder) <= 0) continue;
            if (player.getRandom().nextFloat() >= 0.005f) continue;

            BlockPos target = findBlackThumbTarget(serverLevel, player.blockPosition(), 5);
            if (target == null) continue;

            applyBlackThumb(serverLevel, target);
        }
    }

    private static BlockPos findBlackThumbTarget(ServerLevel level, BlockPos origin, int radius) {
        List<BlockPos> found = new ArrayList<>();
        BlockPos.betweenClosedStream(
                        origin.offset(-radius, -radius, -radius),
                        origin.offset(radius, radius, radius))
                .forEach(pos -> {
                    BlockState state = level.getBlockState(pos);
                    if (isBlackThumbTarget(state)) {
                        found.add(pos.immutable());
                    }
                });

        if (found.isEmpty()) return null;
        return found.get(level.getRandom().nextInt(found.size()));
    }

    private static boolean isBlackThumbTarget(BlockState state) {
        Block block = state.getBlock();
        if (state.is(BlockTags.LEAVES)) return true;
        if (state.is(ModTags.Blocks.SMALL_PLANTS)) return true;
        if (block instanceof FlowerPotBlock pot && pot != Blocks.FLOWER_POT && pot != Blocks.POTTED_DEAD_BUSH) return true;
        if (block instanceof CropBlock crop) return true;
        return false;
    }

    private static void applyBlackThumb(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (state.is(BlockTags.LEAVES)) {
            level.destroyBlock(pos, false);

        } else if (state.is(ModTags.Blocks.SMALL_PLANTS)) {
            level.setBlock(pos, Blocks.DEAD_BUSH.defaultBlockState(), 3);

        } else if (block instanceof CropBlock crop) {
            int age = crop.getAge(state);
            if (age > 0) {
                level.setBlock(pos, crop.getStateForAge(age - 1), 3);
            }

        } else if (block instanceof FlowerPotBlock) {
            level.setBlock(pos, ((FlowerPotBlock) Blocks.POTTED_DEAD_BUSH).defaultBlockState(), 3);
        }

        spawnBlackParticles(level, pos);
    }

    private static void spawnBlackParticles(ServerLevel level, BlockPos pos) {
        level.sendParticles(
                ParticleTypes.LARGE_SMOKE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                12,
                0.3, 0.3, 0.3,
                0.05
        );
    }

    @SubscribeEvent
    public static void onRubberBandTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) player.level();
        ItemStack mainhand = player.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack offhand = player.getItemBySlot(EquipmentSlot.OFFHAND);
        ItemStack held = mainhand.is(ItemTags.MACE_ENCHANTABLE) ? mainhand
                : offhand.is(ItemTags.MACE_ENCHANTABLE) ? offhand
                : ItemStack.EMPTY;

        if (held.isEmpty()) return;

        Holder<Enchantment> rubberBandHolder = serverLevel.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.RUBBER_BAND);

        if (held.getEnchantmentLevel(rubberBandHolder) <= 0) return;
        if(player.onGround()) return;
        if (player.getDeltaMovement().y >= 0) return;
        if (player.getRandom().nextFloat() >= 0.05f) return;

        double lift = 1.0 + player.getRandom().nextDouble() * 2.0;
        double targetY = player.getY() + lift;

        BlockPos targetPos = BlockPos.containing(player.getX(), targetY, player.getZ());
        if (!serverLevel.getBlockState(targetPos).isAir()
                || !serverLevel.getBlockState(targetPos.above()).isAir()) return;

        player.teleportTo(player.getX(), targetY, player.getZ());
        player.resetFallDistance();

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 0.5f, 2.0f);
    }

}
