package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.client.EnderPocketsOverlay;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record EnderSyncPacket(List<ItemStack> slots, ItemStack cursor)
        implements CustomPacketPayload {

    public static final Type<EnderSyncPacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "ender_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnderSyncPacket> STREAM_CODEC =
        StreamCodec.of(
            (buf, p) -> {
                buf.writeVarInt(p.slots().size());
                for (ItemStack s : p.slots()) ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, s);
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, p.cursor());
            },
            buf -> {
                int n = buf.readVarInt();
                List<ItemStack> list = new ArrayList<>(n);
                for (int i = 0; i < n; i++) list.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
                return new EnderSyncPacket(list, ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
            }
        );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(EnderSyncPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> EnderPocketsOverlay.applySync(pkt.slots(), pkt.cursor()));
    }
}