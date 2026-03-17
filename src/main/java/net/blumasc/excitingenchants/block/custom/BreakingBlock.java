package net.blumasc.excitingenchants.block.custom;

import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.block.entity.ModBlockEntities;
import net.blumasc.excitingenchants.block.entity.custom.BreakingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class BreakingBlock extends BaseEntityBlock {

    public static final MapCodec<BreakingBlock> CODEC = simpleCodec(BreakingBlock::new);

    public BreakingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
                                        BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state,
                                    net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BreakingBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            net.minecraft.world.level.Level level, BlockState state,
            BlockEntityType<T> type) {
        return createTickerHelper(type,
                ModBlockEntities.BREAKING_BLOCK_BE.get(),
                BreakingBlockEntity::tick);
    }
}