package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.gui.ConfigScreen;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;

public class HudConfigScreen extends ConfigScreen {

    public HudConfigScreen(AbstractHudEntry hud, Screen parent) {
        //super(10, 20, "kronhud", parent, hud.getNameKey());
        super(List.of(hud.getOptionWrapper()));
    }

}
