package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.client.ClientHimData;
import net.blumasc.excitingenchants.event.ClientHimHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HorrorSyncPacket(int value) implements CustomPacketPayload {

    public static final Type<HorrorSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "horror_sync"));

    public static final StreamCodec<FriendlyByteBuf, HorrorSyncPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.value()),
                    buf -> new HorrorSyncPacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HorrorSyncPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientHimData.horror_status = pkt.value();
            ClientHimHandler.setLastSyncedValue(pkt.value());
        });
    }
}