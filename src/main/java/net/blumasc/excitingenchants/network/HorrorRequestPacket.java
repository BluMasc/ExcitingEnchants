package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.state.HorrorSavedData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HorrorRequestPacket() implements CustomPacketPayload {

    public static final Type<HorrorRequestPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "horror_request"));

    public static final StreamCodec<FriendlyByteBuf, HorrorRequestPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {},
                    buf -> new HorrorRequestPacket()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HorrorRequestPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            HorrorSavedData data = HorrorSavedData.get(player.serverLevel());
            PacketDistributor.sendToPlayer(player, new HorrorSyncPacket(data.getHorrorValue(player.getUUID())));
        });
    }
}