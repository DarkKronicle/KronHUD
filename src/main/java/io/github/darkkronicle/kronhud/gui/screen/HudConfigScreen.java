package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.config.options.OptionSection;
import io.github.darkkronicle.darkkore.gui.ConfigScreen;
import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.gui.AbstractHudEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.List;

public class HudConfigScreen extends ConfigScreen {

    public HudConfigScreen(AbstractHudEntry hud, Screen parent) {
        super(List.of(
                hud.getOptionWrapper(),
                Tab.ofOptions(
                        new Identifier(KronHUD.MOD_ID, ConfigHandler.getInstance().general.getKey()),
                        ConfigHandler.getInstance().general.getNameKey(),
                        ConfigHandler.getInstance().general.getOptions())
                )
        );
    }

}
