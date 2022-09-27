package io.github.darkkronicle.kronhud.gui.screen;

import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import io.github.darkkronicle.darkkore.gui.ConfigScreen;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class HudConfigScreen extends ConfigScreen {

    private AbstractHudEntry hud;
    private final Screen parent;

    public HudConfigScreen(AbstractHudEntry hud, Screen parent) {
        //super(10, 20, "kronhud", parent, hud.getNameKey());
        super(hud.getOptionWrapper());
        this.hud = hud;
        this.parent = parent;
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

    @Override
    public void initImpl() {

    }
}
