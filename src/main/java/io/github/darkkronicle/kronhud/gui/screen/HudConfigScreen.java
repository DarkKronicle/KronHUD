package io.github.darkkronicle.kronhud.gui.screen;

import io.github.darkkronicle.darkkore.gui.ConfigScreen;
import io.github.darkkronicle.darkkore.gui.Tab;
import io.github.darkkronicle.kronhud.KronHUD;
import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.gui.HudEntryOption;
import io.github.darkkronicle.kronhud.gui.HudManager;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Config screen that shows general config options as well as all the {@link io.github.darkkronicle.kronhud.gui.AbstractHudEntry}'s
 */
public class HudConfigScreen extends ConfigScreen {
    public HudConfigScreen() {
        super(List.of(
                Tab.ofOptions(new Identifier("kronhud", "entries"), "option.section.entries", HudManager.getInstance().getEntries().stream().map(HudEntryOption::new).collect(Collectors.toList()))
                ,
                Tab.ofOptions(
                    new Identifier(KronHUD.MOD_ID, ConfigHandler.getInstance().general.getKey()),
                    ConfigHandler.getInstance().general.getNameKey(),
                    ConfigHandler.getInstance().general.getOptions())
                ));
    }
}
