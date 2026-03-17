package net.blumasc.excitingenchants.state;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.network.RageDataSyncPacket;
import net.blumasc.excitingenchants.network.SoulDataSyncPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerEnchantmentStateHandler {

    private static final String NBT_KEY = ExcitingEnchantsMod.MODID+":enchantment_state";

    public static void saveState(LivingEntity player, PlayerEnchantmentState state) {
        CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        CompoundTag stateTag = new CompoundTag();

        stateTag.putInt("cloudStepJumpsUsed", state.getCloudStepJumpsUsed());
        stateTag.putInt("frenzyCount", state.frenzyCount);
        stateTag.putInt("balloonCount", state.balloonCount);
        stateTag.putBoolean("dashUsed", state.isDashUsed());
        stateTag.putBoolean("justJumped", state.hasJustJumped());
        if (state.getFrenzyLastAttackedEntity() != null)
            stateTag.putUUID("frenzyLastAttackedEntity", state.getFrenzyLastAttackedEntity());

        CompoundTag cooldownsTag = new CompoundTag();
        for (Map.Entry<ResourceLocation, Long> entry : state.cooldowns.entrySet()) {
            cooldownsTag.putLong(entry.getKey().toString(), entry.getValue());
        }
        stateTag.put("cooldowns", cooldownsTag);
        ListTag soulsTag = new net.minecraft.nbt.ListTag();
        for (ResourceLocation soul : state.getSouls()) {
            net.minecraft.nbt.StringTag stringTag = net.minecraft.nbt.StringTag.valueOf(soul.toString());
            soulsTag.add(stringTag);
        }
        stateTag.put("souls", soulsTag);

        tag.put(NBT_KEY, stateTag);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, tag);
        if(player instanceof Player p  && !player.level().isClientSide()) {
            syncSouls(p, state.getSouls());
            if(state.getFrenzyLastAttackedEntity() != null) {
                syncRage(p, state.frenzyCount, state.getFrenzyLastAttackedEntity());
            }
        }
    }

    public static PlayerEnchantmentState loadState(LivingEntity player) {
        PlayerEnchantmentState state = new PlayerEnchantmentState();
        CompoundTag tag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (!tag.contains(NBT_KEY)) return state;

        CompoundTag stateTag = tag.getCompound(NBT_KEY);

        state.resetCloudStepJumps();
        if(stateTag.contains("cloudStepJumpsUsed")) {
            state.cloudStepJumpsUsed = stateTag.getInt("cloudStepJumpsUsed");
        }
        if(stateTag.contains("frenzyCount")) {
            state.frenzyCount = stateTag.getInt("frenzyCount");
        }
        if(stateTag.contains("balloonCount")) {
            state.balloonCount = stateTag.getInt("balloonCount");
        }
        if(stateTag.contains("dashUsed")) {
            state.setDashUsed(stateTag.getBoolean("dashUsed"));
        }
        if(stateTag.contains("justJumped")) {
            state.setJustJumped(stateTag.getBoolean("justJumped"));
        }
        if (stateTag.hasUUID("frenzyLastAttackedEntity"))
            state.setFrenzyLastAttackedEntity(stateTag.getUUID("frenzyLastAttackedEntity"));

        CompoundTag cooldownsTag = stateTag.getCompound("cooldowns");
        for (String key : cooldownsTag.getAllKeys()) {
            ResourceLocation id = ResourceLocation.tryParse(key);
            if (id != null)
                state.setCooldown(id, cooldownsTag.getLong(key));
        }
        if (stateTag.contains("souls")) {
            ListTag soulsTag = stateTag.getList("souls",
                    Tag.TAG_STRING);
            for (int i = 0; i < soulsTag.size(); i++) {
                ResourceLocation id = ResourceLocation.tryParse(soulsTag.getString(i));
                if (id != null) {
                    state.getSouls().add(id);
                }
            }
        }

        return state;
    }

    public static void syncSouls(Player player, List<ResourceLocation> souls) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
                new SoulDataSyncPacket(player.getUUID(), souls)
        );
    }

    public static void syncRage(Player player, int rage, UUID target) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new RageDataSyncPacket(rage, target));
    }

}

