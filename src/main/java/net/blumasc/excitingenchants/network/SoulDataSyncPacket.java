package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.client.ClientDiviningData;
import net.blumasc.excitingenchants.client.ClientSoulData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record SoulDataSyncPacket(UUID playerUUID, List<ResourceLocation> souls)
        implements CustomPacketPayload {

    public static final Type<SoulDataSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "soul_data_sync"));

    public static final StreamCodec<FriendlyByteBuf, SoulDataSyncPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeUUID(packet.playerUUID());
                        buf.writeInt(packet.souls().size());
                        for (ResourceLocation soul : packet.souls()) {
                            buf.writeResourceLocation(soul);
                        }
                    },
                    buf -> {
                        UUID uuid = buf.readUUID();
                        int size = buf.readInt();
                        List<ResourceLocation> souls = new ArrayList<>();
                        for (int i = 0; i < size; i++) {
                            souls.add(buf.readResourceLocation());
                        }
                        return new SoulDataSyncPacket(uuid, souls);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SoulDataSyncPacket pkt, IPayloadContext ctx) {
        ClientSoulData.playerSouls.put(pkt.playerUUID(),
                pkt.souls().stream()
                        .map(type -> new ClientSoulData.SoulOrbit(type, 0f, 0.5f))
                        .collect(java.util.stream.Collectors.toList())
        );
    }
}