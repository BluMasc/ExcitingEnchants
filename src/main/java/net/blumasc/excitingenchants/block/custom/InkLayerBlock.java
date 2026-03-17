package net.blumasc.excitingenchants.block.custom;

import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class InkLayerBlock extends FallingBlock {

    public static final MapCodec<InkLayerBlock> CODEC = simpleCodec(InkLayerBlock::new);

    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public InkLayerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
                                        BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos,
                             Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;
        if (hasSquidEncounter(living)) return;
        entity.setDeltaMovement(
                entity.getDeltaMovement().multiply(1.02, 1.0, 1.02)
        );
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        if (entity instanceof LivingEntity living && hasSquidEncounter(living)) {
            return 0.6f;
        }
        return 0.98f;
    }

    @Override
    protected void falling(FallingBlockEntity entity) {
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos,
                              BlockState state,
                              net.minecraft.world.level.block.entity.BlockEntity blockEntity,
                              ItemStack tool) {
        super.playerDestroy(level, player, pos, state, blockEntity, tool);

        if (level.isClientSide()) return;
        if (hasSquidEncounter(player)) return;

        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                ModEffects.INKED, 5 * 20, 0
        ));
    }

    private boolean hasSquidEncounter(LivingEntity entity) {
        ItemStack chest = entity.getItemBySlot(
                net.minecraft.world.entity.EquipmentSlot.CHEST
        );
        if (chest.isEmpty()) return false;

        Holder<net.minecraft.world.item.enchantment.Enchantment> squidHolder =
                entity.level().registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(ModEnchantments.SQUID_ENCOUNTER);

        return chest.getEnchantmentLevel(squidHolder) > 0;
    }
}