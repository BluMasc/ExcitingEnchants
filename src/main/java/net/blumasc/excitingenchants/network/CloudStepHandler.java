// net/blumasc/excitingenchants/enchantments/handler/CloudStepHandler.java
package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.state.PlayerEnchantmentState;
import net.blumasc.excitingenchants.state.PlayerEnchantmentStateHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.state.BlockState;

public class CloudStepHandler {

    public static void tryCloudStep(ServerPlayer player) {
        if (player.onGround()) return;
        if (player.isInWater()) return;
        if (player.getDeltaMovement().y >= 0) return;

        PlayerEnchantmentState pes = PlayerEnchantmentStateHandler.loadState(player);

        Holder<Enchantment> jumpHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.CLOUD_STEP);

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        int level = boots.getEnchantmentLevel(jumpHolder);
        if (level <= 0) return;
        if (pes.getCloudStepJumpsUsed() >= level) return;

        if (pes.isOnCooldown(ModEnchantments.CLOUD_STEP.location(), player.level().getGameTime())) return;

        BlockPos feet = BlockPos.containing(player.getX(), player.getY(), player.getZ());
        if (player.level().getBlockState(feet).getBlock() == ModBlocks.CLOUD_BLOCK.get()) return;
        if (player.level().getBlockState(feet.below()).getBlock() == ModBlocks.CLOUD_BLOCK.get()) return;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos candidate = feet.offset(dx, -1, dz);
                BlockState existing = player.level().getBlockState(candidate);
                if (existing.isAir() || existing.canBeReplaced()) {
                    player.level().setBlock(
                            candidate,
                            ModBlocks.CLOUD_BLOCK.get().defaultBlockState(),
                            3
                    );
                }
            }
        }

        player.level().playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.WOOL_PLACE,
                SoundSource.PLAYERS,
                1.0f, 1.2f
        );

        pes.setCooldown(ModEnchantments.CLOUD_STEP.location(), player.level().getGameTime() + 10);
        pes.incrementCloudStepJumps();
        PlayerEnchantmentStateHandler.saveState(player, pes);
    }
}