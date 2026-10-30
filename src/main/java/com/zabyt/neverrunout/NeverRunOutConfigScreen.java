package com.zabyt.neverrunout;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NeverRunOutConfigScreen extends Screen {

    private static final int HEADER_COLOR = 0xFFFFFFFF;
    private static final int TEXT_COLOR = 0xFFA0A0A0;

    private final Screen parent;
    private final NeverRunOutConfig cfg = NeverRunOutConfig.get();

    public NeverRunOutConfigScreen(Screen parent) {
        super(Component.literal("NeverRunOut Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(enableLabel(), b -> {
            cfg.enabled = !cfg.enabled;
            cfg.save();
            b.setMessage(enableLabel());
        }).bounds(width / 2 - 100, height / 2 - 40, 200, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Clear all marks (" + cfg.markedSlots.size() + ")"),
                b -> {
                    cfg.clearMarks();
                    b.setMessage(Component.literal("Clear all marks (0)"));
                }).bounds(width / 2 - 100, height / 2 - 10, 200, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(width / 2 - 100, height / 2 + 40, 200, 20).build());
    }

    private Component enableLabel() {
        return Component.literal("Enabled: " + (cfg.enabled ? "ON" : "OFF"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, this.height / 2 - 70, HEADER_COLOR);
        graphics.centeredText(this.font,
                Component.literal("Caps Lock marks the held hotbar slot, or a hovered slot in your inventory."),
                this.width / 2, this.height / 2 + 12, TEXT_COLOR);
        graphics.centeredText(this.font,
                Component.literal("Marked slots are refilled from your inventory when they run out."),
                this.width / 2, this.height / 2 + 24, TEXT_COLOR);
    }

    @Override
    public void onClose() {
        cfg.save();
        if (minecraft != null) {
            minecraft.gui.setScreen(parent);
        }
    }
}
