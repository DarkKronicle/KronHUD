package io.github.darkkronicle.kronhud.gui.screen;

import fi.dy.masa.malilib.gui.LeftRight;
import fi.dy.masa.malilib.gui.MaLiLibIcons;
import fi.dy.masa.malilib.gui.interfaces.ISelectionListener;
import fi.dy.masa.malilib.gui.widgets.WidgetListBase;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBarConfigs;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;

import java.util.Collection;

// Based around https://github.com/maruohon/minihud/blob/fabric_1.16_snapshots_temp/src/main/java/fi/dy/masa/minihud/gui/widgets/WidgetListShapes.java
// Licensed under GNU LGPL
public class HudListWidget extends WidgetListBase<AbstractHudEntry, HudEntryWidget> {

    protected ConfigScreen parent;

    public HudListWidget(int x, int y, int width, int height, ISelectionListener<AbstractHudEntry> selectionListener,
            ConfigScreen parent) {
        super(x, y, width, height, selectionListener);
        browserEntryHeight = 22;
        this.parent = parent;
        widgetSearchBar = new WidgetSearchBar(x + 2, y + 4, width - 14, 14, 0, MaLiLibIcons.SEARCH, LeftRight.LEFT);
        browserEntriesOffsetY = 17;
    }

    @Override
    protected Collection<AbstractHudEntry> getAllEntries() {
        return KronHUD.hudManager.getEntries();
    }

	@Override
	protected boolean entryMatchesFilter(AbstractHudEntry entry, String filterText) {
		return entry.getName().toLowerCase().contains(filterText.toLowerCase());
	}

    @Override
    protected HudEntryWidget createListEntryWidget(int x, int y, int listIndex, boolean isOdd, AbstractHudEntry entry) {
        return new HudEntryWidget(x, y, browserEntryWidth, getBrowserEntryHeightFor(entry), isOdd, entry, listIndex,
                this);
    }
}
