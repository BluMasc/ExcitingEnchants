package net.blumasc.excitingenchants.network;

import io.netty.buffer.ByteBuf;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record InventoryOpenPayload(boolean open) implements CustomPacketPayload {
    public static final Type<InventoryOpenPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "inventory_open"));

    public static final StreamCodec<ByteBuf, InventoryOpenPayload> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.BOOL, InventoryOpenPayload::open, InventoryOpenPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(InventoryOpenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
                    int num = context.player().getPersistentData().getInt("InventoryOpen");
                    if (payload.open()) {
                        num++;
                    } else {
                        num--;
                    }
                    num = Math.max(0, num);
                    context.player().getPersistentData().putInt("InventoryOpen", num);
                }
        );
    }

}