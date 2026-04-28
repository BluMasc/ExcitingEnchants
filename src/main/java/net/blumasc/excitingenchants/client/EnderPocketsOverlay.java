package net.blumasc.excitingenchants.client;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.network.EnderActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collections;
import java.util.List;

@EventBusSubscriber(modid = ExcitingEnchantsMod.MODID, value = Dist.CLIENT)
public class EnderPocketsOverlay {

    private static List<ItemStack> cachedSlots = Collections.emptyList();

    public static void applySync(List<ItemStack> slots, ItemStack cursor) {
        cachedSlots = slots;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) mc.player.containerMenu.setCarried(cursor);
    }

    private static final ResourceLocation[] PANEL_TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/ender_pockets_1.png"),
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/ender_pockets_2.png"),
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/ender_pockets_3.png"),
    };
    private static final int TEXTURE_W    = 29;
    private static final int TEXTURE_H    = 65;

    private static final int SLOT_INSET_X = 4;
    private static final int SLOT_INSET_Y = 5;
    private static final int SLOT_STRIDE  = 18;
    private static final int PANEL_GAP  = 2;
    private static final int PANEL_OFFSET_X = -4;
    private static final int PANEL_OFFSET_Y = -6;

    private static final int ANCHOR_SLOT_INDEX = 17;

    private static int panelX(InventoryScreen s) {
        var slot = s.getMenu().slots.get(ANCHOR_SLOT_INDEX);
        return s.leftPos + slot.x + 18 + PANEL_GAP+ PANEL_OFFSET_X;
    }
    private static int panelY(InventoryScreen s) {
        var slot = s.getMenu().slots.get(ANCHOR_SLOT_INDEX);
        return s.topPos + slot.y + PANEL_OFFSET_Y;
    }

    private static int itemX(InventoryScreen s)      { return panelX(s) + SLOT_INSET_X + 1; }
    private static int itemY(InventoryScreen s, int i){ return panelY(s) + SLOT_INSET_Y + 1 + i * SLOT_STRIDE; }

    private static boolean isInPanel(InventoryScreen s, double mx, double my) {
        int px = panelX(s), py = panelY(s);
        return mx >= px && mx < px + TEXTURE_W && my >= py && my < py + TEXTURE_H;
    }

    private static int hitSlot(InventoryScreen s, double mx, double my) {
        int ix = itemX(s);
        for (int i = 0; i < cachedSlots.size(); i++) {
            int iy = itemY(s, i);
            if (mx >= ix - 1 && mx < ix + 17 && my >= iy - 1 && my < iy + 17) return i;
        }
        return -1;
    }
    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (!(event.getScreen() instanceof InventoryScreen)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (ModEnchantments.getEnderPocketsLevel(mc.player) <= 0) return;
        cachedSlots = Collections.emptyList();
        PacketDistributor.sendToServer(new EnderActionPacket(-1, 0, false));
    }

    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof InventoryScreen) cachedSlots = Collections.emptyList();
    }

    @SubscribeEvent
    public static void onRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int currentLevel = ModEnchantments.getEnderPocketsLevel(mc.player);
        if (currentLevel != cachedSlots.size()) {
            if (currentLevel == 0) {
                cachedSlots = Collections.emptyList();
            } else {
                PacketDistributor.sendToServer(new EnderActionPacket(-1, 0, false));
            }
            return;
        }

        if (cachedSlots.isEmpty()) return;

        GuiGraphics g = event.getGuiGraphics();
        int n  = cachedSlots.size();
        int px = panelX(screen);
        int py = panelY(screen);
        int ix = itemX(screen);

        double mx = event.getMouseX();
        double my = event.getMouseY();

        g.pose().pushPose();
        //g.pose().translate(0, 0, 150);
        g.blit(PANEL_TEXTURES[n - 1], px, py, 0, 0, TEXTURE_W, TEXTURE_H, TEXTURE_W, TEXTURE_H);

        for (int i = 0; i < n; i++) {
            int iy = itemY(screen, i);
            ItemStack stack = cachedSlots.get(i);

            if (!stack.isEmpty()) {
                g.renderItem(stack, ix, iy);
                if (stack.getCount() != 1) {
                    String text = String.valueOf(stack.getCount());
                    g.pose().pushPose();
                    g.pose().translate(0, 0, 200);
                    g.drawString(
                            mc.font,
                            text,
                            ix + 17 - mc.font.width(text),
                            iy + 9,
                            0xFFFFFF,
                            true
                    );
                    g.pose().popPose();
                }
                if (stack.isDamaged()) {
                    int barWidth = Math.round(13.0F - (float)stack.getDamageValue() * 13.0F / (float)stack.getMaxDamage());
                    int color = stack.getBarColor();

                    int x = ix + 2;
                    int y = iy + 13;

                    g.fill(x, y, x + 13, y + 2, 0xFF000000);
                    g.fill(x, y, x + barWidth, y + 1, color | 0xFF000000);
                }
            }

            if (mx >= ix - 1 && mx < ix + 17 && my >= iy - 1 && my < iy + 17) {
                g.pose().pushPose();
                g.pose().translate(0, 0, 300);
                g.fill(ix, iy, ix + 16, iy + 16, 0x80FFFFFF);
                g.pose().popPose();
            }
        }

        int hovered = hitSlot(screen, mx, my);
        if (hovered >= 0 && !cachedSlots.get(hovered).isEmpty()) {
            g.renderTooltip(mc.font, cachedSlots.get(hovered), (int) mx, (int) my);
        }
        g.pose().popPose();
    }

    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.MouseButtonPressed.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        if (cachedSlots.isEmpty()) return;
        if (!isInPanel(screen, event.getMouseX(), event.getMouseY())) return;

        event.setCanceled(true);

        int slot = hitSlot(screen, event.getMouseX(), event.getMouseY());
        if (slot < 0) return;

        boolean shift = net.minecraft.client.gui.screens.Screen.hasShiftDown();
        PacketDistributor.sendToServer(new EnderActionPacket(slot, event.getButton(), shift));
    }

    @SubscribeEvent
    public static void onMouseRelease(ScreenEvent.MouseButtonReleased.Pre event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        if (cachedSlots.isEmpty()) return;
        if (!isInPanel(screen, event.getMouseX(), event.getMouseY())) return;

        event.setCanceled(true);
    }
}