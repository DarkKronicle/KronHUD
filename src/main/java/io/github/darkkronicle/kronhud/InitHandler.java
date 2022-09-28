package io.github.darkkronicle.kronhud;

import io.github.darkkronicle.darkkore.config.ConfigurationManager;
import io.github.darkkronicle.darkkore.gui.OptionComponentHolder;
import io.github.darkkronicle.darkkore.intialization.Initializer;
import io.github.darkkronicle.kronhud.config.ConfigHandler;
import io.github.darkkronicle.kronhud.gui.HudEntryOption;
import io.github.darkkronicle.kronhud.gui.screen.HudEntryComponent;

import java.util.Optional;

public class InitHandler implements Initializer {

    @Override
    public void init() {
        OptionComponentHolder.getInstance().addConverter((parent, option, width) -> {
            if (!(option instanceof HudEntryOption)) {
                return Optional.empty();
            }
            return Optional.of(new HudEntryComponent(parent, (HudEntryOption) option, width));
        });


    }

}
