package net.blumasc.excitingenchants.network;

import io.netty.buffer.ByteBuf;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record EnderActionPacket(int slot, int button, boolean shift)
        implements CustomPacketPayload {

    public static final Type<EnderActionPacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "ender_action"));

    public static final StreamCodec<ByteBuf, EnderActionPacket> STREAM_CODEC =
        StreamCodec.of(
            (buf, p) -> { buf.writeShort(p.slot()); buf.writeByte(p.button()); buf.writeBoolean(p.shift()); },
            buf  -> new EnderActionPacket(buf.readShort(), buf.readByte(), buf.readBoolean())
        );

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(EnderActionPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) return;

            int level = ModEnchantments.getEnderPocketsLevel(player);
            if (level <= 0) return;

            if (pkt.slot() >= 0 && pkt.slot() < level) {
                performClick(player, pkt.slot(), pkt.button(), pkt.shift());
            }

            sendSync(player, level);
        });
    }

    private static void performClick(ServerPlayer player, int slotIdx, int button, boolean shift) {
        var ender  = player.getEnderChestInventory();
        var menu   = player.containerMenu;

        ItemStack inSlot  = ender.getItem(slotIdx).copy();
        ItemStack carried = menu.getCarried().copy();

        if (shift) {
            if (!inSlot.isEmpty()) {
                boolean fullyAdded = player.getInventory().add(inSlot);
                ender.setItem(slotIdx, fullyAdded ? ItemStack.EMPTY : inSlot);
            }
        } else if (button == 0) {
            ender.setItem(slotIdx, carried);
            menu.setCarried(inSlot);
        } else if (button == 1) {
            if (carried.isEmpty()) {
                if (!inSlot.isEmpty()) {
                    int half = (inSlot.getCount() + 1) / 2;
                    menu.setCarried(inSlot.copyWithCount(half));
                    inSlot.shrink(half);
                    ender.setItem(slotIdx, inSlot.isEmpty() ? ItemStack.EMPTY : inSlot);
                }
            } else if (inSlot.isEmpty()) {
                ender.setItem(slotIdx, carried.copyWithCount(1));
                carried.shrink(1);
                menu.setCarried(carried.isEmpty() ? ItemStack.EMPTY : carried);
            } else if (ItemStack.isSameItemSameComponents(carried, inSlot)
                    && inSlot.getCount() < inSlot.getMaxStackSize()) {
                inSlot.grow(1);
                ender.setItem(slotIdx, inSlot);
                carried.shrink(1);
                menu.setCarried(carried.isEmpty() ? ItemStack.EMPTY : carried);
            } else {
                ender.setItem(slotIdx, carried);
                menu.setCarried(inSlot);
            }
        }
    }

    public static void sendSync(ServerPlayer player, int level) {
        var ender  = player.getEnderChestInventory();
        List<ItemStack> slots = new ArrayList<>(level);
        for (int i = 0; i < level; i++) slots.add(ender.getItem(i).copy());
        PacketDistributor.sendToPlayer(player,
            new EnderSyncPacket(slots, player.containerMenu.getCarried().copy()));
    }
}