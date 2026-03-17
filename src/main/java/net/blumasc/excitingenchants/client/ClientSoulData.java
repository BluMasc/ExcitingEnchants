package net.blumasc.excitingenchants.client;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientSoulData {
    // Orbital state per player UUID
    public record SoulOrbit(ResourceLocation entityType, float orbitAngle, float orbitHeight) {}
    public static Map<UUID, List<SoulOrbit>> playerSouls = new HashMap<>();
}