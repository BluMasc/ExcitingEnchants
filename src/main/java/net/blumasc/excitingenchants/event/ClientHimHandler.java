package net.blumasc.excitingenchants.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.blumasc.blubasics.item.client.InWorld3dBakedModel;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.block.entity.renderer.TrapClientExtension;
import net.blumasc.excitingenchants.client.*;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.entity.client.castle.CastleRenderLayer;
import net.blumasc.excitingenchants.entity.client.souls.SoulRetrieverRenderLayer;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.network.HorrorRequestPacket;
import net.blumasc.excitingenchants.network.HorrorSavePacket;
import net.blumasc.excitingenchants.network.InventoryOpenPayload;
import net.blumasc.excitingenchants.network.JumpInputPacket;
import net.blumasc.excitingenchants.shader.OilSlickRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@EventBusSubscriber(modid = ExcitingEnchantsMod.MODID, value = Dist.CLIENT)
public class ClientHimHandler {
    private static final int SYNC_THRESHOLD = 10;
    private static int lastSyncedValue = 0;
    private static int lastPhase = -1;
    private static int lastArmorCount = 0;

    private static long lastMessageTime = 0;
    private static final long MESSAGE_COOLDOWN = 500; //ms

    private static final Random RANDOM = new Random();
    private static final String MESSAGE_PREFIX = "chat."+ExcitingEnchantsMod.MODID+".him.";
    private static final String[] FIRST_ARMOR_KEYS = {
            MESSAGE_PREFIX + "first_equipped_1",
            MESSAGE_PREFIX + "first_equipped_2",
            MESSAGE_PREFIX + "first_equipped_3",
            MESSAGE_PREFIX + "first_equipped_4"
    };
    private static final String[][] CRAFTING_KEYS = {{
            MESSAGE_PREFIX + "crafting_1",
            MESSAGE_PREFIX + "crafting_2",
            MESSAGE_PREFIX + "crafting_3"
    },{
            MESSAGE_PREFIX + "crafting_1.unequiped"
    }
    };
    private static final String[][] SLEEPING_KEYS = {{
            MESSAGE_PREFIX + "sleeping_1",
            MESSAGE_PREFIX + "sleeping_2",
            MESSAGE_PREFIX + "sleeping_3"
    },{
            MESSAGE_PREFIX + "sleeping_1.unequiped"
    }
    };
    private static final String[][] KILL_ANIMAL_KEYS = {{
            MESSAGE_PREFIX + "kill_animal_1",
            MESSAGE_PREFIX + "kill_animal_2",
            MESSAGE_PREFIX + "kill_animal_3"
    },
            {
                    MESSAGE_PREFIX + "kill_animal_1.unequiped"
            }
    };
    private static final String[][] KILL_MONSTER_KEYS = {{
            MESSAGE_PREFIX + "kill_monster_1",
            MESSAGE_PREFIX + "kill_monster_2",
            MESSAGE_PREFIX + "kill_monster_3"
    },{
            MESSAGE_PREFIX + "kill_monster_1.unequiped"
    }
    };
    private static final String[][] MINING_KEYS = {{
            MESSAGE_PREFIX + "mining_1",
            MESSAGE_PREFIX + "mining_2"
    },{
            MESSAGE_PREFIX + "mining_1.unequiped"
    }
    };
    private static final String[] LAST_ARMOR_KEYS = {
            MESSAGE_PREFIX + "last_removed_1",
            MESSAGE_PREFIX + "last_removed_2",
            MESSAGE_PREFIX + "last_removed_3"
    };
    private static final String[] DEATH_KEYS = {
            MESSAGE_PREFIX + "death_1",
            MESSAGE_PREFIX + "death_2",
            MESSAGE_PREFIX + "death_3"
    };

