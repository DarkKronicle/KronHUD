package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.gui.ConfigScreen;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import io.github.darkkronicle.kronhud.gui.component.HudEntry;

import java.util.List;

/**
 * Config screen for an {@link AbstractHudEntry}
 */
public class EntryConfigScreen extends ConfigScreen {

    public EntryConfigScreen(HudEntry hud) {
        super(List.of(
                hud.toTab()
        ));
    }

}
