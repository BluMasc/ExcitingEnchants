package net.blumasc.excitingenchants.shader;

import com.google.gson.JsonSyntaxException;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class OilSlickRenderer {
    private static PostChain chain = null;
    private static float time = 0f;
    private static Field passesField = null;

    static {
        try {
            try {
                passesField = PostChain.class.getDeclaredField("passes");
            } catch (NoSuchFieldException e) {
                passesField = PostChain.class.getDeclaredField("f_110020_");
            }
            passesField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        dispose();
        Minecraft mc = Minecraft.getInstance();
        try {
            chain = new PostChain(
                    mc.getTextureManager(),
                    mc.getResourceManager(),
                    mc.getMainRenderTarget(),
                    ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "shaders/post/oil_slick.json")
            );
            chain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void apply(float partialTick) {
        if (chain == null) return;
        Minecraft mc = Minecraft.getInstance();
        chain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());

        time += partialTick;

        if (passesField != null) {
            try {
                List<PostPass> passes = (List<PostPass>) passesField.get(chain);
                for (PostPass pass : passes) {
                    pass.getEffect().safeGetUniform("GameTime").set(time);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        chain.process(partialTick);
    }

    public static void dispose() {
        if (chain != null) { chain.close(); chain = null; }
        time = 0f;
    }

    public static boolean isLoaded() {
        return chain != null;
    }
}