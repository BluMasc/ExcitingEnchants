package net.blumasc.excitingenchants.entity.custom;

import net.blumasc.blubasics.entity.custom.projectile.LightningArcEntity;
import net.blumasc.excitingenchants.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RedLightning extends LightningArcEntity {
    public RedLightning(EntityType<?> type, Level level) {
        super(type, level);
    }

    public RedLightning(Level level, Vec3 start, Vec3 end) {
        super(ModEntities.REDSTONE_ARC.get(), level, start, end);
    }
}
