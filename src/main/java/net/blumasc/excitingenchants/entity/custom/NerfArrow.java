package net.blumasc.excitingenchants.entity.custom;

import net.blumasc.excitingenchants.entity.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class NerfArrow extends AbstractArrow {

    public NerfArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public NerfArrow(Level level, ItemStack ammo, LivingEntity shooter, ItemStack weapon) {
        super(ModEntities.NERF_ARROW.get(), shooter, level, ammo, null);
    }

    @Override
    public double getBaseDamage() {
        return 0.0;
    }

    public ItemStack getWeaponItem() {
        return Items.ARROW.getDefaultInstance().copy();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return null;
    }
    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        target.invulnerableTime = 0;
        target.hurt(this.damageSources().arrow(this, getOwner()), 0.0f);
    }
}
