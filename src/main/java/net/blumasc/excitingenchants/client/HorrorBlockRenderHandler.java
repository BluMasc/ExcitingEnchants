package net.blumasc.excitingenchants.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

public class HorrorBlockRenderHandler {

    public static boolean torchSwapActive = false;

    private static final ResourceLocation REDSTONE_TORCH_PARTICLE =
            ResourceLocation.withDefaultNamespace("block/redstone_torch");

    public static void register(RegisterClientReloadListenersEvent event) {
    }

    public static ResourceLocation getTorchTexture(ResourceLocation original) {
        if (!torchSwapActive) return original;

        String path = original.getPath();
        if (path.contains("torch") && !path.contains("redstone")) {
            return REDSTONE_TORCH_PARTICLE;
        }
        return original;
    }
    @SubscribeEvent
    public static void onFrameStart(ClientTickEvent.Pre event) {
        torchSwapActive = false;
    }
}