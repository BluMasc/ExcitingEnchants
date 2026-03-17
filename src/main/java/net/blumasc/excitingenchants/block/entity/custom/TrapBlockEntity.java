package net.blumasc.excitingenchants.block.entity.custom;

import net.blumasc.excitingenchants.block.custom.TrapBlock;
import net.blumasc.excitingenchants.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TrapBlockEntity extends BlockEntity {

    private int ticksRemaining = 300;

    public TrapBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PITFALL_TRAP_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TrapBlockEntity be) {
        if (level.isClientSide) return;

        if (!state.getValue(TrapBlock.ACTIVE)) {
            be.ticksRemaining--;

            if (be.ticksRemaining <= 0) {
                level.setBlock(pos, state.setValue(TrapBlock.ACTIVE, true), Block.UPDATE_ALL);
                be.setChanged();
            }
        }

        TrapBlock.validateBelow(level, pos);
    }

    public void setTimer(int ticks) {
        this.ticksRemaining = ticks;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Timer", ticksRemaining);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ticksRemaining = tag.getInt("Timer");
    }
}
