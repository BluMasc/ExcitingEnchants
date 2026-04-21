package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.event.ClientHimHandler;
import net.blumasc.excitingenchants.state.HorrorSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record HimActionPacket(int himEvent) implements CustomPacketPayload {
    public static final Type<HimActionPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "horror_event"));

    public static final StreamCodec<FriendlyByteBuf, HimActionPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.himEvent()),
                    buf -> new HimActionPacket(buf.readInt())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HimActionPacket pkt, IPayloadContext ctx) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        switch (pkt.himEvent()) {
            case 0 -> ClientHimHandler.onKillAnimal(player);
            case 1 -> ClientHimHandler.onKillMonster(player);
            case 2 -> ClientHimHandler.onPlayerDeath(player);
            case 3 -> ClientHimHandler.onMineBlock(player);
        }
    }
}
