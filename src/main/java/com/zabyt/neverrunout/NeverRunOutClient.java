package com.zabyt.neverrunout;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeverRunOutClient implements ClientModInitializer {

    public static final String MOD_ID = "neverrunout";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final int CAPS_LOCK = GLFW.GLFW_KEY_CAPS_LOCK;

    private static KeyMapping markKey;


    private static final Map<Integer, Item> LAST_ITEM = new HashMap<>();

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));
        markKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.neverrunout.mark", InputConstants.Type.KEYSYM, CAPS_LOCK, category));

        ClientTickEvents.END_CLIENT_TICK.register(NeverRunOutClient::tick);

        HudElementRegistry.attachElementAfter(net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(MOD_ID, "marked_slots"), MarkedSlotsHud::render);


        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (!(screen instanceof AbstractContainerScreen)) {
                return;
            }
            ScreenKeyboardEvents.beforeKeyPress(screen).register((scr, event) -> {
                if (event.key() != CAPS_LOCK) {
                    return;
                }
                Minecraft mc = Minecraft.getInstance();
                if (!(mc.gui.screen() instanceof AbstractContainerScreen<?> container)
                        || container.hoveredSlot == null || mc.player == null) {
                    return;
                }
                if (container.hoveredSlot.container != mc.player.getInventory()) {
                    return;
                }
                int idx = container.hoveredSlot.getContainerSlot();
                if (idx < 0 || idx >= Inventory.SELECTION_SIZE) {
                    feedback(mc, "Hover a hotbar slot to mark it", false);
                    return;
                }
                toggle(mc, idx);
            });
        });

        NeverRunOutConfig.get();
        LOGGER.info("NeverRunOut initialized");
    }

    private static void tick(Minecraft client) {
        while (markKey.consumeClick()) {
            if (client.gui.screen() == null && client.player != null) {
                toggle(client, client.player.getInventory().getSelectedSlot());
            }
        }
        refillTick(client);
    }

    private static void toggle(Minecraft client, int hotbarSlot) {
        NeverRunOutConfig cfg = NeverRunOutConfig.get();
        if (!cfg.isMarked(hotbarSlot)) {
            cfg.toggleMark(hotbarSlot);
            ItemStack cur = client.player.getInventory().getItem(hotbarSlot);
            if (!cur.isEmpty()) {
                LAST_ITEM.put(hotbarSlot, cur.getItem());
            }
            feedback(client, "NeverRunOut: slot " + (hotbarSlot + 1)
                    + " marked" + describe(cur), true);
        } else {
            cfg.toggleMark(hotbarSlot);
            LAST_ITEM.remove(hotbarSlot);
            feedback(client, "NeverRunOut: slot " + (hotbarSlot + 1) + " unmarked", false);
        }
    }

    private static String describe(ItemStack stack) {
        return stack.isEmpty() ? "" : (" (" + stack.getHoverName().getString() + ")");
    }

    private static void feedback(Minecraft client, String message, boolean marked) {
        if (client.player == null) {
            return;
        }
        client.player.sendOverlayMessage(Component.literal(message));
        if (marked) {
            client.player.playSound(net.minecraft.sounds.SoundEvents.EXPERIENCE_ORB_PICKUP, 0.35F, 1.5F);
        } else {
            client.player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), 0.25F, 0.8F);
        }
    }

    private static void refillTick(Minecraft client) {
        NeverRunOutConfig cfg = NeverRunOutConfig.get();
        if (!cfg.enabled || cfg.markedSlots.isEmpty()) {
            return;
        }
        LocalPlayer player = client.player;
        if (player == null || client.gameMode == null || player.isSpectator()) {
            return;
        }


        if (player.containerMenu != player.inventoryMenu) {
            return;
        }
        Inventory inv = player.getInventory();

        for (int h : List.copyOf(cfg.markedSlots)) {
            if (h < 0 || h >= Inventory.SELECTION_SIZE) {
                continue;
            }
            ItemStack current = inv.getItem(h);
            if (!current.isEmpty()) {
                LAST_ITEM.put(h, current.getItem());
                continue;
            }
            Item want = LAST_ITEM.get(h);
            if (want == null) {
                continue;
            }
            int source = findSource(inv, want);
            if (source < 0) {
                continue;
            }
            client.gameMode.handleContainerInput(
                    player.inventoryMenu.containerId,
                    sourceMenuSlot(source),
                    h,
                    ContainerInput.SWAP,
                    player);
        }
    }


    private static int findSource(Inventory inv, Item want) {
        int best = -1;
        int bestCount = 0;
        for (int s = Inventory.SELECTION_SIZE; s < Inventory.INVENTORY_SIZE; s++) {
            ItemStack stack = inv.getItem(s);
            if (!stack.isEmpty() && stack.getItem() == want && stack.getCount() > bestCount) {
                best = s;
                bestCount = stack.getCount();
            }
        }
        return best;
    }


    private static int sourceMenuSlot(int inventoryIndex) {
        if (inventoryIndex >= Inventory.SELECTION_SIZE) {
            return InventoryMenu.INV_SLOT_START + inventoryIndex - Inventory.SELECTION_SIZE;
        }
        return InventoryMenu.USE_ROW_SLOT_START + inventoryIndex;
    }
}
