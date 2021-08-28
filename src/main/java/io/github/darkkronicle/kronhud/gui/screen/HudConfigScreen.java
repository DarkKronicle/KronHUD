package io.github.darkkronicle.kronhud.gui.screen;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigValue;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.interfaces.IKeybindConfigGui;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import fi.dy.masa.malilib.render.RenderUtils;
import io.github.darkkronicle.kronhud.config.KronColor;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

public class HudConfigScreen extends GuiConfigsBase {

    private AbstractHudEntry hud;

    public HudConfigScreen(AbstractHudEntry hud, Screen parent) {
        super(10, 20, "kronhud", parent, hud.getNameKey());
        this.hud = hud;
    }

    @Override
    protected WidgetListConfigOptions createListWidget(int listX, int listY) {
        return new ListWidget(listX, listY,
                this.getBrowserWidth(), this.getBrowserHeight(), this.getConfigWidth(), this.getZOffset(), this.useKeybindSearch(), this);
    }

    @Override
    protected int getBrowserHeight() {
        return this.height - 37;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        return ConfigOptionWrapper.createFor(hud.getOptions());
    }

    // Hacks to prevent crash. MaLiLib seems to not be as flexible as I thought.
    public static class ListWidget extends WidgetListConfigOptions {

        public ListWidget(int x, int y, int width, int height, int configWidth, float zLevel, boolean useKeybindSearch, GuiConfigsBase parent) {
            super(x, y, width, height, configWidth, zLevel, useKeybindSearch, parent);
        }

        @Override
        protected WidgetConfigOption createListEntryWidget(int x, int y, int listIndex, boolean isOdd, ConfigOptionWrapper wrapper) {
            return new OptionWidget(x, y, this.browserEntryWidth, this.browserEntryHeight,
                    this.maxLabelWidth, this.configWidth, wrapper, listIndex, this.parent, this);
        }

        public static class OptionWidget extends WidgetConfigOption {

            public OptionWidget(int x, int y, int width, int height, int labelWidth, int configWidth, ConfigOptionWrapper wrapper, int listIndex, IKeybindConfigGui host, WidgetListConfigOptionsBase<?, ?> parent) {
                super(x, y, width, height, labelWidth, configWidth, wrapper, listIndex, host, parent);
            }

            @Override
            protected void addConfigOption(int x, int y, float zLevel, int labelWidth, int configWidth, IConfigBase config) {
                if(!(config instanceof KronColor)) {
                    super.addConfigOption(x, y, zLevel, labelWidth, configWidth, config);
                    return;
                }

                addLabel(x, y + 7, labelWidth, 8, -1, config.getConfigGuiDisplayName());
                int configHeight = 20;
                addConfigComment(x, y + 5, labelWidth, 12, config.getComment());

                x += labelWidth + 10;

                int resetX = x + configWidth + 4;
                configWidth -= 24; // adjust the width to match other configs due to the color display
                this.colorDisplayPosX = x + configWidth + 4;
                addConfigTextFieldEntry(x, y, resetX, configWidth, configHeight, (IConfigValue) config);
            }

            @Override
            public void render(int mouseX, int mouseY, boolean selected, MatrixStack matrixStack) {
                super.render(mouseX, mouseY, selected, matrixStack);

                IConfigBase config = wrapper.getConfig();
                if(!(config instanceof KronColor)) {
                    return;
                }

                int y = this.y + 1;
                RenderUtils.drawRect(this.colorDisplayPosX, y, 19, 19, 0xFFFFFFFF);
                RenderUtils.drawRect(this.colorDisplayPosX + 1, y + 1, 17, 17, 0xFF000000);
                RenderUtils.drawRect(this.colorDisplayPosX + 2, y + 2, 15, 15,
                        ((KronColor) config).getIntegerValue());
            }
        }

    }

}
