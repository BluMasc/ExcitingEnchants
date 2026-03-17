package net.blumasc.excitingenchants.item.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BloodOrbItem extends Item {
    public BloodOrbItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        stack.shrink(1);
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }
}