    private static final String[] JOIN_WORLD_KEYS = {
            MESSAGE_PREFIX + "join_world_1",
            MESSAGE_PREFIX + "join_world_2",
            MESSAGE_PREFIX + "join_world_3"
    };
    private static boolean wasAsleep = false;

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientHimData.horror_status = 0;
        lastSyncedValue = 0;
        lastPhase = -1;
        lastArmorCount = 0;
        lastMessageTime = 0;
        wasAsleep = false;
        PacketDistributor.sendToServer(new HorrorRequestPacket());
    }

    @SubscribeEvent
    public static void onClientLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        if (Math.abs(ClientHimData.horror_status - lastSyncedValue) > 0) {
            PacketDistributor.sendToServer(new HorrorSavePacket(ClientHimData.horror_status));
        }
        ClientHimData.horror_status = 0;
        lastSyncedValue = 0;
        lastPhase = -1;
        lastArmorCount = 0;
        lastMessageTime = 0;
        wasAsleep = false;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        long dayTime = mc.level.getDayTime() % 24000;
        int currentPhase = (dayTime < 6000 || dayTime >= 18000) ? 0 : 1;

        if (currentPhase != lastPhase && lastPhase != -1) {
            handleTimeInterval(mc.player);
        }

        checkArmorChanges(mc.player);
        checkSleepState(mc.player);

        lastPhase = currentPhase;
    }
    public static void onCraft(LocalPlayer player) {
        if (ClientHimData.horror_status > 1) {
            sendHerobirineChatMessage("Crafting", ClientHimData.horror_status, false, countHorrorArmorPieces(player)>0);
        }
    }
    public static void onKillAnimal(LocalPlayer player) {
        if (ClientHimData.horror_status > 1) {
            sendHerobirineChatMessage("Kill animal", ClientHimData.horror_status, false, countHorrorArmorPieces(player)>0);
        }
    }
    public static void onKillMonster(LocalPlayer player) {
        if (ClientHimData.horror_status > 1) {
            sendHerobirineChatMessage("Kill monster", ClientHimData.horror_status, false, countHorrorArmorPieces(player)>0);
        }
    }
    public static void onMineBlock(LocalPlayer player) {
        if (ClientHimData.horror_status > 1) {
            sendHerobirineChatMessage("Mining", ClientHimData.horror_status / 5, false, countHorrorArmorPieces(player)>0);
        }
    }
    public static void onPlayerDeath(LocalPlayer player) {
        if (countHorrorArmorPieces(player)>0) {
            sendHerobirineChatMessage("Player death", 100, true, true);
        }
    }
    public static void onJoinWorld(LocalPlayer player) {
        if (countHorrorArmorPieces(player)>0) {
            sendHerobirineChatMessage("Join world", 100, true, true);
        }
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof net.minecraft.client.gui.screens.inventory.CraftingScreen) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                onCraft(player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Minecraft.getInstance().tell(() -> {
                onJoinWorld(player);
            });
        }
    }

    private static void handleTimeInterval(LocalPlayer player) {
        int armorPieces = countHorrorArmorPieces(player);

        if (armorPieces > 0) {
            ClientHimData.horror_status += armorPieces;
            ClientHimData.horror_status = Math.min(100, ClientHimData.horror_status);
        } else {
            int reduction = (int) Math.ceil(ClientHimData.horror_status / 4.0);
            ClientHimData.horror_status = Math.max(0, ClientHimData.horror_status - reduction);
        }

        if (Math.abs(ClientHimData.horror_status - lastSyncedValue) >= SYNC_THRESHOLD) {
            PacketDistributor.sendToServer(new HorrorSavePacket(ClientHimData.horror_status));
            lastSyncedValue = ClientHimData.horror_status;
        }
    }

    private static void checkArmorChanges(LocalPlayer player) {
        int currentArmorCount = countHorrorArmorPieces(player);

        if (lastArmorCount == 0 && currentArmorCount > 0 && ClientHimData.horror_status == 0) {
            sendHerobirineChatMessage("First armor equipped", 100, true, true);
        }
        else if (lastArmorCount > 0 && currentArmorCount == 0) {
            sendHerobirineChatMessage("Last armor removed", 100, true, false);
        }

        lastArmorCount = currentArmorCount;
    }

    private static void checkSleepState(LocalPlayer player) {
        boolean isNowAsleep = player.isSleeping();
        if (isNowAsleep && !wasAsleep) {
            int armorCount = countHorrorArmorPieces(player);
            if (armorCount > 0) {
                sendHerobirineChatMessage("Sleeping", ClientHimData.horror_status, false, true);
            }
        }

        wasAsleep = isNowAsleep;
    }
    private static void sendHerobirineChatMessage(String actionType, int baseChance, boolean forceMessage, boolean hasArmor) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMessageTime < MESSAGE_COOLDOWN) {
            return;
        }
        if (!forceMessage && RANDOM.nextInt(100) >= baseChance) {
            return;
        }

        String[] messageKeys = null;

        int data = hasArmor? 0:1;

        switch (actionType) {
            case "First armor equipped":
                messageKeys = FIRST_ARMOR_KEYS;
                break;
            case "Last armor removed":
                messageKeys = LAST_ARMOR_KEYS;
                break;
            case "Crafting":
                messageKeys = CRAFTING_KEYS[data];
                break;
            case "Sleeping":
                messageKeys = SLEEPING_KEYS[data];
                break;
            case "Kill animal":
                messageKeys = KILL_ANIMAL_KEYS[data];
                break;
            case "Kill monster":
                messageKeys = KILL_MONSTER_KEYS[data];
                break;
            case "Mining":
                messageKeys = MINING_KEYS[data];
                break;
            case "Player death":
                messageKeys = DEATH_KEYS;
                break;
            case "Join world":
                messageKeys = JOIN_WORLD_KEYS;
                break;
            default:
                return;
        }

        if (messageKeys != null && messageKeys.length > 0) {
            String selectedKey = messageKeys[RANDOM.nextInt(messageKeys.length)];
            Component message = Component.translatable(selectedKey);

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.gui.getChat().addMessage(message, null, null);
                lastMessageTime = currentTime;
            }
        }
    }

    private static int countHorrorArmorPieces(LocalPlayer player) {
        int count = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty() && hasHorrorEnchantment(stack)) {
                count++;
            }
        }
        return count;
    }

    private static boolean hasHorrorEnchantment(ItemStack stack) {
        return stack.getEnchantments().keySet().stream()
                .anyMatch(holder -> holder.is(ModEnchantments.HIM));
    }

    public static void setLastSyncedValue(int value) {
        lastSyncedValue = value;
    }

    private static boolean isWearingHorrorArmor(LocalPlayer player) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty() && hasHorrorEnchantment(stack)) return true;
        }
        return false;
    }
}