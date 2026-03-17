package net.blumasc.excitingenchants.state;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

public class PlayerEnchantmentState {
    final Map<ResourceLocation, Long> cooldowns = new HashMap<>();

    int cloudStepJumpsUsed = 0;
    private boolean dashUsed = false;
    private boolean justJumped = false;
    private UUID frenzyLastAttackedEntity = null;
    int frenzyCount = 0;
    private List<ResourceLocation> souls = new ArrayList<>();
    int balloonCount = 0;


    public boolean isOnCooldown(ResourceLocation id, long gameTime) {
        return cooldowns.getOrDefault(id, 0L) > gameTime;
    }

    public void setCooldown(ResourceLocation id, long untilGameTime) {
        cooldowns.put(id, untilGameTime);
    }

    public long getRemainingCooldown(ResourceLocation id, long gameTime) {
        return Math.max(0, cooldowns.getOrDefault(id, 0L) - gameTime);
    }

    public int getBalloonCount(){
        return balloonCount;
    }
    public void setBalloonCount(int count){
        this.balloonCount = count;
    }

    public int getCloudStepJumpsUsed() { return cloudStepJumpsUsed; }
    public void incrementCloudStepJumps() { cloudStepJumpsUsed++; }
    public void resetCloudStepJumps() { cloudStepJumpsUsed = 0; }

    public boolean isDashUsed() { return dashUsed; }
    public void setDashUsed(boolean used) { dashUsed = used; }

    public boolean hasJustJumped() { return justJumped; }
    public void setJustJumped(boolean used) { justJumped= used; }

    public UUID getFrenzyLastAttackedEntity() { return frenzyLastAttackedEntity; }
    public void setFrenzyLastAttackedEntity(UUID entityId) { frenzyLastAttackedEntity = entityId; }
    public int getFrenzyCount(UUID entityId) {
        if(entityId.equals(frenzyLastAttackedEntity)){
            frenzyCount=Math.min(frenzyCount+1, 4);
        }else{
            frenzyCount = 0;
        }
        return frenzyCount;
    }
    public List<ResourceLocation> getSouls() { return souls; }

    public void addSoul(ResourceLocation entityType) {
        if (souls.size() >= 3){
            souls.remove(0);
        }
        souls.add(entityType);
    }

    public void removeSoul(int index) {
        if (index >= 0 && index < souls.size()) souls.remove(index);
    }
}
