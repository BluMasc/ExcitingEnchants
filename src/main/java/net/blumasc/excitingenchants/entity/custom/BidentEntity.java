package net.blumasc.excitingenchants.entity.custom;

import net.blumasc.excitingenchants.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BidentEntity extends ThrownTrident {

    public BidentEntity(EntityType<? extends ThrownTrident> entityType, Level level) {
        super(entityType, level);
    }

    public BidentEntity(Level level, LivingEntity shooter, ItemStack pickupItemStack) {
        super(level, shooter, pickupItemStack);
    }

    public BidentEntity(Level level, double x, double y, double z, ItemStack pickupItemStack) {
        super(level, x, y, z, pickupItemStack);
    }
}
