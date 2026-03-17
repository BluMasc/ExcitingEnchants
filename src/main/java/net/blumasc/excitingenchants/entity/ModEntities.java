package net.blumasc.excitingenchants.entity;

import net.blumasc.blubasics.entity.custom.projectile.LightningArcEntity;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.entity.custom.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ExcitingEnchantsMod.MODID);

    public static final Supplier<EntityType<WoodArrow>> WOOD_ARROW =
            ENTITY_TYPES.register("wood_arrow", () -> EntityType.Builder.<WoodArrow>of(WoodArrow::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build("wood_arrow"));

    public static final Supplier<EntityType<LightningRodArrowEntity>> LIGHTNING_ROD_ARROW =
            ENTITY_TYPES.register("lightning_rod_arrow", () -> EntityType.Builder.<LightningRodArrowEntity>of(LightningRodArrowEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build("lightning_rod_arrow"));

    public static final Supplier<EntityType<NerfArrow>> NERF_ARROW =
            ENTITY_TYPES.register("nerf_arrow", () -> EntityType.Builder.<NerfArrow>of(NerfArrow::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build("nerf_arrow"));

    public static final Supplier<EntityType<BloodShardProjectile>> BLOOD_SHARD =
            ENTITY_TYPES.register("blood_shard", () -> EntityType.Builder.<BloodShardProjectile>of(BloodShardProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20).build("blood_shard"));


    public static final Supplier<EntityType<WardenBeamProjectile>> WARDEN_BEAM =
            ENTITY_TYPES.register("warden_beam", () ->
                    EntityType.Builder.<WardenBeamProjectile>of(WardenBeamProjectile::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(128)
                            .updateInterval(1)
                            .build("warden_beam")
            );

    public static final Supplier<EntityType<GrapeShotProjectile>> GRAPE_SHOT =
            ENTITY_TYPES.register("grape_shot", () ->
                    EntityType.Builder.<GrapeShotProjectile>of(GrapeShotProjectile::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .clientTrackingRange(128)
                            .updateInterval(1)
                            .build("grape_shot")
            );

    public static final Supplier<EntityType<BalloonEntity>> BALLOON =
            ENTITY_TYPES.register("balloon", () ->
                    EntityType.Builder.<BalloonEntity>of(BalloonEntity::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .build("balloon")
            );

    public static final Supplier<EntityType<BidentEntity>> BIDENT =
            ENTITY_TYPES.register("bident",
                    () -> EntityType.Builder.<BidentEntity>of(BidentEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("bident")
            );

    public static final Supplier<EntityType<RedLightning>> REDSTONE_ARC =
            ENTITY_TYPES.register("redstone_arc", () ->
                    EntityType.Builder.<RedLightning>of(RedLightning::new, MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("redstone_arc"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
