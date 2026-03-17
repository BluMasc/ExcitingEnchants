package net.blumasc.excitingenchants.item.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GoldEatenItem extends Item {
    public GoldEatenItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return super.getUseDuration(stack, entity)*2;
    }
}
