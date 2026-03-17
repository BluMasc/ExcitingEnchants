package net.blumasc.excitingenchants.block.custom;

import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.block.entity.ModBlockEntities;
import net.blumasc.excitingenchants.block.entity.custom.CurseTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CurseTableBlock extends BaseEntityBlock {

    public static final MapCodec<CurseTableBlock> CODEC = simpleCodec(CurseTableBlock::new);

    public CurseTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new CurseTableBlockEntity(blockPos,blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(state.getBlock() != newState.getBlock()){
            if(level.getBlockEntity(pos) instanceof CurseTableBlockEntity pedestalBlockEntity){
                pedestalBlockEntity.drops();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    private static final VoxelShape COLLISION =
            Block.box(0, 0, 0, 16, 10, 16);

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return COLLISION;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (!(level.getBlockEntity(pos) instanceof CurseTableBlockEntity CurseTable)) {
            return ItemInteractionResult.FAIL;
        }

        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (player.isCrouching()) {
            boolean success = false;
            for (int i = 0; i < CurseTable.inventory.getSlots(); i++) {
                ItemStack extracted = CurseTable.inventory.extractItem(i, 1, false);
                if (!extracted.isEmpty()) {
                    success = true;
                    giveItemToPlayer(player, extracted);
                }
            }
            if(success) level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 0.8f);
            return ItemInteractionResult.SUCCESS;
        }

        if (!stack.isEmpty() && CurseTable.isCatalyst(stack)) {
            for (int i = 1; i <= 4; i++) {
                if (CurseTable.inventory.getStackInSlot(i).isEmpty()) {
                    ItemStack toInsert = stack.copyWithCount(1);
                    CurseTable.inventory.insertItem(i, toInsert, false);
                    stack.shrink(1);

                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1.5f);
                    return ItemInteractionResult.SUCCESS;
                }
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (!stack.isEmpty()) {
            if (CurseTable.inventory.getStackInSlot(0).isEmpty()) {
                CurseTable.inventory.insertItem(0, stack.copyWithCount(1), false);
                stack.shrink(1);

                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 2f);
            }

            return ItemInteractionResult.SUCCESS;
        }
        if (stack.isEmpty()) {

            ItemStack extracted = CurseTable.inventory.extractItem(0, 1, false);
            if (!extracted.isEmpty()) {
                giveItemToPlayer(player, extracted);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                return ItemInteractionResult.SUCCESS;
            }

            for (int i = 1; i <= 4; i++) {
                extracted = CurseTable.inventory.extractItem(i, 1, false);
                if (!extracted.isEmpty()) {
                    giveItemToPlayer(player, extracted);
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 1f);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return ItemInteractionResult.SUCCESS;
    }

    private void giveItemToPlayer(Player player, ItemStack stack){
        boolean inserted = player.getInventory().add(stack);
        if (!inserted) {
            ItemEntity drop = new ItemEntity(
                    player.level(),
                    player.getX(),
                    player.getY() + 0.5,
                    player.getZ(),
                    stack.copy()
            );
            drop.setPickUpDelay(0);
            player.level().addFreshEntity(drop);
        }
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, ModBlockEntities.CURSE_TABLE_BE.get(),
                (nlevel, blockPos, blockState, blockEntity) -> blockEntity.tick(nlevel, blockPos, blockState));
    }
}
