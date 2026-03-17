package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ModNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {

        event.registrar(ExcitingEnchantsMod.MODID).playToClient(
                DiviningDataSyncPacket.TYPE,
                DiviningDataSyncPacket.CODEC,
                DiviningDataSyncPacket::handle
        );

        event.registrar(ExcitingEnchantsMod.MODID).playToClient(
                SoulDataSyncPacket.TYPE,
                SoulDataSyncPacket.STREAM_CODEC,
                SoulDataSyncPacket::handle
        );
        event.registrar(ExcitingEnchantsMod.MODID).playToClient(
                RageDataSyncPacket.TYPE,
                RageDataSyncPacket.STREAM_CODEC,
                RageDataSyncPacket::handle
        );
        event.registrar(ExcitingEnchantsMod.MODID).playToClient(
                HorrorSyncPacket.TYPE,
                HorrorSyncPacket.STREAM_CODEC,
                HorrorSyncPacket::handle
        );
        event.registrar(ExcitingEnchantsMod.MODID).playToServer(
                JumpInputPacket.TYPE,
                JumpInputPacket.STREAM_CODEC,
                JumpInputPacket::handle
        );

        event.registrar(ExcitingEnchantsMod.MODID).playToServer(
                InventoryOpenPayload.TYPE,
                InventoryOpenPayload.STREAM_CODEC,
                InventoryOpenPayload::handle
        );

        event.registrar(ExcitingEnchantsMod.MODID).playToServer(
                HorrorRequestPacket.TYPE,
                HorrorRequestPacket.STREAM_CODEC,
                HorrorRequestPacket::handle
        );

        event.registrar(ExcitingEnchantsMod.MODID).playToServer(
                HorrorSavePacket.TYPE,
                HorrorSavePacket.STREAM_CODEC,
                HorrorSavePacket::handle
        );
    }
}
