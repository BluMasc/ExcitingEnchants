package net.blumasc.excitingenchants.block.entity.custom;

import net.blumasc.excitingenchants.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.blumasc.excitingenchants.block.entity.ModBlockEntities;

import java.util.List;
import java.util.UUID;

public class BreakingBlockEntity extends BlockEntity {
    private BlockState heldState = Blocks.AIR.defaultBlockState();
    private ItemStack tool = ItemStack.EMPTY;
    private int totalTime;
    private int currentTime;

    private UUID playerUUID;

    public void setPlayer(Player player) {
        this.playerUUID = player.getUUID();
        setChanged();
    }

    public BreakingBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.BREAKING_BLOCK_BE.get(), pos, blockState);
    }

    public BlockState getHeldState() { return heldState; }
    public ItemStack getTool() { return tool; }
    public int getTotalTime() { return totalTime; }
    public int getCurrentTime() { return currentTime; }
    public UUID getPlayerUUID() { return playerUUID; }

    public void setHeldState(BlockState state) {
        this.heldState = state;
        setChanged();
        syncToClient();
    }

    public void setTool(ItemStack tool) {
        this.tool = tool.copy();
        setChanged();
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
        setChanged();
        syncToClient();
    }

    private void syncToClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
                             HolderLookup.Provider registries) {
        handleUpdateTag(pkt.getTag(), registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (heldState != null) {
            tag.put("HeldState", NbtUtils.writeBlockState(heldState));
        }
        if (!tool.isEmpty()) {
            tag.put("Tool", tool.save(registries));
        }
        tag.putInt("TotalTime", totalTime);
        tag.putInt("CurrentTime", currentTime);
        tag.putUUID("PlayerUUID", playerUUID);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("HeldState")) {
            heldState = NbtUtils.readBlockState(
                    registries.lookupOrThrow(net.minecraft.core.registries.Registries.BLOCK),
                    tag.getCompound("HeldState")
            );
        }
        if (tag.contains("Tool")) {
            tool = ItemStack.parseOptional(registries, tag.getCompound("Tool"));
        }
        totalTime = tag.getInt("TotalTime");
        currentTime = tag.getInt("CurrentTime");
        if (tag.hasUUID("PlayerUUID")) playerUUID = tag.getUUID("PlayerUUID");
    }

    public static void tick(Level level, BlockPos pos,
                            BlockState state, BreakingBlockEntity entity) {
        if (level.isClientSide) return;

        entity.currentTime++;
        if (entity.currentTime % 5 == 0) {
            entity.syncToClient();
        }

        if (entity.currentTime >= entity.totalTime) {
            BlockState heldState = entity.getHeldState();
            ItemStack storedTool = entity.getTool();

            ServerLevel serverLevel = (ServerLevel) level;
            ServerPlayer player = serverLevel.getServer()
                    .getPlayerList()
                    .getPlayer(entity.getPlayerUUID());

            if (player != null) {
                ItemStack currentHand = player.getMainHandItem().copy();

                player.setItemInHand(InteractionHand.MAIN_HAND, storedTool);

                heldState.getBlock().playerDestroy(
                        level, player, pos, heldState,
                        level.getBlockEntity(pos), storedTool
                );

                player.setItemInHand(InteractionHand.MAIN_HAND, currentHand);
            } else {
                List<ItemStack> drops = Block.getDrops(
                        heldState, serverLevel, pos, null, null, storedTool
                );
                drops.forEach(drop -> Block.popResource(level, pos, drop));
            }

            level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK,
                    pos, Block.getId(heldState));
            level.removeBlock(pos, false);
        }
    }
}