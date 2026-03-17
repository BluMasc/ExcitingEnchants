package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.client.ClientRageData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RageDataSyncPacket(int rageLevel, UUID target)
        implements CustomPacketPayload {

    public static final Type<RageDataSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "rage_data_sync"));

    public static final StreamCodec<FriendlyByteBuf, RageDataSyncPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeInt(packet.rageLevel());
                        buf.writeUUID(packet.target);
                    },
                    buf -> {
                        int size = buf.readInt();
                        UUID target = buf.readUUID();
                        return new RageDataSyncPacket(size, target);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RageDataSyncPacket pkt, IPayloadContext ctx) {
        ClientRageData.rage = pkt.rageLevel();
        ClientRageData.target = pkt.target();
    }
}