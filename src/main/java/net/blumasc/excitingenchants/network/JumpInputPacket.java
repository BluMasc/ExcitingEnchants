// net/blumasc/excitingenchants/network/JumpInputPacket.java
package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record JumpInputPacket() implements CustomPacketPayload {

    public static final Type<JumpInputPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "jump_input"));

    public static final StreamCodec<FriendlyByteBuf, JumpInputPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {},
                    buf -> new JumpInputPacket()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(JumpInputPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;
            CloudStepHandler.tryCloudStep(player);
        });
    }
}