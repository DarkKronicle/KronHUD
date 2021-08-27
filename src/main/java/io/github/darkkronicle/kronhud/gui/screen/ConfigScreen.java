package io.github.darkkronicle.kronhud.gui.screen;

import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.gui.GuiListBase;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.util.KeyCodes;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import net.minecraft.client.gui.screen.Screen;

// Based around https://github.com/maruohon/minihud/blob/fabric_1.16_snapshots_temp/src/main/java/fi/dy/masa/minihud/gui/GuiShapeManager.java
// Licensed under GNU LGPL
public class ConfigScreen extends GuiListBase<AbstractHudEntry, HudEntryWidget, HudListWidget>
        implements ISelectionListener<AbstractHudEntry> {

    public ConfigScreen(Screen parent) {
        super(10, 20);
        setParent(parent);
        useTitleHierarchy = false;
        title = StringUtils.translate("button.kronhud.configuration");
    }

    @Override
    protected HudListWidget createListWidget(int listX, int listY) {
        return new HudListWidget(listX, listY, getBrowserWidth(), getBrowserHeight(), this, this);
    }

    @Override
    public boolean onKeyTyped(int keyCode, int scanCode, int modifiers) {
		if (this.getListWidget().onKeyTyped(keyCode, scanCode, modifiers)) {
			return true;
		}

		if (keyCode == KeyCodes.KEY_ESCAPE) {
			GuiBase.openGui(getParent());
			return true;
		}

		return false;
    }

	@Override
	protected int getBrowserWidth() {
		return this.width - 20;
	}

	@Override
	protected int getBrowserHeight() {
		return this.height - 37;
	}

    @Override
    public void onSelectionChange(AbstractHudEntry hud) {
    }

}
