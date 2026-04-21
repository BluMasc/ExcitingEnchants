package net.blumasc.excitingenchants.network;

import io.netty.buffer.ByteBuf;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static net.blumasc.excitingenchants.event.EnchantmentEventHandlers.spawnEchoGhosts;

public record LeftClickPayload() implements CustomPacketPayload {
    public static final Type<LeftClickPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "left_clicked"));

    public static final StreamCodec<FriendlyByteBuf, LeftClickPayload> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {},
                    buf -> new LeftClickPayload()
            );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(LeftClickPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
                    Player player = context.player();
                    ItemStack stack = player.getMainHandItem();
                    if (stack.isEmpty()) return;

                    Holder<Enchantment> rubberBandHolder = player.level().registryAccess()
                            .registryOrThrow(Registries.ENCHANTMENT)
                            .getHolderOrThrow(ModEnchantments.TIME_REMNANT);

                    int echoLevel = stack.getEnchantmentLevel(rubberBandHolder);
                    if (echoLevel <= 0) return;

                    spawnEchoGhosts(player, stack);
                }
        );
    }

}