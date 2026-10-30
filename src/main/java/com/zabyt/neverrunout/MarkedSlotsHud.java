package com.zabyt.neverrunout;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Inventory;


final class MarkedSlotsHud {

    private static final int COLOR_STOCKED = 0x7054E38A;
    private static final int COLOR_EMPTY = 0xF054E38A;

    private MarkedSlotsHud() {
    }

    static void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft minecraft = Minecraft.getInstance();
        NeverRunOutConfig cfg = NeverRunOutConfig.get();
        if (!cfg.enabled || cfg.markedSlots.isEmpty()) {
            return;
        }
        if (minecraft.player == null || minecraft.level == null || minecraft.gui.hud.isHidden()) {
            return;
        }
        Inventory inv = minecraft.player.getInventory();
        int barX = graphics.guiWidth() / 2 - 91;
        int barY = graphics.guiHeight() - 22;

        for (int h : cfg.markedSlots) {
            if (h < 0 || h >= Inventory.SELECTION_SIZE) {
                continue;
            }
            boolean empty = inv.getItem(h).isEmpty();


            frame(graphics, barX + h * 20 + 2, barY + 2, 18, 18,
                    empty ? COLOR_EMPTY : COLOR_STOCKED);
        }
    }

    private static void frame(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
        g.fill(x, y, x + w, y + 1, color);
        g.fill(x, y + h - 1, x + w, y + h, color);
        g.fill(x, y + 1, x + 1, y + h - 1, color);
        g.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }
}
