package io.github.darkkronicle.kronhud.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.fabricmc.fabric.api.util.BooleanFunction;
import net.minecraft.client.util.math.MatrixStack;

// Based around https://github.com/maruohon/minihud/blob/fabric_1.16_snapshots_temp/src/main/java/fi/dy/masa/minihud/gui/widgets/WidgetShapeEntry.java
// Licensed under GNU LGPL
public class HudEntryWidget extends WidgetListEntryBase<AbstractHudEntry> {

    private AbstractHudEntry hud;
    private boolean isOdd;
    private HudListWidget parent;
    private int buttonsStartX;

    public HudEntryWidget(int x, int y, int width, int height, boolean isOdd, AbstractHudEntry hud, int listIndex,
            HudListWidget parent) {
        super(x, y, width, height, hud, listIndex);
        this.hud = hud;
        this.isOdd = isOdd;
        this.parent = parent;
        y += 1;
        int posX = x + width - 2;

        posX -= addButton(posX, y, ButtonListener.Type.CONFIGURE);
        posX -= addOnOffButton(posX, y, hud.isEnabled(), ButtonListener.Type.ENABLED);

        buttonsStartX = posX;
    }

    @Override
    public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrices) {
        RenderUtils.color(1f, 1f, 1f, 1f);

        // Draw a lighter background for the hovered and the selected entry
        if (this.isMouseOver(mouseX, mouseY)) {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x70FFFFFF);
        } else if (this.isOdd) {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x20FFFFFF);
        }
        // Draw a slightly lighter background for even entries
        else {
            RenderUtils.drawRect(this.x, this.y, this.width, this.height, 0x50FFFFFF);
        }

        String name = this.entry.getName();
        this.drawString(this.x + 4, this.y + 7, 0xFFFFFFFF, name, matrices);

        RenderUtils.color(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        super.render(mouseX, mouseY, selected, matrices);
    }

    private int addButton(int x, int y, ButtonListener.Type type) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, true, type.getTranslationKey().apply(false));
        addButton(button, new ButtonListener(type, this));
        return button.getWidth() + 1;
    }

    private int addOnOffButton(int x, int y, boolean on, ButtonListener.Type type) {
        ButtonGeneric button = new ButtonGeneric(x, y, 50, true, type.getTranslationKey().apply(on));
        this.addButton(button, new ButtonListener(type, this));
        return button.getWidth() + 2;
    }

    private static class ButtonListener implements IButtonActionListener {

        private Type type;
        private HudEntryWidget parent;

        public ButtonListener(Type type, HudEntryWidget parent) {
            this.type = type;
            this.parent = parent;
        }

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            switch (type) {
                case CONFIGURE:
                    GuiBase.openGui(new HudConfigScreen(parent.hud, parent.parent.parent));
                    break;
                case ENABLED:
                    parent.hud.toggle();
                    parent.parent.refreshEntries();
            }
        }

        @AllArgsConstructor
        public enum Type {
            CONFIGURE((value) -> "button.kronhud.configure"),
            ENABLED((value) -> value ? "button.kronhud.enabled" : "button.kronhud.disabled");

            @Getter
            private BooleanFunction<String> translationKey;

        }

    }

}
