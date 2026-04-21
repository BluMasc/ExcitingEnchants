package net.blumasc.excitingenchants.item.custom;

import net.blumasc.excitingenchants.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CleansingSoapItem extends Item {
    public CleansingSoapItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        var hitPos = context.getClickedPos();
        var aabb = new AABB(hitPos).inflate(1.5);
        var items = level.getEntitiesOfClass(ItemEntity.class, aabb);
        for (var itemEntity : items) {
            ItemStack stack = itemEntity.getItem();

            if (tryCleanse(stack, level)) {
                double x = itemEntity.getX();
                double y = itemEntity.getY();
                double z = itemEntity.getZ();

                ItemStack newStack = stack.copy();
                itemEntity.discard();

                spawnEffects((ServerLevel) level, x, y, z);

                ItemEntity newEntity = new ItemEntity(level, x, y, z, newStack);
                newEntity.setDefaultPickUpDelay();
                level.addFreshEntity(newEntity);

                context.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        ItemStack offhand = player.getOffhandItem();
        if (tryCleanse(offhand, level)) {
            double x = player.getX();
            double y = player.getY() + 0.5;
            double z = player.getZ();

            spawnEffects((ServerLevel) level, x, y, z);

            context.getItemInHand().shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
    private boolean tryCleanse(ItemStack stack, Level level) {
        if (stack.isEmpty()) return false;

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        var curses = enchantments.keySet().stream()
                .filter(holder -> holder.is(EnchantmentTags.CURSE))
                .toList();

        if (curses.isEmpty()) return false;

        var curseToRemove = curses.get(level.getRandom().nextInt(curses.size()));
        EnchantmentHelper.updateEnchantments(stack, mutable -> mutable.set(curseToRemove, 0));
        return true;
    }
    private void spawnEffects(ServerLevel level, double x, double y, double z) {
        level.playSound(null, (int) x, (int) y, (int) z, ModSounds.CURSE_CLEANING.get(), SoundSource.BLOCKS, 0.5F, 0.8F);
        level.sendParticles(ParticleTypes.CLOUD, x, y + 0.2, z, 10, 0.3, 0.3, 0.3, 0.02);
        level.sendParticles(ParticleTypes.BUBBLE, x, y + 0.2, z, 15, 0.3, 0.3, 0.3, 0.002);
    }
}
