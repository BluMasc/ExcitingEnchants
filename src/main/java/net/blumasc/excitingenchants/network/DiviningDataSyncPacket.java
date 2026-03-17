package net.blumasc.excitingenchants.network;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.client.ClientDiviningData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record DiviningDataSyncPacket(
        List<Vec3> enemyPositions,
        List<Vec3> playerPositions
) implements CustomPacketPayload {

    public static final Type<DiviningDataSyncPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "divining_data_sync"));

    public static final StreamCodec<FriendlyByteBuf, DiviningDataSyncPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeInt(pkt.enemyPositions().size());
                        for (Vec3 pos : pkt.enemyPositions()) {
                            buf.writeDouble(pos.x());
                            buf.writeDouble(pos.y());
                            buf.writeDouble(pos.z());
                        }

                        buf.writeInt(pkt.playerPositions().size());
                        for (Vec3 pos : pkt.playerPositions()) {
                            buf.writeDouble(pos.x());
                            buf.writeDouble(pos.y());
                            buf.writeDouble(pos.z());
                        }
                    },
                    buf -> {
                        int enemySize = buf.readInt();
                        List<Vec3> enemies = new ArrayList<>();
                        for (int i = 0; i < enemySize; i++) {
                            double x = buf.readDouble();
                            double y = buf.readDouble();
                            double z = buf.readDouble();
                            enemies.add(new Vec3(x, y, z));
                        }

                        int playerSize = buf.readInt();
                        List<Vec3> players = new ArrayList<>();
                        for (int i = 0; i < playerSize; i++) {
                            double x = buf.readDouble();
                            double y = buf.readDouble();
                            double z = buf.readDouble();
                            players.add(new Vec3(x, y, z));
                        }

                        return new DiviningDataSyncPacket(enemies, players);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DiviningDataSyncPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientDiviningData.EnemyPositions = pkt.enemyPositions();
            ClientDiviningData.PlayerPositions = pkt.playerPositions();
        });
    }
}
