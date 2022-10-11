package io.github.darkkronicle.kronhud;

import io.github.darkkronicle.darkkore.gui.OptionComponentHolder;
import io.github.darkkronicle.darkkore.hotkeys.BasicHotkey;
import io.github.darkkronicle.darkkore.hotkeys.HotkeyHandler;
import io.github.darkkronicle.darkkore.intialization.Initializer;
import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.gui.HudEntryOption;
import io.github.darkkronicle.kronhud.gui.HudManager;
import io.github.darkkronicle.kronhud.gui.screen.HudEditScreen;
import io.github.darkkronicle.kronhud.gui.screen.HudEntryComponent;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Optional;

public class InitHandler implements Initializer {

    @Override
    public void init() {
        HudManager.getInstance().refreshAllBounds();

        OptionComponentHolder.getInstance().addConverter((parent, option, width) -> {
            if (!(option instanceof HudEntryOption)) {
                return Optional.empty();
            }
            return Optional.of(new HudEntryComponent(parent, (HudEntryOption) option, width));
        });
        HotkeyHandler.getInstance().add(
                KronHUD.MOD_ID,
                "edithud",
                () -> List.of(new BasicHotkey(
                        ConfigHandler.getInstance().editHud.getValue(),
                        () -> MinecraftClient.getInstance().setScreen(new HudEditScreen(null)))
                )
        );
    }

}
