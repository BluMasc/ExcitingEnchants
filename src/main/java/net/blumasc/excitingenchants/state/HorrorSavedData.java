package net.blumasc.excitingenchants.state;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HorrorSavedData extends SavedData {

    private static final String NAME = ExcitingEnchantsMod.MODID + "_horror_data";
    private static final String HORROR_VALUE_KEY = "horror_value";

    private final Map<UUID, Integer> horrorValues = new HashMap<>();

    public static final SavedData.Factory<HorrorSavedData> FACTORY = new SavedData.Factory<>(
            HorrorSavedData::new,
            HorrorSavedData::load
    );

    public static HorrorSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(FACTORY, NAME);
    }

    public int getHorrorValue(UUID playerUUID) {
        return horrorValues.getOrDefault(playerUUID, 0);
    }

    public void setHorrorValue(UUID playerUUID, int value) {
        horrorValues.put(playerUUID, value);
        setDirty();
    }

    public static HorrorSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        HorrorSavedData data = new HorrorSavedData();
        CompoundTag playersTag = tag.getCompound(HORROR_VALUE_KEY);
        for (String key : playersTag.getAllKeys()) {
            data.horrorValues.put(UUID.fromString(key), playersTag.getInt(key));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag playersTag = new CompoundTag();
        horrorValues.forEach((uuid, value) -> playersTag.putInt(uuid.toString(), value));
        tag.put(HORROR_VALUE_KEY, playersTag);
        return tag;
    }
}